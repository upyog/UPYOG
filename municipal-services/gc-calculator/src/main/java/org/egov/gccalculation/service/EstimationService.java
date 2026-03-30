package org.egov.gccalculation.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;

import org.egov.gccalculation.constants.GCCalculationConstant;
import org.egov.tracer.model.CustomException;
import org.egov.gccalculation.web.models.*;
import org.egov.gccalculation.util.CalculatorUtil;
import org.egov.gccalculation.util.GCCalculationUtil;
import org.egov.gccalculation.util.GarbageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
	private GarbageUtil waterCessUtil;

	@Autowired
	private CalculatorUtil calculatorUtil;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private GCCalculationUtil wSCalculationUtil;

	@Autowired
	private PayService payService;

	/**
	 * Generates a List of Tax head estimates with tax head code, tax head category
	 * and the amount to be collected for the key.
	 *
	 * @param criteria    criteria based on which calculation will be done.
	 * @param requestInfo request info from incoming request.
	 * @return Map<String, Double>
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, List> getEstimationMap(CalculationCriteria criteria, CalculationReq request,
			Map<String, Object> masterData) {
		String tenantId = request.getRequestInfo().getUserInfo().getTenantId();
		if (criteria.getWaterConnection() == null && !StringUtils.isEmpty(criteria.getConnectionNo())) {
			List<GarbageConnection> waterConnectionList = calculatorUtil.getWaterConnection(request.getRequestInfo(),
					criteria.getConnectionNo(), tenantId);
			GarbageConnection waterConnection = calculatorUtil.getWaterConnectionObject(waterConnectionList);
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
		billingSlabMaster.put(GCCalculationConstant.WC_BILLING_SLAB_MASTER,
				(JSONArray) masterData.get(GCCalculationConstant.WC_BILLING_SLAB_MASTER));
		billingSlabMaster.put(GCCalculationConstant.CALCULATION_ATTRIBUTE_CONST,
				(JSONArray) masterData.get(GCCalculationConstant.CALCULATION_ATTRIBUTE_CONST));
		timeBasedExemptionMasterMap.put(GCCalculationConstant.WC_WATER_CESS_MASTER,
				(JSONArray) (masterData.getOrDefault(GCCalculationConstant.WC_WATER_CESS_MASTER, null)));
		timeBasedExemptionMasterMap.put(GCCalculationConstant.WC_REBATE_MASTER,
				(JSONArray) (masterData.getOrDefault(GCCalculationConstant.WC_REBATE_MASTER, null)));
		for (Map.Entry<String, JSONArray> entry : billingSlabMaster.entrySet()) {
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

		for (Map.Entry<String, List> ent : estimatesAndBillingSlabs.entrySet()) {
			log.info(" estimatesAndBillingSlabs Key = " + ent.getKey() + ", Value = " + ent.getValue());
		}
		return estimatesAndBillingSlabs;
	}

	/**
	 * 
	 * @param waterCharge                  WaterCharge amount
	 * @param connection                   - Connection Object
	 * @param timeBasedExemptionsMasterMap List of Exemptions for the connection
	 * @param requestInfoWrapper           - RequestInfo Wrapper object
	 * @return - Returns list of TaxHeadEstimates
	 */
	private List<TaxHeadEstimate> getEstimatesForTax(BigDecimal waterCharge, GarbageConnection connection,
			Map<String, JSONArray> timeBasedExemptionsMasterMap, RequestInfoWrapper requestInfoWrapper) {
		List<TaxHeadEstimate> estimates = new ArrayList<>();

		HashMap<String, String> add_details = ((HashMap<String, String>) connection.getAdditionalDetails());
		// water_charge to be added only if dischargeConnection is not Onlydisposal
		// (category 62 of Amritsar) means if the connection is only disposal that only
		// discharge/disposal charges should be applicable
		if (add_details.containsKey("dischargeConnection") && add_details.get("dischargeConnection")!=null) {
			if (add_details.get("dischargeConnection").equalsIgnoreCase("OnlyDischarge") == false)
				estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_CHARGE)
						.estimateAmount(waterCharge.setScale(2, 2)).build());
		} else
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_CHARGE)
					.estimateAmount(waterCharge.setScale(2, 2)).build());

		// Water_cess
//		 if
//		 (timeBasedExemptionsMasterMap.get(GCCalculationConstant.WC_WATER_CESS_MASTER)
//		 != null) {
//		 List<Object> waterCessMasterList = timeBasedExemptionsMasterMap
//		 .get(GCCalculationConstant.WC_WATER_CESS_MASTER);
//		 BigDecimal waterCess;
//		 waterCess = waterCessUtil.getWaterCess(waterCharge,
//		 GCCalculationConstant.Assessment_Year, waterCessMasterList);
//		 estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_WATER_CESS)
//		 .estimateAmount(waterCess.setScale(2, 2)).build());

		// DISPOSAL DISCHARGE CHARGES
		if (add_details.containsKey("dischargeConnection") && add_details.get("dischargeConnection")!=null) {

			if (add_details.get("dischargeConnection").equalsIgnoreCase("true")
					|| add_details.get("dischargeConnection").equalsIgnoreCase("OnlyDischarge")) {
				BigDecimal disposal_charge;
				try {
					if (add_details.containsKey("dischargeFee")) // if dischargeFee attribute is present in
																	// additionalDetails then use give dischargeFee else
																	// fix dischagefee to 200
						disposal_charge = new BigDecimal(add_details.get("dischargeFee"));
					else
						disposal_charge = new BigDecimal(200.0);
				} catch (Exception ex) {
					disposal_charge = new BigDecimal(200.0);
				}
				estimates.add(TaxHeadEstimate.builder().taxHeadCode("WS_DISCHARGE_CHARGES")
						.estimateAmount(disposal_charge.setScale(2, 2)).build());
			}
		}

