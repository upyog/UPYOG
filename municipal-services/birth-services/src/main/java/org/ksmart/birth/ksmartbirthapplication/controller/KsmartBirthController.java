package org.ksmart.birth.ksmartbirthapplication.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
import org.ksmart.birth.birthapplication.model.birth.BirthApplicationResponse;
import org.ksmart.birth.birthapplication.model.birth.BirthApplicationSearchCriteria;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.birthapplication.service.RegistryRequestService;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.*;
import org.ksmart.birth.ksmartbirthapplication.service.KsmartBirthService;
import org.ksmart.birth.ksmartbirthapplication.service.KsmartRegistryRequestService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ksmart/birth")
public class KsmartBirthController {
    private final ResponseInfoFactory responseInfoFactory;
    private final KsmartBirthService ksmartBirthService;
    private final RegisterBirthService registerBirthService;
    private final KsmartRegistryRequestService registryReq;
    private final RegistryRequestService registryRequestService;




    @Autowired
    KsmartBirthController(KsmartBirthService ksmartBirthService, ResponseInfoFactory responseInfoFactory,
                          RegisterBirthService registerBirthService, KsmartRegistryRequestService registryReq,
                          RegistryRequestService registryRequestService) {
        this.ksmartBirthService=ksmartBirthService;
        this.responseInfoFactory=responseInfoFactory;
        this.registerBirthService = registerBirthService;
        this.registryReq = registryReq;
        this.registryRequestService = registryRequestService;
    }

    @PostMapping(value = {"/createbirth"})
    public ResponseEntity<?> saveRegisterBirthDetails(@RequestBody KsmartBirthDetailsRequest request) {
        List<KsmartBirthAppliactionDetail> registerBirthDetails=ksmartBirthService.saveKsmartBirthDetails(request);
        KsmartBirthApplicationResponse response=KsmartBirthApplicationResponse.builder()
                                                                              .ksmartBirthDetails(registerBirthDetails)
                                                                              .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                    true))
                                                                              .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = { "/updatebirth"})
    public ResponseEntity<?> updateMarriageDetails(@RequestBody KsmartBirthDetailsRequest request) {
        BirthCertificate birthCertificate = new BirthCertificate();
        List<KsmartBirthAppliactionDetail> birthApplicationDetails=ksmartBirthService.updateKsmartBirthDetails(request);
        //Download certificate when Approved
        if((birthApplicationDetails.get(0).getStatus() == "APPROVED" && birthApplicationDetails.get(0).getAction() == "APPROVE")){
            RegisterBirthDetailsRequest registerBirthDetailsRequest = registryReq.createRegistryRequest(request);
            List<RegisterBirthDetail> registerBirthDetails =  registerBirthService.saveRegisterBirthDetails(registerBirthDetailsRequest);
            RegisterBirthSearchCriteria criteria = new RegisterBirthSearchCriteria();
            criteria.setTenantId(registerBirthDetails.get(0).getTenantId());
            System.out.println(criteria.getTenantId());

            criteria.setRegistrationNo(registerBirthDetails.get(0).getRegistrationNo()); System.out.println(registerBirthDetails.get(0).getRegistrationNo());
            birthCertificate = registerBirthService.download(criteria,request.getRequestInfo());
        }
        KsmartBirthApplicationResponse response=KsmartBirthApplicationResponse.builder()
                .ksmartBirthDetails(birthApplicationDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .birthCertificate(birthCertificate)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);

//
//
//
//
//
//
//        List<KsmartBirthAppliactionDetail> registerBirthDetails = ksmartBirthService.updateKsmartBirthDetails(request);
//        KsmartBirthApplicationResponse response=KsmartBirthApplicationResponse.builder()
//                                                                              .ksmartBirthDetails(registerBirthDetails)
//                                                                              .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),true))
//                                                                              .build();
//        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = {"/searchbirth"})
    public ResponseEntity<KsmartBirthApplicationSearchResponse> listByHospitalId(@RequestBody KsmartBirthDetailsRequest request, @Valid @ModelAttribute KsmartBirthApplicationSearchCriteria criteria) {
        List<KsmartBirthAppliactionDetail> birthDetails=ksmartBirthService.searchKsmartBirthDetails(criteria);
        KsmartBirthApplicationSearchResponse response=KsmartBirthApplicationSearchResponse.builder()
                                                                              .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                                                                              .ksmartBirthDetails(birthDetails)
                                                                              .build();


        return ResponseEntity.ok(response);
    }
}
