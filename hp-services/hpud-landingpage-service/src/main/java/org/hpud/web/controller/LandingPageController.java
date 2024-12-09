package org.hpud.web.controller;

import org.hpud.errorhandlers.HPUDLandingPageException;
import org.hpud.model.LandingPageRequest;
import org.hpud.model.RegistrationRequest;
import org.hpud.model.RegistrationResponse;
import org.hpud.service.LandingPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@RequestMapping("/landing")
@Slf4j
 
public class LandingPageController {
	
	@Autowired
	LandingPageService service;
	
	@PostMapping(value = "/getLandingPageCount")
	@ResponseBody
	public ResponseEntity<?> fetchDesignation(@RequestBody LandingPageRequest request) {
		LandingPageRequest response = new LandingPageRequest();
		try {
			service.fetchCount(request,response);
			response.setStatus("SUCCESS");
			response.setMsg("Landing Page count Fetched successfully!!!");
		} catch (HPUDLandingPageException e) {
			response.setMsg(e.getMessage());
			response.setStatus("ERROR");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(value = "/fetchRegistrationData")
	@ResponseBody
	@CrossOrigin(origins = "http://localhost:3000"
	, allowedHeaders = "*", allowCredentials = "true")
	public ResponseEntity<?> fetchRegistrationData(@RequestBody RegistrationRequest request) {
		RegistrationResponse response = new RegistrationResponse();
		try {
			response = service.fetchRegistrationData(request,response);
			response.setStatus("SUCCESS");
			response.setMsg("Landing Page count Fetched successfully!!!");
		} catch (HPUDLandingPageException e) {
			response.setMsg(e.getMessage());
			response.setStatus("ERROR");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
