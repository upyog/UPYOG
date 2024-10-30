package org.egov.rentlease.controller;


import org.egov.rentlease.model.RentLeaseCreationRequest;
import org.egov.rentlease.model.RentLeaseCreationResponse;
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
	RentLeaseService 	rentLeaseService;
	
	@PostMapping("/_create")
	public ResponseEntity<RentLeaseCreationResponse> create(@RequestBody RentLeaseCreationRequest rentLeaseCreateRequest) {
		return ResponseEntity.ok(rentLeaseService.create(rentLeaseCreateRequest));
	}

}
