package org.egov.wscalculation.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.web.models.*;
import org.egov.wscalculation.util.CalculatorUtil;
import org.egov.wscalculation.util.WSCalculationUtil;
import org.egov.wscalculation.util.WaterCessUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Service
@Slf4j
public class EstimationService {

	@Autowired
	private WaterCessUtil waterCessUtil;
	
	@Autowired
	private CalculatorUtil calculatorUtil;
	

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private WSCalculationUtil wSCalculationUtil;
	
	@Autowired
	private PayService payService;

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
		String tenantId = request.getRequestInfo().getUserInfo().getTenantId();
		if (criteria.getWaterConnection() == null && !StringUtils.isEmpty(criteria.getConnectionNo())) {
			List<WaterConnection> waterConnectionList = calculatorUtil.getWaterConnection(request.getRequestInfo(),
					criteria.getConnectionNo(), tenantId);
			WaterConnection waterConnection = calculatorUtil.getWaterConnectionObject(waterConnectionList);
			criteria.setWaterConnection(waterConnection);
		}
		if (criteria.getWaterConnection() == null || StringUtils.isEmpty(criteria.getConnectionNo())) {
			StringBuilder builder = new StringBuilder();
			builder.append("Water Connection are not present for ")
					.append(StringUtils.isEmpty(criteria.getConnectionNo()) ? "" : criteria.getConnectionNo())
					.append(" connection no");
			throw new CustomException("WATER_CONNECTION_NOT_FOUND", builder.toString());
		}
		Map<String, JSONArray> billingSlabMaster = new HashMap<>();
		Map<String, JSONArray> timeBasedExemptionMasterMap = new HashMap<>();
		ArrayList<String> billingSlabIds = new ArrayList<>();
		billingSlabMaster.put(WSCalculationConstant.WC_BILLING_SLAB_MASTER,
				(JSONArray) masterData.get(WSCalculationConstant.WC_BILLING_SLAB_MASTER));
		billingSlabMaster.put(WSCalculationConstant.CALCULATION_ATTRIBUTE_CONST,
				(JSONArray) masterData.get(WSCalculationConstant.CALCULATION_ATTRIBUTE_CONST));
		timeBasedExemptionMasterMap.put(WSCalculationConstant.WC_WATER_CESS_MASTER,
				(JSONArray) (masterData.getOrDefault(WSCalculationConstant.WC_WATER_CESS_MASTER, null)));
		timeBasedExemptionMasterMap.put(WSCalculationConstant.WC_REBATE_MASTER,
				(JSONArray) (masterData.getOrDefault(WSCalculationConstant.WC_REBATE_MASTER, null)));
		for (Map.Entry<String,JSONArray> entry : billingSlabMaster.entrySet()) {
	            log.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
	     }
		
		// mDataService.setWaterConnectionMasterValues(requestInfo, tenantId,
		// billingSlabMaster,
		// timeBasedExemptionMasterMap);
		BigDecimal taxAmt = getWaterEstimationCharge(criteria.getWaterConnection(), criteria, billingSlabMaster,
				billingSlabIds, request);
		List<TaxHeadEstimate> taxHeadEstimates = getEstimatesForTax(taxAmt, criteria.getWaterConnection(),
				timeBasedExemptionMasterMap,
				RequestInfoWrapper.builder().requestInfo(request.getRequestInfo()).build());

		Map<String, List> estimatesAndBillingSlabs = new HashMap<>();
		estimatesAndBillingSlabs.put("estimates", taxHeadEstimates);
		// Billing slab id
		estimatesAndBillingSlabs.put("billingSlabIds", billingSlabIds);
		
