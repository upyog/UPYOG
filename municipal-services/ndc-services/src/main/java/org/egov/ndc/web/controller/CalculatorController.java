package org.egov.ndc.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.ndc.config.ResponseInfoFactory;
import org.egov.ndc.service.CalculationService;
import org.egov.ndc.service.DemandService;
import org.egov.ndc.web.model.calculator.Calculation;
import org.egov.ndc.web.model.calculator.CalculationReq;
import org.egov.ndc.web.model.calculator.CalculationRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/ndc")
public class CalculatorController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private CalculationService calculationService;
    @Autowired
    private DemandService demandService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    public CalculatorController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @RequestMapping(value = "/v1/_calculate", method = RequestMethod.POST)
    public ResponseEntity<CalculationRes> v1CalculatePost(@ApiParam(value = "required parameters have to be populated", required = true) @Valid @RequestBody CalculationReq calculationReq) {
        List<Calculation> calculations = calculationService.calculate(calculationReq);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(calculationReq.getRequestInfo(),true);
        CalculationRes calculationRes = CalculationRes.builder().responseInfo(responseInfo).calculation(calculations).build();
        return new ResponseEntity<CalculationRes>(calculationRes, HttpStatus.OK);
    }
}
