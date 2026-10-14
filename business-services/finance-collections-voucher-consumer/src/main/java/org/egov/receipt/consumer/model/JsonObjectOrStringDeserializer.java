package org.egov.receipt.consumer.model;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonObjectOrStringDeserializer extends JsonDeserializer<Map<String, Object>> {

	@Override
	public Map<String, Object> deserialize(final JsonParser parser, final DeserializationContext context)
			throws IOException {

		final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		JsonNode node = mapper.readTree(parser);

		if (node == null || node.isNull()) {
			return null;
		}

		if (node.isTextual()) {
			final String value = node.asText().trim();

			if (value.isEmpty()) {
				return null;
			}

			// Decode a JSON object supplied inside a string.
			try (JsonParser nestedParser = mapper.getFactory().createParser(value)) {

				node = mapper.readTree(nestedParser);

				if (nestedParser.nextToken() != null) {
					context.reportInputMismatch(Map.class, "Unexpected content after the JSON object");
				}
			}
		}

		if (node == null || node.isNull()) {
			return null;
		}

		if (!node.isObject()) {
			context.reportInputMismatch(Map.class, "Expected a JSON object or a string containing a JSON object");
		}

		return mapper.convertValue(node, new TypeReference<Map<String, Object>>() {
		});
	}
}