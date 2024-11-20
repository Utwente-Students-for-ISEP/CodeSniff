package org.mining.util.gitmetrics;

import lombok.Getter;
import org.eclipse.jgit.lib.Repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GitMetricAnalyzerBuilder {
    private final List<GitMetricAnalyzer<?>> analyzers = new ArrayList<>();
    private final StringBuilder resultBuilder = new StringBuilder();

    public void addMetric(GitMetricAnalyzer<?> analyzer) {
        analyzers.add(analyzer);
    }

    public void analyze(Repository repository) throws IOException {
        for (GitMetricAnalyzer<?> analyzer : analyzers) {
            analyzer.analyze(repository);
            resultBuilder.append(analyzer).append("\n");
        }
        writeResultsToFile();
    }

    private void writeResultsToFile() throws IOException {
        String resourcePath = Paths.get("src", "main", "resources", "analysis_results.txt").toString();
        File file = new File(resourcePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(resultBuilder.toString());
        }
        System.out.println("Analysis results written to: " + file.getAbsolutePath());
    }
}

