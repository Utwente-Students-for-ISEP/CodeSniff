package org.mining.util.LanguageMetrics.LanguageParsingStartegies;

import java.util.ArrayList;
import java.util.List;

public class LanguageStrategyRunner {
    private final List<ILanguageParserStrategy> parsingStrategies = new ArrayList<>();

    public void addParsingStrategy(ILanguageParserStrategy languageStrategy){
        parsingStrategies.add(languageStrategy);
    }

    public void execute(String sourceDir){
        for(ILanguageParserStrategy strategy: parsingStrategies){
            execute(strategy, sourceDir);
        }
    }

    public void execute(ILanguageParserStrategy languageStrategy, String sourceDir){
        languageStrategy.execute(sourceDir);
    }
}
