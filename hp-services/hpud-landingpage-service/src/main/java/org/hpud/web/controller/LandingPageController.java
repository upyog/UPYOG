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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

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
	public ResponseEntity<?> fetchDesignation(HttpServletRequest requestDetails,@RequestBody LandingPageRequest request) {
		LandingPageRequest response = new LandingPageRequest();
		try {
			request.setIpAddress(getClientIp(requestDetails));
			request.setBrowserName(requestDetails.getHeader("User-Agent"));
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
	
	private String getClientIp(HttpServletRequest request) {
        // Check for X-Forwarded-For header (used by proxies/load balancers)
//		System.out.println(request.getHeader("X-Real-IP"));
        String ip = request.getRemoteAddr();
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // Fallback to remote address
            ip =  request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // Check for the X-Real-IP header (used by some proxies)
            ip = request.getHeader("X-Real-IP");
        }

        return ip;
    }
	
	@PostMapping(value = "/fetchRegistrationData")
	@ResponseBody
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
