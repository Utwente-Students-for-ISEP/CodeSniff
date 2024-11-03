package org.mining.util.inputparser;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CodeAnalysisConfig {
    private String repositoryPath;
    private Map<String, MetricConfig> metrics;
    private LanguageSettings languageSpecificSettings;

    @Getter
    public static class MetricConfig {
        private boolean enabled;
        private Integer maxMethodLength;
        private Integer classSizeThreshold;
        private Integer maxParameters;
        private Integer maxInheritanceDepth;
        private Integer maxCoupling;
    }

    @Getter
    public static class LanguageSettings {
        @JsonProperty("java")
        private LanguageConfig javaConfig;
        @JsonProperty("python")
        private LanguageConfig pythonConfig;
        @JsonProperty("javascript")
        private LanguageConfig javascriptConfig;
    }

    @Getter
    public static class LanguageConfig {
        private boolean enabled;
    }
}
