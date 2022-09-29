package org.egov.filemgmnt.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.egov.filemgmnt.service.ServiceDetailsService;
import org.egov.filemgmnt.util.ResponseInfoFactory;
import org.egov.filemgmnt.web.models.ServiceDetails;
import org.egov.filemgmnt.web.models.ServiceDetailsRequest;
import org.egov.filemgmnt.web.models.ServiceDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Validated

public class ServiceDetailsController {

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    private final ServiceDetailsService serviceService;

    @Autowired
    public ServiceDetailsController(ServiceDetailsService serviceService) {

        this.serviceService = serviceService;
    }

    @PostMapping("/applicantservices/_create")
    public ResponseEntity<ServiceDetailsResponse> create(@Valid @RequestBody ServiceDetailsRequest request) {

        List<ServiceDetails> services = serviceService.create(request);

        ServiceDetailsResponse response = ServiceDetailsResponse.builder()
                                                                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                    Boolean.TRUE))
                                                                .serviceDetailsreq(services)
                                                                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/applicantservices/_update")
    public ResponseEntity<ServiceDetailsResponse> update(@Valid @RequestBody ServiceDetailsRequest request) {

        List<ServiceDetails> services = serviceService.update(request);

        ServiceDetailsResponse response = ServiceDetailsResponse.builder()
                                                                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                    Boolean.TRUE))
                                                                .serviceDetailsreq(services)
                                                                .build();
        return ResponseEntity.ok(response);
    }

}
