package org.ksmart.birth.ksmartbirthapplication.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthapplication.model.birth.BirthApplicationSearchCriteria;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthAppliactionDetail;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthApplicationResponse;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthApplicationSearchCriteria;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthDetailsRequest;
import org.ksmart.birth.ksmartbirthapplication.service.KsmartBirthService;
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

    @Autowired
    KsmartBirthController(KsmartBirthService ksmartBirthService, ResponseInfoFactory responseInfoFactory) {
        this.ksmartBirthService=ksmartBirthService;
        this.responseInfoFactory=responseInfoFactory;
    }

    @PostMapping(value = {"/createbirth"})
    public ResponseEntity<?> saveRegisterBirthDetails(@RequestBody KsmartBirthDetailsRequest request) {
        List<KsmartBirthAppliactionDetail> registerBirthDetails=ksmartBirthService.saveKsmartBirthDetails(request);
        return new ResponseEntity<>(registerBirthDetails, HttpStatus.OK);
    }
    @PostMapping(value = { "/updatebirth"})
    public ResponseEntity<?> updateMarriageDetails(@RequestBody KsmartBirthDetailsRequest request) {
        List<KsmartBirthAppliactionDetail> registerBirthDetails = ksmartBirthService.updateKsmartBirthDetails(request);
        return new ResponseEntity<>(registerBirthDetails, HttpStatus.OK);
    }
    @PostMapping(value = {"/searchbirth"})
    public ResponseEntity<KsmartBirthApplicationResponse> listByHospitalId(@RequestBody KsmartBirthDetailsRequest request, @Valid @ModelAttribute KsmartBirthApplicationSearchCriteria criteria) {
        List<KsmartBirthAppliactionDetail> birthDetails=ksmartBirthService.searchKsmartBirthDetails(criteria);
        KsmartBirthApplicationResponse response=KsmartBirthApplicationResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        Boolean.TRUE))
                .ksmartBirthDetails(birthDetails)
                .build();

        return ResponseEntity.ok(response);
    }
}
