package org.egov.filemgmnt.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.egov.filemgmnt.service.ApplicantPersonalService;
import org.egov.filemgmnt.util.ResponseInfoFactory;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.ApplicantPersonalResponse;
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
public class ApplicantPersonalsController {

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    private final ApplicantPersonalService personalService;

    @Autowired
    public ApplicantPersonalsController(ApplicantPersonalService personalService) {

        this.personalService = personalService;
    }

    @PostMapping("/applicantpersonals/_create")
    public ResponseEntity<ApplicantPersonalResponse> create(@Valid @RequestBody ApplicantPersonalRequest request) {

        List<ApplicantPersonal> personals = personalService.create(request);

        ApplicantPersonalResponse response = ApplicantPersonalResponse.builder()
                                                                      .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                          Boolean.TRUE))
                                                                      .applicantPersonals(personals)
                                                                      .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/applicantpersonals/_update")
    public ResponseEntity<ApplicantPersonalResponse> update(@Valid @RequestBody ApplicantPersonalRequest request) {

        List<ApplicantPersonal> personals = personalService.update(request);

        ApplicantPersonalResponse response = ApplicantPersonalResponse.builder()
                                                                      .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                          Boolean.TRUE))
                                                                      .applicantPersonals(personals)
                                                                      .build();
        return ResponseEntity.ok(response);
    }
}
