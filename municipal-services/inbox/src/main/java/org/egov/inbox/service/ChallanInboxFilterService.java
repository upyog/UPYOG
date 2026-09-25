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
public class ChallanInboxFilterService {

    @Value("${egov.user.host}")
    private String userHost;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

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
                                                  ProcessInstanceSearchCriteria processCriteria, List<String> userUUIDs, List<String> userRoles) {

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

        // MobileNumber
        if (moduleSearchCriteria != null && moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)) {
            searchCriteria.put("citizenUuid", userUUIDs);
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
                    moduleSearchCriteria, processCriteria,userUUIDs, citizenRoles);


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