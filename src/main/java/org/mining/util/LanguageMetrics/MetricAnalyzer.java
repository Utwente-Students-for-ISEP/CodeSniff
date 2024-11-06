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
    /**
     * Runs the metrics analysis process for the specified configuration.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *     <li>Detects enabled languages based on the {@link CodeAnalysisConfig}.</li>
     *     <li>Retrieves corresponding {@link LanguageProcessingComponents} for each enabled language from {@link LanguageFactory}.</li>
     *     <li>Adds each language's {@code ILanguageMetricGenerator} to the {@code metricBuilderAnalyzer} to prepare for metrics generation.</li>
     *     <li>Adds each language's {@code ILanguageParserStrategy} to the {@code languageStrategyRunner} for syntax parsing.</li>
     *     <li>Triggers metrics generation by calling {@code metricBuilderAnalyzer.analyze()}.</li>
     *     <li>Executes language parsing strategies through {@code languageStrategyRunner.execute()}.</li>
     * </ul>
     *
     * <p>Supported languages include Java, Python, and JavaScript, each identified in the
     * {@link CodeAnalysisConfig.LanguageSettings} object.</p>
     *
     * @param config The {@link CodeAnalysisConfig} containing language settings and other
     *               configurations for metrics generation and analysis.
     */
    public void runMetrics(CodeAnalysisConfig config){
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
