package org.ksmart.birth.birthapplication.controller;


import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthapplication.model.birth.BirthApplicationResponse;
import org.ksmart.birth.birthapplication.model.birth.BirthApplicationSearchCriteria;
import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.birthapplication.service.BirthApplicationService;
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
public class BirthApplicationController {

    private final ResponseInfoFactory responseInfoFactory;
    private final BirthApplicationService crBirthService;

    @Autowired
    BirthApplicationController(BirthApplicationService crBirthService, ResponseInfoFactory responseInfoFactory) {
        this.crBirthService=crBirthService;
        this.responseInfoFactory=responseInfoFactory;
    }

    @PostMapping(value = {"/_create"})
    public ResponseEntity<BirthApplicationResponse> saveBirthDetails(@RequestBody BirthDetailsRequest request) {
         System.out.println("controller Request"+ request.getRequestInfo());
        List<BirthApplicationDetail> birthDetails=crBirthService.saveBirthDetails(request);
        BirthApplicationResponse response=BirthApplicationResponse.builder()
                                                                  .birthDetails(birthDetails)
                                                                  .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                                 true))
                                                                 .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = {"/_update"})
    public ResponseEntity<BirthApplicationResponse> updateBirthDetails(@RequestBody BirthDetailsRequest request) {
        List<BirthApplicationDetail> birthDetails=crBirthService.updateBirthDetails(request);
        BirthApplicationResponse response=BirthApplicationResponse.builder()
                                                                  .birthDetails(birthDetails)
                                                                  .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                                 true))
                                                                 .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = {"/_search"})
    public ResponseEntity<BirthApplicationResponse> listByHospitalId(@RequestBody BirthDetailsRequest request, @Valid @ModelAttribute BirthApplicationSearchCriteria criteria) {
        List<BirthApplicationDetail> birthDetails=crBirthService.searchBirthDetails(criteria);
        BirthApplicationResponse response=BirthApplicationResponse.builder()
                                                                  .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                                 Boolean.TRUE))
                                                                  .birthDetails(birthDetails)
                                                                  .build();
        return ResponseEntity.ok(response);
    }

}
