package org.ksmart.birth.birthcorrection.controller;


import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthcorrection.service.BirthCorrectionService;
import org.ksmart.birth.crbirth.model.BirthApplicationResponse;
import org.ksmart.birth.crbirth.model.BirthApplicationSearchCriteria;
import org.ksmart.birth.crbirth.model.BirthDetail;
import org.ksmart.birth.crbirth.model.BirthDetailsRequest;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cr/birth/correction")
public class BirthCorrectionController {

    private final ResponseInfoFactory responseInfoFactory;
    private final BirthCorrectionService birthCorrectionService;

    @Autowired
    BirthCorrectionController(BirthCorrectionService birthCorrectionService, ResponseInfoFactory responseInfoFactory) {
        this.birthCorrectionService = birthCorrectionService;
        this.responseInfoFactory = responseInfoFactory;
    }

    @PostMapping(value = { "/_create"})
    public ResponseEntity<?> saveBirthDetails(@RequestBody BirthDetailsRequest request) {
        List<BirthDetail> birthDetails = birthCorrectionService.saveBirthDetails(request);
        return new ResponseEntity<>(birthDetails, HttpStatus.OK);
    }

    @PostMapping(value = { "/_update"})
    public ResponseEntity<?> updateBirthDetails(@RequestBody BirthDetailsRequest request) {
        List<BirthDetail> birthDetails = birthCorrectionService.updateBirthDetails(request);
        return new ResponseEntity<>(birthDetails, HttpStatus.OK);
    }

    @PostMapping(value = { "/_search"})
    public ResponseEntity<BirthApplicationResponse> listByHospitalId(@RequestBody BirthDetailsRequest request,
                                              @Valid @ModelAttribute BirthApplicationSearchCriteria criteria) {
        List<BirthDetail> birthDetails = birthCorrectionService.searchBirthDetails(criteria);
        BirthApplicationResponse response = BirthApplicationResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        Boolean.TRUE))
                .birthDetails(birthDetails)
                .build();
        return ResponseEntity.ok(response);
    }

}
