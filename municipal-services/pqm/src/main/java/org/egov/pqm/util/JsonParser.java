package org.egov.pqm.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.pqm.error.CustomException;

import java.util.HashMap;
import java.util.Map;

public class JsonParser<T> {
    private final ObjectMapper objectMapper;

    public JsonParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, T> parseJsonToMap(String jsonData, Class<T> entityClass) {
        Map<String, T> codeToEntityMap = new HashMap<>();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            JsonNode entityArray = jsonNode.get("mdms");

            for (JsonNode entityNode : entityArray) {
                String code = entityNode.get("data").get("code").asText();
                T entity = objectMapper.convertValue(entityNode.get("data"), entityClass);

                codeToEntityMap.put(code, entity);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorConstants.PARSING_ERROR, "Unable to make Code-Entity Map");
        }

        return codeToEntityMap;
    }


}
