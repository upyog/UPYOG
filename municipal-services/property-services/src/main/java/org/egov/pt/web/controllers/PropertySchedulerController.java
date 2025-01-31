package org.egov.pt.web.controllers;

import java.util.List;

import org.egov.pt.models.Property;
import org.egov.pt.service.PropertySchedulerService;
import org.egov.pt.service.PropertyService;
import org.egov.pt.util.ResponseInfoFactory;
import org.egov.pt.web.contracts.PropertyResponse;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property-scheduler")
public class PropertySchedulerController {

	@Autowired
	private PropertySchedulerService service;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	private PropertyService propertyService;

	@PostMapping("/tax-calculator")
	public ResponseEntity<?> taxCalculator(@RequestBody RequestInfoWrapper requestInfoWrapper) {

//		List<Property> properties = service.calculateTax(requestInfoWrapper);
//
//		PropertyResponse response = PropertyResponse
//				.builder().responseInfo(responseInfoFactory
//						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
//				.properties(properties).count(properties.size()).build();
//
//		propertyService.setAllCount(properties, response);

		return new ResponseEntity<>(service.calculateTax(requestInfoWrapper), HttpStatus.OK);

//		return ResponseEntity.ok("Tax calculated successfully!!!");
	}

}
