package org.egov.inbox.service;

import java.util.*;

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
public class ADVInboxFilterService {

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.searcher.adv.search.path:}")
    private String advInboxSearcherEndpoint;

    @Value("${egov.searcher.adv.search.desc.path:}")
    private String advInboxSearcherDescEndpoint;

    @Value("${egov.searcher.adv.count.path:}")
    private String advInboxSearcherCountEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    // ServiceRequestRepository not required here; RestTemplate is used directly

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

        String endpoint = resolveSearchEndpoint(moduleSearchCriteria);
        if (endpoint == null || endpoint.isEmpty()) return Collections.emptyList();

        Object result = restTemplate.postForObject(endpoint, searcherRequest, Map.class);

        // Try common fields in likely order: applicationNumber, bookingNo, uuid
        List<String> applicationNumbers = JsonPath.read(result,"$.bookingApplication.[*].booking_no");

        return applicationNumbers == null ? new ArrayList<>() : applicationNumbers;
    }

    private String resolveSearchEndpoint(HashMap<String, Object> moduleSearchCriteria) {
        StringBuilder uri = new StringBuilder();
        if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
                && Objects.equals(moduleSearchCriteria.get(SORT_ORDER_PARAM), DESC_PARAM)
                && advInboxSearcherDescEndpoint != null && !advInboxSearcherDescEndpoint.isEmpty()) {
            uri.append(searcherHost).append(advInboxSearcherDescEndpoint);
        } else if (advInboxSearcherEndpoint != null && !advInboxSearcherEndpoint.isEmpty()){
            uri.append(searcherHost).append(advInboxSearcherEndpoint);
        } else {
            return null;
        }
        return uri.toString();
    }

    private List<String> tryJsonPaths(Object result, List<String> jsonPaths) {
        for (String path : jsonPaths) {
            try {
                List<String> values = JsonPath.read(result, path);
                if (values != null && !values.isEmpty()) return values;
            } catch (Exception ignore) { }
        }
        return Collections.emptyList();
    }

    private Map<String, Object> getSearchCriteria(InboxSearchCriteria criteria, HashMap<String, String> StatusIdNameMap,
            HashMap<String, Object> moduleSearchCriteria, ProcessInstanceSearchCriteria processCriteria) {
        Map<String, Object> searchCriteria = new HashMap<>();

        searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
        searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
            searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
        }
        // If FE sent human-readable status names, translate to UUIDs
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("status")) {
            Object statusObj = moduleSearchCriteria.get("status");
            List<String> requestedStatuses;
            if (statusObj instanceof Collection) {
                requestedStatuses = new ArrayList<>();
                for (Object o : (Collection<?>) statusObj) requestedStatuses.add(String.valueOf(o));
            } else {
                requestedStatuses = Arrays.asList(moduleSearchCriteria.get("status").toString().split(","));
            }

            List<String> matchingIds = new ArrayList<>();
            for (Map.Entry<String, String> e : StatusIdNameMap.entrySet()) {
                if (requestedStatuses.contains(e.getValue())) matchingIds.add(e.getKey());
            }
            if (!matchingIds.isEmpty()) {
                searchCriteria.put("status", matchingIds);
            }
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("applicationNumber")) {
            searchCriteria.put("applicationNumber", moduleSearchCriteria.get("applicationNumber"));
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
        if (advInboxSearcherCountEndpoint != null && !advInboxSearcherCountEndpoint.isEmpty()) {
            HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
            ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = getSearchCriteria(criteria, StatusIdNameMap, moduleSearchCriteria, processCriteria);
            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);
            StringBuilder citizenUri = new StringBuilder();
            citizenUri.append(searcherHost).append(advInboxSearcherCountEndpoint);
            Object result = restTemplate.postForObject(citizenUri.toString(), searcherRequest, Map.class);
            // Try common count paths
            Double count = JsonPath.read(result, "$.totalCount[0].totalcount");
            return count == null ? 0 : count.intValue();
        } else {
            List<String> apps = fetchApplicationNumbersFromSearcher(criteria, StatusIdNameMap, requestInfo);
            return apps.size();
        }
    }
}
