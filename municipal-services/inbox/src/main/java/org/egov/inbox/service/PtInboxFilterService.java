package org.egov.inbox.service;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.repository.ServiceRequestRepository;
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

import static org.egov.inbox.util.PTConstants.*;
import static org.egov.inbox.util.PTConstants.LIMIT_PARAM;

@Slf4j
@Service
public class PtInboxFilterService {

	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	@Value("${egov.searcher.host}")
	private String searcherHost;

	@Value("${egov.searcher.pt.search.path}")
	private String ptInboxSearcherEndpoint;

	@Value("${egov.searcher.asmt.search.path}")
	private String asmtInboxSearcherEndpoint;

	@Value("${egov.searcher.pt.search.desc.path}")
	private String ptInboxSearcherDescEndpoint;

	@Value("${egov.searcher.pt.count.path}")
	private String ptInboxSearcherCountEndpoint;

	@Value("${egov.searcher.asmt.count.path}")
	private String asmtInboxSearcherCountEndpoint;
	
	@Value("${egov.searcher.appeal.count.path}")
	private String appealInboxSearcherCountEndpoint;
	
	@Value("${egov.searcher.appeal.search.path}")
	private String appealInboxSearcherEndpoint;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	public List<String> fetchAcknowledgementIdsFromSearcher(InboxSearchCriteria criteria, HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo){
		List<String> acknowledgementNumbers = new ArrayList<>();
		HashMap moduleSearchCriteria = criteria.getModuleSearchCriteria();
		ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
		Boolean isSearchResultEmpty = false;
		Boolean isMobileNumberPresent = false;
		List<String> userUUIDs = new ArrayList<>();
		if(moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)){
			isMobileNumberPresent = true;
		}
		if(isMobileNumberPresent) {
			String tenantId = criteria.getTenantId();
			String mobileNumber = String.valueOf(moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
			userUUIDs = fetchUserUUID(mobileNumber, requestInfo, tenantId);
			Boolean isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;
			isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
			if(isSearchResultEmpty){
				return new ArrayList<>();
			}
		}

		if(!isSearchResultEmpty){
			Object result = null;

			Map<String, Object> searcherRequest = new HashMap<>();
			Map<String, Object> searchCriteria = new HashMap<>();

			searchCriteria.put(TENANT_ID_PARAM,criteria.getTenantId());
			searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

			// Accomodating module search criteria in searcher request
			if(moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) && !CollectionUtils.isEmpty(userUUIDs)){
				searchCriteria.put(USERID_PARAM, userUUIDs);
			}
			if(moduleSearchCriteria.containsKey(LOCALITY_PARAM)){
				searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
			}
			if(moduleSearchCriteria.containsKey(PROPERTY_ID_PARAM)){
				searchCriteria.put(PROPERTY_ID_PARAM, moduleSearchCriteria.get(PROPERTY_ID_PARAM));
			}
			if(moduleSearchCriteria.containsKey(PT_APPLICATION_NUMBER_PARAM)) {
				searchCriteria.put(PT_APPLICATION_NUMBER_PARAM, moduleSearchCriteria.get(PT_APPLICATION_NUMBER_PARAM));
			}

			// Accomodating process search criteria in searcher request
			if(!ObjectUtils.isEmpty(processCriteria.getAssignee())){
				searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
			}
			if(!ObjectUtils.isEmpty(processCriteria.getStatus())){
				searchCriteria.put(STATUS_PARAM, processCriteria.getStatus());
			}else{
				if(StatusIdNameMap.values().size() > 0) {
					if(CollectionUtils.isEmpty(processCriteria.getStatus())) {
						searchCriteria.put(STATUS_PARAM, StatusIdNameMap.keySet());
					}
				}
			}

			// Paginating searcher results
			searchCriteria.put(OFFSET_PARAM, criteria.getOffset());
			searchCriteria.put(NO_OF_RECORDS_PARAM, criteria.getLimit());
			moduleSearchCriteria.put(LIMIT_PARAM, criteria.getLimit());

			searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
			searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

			StringBuilder uri = new StringBuilder();

			if(moduleSearchCriteria.containsKey(SORT_ORDER_PARAM) && moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)){
				uri.append(searcherHost).append(ptInboxSearcherDescEndpoint);
			}
			else if(criteria.getProcessSearchCriteria().getBusinessService().get(0).equalsIgnoreCase("ASMT"))
			{
				uri.append(searcherHost).append(asmtInboxSearcherEndpoint);
			}
			else if(criteria.getProcessSearchCriteria().getBusinessService().get(0).equalsIgnoreCase("PT.APPEAL"))
				uri.append(searcherHost).append(appealInboxSearcherEndpoint);
			else {
				uri.append(searcherHost).append(ptInboxSearcherEndpoint);
			}

			
			result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

