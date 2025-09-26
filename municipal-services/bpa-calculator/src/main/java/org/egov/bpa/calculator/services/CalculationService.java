package org.egov.bpa.calculator.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.bpa.calculator.config.BPACalculatorConfig;
import org.egov.bpa.calculator.kafka.broker.BPACalculatorProducer;
import org.egov.bpa.calculator.utils.BPACalculatorConstants;
import org.egov.bpa.calculator.utils.CalculationUtils;
import org.egov.bpa.calculator.web.models.BillingSlabSearchCriteria;
import org.egov.bpa.calculator.web.models.Calculation;
import org.egov.bpa.calculator.web.models.CalculationReq;
import org.egov.bpa.calculator.web.models.CalculationRes;
import org.egov.bpa.calculator.web.models.CalulationCriteria;
import org.egov.bpa.calculator.web.models.bpa.BPA;
import org.egov.bpa.calculator.web.models.bpa.EstimatesAndSlabs;
import org.egov.bpa.calculator.web.models.demand.Category;
import org.egov.bpa.calculator.web.models.demand.TaxHeadEstimate;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Service
@Slf4j
public class CalculationService {

	

	@Autowired
	private MDMSService mdmsService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private EDCRService edcrService;
	
	@Autowired
	private BPACalculatorConfig config;

	@Autowired
	private CalculationUtils utils;

	@Autowired
	private BPACalculatorProducer producer;


	@Autowired
	private BPAService bpaService;

	/**
	 * Calculates tax estimates and creates demand
	 * 
	 * @param calculationReq
	 *            The calculationCriteria request
	 * @return List of calculations for all applicationNumbers or tradeLicenses
	 *         in calculationReq
	 */
	public List<Calculation> calculate(CalculationReq calculationReq) {
		String tenantId = calculationReq.getCalulationCriteria().get(0)
				.getTenantId();
		Object mdmsData = mdmsService.mDMSCall(calculationReq, tenantId);
		List<Calculation> calculations = getCalculation(calculationReq.getRequestInfo(),calculationReq.getCalulationCriteria(), mdmsData);
		demandService.generateDemand(calculationReq.getRequestInfo(),calculations, mdmsData);
		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
		producer.push(config.getSaveTopic(), calculationRes);
		return calculations;
	}

	/***
	 * Calculates tax estimates
	 * 
	 * @param requestInfo
	 *            The requestInfo of the calculation request
	 * @param criterias
	 *            list of CalculationCriteria containing the tradeLicense or
	 *            applicationNumber
	 * @return List of calculations for all applicationNumbers or tradeLicenses
	 *         in criterias
	 */
	public List<Calculation> getCalculation(RequestInfo requestInfo,
			List<CalulationCriteria> criterias, Object mdmsData) {
		List<Calculation> calculations = new LinkedList<>();
		for (CalulationCriteria criteria : criterias) {
			BPA bpa;
			if (criteria.getBpa() == null
					&& criteria.getApplicationNo() != null) {
				bpa = bpaService.getBuildingPlan(requestInfo, criteria.getTenantId(),
						criteria.getApplicationNo(), null);
				criteria.setBpa(bpa);
			}

			EstimatesAndSlabs estimatesAndSlabs = getTaxHeadEstimates(criteria,
					requestInfo, mdmsData);
			List<TaxHeadEstimate> taxHeadEstimates = estimatesAndSlabs
					.getEstimates();

			Calculation calculation = new Calculation();
			calculation.setBpa(criteria.getBpa());
			calculation.setTenantId(criteria.getTenantId());
			calculation.setTaxHeadEstimates(taxHeadEstimates);
			calculation.setFeeType( criteria.getFeeType());
			calculations.add(calculation);

		}
		return calculations;
	}

