package org.egov.ndc.web.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.ndc.config.ResponseInfoFactory;
import org.egov.ndc.service.CalculationService;
import org.egov.ndc.web.model.calculator.Calculation;
import org.egov.ndc.web.model.calculator.CalculationReq;
import org.egov.ndc.web.model.calculator.CalculationRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/ndc")
public class CalculatorController {

    private final CalculationService calculationService;
    private final ResponseInfoFactory responseInfoFactory;

    public CalculatorController(CalculationService calculationService, ResponseInfoFactory responseInfoFactory) {
        this.calculationService = calculationService;
        this.responseInfoFactory = responseInfoFactory;
    }

    @PostMapping("/v1/_calculate")
    public ResponseEntity<CalculationRes> v1CalculatePost(
            @Schema(description = "required parameters have to be populated") @Valid @RequestBody CalculationReq calculationReq) {
        List<Calculation> calculations = calculationService.calculate(calculationReq);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(calculationReq.getRequestInfo(), true);
        CalculationRes calculationRes = CalculationRes.builder().responseInfo(responseInfo).calculation(calculations).build();
        return new ResponseEntity<>(calculationRes, HttpStatus.OK);
    }
}
