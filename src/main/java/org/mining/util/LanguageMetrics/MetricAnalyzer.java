package org.mining.util.LanguageMetrics;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.ILanguageMetricGenerator;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.SupportedLanguages;

import java.util.List;

public class MetricAnalyzer {
    private final MetricBuilderAnalyzer metricBuilderAnalyzer;

    public MetricAnalyzer() {
        this.metricBuilderAnalyzer = new MetricBuilderAnalyzer();
    }
    public void runMetrics(CodeAnalysisConfig config){
        //DetectLanguages (through LanguageFactory)
        //Write them into MetricBuilder and call "analyze" - Generates config files for languages
        //Start analyzers

        CodeAnalysisConfig.LanguageSettings languageSettings = config.getLanguageSpecificSettings();

        // Проверяем и добавляем генератор для каждого поддерживаемого языка, если он включен
        if (languageSettings.getJavaConfig() != null && languageSettings.getJavaConfig().isEnabled()) {
            ILanguageMetricGenerator javaGenerator = LanguageFactory.getMetricGenerator(SupportedLanguages.Java);
            if (javaGenerator != null) {
                metricBuilderAnalyzer.addLanguageMetricGenerator(javaGenerator);
            }
        }
        if (languageSettings.getPythonConfig() != null && languageSettings.getPythonConfig().isEnabled()) {
            ILanguageMetricGenerator pythonGenerator = LanguageFactory.getMetricGenerator(SupportedLanguages.Python);
            if (pythonGenerator != null) {
                metricBuilderAnalyzer.addLanguageMetricGenerator(pythonGenerator);
            }
        }
        if (languageSettings.getJavascriptConfig() != null && languageSettings.getJavascriptConfig().isEnabled()) {
            ILanguageMetricGenerator jsGenerator = LanguageFactory.getMetricGenerator(SupportedLanguages.JavaScript);
            if (jsGenerator != null) {
                metricBuilderAnalyzer.addLanguageMetricGenerator(jsGenerator);
            }
        }

        // Запускаем анализ метрик для всех добавленных генераторов
        metricBuilderAnalyzer.analyze(config);

    }
}
