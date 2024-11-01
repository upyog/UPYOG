package org.egov.inbox.service;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.util.InboxConstants;
import org.egov.inbox.util.StreetVendingConstants;
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
public class StreetVendingInboxFilterService {

    @Value("${egov.searcher.host}")
    private String searcherHost;

    @Value("${egov.searcher.streetVending.search.path}")
    private String streetVendingInboxSearcherEndpoint;

    @Value("${egov.searcher.streetVending.search.desc.path}")
    private String streetVendingInboxSearcherDescEndpoint;

    @Value("${egov.searcher.streetVending.count.path}")
    private String streetVendingInboxSearcherCountEndpoint;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RestTemplate restTemplate;

   

    public List<String> fetchApplicationIdsFromSearcher(InboxSearchCriteria criteria, HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo){
        List<String> applicationNumberList = new ArrayList<>();
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        
        boolean isUserPresentForGivenMobileNumber = false;
        
        List<String> userUUIDs = new ArrayList<>();
       
        // Mobile no is present in search criteria
        if(moduleSearchCriteria.containsKey(InboxConstants.MOBILE_NUMBER_PARAM)) {
            String tenantId = criteria.getTenantId();
            String mobileNumber = String.valueOf(moduleSearchCriteria.get(InboxConstants.MOBILE_NUMBER_PARAM));
            userUUIDs =userService.fetchUserUUID(mobileNumber, requestInfo, tenantId);

            // If user is not mapped to given mobile no then return empty list
            isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;
            
            if(!isUserPresentForGivenMobileNumber){
                return new ArrayList<>();
            }
        }

        if(isUserPresentForGivenMobileNumber){
            Object result = null;

            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = new HashMap<>();

            searchCriteria.put(InboxConstants.TENANT_ID_PARAM,criteria.getTenantId());
            searchCriteria.put(InboxConstants.BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

            // Accomodating module search criteria in searcher request
            if(moduleSearchCriteria.containsKey(InboxConstants.MOBILE_NUMBER_PARAM) && !CollectionUtils.isEmpty(userUUIDs)){
                searchCriteria.put(InboxConstants.USERID_PARAM, userUUIDs);
            }
            
            if(moduleSearchCriteria.containsKey(InboxConstants.LOCALITY_PARAM)){
                searchCriteria.put(InboxConstants.LOCALITY_PARAM, moduleSearchCriteria.get(InboxConstants.LOCALITY_PARAM));
            }
            if(moduleSearchCriteria.containsKey(StreetVendingConstants.APPLICATION_NUMBER_PARAM)){
                searchCriteria.put(StreetVendingConstants.APPLICATION_NUMBER_PARAM, moduleSearchCriteria.get(StreetVendingConstants.APPLICATION_NUMBER_PARAM));
            }

            // Accomodating process search criteria in searcher request
            if(!ObjectUtils.isEmpty(processCriteria.getAssignee())){
                searchCriteria.put(InboxConstants.ASSIGNEE_PARAM, processCriteria.getAssignee());
            }
            if(!ObjectUtils.isEmpty(processCriteria.getStatus())){
                searchCriteria.put(InboxConstants.STATUS_PARAM, processCriteria.getStatus());
            }else{
                if(StatusIdNameMap.values().size() > 0) {
                    if(CollectionUtils.isEmpty(processCriteria.getStatus())) {
                        searchCriteria.put(InboxConstants.STATUS_PARAM, StatusIdNameMap.keySet());
                    }
                }
            }

            // Paginating searcher results
            searchCriteria.put(InboxConstants.OFFSET_PARAM, criteria.getOffset());
            searchCriteria.put(InboxConstants.NO_OF_RECORDS_PARAM, criteria.getLimit());
            moduleSearchCriteria.put(InboxConstants.LIMIT_PARAM, criteria.getLimit());

            searcherRequest.put(InboxConstants.REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(InboxConstants.SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder uri = new StringBuilder();
            if(moduleSearchCriteria.containsKey(InboxConstants.SORT_ORDER_PARAM) 
            		&& moduleSearchCriteria.get(InboxConstants.SORT_ORDER_PARAM).equals(InboxConstants.DESC_PARAM)){
                uri.append(searcherHost).append(streetVendingInboxSearcherDescEndpoint);
            }else {
                uri.append(searcherHost).append(streetVendingInboxSearcherEndpoint);
            }

            result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

            applicationNumberList = JsonPath.read(result, "$.StreetVending.*.applicationNumber");
            
            log.info("applicationNumberList fetched from seracher endpoint : " + applicationNumberList);

        }
        return  applicationNumberList;
    }

    public Integer fetchApplicationIdsCountFromSearcher(InboxSearchCriteria criteria, HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo){
        Integer totalCount = 0;
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        boolean isUserPresentForGivenMobileNumber = false;
        List<String> userUUIDs = new ArrayList<>();
        
        if(moduleSearchCriteria.containsKey(InboxConstants.MOBILE_NUMBER_PARAM)) {
        	String tenantId = criteria.getTenantId();
            String mobileNumber = String.valueOf(moduleSearchCriteria.get(InboxConstants.MOBILE_NUMBER_PARAM));
            userUUIDs =userService.fetchUserUUID(mobileNumber, requestInfo, tenantId);
            // If user is not mapped to given mobile no then return empty list
            isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;
            
            if(!isUserPresentForGivenMobileNumber){
                return 0;
            }
        }

        if(isUserPresentForGivenMobileNumber){
            Object result = null;

            Map<String, Object> searcherRequest = new HashMap<>();
            Map<String, Object> searchCriteria = new HashMap<>();

            searchCriteria.put(InboxConstants.TENANT_ID_PARAM,criteria.getTenantId());

            // Accomodating module search criteria in searcher request
            if(moduleSearchCriteria.containsKey(InboxConstants.MOBILE_NUMBER_PARAM) && !CollectionUtils.isEmpty(userUUIDs)){
                searchCriteria.put(InboxConstants.USERID_PARAM, userUUIDs);
            }
            if(moduleSearchCriteria.containsKey(InboxConstants.LOCALITY_PARAM)){
                searchCriteria.put(InboxConstants.LOCALITY_PARAM, moduleSearchCriteria.get(InboxConstants.LOCALITY_PARAM));
            }
            if(moduleSearchCriteria.containsKey(StreetVendingConstants.APPLICATION_NUMBER_PARAM)){
                searchCriteria.put(StreetVendingConstants.APPLICATION_NUMBER_PARAM, moduleSearchCriteria.get(StreetVendingConstants.APPLICATION_NUMBER_PARAM));
            }

            // Accomodating process search criteria in searcher request
            if(!ObjectUtils.isEmpty(processCriteria.getAssignee())){
                searchCriteria.put(InboxConstants.ASSIGNEE_PARAM, processCriteria.getAssignee());
            }
			if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
				searchCriteria.put(InboxConstants.STATUS_PARAM, processCriteria.getStatus());
			} else {
				if (StatusIdNameMap.values().size() > 0) {
					if (CollectionUtils.isEmpty(processCriteria.getStatus())) {
						searchCriteria.put(InboxConstants.STATUS_PARAM, StatusIdNameMap.keySet());
					}
				}
			}

            // Paginating searcher results

            searcherRequest.put(InboxConstants.REQUESTINFO_PARAM, requestInfo);
            searcherRequest.put(InboxConstants.SEARCH_CRITERIA_PARAM, searchCriteria);

            StringBuilder uri = new StringBuilder();
            uri.append(searcherHost).append(streetVendingInboxSearcherCountEndpoint);

            result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

            double count = JsonPath.read(result, "$.TotalCount[0].count");
            totalCount = (int) count;

        }
        return  totalCount;
    }
    
    
    

}
