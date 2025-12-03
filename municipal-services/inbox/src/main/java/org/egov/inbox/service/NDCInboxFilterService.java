package org.egov.inbox.service;

import java.util.*;

import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.repository.ServiceRequestRepository;
import org.egov.inbox.util.NdcConstants;
import org.egov.inbox.web.model.InboxSearchCriteria;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

import static org.egov.inbox.util.NdcConstants.*;

@Slf4j
@Service
public class NDCInboxFilterService {

    @Value("${egov.user.host}")
    private String userHost;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.searcher.ndc.search.path}")
    private String ndcInboxSearcherEndpoint;

    @Value("${egov.searcher.ndc.search.desc.path}")
    private String ndcInboxSearcherDescEndpoint;

    @Value("${egov.searcher.ndc.count.path}")
    private String ndcInboxSearcherCountEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    public List<String> fetchApplicationNumbersFromSearcher(InboxSearchCriteria criteria,
            HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
        List<String> applicationNumbers = new ArrayList<>();
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        Boolean isSearchResultEmpty = false;
        Boolean isMobileNumberPresent = false;
        List<String> userUUIDs = new ArrayList<>();
        if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)) {
            isMobileNumberPresent = true;
        }
        if (isMobileNumberPresent) {
            String tenantId = criteria.getTenantId();
            String mobileNumber = String.valueOf(moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
            userUUIDs = fetchUserUUID(mobileNumber, requestInfo, tenantId);
            Boolean isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;
            isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
            if (isSearchResultEmpty) {
                return new ArrayList<>();
            }
        }

        if (!isSearchResultEmpty) {
            Object result = null;

            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = getSearchCriteria(criteria, StatusIdNameMap, requestInfo,
                    moduleSearchCriteria, processCriteria, userUUIDs);
            // Paginating searcher results
            searchCriteria.put(OFFSET_PARAM, criteria.getOffset());

                searchCriteria.put(NO_OF_RECORDS_PARAM, criteria.getLimit());

            moduleSearchCriteria.put(LIMIT_PARAM, criteria.getLimit());

            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder uri = new StringBuilder();

            if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
                    && moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM))
                uri.append(searcherHost).append(ndcInboxSearcherDescEndpoint);
            else
                uri.append(searcherHost).append(ndcInboxSearcherEndpoint);

            result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

            List<String> citizenApplicationsNumbers = JsonPath.read(result, "$.Applications.*.applicationno");

            applicationNumbers.addAll(citizenApplicationsNumbers);

        }
        return applicationNumbers;
    }

    private Map<String, Object> getSearchCriteria(InboxSearchCriteria criteria, HashMap<String, String> StatusIdNameMap,
            RequestInfo requestInfo, HashMap<String, Object> moduleSearchCriteria,
            ProcessInstanceSearchCriteria processCriteria, List<String> userUUIDs) {
        Map<String, Object> searchCriteria = new HashMap<>();

        searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
        searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());


        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)
                && !CollectionUtils.isEmpty(userUUIDs)) {
            searchCriteria.put(USERID_PARAM, userUUIDs);
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
            searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(NDC_UUID_PARAM)) {
            searchCriteria.put(NDC_UUID_PARAM, moduleSearchCriteria.get(NDC_UUID_PARAM));
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(NDC_APPLICATION_NO_PARAM)) {
            searchCriteria.put(NDC_APPLICATION_NO_PARAM, moduleSearchCriteria.get(NDC_APPLICATION_NO_PARAM));
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(NDC_SOURCE_REF_ID_PARAM)) {
            searchCriteria.put(NDC_SOURCE_APPLICATION_NUMBER_PARAM, moduleSearchCriteria.get(NDC_SOURCE_REF_ID_PARAM));
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(NdcConstants.WF_STATUS)) {
            searchCriteria.put(NdcConstants.WF_STATUS, moduleSearchCriteria.get(NdcConstants.WF_STATUS));
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(STATUS_PARAM)) {
            searchCriteria.put(STATUS_PARAM, StatusIdNameMap.values());
        }
        // Accommodating process search criteria in searcher request
        if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
            searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
        }
        if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
            searchCriteria.put(STATUS_PARAM, processCriteria.getStatus());
        }
//        else {
//            if (StatusIdNameMap != null && StatusIdNameMap.values().size() > 0) {
//                if (CollectionUtils.isEmpty(processCriteria.getStatus())) {
//                    searchCriteria.put(STATUS_PARAM, StatusIdNameMap.keySet());
//                }
//            }
//        }
        return searchCriteria;
    }

    public Integer fetchApplicationCountFromSearcher(InboxSearchCriteria criteria,
            HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
        Integer totalCount = 0;
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        Boolean isSearchResultEmpty = false;
        Boolean isMobileNumberPresent = false;
        List<String> userUUIDs = new ArrayList<>();
        if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)) {
            isMobileNumberPresent = true;
        }
        if (isMobileNumberPresent) {
            String tenantId = criteria.getTenantId();
            String mobileNumber = String.valueOf(moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
            userUUIDs = fetchUserUUID(mobileNumber, requestInfo, tenantId);
            Boolean isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;
            isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
            if (isSearchResultEmpty) {
                return 0;
            }
        }

        if (!isSearchResultEmpty) {
            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = getSearchCriteria(criteria, StatusIdNameMap, requestInfo,
                    moduleSearchCriteria, processCriteria, userUUIDs);
            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);
            StringBuilder citizenUri = new StringBuilder();
            citizenUri.append(searcherHost).append(ndcInboxSearcherCountEndpoint);

            Object result = restTemplate.postForObject(citizenUri.toString(), searcherRequest, Map.class);

            double citizenCount = JsonPath.read(result, "$.TotalCount[0].count");
            totalCount = totalCount + (int) citizenCount;
        }
        return totalCount;
    }

    private List<String> fetchUserUUID(String mobileNumber, RequestInfo requestInfo, String tenantId) {
        StringBuilder uri = new StringBuilder();
        List<String> uuids = new ArrayList<>();
        uri.append(userHost).append(userSearchEndpoint);
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("RequestInfo", requestInfo);
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("userType", CITIZEN);
        userSearchRequest.put("mobileNumber", mobileNumber);
        try {
            Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
            if (null != user) {
                // log.info(user.toString());
                uuids.addAll(JsonPath.read(user, "$.user.*.uuid"));
            } else {
                log.error("Service returned null while fetching user for mobile number - " + mobileNumber);
            }
        } catch (Exception e) {
            log.error("Exception while fetching user for mobile number - " + mobileNumber);
            log.error("Exception trace: ", e);
        }
        return uuids;
    }
}
