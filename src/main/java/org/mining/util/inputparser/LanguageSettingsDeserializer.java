package org.mining.util.inputparser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LanguageSettingsDeserializer extends JsonDeserializer<List<CodeAnalysisConfig.LanguageConfig>> {

    @Override
    public List<CodeAnalysisConfig.LanguageConfig> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<CodeAnalysisConfig.LanguageConfig> languageConfigs = new ArrayList<>();

        node.fields().forEachRemaining(entry -> {
            CodeAnalysisConfig.LanguageConfig config = new CodeAnalysisConfig.LanguageConfig();
            config.setEnabled(entry.getValue().get("enabled").asBoolean());
            config.setLanguage(SupportedLanguages.valueOf(entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1)));
            languageConfigs.add(config);
        });

        return languageConfigs;
    }
}
