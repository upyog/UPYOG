package org.ksmart.birth.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.newbirth.service.NewBirthService;
import org.ksmart.birth.newbirth.service.RegistryRequestService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthResponse;
import org.ksmart.birth.web.model.newbirth.NewBirthSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ksmart/birth")
public class StillBirthController {
    private final ResponseInfoFactory responseInfoFactory;
    private final NewBirthService ksmartBirthService;
    private final RegisterBirthService registerBirthService;
    private final RegistryRequestService registryReq;
    @Autowired
    StillBirthController(NewBirthService ksmartBirthService, ResponseInfoFactory responseInfoFactory,
                         RegisterBirthService registerBirthService, RegistryRequestService registryReq) {
        this.ksmartBirthService=ksmartBirthService;
        this.responseInfoFactory=responseInfoFactory;
        this.registerBirthService = registerBirthService;
        this.registryReq = registryReq;
    }

    @PostMapping(value = {"/createadoption"})
    public ResponseEntity<?> saveRegisterBirthDetails(@RequestBody NewBirthDetailRequest request) {
        List<NewBirthApplication> registerBirthDetails=ksmartBirthService.saveKsmartBirthDetails(request);
        NewBirthResponse response= NewBirthResponse.builder()
                                                                              .ksmartBirthDetails(registerBirthDetails)
                                                                              .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                    true))
                                                                              .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = { "/updateadoption"})
    public ResponseEntity<?> updateRegisterBirthDetails(@RequestBody NewBirthDetailRequest request) {
        BirthCertificate birthCertificate = new BirthCertificate();
        List<NewBirthApplication> birthApplicationDetails=ksmartBirthService.updateKsmartBirthDetails(request);
        //Download certificate when Approved
        if((birthApplicationDetails.get(0).getApplicationStatus() == "APPROVED" && birthApplicationDetails.get(0).getAction() == "APPROVE")){
            RegisterBirthDetailsRequest registerBirthDetailsRequest = registryReq.createRegistryRequest(request);
            List<RegisterBirthDetail> registerBirthDetails =  registerBirthService.saveRegisterBirthDetails(registerBirthDetailsRequest);
            RegisterBirthSearchCriteria criteria = new RegisterBirthSearchCriteria();
            criteria.setTenantId(registerBirthDetails.get(0).getTenantId());
            criteria.setRegistrationNo(registerBirthDetails.get(0).getRegistrationNo());
            birthCertificate = registerBirthService.download(criteria,request.getRequestInfo());
        }
        NewBirthResponse response=NewBirthResponse.builder()
                .ksmartBirthDetails(birthApplicationDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .birthCertificate(birthCertificate)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = {"/searchadoption"})
    public ResponseEntity<NewBirthSearchResponse> searchKsmartBirth(@RequestBody NewBirthDetailRequest request, @Valid @ModelAttribute SearchCriteria criteria) {
        List<NewBirthApplication> birthDetails=ksmartBirthService.searchKsmartBirthDetails(request, criteria);
        NewBirthSearchResponse response=NewBirthSearchResponse.builder()
                                                                              .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                                                                              .newBirthDetails(birthDetails)
                                                                              .build();


        return ResponseEntity.ok(response);
    }
}
