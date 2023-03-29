package org.ksmart.birth.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.correction.service.CorrectionBirthService;
import org.ksmart.birth.correction.service.RegistryRequestServiceCorrection;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.correction.CorrectionApplication;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.ksmart.birth.web.model.correction.CorrectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.STATUS_APPROVED;
import static org.ksmart.birth.utils.BirthConstants.WF_APPROVE;

@Slf4j
@RestController
@RequestMapping("/cr")
public class CorrectionController {
    private final ResponseInfoFactory responseInfoFactory;
    private final CorrectionBirthService correctionService;
    private final RegisterBirthService registerBirthService;
    private final RegistryRequestServiceCorrection registryReq;
    @Autowired
    CorrectionController(CorrectionBirthService correctionService, ResponseInfoFactory responseInfoFactory,
                       RegisterBirthService registerBirthService, RegistryRequestServiceCorrection registryReq) {
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

    @PostMapping(value = { "/updatebirthcorrection"})
    public ResponseEntity<?> updateRegisterBirthDetails(@RequestBody CorrectionRequest request) {
        BirthCertificate birthCertificate = new BirthCertificate();
        List<CorrectionApplication> birthApplicationDetails=correctionService.updateKsmartBirthDetails(request);
        //Download certificate when Approved
        if((birthApplicationDetails.get(0).getApplicationStatus() == STATUS_APPROVED && birthApplicationDetails.get(0).getAction() == WF_APPROVE)){
            RegisterBirthDetailsRequest registerBirthDetailsRequest = registryReq.createRegistryRequest(request);
            List<RegisterBirthDetail> registerBirthDetails =  registerBirthService.updateRegisterBirthDetails(registerBirthDetailsRequest);

            //Dowload after update
//            RegisterBirthSearchCriteria criteria = new RegisterBirthSearchCriteria();
//            criteria.setTenantId(registerBirthDetails.get(0).getTenantId());
//            criteria.setRegistrationNo(registerBirthDetails.get(0).getRegistrationNo());
//            birthCertificate = registerBirthService.download(criteria,request.getRequestInfo());

        }
        CorrectionResponse response=CorrectionResponse.builder()
                .correctionDetails(birthApplicationDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .birthCertificate(birthCertificate)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
