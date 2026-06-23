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
public class FireNocInboxFilterService {

    @Value("${egov.user.host}")
    private String userHost;
    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.searcher.fn.search.path:}")
    private String fireNocInboxSearcherEndpoint;

    @Value("${egov.searcher.fn.search.desc.path:}")
    private String fireNocInboxSearcherDescEndpoint;

    @Value("${egov.searcher.fn.count.path:}")
    private String fireNocInboxSearcherCountEndpoint;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<String> fetchApplicationNumbersFromSearcher(InboxSearchCriteria criteria,
            HashMap<String, String> statusIdNameMap, RequestInfo requestInfo) {
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        boolean isSearchResultEmpty = false;
        boolean isMobileNumberPresent = false;
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
            boolean isUserPresentForGivenMobileNumber = !CollectionUtils.isEmpty(userUUIDs);
            isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
            if (isSearchResultEmpty) {
                return new ArrayList<>();
            }
        } else {
            List<String> roles = requestInfo.getUserInfo().getRoles().stream().map(Role::getCode)
                    .collect(Collectors.toList());
            if (roles.contains(CITIZEN)) {
                userUUIDs.add(requestInfo.getUserInfo().getUuid());
                citizenRoles = roles;
            }
        }

        Map<String, Object> searcherRequest = new HashMap<>();
        Map<String, Object> searchCriteria = getSearchCriteria(criteria, statusIdNameMap, moduleSearchCriteria,
                processCriteria, userUUIDs, citizenRoles);

        // Paginating searcher results
        searchCriteria.put(OFFSET_PARAM, criteria.getOffset());
        searchCriteria.put(NO_OF_RECORDS_PARAM, criteria.getLimit());
        moduleSearchCriteria.put(LIMIT_PARAM, criteria.getLimit());

        searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
        searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

        StringBuilder uri = new StringBuilder();
        if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
                && Objects.equals(moduleSearchCriteria.get(SORT_ORDER_PARAM), DESC_PARAM)
                && fireNocInboxSearcherDescEndpoint != null && !fireNocInboxSearcherDescEndpoint.isEmpty()) {
            uri.append(searcherHost).append(fireNocInboxSearcherDescEndpoint);
        } else {
            uri.append(searcherHost).append(fireNocInboxSearcherEndpoint);
        }

        Object result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

        List<String> applicationNumbers = new ArrayList<>();
        Map<String, String> appNoToUuidMap = new HashMap<>();

        List<Map<String, Object>> firenocs = null;
        try {
            firenocs = JsonPath.read(result, "$.FireNOCs");
        } catch (Exception e) {
            log.error("Error reading FireNOCs from searcher result", e);
        }

        if (firenocs != null) {
            for (Map<String, Object> fn : firenocs) {
                if (fn != null) {
                    String appNo = fn.get("applicationnumber") != null ? String.valueOf(fn.get("applicationnumber")) : null;
                    String uuid = fn.get("uuid") != null ? String.valueOf(fn.get("uuid")) : null;

                    if (appNo != null) {
                        applicationNumbers.add(appNo);
                        if (uuid != null) {
                            appNoToUuidMap.put(appNo, uuid);
                        }
                    }
                }
            }
        }

        if (criteria.getModuleSearchCriteria() != null && !appNoToUuidMap.isEmpty()) {
            criteria.getModuleSearchCriteria().put("firenoc_appNo_to_uuid_map", appNoToUuidMap);
        }

        return applicationNumbers;
    }

    private Map<String, Object> getSearchCriteria(InboxSearchCriteria criteria,
            HashMap<String, String> statusIdNameMap, HashMap<String, Object> moduleSearchCriteria,
            ProcessInstanceSearchCriteria processCriteria, List<String> userUUIDs, List<String> userRoles) {
        Map<String, Object> searchCriteria = new HashMap<>();

        searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
        searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
            searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("applicationNumber")) {
            searchCriteria.put("applicationNumber", moduleSearchCriteria.get("applicationNumber"));
        } else if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("applicationNo")) {
            searchCriteria.put("applicationNumber", moduleSearchCriteria.get("applicationNo"));
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("fireNOCNumber")) {
            searchCriteria.put("fireNOCNumber", moduleSearchCriteria.get("fireNOCNumber"));
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("fireNOCType")) {
            searchCriteria.put("fireNOCType", moduleSearchCriteria.get("fireNOCType"));
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("fireStationId")) {
            searchCriteria.put("fireStationId", moduleSearchCriteria.get("fireStationId"));
        }
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("financialYear")) {
            searchCriteria.put("financialYear", moduleSearchCriteria.get("financialYear"));
        }

        // Map status display names to WF UUIDs for searcher
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(STATUS_PARAM)
                && moduleSearchCriteria.get(STATUS_PARAM) != null) {
            List<String> requestedStatuses = Arrays.asList(moduleSearchCriteria.get(STATUS_PARAM).toString().split(","));
            List<String> matchingIds = statusIdNameMap.entrySet().stream()
                    .filter(entry -> requestedStatuses.contains(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (!matchingIds.isEmpty()) {
                searchCriteria.put(STATUS_PARAM, matchingIds);
            }
        } else {
            searchCriteria.put(STATUS_PARAM, new ArrayList<>(statusIdNameMap.keySet()));
        }

        if (moduleSearchCriteria != null && (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) || userRoles.contains(CITIZEN))
                && !CollectionUtils.isEmpty(userUUIDs)) {
            searchCriteria.put(USERID_PARAM, userUUIDs);
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
            HashMap<String, String> statusIdNameMap, RequestInfo requestInfo) {
        if (fireNocInboxSearcherCountEndpoint != null && !fireNocInboxSearcherCountEndpoint.isEmpty()) {
            HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
            ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
            boolean isSearchResultEmpty = false;
            boolean isMobileNumberPresent = false;
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
                boolean isUserPresentForGivenMobileNumber = !CollectionUtils.isEmpty(userUUIDs);
                isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
                if (isSearchResultEmpty) {
                    return 0;
                }
            } else {
                List<String> roles = requestInfo.getUserInfo().getRoles().stream().map(Role::getCode)
                        .collect(Collectors.toList());
                if (roles.contains(CITIZEN)) {
                    userUUIDs.add(requestInfo.getUserInfo().getUuid());
                    citizenRoles = roles;
                }
            }
            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = getSearchCriteria(criteria, statusIdNameMap, moduleSearchCriteria,
                    processCriteria, userUUIDs, citizenRoles);
            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);
            StringBuilder citizenUri = new StringBuilder();
            citizenUri.append(searcherHost).append(fireNocInboxSearcherCountEndpoint);
            Object result = restTemplate.postForObject(citizenUri.toString(), searcherRequest, Map.class);

            try {
                Double count = JsonPath.read(result, "$.TotalCount.count");
                return count == null ? 0 : count.intValue();
            } catch (Exception e1) {
                try {
                    Double count = JsonPath.read(result, "$.TotalCount[0].count");
                    return count == null ? 0 : count.intValue();
                } catch (Exception e2) {
                    try {
                        Double count = JsonPath.read(result, "$.totalCount[0].totalcount");
                        return count == null ? 0 : count.intValue();
                    } catch (Exception e3) {
                        return 0;
                    }
                }
            }
        } else {
            List<String> apps = fetchApplicationNumbersFromSearcher(criteria, statusIdNameMap, requestInfo);
            return apps.size();
        }
    }

    private Map<String, List<String>> fetchUserUUID(String mobileNumber, RequestInfo requestInfo, String tenantId) {
        Map<String, List<String>> userDetails = new ConcurrentHashMap<>();
        StringBuilder uri = new StringBuilder();
        uri.append(userHost).append(userSearchEndpoint);
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put(REQUESTINFO_PARAM, requestInfo);
        userSearchRequest.put(TENANT_ID_PARAM, tenantId);
        userSearchRequest.put("userType", CITIZEN);
        userSearchRequest.put(MOBILE_NUMBER_PARAM, mobileNumber);
        try {
            Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
            if (null != user) {
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
