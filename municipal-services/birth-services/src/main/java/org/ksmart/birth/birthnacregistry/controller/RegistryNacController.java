package org.ksmart.birth.birthnacregistry.controller;
import lombok.extern.slf4j.Slf4j;

import org.ksmart.birth.birthnacregistry.model.NacCertificate;
import org.ksmart.birth.birthnacregistry.model.RegisterNacSearchCriteria;
import org.ksmart.birth.birthnacregistry.service.RegisterNacService;
import org.ksmart.birth.birthregistry.model.*; 
import org.ksmart.birth.common.contract.RequestInfoWrapper;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cr/registrynac")
public class RegistryNacController {

	 private final ResponseInfoFactory responseInfoFactory;
	    private final RegisterNacService registerNacService;
	    
	    @Autowired
	    RegistryNacController(RegisterNacService registerNacService, ResponseInfoFactory responseInfoFactory) {
	        this.registerNacService=registerNacService;
	        this.responseInfoFactory=responseInfoFactory;
	    }
	    
	    @PostMapping(value = {"/_download"})
	    public ResponseEntity<BirthCertResponse> download(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute RegisterNacSearchCriteria criteria) {
	        NacCertificate nacCert=registerNacService.download(criteria, requestInfoWrapper.getRequestInfo());
	        BirthCertResponse response;
	        response=BirthCertResponse.builder()
	                .filestoreId(nacCert.getFilestoreid())
	                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
	                .build();
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
}
