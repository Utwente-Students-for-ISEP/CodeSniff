package org.mining.util.LanguageMetrics;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.ILanguageMetricGenerator;
import org.mining.util.inputparser.CodeAnalysisConfig;

import java.util.ArrayList;
import java.util.List;

public class MetricBuilderAnalyzer {
    private final List<ILanguageMetricGenerator> languageMetricGenerators = new ArrayList<>();

    public void addLanguageMetricGenerator(ILanguageMetricGenerator generator){
        languageMetricGenerators.add(generator);
    }
    /**
     * Analyzes the code by iterating through each registered language metric generator.
     *
     * <p>This method executes the metric generation process for each language metric generator
     * that has been added to the {@code languageMetricGenerators} list. For each generator,
     * it calls {@code generateAnalyzer()} with the metrics configuration from the given
     * {@link CodeAnalysisConfig}.</p>
     *
     * @param config The {@link CodeAnalysisConfig} containing the metrics settings to be used by each generator.
     *               This includes specific metrics configurations for each enabled metric.
     */
    public void analyze(CodeAnalysisConfig config){
        for(ILanguageMetricGenerator generator: languageMetricGenerators){
            generator.generateAnalyzer(config.getMetrics());
        }
    }
}
