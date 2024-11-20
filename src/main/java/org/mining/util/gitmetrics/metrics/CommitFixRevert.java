package org.mining.util.gitmetrics.metrics;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.mining.util.gitmetrics.GitMetricAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class CommitFixRevert implements GitMetricAnalyzer<List<RevCommit>> {

    private final List<RevCommit> matchingCommits = new ArrayList<>();
    private final String[] keywords = {"revert", "fix"};

    @Override
    public void analyze(Repository repository) {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().call();
            for (RevCommit commit : commits) {
                String message = commit.getFullMessage().toLowerCase();
                for (String keyword : keywords) {
                    if (message.contains(keyword)) {
                        matchingCommits.add(commit);
                        break;
                    }
                }
            }
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Commits containing 'revert' or 'fix':\n");
        for (RevCommit commit : matchingCommits) {
            res.append("Commit ID: ").append(commit.getId().getName()).append("\n");
            res.append("Author: ").append(commit.getAuthorIdent().getName()).append("\n");
            res.append("Date: ").append(commit.getAuthorIdent().getWhen()).append("\n");
            res.append("Message: ").append(commit.getFullMessage()).append("\n");
            res.append("---------------------------------------------\n");
        }
        return res.toString();
    }

    @Override
    public List<RevCommit> returnResult() {
        return matchingCommits;
    }
}

