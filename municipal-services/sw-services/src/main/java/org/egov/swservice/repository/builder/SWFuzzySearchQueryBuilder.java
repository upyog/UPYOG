package org.egov.swservice.repository.builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.swservice.config.SWConfiguration;
import org.egov.swservice.web.models.SearchCriteria;
import org.egov.tracer.model.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SWFuzzySearchQueryBuilder {

    private ObjectMapper mapper;
    private SWConfiguration config; // Updated to SW config

    @Autowired
    public SWFuzzySearchQueryBuilder(ObjectMapper mapper, SWConfiguration config) {
        this.mapper = mapper;
        this.config = config;
    }

    private static final String BASE_QUERY = "{\n" +
            "  \"from\": {{OFFSET}},\n" +
            "  \"size\": {{LIMIT}},\n" +
            "  \"sort\": {\n" +
            "    \"Data.auditDetails.createdTime\": { \"order\": \"desc\" }\n" +
            "  },\n" +
            "  \"query\": {}\n" +
            "}";

    private static final String wildCardQueryTemplate = "{\"query_string\": {\"default_field\": \"{{VAR}}\", \"query\": \"*{{PARAM}}*\"}}";
    private static final String queryTemplate = "{\"query_string\": {\"default_field\": \"{{VAR}}\", \"query\": \"{{PARAM}}\"}}";

    public String getFuzzySearchQuery(SearchCriteria criteria) {
        try {
            String baseQuery = addPagination(criteria);
            JsonNode node = mapper.readTree(baseQuery);
            ObjectNode insideMatch = (ObjectNode) node.get("query");
            List<JsonNode> mustList = new LinkedList<>();

            // 1. Tenant Filter
            if (criteria.getTenantId() != null) {
                mustList.add(getInnerNode(criteria.getTenantId(), "Data.tenantId", false));
            }

            // 2. Owner Name Filter (Checks additionalDetails OR connectionHolders)
            if (criteria.getOwnerName() != null) {
                List<JsonNode> nameShouldClauses = new LinkedList<>();
                nameShouldClauses.add(getInnerNode(criteria.getOwnerName(), "Data.additionalDetails.ownerName", true));
                nameShouldClauses.add(getInnerNode(criteria.getOwnerName(), "Data.connectionHolders.name", true));

                Map<String, Object> shouldBool = new HashMap<>();
                shouldBool.put("should", nameShouldClauses);
                shouldBool.put("minimum_should_match", 1);
                
                mustList.add(mapper.convertValue(new HashMap<String, Object>() {{ put("bool", shouldBool); }}, JsonNode.class));
            }

            // 3. Sewerage Connection Number
            if (!CollectionUtils.isEmpty(criteria.getConnectionNumber())) {
                for (String connNo : criteria.getConnectionNumber()) {
                    mustList.add(getInnerNode(connNo, "Data.connectionNo.keyword", false));
                }
            }

            // 4. Application Number
            if (!CollectionUtils.isEmpty(criteria.getApplicationNumber())) {
                for (String appNo : criteria.getApplicationNumber()) {
                    mustList.add(getInnerNode(appNo, "Data.applicationNo.keyword", false));
                }
            }

            // 5. Mobile Number
            if (criteria.getMobileNumber() != null) {
                mustList.add(getInnerNode(criteria.getMobileNumber(), "Data.connectionHolders.mobileNumber.keyword", false));
            }

            // 6. Property ID
            if (criteria.getPropertyId() != null) {
                mustList.add(getInnerNode(criteria.getPropertyId(), "Data.propertyId.keyword", false));
            }

            // 7. Locality
            if (criteria.getLocality() != null) {
                mustList.add(getInnerNode(criteria.getLocality(), "Data.additionalDetails.locality", false));
            }

            // 8. Application Status
            if (criteria.getStatus() != null) {
                mustList.add(getInnerNode(criteria.getStatus(), "Data.applicationStatus.keyword", false));
            }

            // 9. Date Range Filter
            if (criteria.getFromDate() != null || criteria.getToDate() != null) {
                Map<String, Object> rangeParams = new HashMap<>();
                if (criteria.getFromDate() != null) rangeParams.put("gte", criteria.getFromDate());
                if (criteria.getToDate() != null) rangeParams.put("lte", criteria.getToDate());
                
                Map<String, Object> rangeField = new HashMap<>();
                rangeField.put("Data.auditDetails.createdTime", rangeParams);
                mustList.add(mapper.convertValue(new HashMap<String, Object>() {{ put("range", rangeField); }}, JsonNode.class));
            }

            // Final query assembly
            Map<String, Object> boolMap = new HashMap<>();
            boolMap.put("must", mustList);
            insideMatch.set("bool", mapper.convertValue(boolMap, JsonNode.class));

            return mapper.writeValueAsString(node);

        } catch (Exception e) {
            log.error("ES_SW_QUERY_BUILDER_ERROR", e);
            throw new CustomException("QUERY_BUILD_ERROR", "Failed to build JSON query for Sewerage Connection fuzzy search");
        }
    }

    private JsonNode getInnerNode(String param, String var, boolean isWildCard) throws JsonProcessingException {
        String template = isWildCard ? wildCardQueryTemplate : queryTemplate;
        String innerQuery = template.replace("{{PARAM}}", getEscapedString(param));
        innerQuery = innerQuery.replace("{{VAR}}", var);
        return mapper.readTree(innerQuery);
    }

    private String addPagination(SearchCriteria criteria) {
        Long limit = config.getDefaultLimit().longValue();
        Long offset = config.getDefaultOffset().longValue();

        if (criteria.getLimit() != null) {
            limit = Math.min(criteria.getLimit().longValue(), config.getMaxLimit().longValue());
        }
        if (criteria.getOffset() != null) {
            offset = criteria.getOffset().longValue();
        }

        return BASE_QUERY.replace("{{OFFSET}}", offset.toString())
                         .replace("{{LIMIT}}", limit.toString());
    }

    private String getEscapedString(String inputString) {
        final String[] metaCharacters = {"\\", "/", "^", "$", "{", "}", "[", "]", "(", ")", "*", "+", "?", "|", "<", ">", "-", "&", "%"};
        for (String character : metaCharacters) {
            if (inputString.contains(character)) {
                inputString = inputString.replace(character, "\\\\" + character);
            }
        }
        return inputString;
    }
}