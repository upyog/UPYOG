package org.egov.asset.calculator.services;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.web.models.CalculationReq;
import org.egov.asset.calculator.web.models.CalculationRes;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculationService {

    private final ProcessDepreciationV2 depreciationServiceV2;

    public CalculationService(ProcessDepreciationV2 depreciationServiceV2) {
        this.depreciationServiceV2 = depreciationServiceV2;
    }

    /**
     * Calculates tax estimates and creates demand
     *
     * @param calculationReq The calculationCriteria request
     * @return List of calculations for all applicationNumbers or tradeLicenses in
     * calculationReq
     */
    public CalculationRes calculate(CalculationReq calculationReq) {
        String message = depreciationServiceV2.calculateDepreciation(
                calculationReq.getCalulationCriteria().getTenantId(),
                calculationReq.getCalulationCriteria().getAssetId(),
                false,
                calculationReq.getRequestInfo().getUserInfo().getUuid());
        return CalculationRes.builder().message(message).build();
    }
}
