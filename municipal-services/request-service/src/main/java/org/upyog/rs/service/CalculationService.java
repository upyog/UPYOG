package org.upyog.rs.service;

import java.math.BigDecimal;
import java.time.LocalDate;
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

	public CalculationType mtCalculateFee(int noOfMobileToilet, LocalDate deliveryFromDate, LocalDate deliveryToDate, RequestInfo requestInfo, String tenantId) {
		List<CalculationType> calculationTypes = mdmsUtil.getMTCalculationType(requestInfo,RequestServiceUtil.extractTenantId(tenantId),
				RequestServiceConstants.MDMS_MODULE_NAME);

		if (calculationTypes.isEmpty()) {
			log.info("No calculationTypes found for mobile toilet booking.");
			throw new CustomException("FEE_NOT_FOUND", "Fee not found for application type: " + noOfMobileToilet);
		}

		log.info("calculationTypes for mobile Toilet booking : {}", calculationTypes);
		long numberOfDays = deliveryFromDate.until(deliveryToDate).getDays() + 1; // Including both start and end date
		log.info("Number of days for mobile Toilet booking : {}", numberOfDays);
		log.info("Number of mobile toilets : {}", noOfMobileToilet);
		BigDecimal feePerToilet = calculationTypes.get(0).getAmount();
		calculationTypes.get(0).setAmount(feePerToilet.multiply(BigDecimal.valueOf(noOfMobileToilet)).multiply(BigDecimal.valueOf(numberOfDays)));

		return calculationTypes.get(0);
	}

}
