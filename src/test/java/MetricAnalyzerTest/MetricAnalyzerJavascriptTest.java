package MetricAnalyzerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mining.util.LanguageMetrics.MetricAnalyzer;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.ConfigParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricAnalyzerJavascriptTest {

    private MetricAnalyzer metricAnalyzer;
    private CodeAnalysisConfig codeAnalysisConfig;

    @BeforeEach
    public void setUp() throws IOException {
        InputStream configInputStream = getClass().getClassLoader().getResourceAsStream("properties.json");
        if (configInputStream == null) {
            throw new IllegalArgumentException("Config file not found!");
        }
        codeAnalysisConfig = ConfigParser.parseConfig(configInputStream);

        metricAnalyzer = new MetricAnalyzer();
    }

    @Test
    void testReportGeneration() throws IOException {
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        assertTrue(generatedFile.exists(), "Generated ruleset file does not exist");
    }
}
