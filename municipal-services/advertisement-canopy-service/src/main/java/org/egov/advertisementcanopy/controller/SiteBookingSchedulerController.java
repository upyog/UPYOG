package org.egov.advertisementcanopy.controller;

import org.egov.advertisementcanopy.service.SiteBookingSchedulerService;
import org.egov.advertisementcanopy.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/site-booking-scheduler")
public class SiteBookingSchedulerController {

	@Autowired
	private SiteBookingSchedulerService service;

	@PostMapping("/change-site-status")
	public ResponseEntity<?> changeSiteStatus(@RequestBody RequestInfoWrapper requestInfoWrapper) {

		service.changeSiteStatus(requestInfoWrapper);

		return ResponseEntity.ok("Status of the Site changed successfully!!!");
//		return ResponseEntity.ok(service.changeSiteStatus(requestInfoWrapper));
	}

}