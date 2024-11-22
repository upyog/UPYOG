package org.egov.schedulerservice.controller;

import org.egov.schedulerservice.dto.SchedularMasterRequest;
import org.egov.schedulerservice.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class SchedulerController {

    @Autowired
    private SchedulerService schedulerService;

    @PostMapping("/trigger")
    public ResponseEntity<Object> getSignature(@RequestBody SchedularMasterRequest schedularMasterRequest) {
        
    	Object response = schedulerService.runService(schedularMasterRequest);
    	
    	return new ResponseEntity<>(response, HttpStatus.OK);
    }
}