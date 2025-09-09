package org.egov.inbox.service;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.util.InboxConstants;
import org.egov.inbox.util.PGRAiConstants;
import org.egov.inbox.util.StreetVendingConstants;
import org.egov.inbox.web.model.InboxSearchCriteria;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import static org.egov.inbox.util.RequestServiceConstants.MOBILE_NUMBER_PARAM;
import static org.egov.inbox.util.StreetVendingConstants.*;
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

	public List<String> fetchApplicationIdsFromSearcher(InboxSearchCriteria criteria,
			HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
		List<String> applicationNumberList = new ArrayList<>();
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
			userUUIDs = userService.fetchUserUUID(mobileNumber, requestInfo, tenantId);
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
			if (moduleSearchCriteria.containsKey(VENDING_TYPE_PARAM) && !CollectionUtils.isEmpty(userUUIDs)) {
				searchCriteria.put(VENDING_TYPE_PARAM, moduleSearchCriteria.get(VENDING_TYPE_PARAM));
			}

			if (moduleSearchCriteria.containsKey(VENDING_ZONE_PARAM) && !CollectionUtils.isEmpty(userUUIDs)) {
				searchCriteria.put(VENDING_ZONE_PARAM, moduleSearchCriteria.get(VENDING_ZONE_PARAM));
			}

			if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) && !CollectionUtils.isEmpty(userUUIDs)) {
				searchCriteria.put(MOBILE_NUMBER_PARAM, moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
			}

			if (moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
				searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
			}
			if (moduleSearchCriteria.containsKey(SV_APPLICATION_NUMBER_PARAM)) {
				searchCriteria.put(SV_APPLICATION_NUMBER_PARAM, moduleSearchCriteria.get(SV_APPLICATION_NUMBER_PARAM));
			}
			
			if (moduleSearchCriteria.containsKey(STATUS_PARAM)) {
				searchCriteria.put(STATUS_PARAM, moduleSearchCriteria.get(STATUS_PARAM));
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
				uri.append(searcherHost).append(streetVendingInboxSearcherDescEndpoint);
			} else {
				uri.append(searcherHost).append(streetVendingInboxSearcherEndpoint);
			}

			result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

			applicationNumberList = JsonPath.read(result, "$.StreetVending.*.application_no");

			log.info("applicationNumberList fetched from seracher endpoint : " + applicationNumberList);

		}
		return applicationNumberList;
	}

	public Integer fetchApplicationIdsCountFromSearcher(InboxSearchCriteria criteria,
			HashMap<String, String> StatusIdNameMap, RequestInfo requestInfo) {
		Integer totalCount = 0;
		HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();
		ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
		boolean isUserPresentForGivenMobileNumber = false;
		List<String> userUUIDs = new ArrayList<>();

		if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM)) {
			String tenantId = criteria.getTenantId();
			String mobileNumber = String.valueOf(moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
			userUUIDs = userService.fetchUserUUID(mobileNumber, requestInfo, tenantId);
			// If user is not mapped to given mobile no then return empty list
			isUserPresentForGivenMobileNumber = CollectionUtils.isEmpty(userUUIDs) ? false : true;

			if (!isUserPresentForGivenMobileNumber) {
				return 0;
			}
		}

		if (isUserPresentForGivenMobileNumber) {
			Object result = null;

			Map<String, Object> searcherRequest = new HashMap<>();
			Map<String, Object> searchCriteria = new HashMap<>();

			searchCriteria.put(TENANT_ID_PARAM, criteria.getTenantId());

			// Accomodating module search criteria in searcher request
			if (moduleSearchCriteria.containsKey(VENDING_TYPE_PARAM) && !CollectionUtils.isEmpty(userUUIDs)) {
				searchCriteria.put(VENDING_TYPE_PARAM, moduleSearchCriteria.get(VENDING_TYPE_PARAM));
			}

			if (moduleSearchCriteria.containsKey(VENDING_ZONE_PARAM) && !CollectionUtils.isEmpty(userUUIDs)) {
				searchCriteria.put(VENDING_ZONE_PARAM, moduleSearchCriteria.get(VENDING_ZONE_PARAM));
			}

			if (moduleSearchCriteria.containsKey(MOBILE_NUMBER_PARAM) && !CollectionUtils.isEmpty(userUUIDs)) {
				searchCriteria.put(MOBILE_NUMBER_PARAM, moduleSearchCriteria.get(MOBILE_NUMBER_PARAM));
			}
			if (moduleSearchCriteria.containsKey(LOCALITY_PARAM)) {
				searchCriteria.put(LOCALITY_PARAM, moduleSearchCriteria.get(LOCALITY_PARAM));
			}
			if (moduleSearchCriteria.containsKey(SV_APPLICATION_NUMBER_PARAM)) {
				searchCriteria.put(SV_APPLICATION_NUMBER_PARAM, moduleSearchCriteria.get(SV_APPLICATION_NUMBER_PARAM));
			}
			if (moduleSearchCriteria.containsKey(STATUS_PARAM)) {
				searchCriteria.put(STATUS_PARAM, moduleSearchCriteria.get(STATUS_PARAM));
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

			if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
				searchCriteria.put(VENDING_TYPE_PARAM, processCriteria.getStatus());
			} else {
				if (StatusIdNameMap.values().size() > 0) {
					if (CollectionUtils.isEmpty(processCriteria.getStatus())) {
						searchCriteria.put(VENDING_TYPE_PARAM, StatusIdNameMap.keySet());
					}
				}
			}

			if (!ObjectUtils.isEmpty(processCriteria.getStatus())) {
				searchCriteria.put(VENDING_ZONE_PARAM, processCriteria.getStatus());
			} else {
				if (StatusIdNameMap.values().size() > 0) {
					if (CollectionUtils.isEmpty(processCriteria.getStatus())) {
						searchCriteria.put(VENDING_ZONE_PARAM, StatusIdNameMap.keySet());
					}
				}
			}
			// Paginating searcher results

			searcherRequest.put(REQUESTINFO_PARAM, requestInfo);
			searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

			StringBuilder uri = new StringBuilder();
			uri.append(searcherHost).append(streetVendingInboxSearcherCountEndpoint);

			result = restTemplate.postForObject(uri.toString(), searcherRequest, Map.class);

			double count = JsonPath.read(result, "$.TotalCount[0].count");
			totalCount = (int) count;

		}
		return totalCount;
	}

}
