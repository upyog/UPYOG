package org.egov.garbageservice.controller;

import java.util.Collections;
import java.util.HashMap;

import java.util.Map;
import java.util.UUID;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.garbageservice.model.GarbageAccountActionRequest;
import org.egov.garbageservice.model.GarbageAccountActionResponse;
import org.egov.garbageservice.model.GarbageAccountRequest;
import org.egov.garbageservice.model.GarbageAccountResponse;
import org.egov.garbageservice.model.PayNowRequest;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.common.contract.request.RequestInfo;

import org.egov.garbageservice.model.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccountRequest;
import org.egov.garbageservice.model.TotalCountRequest;
import org.egov.garbageservice.service.GarbageAccountService;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.garbageservice.model.GenrateArrearRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.egov.common.contract.request.User;
import org.egov.common.contract.request.Role;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;


@Slf4j
@Tag(name = "Garbage Account", description = "APIs for managing garbage accounts")
@RestController
@RequestMapping("/garbage-accounts")
/**
 * REST controller exposing garbage account lifecycle and citizen-facing APIs.
 *
 * Behavior:
 * - POST /_create, /_update, /_update_status — create, modify, and workflow-update garbage accounts.
 * - POST /_search — search accounts (optional IsIndex for index-backed search).
 * - POST /open/_search — citizen open search/pay preview; requires at least one search key; builds synthetic RequestInfo if missing.
 * - POST /fetch/{value} — CALCULATEFEE (application details) or ACTIONS (workflow actions) via path segment.
 * - POST /_payNow — initiate pay-now flow for a garbage bill.
 * - POST /_createUserForGarbage, /_counts, /_generateGrbgTaxBillReceipt, /_createArear — user provisioning, dashboards, receipt PDF, arrear generation.
 * - Delegates all business logic to {@link org.egov.garbageservice.service.GarbageAccountService}.
 *
 * Notes:
 * - Base path: /garbage-accounts; documented in Swagger under tag "Garbage Account".
 * - open/_search throws INVALID_SEARCH when no mobile, application, property, oldGarbageId, or name is provided.
 * - /fetch without a valid path value returns HTTP 400.
 */
public class GarbageAccountController {

	@Autowired
	private GarbageAccountService service;

	@Operation(summary = "Create garbage account")
	@PostMapping("/_create")
	public ResponseEntity<GarbageAccountResponse> create(@RequestBody GarbageAccountRequest createGarbageRequest) {
		return ResponseEntity.ok(service.create(createGarbageRequest));
	}

	@Operation(summary = "Update garbage account")
	@PostMapping("/_update")
	public ResponseEntity<GarbageAccountResponse> update(@RequestBody GarbageAccountRequest createGarbageRequest) {
		return ResponseEntity.ok(service.update(createGarbageRequest));
	}

	@Operation(summary = "Search garbage accounts")
	@PostMapping("/_search")
	public ResponseEntity<GarbageAccountResponse> search(
			@RequestBody SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest, @RequestParam(name = "IsIndex", required = false, defaultValue = "false") Boolean IsIndex) {
	
			return ResponseEntity.ok(service.searchGarbageAccounts(searchCriteriaGarbageAccountRequest,IsIndex));
	}
	
