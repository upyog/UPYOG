
package org.egov.filemgmnt.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.egov.filemgmnt.service.ApplicantServiceService;
import org.egov.filemgmnt.util.ResponseInfoFactory;
import org.egov.filemgmnt.web.models.ApplicantService;
import org.egov.filemgmnt.web.models.ApplicantServiceRequest;
import org.egov.filemgmnt.web.models.ApplicantServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Validated

public class ApplicantServiceController {

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    private final ApplicantServiceService serviceService;

    @Autowired
    public ApplicantServiceController(ApplicantServiceService serviceService) {

        this.serviceService = serviceService;
    }

    @PostMapping("/applicantservices/_create")
    public ResponseEntity<ApplicantServiceResponse> create(@Valid @RequestBody ApplicantServiceRequest request) {

        List<ApplicantService> services = serviceService.create(request);

        ApplicantServiceResponse response = ApplicantServiceResponse.builder()
                                                                    .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                        Boolean.TRUE))
                                                                    .applicantServices(services)
                                                                    .build();
        return ResponseEntity.ok(response);
    }

}