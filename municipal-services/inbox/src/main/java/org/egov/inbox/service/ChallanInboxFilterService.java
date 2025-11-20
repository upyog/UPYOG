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
public class ChallanInboxFilterService {

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.searcher.challan.search.path:}")
    private String challanInboxSearcherEndpoint;

    @Value("${egov.searcher.challan.search.desc.path:}")
    private String challanInboxSearcherDescEndpoint;

    @Value("${egov.searcher.challan.count.path:}")
    private String challanInboxSearcherCountEndpoint;

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

        // Fetch challan numbers from the response
        List<String> applicationNumbers = JsonPath.read(result, "$.challanApplication.[*].challanno");

        return applicationNumbers == null ? new ArrayList<>() : applicationNumbers;
    }

    /**
     * Resolve search endpoint based on sort order
     */
    private String resolveSearchEndpoint(HashMap<String, Object> moduleSearchCriteria) {
        StringBuilder uri = new StringBuilder();

        if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
                && Objects.equals(moduleSearchCriteria.get(SORT_ORDER_PARAM), DESC_PARAM)
                && challanInboxSearcherDescEndpoint != null && !challanInboxSearcherDescEndpoint.isEmpty()) {

            uri.append(searcherHost).append(challanInboxSearcherDescEndpoint);

        } else if (challanInboxSearcherEndpoint != null && !challanInboxSearcherEndpoint.isEmpty()) {
            uri.append(searcherHost).append(challanInboxSearcherEndpoint);

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

        // Challan Number
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("challanNumber")) {
            searchCriteria.put("challanNumber", moduleSearchCriteria.get("challanNumber"));
        }

        // Challan ID/UUID
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("id")) {
            searchCriteria.put("id", moduleSearchCriteria.get("id"));
        }

        // Account ID
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("accountId")) {
            searchCriteria.put("accountId", moduleSearchCriteria.get("accountId"));
        }

        // Reference ID
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("referenceId")) {
            searchCriteria.put("referenceId", moduleSearchCriteria.get("referenceId"));
        }

        // Receipt Number
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("receiptNumber")) {
            searchCriteria.put("receiptNumber", moduleSearchCriteria.get("receiptNumber"));
        }

//        // Challan Status
//        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("challanStatus")) {
//            searchCriteria.put("challanStatus", moduleSearchCriteria.get("challanStatus"));
//        }

        // Offence Type Name
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("offenceTypeName")) {
            searchCriteria.put("offenceTypeName", moduleSearchCriteria.get("offenceTypeName"));
        }

        // Offence Category Name
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("offenceCategoryName")) {
            searchCriteria.put("offenceCategoryName", moduleSearchCriteria.get("offenceCategoryName"));
        }

        // Offence Subcategory Name
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("offenceSubcategoryName")) {
            searchCriteria.put("offenceSubcategoryName", moduleSearchCriteria.get("offenceSubcategoryName"));
        }

        // Locality
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
            searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
        }

        // Citizen UUID
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("citizenUuid")) {
            searchCriteria.put("citizenUuid", moduleSearchCriteria.get("citizenUuid"));
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

        if (challanInboxSearcherCountEndpoint != null && !challanInboxSearcherCountEndpoint.isEmpty()) {
            HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
            ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();

            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = getSearchCriteria(criteria, statusIdNameMap,
                    moduleSearchCriteria, processCriteria);

            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder uri = new StringBuilder();
            uri.append(searcherHost).append(challanInboxSearcherCountEndpoint);

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