package org.egov.echallancalculation.service;


import java.util.*;
import java.math.BigDecimal;

import org.egov.common.contract.request.RequestInfo;
import org.egov.echallancalculation.config.ChallanConfiguration;
import org.egov.echallancalculation.model.Amount;
import org.egov.echallancalculation.model.Challan;
import org.egov.echallancalculation.util.CalculationUtils;
import org.egov.echallancalculation.web.models.calculation.Calculation;
import org.egov.echallancalculation.web.models.calculation.CalculationReq;
import org.egov.echallancalculation.web.models.calculation.CalulationCriteria;
import org.egov.echallancalculation.web.models.demand.TaxHeadEstimate;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculationService {

	private static final String CHALLAN_FINE_TAX_HEAD = "CH.CHALLAN_FINE";

	private final DemandService demandService;
	private final CalculationUtils utils;
	private final ChallanConfiguration config;
	private final RestTemplate restTemplate;

	/**
	 * Get CalculationReq and Calculate the Tax Head on challan
	 */
	public List<Calculation> getCalculation(CalculationReq request) {
		
		List<Calculation> calculations = getCalculation(request.getRequestInfo(),request.getCalulationCriteria());
		for(CalulationCriteria criteria : request.getCalulationCriteria()){
			String applicationStatus = criteria.getChallan().getApplicationStatus();
			if(applicationStatus.equalsIgnoreCase("CANCELLED"))
				cancelBill(request.getRequestInfo(),criteria.getChallan());
		}
		demandService.generateDemand(request.getRequestInfo(), calculations,  request.getCalulationCriteria().get(0).getChallan().getBusinessService());
		return calculations;
	}
	
	//Creating calculation using amount entered by user
	public List<Calculation> getCalculation(RequestInfo requestInfo, List<CalulationCriteria> criterias){
	      List<Calculation> calculations = new LinkedList<>();
	      for(CalulationCriteria criteria : criterias) {
	          Challan challan = resolveChallan(requestInfo, criteria);
	          List<TaxHeadEstimate> estimates = buildTaxHeadEstimates(requestInfo, challan);

	          Calculation calculation = new Calculation();
	          calculation.setChallan(criteria.getChallan());
	          calculation.setTenantId(criteria.getTenantId());
	          calculation.setTaxHeadEstimates(estimates);

	          calculations.add(calculation);
	      }
	      return calculations;
	  }

	private Challan resolveChallan(RequestInfo requestInfo, CalulationCriteria criteria) {
		Challan challan = criteria.getChallan();
		if (criteria.getChallan() == null && criteria.getChallanNo() != null) {
			challan = utils.getChallan(requestInfo, criteria.getChallanNo(), criteria.getTenantId());
			criteria.setChallan(challan);
		}
		return challan;
	}

	private List<TaxHeadEstimate> buildTaxHeadEstimates(RequestInfo requestInfo, Challan challan) {
		List<TaxHeadEstimate> estimates = new LinkedList<>();
		String taxHeadCode = CHALLAN_FINE_TAX_HEAD;

		BigDecimal challanAmount = parseChallanAmount(challan);
		AmountArraySummary amountArraySummary = findMaxFromAmountArray(challan.getAmount());
		MdmsRateSummary mdmsSummary = resolveMdmsRateAndTaxHead(requestInfo, challan, taxHeadCode);
		taxHeadCode = mdmsSummary.taxHeadCode();

		BigDecimal maxAmount = challanAmount.max(amountArraySummary.maxAmount()).max(mdmsSummary.rate());

		if (maxAmount.compareTo(BigDecimal.ZERO) > 0) {
			AmountSelection selection = selectFinalAmount(challanAmount, amountArraySummary, mdmsSummary, maxAmount, taxHeadCode);
			TaxHeadEstimate estimate = new TaxHeadEstimate();
			estimate.setEstimateAmount(selection.amount());
			estimate.setTaxHeadCode(selection.taxHeadCode());
			estimates.add(estimate);

			log.info("Demand calculation - challanAmount: {}, amountArrayMax: {}, mdmsRate: {}, finalAmount: {}",
					challanAmount, amountArraySummary.maxAmount(), mdmsSummary.rate(), selection.amount());
			return estimates;
		}

		addFallbackEstimatesFromAmountList(challan.getAmount(), taxHeadCode, estimates);
		return estimates;
	}

	private BigDecimal parseChallanAmount(Challan challan) {
		if (challan.getChallanAmount() == null || challan.getChallanAmount().isEmpty()) {
			return BigDecimal.ZERO;
		}
		try {
			return new BigDecimal(challan.getChallanAmount());
		} catch (NumberFormatException e) {
			log.warn("Invalid challanAmount format: {}", challan.getChallanAmount());
			return BigDecimal.ZERO;
		}
	}

	private AmountArraySummary findMaxFromAmountArray(List<Amount> amountList) {
		BigDecimal amountArrayMax = BigDecimal.ZERO;
		String amountArrayTaxHeadCode = null;

		if (amountList == null || amountList.isEmpty()) {
			return new AmountArraySummary(amountArrayMax, amountArrayTaxHeadCode);
		}

		for (Amount amountItem : amountList) {
			if (amountItem.getAmount() == null) {
				continue;
			}
			int comparison = amountItem.getAmount().compareTo(amountArrayMax);
			if (comparison > 0) {
				amountArrayMax = amountItem.getAmount();
				if (hasTaxHeadCode(amountItem)) {
					amountArrayTaxHeadCode = amountItem.getTaxHeadCode();
				}
			} else if (comparison == 0 && amountArrayTaxHeadCode == null && hasTaxHeadCode(amountItem)) {
				amountArrayTaxHeadCode = amountItem.getTaxHeadCode();
			}
		}
		return new AmountArraySummary(amountArrayMax, amountArrayTaxHeadCode);
	}

	private boolean hasTaxHeadCode(Amount amountItem) {
		return amountItem.getTaxHeadCode() != null && !amountItem.getTaxHeadCode().isEmpty();
	}

	private MdmsRateSummary resolveMdmsRateAndTaxHead(RequestInfo requestInfo, Challan challan, String defaultTaxHeadCode) {
		String offenceTypeId = resolveOffenceTypeId(requestInfo, challan);
		if (offenceTypeId == null) {
			return new MdmsRateSummary(BigDecimal.ZERO, defaultTaxHeadCode);
		}

		BigDecimal mdmsRate = utils.getRateFromMDMS(requestInfo, challan.getTenantId(), offenceTypeId);
		String taxHeadCode = defaultTaxHeadCode;
		if (challan.getOffenceTypeName() != null && !challan.getOffenceTypeName().isEmpty()) {
			String mdmsTaxHeadCode = utils.getTaxHeadCodeFromOffenceTypeName(
					requestInfo, challan.getTenantId(), challan.getOffenceTypeName());
			if (mdmsTaxHeadCode != null && !mdmsTaxHeadCode.isEmpty()) {
				taxHeadCode = mdmsTaxHeadCode;
			}
		}
		return new MdmsRateSummary(mdmsRate, taxHeadCode);
	}

	private String resolveOffenceTypeId(RequestInfo requestInfo, Challan challan) {
		if (challan.getOffenceTypeName() != null && !challan.getOffenceTypeName().isEmpty()) {
			return utils.mapOffenceTypeNameToId(requestInfo, challan.getTenantId(), challan.getOffenceTypeName());
		}
		if (challan.getOffenceType() != null && !challan.getOffenceType().isEmpty()) {
			return challan.getOffenceType();
		}
		return null;
	}

	private AmountSelection selectFinalAmount(BigDecimal challanAmount, AmountArraySummary amountArraySummary,
			MdmsRateSummary mdmsSummary, BigDecimal maxAmount, String taxHeadCode) {
		if (challanAmount.compareTo(maxAmount) >= 0 && challanAmount.compareTo(BigDecimal.ZERO) > 0) {
			return new AmountSelection(challanAmount, taxHeadCode);
		}
		if (amountArraySummary.maxAmount().compareTo(maxAmount) >= 0
				&& amountArraySummary.maxAmount().compareTo(BigDecimal.ZERO) > 0) {
			String selectedTaxHead = amountArraySummary.taxHeadCode() != null && !amountArraySummary.taxHeadCode().isEmpty()
					? amountArraySummary.taxHeadCode() : taxHeadCode;
			return new AmountSelection(amountArraySummary.maxAmount(), selectedTaxHead);
		}
		if (mdmsSummary.rate().compareTo(maxAmount) >= 0 && mdmsSummary.rate().compareTo(BigDecimal.ZERO) > 0) {
			return new AmountSelection(mdmsSummary.rate(), taxHeadCode);
		}
		return new AmountSelection(maxAmount, taxHeadCode);
	}

	private void addFallbackEstimatesFromAmountList(List<Amount> amountList, String taxHeadCode,
			List<TaxHeadEstimate> estimates) {
		if (amountList == null || amountList.isEmpty()) {
			return;
		}
		for (Amount amountItem : amountList) {
			TaxHeadEstimate estimate = new TaxHeadEstimate();
			estimate.setEstimateAmount(amountItem.getAmount());
			estimate.setTaxHeadCode(amountItem.getTaxHeadCode() != null ? amountItem.getTaxHeadCode() : taxHeadCode);
			estimates.add(estimate);
		}
	}

	private record AmountArraySummary(BigDecimal maxAmount, String taxHeadCode) {}
	private record MdmsRateSummary(BigDecimal rate, String taxHeadCode) {}
	private record AmountSelection(BigDecimal amount, String taxHeadCode) {}

	/**
	 * Test method to demonstrate dynamic calculation with provided payload
	 * This method shows how the higher amount logic works with dynamic MDMS mapping
	 */
	public void testCalculationWithPayload(RequestInfo requestInfo, String tenantId) {
		log.info("=== Testing Dynamic Calculation with Provided Payload ===");
		
		// Example from your payload:
		// "offenceTypeName": "Loud Music After 10 PM"
		// "challanAmount": "200"
		
		String offenceTypeName = "Loud Music After 10 PM";
		String challanAmount = "200";
		
		// Dynamically map offence type name to ID from MDMS
		String offenceTypeId = utils.mapOffenceTypeNameToId(requestInfo, tenantId, offenceTypeName);
		log.info("Offence Type: {} -> Dynamically mapped to ID: {}", offenceTypeName, offenceTypeId);
		
		// Parse user amount
		BigDecimal userAmount = new BigDecimal(challanAmount);
		log.info("User entered challan amount: {}", userAmount);
		
		// Dynamically fetch MDMS rate
		BigDecimal mdmsRate = BigDecimal.ZERO;
		String taxHeadCode = CHALLAN_FINE_TAX_HEAD;
		if (offenceTypeId != null) {
			mdmsRate = utils.getRateFromMDMS(requestInfo, tenantId, offenceTypeId);
			log.info("MDMS rate for {}: {}", offenceTypeId, mdmsRate);
			
			// Fetch tax head code from OffenceType master
			taxHeadCode = utils.getTaxHeadCodeFromOffenceTypeName(requestInfo, tenantId, offenceTypeName);
			log.info("Tax Head Code from OffenceType: {}", taxHeadCode);
		}
		
		// Calculate final amount (higher of the two)
		BigDecimal finalAmount = userAmount.max(mdmsRate);
		log.info("Final amount (higher of user amount and MDMS rate): {}", finalAmount);
		
		log.info("=== Dynamic Calculation Result ===");
		log.info("Tax Head Code: {}", taxHeadCode);
		log.info("Final Amount: {}", finalAmount);
		log.info("===============================================");
	}

	/**
	 * Test method to demonstrate minimumPayableAmount functionality
	 * This method shows how the total amount is calculated and stored in minimumPayableAmount
	 */
	public void testMinimumPayableAmount(RequestInfo requestInfo, String tenantId) {
		log.info("=== Testing Minimum Payable Amount Functionality ===");
		
		// Example calculation with your payload
		String offenceTypeName = "Loud Music After 10 PM";
		String challanAmount = "200";
		
		// Get offence type ID and rate
		String offenceTypeId = utils.mapOffenceTypeNameToId(requestInfo, tenantId, offenceTypeName);
		BigDecimal userAmount = new BigDecimal(challanAmount);
		BigDecimal mdmsRate = BigDecimal.ZERO;
		String taxHeadCode = CHALLAN_FINE_TAX_HEAD;
		
		if (offenceTypeId != null) {
			mdmsRate = utils.getRateFromMDMS(requestInfo, tenantId, offenceTypeId);
			taxHeadCode = utils.getTaxHeadCodeFromOffenceTypeName(requestInfo, tenantId, offenceTypeName);
		}
		
		// Calculate final amount (higher of the two)
		BigDecimal finalAmount = userAmount.max(mdmsRate);
		
		// Simulate round-off calculation
		BigDecimal decimalValue = finalAmount.remainder(BigDecimal.ONE);
		BigDecimal roundOff;
		
		if (decimalValue.compareTo(new BigDecimal("0.5")) >= 0) {
			roundOff = BigDecimal.ONE.subtract(decimalValue);
		} else {
			roundOff = decimalValue.negate();
		}
		
		BigDecimal totalWithRoundOff = finalAmount.add(roundOff);
		
		log.info("User Amount: {}", userAmount);
		log.info("MDMS Rate: {}", mdmsRate);
		log.info("Final Amount (before round-off): {}", finalAmount);
		log.info("Round-off Amount: {}", roundOff);
		log.info("Total Amount (minimumPayableAmount): {}", totalWithRoundOff);
		
		log.info("=== Demand Creation ===");
		log.info("Demand will be created with:");
		log.info("- Tax Head: {}", taxHeadCode);
		log.info("- Amount: {}", finalAmount);
		log.info("- Round-off: {}", roundOff);
		log.info("- minimumPayableAmount: {}", totalWithRoundOff);
		log.info("===============================================");
	}

	public void cancelBill(RequestInfo requestInfo, Challan challan){
		Map<String, Object> request = new HashMap<>();
		Map<String, Object> updateBillCriteria = new HashMap<>();
		List<String> consumerCodes = Arrays.asList(challan.getChallanNo());
		String businessService = challan.getBusinessService();

		updateBillCriteria.put("tenantId", challan.getTenantId());
		updateBillCriteria.put("consumerCodes", consumerCodes);
		updateBillCriteria.put("businessService", businessService);
		updateBillCriteria.put("additionalDetails", challan.getAdditionalDetail());

		request.put("RequestInfo", requestInfo);
		request.put("UpdateBillCriteria", updateBillCriteria);

		StringBuilder url = new StringBuilder();
		url.append(config.getBillingHost()).append(config.getCancelBillEndpoint());
		try {
			restTemplate.postForObject(url.toString(), request, Map.class);
		}catch(Exception e) {
			log.error("Exception while fetching user: ", e);
		}
	}

	/**
	 * Update calculation with fee waiver
	 * Validates demandId, applies fee waiver by adding negative demand detail, and updates demand
	 * @param request CalculationReq with demandId and fee waiver information
	 * @return List of updated calculations
	 */
	public List<Calculation> updateCalculation(CalculationReq request) {
		// Validate demandId is present
		for(CalulationCriteria criteria : request.getCalulationCriteria()) {
			if(criteria.getDemandId() == null || criteria.getDemandId().isEmpty()) {
				throw new org.egov.tracer.model.CustomException("INVALID_REQUEST", 
					"demandId is required for update calculation");
			}
		}
		
		// Update demand with fee waiver - this will add negative demand detail
		List<Calculation> calculations = new LinkedList<>();
		for(CalulationCriteria criteria : request.getCalulationCriteria()) {
			Challan challan = criteria.getChallan();
			if (criteria.getChallan() == null && criteria.getChallanNo() != null) {
				challan = utils.getChallan(request.getRequestInfo(), criteria.getChallanNo(), criteria.getTenantId());
				criteria.setChallan(challan);
			}
			
			String businessService = challan.getBusinessService();
			// Update demand and get updated demand with new amounts
			org.egov.echallancalculation.web.models.demand.Demand updatedDemand = 
				demandService.updateDemandWithFeeWaiver(
					request.getRequestInfo(), 
					challan,
					businessService,
					criteria.getDemandId()
				);
			
			// Create tax head estimates from updated demand details
			List<TaxHeadEstimate> taxHeadEstimates = createTaxHeadEstimatesFromDemand(
				updatedDemand, 
				businessService
			);
			
			// Create calculation response with updated amounts
			Calculation calculation = new Calculation();
			calculation.setChallan(challan);
			calculation.setTenantId(criteria.getTenantId());
			calculation.setTaxHeadEstimates(taxHeadEstimates);
			calculations.add(calculation);
		}
		
		return calculations;
	}

	/**
	 * Create tax head estimates from updated demand details
	 * Groups demand details by tax head code and sums the amounts
	 * @param demand The updated demand object
	 * @param businessService The business service
	 * @return List of tax head estimates with updated amounts
	 */
	private List<TaxHeadEstimate> createTaxHeadEstimatesFromDemand(
			org.egov.echallancalculation.web.models.demand.Demand demand, 
			String businessService) {
		List<TaxHeadEstimate> estimates = new LinkedList<>();
		
		if(demand.getDemandDetails() == null || demand.getDemandDetails().isEmpty()) {
			return estimates;
		}
		
		// Group demand details by tax head code and sum amounts
		Map<String, BigDecimal> taxHeadAmountMap = new HashMap<>();
		
		String roundOffTaxHead = businessService + "_ROUNDOFF";
		
		for(org.egov.echallancalculation.web.models.demand.DemandDetail detail : demand.getDemandDetails()) {
			if(detail.getTaxHeadMasterCode() != null && detail.getTaxAmount() != null
					&& !detail.getTaxHeadMasterCode().equalsIgnoreCase(roundOffTaxHead)) {
				String taxHeadCode = detail.getTaxHeadMasterCode();
				BigDecimal currentAmount = taxHeadAmountMap.getOrDefault(taxHeadCode, BigDecimal.ZERO);
				taxHeadAmountMap.put(taxHeadCode, currentAmount.add(detail.getTaxAmount()));
			}
		}
		
		// Create tax head estimates from grouped amounts
		for(Map.Entry<String, BigDecimal> entry : taxHeadAmountMap.entrySet()) {
			// Only include tax heads with non-zero amounts
			if(entry.getValue().compareTo(BigDecimal.ZERO) != 0) {
				TaxHeadEstimate estimate = new TaxHeadEstimate();
				estimate.setTaxHeadCode(entry.getKey());
				estimate.setEstimateAmount(entry.getValue());
				estimates.add(estimate);
			}
		}
		
		// Add round-off separately if exists
		for(org.egov.echallancalculation.web.models.demand.DemandDetail detail : demand.getDemandDetails()) {
			if(detail.getTaxHeadMasterCode() != null && 
			   detail.getTaxHeadMasterCode().equalsIgnoreCase(roundOffTaxHead) &&
			   detail.getTaxAmount() != null &&
			   detail.getTaxAmount().compareTo(BigDecimal.ZERO) != 0) {
				TaxHeadEstimate roundOffEstimate = new TaxHeadEstimate();
				roundOffEstimate.setTaxHeadCode(detail.getTaxHeadMasterCode());
				roundOffEstimate.setEstimateAmount(detail.getTaxAmount());
				estimates.add(roundOffEstimate);
			}
		}
		
		return estimates;
	}

	
}
