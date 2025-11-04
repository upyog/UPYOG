package org.egov.inbox.service;

import com.jayway.jsonpath.JsonPath;
import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.web.model.InboxSearchCriteria;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static org.egov.inbox.util.NdcConstants.*;
import static org.egov.inbox.util.TLConstants.STATUS_PARAM;

@Service
public class LayoutInboxFilterService {

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.searcher.layout.search.path:}")
    private String layoutInboxSearcherEndpoint;

    @Value("${egov.searcher.layout.search.desc.path:}")
    private String layoutInboxSearcherDescEndpoint;

    @Value("${egov.searcher.layout.count.path:}")
    private String layoutInboxSearcherCountEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Fetch application numbers from searcher
     */
    public List<String> fetchApplicationNumbersFromSearcher(InboxSearchCriteria criteria,
                                                            HashMap<String, String> statusIdNameMap,
                                                            RequestInfo requestInfo) {

        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();

        Map<String, Object> searcherRequest = new HashMap<>();
        Map<String, Object> searchCriteria = getSearchCriteria(criteria, statusIdNameMap,
                moduleSearchCriteria, processCriteria);

        // Pagination
        searchCriteria.put(OFFSET_PARAM, criteria.getOffset());

        if (!moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)) {
            searchCriteria.put(NO_OF_RECORDS_PARAM, criteria.getLimit());
        }

        moduleSearchCriteria.put(LIMIT_PARAM, criteria.getLimit());

        searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
        searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

        String endpoint = resolveSearchEndpoint(moduleSearchCriteria);
        if (endpoint == null || endpoint.isEmpty()) {
            return Collections.emptyList();
        }

        Object result = restTemplate.postForObject(endpoint, searcherRequest, Map.class);

        // Fetch application numbers from the response
        List<String> applicationNumbers = JsonPath.read(result, "$.layoutApplication.[*].applicationno");

        return applicationNumbers == null ? new ArrayList<>() : applicationNumbers;
    }

    /**
     * Resolve search endpoint based on sort order
     */
    private String resolveSearchEndpoint(HashMap<String, Object> moduleSearchCriteria) {
        StringBuilder uri = new StringBuilder();

        if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
                && Objects.equals(moduleSearchCriteria.get(SORT_ORDER_PARAM), DESC_PARAM)
                && layoutInboxSearcherDescEndpoint != null && !layoutInboxSearcherDescEndpoint.isEmpty()) {

            uri.append(searcherHost).append(layoutInboxSearcherDescEndpoint);

        } else if (layoutInboxSearcherEndpoint != null && !layoutInboxSearcherEndpoint.isEmpty()) {
            uri.append(searcherHost).append(layoutInboxSearcherEndpoint);

        } else {
            return null;
        }

        return uri.toString();
    }

    /**
     * Build search criteria map
     */
    private Map<String, Object> getSearchCriteria(InboxSearchCriteria criteria,
                                                  HashMap<String, String> statusIdNameMap,
                                                  HashMap<String, Object> moduleSearchCriteria,
                                                  ProcessInstanceSearchCriteria processCriteria) {

        Map<String, Object> searchCriteria = new HashMap<>();

        // Mandatory parameters
        searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
        searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

        // Application Number
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("applicationNumber")) {
            searchCriteria.put("applicationNumber", moduleSearchCriteria.get("applicationNumber"));
        }

        // Layout Number
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("layoutNumber")) {
            searchCriteria.put("layoutNumber", moduleSearchCriteria.get("layoutNumber"));
        }

        // Layout ID/UUID
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("id")) {
            searchCriteria.put("id", moduleSearchCriteria.get("id"));
        }

        // Account ID
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("accountId")) {
            searchCriteria.put("accountId", moduleSearchCriteria.get("accountId"));
        }

        // Application Status
//        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("applicationStatus")) {
//            searchCriteria.put("applicationStatus", moduleSearchCriteria.get("applicationStatus"));
//        }

        // Application Type
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("applicationType")) {
            searchCriteria.put("applicationType", moduleSearchCriteria.get("applicationType"));
        }

        // Layout Type
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("layoutType")) {
            searchCriteria.put("layoutType", moduleSearchCriteria.get("layoutType"));
        }

        // Owner Type
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("ownerType")) {
            searchCriteria.put("ownerType", moduleSearchCriteria.get("ownerType"));
        }

        // Assignee from workflow
        if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
            searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
        }

        // Workflow Status - accommodating processSearchCriteria in the search_criteria
        if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
            List<String> matchingIds = statusIdNameMap.entrySet().stream()
                    .filter(entry -> processCriteria.getStatus().contains(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            searchCriteria.put(STATUS_PARAM, matchingIds);
        } else {
            if (statusIdNameMap.values().size() > 0) {
                if (CollectionUtils.isEmpty(processCriteria.getStatus())) {
                    searchCriteria.put(STATUS_PARAM, statusIdNameMap.keySet());
                }
            }
        }

        return searchCriteria;
    }

    /**
     * Fetch application count
     */
    public Integer fetchApplicationCountFromSearcher(InboxSearchCriteria criteria,
                                                     HashMap<String, String> statusIdNameMap,
                                                     RequestInfo requestInfo) {

        if (layoutInboxSearcherCountEndpoint != null && !layoutInboxSearcherCountEndpoint.isEmpty()) {
            HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
            ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();

            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = getSearchCriteria(criteria, statusIdNameMap,
                    moduleSearchCriteria, processCriteria);

            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder uri = new StringBuilder();
            uri.append(searcherHost).append(layoutInboxSearcherCountEndpoint);

            Object result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

            // Common count path
            Double count = JsonPath.read(result, "$.totalCount[0].totalcount");

            return count == null ? 0 : count.intValue();

        } else {
            List<String> apps = fetchApplicationNumbersFromSearcher(criteria, statusIdNameMap, requestInfo);
            return apps.size();
        }
    }
}