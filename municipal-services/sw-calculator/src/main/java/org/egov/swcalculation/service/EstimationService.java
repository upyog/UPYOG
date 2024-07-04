package org.egov.swcalculation.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swcalculation.constants.SWCalculationConstant;
import org.egov.swcalculation.web.models.*;
import org.egov.swcalculation.util.CalculatorUtils;
import org.egov.swcalculation.util.SWCalculationUtil;
import org.egov.swcalculation.util.SewerageCessUtil;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Service
@Slf4j
public class EstimationService {

	@Autowired
	private CalculatorUtils calculatorUtil;

	@Autowired
	private SewerageCessUtil sewerageCessUtil;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private SWCalculationUtil sWCalculationUtil;

	/**
	 * Generates a List of Tax head estimates with tax head code, tax head
	 * category and the amount to be collected for the key.
	 *
	 * @param criteria
	 *            criteria based on which calculation will be done.
	 * @param requestInfo
	 *            request info from incoming request.
	 * @return Map<String, Double>
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, List> getEstimationMap(CalculationCriteria criteria, CalculationReq request,
			Map<String, Object> masterData) {
		if (StringUtils.isEmpty((criteria.getSewerageConnection()))
				&& !StringUtils.isEmpty(criteria.getConnectionNo())) {
			List<SewerageConnection> sewerageConnectionList = calculatorUtil.getSewerageConnection(
					request.getRequestInfo(), criteria.getConnectionNo(), criteria.getTenantId());
			SewerageConnection sewerageConnection = calculatorUtil.getSewerageConnectionObject(sewerageConnectionList);
			criteria.setSewerageConnection(sewerageConnection);
		}
		if (criteria.getSewerageConnection() == null || StringUtils.isEmpty(criteria.getConnectionNo())) {
			StringBuilder builder = new StringBuilder();
			builder.append("Sewerage Connection are not present for ").append(StringUtils.isEmpty(criteria.getConnectionNo()) ? "" : criteria.getConnectionNo())
					.append(" connection no");
			throw new CustomException("INVALID_CONNECTION_ID", builder.toString());
		}
		Map<String, JSONArray> billingSlabMaster = new HashMap<>();
		Map<String, JSONArray> timeBasedExemptionMasterMap = new HashMap<>();
		ArrayList<String> billingSlabIds = new ArrayList<>();

		billingSlabMaster.put(SWCalculationConstant.SW_BILLING_SLAB_MASTER,
				(JSONArray) masterData.get(SWCalculationConstant.SW_BILLING_SLAB_MASTER));
		billingSlabMaster.put(SWCalculationConstant.CALCULATION_ATTRIBUTE_CONST,
				(JSONArray) masterData.get(SWCalculationConstant.CALCULATION_ATTRIBUTE_CONST));
		timeBasedExemptionMasterMap.put(SWCalculationConstant.SW_SEWERAGE_CESS_MASTER,
				(JSONArray) (masterData.getOrDefault(SWCalculationConstant.SW_SEWERAGE_CESS_MASTER, null)));
		BigDecimal sewerageCharge = getSewerageEstimationCharge(criteria.getSewerageConnection(), criteria,
				billingSlabMaster, billingSlabIds, request);
		List<TaxHeadEstimate> taxHeadEstimates = getEstimatesForTax(sewerageCharge, criteria.getSewerageConnection(),
				timeBasedExemptionMasterMap,
				RequestInfoWrapper.builder().requestInfo(request.getRequestInfo()).build());

		Map<String, List> estimatesAndBillingSlabs = new HashMap<>();
		estimatesAndBillingSlabs.put("estimates", taxHeadEstimates);
		estimatesAndBillingSlabs.put("billingSlabIds", billingSlabIds);
		return estimatesAndBillingSlabs;
	}

	private List<TaxHeadEstimate> getEstimatesForTax(BigDecimal sewerageCharge, SewerageConnection connection,
			Map<String, JSONArray> timeBasedExemptionMasterMap, RequestInfoWrapper requestInfoWrapper) {

		List<TaxHeadEstimate> estimates = new ArrayList<>();
		// sewerage_charge
		estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_CHARGE)
				.estimateAmount(sewerageCharge.setScale(2, 2)).build());

		/// DISPOSAL DISCHARGE CHARGES
		HashMap<String,String> add_details= ((HashMap<String,String>)connection.getAdditionalDetails());
		if(add_details.containsKey("dischargeConnection"))
		{

			if(add_details.get("dischargeConnection").equalsIgnoreCase("true"))
			{
				//BigDecimal disposal_charge=new BigDecimal(200.0);
				BigDecimal disposal_charge;
				try
				{
				if(add_details.containsKey("dischargeFee"))  // if dischargeFee attribute is present in additionalDetails then use give dischargeFee else fix dischagefee to 200
					disposal_charge=new BigDecimal(add_details.get("dischargeFee"));
				else
					disposal_charge=new BigDecimal(200.0);
				}
				catch(Exception ex)
				{
					disposal_charge=new BigDecimal(200.0);
				}
				estimates.add(TaxHeadEstimate.builder().taxHeadCode("SW_DISCHARGE_CHARGES")
						.estimateAmount(disposal_charge.setScale(2, 2)).build());
			}
		}

		// sewerage cess
//		if (timeBasedExemptionMasterMap.get(SWCalculationConstant.SW_SEWERAGE_CESS_MASTER) != null) {
//			List<Object> sewerageCessMasterList = timeBasedExemptionMasterMap
//					.get(SWCalculationConstant.SW_SEWERAGE_CESS_MASTER);
//			BigDecimal sewerageCess = sewerageCessUtil.getSewerageCess(sewerageCharge, SWCalculationConstant.Assesment_Year, sewerageCessMasterList);
//			estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_WATER_CESS)
//					.estimateAmount(sewerageCess.setScale(2, 2)).build());
//		}
		return estimates;
	}

	/**
	 * method to do a first level filtering on the slabs based on the values
	 * present in the Sewerage Details
	 */

