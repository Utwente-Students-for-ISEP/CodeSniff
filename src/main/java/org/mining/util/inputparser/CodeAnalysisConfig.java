package org.mining.util.inputparser;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CodeAnalysisConfig {
    private String repositoryPath;

    @JsonProperty("metrics")
    private Map<MetricEnum, MetricConfig> metrics;

    @JsonProperty("languageSpecificSettings")
    private Map<SupportedLanguages, LanguageConfig> languageSpecificSettings;

    @Getter
    public static class MetricConfig {
        private boolean enabled = true;
        private Integer maxMethodLength;
        private Integer classSizeThreshold;
        private Integer maxParameters;
        private Integer maxInheritanceDepth;
        private Integer maxCoupling;
        private Integer maxCyclomaticComplexity;
        private Integer maxDependencies;

        @JsonSetter("enabled")
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    @Getter
    public static class LanguageConfig {
        private boolean enabled = true;

        @JsonSetter("enabled")
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }
}
