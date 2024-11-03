package org.mining.util.gitmetrics.metrics;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.mining.util.gitmetrics.GitMetricAnalyzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CommitSize implements GitMetricAnalyzer {

    private int totalLinesAdded = 0;
    private int totalLinesDeleted = 0;

    @Override
    public void analyze(Repository repository) {
        try (Git git = new Git(repository);
             RevWalk revWalk = new RevWalk(repository)) {
             Iterable<RevCommit> commits = git.log().call();
             for (RevCommit commit : commits) {
                 if (commit.getParentCount() > 0) {
                     RevCommit parentCommit = revWalk.parseCommit(commit.getParent(0));
                     analyzeCommitDiff(repository, parentCommit, commit);
                 }
             }
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void analyzeCommitDiff(Repository repository, RevCommit parent, RevCommit commit) throws IOException {
        try (DiffFormatter diffFormatter = new DiffFormatter(new ByteArrayOutputStream())) {
            diffFormatter.setRepository(repository);

            // Get the tree iterator for the parent and current commit
            AbstractTreeIterator parentTreeParser = prepareTreeParser(repository, parent);
            AbstractTreeIterator commitTreeParser = prepareTreeParser(repository, commit);

            // Calculate the diff entries between parent and commit
            List<DiffEntry> diffs = diffFormatter.scan(parentTreeParser, commitTreeParser);

            for (DiffEntry entry : diffs) {
                // Get the list of edits (insertions, deletions, and modifications)
                EditList edits = diffFormatter.toFileHeader(entry).toEditList();

                for (Edit edit : edits) {
                    totalLinesAdded += edit.getEndB() - edit.getBeginB();  // Lines added
                    totalLinesDeleted += edit.getEndA() - edit.getBeginA();  // Lines deleted
                }
            }
        }
    }

    private AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) {
        try {
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            treeParser.reset(repository.newObjectReader(), commit.getTree());
            return treeParser;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Commit Size Metric" + "\n" +
                "Total Lines Added: " + totalLinesAdded + "\n" +
                "Total Lines Deleted: " + totalLinesDeleted + "\n" +
                "---------------------------------------------\n";
    }
}
