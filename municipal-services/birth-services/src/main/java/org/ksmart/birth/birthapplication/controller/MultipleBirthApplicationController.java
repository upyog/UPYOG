package org.ksmart.birth.birthapplication.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
import org.ksmart.birth.birthapplication.model.birth.BirthApplicationResponse;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.birthapplication.service.MultipleBirthApplicationService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/cr/multi")
public class MultipleBirthApplicationController {

    private final ResponseInfoFactory responseInfoFactory;
    private final MultipleBirthApplicationService multipleBirthService;

    @Autowired
    MultipleBirthApplicationController(MultipleBirthApplicationService multipleBirthService, ResponseInfoFactory responseInfoFactory) {
        this.multipleBirthService = multipleBirthService;
        this.responseInfoFactory = responseInfoFactory;
    }

    @PostMapping(value = { "/_create"})
    public ResponseEntity<BirthApplicationResponse> saveBirthDetails(@RequestBody BirthDetailsRequest request) {
        List<BirthApplicationDetail> birthDetails = multipleBirthService.saveBirthDetails(request);
        BirthApplicationResponse response = BirthApplicationResponse.builder().birthDetails(birthDetails).responseInfo(
                        responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
