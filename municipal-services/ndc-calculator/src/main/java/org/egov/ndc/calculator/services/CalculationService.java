package org.egov.ndc.calculator.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.ndc.calculator.utils.NDCConstants;
import org.egov.ndc.calculator.web.models.Calculation;
import org.egov.ndc.calculator.web.models.CalculationCriteria;
import org.egov.ndc.calculator.web.models.CalculationReq;
import org.egov.ndc.calculator.web.models.ndc.NdcDetailsRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculationService {

	private final MDMSService mdmsService;

	private final DemandService demandService;

	private final ObjectMapper mapper;

	public List<Calculation> calculate(CalculationReq calculationReq, boolean getCalculationOnly){
		List<Calculation> calculations = getCalculations(calculationReq);

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
			calculation.setTotalAmount(getFlatFee(calculationReq));
			calculations.add(calculation);
		}
		return calculations;
	}

	private Double getFlatFee(CalculationReq calculationReq) {
		Object mdmsData = mdmsService.mDMSCall(calculationReq.getRequestInfo(), calculationReq.getCalculationCriteria().get(0).getTenantId());

		try {
			String code = resolveFeeCode(calculationReq);

			String jsonResponse = mapper.writeValueAsString(mdmsData);
			String jsonPathExpression = String.format("$.MdmsRes.ndc.NdcFee[?(@.code=='%s')].flatFee", code);
			List<Number> flatFeeList = JsonPath.read(jsonResponse, jsonPathExpression);

			if (flatFeeList == null || flatFeeList.isEmpty()) {
				throw new CustomException("FEE_CODE_NOT_FOUND", "Fee code not found in MDMS: " + code);
			}

			double flatFeeValue = flatFeeList.get(0).doubleValue();
			log.info("Flat Fee (extracted with JsonPath): {}", flatFeeValue);
			return flatFeeValue;
		} catch (Exception e) {
			log.error("Error extracting flatFee: " + e.getMessage());
			throw new CustomException("ERROR_FETCHING_FEE_FROM_MDMS", "Error extracting flatFee: " + e.getMessage());
		}
	}

	private String resolveFeeCode(CalculationReq calculationReq) {
		CalculationCriteria criteria = calculationReq.getCalculationCriteria().get(0);
		String code = criteria.getPropertyType();

		if (StringUtils.isNotBlank(code) && !code.equalsIgnoreCase(NDCConstants.RESIDENTIAL)) {
			return NDCConstants.COMMERCIAL;
		}

		if (criteria.getNdcApplicationRequest() == null
				|| criteria.getNdcApplicationRequest().getApplications().isEmpty()) {
			return code;
		}

		List<NdcDetailsRequest> ndcDetails = criteria.getNdcApplicationRequest()
				.getApplications().get(0).getNdcDetails();

		String propertyType = extractPropertyType(ndcDetails);
		if (propertyType == null) {
			throw new CustomException("FEE_TYPE_MISSING", "Property type missing in additionalDetails");
		}

		return NDCConstants.RESIDENTIAL.equalsIgnoreCase(propertyType)
				? NDCConstants.RESIDENTIAL
				: NDCConstants.COMMERCIAL;
	}

	private String extractPropertyType(List<NdcDetailsRequest> ndcDetails) {
		for (NdcDetailsRequest detail : ndcDetails) {
			if (!NDCConstants.PROPERTY_BUSINESSSERVICE.equalsIgnoreCase(detail.getBusinessService())) {
				continue;
			}
			JsonNode additionalDetails = detail.getAdditionalDetails();
			if (additionalDetails != null && additionalDetails.has(NDCConstants.ADDITIONAL_DETAILS_FEE_TYPE_PARAM)) {
				return additionalDetails.get(NDCConstants.ADDITIONAL_DETAILS_FEE_TYPE_PARAM).asText();
			}
		}
		return null;
	}

}
