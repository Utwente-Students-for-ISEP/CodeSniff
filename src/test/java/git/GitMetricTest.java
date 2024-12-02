package git;

import org.antlr.v4.runtime.misc.Pair;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mining.util.gitmetrics.GitMetricAnalyzer;
import org.mining.util.gitmetrics.GitMetricAnalyzerBuilder;
import org.mining.util.gitmetrics.GitMetricEnum;
import org.mining.util.gitmetrics.GitMetricFactory;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.MetricEnum;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.mining.util.gitmetrics.GitMetricEnum.*;

public class GitMetricTest {

    private final String dir_url = "tempDir";
    private String url = "https://github.com/dkrgn/SearchEngine.git";
    private Git git;
    private File dir;
    private Repository repository;
    private GitMetricAnalyzerBuilder builder;

    private final CodeAnalysisConfig.MetricConfig metricConfig = new CodeAnalysisConfig.MetricConfig();

    @BeforeEach
    public void setUp() throws GitAPIException {
        git = getGit();
        dir = new File(dir_url);
        repository = git.getRepository();
    }

    @AfterEach
    public void cleanUp() {
        builder.getAnalyzers().clear();
        git.getRepository().close();
        deleteDirectory(dir);
    }

    private void analyze(Map<GitMetricEnum, CodeAnalysisConfig.MetricConfig> metrics) throws IOException {
        builder = new GitMetricAnalyzerBuilder();
        for (Map.Entry<GitMetricEnum, CodeAnalysisConfig.MetricConfig> entry : metrics.entrySet()) {
            GitMetricAnalyzer<?> metric = GitMetricFactory.getMetric(entry.getKey(), entry.getValue().getCommitDepth());
            builder.addMetric(metric);
        }
        builder.analyze(repository);
    }

    private Git getGit() throws GitAPIException {
        return Git.cloneRepository()
                .setURI(url)
                .setDirectory(new File(dir_url))
                .call();
    }

    private void deleteDirectory(File directory) {
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

    @Test
    public void testCommitFrequency() throws IOException {
        Map<LocalDate, Integer> expected = new TreeMap<>();
        expected.put(LocalDate.of(2023, 2, 25), 2);
        expected.put(LocalDate.of(2023, 8, 5), 1);
        expected.put(LocalDate.of(2023, 8, 7), 1);
        expected.put(LocalDate.of(2023, 8, 8), 1);
        expected.put(LocalDate.of(2023, 12, 28), 1);
        expected.put(LocalDate.of(2024, 11, 13), 4);
        metricConfig.setCommitDepth(-1);
        analyze(Map.of(CommitFrequency, metricConfig));
        assertEquals(expected, builder.getAnalyzers().get(0).returnResult());
    }

    @Test
    public void testCommitSize() throws IOException {
        int added = 9280;
        int deleted = 345;
        metricConfig.setCommitDepth(-1);
        analyze(Map.of(CommitSize, metricConfig));
        assertEquals(new Pair<>(added, deleted), builder.getAnalyzers().get(0).returnResult());
    }

    @Test
    public void testCommitFixRevert() throws IOException {
        metricConfig.setCommitDepth(4);
        analyze(Map.of(CommitFixRevert, metricConfig));
    }

    @Test
    public void testCodeOwnershipByFile() throws IOException {
        metricConfig.setCommitDepth(1);
        analyze(Map.of(CodeOwnershipByFile, metricConfig));
    }

    @Test
    public void testBranchTime() throws IOException {
        metricConfig.setCommitDepth(5);
        analyze(Map.of(BranchTime, metricConfig));
    }

    @Test
    public void testCodeChurn() throws IOException {
        metricConfig.setCommitDepth(-1);
        analyze(Map.of(CodeChurn, metricConfig));
    }
}
