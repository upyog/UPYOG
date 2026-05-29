package org.egov.waterconnection.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.waterconnection.repository.*;
import org.egov.waterconnection.web.models.WaterConnection;
import org.egov.waterconnection.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

import static org.egov.waterconnection.constants.WCConstants.*;

@Service
@Slf4j
public class WSFuzzySearchService {

    private ElasticSearchRepository elasticSearchRepository;
    private ObjectMapper mapper;
    private WaterDaoImpl WaterDaoImpl;

    @Autowired
    public WSFuzzySearchService(ElasticSearchRepository elasticSearchRepository, ObjectMapper mapper, WaterDaoImpl repository) {
        this.elasticSearchRepository = elasticSearchRepository;
        this.mapper = mapper;
        this.WaterDaoImpl = repository;
    }

    /**
     * Executes fuzzy search by coordinating between ES and the Database.
     * * @param requestInfo Standard eGov request metadata
     * @param criteria Search parameters (name, connectionNo, etc.)
     * @return Ordered list of WaterConnections
     */
    public List<WaterConnection> getConnections(RequestInfo requestInfo, SearchCriteria criteria) {

        log.info("Initiating fuzzy search with criteria: {}", criteria);

        // 1. Validate that at least one fuzzy field is provided
        validateFuzzySearchCriteria(criteria);

        // 2. Search ElasticSearch for matching IDs
        Object esResponse = elasticSearchRepository.fuzzySearchForConnections(criteria);

        // 3. Extract IDs and group by TenantId
        Map<String, Set<String>> tenantIdToConnectionNos = getTenantIdToConnectionNoMap(esResponse);

        if (CollectionUtils.isEmpty(tenantIdToConnectionNos)) {
            return new LinkedList<>();
        }

        List<WaterConnection> connections = new LinkedList<>();

        // 4. Hydrate full connection data from DB
        for (Map.Entry<String, Set<String>> entry : tenantIdToConnectionNos.entrySet()) {
            
            SearchCriteria dbCriteria = SearchCriteria.builder()
                    .tenantId(entry.getKey())
                    .connectionNumber(entry.getValue())
                    .build();

            // Fetching from DB ensures we have the latest owner/connection details
            connections.addAll(WaterDaoImpl.getWaterConnectionList(dbCriteria, requestInfo));
        }

        // 5. Restore the order of results based on ElasticSearch relevance score
        return orderByESScore(connections, esResponse);
    }

    /**
     * Re-sorts the DB results to match the ranking (score) provided by ES.
     */
    private List<WaterConnection> orderByESScore(List<WaterConnection> connections, Object esResponse) {

        List<WaterConnection> orderedConnections = new LinkedList<>();

        if (!CollectionUtils.isEmpty(connections)) {
            Map<String, WaterConnection> idToConnectionMap = new LinkedHashMap<>();

            // Map connections by their business identifier (Connection Number)
            connections.forEach(conn -> idToConnectionMap.put(conn.getConnectionNo(), conn));

            try {
                List<Map<String, Object>> data = JsonPath.read(esResponse, ES_DATA_PATH);

                if (!CollectionUtils.isEmpty(data)) {
                    for (Map<String, Object> map : data) {
                        String connNo = JsonPath.read(map, "$.connectionNo");
                        if (idToConnectionMap.containsKey(connNo)) {
                            orderedConnections.add(idToConnectionMap.get(connNo));
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to parse ES response during ordering phase", e);
                throw new CustomException("PARSING_ERROR", "Failed to extract connectionNos from ES response");
            }
        }

        return orderedConnections;
    }

    /**
     * Parses the ES response to create a map of TenantId to ConnectionNumbers.
     */
    private Map<String, Set<String>> getTenantIdToConnectionNoMap(Object esResponse) {

        Map<String, Set<String>> tenantIdToConnectionNos = new LinkedHashMap<>();

        try {
            List<Map<String, Object>> data = JsonPath.read(esResponse, ES_DATA_PATH);

            if (!CollectionUtils.isEmpty(data)) {
                for (Map<String, Object> map : data) {
                    String tenantId = JsonPath.read(map, "$.tenantId");
                    String connectionNo = JsonPath.read(map, "$.connectionNo");

                    // Check for null and empty string to avoid downstream Hydration errors
                    if (tenantId != null && connectionNo != null && !connectionNo.trim().isEmpty()) {
                        tenantIdToConnectionNos
                            .computeIfAbsent(tenantId, k -> new HashSet<>())
                            .add(connectionNo);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse ES response during ID extraction", e);
            throw new CustomException("PARSING_ERROR", "Failed to extract connection data from ES response");
        }

        return tenantIdToConnectionNos;
    }

    /**
     * Ensures search parameters are valid for a fuzzy search.
     */
    private void validateFuzzySearchCriteria(SearchCriteria criteria) {

        // 1. Absolute Mandatory: tenantId must be present for ALL searches
        if (criteria.getTenantId() == null || criteria.getTenantId().trim().isEmpty()) {
            throw new CustomException("EG_WS_SEARCH_TENANTID_MANDATORY", "TenantId is mandatory for all search operations.");
        }

        // 2. Dependency: If searching by Name, Locality is also mandatory
        if (criteria.getOwnerName() != null && criteria.getLocality() == null) {
            throw new CustomException("EG_WS_SEARCH_LOCALITY_MANDATORY", "Locality is mandatory when searching by Owner Name.");
        }

        // 3. Minimum Criteria: At least one fuzzy parameter must exist
        if (criteria.getConnectionNumber() == null && 
            criteria.getOldConnectionNumber() == null && 
            criteria.getOwnerName() == null && 
            criteria.getDoorNo() == null &&
            criteria.getLocality()==null
            ) {
            
            throw new CustomException("INVALID_SEARCH_CRITERIA", "Please provide at least one search parameter (Connection No, Name, or Door No).");
        }
    }
}