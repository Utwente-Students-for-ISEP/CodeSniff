package org.mining.util.LanguageMetrics.LanguageFactory;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.JavaRulesetGenerator;
import org.mining.util.LanguageMetrics.LanguageParsingStartegies.JavaParserStrategy;
import org.mining.util.inputparser.SupportedLanguages;

public class LanguageFactory {
    public static LanguageProcessingComponents getMetricGenerator(SupportedLanguages language){
        return switch (language) {
            case Java -> new LanguageProcessingComponents(new JavaRulesetGenerator(), new JavaParserStrategy());
            default -> null;
        };
    }
}

