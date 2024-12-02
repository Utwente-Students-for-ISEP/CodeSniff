package org.mining.util.gitmetrics.metrics;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.mining.util.gitmetrics.GitMetricAnalyzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeChurn implements GitMetricAnalyzer<Map<String, Map<String, Integer>>> {

    private final Map<String, Map<String, Integer>> churnMap = new HashMap<>();
    private final int commitDepth;

    public CodeChurn(int commitDepth) {
        this.commitDepth = commitDepth;
    }

    @Override
    public void analyze(Repository repository) {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().call();
            int count = 0;
            for (RevCommit commit : commits) {
                if (commitDepth > 0 && count >= commitDepth) {
                    break;
                }
                if (commit.getParentCount() > 0) {
                    RevCommit parent = commit.getParent(0);
                    analyzeCommitDiff(repository, parent, commit);
                }
                count++;
            }
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void analyzeCommitDiff(Repository repository, RevCommit parent, RevCommit commit) throws IOException {
        try (DiffFormatter diffFormatter = new DiffFormatter(new ByteArrayOutputStream())) {
            diffFormatter.setRepository(repository);
            AbstractTreeIterator parentTreeParser = prepareTreeParser(repository, parent);
            AbstractTreeIterator commitTreeParser = prepareTreeParser(repository, commit);
            List<DiffEntry> diffs = diffFormatter.scan(parentTreeParser, commitTreeParser);
            for (DiffEntry entry : diffs) {
                String filePath = entry.getNewPath();
                if (filePath.endsWith(".java")) {
                    EditList edits = diffFormatter.toFileHeader(entry).toEditList();
                    trackCodeChurn(filePath, edits, repository, commit);
                }
            }
        }
    }

    private void trackCodeChurn(String filePath, EditList edits, Repository repository, RevCommit commit) throws IOException {
        churnMap.putIfAbsent(filePath, new HashMap<>());
        String fileContent = readFileFromCommit(repository, commit, filePath);
        if (fileContent == null) return;
        List<MethodRange> methods = parseMethods(fileContent);
        for (Edit edit : edits) {
            int startLine = edit.getBeginB();
            int endLine = edit.getEndB();
            for (MethodRange method : methods) {
                if (method.overlaps(startLine, endLine)) {
                    churnMap.get(filePath).put(method.name, churnMap.get(filePath).getOrDefault(method.name, 0) + 1);
                }
            }
        }
    }

    private AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        treeParser.reset(repository.newObjectReader(), commit.getTree());
        return treeParser;
    }

    private String readFileFromCommit(Repository repository, RevCommit commit, String filePath) throws IOException {
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(filePath));
            if (!treeWalk.next()) {
                return null;
            }
            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            loader.copyTo(outputStream);
            return outputStream.toString();
        }
    }

    private List<MethodRange> parseMethods(String fileContent) {
        List<MethodRange> methods = new ArrayList<>();
        Pattern methodPattern = Pattern.compile(
                "^(\\s*(public|private|protected|\\s*)\\s*(static|final|abstract|\\s*)?\\s*\\w+\\s+\\w+\\s*\\(.*?\\))\\s*([{;])",
                Pattern.MULTILINE
        );
        String[] lines = fileContent.split("\n");
        for (int i = 0; i < lines.length; i++) {
            Matcher matcher = methodPattern.matcher(lines[i]);
            if (matcher.find()) {
                String methodSignature = matcher.group(1).trim();
                methods.add(new MethodRange(methodSignature, i + 1, fileContent));
            }
        }
        return methods;
    }

    @Override
    public Map<String, Map<String, Integer>> returnResult() {
        return churnMap;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Code Churn Analysis:\n");
        for (String filePath : churnMap.keySet()) {
            result.append("File: ").append(filePath).append("\n");
            Map<String, Integer> methodChurnMap = churnMap.get(filePath);
            for (Map.Entry<String, Integer> entry : methodChurnMap.entrySet()) {
                String methodName = entry.getKey();
                int modificationCount = entry.getValue();
                result.append(String.format("  - %s: %d modifications\n", methodName, modificationCount));
            }
            result.append("---------------------------------------------------\n");
        }
        return result.toString();
    }

    private static class MethodRange {
        String name;
        int startLine;
        int endLine;

        public MethodRange(String name, int startLine, String fileContent) {
            this.name = name;
            this.startLine = startLine;
            this.endLine = calculateEndLine(startLine, fileContent);
        }

        private int calculateEndLine(int startLine, String fileContent) {
            int braceCount = 0;
            String[] lines = fileContent.split("\n");
            for (int i = startLine - 1; i < lines.length; i++) {
                String line = lines[i];
                braceCount += countOccurrences(line, '{');
                braceCount -= countOccurrences(line, '}');
                if (braceCount == 0) {
                    return i + 1;
                }
            }
            return Integer.MAX_VALUE;
        }

        private int countOccurrences(String line, char c) {
            int count = 0;
            for (char ch : line.toCharArray()) {
                if (ch == c) count++;
            }
            return count;
        }

        public boolean overlaps(int start, int end) {
            return startLine <= end && endLine >= start;
        }
    }
}

