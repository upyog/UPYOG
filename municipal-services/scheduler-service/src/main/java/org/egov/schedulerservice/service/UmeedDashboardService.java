package org.egov.schedulerservice.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.schedulerservice.request.UmeedDashboardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UmeedDashboardService {

	@Autowired
	private NiuaOAuthTokenService niuaOAuthTokenService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TLService tlService;

	@Autowired
	private UmeedDashboardClientService umeedDashboardClientService;

	public Object pushUmeedDashboardMetricsForTL(RequestInfo requestInfo) {

		// Step 1: Build request info for Umeed Dashboard
		RequestInfo umeedDashboardRequestInfo = buildRequestInfo();

		// Step 2: Fetch Trade License dashboard metrics
		Object umeedDashboardDataMatrics = tlService.getUmeedDashbaordDataMatrics(requestInfo);

		UmeedDashboardRequest umeedDashboardRequest = UmeedDashboardRequest.builder()
				.requestInfo(umeedDashboardRequestInfo)
				.data(objectMapper.valueToTree(umeedDashboardDataMatrics).get("Data")).build();

		// Step 3: call umeed dashboard api to push data
		String ingestResponse = umeedDashboardClientService.sendMetrics(umeedDashboardRequest);

		return ingestResponse;
	}

	private RequestInfo buildRequestInfo() {
		RequestInfo requestInfo = RequestInfo.builder().build();

		Object niuaOAuthTokenResponse = niuaOAuthTokenService.requestNiuaOAuthToken();

		if (null != niuaOAuthTokenResponse && null != objectMapper.valueToTree(niuaOAuthTokenResponse)
				&& !objectMapper.valueToTree(niuaOAuthTokenResponse).isNull()) {
			if (null != objectMapper.valueToTree(niuaOAuthTokenResponse).get("access_token")) {
				requestInfo.setAuthToken(objectMapper.valueToTree(niuaOAuthTokenResponse).get("access_token").asText());
			}
			if (null != objectMapper.valueToTree(niuaOAuthTokenResponse).get("UserRequest")
					&& !objectMapper.valueToTree(niuaOAuthTokenResponse).get("UserRequest").isNull()) {
				try {
					requestInfo.setUserInfo(objectMapper.treeToValue(
							objectMapper.valueToTree(niuaOAuthTokenResponse).get("UserRequest"), User.class));
				} catch (JsonProcessingException e) {
					// Log and handle the mapping failure
					log.error("Error mapping UserRequest to User object: " + e.getMessage());
				}
			}
		}
		return requestInfo;
	}

}
