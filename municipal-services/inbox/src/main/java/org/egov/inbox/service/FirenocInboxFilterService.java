package org.egov.inbox.service;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.egov.inbox.util.CommonConstants.*;

/**
 * Service for filtering FireNOC inbox applications.
 *
 * <p>Provides methods to fetch application numbers and counts from the
 * searcher service based on inbox search criteria and workflow status mappings.
 */
@Slf4j
@Service
public class FirenocInboxFilterService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private InboxConfiguration config;

    /**
     * Fetches FireNOC application numbers from the searcher service
     * based on the provided inbox search criteria.
     *
     * <p>Builds the search request, invokes the searcher API, and extracts
     * matching application numbers from the response. Supports ascending and
     * descending sort order via {@code SORT_ORDER_PARAM}.
     *
     * @param criteria        the inbox search criteria containing tenant, offset, limit, and module filters
     * @param StatusIdNameMap mapping of workflow status IDs to status names; may be {@code null}
     * @param requestInfo     the request metadata
     * @return list of matching application numbers; empty list if none found or an error occurs
     */
    public List<String> fetchApplicationNumbersFromSearcher(InboxSearchCriteria criteria,
                                                            HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
        List<String> applicationNumbers = new ArrayList<>();
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();

        Map<String, Object> searcherRequest = buildSearcherRequest(criteria, StatusIdNameMap, requestInfo);
        Map<String, Object> searchCriteria = (Map<String, Object>) searcherRequest.get(SEARCH_CRITERIA_PARAM);

        searchCriteria.put(OFFSET_PARAM, criteria.getOffset());
        searchCriteria.put(NO_OF_RECORDS_PARAM, criteria.getLimit());
        moduleSearchCriteria.put(LIMIT_PARAM, criteria.getLimit());

        StringBuilder uri = new StringBuilder();
        if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
                && moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
            uri.append(config.getSearcherHost()).append(config.getFirenocInboxSearcherDescEndpoint());
        } else {
            uri.append(config.getSearcherHost()).append(config.getFirenocInboxSearcherEndpoint());
        }

        log.info("Checking FireNoc searcherRequest: {}", searcherRequest);
        Object result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

        try {
            if (result != null) {
                applicationNumbers = JsonPath.read(result, "$.FireNOCs[*].applicationnumber");
                log.info("FireNoc Application Numbers: {}", applicationNumbers);
            }
        } catch (Exception e) {
            log.error("Error while parsing FireNoc searcher results", e);
        }

        return applicationNumbers;
    }

    /**
     * Fetches the total count of FireNOC applications matching the
     * provided inbox search criteria.
     *
     * <p>Builds the search request, invokes the searcher count API, and extracts
     * the total number of matching applications from the response.
     *
     * @param criteria        the inbox search criteria containing tenant and module filters
     * @param StatusIdNameMap mapping of workflow status IDs to status names; may be {@code null}
     * @param requestInfo     the request metadata
     * @return total number of matching applications; {@code 0} if none found or an error occurs
     */
    public Integer fetchApplicationCountFromSearcher(InboxSearchCriteria criteria,
                                                     HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
        Integer totalCount = 0;

        Map<String, Object> searcherRequest = buildSearcherRequest(criteria, StatusIdNameMap, requestInfo);

        StringBuilder uri = new StringBuilder();
        uri.append(config.getSearcherHost()).append(config.getFirenocInboxSearcherCountEndpoint());

        log.info("Checking FireNoc count searcherRequest: {}", searcherRequest);
        Object result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

        try {
            if (result != null) {
                Number count = JsonPath.read(result, "$.TotalCount[0].count");
                totalCount = (count != null) ? count.intValue() : 0;
            }
        } catch (Exception e) {
            log.error("Error while parsing FireNoc count results", e);
        }

        return totalCount;
    }

    /**
     * Builds the common searcher request map shared by both search and count queries.
     *
     * <p>Populates tenant ID, business service, optional assignee, status filters
     * (falling back to all statuses from {@code StatusIdNameMap} when none are specified),
     * and the application number filter if present in the module search criteria.
     *
     * @param criteria        the inbox search criteria
     * @param StatusIdNameMap mapping of workflow status IDs to status names; may be {@code null}
     * @param requestInfo     the request metadata
     * @return a map containing {@code RequestInfo} and {@code SearchCriteria} ready for the searcher API
     */
    private Map<String, Object> buildSearcherRequest(InboxSearchCriteria criteria,
                                                     HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();

        Map<String, Object> searchCriteria = new HashMap<>();
        searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
        searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

        if (moduleSearchCriteria.containsKey("applicationNumber")) {
            searchCriteria.put("applicationNumber", moduleSearchCriteria.get("applicationNumber"));
        }

        if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
            searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
        }

        if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
            searchCriteria.put(STATUS_PARAM, processCriteria.getStatus());
        } else {
            if (StatusIdNameMap != null && !StatusIdNameMap.isEmpty()
                    && CollectionUtils.isEmpty(processCriteria.getStatus())) {
                searchCriteria.put(STATUS_PARAM, StatusIdNameMap.keySet());
            }
        }

        Map<String, Object> searcherRequest = new HashMap<>();
        searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
        searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

        return searcherRequest;
    }
}
