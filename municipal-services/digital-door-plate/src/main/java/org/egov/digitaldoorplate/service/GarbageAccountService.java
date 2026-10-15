package org.egov.digitaldoorplate.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.digitaldoorplate.repository.ServiceRequestRepository;
import org.egov.digitaldoorplate.util.DdpConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GarbageAccountService {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private DdpConstants ddpConfig;

	/**
	 * Searches active garbage account (with active sub accounts) from
	 * garbage-service by account uuid scanned from the door plate QR code.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> searchGarbageAccountByUuid(RequestInfo requestInfo, String tenantId,
			String garbageAccountUuid) {

		StringBuilder uri = new StringBuilder(ddpConfig.getGarbageServiceHostUrl())
				.append(ddpConfig.getGarbageAccountSearchEndpoint())
				.append("?tenantId=").append(tenantId);

		Map<String, Object> searchCriteriaGarbageAccount = new HashMap<>();
		searchCriteriaGarbageAccount.put("uuid", Collections.singletonList(garbageAccountUuid));
		searchCriteriaGarbageAccount.put("isActiveAccount", Boolean.TRUE);
		searchCriteriaGarbageAccount.put("isActiveSubAccount", Boolean.TRUE);

		Map<String, Object> request = new HashMap<>();
		request.put("RequestInfo", requestInfo);
		request.put("searchCriteriaGarbageAccount", searchCriteriaGarbageAccount);

		Optional<Object> response = serviceRequestRepository.fetchResult(uri, request);

		if (!response.isPresent()) {
			throw new CustomException("GARBAGE_SEARCH_FAILED",
					"No response received from garbage-service while searching garbage account.");
		}

		return (Map<String, Object>) response.get();
	}

	/**
	 * Partially updates the DDP workflow fields (vendor print verification,
	 * vendor printing-done, vendor dispatched, ULB verification, or
	 * installation + lat/long) on the garbage account identified by
	 * {@code garbageAccountUuid}, via garbage-service's
	 * {@code /garbage-accounts/_updateDdpWorkflow}. Fields left null are not
	 * sent as "clear this column" but simply omitted from the update (see
	 * garbage-service's COALESCE-based partial update), so each role-specific
	 * caller only needs to pass the field(s) it owns.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> updateDdpWorkflowFields(RequestInfo requestInfo, String tenantId,
			String garbageAccountUuid, String vendorPrintVerified, Boolean ulbVerified, Boolean installationDone,
			String ddpLatitude, String ddpLongitude, Boolean ddpPrintingDone, Boolean ddpDispatched, Boolean ddpPrintVerified, String ddpRejectionReason) {

		StringBuilder uri = new StringBuilder(ddpConfig.getGarbageServiceHostUrl())
				.append(ddpConfig.getGarbageAccountUpdateDdpWorkflowEndpoint());

		Map<String, Object> request = new HashMap<>();
		request.put("RequestInfo", requestInfo);
		request.put("tenantId", tenantId);
		request.put("uuid", garbageAccountUuid);
		request.put("vendorPrintVerified", vendorPrintVerified);
		request.put("ulbVerified", ulbVerified);
		request.put("installationDone", installationDone);
		request.put("ddpLatitude", ddpLatitude);
		request.put("ddpLongitude", ddpLongitude);
		request.put("ddpPrintingDone", ddpPrintingDone);
		request.put("ddpDispatched", ddpDispatched);
		request.put("ddpPrintVerified", ddpPrintVerified);
		request.put("ddpRejectionReason", ddpRejectionReason);

		Optional<Object> response = serviceRequestRepository.fetchResult(uri, request);

		if (!response.isPresent()) {
			throw new CustomException("GARBAGE_UPDATE_FAILED",
					"No response received from garbage-service while updating DDP workflow fields.");
		}

		return (Map<String, Object>) response.get();
	}
}
