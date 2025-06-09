package org.egov.garbageservice.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.garbageservice.model.GarbageAccountActionRequest;
import org.egov.garbageservice.model.GarbageAccountActionResponse;
import org.egov.garbageservice.model.GarbageAccountRequest;
import org.egov.garbageservice.model.GarbageAccountResponse;
import org.egov.garbageservice.model.PayNowRequest;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccountRequest;
import org.egov.garbageservice.model.TotalCountRequest;
import org.egov.garbageservice.service.GarbageAccountService;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/garbage-accounts")
public class GarbageAccountController {

	@Autowired
	private GarbageAccountService service;

	@PostMapping("/_create")
	public ResponseEntity<GarbageAccountResponse> create(@RequestBody GarbageAccountRequest createGarbageRequest) {
		return ResponseEntity.ok(service.create(createGarbageRequest));
	}

	@PostMapping("/_update")
	public ResponseEntity<GarbageAccountResponse> update(@RequestBody GarbageAccountRequest createGarbageRequest) {
		return ResponseEntity.ok(service.update(createGarbageRequest));
	}

	@PostMapping("/_search")
	public ResponseEntity<GarbageAccountResponse> search(
			@RequestBody SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
		return ResponseEntity.ok(service.searchGarbageAccounts(searchCriteriaGarbageAccountRequest));
	}

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

	@PostMapping("/_payNow")
	public ResponseEntity<?> payNowGrbgBill(@RequestBody PayNowRequest payNowRequest) {

		GarbageAccountActionResponse response = null;
		response = service.payNowGrbgBill(payNowRequest);

		return new ResponseEntity(response, HttpStatus.OK);
	}
	
	@PostMapping("/_createUserForGarbage")
	public ResponseEntity<?> createUserForGarbage(@RequestBody SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {

		service.createUserForGarbage(searchCriteriaGarbageAccountRequest);

		return new ResponseEntity("User created for garbage account", HttpStatus.OK);
	}

	@PostMapping("/_counts")

	public ResponseEntity<?> counts(@RequestBody TotalCountRequest totalCountRequest) {


		Map<String, Object> result = service.totalCount(totalCountRequest);

		return new ResponseEntity(result, HttpStatus.OK);

	}
	
	@PostMapping("/_generateGrbgTaxBillReceipt")
	public ResponseEntity<?> generateGrbgTaxBillReceipt(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper, @RequestParam String grbgId) {
		ResponseEntity<Resource> response = service.generateGrbgTaxBillReceipt(requestInfoWrapper,grbgId);

		return response;

	}
}