	/**
	 * Creates TacHeadEstimates
	 * 
	 * @param calulationCriteria
	 *            CalculationCriteria containing the tradeLicense or
	 *            applicationNumber
	 * @param requestInfo
	 *            The requestInfo of the calculation request
	 * @return TaxHeadEstimates and the billingSlabs used to calculate it
	 */
	private EstimatesAndSlabs getTaxHeadEstimates(
			CalulationCriteria calulationCriteria, RequestInfo requestInfo,
			Object mdmsData) {
		List<TaxHeadEstimate> estimates = new LinkedList<>();
		
		TaxHeadEstimate estimate = new TaxHeadEstimate();
		BigDecimal totalTax=BigDecimal.ZERO;
		String taxhead=null;

		EstimatesAndSlabs estimatesAndSlabs = new EstimatesAndSlabs();
		if (calulationCriteria.getFeeType().equalsIgnoreCase(BPACalculatorConstants.LOW_RISK_PERMIT_FEE_TYPE)) {

//			 stopping Application fee for lowrisk applicaiton according to BBI-391
			calulationCriteria.setFeeType(BPACalculatorConstants.MDMS_CALCULATIONTYPE_LOW_APL_FEETYPE);
			estimatesAndSlabs = getBaseTax(calulationCriteria, requestInfo, mdmsData);

			estimates.addAll(estimatesAndSlabs.getEstimates());

			calulationCriteria.setFeeType(BPACalculatorConstants.MDMS_CALCULATIONTYPE_LOW_SANC_FEETYPE);
			estimatesAndSlabs = getBaseTax(calulationCriteria, requestInfo, mdmsData);

			estimates.addAll(estimatesAndSlabs.getEstimates());

			calulationCriteria.setFeeType(BPACalculatorConstants.LOW_RISK_PERMIT_FEE_TYPE);

		} 
		else if (calulationCriteria.getFeeType().equalsIgnoreCase(BPACalculatorConstants.MDMS_CALCULATIONTYPE_APL_FEETYPE) && calulationCriteria.getBpa().getApplicationType().equalsIgnoreCase("BUILDING_PLAN_SCRUTINY"))
		{	
			@SuppressWarnings("unchecked")
			Map<String,String> node=(Map<String, String>)calulationCriteria.getBpa().getAdditionalDetails();
		
			if(!node.containsKey("boundaryWallLength"))
				throw new CustomException(BPACalculatorConstants.PARSING_ERROR, "Boundary Wall length should not be null");
			if(!node.containsKey("builtUpArea"))
				throw new CustomException(BPACalculatorConstants.PARSING_ERROR, "builtUpArea should not be null!!");

		BigDecimal boundayWallLength=new BigDecimal(node.get("boundaryWallLength"));
		BigDecimal area=new BigDecimal(node.get("builtUpArea")).multiply(BigDecimal.valueOf(10.7639));
		
		totalTax=boundayWallLength.multiply(BigDecimal.valueOf(2.5)).add(area.multiply(BigDecimal.valueOf(2.5)));
		estimate.setEstimateAmount(totalTax.setScale(0, RoundingMode.HALF_UP));
		estimate.setCategory(Category.FEE);

		String taxHeadCode = utils.getTaxHeadCode(calulationCriteria.getBpa().getBusinessService(), calulationCriteria.getFeeType());
		estimate.setTaxHeadCode(taxHeadCode);
		estimates.add(estimate);
		}
		
		else if (calulationCriteria.getFeeType().equalsIgnoreCase(BPACalculatorConstants.MDMS_CALCULATIONTYPE_SANC_FEETYPE) 
				&& ( calulationCriteria.getBpa().getBusinessService().equalsIgnoreCase(BPACalculatorConstants.MDMS_BPA) 
						|| calulationCriteria.getBpa().getBusinessService().equalsIgnoreCase(BPACalculatorConstants.MDMS_BPA_LOW) ) )
		{
			
			@SuppressWarnings("unchecked")
			Map<String,Object> node=(Map<String, Object>)calulationCriteria.getBpa().getAdditionalDetails();
			
			if(!node.containsKey("area"))
				throw new CustomException(BPACalculatorConstants.PARSING_ERROR, "Plot area should not be null");
			if(!node.containsKey("builtUpArea"))
				throw new CustomException(BPACalculatorConstants.PARSING_ERROR, "builtUpArea should not be null!!");
			if(!node.containsKey("usage"))
				throw new CustomException(BPACalculatorConstants.PARSING_ERROR, "Usage should not be null!!");
			
			Map<String,Object> fee = (Map<String,Object>)node.get("selfCertificationCharges");
			
			BigDecimal builtUpArea = new BigDecimal((String)node.get("builtUpArea")).multiply(BPACalculatorConstants.SQMETER_TO_SQYARD);
			BigDecimal plotArea = new BigDecimal((String)node.get("area")).multiply(BPACalculatorConstants.SQMETER_TO_SQYARD);
			BigDecimal basementArea = BigDecimal.ZERO;
			String category = (String)node.get("usage");
			Map<String, Object> taxPeriod = mdmsService.getTaxPeriods(mdmsData);
			String finYear = taxPeriod.get(BPACalculatorConstants.MDMS_FINANCIALYEAR)
					.toString();
			String tanentId=calulationCriteria.getBpa().getTenantId();
			
			//Calculate Sanction Fee of BPA
			estimates = calculateSanctionFee(requestInfo, tanentId, plotArea, builtUpArea, basementArea, fee, category, finYear);
		}

		else {
			estimatesAndSlabs = getBaseTax(calulationCriteria, requestInfo, mdmsData);
			estimates.addAll(estimatesAndSlabs.getEstimates());
		}

		estimatesAndSlabs.setEstimates(estimates);

		return estimatesAndSlabs;
	}

