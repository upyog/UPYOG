package org.egov.inbox.service;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.util.PGRAiConstants;
import org.egov.inbox.web.model.InboxSearchCriteria;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PGRAiInboxFilterService {

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.searcher.pgrai.search.path}")
    private String pgraiInboxSearcherEndpoint;

    @Value("${egov.searcher.pgrai.search.desc.path}")
    private String pgraiInboxSearcherDescEndpoint;

    @Value("${egov.searcher.pgrai.count.path}")
    private String pgraiInboxSearcherCountEndpoint;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    public List<String> fetchApplicationIdsFromSearcher(InboxSearchCriteria criteria,
                                                        HashMap<String, String> statusIdNameMap, RequestInfo requestInfo) {
        List<String> applicationNumberList = new ArrayList<>();
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        Boolean isSearchResultEmpty = false;
        Boolean isMobileNumberPresent = false;
        List<String> userUUIDs = new ArrayList<>();

        if (moduleSearchCriteria.containsKey(PGRAiConstants.MOBILE_NUMBER_PARAM)) {
            isMobileNumberPresent = true;
        }
        if (isMobileNumberPresent) {
            String tenantId = criteria.getTenantId();
            String mobileNumber = String.valueOf(moduleSearchCriteria.get(PGRAiConstants.MOBILE_NUMBER_PARAM));
            userUUIDs = userService.fetchUserUUID(mobileNumber, requestInfo, tenantId);
            Boolean isUserPresentForGivenMobileNumber = !CollectionUtils.isEmpty(userUUIDs);
            isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
            if (isSearchResultEmpty) {
                return new ArrayList<>();
            }
        }

        if (!isSearchResultEmpty) {
            Object result;

            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = new HashMap<>();

            searchCriteria.put(PGRAiConstants.TENANT_ID_PARAM, criteria.getTenantId());
            searchCriteria.put(PGRAiConstants.BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

            // Accommodating module search criteria in searcher request
            if (moduleSearchCriteria.containsKey(PGRAiConstants.LOCALITY_PARAM)) {
                searchCriteria.put(PGRAiConstants.LOCALITY_PARAM, moduleSearchCriteria.get(PGRAiConstants.LOCALITY_PARAM));
            }
            if (moduleSearchCriteria.containsKey(PGRAiConstants.PGR_SERVICE_REQUEST_ID)) {
                searchCriteria.put(PGRAiConstants.PGR_SERVICE_REQUEST_ID, moduleSearchCriteria.get(PGRAiConstants.PGR_SERVICE_REQUEST_ID));
            }
            if (moduleSearchCriteria.containsKey(PGRAiConstants.ASSIGNEE_PARAM)) {
                searchCriteria.put(PGRAiConstants.ASSIGNEE_PARAM,  moduleSearchCriteria.get(PGRAiConstants.ASSIGNEE_PARAM));
            }
            if (moduleSearchCriteria.containsKey(PGRAiConstants.APPLICATION_STATUS)) {
                searchCriteria.put(PGRAiConstants.APPLICATION_STATUS, moduleSearchCriteria.get(PGRAiConstants.APPLICATION_STATUS));
            }
            if (moduleSearchCriteria.containsKey(PGRAiConstants.MOBILE_NUMBER_PARAM)) {
                searchCriteria.put(PGRAiConstants.MOBILE_NUMBER_PARAM, moduleSearchCriteria.get(PGRAiConstants.MOBILE_NUMBER_PARAM));
            }
            if (moduleSearchCriteria.containsKey(PGRAiConstants.SERVICE_CODE)) {
                searchCriteria.put(PGRAiConstants.SERVICE_CODE, moduleSearchCriteria.get(PGRAiConstants.SERVICE_CODE));
            }

            searcherRequest.put(PGRAiConstants.REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(PGRAiConstants.SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder uri = new StringBuilder();
            if (moduleSearchCriteria.containsKey(PGRAiConstants.SORT_ORDER_PARAM)
                    && moduleSearchCriteria.get(PGRAiConstants.SORT_ORDER_PARAM).equals(PGRAiConstants.DESC_PARAM)) {
                uri.append(searcherHost).append(pgraiInboxSearcherDescEndpoint);
            } else {
                uri.append(searcherHost).append(pgraiInboxSearcherEndpoint);
            }

            result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

            applicationNumberList = JsonPath.read(result, "$.ServiceRequests.*.servicerequestid");

            log.info("applicationNumberList fetched from searcher endpoint: " + applicationNumberList);
        }
        return applicationNumberList;
    }

    public Integer fetchApplicationIdsCountFromSearcher(InboxSearchCriteria criteria,
                                                        HashMap<String, String> statusIdNameMap, RequestInfo requestInfo) {
        Integer totalCount = 0;
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        List<String> userUUIDs = new ArrayList<>();

            Object result;

            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = new HashMap<>();

            searchCriteria.put(PGRAiConstants.TENANT_ID_PARAM, criteria.getTenantId());

            // Accommodating module search criteria in searcher request
            if (moduleSearchCriteria.containsKey(PGRAiConstants.LOCALITY_PARAM)) {
                searchCriteria.put(PGRAiConstants.LOCALITY_PARAM, moduleSearchCriteria.get(PGRAiConstants.LOCALITY_PARAM));
            }
            if (moduleSearchCriteria.containsKey(PGRAiConstants.PGR_SERVICE_REQUEST_ID)) {
                searchCriteria.put(PGRAiConstants.PGR_SERVICE_REQUEST_ID, moduleSearchCriteria.get(PGRAiConstants.PGR_SERVICE_REQUEST_ID));
            }
            if (moduleSearchCriteria.containsKey(PGRAiConstants.ASSIGNEE_PARAM)) {
                searchCriteria.put(PGRAiConstants.ASSIGNEE_PARAM,  moduleSearchCriteria.get(PGRAiConstants.ASSIGNEE_PARAM));
            }
            if (moduleSearchCriteria.containsKey(PGRAiConstants.APPLICATION_STATUS)) {
                searchCriteria.put(PGRAiConstants.APPLICATION_STATUS, moduleSearchCriteria.get(PGRAiConstants.APPLICATION_STATUS));
            }
            if (moduleSearchCriteria.containsKey(PGRAiConstants.MOBILE_NUMBER_PARAM)) {
                searchCriteria.put(PGRAiConstants.MOBILE_NUMBER_PARAM, moduleSearchCriteria.get(PGRAiConstants.MOBILE_NUMBER_PARAM));
            }
            if (moduleSearchCriteria.containsKey(PGRAiConstants.SERVICE_CODE)) {
                searchCriteria.put(PGRAiConstants.SERVICE_CODE, moduleSearchCriteria.get(PGRAiConstants.SERVICE_CODE));
            }


            searcherRequest.put(PGRAiConstants.REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(PGRAiConstants.SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder uri = new StringBuilder();
            uri.append(searcherHost).append(pgraiInboxSearcherCountEndpoint);

            result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

            double count = JsonPath.read(result, "$.TotalCount[0].count");
            totalCount = (int) count;

        return totalCount;
    }
}