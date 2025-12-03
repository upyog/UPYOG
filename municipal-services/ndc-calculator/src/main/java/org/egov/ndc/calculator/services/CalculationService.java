package org.egov.ndc.calculator.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.ndc.calculator.utils.NDCConstants;
import org.egov.ndc.calculator.utils.ResponseInfoFactory;
import org.egov.ndc.calculator.web.models.Calculation;
import org.egov.ndc.calculator.web.models.CalculationCriteria;
import org.egov.ndc.calculator.web.models.CalculationReq;
import org.egov.ndc.calculator.web.models.ndc.NdcDetailsRequest;
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

		String code = calculationReq.getCalculationCriteria().get(0).getPropertyType();
		try {
			if(StringUtils.isNotBlank(code) && !code.equalsIgnoreCase(NDCConstants.RESIDENTIAL)){
				code = NDCConstants.COMMERCIAL;
			}
			else if(calculationReq.getCalculationCriteria()
					.get(0).getNdcApplicationRequest() !=null &&
                    !calculationReq.getCalculationCriteria()
                            .get(0).getNdcApplicationRequest().getApplications().isEmpty()) {
				List<NdcDetailsRequest> ndcDetails = calculationReq.getCalculationCriteria()
						.get(0).getNdcApplicationRequest()
						.getApplications().get(0).getNdcDetails();


				String propertyType = null;
				for (NdcDetailsRequest detail : ndcDetails) {
					if (NDCConstants.PROPERTY_BUSINESSSERVICE.equalsIgnoreCase(detail.getBusinessService())) {
						JsonNode additionalDetails = detail.getAdditionalDetails();
						if (additionalDetails != null && additionalDetails.has(NDCConstants.ADDITIONAL_DETAILS_FEE_TYPE_PARAM)) {
							propertyType = additionalDetails.get(NDCConstants.ADDITIONAL_DETAILS_FEE_TYPE_PARAM).asText();
							break;
						}
					}
				}

				if (propertyType == null) {
					throw new CustomException("FEE_TYPE_MISSING", "Property type missing in additionalDetails");
				}

				code = NDCConstants.RESIDENTIAL.equalsIgnoreCase(propertyType)
						? NDCConstants.RESIDENTIAL
						: NDCConstants.COMMERCIAL;
			}

			String jsonResponse = mapper.writeValueAsString(mdmsData);
			String jsonPathExpression = String.format("$.MdmsRes.ndc.NdcFee[?(@.code=='%s')].flatFee", code);
			List<Number> flatFeeList = JsonPath.read(jsonResponse, jsonPathExpression);

			if (flatFeeList == null || flatFeeList.isEmpty()) {
				throw new CustomException("FEE_CODE_NOT_FOUND", "Fee code not found in MDMS: " + code);
			}

			double flatFeeValue = flatFeeList.get(0).doubleValue();
			System.out.println("Flat Fee (extracted with JsonPath): " + flatFeeValue);
			return flatFeeValue;
		} catch (Exception e) {
			log.error("Error extracting flatFee: " + e.getMessage());
			throw new CustomException("ERROR_FETCHING_FEE_FROM_MDMS", "Error extracting flatFee: " + e.getMessage());
		}
	}

}