	public BigDecimal getSewerageEstimationCharge(SewerageConnection sewerageConnection, CalculationCriteria criteria,
			Map<String, JSONArray> billingSlabMaster, ArrayList<String> billingSlabIds, CalculationReq request) {
		BigDecimal sewerageCharge = BigDecimal.ZERO;
		HashMap<String, Object> additionalDetail = new HashMap<>();
		additionalDetail = mapper.convertValue(sewerageConnection.getAdditionalDetails(), HashMap.class);
		String billingType = (String) additionalDetail.getOrDefault(SWCalculationConstant.BILLINGTYPE, null);
		if(billingType==null|| billingType=="" || billingType.isEmpty())
			billingType="STANDARD";
		if (sewerageConnection.getConnectionType().equalsIgnoreCase(SWCalculationConstant.nonMeterdConnection)
				&& billingType.equalsIgnoreCase(SWCalculationConstant.CUSTOM)) {
			Integer billingAmountInt = (Integer) additionalDetail.getOrDefault(SWCalculationConstant.CUSTOM_BILL_AMOUNT, 0);
			sewerageCharge = BigDecimal.valueOf(Long.valueOf(billingAmountInt)).setScale(2, 2);
			return sewerageCharge;
//			sewerageCharge = (BigDecimal) additionalDetail.getOrDefault(SWCalculationConstant.CUSTOM_BILL_AMOUNT,
//					BigDecimal.ZERO);
		}
		
		if (billingSlabMaster.get(SWCalculationConstant.SW_BILLING_SLAB_MASTER) == null)
			throw new CustomException("INVALID_BILLING_SLAB", "Billing Slab are Empty");
		List<BillingSlab> mappingBillingSlab;
		try {
			mappingBillingSlab = mapper.readValue(
					billingSlabMaster.get(SWCalculationConstant.SW_BILLING_SLAB_MASTER).toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, BillingSlab.class));
		} catch (IOException e) {
			throw new CustomException("PARSING_ERROR", " Billing Slab can not be parsed!");
		}
		JSONObject calculationAttributeMaster = new JSONObject();
		calculationAttributeMaster.put(SWCalculationConstant.CALCULATION_ATTRIBUTE_CONST,
				billingSlabMaster.get(SWCalculationConstant.CALCULATION_ATTRIBUTE_CONST));
		String calculationAttribute = getCalculationAttribute(calculationAttributeMaster,
				sewerageConnection.getConnectionType());
		SewerageConnectionRequest sewerageConnectionRequest = SewerageConnectionRequest.builder()
				.sewerageConnection(sewerageConnection).requestInfo(request.getRequestInfo()).build();
		Property property = sWCalculationUtil.getProperty(sewerageConnectionRequest);
		log.info("billingSlabMaster: "+billingSlabMaster);
		log.info("mappingBillingSlab:"+mappingBillingSlab+"calculationAttribute: "+calculationAttribute+" calculationAttributeMaster {}:",calculationAttributeMaster);
		List<BillingSlab> billingSlabs = getSlabsFiltered(sewerageConnectionRequest, mappingBillingSlab, calculationAttribute,
				request.getRequestInfo(),property);

		if (billingSlabs == null || billingSlabs.isEmpty())
			throw new CustomException("INVALID_BILLING_SLAB", "Billing Slab are Empty");
//		if (billingSlabs.size() > 1)
//			throw new CustomException("INVALID_BILLING_SLAB",
//					"More than one billing slab found");
		// Add Billing Slab Ids
		billingSlabIds.add(billingSlabs.get(0).getId());

