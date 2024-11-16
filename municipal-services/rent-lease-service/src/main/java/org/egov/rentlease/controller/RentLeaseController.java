package org.egov.rentlease.controller;


import org.egov.rentlease.model.RentLeaseCreationRequest;
import org.egov.rentlease.model.RentLeaseCreationResponse;
import org.egov.rentlease.model.RentLeaseSearchRequest;
import org.egov.rentlease.model.RentLeaseSearchResponse;
import org.egov.rentlease.service.RentLeaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class RentLeaseController {
	
	@Autowired
	RentLeaseService rentLeaseService;
	
	@PostMapping("/_create")
	public ResponseEntity<RentLeaseCreationResponse> create(@RequestBody RentLeaseCreationRequest rentLeaseCreateRequest) {
		return ResponseEntity.ok(rentLeaseService.create(rentLeaseCreateRequest));
	}
	
	@PostMapping("/_search")
	public ResponseEntity<RentLeaseCreationResponse> search(@RequestBody RentLeaseSearchRequest rentLeaseSearchRequest) {
		return ResponseEntity.ok(rentLeaseService.search(rentLeaseSearchRequest));
	}
	
	@PostMapping("/_update")
	public ResponseEntity<RentLeaseCreationResponse> update(@RequestBody RentLeaseCreationRequest rentLeaseCreateRequest) {
		return ResponseEntity.ok(rentLeaseService.update(rentLeaseCreateRequest));
	}

}
