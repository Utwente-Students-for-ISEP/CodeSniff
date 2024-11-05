package org.mining.util.gitmetrics.metrics;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.mining.util.gitmetrics.GitMetricAnalyzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeOwnershipByFile implements GitMetricAnalyzer {

    private final Map<String, Map<String, Integer>> fileOwnership = new HashMap<>();

    @Override
    public void analyze(Repository repository) {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().call();
            for (RevCommit commit : commits) {
                String author = commit.getAuthorIdent().getName();
                analyzeCommitDiff(repository, commit, author);
            }
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void analyzeCommitDiff(Repository repository, RevCommit commit, String author) throws IOException {
        if (commit.getParentCount() == 0) {
            return;
        }
        RevCommit parent = commit.getParent(0);
        try (DiffFormatter diffFormatter = new DiffFormatter(new ByteArrayOutputStream())) {
            diffFormatter.setRepository(repository);
            AbstractTreeIterator parentTreeParser = prepareTreeParser(repository, parent);
            AbstractTreeIterator commitTreeParser = prepareTreeParser(repository, commit);
            List<DiffEntry> diffs = diffFormatter.scan(parentTreeParser, commitTreeParser);
            for (DiffEntry entry : diffs) {
                String filePath = entry.getNewPath();
                fileOwnership.putIfAbsent(filePath, new HashMap<>());
                Map<String, Integer> authorChanges = fileOwnership.get(filePath);
                authorChanges.put(author, authorChanges.getOrDefault(author, 0) + 1);
            }
        }
    }

    private AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        treeParser.reset(repository.newObjectReader(), commit.getTree());
        return treeParser;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Code Ownership by File:\n");
        for (String filePath : fileOwnership.keySet()) {
            Map<String, Integer> authorChanges = fileOwnership.get(filePath);
            int totalChanges = authorChanges.values().stream().mapToInt(Integer::intValue).sum();
            String topContributor = findTopContributor(authorChanges);
            result.append("File: ").append(filePath).append("\n");
            result.append("Top Contributor: ").append(topContributor)
                    .append(" (").append(authorChanges.get(topContributor) * 100 / totalChanges).append("% of changes)\n");
            result.append("All Contributors:\n");
            for (Map.Entry<String, Integer> entry : authorChanges.entrySet()) {
                String author = entry.getKey();
                int changes = entry.getValue();
                double percentage = (double) changes / totalChanges * 100;
                result.append(String.format("  - %s: %d changes (%.2f%%)\n", author, changes, percentage));
            }
            result.append("---------------------------------------------\n");
        }
        return result.toString();
    }


    private String findTopContributor(Map<String, Integer> authorChanges) {
        String topContributor = null;
        int maxChanges = 0;
        for (Map.Entry<String, Integer> entry : authorChanges.entrySet()) {
            if (entry.getValue() > maxChanges) {
                topContributor = entry.getKey();
                maxChanges = entry.getValue();
            }
        }
        return topContributor;
    }
}
