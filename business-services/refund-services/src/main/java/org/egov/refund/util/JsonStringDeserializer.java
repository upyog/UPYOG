package org.egov.refund.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonStringDeserializer extends JsonDeserializer<String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {

        JsonNode node = parser.readValueAsTree();

        if (node == null || node.isNull()) {
            return null;
        }

        if (node.isTextual()) {
            return node.asText();
        }

        return objectMapper.writeValueAsString(node);
    }
}