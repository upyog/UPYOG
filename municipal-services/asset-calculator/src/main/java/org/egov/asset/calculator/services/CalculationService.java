package org.egov.asset.calculator.services;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.config.CalculatorConfig;
import org.egov.asset.calculator.utils.CalculationUtils;
import org.egov.asset.calculator.web.models.CalculationReq;
import org.egov.asset.calculator.web.models.CalculationRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculationService {

    @Autowired
    private MDMSService mdmsService;

    @Autowired
    private CalculatorConfig config;

    @Autowired
    private CalculationUtils utils;

    @Autowired
    private ProcessDepreciation processDepreciation;

    CalculationRes calculationRes;


//	@Autowired

    /**
     * Calculates tax estimates and creates demand
     *
     * @param calculationReq The calculationCriteria request
     * @return List of calculations for all applicationNumbers or tradeLicenses in
     * calculationReq
     */
    public CalculationRes calculate(CalculationReq calculationReq) {
        String tenantId = calculationReq.getCalulationCriteria().getTenantId().split("\\.")[0];
        //Object mdmsData = mdmsService.mDMSCall(calculationReq, tenantId);
        String message = processDepreciation.executeSingleAndLegacyDepreciationProcedure(calculationReq.getCalulationCriteria().getTenantId(), calculationReq.getCalulationCriteria().getAssetId());
        calculationRes = CalculationRes.builder().message(message).build();
        return calculationRes;
    }

}
