package org.mining.util;

import org.mining.util.metrics.*;

public class GitMetricFactory {
    public static GitMetricAnalyzer getMetric(String metricName) {
        return switch (metricName) {
            case "CommitFrequency" -> new CommitFrequency();
            case "CommitSize" -> new CommitSize();
            case "CommitFixRevert" -> new CommitFixRevert();
            case "CodeOwnershipByFile" -> new CodeOwnershipByFile();
            case "BranchTime" -> new BranchTime();
            default -> throw new IllegalArgumentException("Unknown metric: " + metricName);
        };
    }
}
