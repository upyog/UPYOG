package org.upyog.rs.service;

import java.math.BigDecimal;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.util.MdmsUtil;
import org.upyog.rs.util.RequestServiceUtil;
import org.upyog.rs.web.models.billing.CalculationType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalculationService {

	@Autowired
	private MdmsUtil mdmsUtil;

	public BigDecimal calculateFee(int tankerQuantity, String tankerType, RequestInfo requestInfo, String tenantId) {
		List<CalculationType> calculationTypes = mdmsUtil.getCalculationType(requestInfo,RequestServiceUtil.extractTenantId(tenantId),
				RequestServiceConstants.MDMS_MODULE_NAME);
		
		log.info("calculationTypes for tanker booking : {}", calculationTypes);

		for (CalculationType calculation : calculationTypes) {
			if (calculation.getCode().equalsIgnoreCase(tankerType)) {
				return calculation.getAmount().multiply(BigDecimal.valueOf(tankerQuantity));
			}
		}
		throw new CustomException("FEE_NOT_FOUND", "Fee not found for application type: " + tankerType);
	}

}
