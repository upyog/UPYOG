package org.ksmart.birth.ksmartbirthapplication.controller;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthAppliactionDetail;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthDetailsRequest;
import org.ksmart.birth.ksmartbirthapplication.service.KsmartBirthService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class KsmartBirthController {
    private final ResponseInfoFactory responseInfoFactory;
    private final KsmartBirthService ksmartBirthService;

    @Autowired
    KsmartBirthController(KsmartBirthService ksmartBirthService, ResponseInfoFactory responseInfoFactory) {
        this.ksmartBirthService=ksmartBirthService;
        this.responseInfoFactory=responseInfoFactory;
    }


    @PostMapping(value = {"/_create"})
    public ResponseEntity<?> saveRegisterBirthDetails(@RequestBody KsmartBirthDetailsRequest request) {
        List<KsmartBirthAppliactionDetail> registerBirthDetails=ksmartBirthService.saveKsmartBirthDetails(request);
        return new ResponseEntity<>(registerBirthDetails, HttpStatus.OK);
    }
}
