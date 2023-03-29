package org.ksmart.birth.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.correction.service.CorrectionBirthService;
import org.ksmart.birth.newbirth.service.NewBirthService;
import org.ksmart.birth.newbirth.service.RegistryRequestService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.correction.CorrectionApplication;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.ksmart.birth.web.model.correction.CorrectionResponse;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthResponse;
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
@RequestMapping("/cr")
public class CorrectionController {
    private final ResponseInfoFactory responseInfoFactory;
    private final CorrectionBirthService correctionService;
    private final RegisterBirthService registerBirthService;
    private final RegistryRequestService registryReq;
    @Autowired
    CorrectionController(CorrectionBirthService correctionService, ResponseInfoFactory responseInfoFactory,
                       RegisterBirthService registerBirthService, RegistryRequestService registryReq) {
        this.correctionService=correctionService;
        this.responseInfoFactory=responseInfoFactory;
        this.registerBirthService = registerBirthService;
        this.registryReq = registryReq;
    }

    @PostMapping(value = {"/createbirthcorrection"})
    public ResponseEntity<?> saveCorrectionBirthDetails(@RequestBody CorrectionRequest request) {
        List<CorrectionApplication> birthDetails=correctionService.saveCorrectionBirthDetails(request);
        CorrectionResponse response= CorrectionResponse.builder()
                .correctionDetails(birthDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
