package org.mining.util.gitmetrics.metrics;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.mining.util.gitmetrics.GitMetricAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class BranchCountWithAuthors implements GitMetricAnalyzer<Map<String, List<BranchCountWithAuthors.BranchDetail>>> {

    private final Map<String, List<BranchDetail>> authorBranchDetails = new HashMap<>();
    private int totalBranchCount = 0;
    private final int commitDepth;

    public BranchCountWithAuthors(int commitDepth) {
        this.commitDepth = commitDepth;
    }

    @Override
    public void analyze(Repository repository) {
        try (Git git = new Git(repository);
             RevWalk revWalk = new RevWalk(repository)) {
            List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
            totalBranchCount = branches.size();
            for (Ref branch : branches) {
                String author = getBranchAuthor(repository, branch, revWalk);
                if (author == null) {
                    author = "Unknown";
                }
                int commitsProcessed = getCommitDepth(repository, branch);
                authorBranchDetails.putIfAbsent(author, new ArrayList<>());
                authorBranchDetails.get(author).add(new BranchDetail(branch.getName(), commitsProcessed));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getBranchAuthor(Repository repository, Ref branch, RevWalk revWalk) throws IOException, GitAPIException {
        Iterable<RevCommit> commits = new Git(repository).log().add(branch.getObjectId()).call();
        for (RevCommit commit : commits) {
            revWalk.parseCommit(commit);
            return commit.getAuthorIdent().getName();
        }
        return null;
    }

    private int getCommitDepth(Repository repository, Ref branch) throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            return (int) StreamSupport.stream(git.log().add(branch.getObjectId()).call().spliterator(), false)
                    .limit(commitDepth > 0 ? commitDepth : Long.MAX_VALUE)
                    .count();
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Branch Count with Authors and Commit Depth:\n");
        result.append("Total Branches: ").append(totalBranchCount).append("\n");
        for (Map.Entry<String, List<BranchDetail>> entry : authorBranchDetails.entrySet()) {
            result.append("Author: ").append(entry.getKey()).append("\n");
            for (BranchDetail detail : entry.getValue()) {
                result.append("  - Branch: ").append(detail.branchName())
                        .append(", Commits: ").append(detail.commitDepth()).append("\n");
            }
        }
        result.append("---------------------------------------------\n");
        return result.toString();
    }

    @Override
    public Map<String, List<BranchDetail>> returnResult() {
        return authorBranchDetails;
    }

    public record BranchDetail(String branchName, int commitDepth) {}
}