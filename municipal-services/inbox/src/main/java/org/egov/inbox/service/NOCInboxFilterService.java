package org.egov.inbox.service;

import java.util.*;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.web.model.InboxSearchCriteria;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import static org.egov.inbox.util.NdcConstants.*;

@Service
public class NOCInboxFilterService {

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.searcher.noc.search.path:}")
    private String nocInboxSearcherEndpoint;

    @Value("${egov.searcher.noc.search.desc.path:}")
    private String nocInboxSearcherDescEndpoint;

    @Value("${egov.searcher.noc.count.path:}")
    private String nocInboxSearcherCountEndpoint;

    @Autowired
    private RestTemplate restTemplate;


    public List<String> fetchApplicationNumbersFromSearcher(InboxSearchCriteria criteria,
            HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();

        Map<String, Object> searcherRequest = new HashMap<>();
        Map<String, Object> searchCriteria = getSearchCriteria(criteria, StatusIdNameMap, moduleSearchCriteria, processCriteria);
        // Paginating searcher results
        searchCriteria.put(OFFSET_PARAM, criteria.getOffset());
        if(!moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM))
        {
            searchCriteria.put(NO_OF_RECORDS_PARAM, criteria.getLimit());
        }
        moduleSearchCriteria.put(LIMIT_PARAM, criteria.getLimit());

        searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
        searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

        StringBuilder uri = new StringBuilder();
        if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
                && Objects.equals(moduleSearchCriteria.get(SORT_ORDER_PARAM), DESC_PARAM)
                && nocInboxSearcherDescEndpoint != null && !nocInboxSearcherDescEndpoint.isEmpty()) {
            uri.append(searcherHost).append(nocInboxSearcherDescEndpoint);
        } else {
            uri.append(searcherHost).append(nocInboxSearcherEndpoint);
        }

        Object result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

        List<String> applicationNumbers = new ArrayList<>();
        try {
            applicationNumbers = JsonPath.read(result, "$.Noc.[*].applicationno");
        } catch (Exception e1) {
            try {
                applicationNumbers = JsonPath.read(result, "$..applicationNo");
            } catch (Exception e2) {
                try {
                    applicationNumbers = JsonPath.read(result, "$..applicationNumber");
                } catch (Exception e3) {
                    applicationNumbers = new ArrayList<>();
                }
            }
        }

        return applicationNumbers == null ? new ArrayList<>() : applicationNumbers;
    }

    private Map<String, Object> getSearchCriteria(InboxSearchCriteria criteria, HashMap<String, String> StatusIdNameMap,
            HashMap<String, Object> moduleSearchCriteria, ProcessInstanceSearchCriteria processCriteria) {
        Map<String, Object> searchCriteria = new HashMap<>();

        searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
        searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
            searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
        }

        // Map status display names to WF UUIDs for searcher
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("status") && moduleSearchCriteria.get("status") != null) {
            List<String> requestedStatuses = Arrays.asList(moduleSearchCriteria.get("status").toString().split(","));
            List<String> matchingIds = StatusIdNameMap.entrySet().stream()
                    .filter(entry -> requestedStatuses.contains(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (!matchingIds.isEmpty()) {
                searchCriteria.put("status", matchingIds);
            }
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("applicationNo")) {
            searchCriteria.put("applicationNo", moduleSearchCriteria.get("applicationNo"));
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("uuid")) {
            searchCriteria.put("id", moduleSearchCriteria.get("uuid"));
        }
        if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
            searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
        }
        return searchCriteria;
    }

    public Integer fetchApplicationCountFromSearcher(InboxSearchCriteria criteria,
            HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo){
        if (nocInboxSearcherCountEndpoint != null && !nocInboxSearcherCountEndpoint.isEmpty()) {
            HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
            ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = getSearchCriteria(criteria, StatusIdNameMap, moduleSearchCriteria, processCriteria);
            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);
            StringBuilder citizenUri = new StringBuilder();
            citizenUri.append(searcherHost).append(nocInboxSearcherCountEndpoint);
            Object result = restTemplate.postForObject(citizenUri.toString(), searcherRequest, Map.class);
            // Try common count response shapes
            try {
                Double count = JsonPath.read(result, "$.totalCount[0].totalcount");
                return count == null ? 0 : count.intValue();
            } catch (Exception e1) {
                try {
                    Double count = JsonPath.read(result, "$.TotalCount[0].count");
                    return count == null ? 0 : count.intValue();
                } catch (Exception e2) {
                    return 0;
                }
            }
        } else {
            List<String> apps = fetchApplicationNumbersFromSearcher(criteria, StatusIdNameMap, requestInfo);
            return apps.size();
        }
    }
}