	@Operation(summary = "Open search for garbage accounts")
	@PostMapping("/open/_search")
	public ResponseEntity<?> openSearch(
	        @RequestBody SearchCriteriaGarbageAccountRequest request,
	        @RequestParam(name = "IsIndex", required = false, defaultValue = "false") Boolean isIndex) {

		if (request.getRequestInfo() == null) {
		    RequestInfo requestInfo = new RequestInfo();
		    requestInfo.setApiId("open-search");
		    requestInfo.setVer("1.0");
		    requestInfo.setTs(System.currentTimeMillis());

		    User user = new User();
		    user.setType(GrbgConstants.USER_TYPE_CITIZEN);
		    user.setUuid("OPEN-SEARCH");
		    user.setRoles(Collections.emptyList());

		    requestInfo.setUserInfo(user);
		    request.setRequestInfo(requestInfo);
		}

		if (request.getSearchCriteriaGarbageAccount() == null) {
		    request.setSearchCriteriaGarbageAccount(new SearchCriteriaGarbageAccount());
		}

	    
	    SearchCriteriaGarbageAccount sc =
	            request.getSearchCriteriaGarbageAccount();


		if ((sc.getMobileNumber() == null || sc.getMobileNumber().isEmpty())
		        && (sc.getApplicationNumber() == null || sc.getApplicationNumber().isEmpty())
		        && (sc.getPropertyId() == null || sc.getPropertyId().isEmpty())
		        && (sc.getOldGarbageIds() == null || sc.getOldGarbageIds().isEmpty())
		        && (sc.getName() == null || sc.getName().isEmpty())) {
		
		    throw new CustomException(
		            "INVALID_SEARCH",
		            "Provide at least one of mobileNumber, applicationNumber, propertyId, oldGarbageIds or owner name"
		    );
		}
	    return ResponseEntity.ok(
	            service.openSearchPayPreview(request, isIndex)
	    );
	}



	@Operation(summary = "Fetch application details or actions")
	@PostMapping({ "/fetch", "/fetch/{value}" })
	public ResponseEntity<?> calculateTLFee(@RequestBody GarbageAccountActionRequest garbageAccountActionRequest,
			@PathVariable String value) {

		GarbageAccountActionResponse response = null;

		if (StringUtils.equalsIgnoreCase(value, "CALCULATEFEE")) {
			response = service.getApplicationDetails(garbageAccountActionRequest);
		} else if (StringUtils.equalsIgnoreCase(value, "ACTIONS")) {
			response = service.getActionsOnApplication(garbageAccountActionRequest);
		} else {
			return new ResponseEntity("Provide parameter to be fetched in URL.", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity(response, HttpStatus.OK);
	}

	@Operation(summary = "Pay garbage bill now")
	@PostMapping("/_payNow")
	public ResponseEntity<?> payNowGrbgBill(@RequestBody PayNowRequest payNowRequest) {

		GarbageAccountActionResponse response = null;
		response = service.payNowGrbgBill(payNowRequest,true);

		return new ResponseEntity(response, HttpStatus.OK);
	}
	
	@Operation(summary = "Create user for garbage account")
	@PostMapping("/_createUserForGarbage")
	public ResponseEntity<?> createUserForGarbage(@RequestBody SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {

		log.info("createGarbageUser {}",searchCriteriaGarbageAccountRequest);
		service.createUserForGarbage(searchCriteriaGarbageAccountRequest);

		return new ResponseEntity("User created for garbage account", HttpStatus.OK);
	}

	@Operation(summary = "Get total counts")
	@PostMapping("/_counts")

	public ResponseEntity<?> counts(@RequestBody TotalCountRequest totalCountRequest) {


		Map<String, Object> result = service.totalCount(totalCountRequest);

		return new ResponseEntity(result, HttpStatus.OK);

	}
	
	@Operation(summary = "Generate garbage tax bill receipt")
	@PostMapping("/_generateGrbgTaxBillReceipt")
	public ResponseEntity<?> generateGrbgTaxBillReceipt(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam String grbgId , @RequestParam String billid, @RequestParam String status ) {
		ResponseEntity<Resource> response = service.generateGrbgTaxBillReceipt(requestInfoWrapper, grbgId ,billid,status);

		return response;

	}
	
	@Operation(summary = "Update garbage account status")
	@PostMapping("/_update_status")
	public ResponseEntity<GarbageAccountResponse> updateStatus(
			@RequestBody GarbageAccountRequest createGarbageRequest) {
		return ResponseEntity.ok(service.updateStatus(createGarbageRequest));
	}
	
	@Operation(summary = "Create arrear for garbage account")
	@PostMapping("/_createArear")
	public ResponseEntity<Map<String, Object>> createArear(
			@Valid @RequestBody GenrateArrearRequest genrateArrearRequest) {
		return ResponseEntity.ok(service.generateArrear(genrateArrearRequest));
	}
}