		// Sewerage Charge Calculation
		Double totalUnits = getCalculationUnit(sewerageConnection, calculationAttribute, criteria,property);
//		if (totalUnits == 0.0)
//			return sewerageCharge;
		BillingSlab billSlab = billingSlabs.get(0);
		if (isRangeCalculation(calculationAttribute)) {

			 String waterSubUsageType = (String) additionalDetail
					.getOrDefault(SWCalculationConstant.WATER_SUBUSAGE_TYPE, null);
if (waterSubUsageType==null|| waterSubUsageType.isEmpty())
	{
	if (property.getUsageCategory().equalsIgnoreCase("RESIDENTIAL"))
		waterSubUsageType="USAGE_DOM_NA";
	else if (property.getUsageCategory().equalsIgnoreCase("COMMERCIAL"))
		waterSubUsageType="USAGE_COMM_NA";
	else if (property.getUsageCategory().equalsIgnoreCase("INDUSTRIAL"))
		waterSubUsageType="USAGE_COMM_NA";
	else if (property.getUsageCategory().equalsIgnoreCase("INSTITUTIONAL"))
		waterSubUsageType="USAGE_COMM_NA";
	}
			for (Slab slab : billSlab.getSlabs()) {

				boolean slabCondition = false;

				if (waterSubUsageType != null) {
					slabCondition = totalUnits >= slab.getFrom() && totalUnits <= slab.getTo()
							&& slab.getEffectiveFrom() <= System.currentTimeMillis()
							&& slab.getEffectiveTo() >= System.currentTimeMillis()
							&& waterSubUsageType.equalsIgnoreCase(billSlab.getWaterSubUsageType());
				} else {
					slabCondition = totalUnits >= slab.getFrom() && totalUnits <= slab.getTo()
							&& slab.getEffectiveFrom() <= System.currentTimeMillis()
							&& slab.getEffectiveTo() >= System.currentTimeMillis();
				}

				if (slabCondition) {
					sewerageCharge = BigDecimal.valueOf((slab.getCharge()));
					// request.setTaxPeriodFrom(criteria.getFrom());
					// request.setTaxPeriodTo(criteria.getTo());
					if (request.getTaxPeriodFrom() > 0 && request.getTaxPeriodTo() > 0) {
						if (sewerageConnection.getConnectionExecutionDate() > request.getTaxPeriodFrom()) {
							long milli_sec_btw_conn_date = Math.abs(request.getTaxPeriodTo() - sewerageConnection.getConnectionExecutionDate());
							long milli_sec_btw_quarter = Math.abs(request.getTaxPeriodTo() - request.getTaxPeriodFrom());
							//Converting milli seconds to days
						    long days_conn_date = TimeUnit.MILLISECONDS.toDays(milli_sec_btw_conn_date) + 1;
						    long days_quarter = TimeUnit.MILLISECONDS.toDays(milli_sec_btw_quarter) + 1;
	
							//sewerageCharge = BigDecimal.valueOf((totalUnits * slab.getCharge() * daysDifference));
							//sewerageCharge = sewerageCharge.add(BigDecimal.valueOf(days_conn_date * (slab.getCharge() / days_quarter)).setScale(2, 2));
							sewerageCharge = BigDecimal.valueOf(days_conn_date * (slab.getCharge() / days_quarter)).setScale(2, 2);
						}

					}

					if (billSlab.getMinimumCharge() > sewerageCharge.doubleValue()) {
						sewerageCharge = BigDecimal.valueOf(billSlab.getMinimumCharge());
					}
					break;
				}
			}

		} else {
			sewerageCharge = BigDecimal.valueOf(billSlab.getMinimumCharge());
		}
		return sewerageCharge;
	}

	private String getCalculationAttribute(Map<String, Object> calculationAttributeMap, String connectionType) {
		if (calculationAttributeMap == null)
			throw new CustomException("CALCULATION_ATTRIBUTE_MASTER_NOT_FOUND",
					"Calculation attribute master not found!!");
		JSONArray filteredMasters = JsonPath.read(calculationAttributeMap,
				"$.CalculationAttribute[?(@.name=='" + connectionType + "')]");
		JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
		return master.getAsString(SWCalculationConstant.ATTRIBUTE);
	}

	public String getAssessmentYear() {
		LocalDateTime localDateTime = LocalDateTime.now();
		int currentMonth = localDateTime.getMonthValue();
		String assessmentYear;
		if (currentMonth >= 4) {
			assessmentYear = YearMonth.now().getYear() + "-";
			assessmentYear = assessmentYear
					+ (Integer.toString(YearMonth.now().getYear() + 1).substring(2, assessmentYear.length() - 1));
		} else {
			assessmentYear = (YearMonth.now().getYear() - 1) + "-";
			assessmentYear = assessmentYear
					+ (Integer.toString(YearMonth.now().getYear()).substring(2, assessmentYear.length() - 1));
		}
		return assessmentYear;
	}

	/**
	 *
	 * @param assessmentYear - Assessment Year
	 * @param sewerageCharge - Sewerage Charge
	 * @param sewerageConnection - Sewerage connection Object
	 * @param timeBasedExemptionMasterMap - List of time based exemptions master data
	 * @param requestInfoWrapper - Request Info Object
	 * @return - Returns list of TaxHead estimates
	 */
	@SuppressWarnings("unused")
	private List<TaxHeadEstimate> getEstimatesForTax(String assessmentYear, BigDecimal sewerageCharge,
			SewerageConnection sewerageConnection, Map<String, JSONArray> timeBasedExemptionMasterMap,
			RequestInfoWrapper requestInfoWrapper) {
		List<TaxHeadEstimate> estimates = new ArrayList<>();
		// sewerage_charge
		estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_CHARGE)
				.estimateAmount(sewerageCharge.setScale(2, 2)).build());
		return estimates;
	}

	/**
	 * 
	 * @param sewerageConnection - Sewerage Connection Object
	 * @param billingSlabs - List of Billing Slabs
	 * @param requestInfo - Request Info Object
	 * @return List of billing slab based on matching criteria
	 */
	private List<BillingSlab> getSlabsFiltered(SewerageConnectionRequest sewerageConnection, List<BillingSlab> billingSlabs,
			String calculationAttribute, RequestInfo requestInfo, Property property) {

		
//		SewerageConnectionRequest sewerageConnectionRequest = SewerageConnectionRequest.builder()
//				.sewerageConnection(sewerageConnection).requestInfo(requestInfo).build();
//		property = sWCalculationUtil.getProperty(sewerageConnectionRequest);
		

		// get billing Slab
		log.debug(" the slabs count : " + billingSlabs.size());
		final String propertyType = (property.getUsageCategory() != null)
				? property.getUsageCategory().split("\\.")[property.getUsageCategory().split("\\.").length - 1] : "";
		HashMap<String, Object> additionalDetail = new HashMap<>();
		additionalDetail = mapper.convertValue(sewerageConnection.getSewerageConnection().getAdditionalDetails(),
				HashMap.class);

		final String buildingType = SWCalculationConstant.PROPERTY_TYPE_MIXED.equalsIgnoreCase(propertyType)
				? (String) additionalDetail.getOrDefault(SWCalculationConstant.UNIT_USAGE_TYPE_KEY, null)
				: propertyType;
	
		final String connectionType = sewerageConnection.getSewerageConnection().getConnectionType();

		return billingSlabs.stream().filter(slab -> {
			boolean isBuildingTypeMatching = slab.getBuildingType().equalsIgnoreCase(buildingType);
			boolean isConnectionTypeMatching = slab.getConnectionType().equalsIgnoreCase(connectionType);
			boolean isCalculationAttributeMatching = slab.getCalculationAttribute()
					.equalsIgnoreCase(calculationAttribute);
			
			log.info("BuildingTypeMatching: " + slab.getBuildingType() + "buildingType: " + buildingType +
					"connectionType: " + connectionType + "calculationAttribute: " + calculationAttribute);

			log.info("isBuildingTypeMatching: " +isBuildingTypeMatching+" isConnectionTypeMatching: "
					+isConnectionTypeMatching+" isCalculationAttributeMatching: "+isCalculationAttributeMatching);
			return isBuildingTypeMatching && isConnectionTypeMatching && isCalculationAttributeMatching;
		}).collect(Collectors.toList());
	}

	private Double getCalculationUnit(SewerageConnection sewerageConnection, String calculationAttribute,
			CalculationCriteria criteria,Property property) {
		Double totalUnite = 0.0;
		if (sewerageConnection.getConnectionType().equals(SWCalculationConstant.meteredConnectionType)) {
			return totalUnite;
		} else if (sewerageConnection.getConnectionType().equals(SWCalculationConstant.nonMeterdConnection)
				&& calculationAttribute.equalsIgnoreCase(SWCalculationConstant.plotBasedConst)) {
			if (property.getLandArea() != null && property.getLandArea() > 0)
				return property.getLandArea();
			return new Double(sewerageConnection.getNoOfToilets());
		} else if (sewerageConnection.getConnectionType().equals(SWCalculationConstant.nonMeterdConnection)
				&& calculationAttribute.equalsIgnoreCase(SWCalculationConstant.noOfWaterClosets)) {
			if (sewerageConnection.getNoOfWaterClosets() == null)
				return totalUnite;
			return new Double(sewerageConnection.getNoOfWaterClosets());
		}
		return totalUnite;
	
	}

	/**
	 * 
	 * @param type
	 *            will be calculation Attribute
	 * @return true if calculation Attribute is not Flat else false
	 */
	private boolean isRangeCalculation(String type) {
		return !type.equalsIgnoreCase(SWCalculationConstant.flatRateCalculationAttribute);
	}

	@SuppressWarnings("rawtypes")
	public Map<String, List> getFeeEstimation(CalculationCriteria criteria, RequestInfo requestInfo,
			Map<String, Object> masterData) {
		String tenantId = requestInfo.getUserInfo().getTenantId();
		if (StringUtils.isEmpty(criteria.getSewerageConnection())
				&& !StringUtils.isEmpty(criteria.getApplicationNo())) {
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setApplicationNumber(criteria.getApplicationNo());
			searchCriteria.setTenantId(criteria.getTenantId());
			criteria.setSewerageConnection(
					calculatorUtil.getSewerageConnectionOnApplicationNO(requestInfo, searchCriteria, tenantId));
		}
		if (criteria.getSewerageConnection() == null) {
			throw new CustomException("SEWERAGE_CONNECTION_NOT_FOUND",
					"Sewerage Connection are not present for " + criteria.getApplicationNo() + " Application no");
		}
		ArrayList<String> billingSlabIds = new ArrayList<>();
		billingSlabIds.add("");
		List<TaxHeadEstimate> taxHeadEstimates = getTaxHeadForFeeEstimation(criteria, masterData, requestInfo);
		Map<String, List> estimatesAndBillingSlabs = new HashMap<>();
		estimatesAndBillingSlabs.put("estimates", taxHeadEstimates);
		// //Billing slab id
		estimatesAndBillingSlabs.put("billingSlabIds", billingSlabIds);
		return estimatesAndBillingSlabs;
	}

	/**
	 * 
	 * @param criteria - Calculation Criteria Object
	 * @param masterData - MDMS Master Data
	 * @param requestInfo - Request Info Object
	 * @return return all tax heads
	 */
	private List<TaxHeadEstimate> getTaxHeadForFeeEstimation(CalculationCriteria criteria,
			Map<String, Object> masterData, RequestInfo requestInfo) {
		JSONArray feeSlab = (JSONArray) masterData.getOrDefault(SWCalculationConstant.SC_FEESLAB_MASTER, null);
		if (feeSlab == null)
			throw new CustomException("FEE_SLAB_NOT_FOUND", "fee slab master data not found!!");

		Property property = sWCalculationUtil.getProperty(SewerageConnectionRequest.builder()
				.sewerageConnection(criteria.getSewerageConnection()).requestInfo(requestInfo).build());

		JSONObject feeObj = mapper.convertValue(feeSlab.get(0), JSONObject.class);
		BigDecimal formFee = BigDecimal.ZERO;
		if (feeObj.get(SWCalculationConstant.FORM_FEE_CONST) != null) {
			formFee = new BigDecimal(feeObj.getAsNumber(SWCalculationConstant.FORM_FEE_CONST).toString());
		}
//		BigDecimal scrutinyFee = BigDecimal.ZERO;
//		if (feeObj.get(SWCalculationConstant.SCRUTINY_FEE_CONST) != null) {
//			scrutinyFee = new BigDecimal(feeObj.getAsNumber(SWCalculationConstant.SCRUTINY_FEE_CONST).toString());
//		}
//		BigDecimal otherCharges = BigDecimal.ZERO;
//		if (feeObj.get(SWCalculationConstant.OTHER_CHARGE_CONST) != null) {
//			otherCharges = new BigDecimal(feeObj.getAsNumber(SWCalculationConstant.OTHER_CHARGE_CONST).toString());
		
		String connection_propertyType=((HashMap<String,String>)criteria.getSewerageConnection().getAdditionalDetails()).get("waterSubUsageType");
			
		BigDecimal securityDeposit = BigDecimal.ZERO;
		String tenantid= criteria.getTenantId();

		if(tenantid.equalsIgnoreCase("pb.patiala")) {
			if (feeObj.get(SWCalculationConstant.SW_SECURITY_DEPOSIT_CONST) != null) {
				if(connection_propertyType.contains("DOM") || connection_propertyType.contains("USAGE_RESIDENTIAL") )
					securityDeposit = new BigDecimal(1000.00);
				else 
					securityDeposit = new BigDecimal(
							feeObj.getAsNumber(SWCalculationConstant.SW_SECURITY_DEPOSIT_CONST).toString());
			}
		}else {
		
		if (feeObj.get(SWCalculationConstant.SW_SECURITY_DEPOSIT_CONST) != null) {
			securityDeposit = new BigDecimal(
					feeObj.getAsNumber(SWCalculationConstant.SW_SECURITY_DEPOSIT_CONST).toString());
		}
		}

		// 
		BigDecimal connectionFee = BigDecimal.ZERO;
		
		if (feeObj.get(SWCalculationConstant.SW_CONNECTION_FEE_CONST) != null) 
		{

			BigDecimal connection_plotSize;
			if(property.getLandArea()==null || property.getLandArea().equals("")) // in case of shared proprties landArea may not be present
				connection_plotSize=null;
			else
				connection_plotSize=new BigDecimal(property.getLandArea());
			
			if(connection_plotSize==null || connection_propertyType==null || connection_propertyType.equals(""))
				connection_propertyType="DEFAULT"; // default connectionFee to be applied from mdms
			else if(connection_propertyType.contains("DOM") || connection_propertyType.contains("USAGE_RESIDENTIAL") )
				connection_propertyType="DOMESTIC";
			else 
				connection_propertyType="COMMERCIAL";
			
			
			ArrayList conn_fees=(ArrayList)feeObj.get(SWCalculationConstant.SW_CONNECTION_FEE_CONST);
			
			BigDecimal fromPlotSize=BigDecimal.ZERO;
			BigDecimal toPlotSize=BigDecimal.ZERO;
			BigDecimal connectionFeeApplicable=BigDecimal.ZERO;
			String propertyType=null;
			
			HashMap<String,String> connFeeMap=null;
			for (int i=0;i<conn_fees.size();i++)
			{
			   connFeeMap=(HashMap<String,String>)conn_fees.get(i);
			   fromPlotSize=new BigDecimal(connFeeMap.get("fromPlotSize"));
			   toPlotSize=new BigDecimal(connFeeMap.get("toPlotSize"));
			   //connectionFeeApplicable=new BigDecimal(connFeeMap.get("connectionFee"));
			   propertyType=connFeeMap.get("usageType").toString();
			   if(propertyType.equals(connection_propertyType) &&  connection_plotSize.compareTo(fromPlotSize)>0 && connection_plotSize.compareTo(toPlotSize)<=0)
			   {
				   connectionFeeApplicable=new BigDecimal(connFeeMap.get("connectionFee"));
				   break; // matched the attributes and got valid connection fee
			   }
			   
			}
			
			
			//connectionFee = new BigDecimal(feeObj.getAsNumber(SWCalculationConstant.SW_CONNECTION_FEE_CONST).toString());
			connectionFee=connectionFeeApplicable;
		}

		
		BigDecimal otherCharges = BigDecimal.ZERO; 
		if(feeObj.get(SWCalculationConstant.OTHER_FEE_CONST) != null) {
			otherCharges = new BigDecimal(feeObj.getAsNumber(SWCalculationConstant.OTHER_FEE_CONST).toString());
		}
		 
		BigDecimal taxAndCessPercentage = BigDecimal.ZERO;
		if (feeObj.get(SWCalculationConstant.TAX_PERCENTAGE_CONST) != null) {
			taxAndCessPercentage = new BigDecimal(
					feeObj.getAsNumber(SWCalculationConstant.TAX_PERCENTAGE_CONST).toString());
		}
//		BigDecimal meterCost = BigDecimal.ZERO;
//		if (feeObj.get(SWCalculationConstant.METER_COST_CONST) != null
//				&& criteria.getSewerageConnection().getConnectionType() != null && criteria.getSewerageConnection()
//						.getConnectionType().equalsIgnoreCase(SWCalculationConstant.meteredConnectionType)) {
//			meterCost = new BigDecimal(feeObj.getAsNumber(SWCalculationConstant.METER_COST_CONST).toString());
//		}
		BigDecimal roadCuttingCharge = BigDecimal.ZERO;
		BigDecimal usageTypeCharge = BigDecimal.ZERO;

		if(criteria.getSewerageConnection().getRoadCuttingInfo() != null){
			for(RoadCuttingInfo roadCuttingInfo : criteria.getSewerageConnection().getRoadCuttingInfo()){
				BigDecimal singleRoadCuttingCharge = BigDecimal.ZERO;
				if (roadCuttingInfo.getRoadType() != null)
					singleRoadCuttingCharge = getChargeForRoadCutting(masterData, roadCuttingInfo.getRoadType(),
							roadCuttingInfo.getRoadCuttingArea());

				BigDecimal singleUsageTypeCharge = BigDecimal.ZERO;
				if (roadCuttingInfo.getRoadCuttingArea() != null)
					singleUsageTypeCharge = getUsageTypeFee(masterData,
							property.getUsageCategory(),
							roadCuttingInfo.getRoadCuttingArea());

				roadCuttingCharge = roadCuttingCharge.add(singleRoadCuttingCharge);
				usageTypeCharge = usageTypeCharge.add(singleUsageTypeCharge);
			}
		}


//		BigDecimal roadPlotCharge = BigDecimal.ZERO;
//		if (property.getLandArea() != null) {
//			roadPlotCharge = getPlotSizeFee(masterData, property.getLandArea());
//		}
//		BigDecimal usageTypeCharge = BigDecimal.ZERO;
//		if (criteria.getSewerageConnection().getRoadCuttingArea() != null) {
//			usageTypeCharge = getUsageTypeFee(masterData,
//					property.getUsageCategory(),
//					criteria.getSewerageConnection().getRoadCuttingArea());
//		}

		BigDecimal totalCharge = formFee.add(securityDeposit).add(roadCuttingCharge).add(connectionFee).add(otherCharges);
//				.add(meterCost).add(roadPlotCharge).add(usageTypeCharge);
//		BigDecimal totalCharge = formFee.add(scrutinyFee).add(otherCharges).add(meterCost).add(roadCuttingCharge)
//				.add(roadPlotCharge).add(usageTypeCharge);
		BigDecimal tax = totalCharge.multiply(taxAndCessPercentage.divide(SWCalculationConstant.HUNDRED));
		//
		List<TaxHeadEstimate> estimates = new ArrayList<>();
		//BigDecimal otherCharges=BigDecimal.ZERO;
		HashMap<String, Object> additionalDetails = mapper.convertValue(criteria.getSewerageConnection().getAdditionalDetails(),
				HashMap.class);
		String concategory = additionalDetails.get(SWCalculationConstant.connectionCategory) != null 
                ? additionalDetails.get(SWCalculationConstant.connectionCategory).toString() 
                : "null";
		// if (additionalDetails.get(SWCalculationConstant.connectionCategory).toString().equalsIgnoreCase("REGULARIZED")) {
                if (concategory.equalsIgnoreCase("REGULARIZED")) {
			  if (!(otherCharges.compareTo(BigDecimal.ZERO) == 0))
				  estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_OTHER_CHARGE) .estimateAmount(otherCharges.setScale(2, 2)).build());



		}
		else {
		if (!(formFee.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_FORM_FEE)
					.estimateAmount(formFee.setScale(2, 2)).build());
		if (!(securityDeposit.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_SECURITY_DEPOSIT)
					.estimateAmount(securityDeposit.setScale(2, 2)).build());
//		if (!(scrutinyFee.compareTo(BigDecimal.ZERO) == 0))
//			estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_SCRUTINY_FEE)
//					.estimateAmount(scrutinyFee.setScale(2, 2)).build());
		if (!(otherCharges.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_OTHER_CHARGE)
					.estimateAmount(otherCharges.setScale(2, 2)).build());
		if (!(roadCuttingCharge.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_ROAD_CUTTING_CHARGE)
					.estimateAmount(roadCuttingCharge.setScale(2, 2)).build());
		if (!(connectionFee.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_CONNECTION_FEE)
					.estimateAmount(connectionFee.setScale(2, 2)).build());
//		if (!(usageTypeCharge.compareTo(BigDecimal.ZERO) == 0))
//			estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_ONE_TIME_FEE)
//					.estimateAmount(usageTypeCharge.setScale(2, 2)).build());
//		if (!(roadPlotCharge.compareTo(BigDecimal.ZERO) == 0))
//			estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_SECURITY_CHARGE)
//					.estimateAmount(roadPlotCharge.setScale(2, 2)).build());
		if (!(tax.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_TAX_AND_CESS)
					.estimateAmount(tax.setScale(2, 2)).build());
		}
		addAdhocPenaltyAndRebate(estimates, criteria.getSewerageConnection());
		return estimates;
		
	} 

	
	/**
	 * Enrich the adhoc penalty and adhoc rebate
	 * 
	 * @param estimates
	 *            tax head estimate
	 * @param connection
	 *            water connection object
	 */
	@SuppressWarnings("unchecked")
	private void addAdhocPenaltyAndRebate(List<TaxHeadEstimate> estimates, SewerageConnection connection) {
		if (connection.getAdditionalDetails() != null) {
			HashMap<String, Object> additionalDetails = mapper.convertValue(connection.getAdditionalDetails(),
					HashMap.class);
			String concategory = additionalDetails.get(SWCalculationConstant.connectionCategory) != null 
	                ? additionalDetails.get(SWCalculationConstant.connectionCategory).toString() 
	                : "null";
			if (concategory.equalsIgnoreCase("REGULARIZED")) {
			// if (additionalDetails.get(SWCalculationConstant.connectionCategory).toString().equalsIgnoreCase("REGULARIZED")) {

				if (additionalDetails.getOrDefault(SWCalculationConstant.OTHER_FEE_CONST, null) != null) {
					estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.OTHER_FEE)
							.estimateAmount(
									new BigDecimal(additionalDetails.get(SWCalculationConstant.OTHER_FEE_CONST).toString()))
							.build());
				}
			}


			else {
			if (additionalDetails.getOrDefault(SWCalculationConstant.ADHOC_PENALTY, null) != null) {
				estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_ADHOC_PENALTY)
						.estimateAmount(
								new BigDecimal(additionalDetails.get(SWCalculationConstant.ADHOC_PENALTY).toString()))
						.build());
			}
			if (additionalDetails.getOrDefault(SWCalculationConstant.ADHOC_REBATE, null) != null) {
				estimates
						.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_ADHOC_REBATE)
								.estimateAmount(new BigDecimal(
										additionalDetails.get(SWCalculationConstant.ADHOC_REBATE).toString()).negate())
								.build());
			}
			if (additionalDetails.getOrDefault(SWCalculationConstant.COMPOSITION_FEE_CONST, null) != null) {
				estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.COMPOSITION_FEE)
						.estimateAmount(new BigDecimal(
								additionalDetails.get(SWCalculationConstant.COMPOSITION_FEE_CONST).toString()))
						.build());
			}
				// System.out.println(additionalDetails.get(SWCalculationConstant.connectionCategory).toString());
			if (additionalDetails.getOrDefault(SWCalculationConstant.USER_CHARGES_CONST, null) != null) {
				estimates
						.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.USER_CHARGES)
								.estimateAmount(new BigDecimal(
										additionalDetails.get(SWCalculationConstant.USER_CHARGES_CONST).toString()))
								.build());
			}

			if (additionalDetails.getOrDefault(SWCalculationConstant.OTHER_FEE_CONST, null) != null) {
				estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.OTHER_FEE)
						.estimateAmount(
								new BigDecimal(additionalDetails.get(SWCalculationConstant.OTHER_FEE_CONST).toString()))
						.build());
			}
				}
		}
	}
	/**
	 * 
	 * @param masterData - MDMS Master Data
	 * @param roadType - Road Type value
	 * @param roadCuttingArea - Road Cutting Area value
	 * @return road cutting charge
	 */
	private BigDecimal getChargeForRoadCutting(Map<String, Object> masterData, String roadType, Float roadCuttingArea) {
		JSONArray roadSlab = (JSONArray) masterData.getOrDefault(SWCalculationConstant.SC_ROADTYPE_MASTER, null);
		BigDecimal charge = BigDecimal.ZERO;
		BigDecimal cuttingArea = new BigDecimal(
				roadCuttingArea == null ? BigDecimal.ZERO.toString() : roadCuttingArea.toString());
		JSONObject masterSlab = new JSONObject();
		if (roadSlab != null) {
			masterSlab.put("RoadType", roadSlab);
			JSONArray filteredMasters = JsonPath.read(masterSlab, "$.RoadType[?(@.code=='" + roadType + "')]");
			if (CollectionUtils.isEmpty(filteredMasters))
				return charge;
			JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
			charge = new BigDecimal(master.getAsNumber(SWCalculationConstant.UNIT_COST_CONST).toString());
			charge = charge.multiply(cuttingArea);
		}
		return charge;
	}

	/**
	 * 
	 * @param masterData - MDMS Master Data
	 * @param plotSize - Plot Size
	 * @return get fee based on plot size
	 */
	private BigDecimal getPlotSizeFee(Map<String, Object> masterData, Double plotSize) {
		BigDecimal charge = BigDecimal.ZERO;
		JSONArray plotSlab = (JSONArray) masterData.getOrDefault(SWCalculationConstant.SC_PLOTSLAB_MASTER, null);
		JSONObject masterSlab = new JSONObject();
		if (plotSlab != null) {
			masterSlab.put("PlotSizeSlab", plotSlab);
			JSONArray filteredMasters = JsonPath.read(masterSlab,
					"$.PlotSizeSlab[?(@.from <=" + plotSize + "&& @.to > " + plotSize + ")]");
			if (CollectionUtils.isEmpty(filteredMasters))
				return charge;
			JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
			charge = new BigDecimal(master.getAsNumber(SWCalculationConstant.UNIT_COST_CONST).toString());
		}
		return charge;
	}

	/**
	 * 
	 * @param masterData - MDMS Master Data
	 * @param usageType - Usage Type
	 * @param roadCuttingArea - Road Cutting Area
	 * @return - Returns the Usage Type fee
	 */
	private BigDecimal getUsageTypeFee(Map<String, Object> masterData, String usageType, Float roadCuttingArea) {
		BigDecimal charge = BigDecimal.ZERO;
		JSONArray usageSlab = (JSONArray) masterData.getOrDefault(SWCalculationConstant.SC_PROPERTYUSAGETYPE_MASTER,
				null);
		JSONObject masterSlab = new JSONObject();
		BigDecimal cuttingArea = new BigDecimal(roadCuttingArea.toString());
		if (usageSlab != null) {
			masterSlab.put("PropertyUsageType", usageSlab);
			JSONArray filteredMasters = JsonPath.read(masterSlab,
					"$.PropertyUsageType[?(@.code=='" + usageType + "')]");
			if (CollectionUtils.isEmpty(filteredMasters))
				return charge;
			JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
			charge = new BigDecimal(master.getAsNumber(SWCalculationConstant.UNIT_COST_CONST).toString());
			charge = charge.multiply(cuttingArea);
		}
		return charge;
	}
	
	public Map<String, Object> getQuarterStartAndEndDate(Map<String, Object> billingPeriod){
		Date date = new Date();
		Calendar fromDateCalendar = Calendar.getInstance();
		fromDateCalendar.setTime(date);
		fromDateCalendar.set(Calendar.MONTH, fromDateCalendar.get(Calendar.MONTH)/3 * 3);
		fromDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
		setTimeToBeginningOfDay(fromDateCalendar);
		Calendar toDateCalendar = Calendar.getInstance();
		toDateCalendar.setTime(date);
		toDateCalendar.set(Calendar.MONTH, toDateCalendar.get(Calendar.MONTH)/3 * 3 + 2);
		toDateCalendar.set(Calendar.DAY_OF_MONTH, toDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		setTimeToEndOfDay(toDateCalendar);
		billingPeriod.put(SWCalculationConstant.STARTING_DATE_APPLICABLES, fromDateCalendar.getTimeInMillis());
		billingPeriod.put(SWCalculationConstant.ENDING_DATE_APPLICABLES, toDateCalendar.getTimeInMillis());
		return billingPeriod;
	}
	
	public Map<String, Object> getMonthStartAndEndDate(Map<String, Object> billingPeriod){
		Date date = new Date();
		Calendar monthStartDate = Calendar.getInstance();
		monthStartDate.setTime(date);
		monthStartDate.set(Calendar.DAY_OF_MONTH, monthStartDate.getActualMinimum(Calendar.DAY_OF_MONTH));
		setTimeToBeginningOfDay(monthStartDate);
	    
		Calendar monthEndDate = Calendar.getInstance();
		monthEndDate.setTime(date);
		monthEndDate.set(Calendar.DAY_OF_MONTH, monthEndDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		setTimeToEndOfDay(monthEndDate);
		billingPeriod.put(SWCalculationConstant.STARTING_DATE_APPLICABLES, monthStartDate.getTimeInMillis());
		billingPeriod.put(SWCalculationConstant.ENDING_DATE_APPLICABLES, monthEndDate.getTimeInMillis());
		return billingPeriod;
	}
	
	private static void setTimeToBeginningOfDay(Calendar calendar) {
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	}

	private static void setTimeToEndOfDay(Calendar calendar) {
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	}
	
	public Map<String, List> getReconnectionFeeEstimation(CalculationCriteria criteria, RequestInfo requestInfo, Map<String, Object> masterData ) {
		String tenantId = requestInfo.getUserInfo().getTenantId();
		if (StringUtils.isEmpty(criteria.getSewerageConnection()) && !StringUtils.isEmpty(criteria.getApplicationNo())) {
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setApplicationNumber(criteria.getApplicationNo());
			searchCriteria.setTenantId(criteria.getTenantId());
			criteria.setSewerageConnection(
					calculatorUtil.getSewerageConnectionOnApplicationNO(requestInfo, searchCriteria, tenantId));	
		}

		if (criteria.getSewerageConnection() == null) {
			throw new CustomException("SEWERAGE_CONNECTION_NOT_FOUND",
					"Sewerage Connection are not present for " + criteria.getApplicationNo() + " Application no");
		}
		List<TaxHeadEstimate> taxHeadEstimates = getTaxHeadForReconnectionFeeEstimationV2(criteria,masterData, requestInfo);
		Map<String, List> estimatesAndBillingSlabs = new HashMap<>();
		estimatesAndBillingSlabs.put("estimates", taxHeadEstimates);
		return estimatesAndBillingSlabs;
	}

	private List<TaxHeadEstimate> getTaxHeadForReconnectionFeeEstimationV2(CalculationCriteria criteria,
			Map<String, Object> masterData, RequestInfo requestInfo) {
		JSONArray feeSlab = (JSONArray) masterData.getOrDefault(SWCalculationConstant.SC_FEESLAB_MASTER, null);
		if (feeSlab == null)
			throw new CustomException("FEE_SLAB_NOT_FOUND", "fee slab master data not found!!"); 
		
		JSONObject feeObj = mapper.convertValue(feeSlab.get(0), JSONObject.class);
		BigDecimal reconnectionCharge = BigDecimal.ZERO;
		
		if (feeObj.get(SWCalculationConstant.RECONNECTION_FEE_CONST) != null) {
			reconnectionCharge = new BigDecimal(feeObj.getAsNumber(SWCalculationConstant.RECONNECTION_FEE_CONST).toString());
		}
		
		List<TaxHeadEstimate> estimates = new ArrayList<>();

		estimates.add(TaxHeadEstimate.builder().taxHeadCode(SWCalculationConstant.SW_RECONNECTION_CHARGE)
				.estimateAmount(reconnectionCharge).build());
		return estimates;

	}
}
