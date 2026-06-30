package org.egov.inbox.service;

import com.jayway.jsonpath.JsonPath;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.egov.inbox.util.BpaConstants.*;
import static org.egov.inbox.util.TLConstants.STATUS_PARAM;

@Slf4j
@Service
public class GarbageInboxFilterService {

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.user.host}")
    private String userHost;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.searcher.gc.search.path:}")
    private String gcInboxSearcherEndpoint;

    @Value("${egov.searcher.gc.search.desc.path:}")
    private String gcInboxSearcherDescEndpoint;

    @Value("${egov.searcher.gc.count.path:}")
    private String gcInboxSearcherCountEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    /**
     * Fetch application numbers from searcher
     */
    public List<String> fetchApplicationNumbersFromSearcher(InboxSearchCriteria criteria,
                                                            HashMap<String, String> statusIdNameMap,
                                                            RequestInfo requestInfo) {

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
        Map<String, Object> searchCriteria = getSearchCriteria(criteria, statusIdNameMap,
                moduleSearchCriteria, processCriteria, userUUIDs, citizenRoles);

        // Pagination
        searchCriteria.put(OFFSET_PARAM, criteria.getOffset());
        searchCriteria.put(NO_OF_RECORDS_PARAM, criteria.getLimit());
        moduleSearchCriteria.put(LIMIT_PARAM, criteria.getLimit());

        searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
        searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

        String endpoint = resolveSearchEndpoint(moduleSearchCriteria);
        if (endpoint == null || endpoint.isEmpty()) {
            return Collections.emptyList();
        }

        Object result = restTemplate.postForObject(endpoint, searcherRequest, Map.class);

        // Extract application numbers from garbageApplications
        List<String> applicationNumbers = JsonPath.read(result, "$.garbageApplications.[*].applicationno");

        return applicationNumbers == null ? new ArrayList<>() : applicationNumbers;
    }

    /**
     * Resolve search endpoint based on sort order
     */
    private String resolveSearchEndpoint(HashMap<String, Object> moduleSearchCriteria) {
        StringBuilder uri = new StringBuilder();

        if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
                && Objects.equals(moduleSearchCriteria.get(SORT_ORDER_PARAM), DESC_PARAM)
                && gcInboxSearcherDescEndpoint != null && !gcInboxSearcherDescEndpoint.isEmpty()) {

            uri.append(searcherHost).append(gcInboxSearcherDescEndpoint);

        } else if (gcInboxSearcherEndpoint != null && !gcInboxSearcherEndpoint.isEmpty()) {
            uri.append(searcherHost).append(gcInboxSearcherEndpoint);

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
                                                  ProcessInstanceSearchCriteria processCriteria,
                                                  List<String> userUUIDs,
                                                  List<String> userRoles) {

        Map<String, Object> searchCriteria = new HashMap<>();

        searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
        searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
            searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
        }

        if (moduleSearchCriteria != null && (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) || userRoles.contains(CITIZEN))
                && !CollectionUtils.isEmpty(userUUIDs)) {
            searchCriteria.put(USERID_PARAM, userUUIDs);
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("applicationNumber")) {
            searchCriteria.put("applicationNumber", moduleSearchCriteria.get("applicationNumber"));
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("connectionNumber")) {
            searchCriteria.put("connectionNumber", moduleSearchCriteria.get("connectionNumber"));
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("oldConnectionNumber")) {
            searchCriteria.put("oldConnectionNumber", moduleSearchCriteria.get("oldConnectionNumber"));
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("uuid")) {
            searchCriteria.put("id", moduleSearchCriteria.get("uuid"));
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("propertyId")) {
            searchCriteria.put("propertyId", moduleSearchCriteria.get("propertyId"));
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("applicationStatus")) {
            searchCriteria.put("applicationStatus", moduleSearchCriteria.get("applicationStatus"));
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("connectionStatus")) {
            searchCriteria.put("connectionStatus", moduleSearchCriteria.get("connectionStatus"));
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("applicationType")) {
            searchCriteria.put("applicationType", moduleSearchCriteria.get("applicationType"));
        }

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("channel")) {
            searchCriteria.put("channel", moduleSearchCriteria.get("channel"));
        }

        if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
            searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
        }

        if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
        List<String> matchingIdsGc = statusIdNameMap.entrySet().stream()
                .filter(entry -> processCriteria.getStatus().contains(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(matchingIdsGc)) {
            searchCriteria.put(STATUS_PARAM, matchingIdsGc);
        } else {
            // No matching statuses → return empty result set immediately
            // Putting an empty IN list would cause SQL syntax error
            searchCriteria.put(STATUS_PARAM, Collections.singletonList("NO_MATCH"));
        }
    } else {
        if (!statusIdNameMap.isEmpty()) {
            searchCriteria.put(STATUS_PARAM, statusIdNameMap.keySet());
        }
        // If statusIdNameMap is also empty, don't put STATUS_PARAM at all
        // so the searcher omits the IN clause entirely
    }

        return searchCriteria;
    }

    /**
     * Fetch application count
     */
    public Integer fetchApplicationCountFromSearcher(InboxSearchCriteria criteria,
                                                     HashMap<String, String> statusIdNameMap,
                                                     RequestInfo requestInfo) {

        if (gcInboxSearcherCountEndpoint != null && !gcInboxSearcherCountEndpoint.isEmpty()) {
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
            Map<String, Object> searchCriteria = getSearchCriteria(criteria, statusIdNameMap,
                    moduleSearchCriteria, processCriteria, userUUIDs, citizenRoles);

            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder uri = new StringBuilder();
            uri.append(searcherHost).append(gcInboxSearcherCountEndpoint);

            Object result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

            // Common count path
            Double count = JsonPath.read(result, "$.totalCount[0].totalcount");

            return count == null ? 0 : count.intValue();

        } else {
            List<String> apps = fetchApplicationNumbersFromSearcher(criteria, statusIdNameMap, requestInfo);
            return apps.size();
        }
    }

    /**
     * Fetch user UUID by mobile number
     */
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