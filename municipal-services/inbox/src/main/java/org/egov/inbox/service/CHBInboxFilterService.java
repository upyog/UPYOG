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
import static org.egov.inbox.util.BpaConstants.CITIZEN;
import static org.egov.inbox.util.TLConstants.STATUS_PARAM;

@Slf4j
@Service
public class CHBInboxFilterService {

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.user.host}")
    private String userHost;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.searcher.chb.search.path:}")
    private String chbInboxSearcherEndpoint;

    @Value("${egov.searcher.chb.search.desc.path:}")
    private String chbInboxSearcherDescEndpoint;

    @Value("${egov.searcher.chb.count.path:}")
    private String chbInboxSearcherCountEndpoint;

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
                moduleSearchCriteria, processCriteria,userUUIDs,citizenRoles);


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

        // Try common fields: applicationNumber, bookingNo, uuid
        List<String> applicationNumbers = JsonPath.read(result, "$.bookingApplication.[*].booking_no");

        return applicationNumbers == null ? new ArrayList<>() : applicationNumbers;
    }

    /**
     * Resolve search endpoint based on sort order
     */
    private String resolveSearchEndpoint(HashMap<String, Object> moduleSearchCriteria) {
        StringBuilder uri = new StringBuilder();

        if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
                && Objects.equals(moduleSearchCriteria.get(SORT_ORDER_PARAM), DESC_PARAM)
                && chbInboxSearcherDescEndpoint != null && !chbInboxSearcherDescEndpoint.isEmpty()) {

            uri.append(searcherHost).append(chbInboxSearcherDescEndpoint);

        } else if (chbInboxSearcherEndpoint != null && !chbInboxSearcherEndpoint.isEmpty()) {
            uri.append(searcherHost).append(chbInboxSearcherEndpoint);

        } else {
            return null;
        }

        return uri.toString();
    }

    /**
     * Try multiple JsonPath expressions in order until values are found
     */
    private List<String> tryJsonPaths(Object result, List<String> jsonPaths) {
        for (String path : jsonPaths) {
            try {
                List<String> values = JsonPath.read(result, path);
                if (values != null && !values.isEmpty()) return values;
            } catch (Exception ignore) {
            }
        }
        return Collections.emptyList();
    }

    /**
     * Build search criteria map
     */
    private Map<String, Object> getSearchCriteria(InboxSearchCriteria criteria,
                                                  HashMap<String, String> statusIdNameMap,
                                                  HashMap<String, Object> moduleSearchCriteria,
                                                  ProcessInstanceSearchCriteria processCriteria, List<String> userUUIDs, List<String> userRoles) {

        Map<String, Object> searchCriteria = new HashMap<>();

        searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
        searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
            searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
        }

        // if we need to search by application status then use this
//        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey("bookingStatus")) {
//            Object statusObj = moduleSearchCriteria.get("bookingStatus");
//            List<String> requestedStatuses;
//
//            if (statusObj instanceof Collection) {
//                requestedStatuses = new ArrayList<>();
//                for (Object o : (Collection<?>) statusObj) {
//                    requestedStatuses.add(String.valueOf(o));
//                }
//            } else {
//                requestedStatuses = Arrays.asList(
//                        moduleSearchCriteria.get("status").toString().split(",")
//                );
//            }
//
//            List<String> matchingIds = new ArrayList<>();
//            for (Map.Entry<String, String> e : statusIdNameMap.entrySet()) {
//                if (requestedStatuses.contains(e.getValue())) {
//                    matchingIds.add(e.getKey());
//                }
//            }
//
//            if (!matchingIds.isEmpty()) {
//                searchCriteria.put("status", matchingIds);
//            }
//        }
        if (moduleSearchCriteria != null && (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) || userRoles.contains(CITIZEN))
                && !CollectionUtils.isEmpty(userUUIDs)) {
            searchCriteria.put(USERID_PARAM, userUUIDs);
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
        //accomadating processearchcriteria in the search_criteria
        if(!ObjectUtils.isEmpty(processCriteria.getStatus())){
            List<String> matchingIdsChb = statusIdNameMap.entrySet().stream()
                    .filter(entry -> processCriteria.getStatus().contains(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            searchCriteria.put(STATUS_PARAM, matchingIdsChb);
        }else{
            if(statusIdNameMap.values().size() > 0) {
                if(CollectionUtils.isEmpty(processCriteria.getStatus())) {
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

        if (chbInboxSearcherCountEndpoint != null && !chbInboxSearcherCountEndpoint.isEmpty()) {
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
                    moduleSearchCriteria, processCriteria,userUUIDs,citizenRoles);

            searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder citizenUri = new StringBuilder();
            citizenUri.append(searcherHost).append(chbInboxSearcherCountEndpoint);

            Object result = restTemplate.postForObject(citizenUri.toString(), searcherRequest, Map.class);

            // Common count path
            Double count = JsonPath.read(result, "$.totalCount[0].totalcount");

            return count == null ? 0 : count.intValue();

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
