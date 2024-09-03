package org.egov.advertisementcanopy.controller;

import org.egov.advertisementcanopy.model.SiteBookingRequest;
import org.egov.advertisementcanopy.model.SiteBookingResponse;
import org.egov.advertisementcanopy.model.SiteBookingSearchRequest;
import org.egov.advertisementcanopy.service.SiteBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
}