	/**
	 * Calculates base tax and cretaes its taxHeadEstimate
	 * 
	 * @param calulationCriteria
	 *            CalculationCriteria containing the tradeLicense or
	 *            applicationNumber
	 * @param requestInfo
	 *            The requestInfo of the calculation request
	 * @return BaseTax taxHeadEstimate and billingSlabs used to calculate it
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private EstimatesAndSlabs getBaseTax(CalulationCriteria calulationCriteria, RequestInfo requestInfo,
			Object mdmsData) {
		BPA bpa = calulationCriteria.getBpa();
		EstimatesAndSlabs estimatesAndSlabs = new EstimatesAndSlabs();
		BillingSlabSearchCriteria searchCriteria = new BillingSlabSearchCriteria();
		searchCriteria.setTenantId(bpa.getTenantId());

		Map calculationTypeMap = mdmsService.getCalculationType(requestInfo, bpa, mdmsData,
				calulationCriteria.getFeeType());
		int calculatedAmout = 0;
		ArrayList<TaxHeadEstimate> estimates = new ArrayList<TaxHeadEstimate>();
		if (calculationTypeMap.containsKey("calsiLogic")) {
			LinkedHashMap ocEdcr = edcrService.getEDCRDetails(requestInfo, bpa);
			String jsonString = new JSONObject(ocEdcr).toString();
			DocumentContext context = JsonPath.using(Configuration.defaultConfiguration()).parse(jsonString);
			JSONArray permitNumber = context.read("edcrDetail.*.permitNumber");
			String jsonData = new JSONObject(calculationTypeMap).toString();
			DocumentContext calcContext = JsonPath.using(Configuration.defaultConfiguration()).parse(jsonData);
			JSONArray parameterPaths = calcContext.read("calsiLogic.*.paramPath");
			JSONArray tLimit = calcContext.read("calsiLogic.*.tolerancelimit");
			System.out.println("tolerance limit in: " + tLimit.get(0));
			DocumentContext edcrContext = null;
			if (!CollectionUtils.isEmpty(permitNumber)) {
				BPA permitBpa = bpaService.getBuildingPlan(requestInfo, bpa.getTenantId(), null,
						permitNumber.get(0).toString());
				if (permitBpa.getEdcrNumber() != null) {
					LinkedHashMap edcr = edcrService.getEDCRDetails(requestInfo, permitBpa);
					String edcrData = new JSONObject(edcr).toString();
					edcrContext = JsonPath.using(Configuration.defaultConfiguration()).parse(edcrData);
				}
			}
			
			for (int i = 0; i < parameterPaths.size(); i++) {
				Double ocTotalBuitUpArea = context.read(parameterPaths.get(i).toString());
				Double bpaTotalBuitUpArea = edcrContext.read(parameterPaths.get(i).toString());
				Double diffInBuildArea = ocTotalBuitUpArea - bpaTotalBuitUpArea;
				System.out.println("difference in area: " + diffInBuildArea);
				Double limit = Double.valueOf(tLimit.get(i).toString());
				if (diffInBuildArea > limit) {
					JSONArray data = calcContext.read("calsiLogic.*.deviation");
					System.out.println(data.get(0));
					JSONArray data1 = (JSONArray) data.get(0);
					for (int j = 0; j < data1.size(); j++) {
						LinkedHashMap diff = (LinkedHashMap) data1.get(j);
						Integer from = (Integer) diff.get("from");
						Integer to = (Integer) diff.get("to");
						Integer uom = (Integer) diff.get("uom");
						Integer mf = (Integer) diff.get("MF");
						if (diffInBuildArea >= from && diffInBuildArea <= to) {
							calculatedAmout = (int) (diffInBuildArea * mf * uom);
							break;
						}
					}
				} else {
					calculatedAmout = 0;
				}
				TaxHeadEstimate estimate = new TaxHeadEstimate();
				BigDecimal totalTax = BigDecimal.valueOf(calculatedAmout);
				if (totalTax.compareTo(BigDecimal.ZERO) == -1)
					throw new CustomException(BPACalculatorConstants.INVALID_AMOUNT, "Tax amount is negative");

				estimate.setEstimateAmount(totalTax);
				estimate.setCategory(Category.FEE);

				String taxHeadCode = utils.getTaxHeadCode(bpa.getBusinessService(), calulationCriteria.getFeeType());
				estimate.setTaxHeadCode(taxHeadCode);
				estimates.add(estimate);
			}
		} else {
			TaxHeadEstimate estimate = new TaxHeadEstimate();
			calculatedAmout = Integer
					.parseInt(calculationTypeMap.get(BPACalculatorConstants.MDMS_CALCULATIONTYPE_AMOUNT).toString());

			BigDecimal totalTax = BigDecimal.valueOf(calculatedAmout);
			if (totalTax.compareTo(BigDecimal.ZERO) == -1)
				throw new CustomException(BPACalculatorConstants.INVALID_AMOUNT, "Tax amount is negative");

			estimate.setEstimateAmount(totalTax);
			estimate.setCategory(Category.FEE);

			String taxHeadCode = utils.getTaxHeadCode(bpa.getBusinessService(), calulationCriteria.getFeeType());
			estimate.setTaxHeadCode(taxHeadCode);
			estimates.add(estimate);
		}
		estimatesAndSlabs.setEstimates(estimates);
		return estimatesAndSlabs;
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
	private List<TaxHeadEstimate> calculateSanctionFee (RequestInfo requestInfo,String tanentId, BigDecimal plotArea, BigDecimal builtUpArea
			, BigDecimal basementArea, Map<String,Object> fee, String category, String finYear) {
		List<TaxHeadEstimate> estimates = new LinkedList<>();
		Object mdmsData = mdmsService.getMDMSSanctionFeeCharges(requestInfo, tanentId, BPACalculatorConstants.MDMS_CHARGES_TYPE_CODE, category, finYear);
		List<Map<String,Object>> chargesTypejsonOutput = JsonPath.read(mdmsData, BPACalculatorConstants.MDMS_CHARGES_TYPE_PATH);
		
		chargesTypejsonOutput.forEach(chargesType -> {
			BigDecimal rate = new BigDecimal(chargesType.containsKey("rate") ? (Double) chargesType.get("rate") : 0.0);
			TaxHeadEstimate estimate = new TaxHeadEstimate();
			BigDecimal amount= BigDecimal.ZERO;
			String taxhead= chargesType.get("taxHeadCode").toString();
			
			switch (taxhead) {
			
			case BPACalculatorConstants.BPA_PROCESSING_FEES:
			case BPACalculatorConstants.BPA_CLU_CHARGES:
			case BPACalculatorConstants.BPA_EXTERNAL_DEVELOPMENT_CHARGES:
				amount=rate.multiply(builtUpArea).setScale(0, RoundingMode.HALF_UP);
				break;
			case BPACalculatorConstants.BPA_MALBA_CHARGES:
				BigDecimal sqFeetArea = builtUpArea.multiply(BPACalculatorConstants.SQYARD_TO_SQFEET);
				Double slabAmount = (Double)((List<Map<String, Object>>)chargesType.get("slabs")).stream().filter(slab -> {
					return sqFeetArea.compareTo(new BigDecimal(slab.get("fromPlotArea").toString())) >= 0 
					        && sqFeetArea.compareTo(new BigDecimal(slab.get("toPlotArea").toString())) <= 0;
				}).map(slab -> slab.get("rate")).findFirst().orElse(0.0);
				if(slabAmount == 0.0) {
					List<Object> slabs = (List<Object>)chargesType.get("slabs");
					Map<String, Object> maxSlab = (Map<String, Object>)slabs.get(slabs.size() -1 );
					amount = sqFeetArea.subtract(new BigDecimal((Double)maxSlab.get("toPlotArea")))
							.multiply(rate).add(new BigDecimal((Double)maxSlab.get("rate"))).setScale(0, RoundingMode.HALF_UP);
				}else
					amount = new BigDecimal(slabAmount).setScale(0, RoundingMode.HALF_UP);
				break;
			case BPACalculatorConstants.BPA_MINING_CHARGES:
				amount=rate.multiply(basementArea.multiply(BPACalculatorConstants.SQYARD_TO_SQFEET)).setScale(0, RoundingMode.HALF_UP);
				break;
			case BPACalculatorConstants.BPA_LABOUR_CESS:
				amount=rate.multiply(builtUpArea.multiply(BPACalculatorConstants.SQYARD_TO_SQFEET)).setScale(0, RoundingMode.HALF_UP);
				break;
			case BPACalculatorConstants.BPA_CLUBBING_CHARGES:
				amount=rate.multiply(plotArea).setScale(0, RoundingMode.HALF_UP);
				break;
			case BPACalculatorConstants.BPA_WATER_CHARGES:
			case BPACalculatorConstants.BPA_URBAN_DEVELOPMENT_CESS:
			case BPACalculatorConstants.BPA_GAUSHALA_CHARGES_CESS:
			case BPACalculatorConstants.BPA_RAIN_WATER_HARVESTING_CHARGES:
			case BPACalculatorConstants.BPA_SUB_DIVISION_CHARGES:
				amount = rate.setScale(0, RoundingMode.HALF_UP);
				break;	
			case BPACalculatorConstants.BPA_OTHER_CHARGES:
			case BPACalculatorConstants.BPA_DEVELOPMENT_CHARGES:
				if(fee.containsKey(taxhead) && fee.get(taxhead) != null && !fee.get(taxhead).toString().trim().isEmpty() && !fee.get(taxhead).toString().equalsIgnoreCase("undefined"))
					amount = new BigDecimal(fee.get(taxhead).toString()).setScale(0, RoundingMode.HALF_UP);
				else
					amount = rate.setScale(0, RoundingMode.HALF_UP);
				break;
			case BPACalculatorConstants.BPA_LESS_ADJUSMENT_PLOT:
				if(fee.containsKey(taxhead) && fee.get(taxhead) != null && !fee.get(taxhead).toString().trim().isEmpty() && !fee.get(taxhead).toString().equalsIgnoreCase("undefined"))
					amount = new BigDecimal(fee.get(taxhead).toString()).multiply(rate).setScale(0, RoundingMode.HALF_UP);
				else
					amount = BigDecimal.ZERO;
				break;
			}
			
			estimate.setEstimateAmount(amount);
			estimate.setCategory(Category.FEE);
			estimate.setTaxHeadCode(taxhead);
			estimates.add(estimate);
			
		});
		
		//Updating Urban Development Cess based on other fees
		estimates.stream().filter(estimate -> estimate.getTaxHeadCode().equalsIgnoreCase(BPACalculatorConstants.BPA_URBAN_DEVELOPMENT_CESS)).forEach(estimate -> {
			BigDecimal totalFee = estimates.stream().filter(est -> est.getTaxHeadCode().equalsIgnoreCase(BPACalculatorConstants.BPA_PROCESSING_FEES) || 
					est.getTaxHeadCode().equalsIgnoreCase(BPACalculatorConstants.BPA_CLU_CHARGES) || 
					est.getTaxHeadCode().equalsIgnoreCase(BPACalculatorConstants.BPA_EXTERNAL_DEVELOPMENT_CHARGES))
			.map(est -> est.getEstimateAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
			estimate.setEstimateAmount(estimate.getEstimateAmount().multiply(totalFee).divide(BigDecimal.valueOf(100.0)).setScale(0, RoundingMode.HALF_UP));
		});
		
		//Updating Water Charges based on Malba Charges
		estimates.stream().filter(est -> est.getTaxHeadCode().equalsIgnoreCase(BPACalculatorConstants.BPA_WATER_CHARGES)).forEach(estimate -> {
			BigDecimal amount = estimates.stream().filter(est -> est.getTaxHeadCode().equalsIgnoreCase(BPACalculatorConstants.BPA_MALBA_CHARGES))
					.map(est -> est.getEstimateAmount()).findFirst().orElse(BigDecimal.ZERO)
					.multiply(estimate.getEstimateAmount()).divide(new BigDecimal(100.0)).setScale(0, RoundingMode.HALF_UP);
			estimate.setEstimateAmount(amount);
		});
		
		return estimates;
	}

}

