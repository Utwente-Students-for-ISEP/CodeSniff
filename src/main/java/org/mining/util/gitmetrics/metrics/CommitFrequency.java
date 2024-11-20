package org.mining.util.gitmetrics.metrics;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.mining.util.gitmetrics.GitMetricAnalyzer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.TreeMap;

public class CommitFrequency implements GitMetricAnalyzer<Map<LocalDate, Integer>> {

    private final Map<LocalDate, Integer> commitFrequency = new TreeMap<>();

    @Override
    public void analyze(Repository repository) {
        try (RevWalk revWalk = new RevWalk(repository)) {
            // Resolve the head (master/main branch) to the latest commit
            revWalk.markStart(revWalk.parseCommit(repository.resolve("refs/heads/main")));
            for (RevCommit commit : revWalk) {
                LocalDate commitDate = Instant.ofEpochSecond(commit.getCommitTime())
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                commitFrequency.put(commitDate, commitFrequency.getOrDefault(commitDate, 0) + 1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Commit Frequency Metric").append("\n");
        for (Map.Entry<LocalDate, Integer> entry : commitFrequency.entrySet()) {
            res.append(entry.getKey()).append(": ").append(entry.getValue()).append(" commits").append("\n");
        }
        res.append("---------------------------------------------\n");
        return res.toString();
    }

    @Override
    public Map<LocalDate, Integer> returnResult() {
        return commitFrequency;
    }
}
