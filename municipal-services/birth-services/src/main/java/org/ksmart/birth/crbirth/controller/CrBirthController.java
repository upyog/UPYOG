package org.ksmart.birth.crbirth.controller;


import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.common.contract.BirthResponse;
import org.ksmart.birth.crbirth.model.BirthApplicationResponse;
import org.ksmart.birth.crbirth.model.BirthApplicationSearchCriteria;
import org.ksmart.birth.crbirth.model.BirthDetail;
import org.ksmart.birth.crbirth.model.BirthDetailsRequest;
import org.ksmart.birth.crbirth.service.CrBirthService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cr/birth")
public class CrBirthController {

    private final ResponseInfoFactory responseInfoFactory;
    private final CrBirthService crBirthService;

    @Autowired
    CrBirthController(CrBirthService crBirthService, ResponseInfoFactory responseInfoFactory) {
        this.crBirthService = crBirthService;
        this.responseInfoFactory = responseInfoFactory;
    }

    @PostMapping(value = { "/_create"})
    public ResponseEntity<BirthApplicationResponse> saveBirthDetails(@RequestBody BirthDetailsRequest request) {
        List<BirthDetail> birthDetails = crBirthService.saveBirthDetails(request);
        BirthApplicationResponse response = BirthApplicationResponse.builder().birthDetails(birthDetails).responseInfo(
                        responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = { "/_update"})
    public ResponseEntity<?> updateBirthDetails(@RequestBody BirthDetailsRequest request) {
        List<BirthDetail> birthDetails = crBirthService.updateBirthDetails(request);
        return new ResponseEntity<>(birthDetails, HttpStatus.OK);
    }

    @PostMapping(value = { "/_search"})
    public ResponseEntity<BirthApplicationResponse> listByHospitalId(@RequestBody BirthDetailsRequest request,
                                              @Valid @ModelAttribute BirthApplicationSearchCriteria criteria) {
        List<BirthDetail> birthDetails = crBirthService.searchBirthDetails(criteria);
        BirthApplicationResponse response = BirthApplicationResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        Boolean.TRUE))
                .birthDetails(birthDetails)
                .build();
        return ResponseEntity.ok(response);
    }

}
