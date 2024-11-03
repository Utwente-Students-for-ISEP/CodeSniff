package org.mining;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.mining.util.gitmetrics.GitMetricAnalyzer;
import org.mining.util.gitmetrics.GitMetricAnalyzerBuilder;
import org.mining.util.gitmetrics.GitMetricFactory;
import org.mining.util.gitmetrics.JsonObject;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.ConfigParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GitAnalyzer {

    private static String url;
    private static String dir_url;
    private static CodeAnalysisConfig codeAnalysisConfig;

    public static void main(String[] args) throws Exception {
        getConfig();
        Git git = Git.open(new File(codeAnalysisConfig.getRepositoryPath()));
        Repository repository = git.getRepository();
        // Build metric analysis chain
        GitMetricAnalyzerBuilder builder = new GitMetricAnalyzerBuilder();
        for (String metricName : codeAnalysisConfig.getMetrics().keySet()) {
            GitMetricAnalyzer metric = GitMetricFactory.getMetric(metricName);
            if (metric != null) {
                builder.addMetric(metric);
            }
        }
        // Analyze metrics
        builder.analyze(repository);
        //Cleanup
        git.getRepository().close();
        //deleteDirectory(dir);
    }

    private static Git getGit() throws GitAPIException {
        return Git.cloneRepository()
                .setURI(url)
                .setDirectory(new File(dir_url))
                .call();
    }

    private static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        boolean isDeleted = directory.delete();
        if (!isDeleted) {
            System.err.println("Failed to delete: " + directory.getAbsolutePath());
        }
    }

    private static void getConfig() throws IOException {
        InputStream input = GitAnalyzer.class.getClassLoader().getResourceAsStream("properties.json");
        if (input == null) {
            throw new IllegalArgumentException("File not found! properties.json");
        }

        codeAnalysisConfig = ConfigParser.parseConfig(input);
    }
}
