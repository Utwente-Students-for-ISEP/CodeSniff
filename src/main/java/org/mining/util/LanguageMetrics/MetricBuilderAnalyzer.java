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
    public void analyze(CodeAnalysisConfig config){
        for(ILanguageMetricGenerator generator: languageMetricGenerators){
            generator.generateAnalyzer(config.getMetrics());
        }
    }
}
