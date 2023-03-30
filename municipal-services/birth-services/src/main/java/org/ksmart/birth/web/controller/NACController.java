package org.ksmart.birth.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthnac.service.NacService;
import org.ksmart.birth.web.model.birthnac.NacApplication;
import org.ksmart.birth.web.model.birthnac.NacDetailRequest;
import org.ksmart.birth.web.model.birthnac.NacResponse;
import org.ksmart.birth.web.model.birthnac.NacSearchResponse;
import org.ksmart.birth.web.model.birthnac.NacSearchCriteria;
 

import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.birthnac.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cr")
public class NACController {
    private final ResponseInfoFactory responseInfoFactory;
    private final NacService nacService;
 
     
    @Autowired
    NACController(NacService nacService, ResponseInfoFactory responseInfoFactory) {
        this.nacService=nacService;
        this.responseInfoFactory=responseInfoFactory;
      
    }

    @PostMapping(value = {"/createnac"})
    public ResponseEntity<?> saveAdoptionDetails(@RequestBody NacDetailRequest request) {
        List<NacApplication> nacDetails=nacService.saveAdoptionDetails(request);
        NacResponse response= NacResponse.builder()
                                                                              .nacDetails(nacDetails)
                                                                              .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                    true))
                                                                              .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = { "/updatenac"})
    public ResponseEntity<?> updateRegisterBirthDetails(@RequestBody NacDetailRequest request) {
       
        List<NacApplication> nacApplicationDetails=nacService.updateAdoptionBirthDetails(request);
        //Download certificate when Approved
        if((nacApplicationDetails.get(0).getApplicationStatus() == "APPROVED" && nacApplicationDetails.get(0).getAction() == "APPROVE")){
           
          
            NacSearchCriteria criteria = new NacSearchCriteria();
            criteria.setTenantId(nacApplicationDetails.get(0).getTenantId());
            criteria.setRegistrationNo(nacApplicationDetails.get(0).getRegistrationNo());
//            nacCertificate = nacService.download(criteria,request);
        }
        NacResponse response=NacResponse.builder()
                .nacDetails(nacApplicationDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
//                .nacCertificate(nacCertificate)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//    @PostMapping(value = {"/searchadoption"})
//    public ResponseEntity<AdoptionSearchResponse> searchKsmartBirth(@RequestBody AdoptionDetailRequest request, @Valid @ModelAttribute SearchCriteria criteria) {
//        List<AdoptionApplication> adoptionDetails=adoptionService.searchKsmartBirthDetails(request, criteria);
//        AdoptionSearchResponse response=AdoptionSearchResponse.builder()
//                                                                              .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
//                                                                              .AdoptionDetails(adoptionDetails)
//                                                                              .build();
//
//
//        return ResponseEntity.ok(response);
//    }
}
