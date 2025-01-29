package org.egov.ptr.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.models.CalculationType;
import org.egov.ptr.models.DemandDetail;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.util.MdmsUtil;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalculationService {

	@Autowired
	private MdmsUtil mdmsUtil;


	public BigDecimal calculateFee(String applicationType, RequestInfo requestInfo, String tenantId) {
		List<CalculationType> calculationTypes = mdmsUtil.getCalculationType(requestInfo, tenantId,
				PTRConstants.PET_MASTER_MODULE_NAME);

		for (CalculationType calculation : calculationTypes) {
			if (calculation.getApplicationType().equalsIgnoreCase(applicationType)) {
				return calculation.getAmount();
			}
		}

		throw new CustomException("FEE_NOT_FOUND", "Fee not found for application type: " + applicationType);
	}

}
