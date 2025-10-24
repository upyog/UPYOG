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
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
			
			if(criteria.getNoc().getNocDetails().getAdditionalDetails() != null) {
				Map<String, Object> siteDetails = (Map<String, Object>)((Map<String, Object>)criteria.getNoc().getNocDetails().getAdditionalDetails()).get("siteDetails");
				if(siteDetails.get("specificationPlotArea") != null)
					plotArea = new BigDecimal(siteDetails.getOrDefault("specificationPlotArea", "0").toString().trim());
				if(siteDetails.get("totalFloorArea") != null)
					builtUpArea = new BigDecimal(siteDetails.getOrDefault("totalFloorArea", "0").toString().trim());
				if(siteDetails.get("basementArea") != null)
					basementArea = new BigDecimal(siteDetails.getOrDefault("basementArea", "0").toString().trim());
				if(siteDetails.get("specificationBuildingCategory") != null)
					category = siteDetails.get("specificationBuildingCategory").toString().trim();
				
				LocalDate today = LocalDate.now();
				if(today.getMonthValue() > 3)
					finYear = today.getYear() + "-" + ((today.getYear() % 2000) +1);
				else
					finYear = (today.getYear()-1) + "-" + (today.getYear()) % 2000;
				
			}
			Object mdmsData = mdmsService.getMDMSSanctionFeeCharges(calculationReq.getRequestInfo(), tenantId, NOCConstants.MDMS_CHARGES_TYPE_CODE, category, finYear);
			estimates = calculateFee(calculationReq.getRequestInfo(), mdmsData, plotArea, builtUpArea, basementArea);
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
	 * @param requestInfo to call the MDMS API
	 * @param tanentId required to call the MDMS API
	 * @param plotArea Plot area of the Building
	 * @param builtUpArea Build-up area of the Building
	 * @param basementArea Basement area of the Building
	 * @param category Usage type of the Building
	 * @param finYear Current financial year
	 * @return List of TaxHeadEstimate for the Demand creation
	 */
	private List<TaxHeadEstimate> calculateFee (RequestInfo requestInfo, Object mdmsData, BigDecimal plotArea, BigDecimal builtUpArea, BigDecimal basementArea) {
		List<TaxHeadEstimate> estimates = new LinkedList<>();
		List<Map<String,Object>> chargesTypejsonOutput = JsonPath.read(mdmsData, NOCConstants.MDMS_CHARGES_TYPE_PATH);
		
		chargesTypejsonOutput.forEach(chargesType -> {
			BigDecimal rate = new BigDecimal(chargesType.containsKey("rate") ? (Double) chargesType.get("rate") : 0.0);
			TaxHeadEstimate estimate = new TaxHeadEstimate();
			BigDecimal amount= BigDecimal.ZERO;
			String taxhead= chargesType.get("taxHeadCode").toString();
			
			switch (taxhead) {
			
			case NOCConstants.NOC_PROCESSING_FEES:
			case NOCConstants.NOC_CLU_CHARGES:
			case NOCConstants.NOC_EXTERNAL_DEVELOPMENT_CHARGES:
				amount=rate.multiply(builtUpArea).setScale(0, RoundingMode.HALF_UP);
				break;
			case NOCConstants.NOC_MALBA_CHARGES:
				BigDecimal sqFeetArea = builtUpArea.multiply(NOCConstants.SQYARD_TO_SQFEET);
				Double slabAmount = (Double)((List<Map<String, Object>>)chargesType.get("slabs")).stream().filter(slab -> {
					return sqFeetArea.compareTo(new BigDecimal(slab.get("fromPlotArea").toString())) >= 0 
					        && sqFeetArea.compareTo(new BigDecimal(slab.get("toPlotArea").toString())) <= 0;
				}).map(slab -> slab.get("rate")).findFirst().orElse(0.0);
				if(slabAmount == 0.0) {
					List<Object> slabs = (List<Object>)chargesType.get("slabs");
					Map<String, Object> maxSlab = (Map<String, Object>)slabs.get(slabs.size() -1 );
					amount = sqFeetArea.subtract(new BigDecimal((Double)maxSlab.get("toPlotArea")))
							.multiply(rate).add(new BigDecimal((Double)maxSlab.get("rate")));
				}else
					amount = new BigDecimal(slabAmount);
				break;
			case NOCConstants.NOC_MINING_CHARGES:
				amount=rate.multiply(basementArea.multiply(NOCConstants.SQYARD_TO_SQFEET)).setScale(0, RoundingMode.HALF_UP);
				break;
			case NOCConstants.NOC_LABOUR_CESS:
				amount=rate.multiply(builtUpArea.multiply(NOCConstants.SQYARD_TO_SQFEET)).setScale(0, RoundingMode.HALF_UP);
				break;
			case NOCConstants.NOC_CLUBBING_CHARGES:
				amount=rate.multiply(plotArea).setScale(0, RoundingMode.HALF_UP);
				break;
			case NOCConstants.NOC_WATER_CHARGES:
			case NOCConstants.NOC_URBAN_DEVELOPMENT_CESS:
			case NOCConstants.NOC_GAUSHALA_CHARGES_CESS:
			case NOCConstants.NOC_RAIN_WATER_HARVESTING_CHARGES:
			case NOCConstants.NOC_SUB_DIVISION_CHARGES:
			case NOCConstants.NOC_OTHER_CHARGES:
				amount = rate.setScale(0, RoundingMode.HALF_UP);
				break;	
			}
			
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
		
		//Updating Water Charges based on Malba Charges
		estimates.stream().filter(est -> est.getTaxHeadCode().equalsIgnoreCase(NOCConstants.NOC_WATER_CHARGES)).forEach(estimate -> {
			BigDecimal amount = estimates.stream().filter(est -> est.getTaxHeadCode().equalsIgnoreCase(NOCConstants.NOC_MALBA_CHARGES))
					.map(est -> est.getEstimateAmount()).findFirst().orElse(BigDecimal.ZERO)
					.multiply(estimate.getEstimateAmount()).divide(new BigDecimal(100.0)).setScale(0, RoundingMode.HALF_UP);
			estimate.setEstimateAmount(amount);
		});
		
		return estimates;
	}

}
