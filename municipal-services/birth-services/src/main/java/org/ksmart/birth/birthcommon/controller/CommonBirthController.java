package org.ksmart.birth.birthcommon.controller;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthcommon.model.common.CommonPay;
import org.ksmart.birth.birthcommon.model.common.CommonPayRequest;
import org.ksmart.birth.birthcommon.model.common.CommonPayResponse;
import org.ksmart.birth.birthcommon.services.CommonService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cr/common")
public class CommonBirthController {
    private final CommonService commonService;
    private final ResponseInfoFactory responseInfoFactory;
    @Autowired
    CommonBirthController(CommonService commonService, ResponseInfoFactory responseInfoFactory){
        this.commonService = commonService;
        this.responseInfoFactory = responseInfoFactory;
    }

    @PostMapping(value = {"/updatepaywf"})
    public ResponseEntity<?> updatePaymentWorkflow(@RequestBody CommonPayRequest request) {
        List<CommonPay> paymentDetails=commonService.updatePaymentWorkflow(request);
        CommonPayResponse response= CommonPayResponse.builder()
                .payments(paymentDetails)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                        true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = {"/searchbirthcommon"})
    public ResponseEntity<NewBirthSearchResponse> searchbirthCommon(@RequestBody NewBirthDetailRequest request, @Valid @ModelAttribute SearchCriteria criteria) {
        List<NewBirthApplication> birthDetails=commonService.searchBirthDetailsCommon(request, criteria);
        NewBirthSearchResponse response=NewBirthSearchResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                .newBirthDetails(birthDetails)
                .build();
        return ResponseEntity.ok(response);
    }

}
