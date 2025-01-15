package org.mining;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GitAnalyzerTest {
    @Test
    void testDeleteDirectory() {
        // Create a nested directory structure
        File dir = new File("testDir");
        File subDir = new File(dir, "subDir");
        subDir.mkdirs();

        File file = new File(subDir, "file.txt");
        try {
            assertTrue(file.createNewFile());
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }

        // Ensure directory and file exist
        assertTrue(file.exists());
        assertTrue(dir.exists());

        // Delete the directory
        GitAnalyzer.deleteDirectory(dir);

        // Verify deletion
        assertFalse(dir.exists());
    }

    @Test
    void testGetConfig() throws IOException {
        // Ensure the properties.json file exists in the correct location
        String resourcePath = GitAnalyzer.class.getClassLoader().getResource("properties.json").getPath();
        assertNotNull(resourcePath);

        // Call getConfig and verify the parsed config is not null
        GitAnalyzer.getConfig();
        assertNotNull(GitAnalyzer.codeAnalysisConfig);
    }

}