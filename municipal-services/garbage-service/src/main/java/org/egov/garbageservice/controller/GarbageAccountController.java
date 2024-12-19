package org.egov.garbageservice.controller;

import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.model.GarbageAccountActionRequest;
import org.egov.garbageservice.model.GarbageAccountActionResponse;
import org.egov.garbageservice.model.GarbageAccountRequest;
import org.egov.garbageservice.model.GarbageAccountResponse;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccountRequest;
import org.egov.garbageservice.service.GarbageAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping("/_payNow")
	public ResponseEntity<?> payNowGrbgBill(@RequestParam(value = "id", required = true) String encryptedUserUuid) {

		GarbageAccountActionResponse response = null;
		response = service.payNowGrbgBill(encryptedUserUuid);

		return new ResponseEntity(response, HttpStatus.OK);
	}

}
