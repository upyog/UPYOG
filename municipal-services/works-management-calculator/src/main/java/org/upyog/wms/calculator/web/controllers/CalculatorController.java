package org.upyog.wms.calculator.web.controllers;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.wms.calculator.service.CalculationService;
import org.upyog.wms.calculator.util.ResponseInfoFactory;
import org.upyog.wms.calculator.web.models.Calculation;
import org.upyog.wms.calculator.web.models.CalculationReq;
import org.upyog.wms.calculator.web.models.CalculationRes;
import org.upyog.wms.calculator.web.models.GetBillCriteria;
import org.upyog.wms.calculator.web.models.RequestInfoWrapper;
import org.upyog.wms.calculator.web.models.demand.Demand;
import org.upyog.wms.calculator.web.models.demand.DemandResponse;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculationService calculationService;
    private final ResponseInfoFactory responseInfoFactory;

    /** Financial summary and fees, without creating anything. */
    @PostMapping("/_estimate")
    public ResponseEntity<CalculationRes> estimate(@Valid @RequestBody CalculationReq request) {
        return ok(request, calculationService.estimate(request));
    }

    /** Financial summary and fees; creates / updates the billing demand when a fee is due. */
    @PostMapping("/_calculate")
    public ResponseEntity<CalculationRes> calculate(@Valid @RequestBody CalculationReq request) {
        return ok(request, calculationService.calculate(request));
    }

    /** Re-applies the current fee configuration to existing demands of the given application numbers. */
    @PostMapping("/_updatedemand")
    public ResponseEntity<DemandResponse> updateDemand(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                       @Valid @ModelAttribute GetBillCriteria criteria) {
        List<Demand> demands = calculationService.updateDemand(requestInfoWrapper.getRequestInfo(), criteria);
        DemandResponse response = DemandResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(
                        requestInfoWrapper.getRequestInfo(), true))
                .demands(demands)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<CalculationRes> ok(CalculationReq request, List<Calculation> calculations) {
        CalculationRes response = CalculationRes.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .calculations(calculations)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