//		if (timeBasedExemptionsMasterMap.get(GCCalculationConstant.WC_REBATE_MASTER) != null) {
//			BigDecimal rebate;
//			rebate = payService.getApplicableRebate(waterCharge,null,  timeBasedExemptionsMasterMap.get(GCCalculationConstant.WC_REBATE_MASTER));
//			estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_TIME_REBATE)
//					.estimateAmount(rebate.negate().setScale(2, 2)).build());
//		}

		return estimates;
	}

	/**
	 * method to do a first level filtering on the slabs based on the values present
	 * in the Water Details
	 */

	@SuppressWarnings("unchecked")
	public BigDecimal getWaterEstimationCharge(GarbageConnection waterConnection, CalculationCriteria criteria,
											   Map<String, JSONArray> billingSlabMaster, ArrayList<String> billingSlabIds, CalculationReq request) {

	    BigDecimal waterCharge = BigDecimal.ZERO;
	  
	    HashMap<String, Object> additionalDetail = mapper.convertValue(waterConnection.getAdditionalDetails(), HashMap.class);

	    try {
	        log.info("Water Connection Object in estimation service : " + mapper.writeValueAsString(waterConnection));
	    } catch (JsonProcessingException e1) {
	        e1.printStackTrace();
	    }

	    String billingType = (String) additionalDetail.getOrDefault(GCCalculationConstant.BILLINGTYPE, null);

	    // Custom billing for non-metered connection
	    if (waterConnection.getConnectionType() != null 
	            && waterConnection.getConnectionType().equalsIgnoreCase(GCCalculationConstant.nonMeterdConnection)
	            && GCCalculationConstant.CUSTOM.equalsIgnoreCase(billingType)) {

	        Object customAmountObj = additionalDetail.getOrDefault(GCCalculationConstant.CUSTOM_BILL_AMOUNT, 0);
	        Integer billingAmountInt = customAmountObj instanceof String
	                ? Integer.parseInt((String) customAmountObj)
	                : (Integer) customAmountObj;

	        return BigDecimal.valueOf(Long.valueOf(billingAmountInt)).setScale(2, 2);
	    }

	    // Check billing slab master
	    if (billingSlabMaster.get(GCCalculationConstant.WC_BILLING_SLAB_MASTER) == null)
	        throw new CustomException("BILLING_SLAB_NOT_FOUND", "Billing Slab are Empty");

	    List<BillingSlab> mappingBillingSlab;
	    try {
	        mappingBillingSlab = mapper.readValue(
	                billingSlabMaster.get(GCCalculationConstant.WC_BILLING_SLAB_MASTER).toJSONString(),
	                mapper.getTypeFactory().constructCollectionType(List.class, BillingSlab.class));
	    } catch (IOException e) {
	        throw new CustomException("PARSING_ERROR", "Billing Slab can not be parsed!");
	    }

	    Property property = wSCalculationUtil.getProperty(GarbageConnectionRequest.builder()
	            .waterConnection(waterConnection)
	            .requestInfo(request.getRequestInfo())
	            .build());

	    JSONObject calculationAttributeMaster = new JSONObject();
	    calculationAttributeMaster.put(GCCalculationConstant.CALCULATION_ATTRIBUTE_CONST,
	            billingSlabMaster.get(GCCalculationConstant.CALCULATION_ATTRIBUTE_CONST));
	    
	    // Default to "Non Metered" if connectionType is null
	    String connectionType = waterConnection.getConnectionType() != null 
	            ? waterConnection.getConnectionType() 
	            : "Non Metered";
	    String calculationAttribute = getCalculationAttribute(calculationAttributeMaster, connectionType);

	    List<BillingSlab> billingSlabs = getSlabsFiltered(property, waterConnection, mappingBillingSlab, calculationAttribute);

	    if (billingSlabs == null || billingSlabs.isEmpty())
	        throw new CustomException("BILLING_SLAB_NOT_FOUND", "Billing Slab are Empty");

	    Double totalUOM = getUnitOfMeasurement(property, waterConnection, calculationAttribute, criteria);

	    // Track all slab IDs but calculate water charge only once
	    BillingSlab applicableBillSlab = null;
	    Slab applicableSlab = null;

	    for (BillingSlab billSlab : billingSlabs) {
	        billingSlabIds.add(billSlab.getId()); // collect all IDs

	        // For Flat rate (empty slabs), just pick first matching billing slab
	        if (billSlab.getSlabs() == null || billSlab.getSlabs().isEmpty()) {
	            if (applicableBillSlab == null) {
	                applicableBillSlab = billSlab;
	            }
	            continue;
	        }

	        // For range-based calculation, filter slabs by UOM
	        List<Slab> filteredSlabs = billSlab.getSlabs().stream()
	                .filter(slab -> slab.getFrom() <= totalUOM && slab.getTo() >= totalUOM
	                        && slab.getEffectiveFrom() <= System.currentTimeMillis()
	                        && slab.getEffectiveTo() >= System.currentTimeMillis())
	                .collect(Collectors.toList());

	        if (!filteredSlabs.isEmpty() && applicableBillSlab == null) {
	            applicableBillSlab = billSlab;
	            applicableSlab = filteredSlabs.get(0);
	        }
	    }

	    if (applicableBillSlab != null) {
	        if (isRangeCalculation(calculationAttribute) && applicableSlab != null) {
	            if (waterConnection.getConnectionType() != null 
	                    && GCCalculationConstant.meteredConnectionType.equalsIgnoreCase(waterConnection.getConnectionType())) {
	                Double meterReading = totalUOM;
	                String meterStatus = criteria.getMeterStatus().toString();

	                if (GCCalculationConstant.NO_METER.equalsIgnoreCase(meterStatus)
	                        || GCCalculationConstant.BREAKDOWN.equalsIgnoreCase(meterStatus)) {

	                    meterReading = (Double) additionalDetail.getOrDefault(
	                            GCCalculationConstant.AVARAGEMETERREADING, totalUOM);
	                }

	                waterCharge = BigDecimal.valueOf(meterReading * applicableSlab.getCharge());

	                if (GCCalculationConstant.LOCKED.equalsIgnoreCase(meterStatus)
	                        || waterCharge.doubleValue() < applicableBillSlab.getMinimumCharge()) {
	                    waterCharge = BigDecimal.valueOf(applicableBillSlab.getMinimumCharge());
	                }

	            } else if (waterConnection.getConnectionType() != null 
	                    && GCCalculationConstant.nonMeterdConnection.equalsIgnoreCase(waterConnection.getConnectionType())) {
	                request.setTaxPeriodFrom(criteria.getFrom());
	                request.setTaxPeriodTo(criteria.getTo());

	                if (request.getTaxPeriodFrom() > 0 && request.getTaxPeriodTo() > 0
	                        && waterConnection.getConnectionExecutionDate() > request.getTaxPeriodFrom()) {

	                    long milliBetweenConnDate = Math.abs(request.getTaxPeriodTo() - waterConnection.getConnectionExecutionDate());
	                    long milliBetweenQuarter = Math.abs(request.getTaxPeriodTo() - request.getTaxPeriodFrom());

	                    long daysConn = TimeUnit.MILLISECONDS.toDays(milliBetweenConnDate) + 1;
	                    long daysQuarter = TimeUnit.MILLISECONDS.toDays(milliBetweenQuarter) + 1;

	                    waterCharge = BigDecimal.valueOf(daysConn * (applicableSlab.getCharge() / daysQuarter))
	                            .setScale(2, 2);
	                } else {
	                    waterCharge = BigDecimal.valueOf(applicableSlab.getCharge());
	                }

                    if (waterCharge.doubleValue() < applicableBillSlab.getMinimumCharge()) {
                        waterCharge = BigDecimal.valueOf(applicableBillSlab.getMinimumCharge());
                    }
                }
            } else {
                // Flat rate calculation
                request.setTaxPeriodFrom(criteria.getFrom());
                request.setTaxPeriodTo(criteria.getTo());

                // Pro-rate charge if connection execution date is after tax period start
                if (request.getTaxPeriodFrom() > 0 && request.getTaxPeriodTo() > 0
                        && waterConnection.getConnectionExecutionDate() > request.getTaxPeriodFrom()) {

                    long milliBetweenConnDate = Math.abs(request.getTaxPeriodTo() - waterConnection.getConnectionExecutionDate());
                    long milliBetweenQuarter = Math.abs(request.getTaxPeriodTo() - request.getTaxPeriodFrom());

                    long daysConn = TimeUnit.MILLISECONDS.toDays(milliBetweenConnDate) + 1;
                    long daysQuarter = TimeUnit.MILLISECONDS.toDays(milliBetweenQuarter) + 1;

                    waterCharge = BigDecimal.valueOf(daysConn * (applicableBillSlab.getMinimumCharge() / daysQuarter))
                            .setScale(2, RoundingMode.HALF_UP);

                    log.info("Pro-rated billing: Connection started on {} ({} days out of {} days), charge: {} (from {})",
                            new Date(waterConnection.getConnectionExecutionDate()), daysConn, daysQuarter,
                            waterCharge, applicableBillSlab.getMinimumCharge());
                } else {
                    waterCharge = BigDecimal.valueOf(applicableBillSlab.getMinimumCharge());
                }

                // NO frequency-based division needed anymore!
                // Monthly connections now receive monthly tax periods (not quarterly divided by 3)
                // The slab charge should already be configured for the appropriate period
                log.info("Final charge for connection {}: {} (Frequency: {})",
                        waterConnection.getConnectionNo(), waterCharge, waterConnection.getFrequency());
            }
        }

        return waterCharge;
    }

    private List<BillingSlab> getSlabsFiltered(Property property, GarbageConnection waterConnection,
                                               List<BillingSlab> billingSlabs, String calculationAttribute) {

        // Get specific unit from property using unitId
        Unit unit = wSCalculationUtil.getUnitFromProperty(waterConnection, property);

        // Use UNIT's usageCategory (not property's)
        final String fullUsageCategory = unit.getUsageCategory();
        final String connectionType = waterConnection.getConnectionType() != null
                ? waterConnection.getConnectionType()
                : "Non Metered";

        // Get connection frequency (default to Quarterly if not set)
        final String frequency = waterConnection.getFrequency() != null
                ? waterConnection.getFrequency()
                : "Quarterly";

        log.info("Matching billing slab for Unit ID: {}, UsageCategory: {}, Frequency: {}",
                unit.getId(), fullUsageCategory, frequency);

        // Hierarchical matching from specific to general
        // Example: NONRESIDENTIAL.COMMERCIAL.RETAIL.MALLS -> NONRESIDENTIAL.COMMERCIAL.RETAIL -> NONRESIDENTIAL.COMMERCIAL -> NONRESIDENTIAL
        String[] parts = fullUsageCategory.split("\\.");

        // Pre-filter slabs by connectionType, calculationAttribute, and billingCycle for better performance
        List<BillingSlab> eligibleSlabs = billingSlabs.stream()
                .filter(slab -> slab.getConnectionType().equalsIgnoreCase(connectionType)
                        && slab.getCalculationAttribute().equalsIgnoreCase(calculationAttribute)
                        && (slab.getBillingCycle() == null || slab.getBillingCycle().equalsIgnoreCase(frequency)))
                .collect(Collectors.toList());

        if (eligibleSlabs.isEmpty()) {
            throw new CustomException("NO_ELIGIBLE_SLABS",
                    "No billing slabs found for connectionType: " + connectionType
                            + ", calculationAttribute: " + calculationAttribute
                            + ", frequency: " + frequency);
        }

        // Try matching from most specific to most general level
        for (int i = parts.length; i > 0; i--) {
            String buildingTypeToMatch = String.join(".", Arrays.copyOfRange(parts, 0, i));

            List<BillingSlab> matchedSlabs = eligibleSlabs.stream()
                    .filter(slab -> slab.getBuildingType().equalsIgnoreCase(buildingTypeToMatch))
                    .collect(Collectors.toList());

            if (!matchedSlabs.isEmpty()) {
                log.info("Successfully matched {} slab(s) at level {} for unit usageCategory: {} -> {}",
                        matchedSlabs.size(), i, fullUsageCategory, buildingTypeToMatch);
                return matchedSlabs;
            }
        }

        // If no match found at any level, throw exception
        throw new CustomException("BILLING_SLAB_NOT_FOUND",
                "No billing slab found for unit usage category: " + fullUsageCategory
                        + ", connectionType: " + connectionType
                        + ", calculationAttribute: " + calculationAttribute
                        + ", frequency: " + frequency);
    }
	private String getCalculationAttribute(Map<String, Object> calculationAttributeMap, String connectionType) {
		if (calculationAttributeMap == null)
			throw new CustomException("CALCULATION_ATTRIBUTE_MASTER_NOT_FOUND",
					"Calculation attribute master not found!!");
		JSONArray filteredMasters = JsonPath.read(calculationAttributeMap,
				"$.CalculationAttribute[?(@.name=='" + connectionType + "')]");
		if (!CollectionUtils.isEmpty(filteredMasters)) {
			JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
			return master.getAsString(GCCalculationConstant.ATTRIBUTE);
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
		return !type.equalsIgnoreCase(GCCalculationConstant.flatRateCalculationAttribute);
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

	private Double getUnitOfMeasurement(Property property, GarbageConnection waterConnection, String calculationAttribute,
										CalculationCriteria criteria) {
		Double totalUnit = 0.0;
		if (waterConnection.getConnectionType() != null 
				&& waterConnection.getConnectionType().equals(GCCalculationConstant.meteredConnectionType)) {
			totalUnit = (criteria.getCurrentReading() - criteria.getLastReading());
			return totalUnit;
		}
//		else if (waterConnection.getConnectionType().equals(GCCalculationConstant.nonMeterdConnection)
//				&& calculationAttribute.equalsIgnoreCase(GCCalculationConstant.noOfTapsConst)) {
//			if (waterConnection.getNoOfTaps() != null && waterConnection.getNoOfTaps() > 0)
//				return new Double(waterConnection.getNoOfTaps());
//	}
//		else if (waterConnection.getConnectionType().equals(GCCalculationConstant.nonMeterdConnection)
//				&& calculationAttribute.equalsIgnoreCase(GCCalculationConstant.pipeSizeConst)) {
//			if (waterConnection.getPipeSize() == null && waterConnection.getPipeSize() > 0)
//				return waterConnection.getPipeSize();
//		}
	else if (waterConnection.getConnectionType() != null 
				&& waterConnection.getConnectionType().equals(GCCalculationConstant.nonMeterdConnection)
				&& calculationAttribute.equalsIgnoreCase(GCCalculationConstant.plotBasedConst)) {
			if (property.getLandArea() != null && property.getLandArea() > 0)
				return property.getLandArea();
		}
		return 0.0;
	}

	private Double getUnitOfMeasurement(GarbageConnection waterConnection, String calculationAttribute,
										CalculationCriteria criteria) {
		Double totalUnit = 0.0;
		if (waterConnection.getConnectionType() != null 
				&& waterConnection.getConnectionType().equals(GCCalculationConstant.meteredConnectionType)) {
			totalUnit = (criteria.getCurrentReading() - criteria.getLastReading());
			return totalUnit;
		}
//		else if (waterConnection.getConnectionType().equals(GCCalculationConstant.nonMeterdConnection)
//				&& calculationAttribute.equalsIgnoreCase(GCCalculationConstant.noOfTapsConst)) {
//			if (waterConnection.getNoOfTaps() == null)
//				return totalUnit;
//			return new Double(waterConnection.getNoOfTaps());
//		}
//		else if (waterConnection.getConnectionType().equals(GCCalculationConstant.nonMeterdConnection)
//				&& calculationAttribute.equalsIgnoreCase(GCCalculationConstant.pipeSizeConst)) {
//			if (waterConnection.getPipeSize() == null)
//				return totalUnit;
//			return waterConnection.getPipeSize();
//		}
		return 0.0;
	}

	public Map<String, Object> getQuarterStartAndEndDate(Map<String, Object> billingPeriod) {
		Date date = new Date();
		Calendar fromDateCalendar = Calendar.getInstance();
		fromDateCalendar.setTime(date);
		fromDateCalendar.set(Calendar.MONTH, fromDateCalendar.get(Calendar.MONTH) / 3 * 3);
		fromDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
		setTimeToBeginningOfDay(fromDateCalendar);
		Calendar toDateCalendar = Calendar.getInstance();
		toDateCalendar.setTime(date);
		toDateCalendar.set(Calendar.MONTH, toDateCalendar.get(Calendar.MONTH) / 3 * 3 + 2);
		toDateCalendar.set(Calendar.DAY_OF_MONTH, toDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		setTimeToEndofDay(toDateCalendar);
		billingPeriod.put(GCCalculationConstant.STARTING_DATE_APPLICABLES, fromDateCalendar.getTimeInMillis());
		billingPeriod.put(GCCalculationConstant.ENDING_DATE_APPLICABLES, toDateCalendar.getTimeInMillis());
		return billingPeriod;
	}

	public Map<String, Object> getMonthStartAndEndDate(Map<String, Object> billingPeriod) {
		Date date = new Date();
		Calendar monthStartDate = Calendar.getInstance();
		monthStartDate.setTime(date);
		monthStartDate.set(Calendar.DAY_OF_MONTH, monthStartDate.getActualMinimum(Calendar.DAY_OF_MONTH));
		setTimeToBeginningOfDay(monthStartDate);

		Calendar monthEndDate = Calendar.getInstance();
		monthEndDate.setTime(date);
		monthEndDate.set(Calendar.DAY_OF_MONTH, monthEndDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		setTimeToEndofDay(monthEndDate);
		billingPeriod.put(GCCalculationConstant.STARTING_DATE_APPLICABLES, monthStartDate.getTimeInMillis());
		billingPeriod.put(GCCalculationConstant.ENDING_DATE_APPLICABLES, monthEndDate.getTimeInMillis());
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
	 * @param criteria    - Calculation Search Criteria
	 * @param requestInfo - Request Info Object
	 * @param masterData  - Master Data map
	 * @return Fee Estimation Map
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, List> getFeeEstimation(CalculationCriteria criteria, RequestInfo requestInfo,
			Map<String, Object> masterData) {
		if (StringUtils.isEmpty(criteria.getWaterConnection()) && !StringUtils.isEmpty(criteria.getApplicationNo())) {
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setApplicationNumber(criteria.getApplicationNo());
			searchCriteria.setTenantId(criteria.getTenantId());
			GarbageConnection waterConnection = calculatorUtil.getWaterConnectionOnApplicationNO(requestInfo,
					searchCriteria, requestInfo.getUserInfo().getTenantId());
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
	 * @param criteria    Calculation Search Criteria
	 * @param masterData  - Master Data
	 * @param requestInfo - RequestInfo
	 * @return return all tax heads
	 */
	private List<TaxHeadEstimate> getTaxHeadForFeeEstimation(CalculationCriteria criteria,
			Map<String, Object> masterData, RequestInfo requestInfo) {
		JSONArray feeSlab = (JSONArray) masterData.getOrDefault(GCCalculationConstant.WC_FEESLAB_MASTER, null);
		if (feeSlab == null)
			throw new CustomException("FEE_SLAB_NOT_FOUND", "fee slab master data not found!!");

		Property property = wSCalculationUtil.getProperty(GarbageConnectionRequest.builder()
				.waterConnection(criteria.getWaterConnection()).requestInfo(requestInfo).build());

		JSONObject feeObj = mapper.convertValue(feeSlab.get(0), JSONObject.class);
		BigDecimal formFee = BigDecimal.ZERO;

		if (feeObj.get(GCCalculationConstant.FORM_FEE_CONST) != null) {
			formFee = new BigDecimal(feeObj.getAsNumber(GCCalculationConstant.FORM_FEE_CONST).toString());
		}

//		BigDecimal scrutinyFee = BigDecimal.ZERO;
//		if (feeObj.get(GCCalculationConstant.SCRUTINY_FEE_CONST) != null) {
//			scrutinyFee = new BigDecimal(feeObj.getAsNumber(GCCalculationConstant.SCRUTINY_FEE_CONST).toString());
//		}

		// Get property type from property service (same as SW pattern)
		// Extract main building type from property's usageCategory
		final String propertyUsageCategory = property.getUsageCategory() != null ? property.getUsageCategory() : "";
		final String buildingType = propertyUsageCategory.contains(".") 
				? propertyUsageCategory.split("\\.")[propertyUsageCategory.split("\\.").length - 1]
				: propertyUsageCategory;
		
		// Map property type: keep as RESIDENTIAL/COMMERCIAL/INSTITUTIONAL (no conversion to DOMESTIC)
		String connection_propertyType = "RESIDENTIAL"; // Default
		if (buildingType.toUpperCase().contains("RESIDENTIAL")) {
			connection_propertyType = "RESIDENTIAL";
		} else if (buildingType.toUpperCase().contains("COMMERCIAL")) {
			connection_propertyType = "COMMERCIAL";
		} else if (buildingType.toUpperCase().contains("INSTITUTIONAL")) {
			connection_propertyType = "INSTITUTIONAL";
		}
		
		// Get propertySubType from additionalDetails (like SW uses waterSubUsageType)
		HashMap<String, Object> additionalDetail = mapper.convertValue(
				criteria.getWaterConnection().getAdditionalDetails(), HashMap.class);
		String propertySubType = (String) additionalDetail.getOrDefault("propertySubType", null);
		
		log.info("Property usageCategory: " + propertyUsageCategory + ", buildingType: " + buildingType 
				+ ", connection_propertyType: " + connection_propertyType + ", propertySubType: " + propertySubType);

		BigDecimal securityCharge = BigDecimal.ZERO;

		if (feeObj.get(GCCalculationConstant.WS_SECURITY_CHARGE_CONST) != null) {
			// Security charge is 0 in your MDMS, kept for future use if needed
			Object securityChargeObj = feeObj.get(GCCalculationConstant.WS_SECURITY_CHARGE_CONST);
			
			if (securityChargeObj instanceof List) {
				ArrayList sec_fees = (ArrayList) feeObj.get(GCCalculationConstant.WS_SECURITY_CHARGE_CONST);

				for (int i = 0; i < sec_fees.size(); i++) {
					HashMap<String, String> secFeesMap = (HashMap<String, String>) sec_fees.get(i);
					String usageType = secFeesMap.get("usageType");
					
					if (usageType.equals(connection_propertyType)) {
						securityCharge = new BigDecimal(secFeesMap.get(GCCalculationConstant.WS_SECURITY_CHARGE_CONST));
						log.info("Matched security charge: " + securityCharge + " for usageType: " + usageType);
						break;
					}
				}
			} else {
				securityCharge = new BigDecimal(
						feeObj.getAsNumber(GCCalculationConstant.WS_SECURITY_CHARGE_CONST).toString());
			}
		} /*
			 * String tenantid= criteria.getTenantId();
			 * 
			 * if(tenantid.equalsIgnoreCase("pb.patiala")) { if
			 * (feeObj.get(GCCalculationConstant.WS_SECURITY_CHARGE_CONST) != null) {
			 * if(connection_propertyType.contains("DOM") ||
			 * connection_propertyType.contains("USAGE_RESIDENTIAL") ) securityCharge = new
			 * BigDecimal(1000.00); else securityCharge = new BigDecimal(
			 * feeObj.getAsNumber(GCCalculationConstant.WS_SECURITY_CHARGE_CONST).toString()
			 * ); } }else { if (feeObj.get(GCCalculationConstant.WS_SECURITY_CHARGE_CONST)
			 * != null) { securityCharge = new BigDecimal(
			 * feeObj.getAsNumber(GCCalculationConstant.WS_SECURITY_CHARGE_CONST).toString()
			 * ); } }
			 */
		// Connection Fee to be evaluated here from mdms depending on plotsize

		// Connection Fee - Matches by usageType, optionally by plot size if specified in MDMS
		BigDecimal connectionFee = BigDecimal.ZERO;
		if (feeObj.get(GCCalculationConstant.WS_CONNECTION_FEE_CONST) != null) {
			ArrayList conn_fees = (ArrayList) feeObj.get(GCCalculationConstant.WS_CONNECTION_FEE_CONST);
			BigDecimal connection_plotSize = property.getLandArea() != null ? new BigDecimal(property.getLandArea()) : null;

			for (int i = 0; i < conn_fees.size(); i++) {
				HashMap<String, String> connFeeMap = (HashMap<String, String>) conn_fees.get(i);
				String usageType = connFeeMap.get("usageType");
				
				// Check if usageType matches
				if (usageType.equals(connection_propertyType)) {
					// Check if plot size range is specified in MDMS
					if (connFeeMap.containsKey("fromPlotSize") && connFeeMap.containsKey("toPlotSize") 
							&& connection_plotSize != null) {
						BigDecimal fromPlotSize = new BigDecimal(connFeeMap.get("fromPlotSize"));
						BigDecimal toPlotSize = new BigDecimal(connFeeMap.get("toPlotSize"));
						
						// Match by plot size range if specified
						if (connection_plotSize.compareTo(fromPlotSize) >= 0 
								&& connection_plotSize.compareTo(toPlotSize) <= 0) {
							connectionFee = new BigDecimal(connFeeMap.get("connectionFee"));
							log.info("Matched connection fee: " + connectionFee + " for usageType: " + usageType 
									+ " plotSize: " + connection_plotSize);
							break;
						}
					} else {
						// No plot size range specified, match on usageType only (current behavior)
						connectionFee = new BigDecimal(connFeeMap.get("connectionFee"));
						log.info("Matched connection fee: " + connectionFee + " for usageType: " + usageType);
						break;
					}
				}
			}
		}

		BigDecimal otherCharges = BigDecimal.ZERO;

		if (feeObj.get(GCCalculationConstant.OTHER_FEE_CONST) != null) {
			otherCharges = new BigDecimal(feeObj.getAsNumber(GCCalculationConstant.OTHER_FEE_CONST).toString());
		}

		BigDecimal taxAndCessPercentage = BigDecimal.ZERO;
		if (feeObj.get(GCCalculationConstant.TAX_PERCENTAGE_CONST) != null) {
			taxAndCessPercentage = new BigDecimal(
					feeObj.getAsNumber(GCCalculationConstant.TAX_PERCENTAGE_CONST).toString());
		}
//		BigDecimal meterTestingFee = BigDecimal.ZERO;
//		if (feeObj.get(GCCalculationConstant.METER_TESTING_FEE_CONST) != null
//				&& criteria.getWaterConnection().getConnectionType() != null && criteria.getWaterConnection()
//						.getConnectionType().equalsIgnoreCase(GCCalculationConstant.meteredConnectionType)) {
//			meterTestingFee = new BigDecimal(
//					feeObj.getAsNumber(GCCalculationConstant.METER_TESTING_FEE_CONST).toString());
//		}
//		BigDecimal roadCuttingCharge = BigDecimal.ZERO;
//		BigDecimal usageTypeCharge = BigDecimal.ZERO;

//		if (criteria.getWaterConnection().getRoadCuttingInfo() != null) {
//			for (RoadCuttingInfo roadCuttingInfo : criteria.getWaterConnection().getRoadCuttingInfo()) {
//				BigDecimal singleRoadCuttingCharge = BigDecimal.ZERO;
//				if (roadCuttingInfo.getRoadType() != null)
//					singleRoadCuttingCharge = getChargeForRoadCutting(masterData, roadCuttingInfo.getRoadType(),
//							roadCuttingInfo.getRoadCuttingArea());
//				roadCuttingCharge = roadCuttingCharge.add(singleRoadCuttingCharge);
//
////				BigDecimal singleUsageTypeCharge = BigDecimal.ZERO;
////				if (roadCuttingInfo.getRoadCuttingArea() != null)
////					singleUsageTypeCharge = getUsageTypeFee(masterData,
////							property.getUsageCategory(),
////							roadCuttingInfo.getRoadCuttingArea());
//
////				roadCuttingCharge = roadCuttingCharge.add(singleRoadCuttingCharge);
////				usageTypeCharge = usageTypeCharge.add(singleUsageTypeCharge);
//			}
//		}

//		BigDecimal roadPlotCharge = BigDecimal.ZERO;
//		if (property.getLandArea() != null)
//			roadPlotCharge = getPlotSizeFee(masterData, property.getLandArea());

//		BigDecimal totalCharge = formFee.add(scrutinyFee).add(otherCharges).add(meterTestingFee).add(roadCuttingCharge)
//				.add(roadPlotCharge).add(usageTypeCharge);
//		BigDecimal totalCharge = formFee.add(securityCharge).add(meterTestingFee).add(roadCuttingCharge);
//		BigDecimal tax = totalCharge.multiply(taxAndCessPercentage.divide(GCCalculationConstant.HUNDRED));
		List<TaxHeadEstimate> estimates = new ArrayList<>();
		//
		
		/*
		 For legacy and Regularized wave off of rest fee slab -PI-18845
		 --->Abhishek Rana
		 
		 */
		HashMap<String, Object> additionalDetails = mapper
				.convertValue(criteria.getWaterConnection().getAdditionalDetails(), HashMap.class);
		Object categoryObj = additionalDetails.get(GCCalculationConstant.connectionCategory);
		String category = categoryObj != null ? categoryObj.toString().toUpperCase() : null;

		if ("REGULARIZED".equals(category) || "LEGACY".equals(category)) {
//		    if (otherCharges.compareTo(BigDecimal.ZERO) != 0) {
			otherCharges = (otherCharges == null) ? BigDecimal.ZERO : otherCharges;
		        estimates.add(TaxHeadEstimate.builder()
		                .taxHeadCode(GCCalculationConstant.WS_OTHER_CHARGE)
		                .estimateAmount(otherCharges.setScale(2, 2))
		                .build());
		    
		} else {
			if (!(formFee.compareTo(BigDecimal.ZERO) == 0))
				estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_FORM_FEE)
						.estimateAmount(formFee.setScale(2, 2)).build());
			if (!(securityCharge.compareTo(BigDecimal.ZERO) == 0))
				estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_SECURITY_DEPOSIT)
						.estimateAmount(securityCharge.setScale(2, 2)).build());
			if (!(connectionFee.compareTo(BigDecimal.ZERO) == 0))
				estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_CONNECTION_FEE)
						.estimateAmount(connectionFee.setScale(2, 2)).build());
//			if (!(meterTestingFee.compareTo(BigDecimal.ZERO) == 0))
//				estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_METER_TESTING_FEE)
//						.estimateAmount(meterTestingFee.setScale(2, 2)).build());
////		if (!(scrutinyFee.compareTo(BigDecimal.ZERO) == 0))
////			estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_SCRUTINY_FEE)
////					.estimateAmount(scrutinyFee.setScale(2, 2)).build());
//			if (!(meterTestingFee.compareTo(BigDecimal.ZERO) == 0))
//				estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_METER_CHARGE)
//						.estimateAmount(meterTestingFee.setScale(2, 2)).build());
			if (!(otherCharges.compareTo(BigDecimal.ZERO) == 0))
				estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_OTHER_CHARGE)
						.estimateAmount(otherCharges.setScale(2, 2)).build());
//			if (!(roadCuttingCharge.compareTo(BigDecimal.ZERO) == 0))
//				estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_ROAD_CUTTING_CHARGE)
//						.estimateAmount(roadCuttingCharge.setScale(2, 2)).build());
//		if (!(usageTypeCharge.compareTo(BigDecimal.ZERO) == 0))
//			estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_ONE_TIME_FEE)
//					.estimateAmount(usageTypeCharge.setScale(2, 2)).build());
//		if (!(roadPlotCharge.compareTo(BigDecimal.ZERO) == 0))
//			estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_SECURITY_CHARGE)
//					.estimateAmount(roadPlotCharge.setScale(2, 2)).build());
//			if (!(tax.compareTo(BigDecimal.ZERO) == 0))
//				estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_TAX_AND_CESS)
//						.estimateAmount(tax.setScale(2, 2)).build());
		}
		addAdhocPenaltyAndRebate(estimates, criteria.getWaterConnection());
		if (CollectionUtils.isEmpty(estimates)) {
			// Billing demand create API requires at least one demand detail entry.
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_FORM_FEE)
					.estimateAmount(BigDecimal.ZERO.setScale(2, 2)).build());
		}
		return estimates;
	}

	/**
	 * 
	 * @param masterData      Master Data Map
	 * @param roadType        - Road type
	 * @param roadCuttingArea - Road Cutting Area
	 * @return road cutting charge
	 */
	private BigDecimal getChargeForRoadCutting(Map<String, Object> masterData, String roadType, Float roadCuttingArea) {
		JSONArray roadSlab = (JSONArray) masterData.getOrDefault(GCCalculationConstant.WC_ROADTYPE_MASTER, null);
		BigDecimal charge = BigDecimal.ZERO;
		JSONObject masterSlab = new JSONObject();
		if (roadSlab != null) {
			masterSlab.put("RoadType", roadSlab);
			JSONArray filteredMasters = JsonPath.read(masterSlab, "$.RoadType[?(@.code=='" + roadType + "')]");
			if (CollectionUtils.isEmpty(filteredMasters))
				return BigDecimal.ZERO;
			JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
			charge = new BigDecimal(master.getAsNumber(GCCalculationConstant.UNIT_COST_CONST).toString());
			charge = charge.multiply(
					new BigDecimal(roadCuttingArea == null ? BigDecimal.ZERO.toString() : roadCuttingArea.toString()));
		}
		return charge;
	}

	/**
	 * 
	 * @param masterData - Master Data Map
	 * @param plotSize   - Plot Size
	 * @return get fee based on plot size
	 */
	private BigDecimal getPlotSizeFee(Map<String, Object> masterData, Double plotSize) {
		BigDecimal charge = BigDecimal.ZERO;
		JSONArray plotSlab = (JSONArray) masterData.getOrDefault(GCCalculationConstant.WC_PLOTSLAB_MASTER, null);
		JSONObject masterSlab = new JSONObject();
		if (plotSlab != null) {
			masterSlab.put("PlotSizeSlab", plotSlab);
			JSONArray filteredMasters = JsonPath.read(masterSlab,
					"$.PlotSizeSlab[?(@.from <=" + plotSize + "&& @.to > " + plotSize + ")]");
			if (CollectionUtils.isEmpty(filteredMasters))
				return charge;
			JSONObject master = mapper.convertValue(filteredMasters.get(0), JSONObject.class);
			charge = new BigDecimal(master.getAsNumber(GCCalculationConstant.UNIT_COST_CONST).toString());
		}
		return charge;
	}

	/**
	 * 
	 * @param masterData      Master Data Map
	 * @param usageType       - Property Usage Type
	 * @param roadCuttingArea Road Cutting Area
	 * @return returns UsageType Fee
	 */
	private BigDecimal getUsageTypeFee(Map<String, Object> masterData, String usageType, Float roadCuttingArea) {
		BigDecimal charge = BigDecimal.ZERO;
		JSONArray usageSlab = (JSONArray) masterData.getOrDefault(GCCalculationConstant.WC_PROPERTYUSAGETYPE_MASTER,
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
			charge = new BigDecimal(master.getAsNumber(GCCalculationConstant.UNIT_COST_CONST).toString());
			charge = charge.multiply(cuttingArea);
		}
		return charge;
	}

	/**
	 * Enrich the adhoc penalty and adhoc rebate
	 * 
	 * @param estimates  tax head estimate
	 * @param connection water connection object
	 */
	@SuppressWarnings({ "unchecked" })
	private void addAdhocPenaltyAndRebate(List<TaxHeadEstimate> estimates, GarbageConnection connection) {
		if (connection.getAdditionalDetails() != null) {
			HashMap<String, Object> additionalDetails = mapper.convertValue(connection.getAdditionalDetails(),
					HashMap.class);
			if (additionalDetails.get(GCCalculationConstant.connectionCategory).toString()
					.equalsIgnoreCase("REGULARIZED")) {

				if (additionalDetails.getOrDefault(GCCalculationConstant.OTHER_FEE_CONST, null) != null) {
					estimates
							.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.OTHER_FEE)
									.estimateAmount(new BigDecimal(
											additionalDetails.get(GCCalculationConstant.OTHER_FEE_CONST).toString()))
									.build());
				}
			}

			else {
				if (additionalDetails.getOrDefault(GCCalculationConstant.ADHOC_PENALTY, null) != null) {
					estimates
							.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_ADHOC_PENALTY)
									.estimateAmount(new BigDecimal(
											additionalDetails.get(GCCalculationConstant.ADHOC_PENALTY).toString()))
									.build());
				}
				if (additionalDetails.getOrDefault(GCCalculationConstant.ADHOC_REBATE, null) != null) {
					estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_ADHOC_REBATE)
							.estimateAmount(
									new BigDecimal(additionalDetails.get(GCCalculationConstant.ADHOC_REBATE).toString())
											.negate())
							.build());
				}

				if (additionalDetails.getOrDefault(GCCalculationConstant.COMPOSITION_FEE_CONST, null) != null) {
					estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_COMPOSITION_FEE)
							.estimateAmount(new BigDecimal(
									additionalDetails.get(GCCalculationConstant.COMPOSITION_FEE_CONST).toString()))
							.build());
				}
				System.out.println(additionalDetails.get(GCCalculationConstant.connectionCategory).toString());
				if (additionalDetails.getOrDefault(GCCalculationConstant.USER_CHARGES_CONST, null) != null) {
					estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.USER_CHARGES)
							.estimateAmount(new BigDecimal(
									additionalDetails.get(GCCalculationConstant.USER_CHARGES_CONST).toString()))
							.build());
				}

				if (additionalDetails.getOrDefault(GCCalculationConstant.OTHER_FEE_CONST, null) != null) {
					estimates
							.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.OTHER_FEE)
									.estimateAmount(new BigDecimal(
											additionalDetails.get(GCCalculationConstant.OTHER_FEE_CONST).toString()))
									.build());
				}
			}
		}
	}

	public Map<String, List> getReconnectionFeeEstimation(CalculationCriteria criteria, RequestInfo requestInfo,
			Map<String, Object> masterData) {
		if (StringUtils.isEmpty(criteria.getWaterConnection()) && !StringUtils.isEmpty(criteria.getApplicationNo())) {
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setApplicationNumber(criteria.getApplicationNo());
			searchCriteria.setTenantId(criteria.getTenantId());
			GarbageConnection waterConnection = calculatorUtil.getWaterConnectionOnApplicationNO(requestInfo,
					searchCriteria, requestInfo.getUserInfo().getTenantId());
			criteria.setWaterConnection(waterConnection);
		}
		if (StringUtils.isEmpty(criteria.getWaterConnection())) {
			throw new CustomException("WATER_CONNECTION_NOT_FOUND",
					"Water Connection are not present for " + criteria.getApplicationNo() + " Application no");
		}
		List<TaxHeadEstimate> taxHeadEstimates = getTaxHeadForReconnectionFeeEstimationV2(criteria, masterData,
				requestInfo);
		Map<String, List> estimatesAndBillingSlabs = new HashMap<>();
		estimatesAndBillingSlabs.put("estimates", taxHeadEstimates);
		return estimatesAndBillingSlabs;
	}

	private List<TaxHeadEstimate> getTaxHeadForReconnectionFeeEstimationV2(CalculationCriteria criteria,
			Map<String, Object> masterData, RequestInfo requestInfo) {
		JSONArray feeSlab = (JSONArray) masterData.getOrDefault(GCCalculationConstant.WC_FEESLAB_MASTER, null);
		if (feeSlab == null)
			throw new CustomException("FEE_SLAB_NOT_FOUND", "fee slab master data not found!!");

		JSONObject feeObj = mapper.convertValue(feeSlab.get(0), JSONObject.class);
		BigDecimal reconnectionCharge = BigDecimal.ZERO;

		if (feeObj.get(GCCalculationConstant.RECONNECTION_FEE_CONST) != null) {
			reconnectionCharge = new BigDecimal(
					feeObj.getAsNumber(GCCalculationConstant.RECONNECTION_FEE_CONST).toString());
		}

		List<TaxHeadEstimate> estimates = new ArrayList<>();

		estimates.add(TaxHeadEstimate.builder().taxHeadCode(GCCalculationConstant.WS_RECONNECTION_CHARGE)
				.estimateAmount(reconnectionCharge).build());
		return estimates;

	}

}
