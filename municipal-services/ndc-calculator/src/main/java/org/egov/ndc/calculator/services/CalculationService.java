package org.egov.ndc.calculator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.User;
import org.egov.ndc.calculator.utils.ResponseInfoFactory;
import org.egov.ndc.calculator.web.models.Calculation;
import org.egov.ndc.calculator.web.models.CalculationCriteria;
import org.egov.ndc.calculator.web.models.CalculationReq;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class CalculationService {

	

	@Autowired
	private MDMSService mdmsService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	public List<Calculation> calculate(CalculationReq calculationReq, boolean getCalculationOnly){
		List<Calculation> calculations = getCalculations(calculationReq);
//        CalculationRes calculationRes = CalculationRes.builder().responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(calculationReq.getRequestInfo(),true)).calculation(calculations).build();

		if(!getCalculationOnly) {
			demandService.generateDemands(calculationReq.getRequestInfo(), calculations,calculationReq);
		}
		return calculations;
	}

	public List<Calculation> getCalculations(CalculationReq calculationReq){
		List<Calculation> calculations = new LinkedList<>();
		for(CalculationCriteria calculationCriteria : calculationReq.getCalculationCriteria()) {
			Calculation calculation = new Calculation();
			calculation.setApplicationNumber(calculationCriteria.getApplicationNumber());
			calculation.setTenantId(calculationCriteria.getTenantId());
			calculation.setTotalAmount(Double.valueOf(getFlatFee(calculationReq)));
			calculations.add(calculation);
		}
		return calculations;
	}

	private Double getFlatFee(CalculationReq calculationReq) {
		Object mdmsData = mdmsService.mDMSCall(calculationReq.getRequestInfo(), calculationReq.getCalculationCriteria().get(0).getTenantId());
		String jsonPathExpression = "$.MdmsRes.ndc.NdcFee[0].flatFee";

		try {
			String jsonResponse = mapper.writeValueAsString(mdmsData);
			Number flatFee = JsonPath.read(jsonResponse, jsonPathExpression);
			Double flatFeeValue = flatFee.doubleValue();
			System.out.println("Flat Fee (extracted with JsonPath): " + flatFeeValue);
			return flatFeeValue;
		} catch (Exception e) {
			log.error("Error extracting flatFee: " + e.getMessage());
			throw new CustomException("ERROR_FETCHING_FEE_FROM_MDMS","Error extracting flatFee: " + e.getMessage());
		}
	}

}
