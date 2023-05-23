package org.ksmart.birth.web.controller.bornoutside;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.bornoutside.service.RegistryRequestServiceForBirthOutside;
import org.ksmart.birth.bornoutside.service.BornOutsideService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.ksmart.birth.web.model.bornoutside.BornOutsideDetailRequest;
import org.ksmart.birth.web.model.bornoutside.BornOutsideResponse;
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
public class BornOutsideController {
    private final ResponseInfoFactory responseInfoFactory;
    private final BornOutsideService birthService;
    private final RegisterBirthService registerBirthService;
    private final RegistryRequestServiceForBirthOutside registryReq;

    @Autowired
    BornOutsideController(BornOutsideService birthService, ResponseInfoFactory responseInfoFactory,
                          RegisterBirthService registerBirthService, RegistryRequestServiceForBirthOutside registryReq) {
        this.birthService = birthService;
        this.responseInfoFactory = responseInfoFactory;
        this.registerBirthService = registerBirthService;
        this.registryReq = registryReq;
    }

    @PostMapping(value = {"/createbornoutside"})
    public ResponseEntity<?> saveBirthApplication (@RequestBody BornOutsideDetailRequest request) {
        List<BornOutsideApplication> registerBirthDetails = birthService.saveBirthApplication(request);
        BornOutsideResponse response = BornOutsideResponse.builder()
                .birthDetails(registerBirthDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = {"/updatebornoutside"})
    public ResponseEntity<?> updateBirthApplication(@RequestBody BornOutsideDetailRequest request) {
        BirthCertificate birthCertificate = new BirthCertificate();
        List<BornOutsideApplication> birthApplicationDetails = birthService.updateBirthApplication(request);

        //Save details to register when Approved
        if ((birthApplicationDetails.get(0).getApplicationStatus().equals(STATUS_APPROVED) && birthApplicationDetails.get(0).getAction().equals(WF_APPROVE))) {
            RegisterBirthDetailsRequest registerBirthDetailsRequest = registryReq.createRegistryRequestNew(request);
            registerBirthService.saveRegisterBirthDetails(registerBirthDetailsRequest);
        }
        BornOutsideResponse response = BornOutsideResponse.builder()
                .birthDetails(birthApplicationDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = {"/searchbornoutside"})
    public ResponseEntity<BornOutsideResponse> searchKsmartBirth(@RequestBody BornOutsideDetailRequest request, @Valid @ModelAttribute SearchCriteria criteria) {
        BornOutsideResponse response = birthService.searchBirthDetails(request, criteria);
//        BornOutsideResponse response = BornOutsideResponse.builder()
//                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
//                .birthDetails(birthDetails)
//                .count(birthDetails.size())
//                .build();
        return ResponseEntity.ok(response);
    }
}
