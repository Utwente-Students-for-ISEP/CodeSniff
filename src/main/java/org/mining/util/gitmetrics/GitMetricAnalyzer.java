package org.mining.util.gitmetrics;

import org.eclipse.jgit.lib.Repository;

public interface GitMetricAnalyzer<T> {
    void analyze(Repository repository);

    T returnResult();
}
