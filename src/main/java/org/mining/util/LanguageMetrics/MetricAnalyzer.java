package org.mining.util.LanguageMetrics;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.ILanguageMetricGenerator;
import org.mining.util.LanguageMetrics.LanguageFactory.LanguageFactory;
import org.mining.util.LanguageMetrics.LanguageFactory.LanguageProcessingComponents;
import org.mining.util.LanguageMetrics.LanguageParsingStartegies.LanguageStrategyRunner;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.SupportedLanguages;

public class MetricAnalyzer {
    private final MetricBuilderAnalyzer metricBuilderAnalyzer;
    private final LanguageStrategyRunner languageStrategyRunner;

    public MetricAnalyzer() {
        this.metricBuilderAnalyzer = new MetricBuilderAnalyzer();
        this.languageStrategyRunner = new LanguageStrategyRunner();
    }
    public void runMetrics(CodeAnalysisConfig config){
        //DetectLanguages (through LanguageFactory)
        //Write them into MetricBuilder and call "analyze" - Generates config files for languages
        //Set up strategies
        //Start analyzers

        CodeAnalysisConfig.LanguageSettings languageSettings = config.getLanguageSpecificSettings();

        if (languageSettings.getJavaConfig() != null && languageSettings.getJavaConfig().isEnabled()) {
            LanguageProcessingComponents processingComponents = LanguageFactory.getMetricGenerator(SupportedLanguages.Java);
            if (processingComponents != null) {
                metricBuilderAnalyzer.addLanguageMetricGenerator(processingComponents.metricGenerator());
                languageStrategyRunner.addParsingStrategy(processingComponents.parserStrategy());

            }
        }
        if (languageSettings.getPythonConfig() != null && languageSettings.getPythonConfig().isEnabled()) {
            LanguageProcessingComponents processingComponents = LanguageFactory.getMetricGenerator(SupportedLanguages.Python);
            if (processingComponents != null) {
                metricBuilderAnalyzer.addLanguageMetricGenerator(processingComponents.metricGenerator());
                languageStrategyRunner.addParsingStrategy(processingComponents.parserStrategy());
            }
        }
        if (languageSettings.getJavascriptConfig() != null && languageSettings.getJavascriptConfig().isEnabled()) {
            LanguageProcessingComponents processingComponents = LanguageFactory.getMetricGenerator(SupportedLanguages.JavaScript);
            if (processingComponents != null) {
                metricBuilderAnalyzer.addLanguageMetricGenerator(processingComponents.metricGenerator());
                languageStrategyRunner.addParsingStrategy(processingComponents.parserStrategy());
            }
        }

        metricBuilderAnalyzer.analyze(config);
        languageStrategyRunner.execute(config.getRepositoryPath());

    }
}
