package org.egov.advertisementcanopy.controller;

import org.apache.commons.lang3.StringUtils;
import org.egov.advertisementcanopy.model.SiteBookingActionRequest;
import org.egov.advertisementcanopy.model.SiteBookingActionResponse;
import org.egov.advertisementcanopy.model.SiteBookingRequest;
import org.egov.advertisementcanopy.model.SiteBookingResponse;
import org.egov.advertisementcanopy.model.SiteBookingSearchRequest;
import org.egov.advertisementcanopy.service.SiteBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/site-booking")
public class SiteBookingController {

	@Autowired
	private SiteBookingService service;

	@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
	@PostMapping("/_create")
	public ResponseEntity<?> createBooking(@RequestBody SiteBookingRequest siteBookingRequest) {
		return new ResponseEntity<SiteBookingResponse>(service.createBooking(siteBookingRequest), HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
	@PostMapping("/_search")
	public ResponseEntity<?> searchBooking(@RequestBody SiteBookingSearchRequest siteBookingSearchRequest) {
		return new ResponseEntity<SiteBookingResponse>(service.searchBooking(siteBookingSearchRequest), HttpStatus.OK);
	}

	@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
	@PostMapping("/_update")
	public ResponseEntity<?> updateBooking(@RequestBody SiteBookingRequest siteBookingRequest) {
		return new ResponseEntity<SiteBookingResponse>(service.updateBooking(siteBookingRequest), HttpStatus.OK);
	}

    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
    @PostMapping({"/fetch","/fetch/{value}"})
    public ResponseEntity<?> calculateTLFee(@RequestBody SiteBookingActionRequest siteBookingActionRequest
    										, @PathVariable String value){
    	
    	SiteBookingActionResponse response = null;
    	
    	if(StringUtils.equalsIgnoreCase(value, "CALCULATEFEE")) {
    		response = service.getApplicationDetails(siteBookingActionRequest);
    	}else if(StringUtils.equalsIgnoreCase(value, "ACTIONS")){
    		response = service.getActionsOnApplication(siteBookingActionRequest);
    	}else {
    		return new ResponseEntity("Provide parameter to be fetched in URL.", HttpStatus.BAD_REQUEST);
    	}
    	
    	return new ResponseEntity(response, HttpStatus.OK);
    }
}