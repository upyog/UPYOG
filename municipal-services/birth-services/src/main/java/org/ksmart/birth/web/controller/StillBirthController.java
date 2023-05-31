package org.ksmart.birth.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.stillbirth.service.RegistryRequestServiceForStillBirth;
import org.ksmart.birth.stillbirth.service.StillBirthService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.stillbirth.StillBirthApplication;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.STATUS_APPROVED;
import static org.ksmart.birth.utils.BirthConstants.WF_APPROVE;

@Slf4j
@RestController
@RequestMapping("/cr")
public class StillBirthController {
    private final ResponseInfoFactory responseInfoFactory;
    private final StillBirthService stillBirthService;
    private final RegisterBirthService registerBirthService;
    private final RegistryRequestServiceForStillBirth registryReq;
    @Autowired
    StillBirthController(StillBirthService stillBirthService, ResponseInfoFactory responseInfoFactory,
                         RegisterBirthService registerBirthService, RegistryRequestServiceForStillBirth registryReq) {
        this.stillBirthService=stillBirthService;
        this.responseInfoFactory=responseInfoFactory;
        this.registerBirthService = registerBirthService;
        this.registryReq = registryReq;
    }

    @PostMapping(value = {"/createstillbirth"})
    public ResponseEntity<?> saveBirthDetails(@RequestBody StillBirthDetailRequest request) {
        List<StillBirthApplication> birthDetails=stillBirthService.saveBirthDetails(request);
        StillBirthResponse response= StillBirthResponse.builder()
                                                        .birthDetails(birthDetails)
                                                        .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),true))
                                                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = { "/updatestillbirth"})
    public ResponseEntity<?> updateBirthDetails(@RequestBody StillBirthDetailRequest request) {
        BirthCertificate birthCertificate = new BirthCertificate();
        List<StillBirthApplication> birthApplicationDetails=stillBirthService.updateBirthDetails(request);
        if(request.getBirthDetails().get(0).getIsWorkflow()) {
            if ((birthApplicationDetails.get(0).getApplicationStatus().equals(STATUS_APPROVED) && birthApplicationDetails.get(0).getAction().equals(WF_APPROVE))) {
                RegisterBirthDetailsRequest registerBirthDetailsRequest = registryReq.createRegistryRequestNew(request);
                if (registerBirthDetailsRequest.getRegisterBirthDetails().size() == 1) {
                    registerBirthService.saveRegisterBirthDetails(registerBirthDetailsRequest);
                }
            }
        }
        StillBirthResponse response=StillBirthResponse.builder()
                .birthDetails(birthApplicationDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = {"/searchstillbirth"})
    public ResponseEntity<StillBirthResponse> searchKsmartBirth(@RequestBody StillBirthDetailRequest request, @Valid @ModelAttribute SearchCriteria criteria) {
        StillBirthResponse response=stillBirthService.searchKsmartBirthDetails(request, criteria);
//        StillBirthResponse response=StillBirthResponse.builder()
//                                                      .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
//                                                      .birthDetails(birthDetails)
//                                                      .count(birthDetails.size())
//                                                      .build();
        return ResponseEntity.ok(response);
    }
}
