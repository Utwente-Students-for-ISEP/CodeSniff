package org.mining.util.gitmetrics;

import org.eclipse.jgit.lib.Repository;

public interface GitMetricAnalyzer {
    void analyze(Repository repository);
}
