package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavaMetrics;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.IMetric;
import org.mining.util.inputparser.CodeAnalysisConfig;

import java.nio.file.Path;

public class JavaCyclomaticComplexity extends AbstractJavaMetric {

    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder xmlBuilder ) {
        xmlBuilder.append("    <rule ref=\"category/java/design.xml/CyclomaticComplexity\" />\n");
    }
}
