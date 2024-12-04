package org.egov.hrms.web.controller;

import org.egov.hrms.service.MsevaSsoService;
import org.egov.hrms.web.contract.AuthenticateUserInputRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/employees/sso")
public class MsevaSsoWebController {
	
    @Autowired
    private MsevaSsoService msevaSsoService;
    
    
    @PostMapping("/authenticate-user")
    public String generateSSOUrl(@RequestBody AuthenticateUserInputRequest authenticateUserInputRequest) {
        return msevaSsoService.generateSsoUrl(authenticateUserInputRequest);
    }
}
