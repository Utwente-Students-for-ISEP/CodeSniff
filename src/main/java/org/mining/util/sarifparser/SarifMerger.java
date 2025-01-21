package org.mining.util.sarifparser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SarifMerger {

    public static void startMerge(List<String> filePaths, String outputPath) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode mergedSarif = objectMapper.createObjectNode();

        // Initialize SARIF metadata
        mergedSarif.put("version", "2.1.0");
        mergedSarif.put("$schema", "https://json.schemastore.org/sarif-2.1.0.json");

        ArrayNode mergedRuns = objectMapper.createArrayNode();

        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                System.err.println("File does not exist or is not a valid file: " + filePath);
                continue;
            }
            try {
                JsonNode sarifContent = objectMapper.readTree(file);
                if (sarifContent.has("runs")) {
                    ArrayNode runs = (ArrayNode) sarifContent.get("runs");
                    mergedRuns.addAll(runs);
                } else {
                    System.err.println("File does not contain a valid 'runs' array: " + filePath);
                }
            } catch (IOException e) {
                System.err.println("Error reading or parsing SARIF file: " + filePath);
                e.printStackTrace();
            }
        }

        if (mergedRuns.isEmpty()) {
            System.err.println("No valid SARIF runs found. Merged file will be empty.");
        }

        mergedSarif.set("runs", mergedRuns);

        try {
            // Write the merged SARIF to the output file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputPath), mergedSarif);
            System.out.println("Merged SARIF file created: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error writing merged SARIF file: " + outputPath);
            e.printStackTrace();
        }
    }

    public static void mergeSarif() {
        try {
            // Example usage
            List<String> sarifFiles = List.of("src/main/resources/java_sarif.sarif", "src/main/resources/eslint_sarif.sarif", "src/main/resources/jgit_sarif.sarif");
            String outputFile = "Final.sarif";
            startMerge(sarifFiles, outputFile);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
