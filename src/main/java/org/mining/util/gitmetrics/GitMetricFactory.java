package org.mining.util.gitmetrics;

import org.mining.util.gitmetrics.metrics.*;
import org.mining.util.inputparser.MetricEnum;

public class GitMetricFactory {
    public static GitMetricAnalyzer getMetric(MetricEnum metricName) {
        return switch (metricName) {
            case CommitFrequency -> new CommitFrequency();
            case CommitSize -> new CommitSize();
            case CommitFixRevert -> new CommitFixRevert();
            case CodeOwnershipByFile -> new CodeOwnershipByFile();
            case BranchTime -> new BranchTime();
            default -> null;
        };
    }
}
