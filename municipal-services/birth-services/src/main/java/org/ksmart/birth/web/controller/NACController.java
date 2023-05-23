package org.ksmart.birth.web.controller;

import lombok.extern.slf4j.Slf4j;

 
import org.ksmart.birth.birthnac.service.NacService;
import org.ksmart.birth.birthnacregistry.service.RegisterNacService;
import org.ksmart.birth.birthnac.service.RegistryRequestServiceForNac;
import org.ksmart.birth.birthnacregistry.model.NacCertificate;
import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.ksmart.birth.birthnacregistry.model.RegisterNacRequest;
import org.ksmart.birth.birthnacregistry.model.RegisterNacSearchCriteria;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.web.model.birthnac.NacApplication;
import org.ksmart.birth.web.model.birthnac.NacDetailRequest;
import org.ksmart.birth.web.model.birthnac.NacResponse;
import org.ksmart.birth.web.model.birthnac.NacSearchResponse;
import org.ksmart.birth.web.model.birthnac.NacSearchCriteria;

import org.ksmart.birth.web.model.birthnac.certificate.CertificateResponse;
import org.ksmart.birth.web.model.birthnac.certificate.CertificateDetails;
 

import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.birthnac.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.ksmart.birth.utils.BirthConstants.STATUS_APPROVED;
import static org.ksmart.birth.utils.BirthConstants.WF_APPROVE;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cr")
public class NACController {
    private final ResponseInfoFactory responseInfoFactory;
    private final NacService nacService;
    private final RegistryRequestServiceForNac registryReq;
    private final RegisterNacService  registerNacService;
     
    @Autowired
    NACController(NacService nacService, ResponseInfoFactory responseInfoFactory,RegistryRequestServiceForNac registryReq,RegisterNacService  registerNacService) {
        this.nacService=nacService;
        this.responseInfoFactory=responseInfoFactory;
        this.registryReq=registryReq;
        this.registerNacService=registerNacService;
      
    }

    @PostMapping(value = {"/createnac"})
    public ResponseEntity<?> saveAdoptionDetails(@RequestBody NacDetailRequest request) {
        List<NacApplication> nacDetails=nacService.saveNacDetails(request);
         
        NacResponse response= NacResponse.builder()
                                                                              .nacDetails(nacDetails)
                                                                              .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                    true))
                                                                              .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = { "/updatenac"})
    public ResponseEntity<?> updateRegisterBirthDetails(@RequestBody NacDetailRequest request) {
    	 NacCertificate nacCertificate = new NacCertificate();
        List<NacApplication> nacApplicationDetails=nacService.updateNacDetails(request);
        //Download certificate when Approved       
      
        	 if((nacApplicationDetails.get(0).getApplicationStatus().equals(STATUS_APPROVED)  && nacApplicationDetails.get(0).getAction().equals(WF_APPROVE))){
        	 System.out.println("nia testing");
        	RegisterNacRequest registerNacRequest = registryReq.createRegistryRequestNew(request);
        	   List<RegisterNac> registerBirthDetails =  registerNacService.saveRegisterBirthDetails(registerNacRequest);
        	   
//        	   RegisterNacSearchCriteria criteria = new RegisterNacSearchCriteria();
//            criteria.setTenantId(nacApplicationDetails.get(0).getTenantId());
//            criteria.setRegistrationNo(nacApplicationDetails.get(0).getRegistrationNo());
//            	nacCertificate = registerNacService.download(criteria,request.getRequestInfo());
        }
        NacResponse response=NacResponse.builder()
                .nacDetails(nacApplicationDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .nacCertificate(nacCertificate)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping(value = {"/searchnac"})
    public ResponseEntity<NacSearchResponse> searchKsmartBirth(@RequestBody NacDetailRequest request, @Valid @ModelAttribute SearchCriteria criteria) {
        List<NacApplication> nacDetails=nacService.searchNacDetails(request, criteria);
        NacSearchResponse response=NacSearchResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                .nacDetails(nacDetails)
                .count(nacDetails.size())
                .build();
        return ResponseEntity.ok(response);
    }
}
