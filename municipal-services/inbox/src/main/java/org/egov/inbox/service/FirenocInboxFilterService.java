package org.egov.inbox.service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.config.InboxConfiguration;
import org.egov.inbox.repository.ServiceRequestRepository;
import org.egov.inbox.web.model.InboxSearchCriteria;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.egov.inbox.util.CommonConstants.*;

@Slf4j
@Service
public class FirenocInboxFilterService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private InboxConfiguration config;

    /**
     * Fetches Garbage Collection application numbers from the searcher service
     * based on the provided inbox search criteria.
     *
     * <p>Builds the search request, invokes the searcher API, and extracts
     * the matching application numbers from the response.
     *
     * @param criteria the inbox search criteria
     * @param StatusIdNameMap mapping of workflow status IDs to status names
     * @param requestInfo the request information
     * @return a list of matching application numbers; returns an empty list if
     *         no applications are found or an error occurs
     */
    public List<String> fetchApplicationNumbersFromSearcher(InboxSearchCriteria criteria,
                                                            HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
        List<String> applicationNumbers = new ArrayList<>();
        HashMap moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        Boolean isSearchResultEmpty = false;

        if (!isSearchResultEmpty) {
            Object result = null;

            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = new HashMap<>();

            searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
            searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

            // Accommodate module search criteria in searcher request
            if (moduleSearchCriteria.containsKey("applicationNumber")) {
                searchCriteria.put("applicationNumber", moduleSearchCriteria.get("applicationNumber"));
            }

            // Accommodate process search criteria in searcher request
            if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
                searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
            }
            if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
                searchCriteria.put(STATUS_PARAM, processCriteria.getStatus());
            } else {
                if (StatusIdNameMap.values().size() > 0) {
                    if (CollectionUtils.isEmpty(processCriteria.getStatus())) {
                        searchCriteria.put(STATUS_PARAM, StatusIdNameMap.keySet());
                    }
                }
            }

            // Paginating searcher results
            searchCriteria.put(OFFSET_PARAM, criteria.getOffset());
            searchCriteria.put(NO_OF_RECORDS_PARAM, criteria.getLimit());
            moduleSearchCriteria.put(LIMIT_PARAM, criteria.getLimit());

            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder uri = new StringBuilder();
            if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
                    && moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
                uri.append(config.getSearcherHost()).append(config.getFirenocInboxSearcherDescEndpoint());
            } else {
                uri.append(config.getSearcherHost()).append(config.getFirenocInboxSearcherEndpoint());
            }
            log.info("Checking FireNoc searcherRequest: " + searcherRequest);
            result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

            String jsonString = null;
            try {
                jsonString = mapper.writeValueAsString(result);
                applicationNumbers = JsonPath.read(jsonString, "$.FireNOCs[*].applicationnumber");
                log.info("FireNoc Application Numbers: " + applicationNumbers);
            } catch (Exception e) {
                log.error("Error while parsing FireNoc searcher results", e);
            }
        }
        return applicationNumbers;
    }

    /**
     * Fetches the total count of Garbage Collection applications matching the
     * provided inbox search criteria.
     *
     * <p>Builds the search request, invokes the searcher count API, and extracts
     * the total number of matching applications from the response.
     *
     * @param criteria the inbox search criteria
     * @param StatusIdNameMap mapping of workflow status IDs to status names
     * @param requestInfo the request information
     * @return the total number of matching applications; returns {@code 0} if
     *         no records are found or an error occurs
     */
    public Integer fetchApplicationCountFromSearcher(InboxSearchCriteria criteria,
                                                     HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
        Integer totalCount = 0;
        HashMap moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        Boolean isSearchResultEmpty = false;

        if (!isSearchResultEmpty) {
            Object result = null;

            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = new HashMap<>();

            searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
            searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

            // Accommodate module search criteria in searcher request
            if (moduleSearchCriteria.containsKey("applicationNumber")) {
                searchCriteria.put("applicationNumber", moduleSearchCriteria.get("applicationNumber"));
            }

            // Accommodate process search criteria in searcher request
            if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
                searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
            }
            if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
                searchCriteria.put(STATUS_PARAM, processCriteria.getStatus());
            } else {
                if (StatusIdNameMap.values().size() > 0) {
                    if (CollectionUtils.isEmpty(processCriteria.getStatus())) {
                        searchCriteria.put(STATUS_PARAM, StatusIdNameMap.keySet());
                    }
                }
            }

            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder uri = new StringBuilder();
            uri.append(config.getSearcherHost()).append(config.getFirenocInboxSearcherCountEndpoint());

            log.info("Checking FireNoc count searcherRequest: " + searcherRequest);
            result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

            try {
                String jsonString = mapper.writeValueAsString(result);
                double count = JsonPath.read(jsonString, "$.TotalCount[0].count");
                totalCount = (int) count;
            } catch (Exception e) {
                log.error("Error while parsing FireNoc count results", e);
            }
        }
        return totalCount;
    }
}
