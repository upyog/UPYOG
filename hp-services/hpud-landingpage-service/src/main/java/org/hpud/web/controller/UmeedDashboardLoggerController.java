package org.hpud.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.hpud.entity.UmeedDashboardLogger;
import org.hpud.model.UmeedLogRequest;
import org.hpud.model.UmeedLogResponse;
import org.hpud.service.UmeedDashboardLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
 


@RestController
@RequestMapping("/umeed-dashboard-logger")
public class UmeedDashboardLoggerController {
	
	@Autowired
	UmeedDashboardLogService service;
	
	@PostMapping(value = "/create")
	@ResponseBody
	public ResponseEntity<?> createLog(@RequestBody UmeedLogRequest request) {
		
	    UmeedDashboardLogger savedLog = service.saveLog(request);

	    if (savedLog == null) {
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new UmeedLogResponse("FAILED", "Failed to save log"));
	    }
		UmeedLogResponse response = new UmeedLogResponse();
		response.setStatus("SUCCESS");
		response.setMessage("Log saved successfully");
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	

}