			//assessmentnumber
			if(criteria.getProcessSearchCriteria().getBusinessService().get(0).equalsIgnoreCase("ASMT"))
				acknowledgementNumbers = JsonPath.read(result, "$.Properties.*.assessmentnumber");
			else
				acknowledgementNumbers = JsonPath.read(result, "$.Properties.*.acknowldgementnumber");

		}
		return  acknowledgementNumbers;
	}

	public Integer fetchAcknowledgementIdsCountFromSearcher(InboxSearchCriteria criteria, HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo){
		Integer totalCount = 0;
		HashMap moduleSearchCriteria = criteria.getModuleSearchCriteria();
		ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
		Boolean isSearchResultEmpty = false;
		Boolean isMobileNumberPresent = false;
		List<String> userUUIDs = new ArrayList<>();
		if(moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)){
			isMobileNumberPresent = true;
		}
		if(isMobileNumberPresent) {
			String tenantId = criteria.getTenantId();
			String mobileNumber = String.valueOf(moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
			userUUIDs = fetchUserUUID(mobileNumber, requestInfo, tenantId);
			Boolean isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;
			isSearchResultEmpty = !isMobileNumberPresent || !isUserPresentForGivenMobileNumber;
			if(isSearchResultEmpty){
				return 0;
			}
		}

		if(!isSearchResultEmpty){
			Object result = null;

			Map<String, Object> searcherRequest = new HashMap<>();
			Map<String, Object> searchCriteria = new HashMap<>();

			searchCriteria.put(TENANT_ID_PARAM,criteria.getTenantId());

			// Accomodating module search criteria in searcher request
			if(moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) && !CollectionUtils.isEmpty(userUUIDs)){
				searchCriteria.put(USERID_PARAM, userUUIDs);
			}
			if(moduleSearchCriteria.containsKey(LOCALITY_PARAM)){
				searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
			}
			if(moduleSearchCriteria.containsKey(PROPERTY_ID_PARAM)){
				searchCriteria.put(PROPERTY_ID_PARAM, moduleSearchCriteria.get(PROPERTY_ID_PARAM));
			}
			if(moduleSearchCriteria.containsKey(PT_APPLICATION_NUMBER_PARAM)) {
				searchCriteria.put(PT_APPLICATION_NUMBER_PARAM, moduleSearchCriteria.get(PT_APPLICATION_NUMBER_PARAM));
			}

			// Accomodating process search criteria in searcher request
			if(!ObjectUtils.isEmpty(processCriteria.getAssignee())){
				searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
			}
			if(!ObjectUtils.isEmpty(processCriteria.getStatus())){
				searchCriteria.put(STATUS_PARAM, processCriteria.getStatus());
			}else{
				if(StatusIdNameMap.values().size() > 0) {
					if(CollectionUtils.isEmpty(processCriteria.getStatus())) {
						searchCriteria.put(STATUS_PARAM, StatusIdNameMap.keySet());
					}
				}
			}

			// Paginating searcher results

			searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
			searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

			StringBuilder uri = new StringBuilder();


			if(criteria.getProcessSearchCriteria().getBusinessService().get(0).equalsIgnoreCase("ASMT"))
				uri.append(searcherHost).append(asmtInboxSearcherCountEndpoint);
			else if(criteria.getProcessSearchCriteria().getBusinessService().get(0).equalsIgnoreCase("PT.APPEAL"))
				uri.append(searcherHost).append(appealInboxSearcherCountEndpoint);
			else
				uri.append(searcherHost).append(ptInboxSearcherCountEndpoint);

			result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

			double count = JsonPath.read(result, "$.TotalCount[0].count");
			totalCount = new Integer((int) count);

		}
		return  totalCount;
	}


	private List<String> fetchUserUUID(String mobileNumber, RequestInfo requestInfo, String tenantId) {
		StringBuilder uri = new StringBuilder();
		uri.append(userHost).append(userSearchEndpoint);
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", "CITIZEN");
		userSearchRequest.put("mobileNumber", mobileNumber);
		List<String> userUuids = new ArrayList<>();
		try {
			Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
			if(null != user) {
				//log.info(user.toString());
				userUuids = JsonPath.read(user, "$.user.*.uuid");
			}else {
				log.error("Service returned null while fetching user for mobile number - " + mobileNumber);
			}
		}catch(Exception e) {
			log.error("Exception while fetching user for mobile number - " + mobileNumber);
			log.error("Exception trace: ", e);
		}
		return userUuids;
	}
}
