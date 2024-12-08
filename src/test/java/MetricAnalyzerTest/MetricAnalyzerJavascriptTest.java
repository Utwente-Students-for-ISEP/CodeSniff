package MetricAnalyzerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mining.util.LanguageMetrics.MetricAnalyzer;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.ConfigParser;

import java.io.*;

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
        String currentDirectory = System.getProperty("user.dir");

        // Create the folder path
        File folder = new File(currentDirectory + File.separator + "src" + File.separator +
                "test" + File.separator + "resources" + File.separator + "TestJavascriptFiles");
        codeAnalysisConfig.setRepositoryPath(folder.getAbsolutePath());

        metricAnalyzer = new MetricAnalyzer();
    }

    @Test
    void testReportGeneration() throws IOException {
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        assertTrue(generatedFile.exists(), "Generated ruleset file does not exist");
    }
    @Test
    void testReportCorrect() throws IOException {
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        String expectedLine = "\"ruleId\":\"custom-rules/depth-of-inheritance-tree\",\"severity\":1,"
                + "\"message\":\"Class \\\"Puppy\\\" in file \\\"/app/example.js\\\" has a Depth of Inheritance Tree (DIT) of 3, "
                + "which exceeds the threshold of 2.\"";

        // Variable to track if the line is found
        boolean lineFound = false;

        // Read the file line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(generatedFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(expectedLine)) {
                    lineFound = true;
                    break;
                }
            }
        }

        assertTrue(lineFound, "The file does not contain the expected line.");
    }




}
