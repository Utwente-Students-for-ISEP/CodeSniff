package org.mining.util.gitmetrics.metrics;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.mining.util.gitmetrics.GitMetricAnalyzer;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BranchTime implements GitMetricAnalyzer {

    private final List<Long> branchLifetimes = new ArrayList<>();

    @Override
    public void analyze(Repository repository) {
        try (Git git = new Git(repository)) {
            List<Ref> branches = git.branchList().call();
            for (Ref branch : branches) {
                if (branch.getName().endsWith("main") || branch.getName().endsWith("master")) {
                    continue;
                }
                long branchLifetime = calculateBranchLifetime(repository, branch);
                if (branchLifetime > 0) {
                    branchLifetimes.add(branchLifetime);
                }
            }
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long calculateBranchLifetime(Repository repository, Ref branch) throws IOException, GitAPIException {
        RevCommit branchCreationCommit = getFirstCommit(repository, branch);
        if (branchCreationCommit == null) {
            return -1;
        }

        RevCommit mergeCommit = getMergeCommit(repository, branch.getName());
        if (mergeCommit == null) {
            return -1;
        }

        long branchCreationTime = branchCreationCommit.getCommitTime();
        long branchMergeTime = mergeCommit.getCommitTime();
        return branchMergeTime - branchCreationTime;
    }

    private RevCommit getFirstCommit(Repository repository, Ref branch) throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().add(branch.getObjectId()).call();
            RevCommit firstCommit = null;
            for (RevCommit commit : commits) {
                firstCommit = commit;
            }
            return firstCommit;
        }
    }

    private RevCommit getMergeCommit(Repository repository, String branchName) throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().add(repository.resolve(branchName)).call();
            for (RevCommit commit : commits) {
                if (commit.getParentCount() > 1) {
                    return commit;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        if (branchLifetimes.isEmpty()) {
            return "No merged branches to calculate average branch time.";
        }

        long totalLifetime = branchLifetimes.stream().mapToLong(Long::longValue).sum();
        long averageLifetime = totalLifetime / branchLifetimes.size();

        Duration duration = Duration.ofSeconds(averageLifetime);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        return String.format("Average Branch Lifetime: %d days, %d hours, %d minutes", days, hours, minutes);
    }
}

