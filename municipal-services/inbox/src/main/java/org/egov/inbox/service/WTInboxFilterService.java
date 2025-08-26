package org.egov.inbox.service;

import static org.egov.inbox.util.RequestServiceConstants.ASSIGNEE_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.BOOKING_NO_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.BUSINESS_SERVICE_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.DESC_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.LOCALITY_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.MOBILE_NUMBER_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.REQUESTINFO_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.SEARCH_CRITERIA_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.SORT_ORDER_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.STATUS_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.TENANT_ID_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.USERID_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.OFFSET_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.NO_OF_RECORDS_PARAM;
import static org.egov.inbox.util.RequestServiceConstants.LIMIT_PARAM;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

/**
 * Fetches the application numbers from the searcher service based on the provided search criteria.
 * 
 * This method constructs the search request by filtering the search criteria like tenantId, businessService, 
 * assignee, status, mobileNumber, booking number, and locality, and sends a request to the searcher service 
 * to fetch matching application numbers. It handles pagination and sorting of the results and also checks 
 * if a mobile number is provided, in which case it fetches the corresponding user UUIDs.
 * 
 * If no mobile number is provided or if no user exists for the given mobile number, it returns an empty list. 
 * 
 * @param criteria - The search criteria containing filters like tenantId, businessService, status, etc.
 * @param StatusIdNameMap - A map that holds status ID and name mappings.
 * @param requestInfo - The request metadata containing information like api_id, ts, etc.
 * @return List<String> - A list of application numbers (booking numbers) retrieved from the searcher service.
 */

public class WTInboxFilterService {
		
		@Value("${egov.user.host}")
		private String userHost;

		@Value("${egov.user.search.path}")
		private String userSearchEndpoint;

		@Value("${egov.searcher.host}")
		private String searcherHost;

		@Value("${egov.searcher.wt.search.path}")
		private String wtInboxSearcherEndpoint;

		@Value("${egov.searcher.wt.search.desc.path}")
		private String wtInboxSearcherDescEndpoint;

		@Autowired
		private RestTemplate restTemplate;
		
		@Autowired
		private ObjectMapper mapper;

		@Autowired
		private ServiceRequestRepository serviceRequestRepository;
		
		/**
		 * Fetches application numbers from the searcher service based on the provided search criteria.
		 * It accommodates both module-specific and process-specific search parameters and integrates
		 * user UUIDs if the mobile number is present in the search criteria.
		 *
		 * @param criteria          The inbox search criteria containing module and process-specific filters.
		 * @param StatusIdNameMap   A map of status IDs to their corresponding status names.
		 * @param requestInfo       The RequestInfo object containing metadata for the request.
		 * @return A list of application numbers matching the search criteria, or an empty list if no results are found.
		 */


		public List<String> fetchApplicationNumbersFromSearcher(InboxSearchCriteria criteria,
				HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
			List<String> applicationNumbers = new ArrayList<>();
			HashMap moduleSearchCriteria = criteria.getModuleSearchCriteria();
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
				Map<String, Object> searchCriteria = new HashMap<>();

				searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());
				searchCriteria.put(BUSINESS_SERVICE_PARAM, processCriteria.getBusinessService());

				// Accomodating module search criteria in searcher request
				if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) && !CollectionUtils.isEmpty(userUUIDs)) {
					searchCriteria.put(MOBILE_NUMBER_PARAM, moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
				}
				if (moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
					searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
				}
				if (moduleSearchCriteria.containsKey(BOOKING_NO_PARAM)) {
					searchCriteria.put(BOOKING_NO_PARAM, moduleSearchCriteria.get(BOOKING_NO_PARAM));
				}

				// Accomodating process search criteria in searcher request
				if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
					searchCriteria.put(ASSIGNEE_PARAM, processCriteria.getAssignee());
				}
				if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
					searchCriteria.put(STATUS_PARAM, processCriteria.getStatus());
				} else {
					if (StatusIdNameMap.values().size() > 0) {
						if (CollectionUtils.isEmpty(processCriteria.getStatus())) {
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
				if (moduleSearchCriteria.containsKey(SORT_ORDER_PARAM)
						&& moduleSearchCriteria.get(SORT_ORDER_PARAM).equals(DESC_PARAM)) {
					uri.append(searcherHost).append(wtInboxSearcherDescEndpoint);
				} else {
					uri.append(searcherHost).append(wtInboxSearcherEndpoint);
				}
				log.info("Checking ----- ------" + searcherRequest);
				result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);
//				applicationNumbers = JsonPath.read(result, "$.hallsBookingApplication.*.booking_no");
//				ObjectMapper mapper = new ObjectMapper();
				String jsonString = null;
				try {
					jsonString = mapper.writeValueAsString(result);
				} catch (JsonGenerationException e) {
					// TODO Auto-generated catch block
					log.error("JSON Generation error: ", e);
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					 log.error("JSON Mapping error: ", e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					 log.error("IO Exception while converting result to JSON: ", e);
				}
				
				// Use JsonPath to extract booking numbers
				applicationNumbers = JsonPath.read(jsonString, "$.waterTankerBookingDetail[*].booking_no");
				log.info("Booking Numbers: " + applicationNumbers);

			}
			return applicationNumbers;
		}
		
		/**
		 * Fetches a list of user UUIDs for the given mobile number, tenant ID, and RequestInfo.
		 * Utilizes a service request to retrieve user details from the user service.
		 *
		 * @param mobileNumber The mobile number of the user.
		 * @param requestInfo  The RequestInfo object containing metadata for the request.
		 * @param tenantId     The tenant ID for the user search.
		 * @return A list of UUIDs of the users matching the search criteria.
		 */

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
				if (null != user) {
					// log.info(user.toString());
					userUuids = JsonPath.read(user, "$.user.*.uuid");
				} else {
					log.error("Service returned null while fetching user for mobile number - " + mobileNumber);
				}
			} catch (Exception e) {
				log.error("Exception while fetching user for mobile number - " + mobileNumber);
				log.error("Exception trace: ", e);
			}
			return userUuids;
		}

}
