package org.egov.swservice.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swservice.repository.ElasticSearchRepository;
import org.egov.swservice.repository.SewerageDaoImpl;
import org.egov.swservice.web.models.*;
import org.egov.tracer.model.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

//Note the 'static' keyword and the '*' or specific variable name
import static org.egov.swservice.util.SWConstants.ES_DATA_PATH;



@Service
@Slf4j
public class SWFuzzySearchService {

    private ElasticSearchRepository elasticSearchRepository;
    private ObjectMapper mapper;
    private SewerageDaoImpl sewerageDao; // Changed from WaterDaoImpl

    @Autowired
    public SWFuzzySearchService(ElasticSearchRepository elasticSearchRepository, ObjectMapper mapper, SewerageDaoImpl repository) {
        this.elasticSearchRepository = elasticSearchRepository;
        this.mapper = mapper;
        this.sewerageDao = repository;
    }

    /**
     * Executes fuzzy search by coordinating between ES and the Database for Sewerage Connections.
     * * @param requestInfo Standard eGov request metadata
     * @param criteria Search parameters (name, connectionNo, etc.)
     * @return Ordered list of SewerageConnections
     */
    public List<SewerageConnection> getConnections(RequestInfo requestInfo, SearchCriteria criteria) {

        log.info("Initiating SW fuzzy search with criteria: {}", criteria);

        // 1. Validate that at least one fuzzy field is provided
        validateFuzzySearchCriteria(criteria);

        // 2. Search ElasticSearch for matching IDs
        Object esResponse = elasticSearchRepository.fuzzySearchForConnections(criteria);

        // 3. Extract IDs and group by TenantId
        Map<String, Set<String>> tenantIdToConnectionNos = getTenantIdToConnectionNoMap(esResponse);

        if (CollectionUtils.isEmpty(tenantIdToConnectionNos)) {
            return new LinkedList<>();
        }

        List<SewerageConnection> connections = new LinkedList<>();

        // 4. Hydrate full connection data from DB
        for (Map.Entry<String, Set<String>> entry : tenantIdToConnectionNos.entrySet()) {
            
            SearchCriteria dbCriteria = SearchCriteria.builder()
                    .tenantId(entry.getKey())
                    .connectionNumber(entry.getValue())
                    .build();

            // Fetching from DB ensures we have the latest owner/connection details
            connections.addAll(sewerageDao.getSewerageConnectionList(dbCriteria, requestInfo));
        }

        // 5. Restore the order of results based on ElasticSearch relevance score
        return orderByESScore(connections, esResponse);
    }

    /**
     * Re-sorts the DB results to match the ranking (score) provided by ES.
     */
    private List<SewerageConnection> orderByESScore(List<SewerageConnection> connections, Object esResponse) {

        List<SewerageConnection> orderedConnections = new LinkedList<>();

        if (!CollectionUtils.isEmpty(connections)) {
            Map<String, SewerageConnection> idToConnectionMap = new LinkedHashMap<>();

            // Map connections by their business identifier (Connection Number)
            connections.forEach(conn -> idToConnectionMap.put(conn.getConnectionNo(), conn));

            try {
                // ES_DATA_PATH should be defined in SWConstants (usually "$.hits.hits.._source.Data")
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
                log.error("Failed to parse ES response during ordering phase for SW", e);
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

                    if (tenantId != null && connectionNo != null && !connectionNo.trim().isEmpty()) {
                        tenantIdToConnectionNos
                            .computeIfAbsent(tenantId, k -> new HashSet<>())
                            .add(connectionNo);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse ES response during ID extraction for SW", e);
            throw new CustomException("PARSING_ERROR", "Failed to extract connection data from ES response");
        }

        return tenantIdToConnectionNos;
    }

    /**
     * Ensures search parameters are valid for a fuzzy search in SW.
     */
    private void validateFuzzySearchCriteria(SearchCriteria criteria) {

        if (criteria.getTenantId() == null || criteria.getTenantId().trim().isEmpty()) {
            throw new CustomException("EG_SW_SEARCH_TENANTID_MANDATORY", "TenantId is mandatory for all search operations.");
        }

        if (criteria.getOwnerName() != null && criteria.getLocality() == null) {
            throw new CustomException("EG_SW_SEARCH_LOCALITY_MANDATORY", "Locality is mandatory when searching by Owner Name.");
        }

        if (CollectionUtils.isEmpty(criteria.getConnectionNumber()) && 
            criteria.getOldConnectionNumber() == null && 
            criteria.getOwnerName() == null && 
            criteria.getDoorNo() == null &&
            criteria.getLocality() == null
            ) {
            
            throw new CustomException("INVALID_SEARCH_CRITERIA", "Please provide at least one search parameter for Sewerage connection search.");
        }
    }
}