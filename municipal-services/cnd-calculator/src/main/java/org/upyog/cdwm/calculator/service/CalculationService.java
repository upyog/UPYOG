package org.upyog.cdwm.calculator.service;

import java.math.BigDecimal;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.calculator.util.CalculatorConstants;
import org.upyog.cdwm.calculator.util.MdmsUtil;
import org.upyog.cdwm.calculator.web.models.CNDApplicationDetail;
import org.upyog.cdwm.calculator.web.models.CalculationType;


import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for calculating the fee for CND (Construction and Demolition) waste applications.
 * It retrieves the calculation type from MDMS and applies the relevant fee calculation.
 */

@Slf4j
@Service
public class CalculationService {

	@Autowired
	private MdmsUtil mdmsUtil;

	/**
     * Calculates the fee for the given CND request based on the waste quantity and predefined fee structure.
     *
     * @param cndApplicationDetail  The request object containing details like tenant ID and total waste quantity.
     * @param requestInfo The request information containing metadata.
     * @return The calculated fee as a BigDecimal.
     * @throws CustomException If the fee calculation type is not found in MDMS.
     */
	
	public BigDecimal calculateFee(CNDApplicationDetail cndApplicationDetail, RequestInfo requestInfo) {
		List<CalculationType> calculationTypes = mdmsUtil.getCalculationType(requestInfo, cndApplicationDetail.getTenantId(),
				CalculatorConstants.MDMS_MODULE_NAME);

		log.info("calculationTypes for cnd application : {}", calculationTypes);

		if (calculationTypes == null || calculationTypes.isEmpty()) {
            throw new CustomException("FEE_NOT_FOUND",
                "Fee not found for per metric ton: " + cndApplicationDetail.getTotalWasteQuantity());
        }

        CalculationType calculation = calculationTypes.get(0);
        log.info("Calculation total waste quantity {} " + cndApplicationDetail.getTotalWasteQuantity());
        log.info("Calculation amount {} " + calculation.getAmount());
        return calculation.getAmount().multiply(cndApplicationDetail.getTotalWasteQuantity());
    }


}
