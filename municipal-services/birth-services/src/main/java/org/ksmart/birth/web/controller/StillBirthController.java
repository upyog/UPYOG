package org.ksmart.birth.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.newbirth.service.RegistryRequestService;
import org.ksmart.birth.stillbirth.service.RegistryRequestServiceForStillBirth;
import org.ksmart.birth.stillbirth.service.StillBirthService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.stillbirth.StillBirthApplication;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cr")
public class StillBirthController {
    private final ResponseInfoFactory responseInfoFactory;
    private final StillBirthService stillBirthService;
    private final RegisterBirthService registerBirthService;
    private final RegistryRequestServiceForStillBirth registryReq;
    @Autowired
    StillBirthController(StillBirthService stillBirthService, ResponseInfoFactory responseInfoFactory,
                         RegisterBirthService registerBirthService, RegistryRequestServiceForStillBirth registryReq) {
        this.stillBirthService=stillBirthService;
        this.responseInfoFactory=responseInfoFactory;
        this.registerBirthService = registerBirthService;
        this.registryReq = registryReq;
    }

    @PostMapping(value = {"/createstillbirth"})
    public ResponseEntity<?> saveBirthDetails(@RequestBody StillBirthDetailRequest request) {
        List<StillBirthApplication> birthDetails=stillBirthService.saveBirthDetails(request);
        StillBirthResponse response= StillBirthResponse.builder()
                                                        .birthDetails(birthDetails)
                                                        .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),true))
                                                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = { "/updatestillbirth"})
    public ResponseEntity<?> updateBirthDetails(@RequestBody StillBirthDetailRequest request) {
        BirthCertificate birthCertificate = new BirthCertificate();
        List<StillBirthApplication> birthApplicationDetails=stillBirthService.updateBirthDetails(request);
        //Download certificate when Approved
        if((birthApplicationDetails.get(0).getApplicationStatus().equals("APPROVED") && birthApplicationDetails.get(0).getAction().equals("APPROVE"))){
            RegisterBirthDetailsRequest registerBirthDetailsRequest = registryReq.createRegistryRequest(request);
            List<RegisterBirthDetail> registerBirthDetails =  registerBirthService.saveRegisterBirthDetails(registerBirthDetailsRequest);
//            RegisterBirthSearchCriteria criteria = new RegisterBirthSearchCriteria();
//            criteria.setTenantId(registerBirthDetails.get(0).getTenantId());
//            criteria.setRegistrationNo(registerBirthDetails.get(0).getRegistrationNo());
//            birthCertificate = registerBirthService.download(criteria,request.getRequestInfo());
        }
        StillBirthResponse response=StillBirthResponse.builder()
                .birthDetails(birthApplicationDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = {"/searchstillbirth"})
    public ResponseEntity<StillBirthResponse> searchKsmartBirth(@RequestBody StillBirthDetailRequest request, @Valid @ModelAttribute SearchCriteria criteria) {
        List<StillBirthApplication> birthDetails=stillBirthService.searchKsmartBirthDetails(request, criteria);
        StillBirthResponse response=StillBirthResponse.builder()
                                                      .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                                                      .birthDetails(birthDetails)
                                                      .build();
        return ResponseEntity.ok(response);
    }
}
