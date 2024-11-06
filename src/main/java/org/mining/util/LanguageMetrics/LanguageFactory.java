package org.mining.util.LanguageMetrics;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.ILanguageMetricGenerator;
import org.mining.util.LanguageMetrics.LanguageAnalyzer.JavaRulesetGenerator;
import org.mining.util.inputparser.SupportedLanguages;

public class LanguageFactory {
    public static ILanguageMetricGenerator getMetricGenerator(SupportedLanguages language){
        return switch (language) {
            case Java -> new JavaRulesetGenerator();
            default -> null;
        };
    }
}
