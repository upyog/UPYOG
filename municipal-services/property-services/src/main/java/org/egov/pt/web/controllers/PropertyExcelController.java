package org.egov.pt.web.controllers;

import java.util.List;

import org.egov.pt.models.Property;
import org.egov.pt.service.PropertyExcelService;
import org.egov.pt.service.PropertyService;
import org.egov.pt.web.contracts.PropertyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/property-excel")
public class PropertyExcelController {

	@Autowired
	private PropertyExcelService propertyExcelService;

	@Autowired
	private PropertyService propertyService;

	@PostMapping("/_create")
	public ResponseEntity<PropertyResponse> createFromExcel(@RequestParam MultipartFile file) {

		List<Property> properties = propertyExcelService.createFromExcel(file);

		PropertyResponse response = PropertyResponse.builder().properties(properties).build();

		propertyService.setAllCount(properties, response);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
