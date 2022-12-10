package org.ksmart.birth.birthregistry.controller;

import lombok.extern.slf4j.Slf4j;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthResponse;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/cr/registry")
public class RegistryController {

    private final ResponseInfoFactory responseInfoFactory;
    private final RegisterBirthService registerBirthService;

    @Autowired
    RegistryController(RegisterBirthService registerBirthService, ResponseInfoFactory responseInfoFactory) {
        this.registerBirthService = registerBirthService;
        this.responseInfoFactory = responseInfoFactory;
    }


        @PostMapping(value = { "/_create"})
        public ResponseEntity<?> saveRegisterBirthDetails(@RequestBody RegisterBirthDetailsRequest request) {
            List<RegisterBirthDetail> registerBirthDetails = registerBirthService.saveRegisterBirthDetails(request);
            return new ResponseEntity<>(registerBirthDetails, HttpStatus.OK);
        }

        @PostMapping(value = { "/_update"})
        public ResponseEntity<?> updateRegisterBirthDetails(@RequestBody RegisterBirthDetailsRequest request) {
            List<RegisterBirthDetail> registerBirthDetails = registerBirthService.updateRegisterBirthDetails(request);
            return new ResponseEntity<>(registerBirthDetails, HttpStatus.OK);
        }

    @PostMapping(value = { "/_search"})
    public ResponseEntity<RegisterBirthResponse> listByHospitalId(@RequestBody RegisterBirthDetailsRequest request,
                                                                     @Valid @ModelAttribute RegisterBirthSearchCriteria criteria) {
        List<RegisterBirthDetail> registerBirthDetail = registerBirthService.searchRegisterBirthDetails(criteria);
        RegisterBirthResponse response = RegisterBirthResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),Boolean.TRUE))
                .registerDetails(registerBirthDetail)
                .build();
        return ResponseEntity.ok(response);
    }

    }

