package org.ksmart.birth.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.adoption.service.AdoptionService;
import org.ksmart.birth.adoption.service.RegistryRequestServiceForAdoption;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.adoption.AdoptionApplication;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.ksmart.birth.web.model.adoption.AdoptionResponse;
import org.ksmart.birth.web.model.adoption.AdoptionSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.STATUS_APPROVED;
import static org.ksmart.birth.utils.BirthConstants.WF_APPROVE;
import static org.ksmart.birth.utils.enums.ErrorCodes.REQUIRED;

@Slf4j
@RestController
@RequestMapping("/cr")
public class AdoptionController {
    private final ResponseInfoFactory responseInfoFactory;
    private final AdoptionService adoptionService;
    private final RegisterBirthService registerBirthService;
    private final RegistryRequestServiceForAdoption registryReq;
    @Autowired
    AdoptionController(AdoptionService adoptionService, ResponseInfoFactory responseInfoFactory,
                       RegisterBirthService registerBirthService, RegistryRequestServiceForAdoption registryReq) {
        this.adoptionService=adoptionService;
        this.responseInfoFactory=responseInfoFactory;
        this.registerBirthService = registerBirthService;
        this.registryReq = registryReq;
    }

    @PostMapping(value = {"/createadoption"})
    public ResponseEntity<?> saveAdoptionDetails(@RequestBody AdoptionDetailRequest request) {
        List<AdoptionApplication> registerAdoptionDetails=adoptionService.saveAdoptionDetails(request);
        AdoptionResponse response= AdoptionResponse.builder()
                                                                              .adoptionDetails(registerAdoptionDetails)
                                                                              .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                    true))
                                                                              .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = { "/updateadoption"})
    public ResponseEntity<?> updateRegisterBirthDetails(@RequestBody AdoptionDetailRequest request) {
        BirthCertificate birthCertificate = new BirthCertificate();
        List<AdoptionApplication> adoptionApplicationDetails=adoptionService.updateAdoptionBirthDetails(request);
        //Download certificate when Approved
        
        if(request.getAdoptionDetails().get(0).getIsWorkflow()) {
        	System.out.println("status  :"+adoptionApplicationDetails.get(0).getApplicationStatus());
        	System.out.println("status  :"+adoptionApplicationDetails.get(0).getAction());
        if((adoptionApplicationDetails.get(0).getApplicationStatus().equals(STATUS_APPROVED)  && adoptionApplicationDetails.get(0).getAction().equals(WF_APPROVE))){
            RegisterBirthDetailsRequest registerBirthDetailsRequest = registryReq.createRegistryRequestNew(request);
            System.out.println("status  :"+registerBirthDetailsRequest.getRegisterBirthDetails().size());
            if (registerBirthDetailsRequest.getRegisterBirthDetails().size() == 1) {
                registerBirthService.saveRegisterBirthDetails(registerBirthDetailsRequest);
            }
            
            
//            List<RegisterBirthDetail> registerBirthDetails =  registerBirthService.saveRegisterBirthDetails(registerBirthDetailsRequest);
//            RegisterBirthSearchCriteria criteria = new RegisterBirthSearchCriteria();
//            criteria.setTenantId(registerBirthDetails.get(0).getTenantId());
//            criteria.setRegistrationNo(registerBirthDetails.get(0).getRegistrationNo());
//            birthCertificate = registerBirthService.download(criteria,request.getRequestInfo());
         }
        }
        AdoptionResponse response=AdoptionResponse.builder()
                .adoptionDetails(adoptionApplicationDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .birthCertificate(birthCertificate)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = {"/searchadoption"})
    public ResponseEntity<AdoptionSearchResponse> searchKsmartBirth(@RequestBody AdoptionDetailRequest request, @Valid @ModelAttribute SearchCriteria criteria) {
        List<AdoptionApplication> adoptionDetails=adoptionService.searchKsmartBirthDetails(request, criteria);
        AdoptionSearchResponse response=AdoptionSearchResponse.builder()
                                                                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                                                                .AdoptionDetails(adoptionDetails)
                                                                .count(adoptionDetails.size())
                                                                .build();


        return ResponseEntity.ok(response);
    }
}
