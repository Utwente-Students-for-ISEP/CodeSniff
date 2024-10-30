package org.mining.util;

import org.eclipse.jgit.lib.Repository;

public interface GitMetricAnalyzer {
    void analyze(Repository repository);
}
