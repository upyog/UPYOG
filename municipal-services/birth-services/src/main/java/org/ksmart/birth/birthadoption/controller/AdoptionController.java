package org.ksmart.birth.birthadoption.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthadoption.model.AdoptionDetail;
import org.ksmart.birth.birthadoption.model.adoption.AdoptionRequest;
import org.ksmart.birth.birthadoption.service.AdoptionService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/cr/adoption")
public class AdoptionController {
    private final AdoptionService adoptionService;
    private final ResponseInfoFactory responseInfoFactory;

    @Autowired
    AdoptionController(AdoptionService adoptionService, ResponseInfoFactory responseInfoFactory) {
        this.adoptionService = adoptionService;
        this.responseInfoFactory = responseInfoFactory;
    }

    @PostMapping(value = { "/_create"})
    public ResponseEntity<?> saveAdoptionDetails(@RequestBody AdoptionRequest request) {
        List<AdoptionDetail> adoptionDetails = adoptionService.saveAdoptionDetails(request);
        return new ResponseEntity<>(adoptionDetails, HttpStatus.OK);
    }

    @PostMapping(value = { "/_update"})
    public ResponseEntity<?> updateRegisterBirthDetails(@RequestBody AdoptionRequest request) {
        List<AdoptionDetail> registerBirthDetails = adoptionService.updateAdoptionDetails(request);
        return new ResponseEntity<>(registerBirthDetails, HttpStatus.OK);
    }
//
//    @PostMapping(value = { "/_search"})
//    public ResponseEntity<RegisterBirthResponse> listByHospitalId(@RequestBody RegisterBirthDetailsRequest request,
//                                                                  @Valid @ModelAttribute RegisterBirthSearchCriteria criteria) {
//        List<RegisterBirthDetail> registerBirthDetail = adoptionService.searchRegisterBirthDetails(criteria);
//        RegisterBirthResponse response = RegisterBirthResponse.builder()
//                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),Boolean.TRUE))
//                .registerDetails(registerBirthDetail)
//                .build();
//        return ResponseEntity.ok(response);
//    }
}
