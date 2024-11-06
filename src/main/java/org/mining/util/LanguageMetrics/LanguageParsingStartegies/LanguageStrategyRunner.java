package org.mining.util.LanguageMetrics.LanguageParsingStartegies;

import java.util.ArrayList;
import java.util.List;

public class LanguageStrategyRunner {
    private final List<ILanguageParserStrategy> parsingStrategies = new ArrayList<>();

    public void addParsingStrategy(ILanguageParserStrategy languageStrategy){
        parsingStrategies.add(languageStrategy);
    }

    /**
     * Executes all registered language parsing strategies for the specified source directory.
     *
     * <p>This method iterates over each parsing strategy in {@code parsingStrategies} and
     * invokes {@link #execute(ILanguageParserStrategy, String)} on each, providing the
     * directory where source code is located. Each strategy will handle parsing in its
     * own language-specific way.</p>
     *
     * @param sourceDir The directory path containing the source code files to be analyzed
     *                  by each parsing strategy.
     */
    public void execute(String sourceDir){
        for(ILanguageParserStrategy strategy: parsingStrategies){
            execute(strategy, sourceDir);
        }
    }
    /**
     * Executes a single language parsing strategy for the specified source directory.
     *
     * <p>This method delegates to the {@link ILanguageParserStrategy#execute(String)} method
     * of the given strategy, which performs parsing on the provided source directory.</p>
     *
     * @param languageStrategy The language parsing strategy to execute.
     * @param sourceDir        The directory path containing the source code files to be analyzed.
     */
    public void execute(ILanguageParserStrategy languageStrategy, String sourceDir){
        languageStrategy.execute(sourceDir);
    }
}
