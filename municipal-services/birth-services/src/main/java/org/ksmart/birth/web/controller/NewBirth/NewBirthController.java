package org.ksmart.birth.web.controller.NewBirth;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.newbirth.service.NewBirthService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthResponse;
import org.ksmart.birth.web.model.newbirth.NewBirthSearchResponse;
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
public class NewBirthController {
    private final ResponseInfoFactory responseInfoFactory;
    private final NewBirthService ksmartBirthService;
    private final RegisterBirthService registerBirthService;
    @Autowired
    NewBirthController(NewBirthService ksmartBirthService, ResponseInfoFactory responseInfoFactory,
                       RegisterBirthService registerBirthService) {
        this.ksmartBirthService=ksmartBirthService;
        this.responseInfoFactory=responseInfoFactory;
        this.registerBirthService = registerBirthService;
    }

    @PostMapping(value = {"/createbirth"})
    public ResponseEntity<?> saveRegisterBirthDetails(@RequestBody NewBirthDetailRequest request) {
        List<NewBirthApplication> registerBirthDetails=ksmartBirthService.saveKsmartBirthDetails(request);
        NewBirthResponse response= NewBirthResponse.builder()
                                                   .ksmartBirthDetails(registerBirthDetails)
                                                   .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                                                   .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = { "/updatebirth"})
    public ResponseEntity<?> updateRegisterBirthDetails(@RequestBody NewBirthDetailRequest request) {
        BirthCertificate birthCertificate = new BirthCertificate();
        List<NewBirthApplication> birthApplicationDetails=ksmartBirthService.updateBirthDetails(request);

        //Download certificate when Approved
        //if(request.getNewBirthDetails().get(0).getIsWorkflow()) {
            if ((birthApplicationDetails.get(0).getApplicationStatus().equals(STATUS_APPROVED) && birthApplicationDetails.get(0).getAction().equals(WF_APPROVE))) {
                       RegisterBirthDetailsRequest registerBirthDetailsRequest = ksmartBirthService.createRegistryRequest(request);
                if (registerBirthDetailsRequest.getRegisterBirthDetails().size() == 1) {
                    registerBirthService.saveRegisterBirthDetails(registerBirthDetailsRequest);
                }
            }
        //}
        NewBirthResponse response=NewBirthResponse.builder()
                .ksmartBirthDetails(birthApplicationDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .birthCertificate(birthCertificate)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = {"/searchbirth"})
    public ResponseEntity<NewBirthSearchResponse> searchKsmartBirth(@RequestBody NewBirthDetailRequest request, @Valid @ModelAttribute SearchCriteria criteria) {
        List<NewBirthApplication> birthDetails=ksmartBirthService.searchBirthDetails(request, criteria);
        NewBirthSearchResponse response=NewBirthSearchResponse.builder()
                                                              .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                                                              .newBirthDetails(birthDetails)
                                                              .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = {"/searchbirthcommon"})
    public ResponseEntity<NewBirthSearchResponse> searchbirthCommon(@RequestBody NewBirthDetailRequest request, @Valid @ModelAttribute SearchCriteria criteria) {
        List<NewBirthApplication> birthDetails=ksmartBirthService.searchBirthDetailsCommon(request, criteria);
        NewBirthSearchResponse response=NewBirthSearchResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                .newBirthDetails(birthDetails)
                .build();
        return ResponseEntity.ok(response);
    }
}
