package org.egov.noc.calculator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

import org.egov.common.contract.request.RequestInfo;
import org.egov.noc.calculator.utils.NOCConstants;
import org.egov.noc.calculator.utils.ResponseInfoFactory;
import org.egov.noc.calculator.web.models.Calculation;
import org.egov.noc.calculator.web.models.CalculationCriteria;
import org.egov.noc.calculator.web.models.CalculationReq;
import org.egov.noc.calculator.web.models.Noc;
import org.egov.noc.calculator.web.models.demand.Category;
import org.egov.noc.calculator.web.models.demand.TaxHeadEstimate;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private NOCService nocService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	public List<Calculation> calculate(CalculationReq calculationReq, boolean getCalculationOnly){
		
		List<Calculation> calculations = getCalculations(calculationReq);

		if(!getCalculationOnly) {
			demandService.generateDemands(calculationReq.getRequestInfo(), calculations);
		}
		return calculations;
	}

	public List<Calculation> getCalculations(CalculationReq calculationReq){
		
		List<Calculation> calculations = new LinkedList<>();
		
		for(CalculationCriteria criteria : calculationReq.getCalculationCriteria()) {
			
			 if(criteria.getApplicationNumber()!=null && criteria.getNoc() == null) {
         		Noc noc = nocService.getNOC(calculationReq.getRequestInfo(), criteria.getTenantId(), criteria.getApplicationNumber());
         		criteria.setNoc(noc);
			 	}
			 if (criteria.getNoc() == null)
	                throw new CustomException(NOCConstants.INVALID_APPLICATION_NUMBER, "Demand cannot be generated for applicationNumber " +
	                		criteria.getApplicationNumber() + "  NOC application with this number does not exist ");
			 
			
			List<TaxHeadEstimate> estimates;
			
			String tenantId = criteria.getTenantId();
			BigDecimal plotArea = new BigDecimal(0);
			BigDecimal builtUpArea = new BigDecimal(0);
			BigDecimal basementArea = new BigDecimal(0);
			String category = "";
			String finYear = "";
			String roadType = "";
			
			if(criteria.getNoc().getNocDetails().getAdditionalDetails() != null) {
				Map<String, Object> siteDetails = (Map<String, Object>)((Map<String, Object>)criteria.getNoc().getNocDetails().getAdditionalDetails()).get("siteDetails");
				if(siteDetails.get("specificationPlotArea") != null)
					plotArea = new BigDecimal(siteDetails.getOrDefault("specificationPlotArea", "0").toString().trim())
					.multiply(NOCConstants.SQMETER_TO_SQYARD); // Sq Yard
				if(siteDetails.get("totalFloorArea") != null)
					builtUpArea = new BigDecimal(siteDetails.getOrDefault("totalFloorArea", "0").toString().trim())
							.multiply(NOCConstants.SQMETER_TO_SQYARD); // Sq Yard
				if(siteDetails.get("basementArea") != null)
					basementArea = new BigDecimal(siteDetails.getOrDefault("basementArea", "0").toString().trim())
							.multiply(NOCConstants.SQMETER_TO_SQYARD); // Sq Yard
				if(siteDetails.get("specificationBuildingCategory") != null)
					category = siteDetails.get("specificationBuildingCategory").toString().trim();
				if(siteDetails.get("roadType") != null)
					roadType = siteDetails.get("roadType").toString().trim();
				
				LocalDate today = LocalDate.now();
				if(today.getMonthValue() > 3)
					finYear = today.getYear() + "-" + ((today.getYear() % 100) +1);
				else
					finYear = (today.getYear()-1) + "-" + (today.getYear()) % 100;
				
			}
			Object mdmsData = mdmsService.getMDMSSanctionFeeCharges(calculationReq.getRequestInfo(), tenantId, NOCConstants.MDMS_CHARGES_TYPE_CODE, category, finYear);
			estimates = calculateFee(mdmsData, plotArea, builtUpArea, basementArea, roadType);
			if(estimates.isEmpty())
				throw new CustomException("NO_FEE_CONFIGURED","No fee configured for the application");	
			
			List<Long> taxPeriodFrom = JsonPath.read(mdmsData, NOCConstants.MDMS_TAX_PERIOD_FROM_PATH);
			List<Long> taxPeriodTo = JsonPath.read(mdmsData, NOCConstants.MDMS_TAX_PERIOD_TO_PATH);
			
			Calculation calculation = new Calculation();
			calculation.setApplicationNumber(criteria.getApplicationNumber());
			calculation.setTenantId(criteria.getTenantId());
			calculation.setTaxHeadEstimates(estimates);
			calculation.setTotalAmount(estimates.stream().map(TaxHeadEstimate::getEstimateAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
			calculation.setTaxPeriodFrom(taxPeriodFrom != null && !taxPeriodFrom.isEmpty() ? taxPeriodFrom.get(0) : System.currentTimeMillis());
			calculation.setTaxPeriodTo(taxPeriodTo != null && !taxPeriodTo.isEmpty() ? taxPeriodTo.get(0) : null);
			calculation.setNoc(criteria.getNoc());
			calculations.add(calculation);
			
		}
		
		return calculations;
	}

	private Double getFlatFee(CalculationReq calculationReq) {
		Object mdmsData = mdmsService.mDMSCall(calculationReq.getRequestInfo(), calculationReq.getCalculationCriteria().get(0).getTenantId());
		String jsonPathExpression = "$.MdmsRes.noc.NocFee[0].flatFee";

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
	
	/**
	 * Calculate the Sanction fee of the BPA application
	 * @param mdmsData required to get Charges types
	 * @param plotArea Plot area of the Building
	 * @param builtUpArea Build-up area of the Building
	 * @param basementArea Basement area of the Building
	 * @param roadType Road Type of the Building
	 * @return List of TaxHeadEstimate for the Demand creation
	 */
	private List<TaxHeadEstimate> calculateFee (Object mdmsData, BigDecimal plotArea, BigDecimal builtUpArea, BigDecimal basementArea, String roadType) {
		List<TaxHeadEstimate> estimates = new LinkedList<>();
		List<Map<String,Object>> chargesTypejsonOutput = JsonPath.read(mdmsData, NOCConstants.MDMS_CHARGES_TYPE_PATH);
		
		chargesTypejsonOutput.forEach(chargesType -> {
			BigDecimal rate = BigDecimal.valueOf(chargesType.containsKey("rate") ? (Double) chargesType.get("rate") : 0.0);
			BigDecimal concession = BigDecimal.valueOf(chargesType.containsKey("concession") ? (Double) chargesType.get("concession") : 0.0);
			TaxHeadEstimate estimate = new TaxHeadEstimate();
			BigDecimal amount= BigDecimal.ZERO;
			String taxhead= chargesType.get("taxHeadCode").toString();
			
			switch (taxhead) {
			
			case NOCConstants.NOC_PROCESSING_FEES:
			case NOCConstants.NOC_EXTERNAL_DEVELOPMENT_CHARGES:
				amount=rate.multiply(plotArea).setScale(0, RoundingMode.HALF_UP);
				break;
			case NOCConstants.NOC_CLU_CHARGES:
				if(chargesType.containsKey("slabs")) {
					Map<String,Double> slabAmountMap = ((List<Map<String, Object>>)chargesType.get("slabs")).stream()
							.collect(Collectors.toMap(slab -> slab.get("roadType").toString(), slab -> (Double)slab.get("rate")));
					Double cluSlabAmount = slabAmountMap.containsKey(roadType) ? slabAmountMap.get(roadType) : slabAmountMap.get("Other Road");
					amount = BigDecimal.valueOf(cluSlabAmount).multiply(plotArea).setScale(0, RoundingMode.HALF_UP);
				}
				break;
			case NOCConstants.NOC_URBAN_DEVELOPMENT_CESS:
				amount= rate.setScale(0, RoundingMode.HALF_UP);
				break;
			case NOCConstants.NOC_COMPOUNDING_FEES:
				amount = rate.multiply(builtUpArea).setScale(0, RoundingMode.HALF_UP);
				break;
			default:
				amount = BigDecimal.ZERO;
			}
			
			if(!concession.equals(BigDecimal.ZERO))
				amount = amount.subtract(amount.divide(new BigDecimal(100)).multiply(concession)).setScale(0, RoundingMode.HALF_UP);
			
			estimate.setEstimateAmount(amount);
			estimate.setCategory(Category.FEE);
			estimate.setTaxHeadCode(taxhead);
			estimates.add(estimate);
			
		});
		
		//Updating Urban Development Cess based on other fees
		estimates.stream().filter(estimate -> estimate.getTaxHeadCode().equalsIgnoreCase(NOCConstants.NOC_URBAN_DEVELOPMENT_CESS)).forEach(estimate -> {
			BigDecimal totalFee = estimates.stream().filter(est -> est.getTaxHeadCode().equalsIgnoreCase(NOCConstants.NOC_PROCESSING_FEES) || 
					est.getTaxHeadCode().equalsIgnoreCase(NOCConstants.NOC_CLU_CHARGES) || 
					est.getTaxHeadCode().equalsIgnoreCase(NOCConstants.NOC_EXTERNAL_DEVELOPMENT_CHARGES))
			.map(est -> est.getEstimateAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
			estimate.setEstimateAmount(estimate.getEstimateAmount().multiply(totalFee).divide(BigDecimal.valueOf(100.0)).setScale(0, RoundingMode.HALF_UP));
		});
		
		return estimates;
	}

}
