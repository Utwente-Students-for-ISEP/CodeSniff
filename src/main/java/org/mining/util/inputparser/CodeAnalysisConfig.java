package org.mining.util.inputparser;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;

@Getter
public class CodeAnalysisConfig {
    private String repositoryPath;
    //String is concrete metric -> enum
    @Getter
    private Map<MetricEnum, MetricConfig> metrics;
    private LanguageSettings languageSpecificSettings;

    @Getter
    public static class MetricConfig {
        private boolean enabled = true;
        private Integer maxMethodLength;
        private Integer classSizeThreshold;
        private Integer maxParameters;
        private Integer maxInheritanceDepth;
        private Integer maxCoupling;

        @JsonSetter("enabled")
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
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
        // Each language config is enabled by default
        private boolean enabled = true;

        @JsonSetter("enabled")
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