		for(Map.Entry<String, List> ent : estimatesAndBillingSlabs.entrySet()) {
			  log.info(" estimatesAndBillingSlabs Key = " + ent.getKey() + ", Value = " + ent.getValue());
		}
		return estimatesAndBillingSlabs;
	}

	/**
	 * 
	 * @param waterCharge WaterCharge amount
	 * @param connection - Connection Object
	 * @param timeBasedExemptionsMasterMap List of Exemptions for the connection
	 * @param requestInfoWrapper - RequestInfo Wrapper object
	 * @return - Returns list of TaxHeadEstimates
	 */
	private List<TaxHeadEstimate> getEstimatesForTax(BigDecimal waterCharge,
			WaterConnection connection,
			Map<String, JSONArray> timeBasedExemptionsMasterMap, RequestInfoWrapper requestInfoWrapper) {
		List<TaxHeadEstimate> estimates = new ArrayList<>();
		// water_charge
		estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_CHARGE)
				.estimateAmount(waterCharge.setScale(2, 2)).build());

		// Water_cess
		if (timeBasedExemptionsMasterMap.get(WSCalculationConstant.WC_WATER_CESS_MASTER) != null) {
			List<Object> waterCessMasterList = timeBasedExemptionsMasterMap
					.get(WSCalculationConstant.WC_WATER_CESS_MASTER);
			BigDecimal waterCess;
			waterCess = waterCessUtil.getWaterCess(waterCharge, WSCalculationConstant.Assessment_Year, waterCessMasterList);
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_WATER_CESS)
					.estimateAmount(waterCess.setScale(2, 2)).build());
		}
		

		if (timeBasedExemptionsMasterMap.get(WSCalculationConstant.WC_REBATE_MASTER) != null) {
			BigDecimal rebate;
			rebate = payService.getApplicableRebate(waterCharge,null,  timeBasedExemptionsMasterMap.get(WSCalculationConstant.WC_REBATE_MASTER));
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_TIME_REBATE)
					.estimateAmount(rebate.negate().setScale(2, 2)).build());
		}
		
		return estimates;
	}

	/**
	 * method to do a first level filtering on the slabs based on the values
	 * present in the Water Details
	 */

	@SuppressWarnings("unchecked")
	public BigDecimal getWaterEstimationCharge(WaterConnection waterConnection, CalculationCriteria criteria,
			Map<String, JSONArray> billingSlabMaster, ArrayList<String> billingSlabIds, CalculationReq request) {
		BigDecimal waterCharge = BigDecimal.ZERO;
		NumberFormat formatter = new DecimalFormat("#0.00");
		MathContext m = new MathContext(2); 
		HashMap<String, Object> additionalDetail = new HashMap<>();
		additionalDetail = mapper.convertValue(waterConnection.getAdditionalDetails(), HashMap.class);
		try {
			log.info("Water Connection Object in estimation service : " + mapper.writeValueAsString(waterConnection));
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String billingType = (String) additionalDetail.getOrDefault(WSCalculationConstant.BILLINGTYPE, null);
		if (waterConnection.getConnectionType().equalsIgnoreCase(WSCalculationConstant.nonMeterdConnection)
				&& billingType.equalsIgnoreCase(WSCalculationConstant.CUSTOM)) {
			Integer billingAmountInt  = 0;
			Object customAmountObj = additionalDetail.getOrDefault(WSCalculationConstant.CUSTOM_BILL_AMOUNT, 0);
			if ( customAmountObj instanceof String) {
				billingAmountInt = Integer.parseInt((String)customAmountObj);
			}else {
				billingAmountInt = (Integer) customAmountObj;
			}
//			Integer billingAmountInt = (Integer) additionalDetail.getOrDefault(WSCalculationConstant.CUSTOM_BILL_AMOUNT, 0);
			BigDecimal customWaterCharges = BigDecimal.valueOf(Long.valueOf(billingAmountInt)).setScale(2, 2);
			return customWaterCharges;
		} else {
			if (billingSlabMaster.get(WSCalculationConstant.WC_BILLING_SLAB_MASTER) == null)
				throw new CustomException("BILLING_SLAB_NOT_FOUND", "Billing Slab are Empty");
			List<BillingSlab> mappingBillingSlab;
			try {
				mappingBillingSlab = mapper.readValue(
						billingSlabMaster.get(WSCalculationConstant.WC_BILLING_SLAB_MASTER).toJSONString(),
						mapper.getTypeFactory().constructCollectionType(List.class, BillingSlab.class));
			} catch (IOException e) {
				throw new CustomException("PARSING_ERROR", "Billing Slab can not be parsed!");
			}
			Property property = wSCalculationUtil.getProperty(
					WaterConnectionRequest.builder().waterConnection(waterConnection).requestInfo(request.getRequestInfo()).build());
			
			JSONObject calculationAttributeMaster = new JSONObject();
			calculationAttributeMaster.put(WSCalculationConstant.CALCULATION_ATTRIBUTE_CONST,
					billingSlabMaster.get(WSCalculationConstant.CALCULATION_ATTRIBUTE_CONST));
			String calculationAttribute = getCalculationAttribute(calculationAttributeMaster,
					waterConnection.getConnectionType());
			log.info("billingSlabMaster: "+billingSlabMaster);
			log.info("mappingBillingSlab:"+mappingBillingSlab+"calculationAttribute: "+calculationAttribute+" calculationAttributeMaster {}:",calculationAttributeMaster);
			List<BillingSlab> billingSlabs = getSlabsFiltered(property, waterConnection, mappingBillingSlab, calculationAttribute);
			
			if (billingSlabs == null || billingSlabs.isEmpty())
				throw new CustomException("BILLING_SLAB_NOT_FOUND", "Billing Slab are Empty");
			/*
			 * if (billingSlabs.size() > 1) throw new
			 * CustomException("INVALID_BILLING_SLAB", "More than one billing slab found");
			 */
			billingSlabIds.add(billingSlabs.get(0).getId());
			log.debug(" Billing Slab Id For Water Charge Calculation --->  " + billingSlabIds.toString());

			// WaterCharge Calculation
			Double totalUOM = getUnitOfMeasurement(property, waterConnection, calculationAttribute, criteria);
//			if (totalUOM == 0.0)
//				return waterCharge;
			BillingSlab billSlab = billingSlabs.get(0);
			
			log.info("totalUOM: " + totalUOM);

			log.info("Before billingslab  filter: " + billSlab.toString());

			List<Slab> filteredSlabs = billSlab.getSlabs().stream()
					.filter(slab -> slab.getFrom() <= totalUOM && slab.getTo() >= totalUOM
							&& slab.getFrom() <= System.currentTimeMillis()
							&& slab.getTo() >= System.currentTimeMillis())
					.collect(Collectors.toList());
			log.info("After billingslab  filter: " + filteredSlabs.size());
			// IF calculation type is flat then take flat rate else take slab and calculate
			// the charge
			// For metered connection calculation on graded fee slab
			// For Non metered connection calculation on normal connection
			if (isRangeCalculation(calculationAttribute)) {
				if (waterConnection.getConnectionType().equalsIgnoreCase(WSCalculationConstant.meteredConnectionType)) {
					String meterStatus = criteria.getMeterStatus().toString();

					waterCharge = waterCharge.add(BigDecimal.valueOf(totalUOM * filteredSlabs.get(0).getCharge()));

					if (meterStatus.equalsIgnoreCase(WSCalculationConstant.LOCKED)) {
						waterCharge = BigDecimal.valueOf(billSlab.getMinimumCharge());
					}

					if (meterStatus.equalsIgnoreCase(WSCalculationConstant.NO_METER)
							|| meterStatus.equalsIgnoreCase(WSCalculationConstant.BREAKDOWN)) {

						Double avarageMeterReading = (Double) additionalDetail
								.getOrDefault(WSCalculationConstant.AVARAGEMETERREADING, null);

						Double unitRate = (Double) filteredSlabs.get(0).getCharge();

						if (avarageMeterReading != null) {
							waterCharge = BigDecimal.valueOf(avarageMeterReading * unitRate);
							BigDecimal b2 = waterCharge.round(m); 
							waterCharge = BigDecimal.valueOf((Double.valueOf(formatter.format(b2))));
						}

					}

					if (billSlab.getMinimumCharge() > waterCharge.doubleValue()) {
						waterCharge = BigDecimal.valueOf(billSlab.getMinimumCharge());
					}

				} else if (waterConnection.getConnectionType()
						.equalsIgnoreCase(WSCalculationConstant.nonMeterdConnection)) {
					request.setTaxPeriodFrom(criteria.getFrom());
					request.setTaxPeriodTo(criteria.getTo());
						if (request.getTaxPeriodFrom() > 0 && request.getTaxPeriodTo() > 0) {
						if (waterConnection.getConnectionExecutionDate() > request.getTaxPeriodFrom()) {
							// Added pro rating
							long milli_sec_btw_conn_date = Math.abs(request.getTaxPeriodTo() - waterConnection.getConnectionExecutionDate());
							long milli_sec_btw_quarter = Math.abs(request.getTaxPeriodTo() - request.getTaxPeriodFrom());
							//Converting milli seconds to days
						    long days_conn_date = TimeUnit.MILLISECONDS.toDays(milli_sec_btw_conn_date) + 1;
						    long days_quarter = TimeUnit.MILLISECONDS.toDays(milli_sec_btw_quarter) + 1;

							// waterCharge = waterCharge.add(BigDecimal.valueOf(days_conn_date * (filteredSlabs.get(0).getCharge() / days_quarter)).setScale(2, 2));
							waterCharge = BigDecimal.valueOf(days_conn_date * (filteredSlabs.get(0).getCharge() / days_quarter)).setScale(2, 2);
//							double daysFactor = ((request.getTaxPeriodTo() - waterConnection.getConnectionExecutionDate())
//									/ (request.getTaxPeriodTo() - request.getTaxPeriodFrom())); 
//							waterCharge = waterCharge
//									.add(BigDecimal.valueOf(filteredSlabs.get(0).getCharge() * daysFactor));
						} else {

							waterCharge = waterCharge
									.add(BigDecimal.valueOf(filteredSlabs.get(0).getCharge()));
						}

					} else {
						waterCharge = waterCharge.add(BigDecimal.valueOf(filteredSlabs.get(0).getCharge()));

					}
//					waterCharge = waterCharge.add(BigDecimal.valueOf(filteredSlabs.get(0).getCharge()));
					/**
					 * Below 'if' statement is used to calculate the rate...
					 * if water charge is less than minimum charge.
					 */
					if (billSlab.getMinimumCharge() > waterCharge.doubleValue()) {
						waterCharge = BigDecimal.valueOf(billSlab.getMinimumCharge());
					}

				}
			} else {
				waterCharge = BigDecimal.valueOf(billSlab.getMinimumCharge());
			}
			return waterCharge;
		}
	}

	@SuppressWarnings("unchecked")
	private List<BillingSlab> getSlabsFiltered(Property property, WaterConnection waterConnection, List<BillingSlab> billingSlabs,
			String calculationAttribute) {

		
		// get billing Slab
		log.debug(" the slabs count : " + billingSlabs.size());
		final String propertyType = (property.getUsageCategory() != null) ? property.getUsageCategory().split("\\.")[property.getUsageCategory().split("\\.").length-1]: "";
		log.info("propertyType: "+propertyType );
		// final String buildingType = "Domestic";
		final String connectionType = waterConnection.getConnectionType();

		HashMap<String, Object> additionalDetail = new HashMap<>();
		additionalDetail = mapper.convertValue(waterConnection.getAdditionalDetails(), HashMap.class);
		final String waterSubUsageType = (String) additionalDetail
				.getOrDefault(WSCalculationConstant.WATER_SUBUSAGE_TYPE, null);

		final String buildingType = WSCalculationConstant.PROPERTY_TYPE_MIXED.equalsIgnoreCase(propertyType)
				? (String) additionalDetail.getOrDefault(WSCalculationConstant.UNIT_USAGE_TYPE_KEY, null)
				: propertyType;

		// For water mix building type giving null ass unit usages type is missing in  additional detail of ws application
// 				 String finalbuildingType = null;
// 		 System.out.println((String) additionalDetail.getOrDefault(WSCalculationConstant.WATER_SUBUSAGE_TYPE, null));
//  if(waterSubUsageType.equalsIgnoreCase( WSCalculationConstant.PROPERTY_SUB_DOMESTIC_TYPE_MIXED ) && propertyType.equalsIgnoreCase(WSCalculationConstant.PROPERTY_TYPE_MIXED))
//  {
// 	 finalbuildingType = propertyType;
//  }
//  else if(waterSubUsageType.equalsIgnoreCase( WSCalculationConstant.PROPERTY_SUB_COMMERCIAL_TYPE_MIXED ) && propertyType.equalsIgnoreCase(WSCalculationConstant.PROPERTY_TYPE_MIXED))
//  {
// 	 finalbuildingType = propertyType;
// }
//  else if(waterSubUsageType!=WSCalculationConstant.PROPERTY_SUB_DOMESTIC_TYPE_MIXED && propertyType.equalsIgnoreCase(WSCalculationConstant.PROPERTY_TYPE_MIXED))
//  {
// 	 finalbuildingType =null; 
//  }
// else if (waterSubUsageType!=WSCalculationConstant.PROPERTY_SUB_COMMERCIAL_TYPE_MIXED && propertyType.equalsIgnoreCase(WSCalculationConstant.PROPERTY_TYPE_MIXED))
//  {
// 	 finalbuildingType =null; 
//  }
//  else
//  {
// 	 finalbuildingType = propertyType;
//  }
 
//  final String buildingType=finalbuildingType;
		return billingSlabs.stream().filter(slab -> {
			boolean isBuildingTypeMatching = slab.getBuildingType().equalsIgnoreCase(buildingType);
			boolean isConnectionTypeMatching = slab.getConnectionType().equalsIgnoreCase(connectionType);
			boolean isCalculationAttributeMatching = slab.getCalculationAttribute()
					.equalsIgnoreCase(calculationAttribute);
			log.info("BuildingTypeMatching: " + slab.getBuildingType() + "buildingType: " + buildingType +
					"connectionType: " + connectionType + "calculationAttribute: " + calculationAttribute);

			log.info("isBuildingTypeMatching: " +isBuildingTypeMatching+" isConnectionTypeMatching: "
					+isConnectionTypeMatching+" isCalculationAttributeMatching: "+isCalculationAttributeMatching + 
					" isWaterSubUsageType: "+waterSubUsageType);

// 			if (waterSubUsageType != null) {
// 				boolean isWaterSubUsageType = slab.getWaterSubUsageType().equalsIgnoreCase(waterSubUsageType);
// 				return isBuildingTypeMatching && isConnectionTypeMatching && isCalculationAttributeMatching
// 						&& isWaterSubUsageType;
// 			}
			return isBuildingTypeMatching && isConnectionTypeMatching && isCalculationAttributeMatching;
		}).collect(Collectors.toList());
	}
	
	private String getCalculationAttribute(Map<String, Object> calculationAttributeMap, String connectionType) {
		if (calculationAttributeMap == null)
			throw new CustomException("CALCULATION_ATTRIBUTE_MASTER_NOT_FOUND",
					"Calculation attribute master not found!!");
		JSONArray filteredMasters = JsonPath.read(calculationAttributeMap,
				"$.CalculationAttribute[?(@.name=='" + connectionType + "')]");
		if (!CollectionUtils.isEmpty(filteredMasters)) {
			JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
			return master.getAsString(WSCalculationConstant.ATTRIBUTE);
		} else {
			throw new CustomException("CALCULATION_ATTRIBUTE_MASTER_NOT_FOUND",
					"Calculation attribute master not found the connection type :" + connectionType);
		}
	}
	
	/**
	 * 
	 * @param type will be calculation Attribute
	 * @return true if calculation Attribute is not Flat else false
	 */
	private boolean isRangeCalculation(String type) {
		return !type.equalsIgnoreCase(WSCalculationConstant.flatRateCalculationAttribute);
	}
	
	public String getAssessmentYear() {
		LocalDateTime localDateTime = LocalDateTime.now();
		int currentMonth = localDateTime.getMonthValue();
		String assessmentYear;
		if (currentMonth >= Month.APRIL.getValue()) {
			assessmentYear = YearMonth.now().getYear() + "-";
			assessmentYear = assessmentYear
					+ (Integer.toString(YearMonth.now().getYear() + 1).substring(2, assessmentYear.length() - 1));
		} else {
			assessmentYear = YearMonth.now().getYear() - 1 + "-";
			assessmentYear = assessmentYear
					+ (Integer.toString(YearMonth.now().getYear()).substring(2, assessmentYear.length() - 1));

		}
		return assessmentYear;
	}
	
	private Double getUnitOfMeasurement(Property property, WaterConnection waterConnection, String calculationAttribute,
			CalculationCriteria criteria) {
		Double totalUnit = 0.0;
		if (waterConnection.getConnectionType().equals(WSCalculationConstant.meteredConnectionType)) {
			totalUnit = (criteria.getCurrentReading() - criteria.getLastReading());
			return totalUnit;
		} else if (waterConnection.getConnectionType().equals(WSCalculationConstant.nonMeterdConnection)
				&& calculationAttribute.equalsIgnoreCase(WSCalculationConstant.noOfTapsConst)) {
			if (waterConnection.getNoOfTaps() != null && waterConnection.getNoOfTaps() > 0) 
			return new Double(waterConnection.getNoOfTaps());
		} else if (waterConnection.getConnectionType().equals(WSCalculationConstant.nonMeterdConnection)
				&& calculationAttribute.equalsIgnoreCase(WSCalculationConstant.pipeSizeConst)) {
			if (waterConnection.getPipeSize() == null && waterConnection.getPipeSize() > 0)
			return waterConnection.getPipeSize();
		} else if (waterConnection.getConnectionType().equals(WSCalculationConstant.nonMeterdConnection)
				&& calculationAttribute.equalsIgnoreCase(WSCalculationConstant.plotBasedConst)) {
			if (property.getLandArea() != null && property.getLandArea() > 0)
				return property.getLandArea();
		}
		return 0.0;
	}
	
	private Double getUnitOfMeasurement(WaterConnection waterConnection, String calculationAttribute,
			CalculationCriteria criteria) {
		Double totalUnit = 0.0;
		if (waterConnection.getConnectionType().equals(WSCalculationConstant.meteredConnectionType)) {
			totalUnit = (criteria.getCurrentReading() - criteria.getLastReading());
			return totalUnit;
		} else if (waterConnection.getConnectionType().equals(WSCalculationConstant.nonMeterdConnection)
				&& calculationAttribute.equalsIgnoreCase(WSCalculationConstant.noOfTapsConst)) {
			if (waterConnection.getNoOfTaps() == null)
				return totalUnit;
			return new Double(waterConnection.getNoOfTaps());
		} else if (waterConnection.getConnectionType().equals(WSCalculationConstant.nonMeterdConnection)
				&& calculationAttribute.equalsIgnoreCase(WSCalculationConstant.pipeSizeConst)) {
			if (waterConnection.getPipeSize() == null)
				return totalUnit;
			return waterConnection.getPipeSize();
		}
		return 0.0;
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
		setTimeToEndofDay(toDateCalendar);
		billingPeriod.put(WSCalculationConstant.STARTING_DATE_APPLICABLES, fromDateCalendar.getTimeInMillis());
		billingPeriod.put(WSCalculationConstant.ENDING_DATE_APPLICABLES, toDateCalendar.getTimeInMillis());
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
		setTimeToEndofDay(monthEndDate);
		billingPeriod.put(WSCalculationConstant.STARTING_DATE_APPLICABLES, monthStartDate.getTimeInMillis());
		billingPeriod.put(WSCalculationConstant.ENDING_DATE_APPLICABLES, monthEndDate.getTimeInMillis());
		return billingPeriod;
	}
	
	private static void setTimeToBeginningOfDay(Calendar calendar) {
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	}

	private static void setTimeToEndofDay(Calendar calendar) {
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	}
	
	
	/**
	 * 
	 * @param criteria - Calculation Search Criteria
	 * @param requestInfo - Request Info Object
	 * @param masterData - Master Data map
	 * @return Fee Estimation Map
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, List> getFeeEstimation(CalculationCriteria criteria, RequestInfo requestInfo,
			Map<String, Object> masterData) {
		if (StringUtils.isEmpty(criteria.getWaterConnection()) && !StringUtils.isEmpty(criteria.getApplicationNo())) {
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setApplicationNumber(criteria.getApplicationNo());
			searchCriteria.setTenantId(criteria.getTenantId());
			WaterConnection waterConnection = calculatorUtil.getWaterConnectionOnApplicationNO(requestInfo, searchCriteria, requestInfo.getUserInfo().getTenantId());
			criteria.setWaterConnection(waterConnection);
		}
		if (StringUtils.isEmpty(criteria.getWaterConnection())) {
			throw new CustomException("WATER_CONNECTION_NOT_FOUND",
					"Water Connection are not present for " + criteria.getApplicationNo() + " Application no");
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
	 * @param criteria Calculation Search Criteria
	 * @param masterData - Master Data
	 * @param requestInfo - RequestInfo
	 * @return return all tax heads
	 */
	private List<TaxHeadEstimate> getTaxHeadForFeeEstimation(CalculationCriteria criteria,
			Map<String, Object> masterData, RequestInfo requestInfo) {
		JSONArray feeSlab = (JSONArray) masterData.getOrDefault(WSCalculationConstant.WC_FEESLAB_MASTER, null);
		if (feeSlab == null)
			throw new CustomException("FEE_SLAB_NOT_FOUND", "fee slab master data not found!!");
		
		Property property = wSCalculationUtil.getProperty(WaterConnectionRequest.builder()
				.waterConnection(criteria.getWaterConnection()).requestInfo(requestInfo).build());
		
		JSONObject feeObj = mapper.convertValue(feeSlab.get(0), JSONObject.class);
		BigDecimal formFee = BigDecimal.ZERO;
		if (feeObj.get(WSCalculationConstant.FORM_FEE_CONST) != null) {
			formFee = new BigDecimal(feeObj.getAsNumber(WSCalculationConstant.FORM_FEE_CONST).toString());
		}

//		BigDecimal scrutinyFee = BigDecimal.ZERO;
//		if (feeObj.get(WSCalculationConstant.SCRUTINY_FEE_CONST) != null) {
//			scrutinyFee = new BigDecimal(feeObj.getAsNumber(WSCalculationConstant.SCRUTINY_FEE_CONST).toString());
//		}

		BigDecimal securityCharge = BigDecimal.ZERO;
		if (feeObj.get(WSCalculationConstant.WS_SECURITY_CHARGE_CONST) != null) {
			securityCharge = new BigDecimal(
					feeObj.getAsNumber(WSCalculationConstant.WS_SECURITY_CHARGE_CONST).toString());
		}
		
		//Connection Fee to be evaluated here  from mdms depending on plotsize

		BigDecimal connectionFee = BigDecimal.ZERO;
		if (feeObj.get(WSCalculationConstant.WS_CONNECTION_FEE_CONST) != null) {
			
			BigDecimal connection_plotSize;
			if(property.getLandArea()==null || property.getLandArea().equals("")) // in case of shared proprties landArea may not be present
				connection_plotSize=null;
			else
				connection_plotSize=new BigDecimal(property.getLandArea());
			
			String connection_propertyType=((HashMap<String,String>)criteria.getWaterConnection().getAdditionalDetails()).get("waterSubUsageType");
			if(connection_plotSize==null || connection_propertyType==null || connection_propertyType.equals(""))
				connection_propertyType="DEFAULT"; // default connectionFee to be applied from mdms
			else if(connection_propertyType.contains("DOM") || connection_propertyType.contains("USAGE_RESIDENTIAL") )
				connection_propertyType="DOMESTIC";
			else 
				connection_propertyType="COMMERCIAL";
			
			ArrayList conn_fees=(ArrayList)feeObj.get(WSCalculationConstant.WS_CONNECTION_FEE_CONST);
			
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
			
			//connectionFee = new BigDecimal(feeObj.getAsNumber(WSCalculationConstant.WS_CONNECTION_FEE_CONST).toString());
			connectionFee=connectionFeeApplicable;
		}

		BigDecimal otherCharges = BigDecimal.ZERO;
		
		  if (feeObj.get(WSCalculationConstant.OTHER_FEE_CONST) != null) {
		  otherCharges = new
		  BigDecimal(feeObj.getAsNumber(WSCalculationConstant.OTHER_FEE_CONST).
		  toString()); }
		 
		BigDecimal taxAndCessPercentage = BigDecimal.ZERO;
		if (feeObj.get(WSCalculationConstant.TAX_PERCENTAGE_CONST) != null) {
			taxAndCessPercentage = new BigDecimal(
					feeObj.getAsNumber(WSCalculationConstant.TAX_PERCENTAGE_CONST).toString());
		}
		BigDecimal meterTestingFee = BigDecimal.ZERO;
		if (feeObj.get(WSCalculationConstant.METER_TESTING_FEE_CONST) != null
				&& criteria.getWaterConnection().getConnectionType() != null && criteria.getWaterConnection()
						.getConnectionType().equalsIgnoreCase(WSCalculationConstant.meteredConnectionType)) {
			meterTestingFee = new BigDecimal(
					feeObj.getAsNumber(WSCalculationConstant.METER_TESTING_FEE_CONST).toString());
		}
		BigDecimal roadCuttingCharge = BigDecimal.ZERO;
		BigDecimal usageTypeCharge = BigDecimal.ZERO;

		if(criteria.getWaterConnection().getRoadCuttingInfo() != null){
			for(RoadCuttingInfo roadCuttingInfo : criteria.getWaterConnection().getRoadCuttingInfo()){
				BigDecimal singleRoadCuttingCharge = BigDecimal.ZERO;
				if (roadCuttingInfo.getRoadType() != null)
					singleRoadCuttingCharge = getChargeForRoadCutting(masterData, roadCuttingInfo.getRoadType(),
							roadCuttingInfo.getRoadCuttingArea());
				roadCuttingCharge = roadCuttingCharge.add(singleRoadCuttingCharge);

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
//		if (property.getLandArea() != null)
//			roadPlotCharge = getPlotSizeFee(masterData, property.getLandArea());

//		BigDecimal totalCharge = formFee.add(scrutinyFee).add(otherCharges).add(meterTestingFee).add(roadCuttingCharge)
//				.add(roadPlotCharge).add(usageTypeCharge);
BigDecimal totalCharge = formFee.add(securityCharge).add(meterTestingFee).add(roadCuttingCharge);
		BigDecimal tax = totalCharge.multiply(taxAndCessPercentage.divide(WSCalculationConstant.HUNDRED));
		List<TaxHeadEstimate> estimates = new ArrayList<>();
		//
		HashMap<String, Object> additionalDetails = mapper.convertValue(criteria.getWaterConnection().getAdditionalDetails(),
				HashMap.class);
		if (additionalDetails.get(WSCalculationConstant.connectionCategory).toString().equalsIgnoreCase("REGULARIZED")) {

			  if (!(otherCharges.compareTo(BigDecimal.ZERO) == 0))
				  estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_OTHER_CHARGE) .estimateAmount(otherCharges.setScale(2, 2)).build());



		}
		else {
		if (!(formFee.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_FORM_FEE)
					.estimateAmount(formFee.setScale(2, 2)).build());
					if (!(securityCharge.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_SECURITY_DEPOSIT)
					.estimateAmount(securityCharge.setScale(2, 2)).build());
		if (!(connectionFee.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_CONNECTION_FEE)
					.estimateAmount(connectionFee.setScale(2, 2)).build());
		if (!(meterTestingFee.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_METER_TESTING_FEE)
					.estimateAmount(meterTestingFee.setScale(2, 2)).build());
//		if (!(scrutinyFee.compareTo(BigDecimal.ZERO) == 0))
//			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_SCRUTINY_FEE)
//					.estimateAmount(scrutinyFee.setScale(2, 2)).build());
		if (!(meterTestingFee.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_METER_CHARGE)
					.estimateAmount(meterTestingFee.setScale(2, 2)).build());
		if (!(otherCharges.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_OTHER_CHARGE)
					.estimateAmount(otherCharges.setScale(2, 2)).build());
		if (!(roadCuttingCharge.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_ROAD_CUTTING_CHARGE)
					.estimateAmount(roadCuttingCharge.setScale(2, 2)).build());
//		if (!(usageTypeCharge.compareTo(BigDecimal.ZERO) == 0))
//			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_ONE_TIME_FEE)
//					.estimateAmount(usageTypeCharge.setScale(2, 2)).build());
//		if (!(roadPlotCharge.compareTo(BigDecimal.ZERO) == 0))
//			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_SECURITY_CHARGE)
//					.estimateAmount(roadPlotCharge.setScale(2, 2)).build());
		if (!(tax.compareTo(BigDecimal.ZERO) == 0))
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_TAX_AND_CESS)
					.estimateAmount(tax.setScale(2, 2)).build());
          }
		addAdhocPenaltyAndRebate(estimates, criteria.getWaterConnection());
		return estimates;
	}
	
	
	/**
	 * 
	 * @param masterData Master Data Map
	 * @param roadType - Road type
	 * @param roadCuttingArea - Road Cutting Area
	 * @return road cutting charge
	 */
	private BigDecimal getChargeForRoadCutting(Map<String, Object> masterData, String roadType, Float roadCuttingArea) {
		JSONArray roadSlab = (JSONArray) masterData.getOrDefault(WSCalculationConstant.WC_ROADTYPE_MASTER, null);
		BigDecimal charge = BigDecimal.ZERO;
		JSONObject masterSlab = new JSONObject();
		if(roadSlab != null) {
			masterSlab.put("RoadType", roadSlab);
			JSONArray filteredMasters = JsonPath.read(masterSlab, "$.RoadType[?(@.code=='" + roadType + "')]");
			if (CollectionUtils.isEmpty(filteredMasters))
				return BigDecimal.ZERO;
			JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
			charge = new BigDecimal(master.getAsNumber(WSCalculationConstant.UNIT_COST_CONST).toString());
			charge = charge.multiply(
					new BigDecimal(roadCuttingArea == null ? BigDecimal.ZERO.toString() : roadCuttingArea.toString()));
		}
		return charge;
	}
	
	/**
	 * 
	 * @param masterData - Master Data Map
	 * @param plotSize - Plot Size
	 * @return get fee based on plot size
	 */
	private BigDecimal getPlotSizeFee(Map<String, Object> masterData, Double plotSize) {
		BigDecimal charge = BigDecimal.ZERO;
		JSONArray plotSlab = (JSONArray) masterData.getOrDefault(WSCalculationConstant.WC_PLOTSLAB_MASTER, null);
		JSONObject masterSlab = new JSONObject();
		if (plotSlab != null) {
			masterSlab.put("PlotSizeSlab", plotSlab);
			JSONArray filteredMasters = JsonPath.read(masterSlab, "$.PlotSizeSlab[?(@.from <="+ plotSize +"&& @.to > " + plotSize +")]");
			if(CollectionUtils.isEmpty(filteredMasters))
				return charge;
			JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
			charge = new BigDecimal(master.getAsNumber(WSCalculationConstant.UNIT_COST_CONST).toString());
		}
		return charge;
	}
	
	/**
	 * 
	 * @param masterData Master Data Map
	 * @param usageType - Property Usage Type
	 * @param roadCuttingArea Road Cutting Area
	 * @return  returns UsageType Fee
	 */
	private BigDecimal getUsageTypeFee(Map<String, Object> masterData, String usageType, Float roadCuttingArea) {
		BigDecimal charge = BigDecimal.ZERO;
		JSONArray usageSlab = (JSONArray) masterData.getOrDefault(WSCalculationConstant.WC_PROPERTYUSAGETYPE_MASTER, null);
		JSONObject masterSlab = new JSONObject();
		BigDecimal cuttingArea = new BigDecimal(roadCuttingArea.toString());
		if(usageSlab != null) {
			masterSlab.put("PropertyUsageType", usageSlab);
			JSONArray filteredMasters = JsonPath.read(masterSlab, "$.PropertyUsageType[?(@.code=='"+usageType+"')]");
			if(CollectionUtils.isEmpty(filteredMasters))
				return charge;
			JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
			charge = new BigDecimal(master.getAsNumber(WSCalculationConstant.UNIT_COST_CONST).toString());
			charge = charge.multiply(cuttingArea);
		}
		return charge;
	}
	
	/**
	 * Enrich the adhoc penalty and adhoc rebate
	 * @param estimates tax head estimate
	 * @param connection water connection object
	 */
	@SuppressWarnings({ "unchecked"})
	private void addAdhocPenaltyAndRebate(List<TaxHeadEstimate> estimates, WaterConnection connection) {
		if (connection.getAdditionalDetails() != null) {
			HashMap<String, Object> additionalDetails = mapper.convertValue(connection.getAdditionalDetails(),
					HashMap.class);
			if (additionalDetails.get(WSCalculationConstant.connectionCategory).toString().equalsIgnoreCase("REGULARIZED")) {

				if (additionalDetails.getOrDefault(WSCalculationConstant.OTHER_FEE_CONST, null) != null) {
					estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.OTHER_FEE)
							.estimateAmount(
									new BigDecimal(additionalDetails.get(WSCalculationConstant.OTHER_FEE_CONST).toString()))
							.build());
				}
			}

			else
			{if (additionalDetails.getOrDefault(WSCalculationConstant.ADHOC_PENALTY, null) != null) {
				estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_ADHOC_PENALTY)
						.estimateAmount(
								new BigDecimal(additionalDetails.get(WSCalculationConstant.ADHOC_PENALTY).toString()))
						.build());
			}
			if (additionalDetails.getOrDefault(WSCalculationConstant.ADHOC_REBATE, null) != null) {
				estimates
						.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_ADHOC_REBATE)
								.estimateAmount(new BigDecimal(
										additionalDetails.get(WSCalculationConstant.ADHOC_REBATE).toString()).negate())
								.build());
			}
		}}
	}
	
	public Map<String, List> getReconnectionFeeEstimation(CalculationCriteria criteria, RequestInfo requestInfo, Map<String, Object> masterData ) {
		if (StringUtils.isEmpty(criteria.getWaterConnection()) && !StringUtils.isEmpty(criteria.getApplicationNo())) {
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setApplicationNumber(criteria.getApplicationNo());
			searchCriteria.setTenantId(criteria.getTenantId());
			WaterConnection waterConnection = calculatorUtil.getWaterConnectionOnApplicationNO(requestInfo, searchCriteria, requestInfo.getUserInfo().getTenantId());
			criteria.setWaterConnection(waterConnection);
		}
		if (StringUtils.isEmpty(criteria.getWaterConnection())) {
			throw new CustomException("WATER_CONNECTION_NOT_FOUND",
					"Water Connection are not present for " + criteria.getApplicationNo() + " Application no");
		}
		List<TaxHeadEstimate> taxHeadEstimates = getTaxHeadForReconnectionFeeEstimationV2(criteria,masterData, requestInfo);
		Map<String, List> estimatesAndBillingSlabs = new HashMap<>();
		estimatesAndBillingSlabs.put("estimates", taxHeadEstimates);
		return estimatesAndBillingSlabs;
	}

	private List<TaxHeadEstimate> getTaxHeadForReconnectionFeeEstimationV2(CalculationCriteria criteria,
			Map<String, Object> masterData, RequestInfo requestInfo) {
		JSONArray feeSlab = (JSONArray) masterData.getOrDefault(WSCalculationConstant.WC_FEESLAB_MASTER, null);
		if (feeSlab == null)
			throw new CustomException("FEE_SLAB_NOT_FOUND", "fee slab master data not found!!"); 
		
		JSONObject feeObj = mapper.convertValue(feeSlab.get(0), JSONObject.class);
		BigDecimal reconnectionCharge = BigDecimal.ZERO;
		
		if (feeObj.get(WSCalculationConstant.RECONNECTION_FEE_CONST) != null) {
			reconnectionCharge = new BigDecimal(feeObj.getAsNumber(WSCalculationConstant.RECONNECTION_FEE_CONST).toString());
		}
		
		List<TaxHeadEstimate> estimates = new ArrayList<>();

		estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_RECONNECTION_CHARGE)
				.estimateAmount(reconnectionCharge).build());
		return estimates;

	}

}
