package org.upyog.cdwm.calculator.service;

import java.math.BigDecimal;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.calculator.util.CalculatorConstants;
import org.upyog.cdwm.calculator.util.MdmsUtil;
import org.upyog.cdwm.calculator.web.models.CNDRequest;
import org.upyog.cdwm.calculator.web.models.CalculationType;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalculationService {

	@Autowired
	private MdmsUtil mdmsUtil;

	public BigDecimal calculateFee(CNDRequest cndRequest, RequestInfo requestInfo) {
		List<CalculationType> calculationTypes = mdmsUtil.getCalculationType(requestInfo, cndRequest.getTenantId(), CalculatorConstants.MDMS_MODULE_NAME );
				
	
		log.info("calculationTypes for cnd application : {}", calculationTypes);

		for (CalculationType calculation : calculationTypes) {
			if (calculation.getCode().equalsIgnoreCase(cndRequest.getVehicleType())) {
				return calculation.getAmount().multiply(BigDecimal.valueOf(cndRequest.getNoOfTrips()));
			}
		}
		throw new CustomException("FEE_NOT_FOUND", "Fee not found for application type: " + cndRequest.getVehicleType());
	}


}
