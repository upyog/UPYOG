package org.ksmart.birth.birthregistry.controller;

import lombok.extern.slf4j.Slf4j;

import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.common.contract.RequestInfoWrapper;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cr/registry")
public class RegistryBirthController {

    private final ResponseInfoFactory responseInfoFactory;
    private final RegisterBirthService registerBirthService;

    @Autowired
    RegistryBirthController(RegisterBirthService registerBirthService, ResponseInfoFactory responseInfoFactory) {
        this.registerBirthService=registerBirthService;
        this.responseInfoFactory=responseInfoFactory;
    }
    @PostMapping(value = {"/_create"})
    public ResponseEntity<?> saveRegisterBirthDetails(@RequestBody RegisterBirthDetailsRequest request) {
        List<RegisterBirthDetail> registerBirthDetails=registerBirthService.saveRegisterBirthDetails(request);
        return new ResponseEntity<>(registerBirthDetails, HttpStatus.OK);
    }
    @PostMapping(value = {"/_update"})
    public ResponseEntity<?> updateRegisterBirthDetails(@RequestBody RegisterBirthDetailsRequest request) {
        List<RegisterBirthDetail> registerBirthDetails=registerBirthService.updateRegisterBirthDetails(request);
        return new ResponseEntity<>(registerBirthDetails, HttpStatus.OK);
    }
    @PostMapping(value = {"/_search"})
    public ResponseEntity<RegisterBirthResponse> listByHospitalId(@RequestBody RequestInfoWrapper request, @Valid @ModelAttribute RegisterBirthSearchCriteria criteria) {

        List<RegisterBirthDetail> registerBirthDetail=registerBirthService.searchRegisterBirthDetails(criteria);
        RegisterBirthResponse response=RegisterBirthResponse.builder()
                                                            .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                                                            .registerDetails(registerBirthDetail)
                                                            .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping(value = {"/_searchAdoption"})
    public ResponseEntity<NewBirthSearchResponse> getCertDataAdoption(@RequestBody RequestInfoWrapper request, @Valid @ModelAttribute RegisterBirthSearchCriteria criteria) {

        List<NewBirthApplication> registerBirthDetail=registerBirthService.searchRegisterBirthDetailsAdoption(criteria);
        NewBirthSearchResponse response=NewBirthSearchResponse.builder()
                                                            .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                                                            .newBirthDetails(registerBirthDetail)
                                                            .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping(value = {"/searchcert"})
    public ResponseEntity<RegisterCertificateRespose> getCertData(@RequestBody RequestInfoWrapper request, @Valid @ModelAttribute RegisterBirthSearchCriteria criteria) {

        List<RegisterCertificateData> registerBirthDetail=registerBirthService.searchRegisterForCert(criteria, request.getRequestInfo());
        RegisterCertificateRespose response=RegisterCertificateRespose.builder()
                                                                      .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                                                                      .registerBirthCerts(registerBirthDetail)
                                                                      .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping(value = {"/_download"})
    public ResponseEntity<BirthCertResponse> download(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute RegisterBirthSearchCriteria criteria) {
        BirthCertificate birthCert=registerBirthService.download(criteria, requestInfoWrapper.getRequestInfo());
        BirthCertResponse response;
        List<BirthCertificate> birthCertificates = new ArrayList<>();
        birthCertificates.add(birthCert);
        response=BirthCertResponse.builder()
                                  .birthCertificates(birthCertificates)
                                  .tenantId(birthCert.getTenantId())
                                  .filestoreId(birthCert.getFilestoreid())
                                  .consumerCode(birthCert.getApplicationNumber())
                                  .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                                  .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

