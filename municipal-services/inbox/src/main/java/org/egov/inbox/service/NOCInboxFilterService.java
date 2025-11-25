package org.egov.inbox.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.inbox.repository.ServiceRequestRepository;
import org.egov.inbox.web.model.InboxSearchCriteria;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import static org.egov.inbox.util.BpaConstants.*;


@Service
@Slf4j
public class NOCInboxFilterService {

    @Value("${egov.user.host}")
    private String userHost;
    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.searcher.noc.search.path:}")
    private String nocInboxSearcherEndpoint;

    @Value("${egov.searcher.noc.search.desc.path:}")
    private String nocInboxSearcherDescEndpoint;

    @Value("${egov.searcher.noc.count.path:}")
    private String nocInboxSearcherCountEndpoint;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private RestTemplate restTemplate;


    public List<String> fetchApplicationNumbersFromSearcher(InboxSearchCriteria criteria,
            HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        Boolean isSearchResultEmpty = false;
        Boolean isMobileNumberPresent = false;
        List<String> userUUIDs = new ArrayList<>();
        List<String> citizenRoles = Collections.emptyList();
        if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)) {
            isMobileNumberPresent = true;
        }
        if (isMobileNumberPresent) {
            String tenantId = criteria.getTenantId();
            String mobileNumber = String.valueOf(moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
            Map<String, List<String>> userDetails = fetchUserUUID(mobileNumber, requestInfo, tenantId);
            userUUIDs = userDetails.get(USER_UUID);
            citizenRoles = userDetails.get(USER_ROLES);
            Boolean isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;
            isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
            if (isSearchResultEmpty) {
                return new ArrayList<>();
            }
        } else {
            List<String> roles = requestInfo.getUserInfo().getRoles().stream().map(Role::getCode).collect(Collectors.toList());
            if(roles.contains(CITIZEN)) {
                userUUIDs.add(requestInfo.getUserInfo().getUuid());
                citizenRoles = roles;
            }
        }
        Map<String, Object> searcherRequest = new HashMap<>();
        Map<String, Object> searchCriteria = getSearchCriteria(criteria, StatusIdNameMap, moduleSearchCriteria, processCriteria,userUUIDs, citizenRoles);
        // Paginating searcher results
        searchCriteria.put(OFFSET_PARAM, criteria.getOffset());

            searchCriteria.put(NO_OF_RECORDS_PARAM, criteria.getLimit());

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
            HashMap<String, Object> moduleSearchCriteria, ProcessInstanceSearchCriteria processCriteria, List<String> userUUIDs, List<String> userRoles) {
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
        else {
			// If no status filter is provided, include all statuses
			searchCriteria.put("status", new ArrayList<>(StatusIdNameMap.keySet()));
		}
        if (moduleSearchCriteria != null && (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) || userRoles.contains(CITIZEN))
                && !CollectionUtils.isEmpty(userUUIDs)) {
            searchCriteria.put(USERID_PARAM, userUUIDs);
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
            Boolean isSearchResultEmpty = false;
            Boolean isMobileNumberPresent = false;
            List<String> userUUIDs = new ArrayList<>();
            List<String> citizenRoles = Collections.emptyList();
            if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)) {
                isMobileNumberPresent = true;
            }
            if (isMobileNumberPresent) {
                String tenantId = criteria.getTenantId();
                String mobileNumber = String.valueOf(moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
                Map<String, List<String>> userDetails = fetchUserUUID(mobileNumber, requestInfo, tenantId);
                userUUIDs = userDetails.get(USER_UUID);
                citizenRoles = userDetails.get(USER_ROLES);
                Boolean isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;
                isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
                if (isSearchResultEmpty) {
                    return 0;
                }
            } else {
                List<String> roles = requestInfo.getUserInfo().getRoles().stream().map(Role::getCode).collect(Collectors.toList());
                if(roles.contains(CITIZEN)) {
                    userUUIDs.add(requestInfo.getUserInfo().getUuid());
                    citizenRoles = roles;
                }
            }
            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = getSearchCriteria(criteria, StatusIdNameMap, moduleSearchCriteria, processCriteria,userUUIDs,citizenRoles);
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

    private Map<String, List<String>> fetchUserUUID(String mobileNumber, RequestInfo requestInfo, String tenantId) {
        Map<String, List<String>> userDetails = new ConcurrentHashMap<>();
        StringBuilder uri = new StringBuilder();
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
                userDetails.put(USER_UUID, JsonPath.read(user, "$.user.*.uuid"));
                userDetails.put(USER_ROLES, new ArrayList<>(new HashSet<>(JsonPath.read(user, "$.user.*.roles.*.code"))));
            } else {
                log.error("Service returned null while fetching user for mobile number - " + mobileNumber);
            }
        } catch (Exception e) {
            log.error("Exception while fetching user for mobile number - " + mobileNumber);
            log.error("Exception trace: ", e);
        }
        return userDetails;
    }
}
