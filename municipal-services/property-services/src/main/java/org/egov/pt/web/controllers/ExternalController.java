package org.egov.pt.web.controllers;

import javax.validation.Valid;

import org.egov.pt.web.contracts.PropertyExternalRequest;
import org.egov.pt.web.contracts.PropertyExternalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property-external")
public class ExternalController {

	@PostMapping("/_propertydetails")
	public ResponseEntity<PropertyExternalResponse> externalData(@Valid @RequestBody PropertyExternalRequest propertyExternalRequest)
	{
		return null;
	}
}
