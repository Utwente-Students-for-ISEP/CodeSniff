package org.mining;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.mining.util.GitMetricAnalyzer;
import org.mining.util.GitMetricAnalyzerBuilder;
import org.mining.util.GitMetricFactory;
import org.mining.util.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GitAnalyzer {

    private static String url;
    private static String dir_url;
    private static List<String> metrics;

    public static void main(String[] args) throws Exception {
        //Read JSON File (in args file name is passed)
        readConfigFile("metrics.json");
        //Set up Repository and Connection
        File dir = new File(dir_url);
        Git git = getGit();
        Repository repository = git.getRepository();
        // Build metric analysis chain
        GitMetricAnalyzerBuilder builder = new GitMetricAnalyzerBuilder();
        for (String metricName : metrics) {
            GitMetricAnalyzer metric = GitMetricFactory.getMetric(metricName);
            builder.addMetric(metric);
        }
        // Analyze metrics
        builder.analyze(repository);
        //Cleanup
        git.getRepository().close();
        deleteDirectory(dir);
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

    public static void readConfigFile(String file) {
        if (file.isEmpty()) {
            file = "metrics.json";
        }
        ClassLoader classLoader = GitAnalyzer.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(file)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found! " + file);
            } else {
                JsonObject data = new ObjectMapper().readValue(inputStream, JsonObject.class);
                url = data.getUrl();
                dir_url = data.getDir();
                metrics = data.getMetrics();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
