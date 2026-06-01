package org.egov.gccalculation.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.gccalculation.constants.GCCalculationConstant;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.tracer.model.CustomException;
import org.egov.gccalculation.config.GCCalculationConfiguration;
import org.egov.gccalculation.repository.ServiceRequestRepository;
import org.egov.gccalculation.util.CalculatorUtil;
import org.egov.gccalculation.util.GCCalculationUtil;
import org.egov.gccalculation.web.models.CalculationCriteria;
import org.egov.gccalculation.web.models.Demand;
import org.egov.gccalculation.web.models.RequestInfoWrapper;
import org.egov.gccalculation.web.models.TaxHeadMaster;
import org.egov.gccalculation.web.models.TaxHeadMasterResponse;
import org.egov.gccalculation.web.models.TaxPeriod;
import org.egov.gccalculation.web.models.TaxPeriodResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
@Slf4j
@Service
public class MasterDataService {

	@Autowired
	private ServiceRequestRepository repository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private GCCalculationUtil wSCalculationUtil;

	@Autowired
	private GCCalculationConfiguration config;

	@Autowired
	private CalculatorUtil calculatorUtils;
	
	@Autowired
	private EstimationService estimationService;

	/**
	 * Fetches and creates map of all required masters
	 * 
	 * @param requestInfo The calculation request
	 * @param tenantId Tenant Id
	 * @param serviceFieldValue Service Field value
	 * @return Returns the MDMS Master Map
	 */
	public Map<String, Object> getMasterMap(RequestInfo requestInfo, String tenantId, String serviceFieldValue) {
		Map<String, Object> masterMap = new HashMap<>();
		List<TaxPeriod> taxPeriods = getTaxPeriodList(requestInfo, tenantId, serviceFieldValue);
		List<TaxHeadMaster> taxHeadMasters = getTaxHeadMasterMap(requestInfo, tenantId, serviceFieldValue);
		Map<String, Map<String, Object>> financialYearMaster = getFinancialYear(requestInfo, tenantId);
		masterMap.put(GCCalculationConstant.TAXPERIOD_MASTER_KEY, taxPeriods);
		masterMap.put(GCCalculationConstant.TAXHEADMASTER_MASTER_KEY, taxHeadMasters);
		masterMap.put(GCCalculationConstant.FINANCIALYEAR_MASTER_KEY, financialYearMaster);
		return masterMap;
	}

	/**
	 * Fetch Tax Head Masters From billing service
	 * 
	 * @param requestInfo - Request Info
	 * @param tenantId - Tenant ID
	 * @return - Returns the list of TaxPeriod
	 */
	public List<TaxPeriod> getTaxPeriodList(RequestInfo requestInfo, String tenantId, String serviceFieldValue) {
		TaxPeriodResponse res = mapper.convertValue(
				repository.fetchResult(wSCalculationUtil.getTaxPeriodSearchUrl(tenantId, serviceFieldValue),
						RequestInfoWrapper.builder().requestInfo(requestInfo).build()),
				TaxPeriodResponse.class);
		return res.getTaxPeriods();
	}

	/**
	 * Fetch Tax Head Masters From billing service
	 * 
	 * @param requestInfo - Request Info
	 * @param tenantId - Tenant ID
	 * @return - Returns the list of TaxHeadMaster details
	 */
	public List<TaxHeadMaster> getTaxHeadMasterMap(RequestInfo requestInfo, String tenantId, String serviceFieldValue) {
		TaxHeadMasterResponse res = mapper.convertValue(
				repository.fetchResult(wSCalculationUtil.getTaxHeadSearchUrl(tenantId, serviceFieldValue),
						RequestInfoWrapper.builder().requestInfo(requestInfo).build()),
				TaxHeadMasterResponse.class);
		return res.getTaxHeadMasters();
	}

	/**
	 * 
	 * @param requestInfo - Request Info Object
	 * @param tenantId - Tenant ID
	 * @param billingSlabMaster - Billing Slab Master Data
	 * @param timeBasedExemptionMasterMap - Time Based Exemption Master Data
	 */
	public void setWaterConnectionMasterValues(RequestInfo requestInfo, String tenantId,
			Map<String, JSONArray> billingSlabMaster, Map<String, JSONArray> timeBasedExemptionMasterMap) {

		MdmsResponse response = mapper.convertValue(repository.fetchResult(calculatorUtils.getMdmsSearchUrl(),
				calculatorUtils.getWaterConnectionModuleRequest(requestInfo, tenantId)), MdmsResponse.class);
		Map<String, JSONArray> res = response.getMdmsRes().get(GCCalculationConstant.WS_TAX_MODULE);
		for (Entry<String, JSONArray> entry : res.entrySet()) {

			String masterName = entry.getKey();

			/* Masters which need to be parsed will be contained in the list */
			if (GCCalculationConstant.WS_BILLING_SLAB_MASTERS.contains(entry.getKey()))
				billingSlabMaster.put(masterName, entry.getValue());

			/* Master not contained in list will be stored as it is */
			timeBasedExemptionMasterMap.put(entry.getKey(), entry.getValue());
		}
	}
	
	 /**
	  * 
	  * @param requestInfo - Request Info
	  * @param tenantId - TenantId
	  * @param masterMap - Master MDMS Data
	  */
	public void loadBillingSlabsAndTimeBasedExemptions(RequestInfo requestInfo, String tenantId,
			Map<String, Object> masterMap) {

		MdmsResponse response = mapper.convertValue(repository.fetchResult(calculatorUtils.getMdmsSearchUrl(),
				calculatorUtils.getWaterConnectionModuleRequest(requestInfo, tenantId)), MdmsResponse.class);
		Map<String, JSONArray> res = response.getMdmsRes().get(GCCalculationConstant.WS_TAX_MODULE);
		for (Entry<String, JSONArray> entry : res.entrySet()) {

			String masterName = entry.getKey();

			/* Masters which need to be parsed will be contained in the list */
			if (GCCalculationConstant.WS_BILLING_SLAB_MASTERS.contains(entry.getKey()))
				masterMap.put(masterName, entry.getValue());

			/* Master not contained in list will be stored as it is */
			masterMap.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Fetches Financial Year from MDMS Api
	 *
	 * @param requestInfo RequestInfo
	 * @param tenantId Tenant Id
	 * @return Returns the Financial Year details
	 */
	public Map<String, Map<String, Object>> getFinancialYear(RequestInfo requestInfo, String tenantId) {
		Set<String> assessmentYears = new HashSet<>(1);
		assessmentYears.add(estimationService.getAssessmentYear());
		MdmsCriteriaReq mdmsCriteriaReq = calculatorUtils.getFinancialYearRequest(requestInfo, assessmentYears,
				tenantId);
		StringBuilder url = calculatorUtils.getMdmsSearchUrl();
		Object res = repository.fetchResult(url, mdmsCriteriaReq);
		Map<String, Map<String, Object>> financialYearMap = new HashMap<>();
		for (String assessmentYear : assessmentYears) {
			String jsonPath = GCCalculationConstant.MDMS_FINACIALYEAR_PATH.replace("{}", assessmentYear);
			try {
				List<Map<String, Object>> jsonOutput = JsonPath.read(res, jsonPath);
				Map<String, Object> financialYearProperties = jsonOutput.get(0);
				financialYearMap.put(assessmentYear, financialYearProperties);
			} catch (IndexOutOfBoundsException e) {
				throw new CustomException(GCCalculationConstant.EG_WS_FINANCIAL_MASTER_NOT_FOUND,
						GCCalculationConstant.EG_WS_FINANCIAL_MASTER_NOT_FOUND_MSG + assessmentYear);
			}
		}
		return financialYearMap;
	}
	
	/**
	 *
	 * @param criteria - Calculation Criteria
	 * @param mdmsResponse - MDMS Response
	 * @param masterMap - MDMS Master Data
	 * @param connectiontype - Connection Type
	 * @param frequency - Billing Frequency (Monthly/Quarterly)
	 * @return master map with date period
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> enrichBillingPeriod(CalculationCriteria criteria, ArrayList<?> mdmsResponse,
			Map<String, Object> masterMap, String connectiontype, String frequency) {
		log.info("Billing Frequency Map {}", mdmsResponse.toString());
		
		// Default to "Non Metered" if connectiontype is null
		String effectiveConnectionType = connectiontype != null ? connectiontype : "Non Metered";
		
		// Map frequency to billingCycle: null or "Quarterly" -> "quarterly", "Monthly" -> "monthly"
		String desiredBillingCycle = (frequency != null && frequency.equalsIgnoreCase("Monthly")) 
				? "monthly" 
				: "quarterly"; // Default to quarterly
		
		log.info("Matching billing period for connectionType: {} and billingCycle: {}", 
				effectiveConnectionType, desiredBillingCycle);
		
		Map<String, Object> master = new HashMap<>();
		// First, try to find exact match (connectionType + billingCycle)
		for (Object o : mdmsResponse) {
			Map<String, Object> entry = (Map<String, Object>) o;
			String entryConnectionType = entry.get(GCCalculationConstant.ConnectionType) != null 
					? entry.get(GCCalculationConstant.ConnectionType).toString() 
					: "";
			String entryBillingCycle = entry.get(GCCalculationConstant.Billing_Cycle_String) != null 
					? entry.get(GCCalculationConstant.Billing_Cycle_String).toString() 
					: "";
			
			if (entryConnectionType.equalsIgnoreCase(effectiveConnectionType) 
					&& entryBillingCycle.equalsIgnoreCase(desiredBillingCycle)) {
				master = entry;
				log.info("Found exact match for connectionType: {} and billingCycle: {}", 
						effectiveConnectionType, desiredBillingCycle);
				break;
			}
		}
		
		// If no exact match, fallback to connectionType only match
		if (master.isEmpty()) {
			for (Object o : mdmsResponse) {
				Map<String, Object> entry = (Map<String, Object>) o;
				String entryConnectionType = entry.get(GCCalculationConstant.ConnectionType) != null 
						? entry.get(GCCalculationConstant.ConnectionType).toString() 
						: "";
				
				if (entryConnectionType.equalsIgnoreCase(effectiveConnectionType)) {
					master = entry;
					log.warn("No exact match found, using first entry with connectionType: {}", effectiveConnectionType);
					break;
				}
			}
		}
		
		// If still no match found, default to first entry (should not happen with proper MDMS)
		if (master.isEmpty() && !mdmsResponse.isEmpty()) {
			log.warn("No billing period found for connectionType: " + effectiveConnectionType + ", using first entry");
			master = (Map<String, Object>) mdmsResponse.get(0);
		}
		
		Map<String, Object> billingPeriod = new HashMap<>();
		if (master.get(GCCalculationConstant.ConnectionType) != null 
				&& master.get(GCCalculationConstant.ConnectionType).toString()
				.equalsIgnoreCase(GCCalculationConstant.meteredConnectionType)) {
			// Metered: always use explicit from/to from criteria
			billingPeriod.put(GCCalculationConstant.STARTING_DATE_APPLICABLES, criteria.getFrom());
			billingPeriod.put(GCCalculationConstant.ENDING_DATE_APPLICABLES, criteria.getTo());
		} else if (criteria.getFrom() != null && criteria.getFrom() > 0
				&& criteria.getTo() != null && criteria.getTo() > 0) {
			// Non-metered with explicit dates passed (e.g. previous month billing for new GC connection)
			log.info("Non-metered connection: using explicitly provided billing period [{} - {}]",
					criteria.getFrom(), criteria.getTo());
			billingPeriod.put(GCCalculationConstant.STARTING_DATE_APPLICABLES, criteria.getFrom());
			billingPeriod.put(GCCalculationConstant.ENDING_DATE_APPLICABLES, criteria.getTo());
		} else {
			if (master.get(GCCalculationConstant.Billing_Cycle_String) != null 
					&& GCCalculationConstant.Monthly_Billing_Period
					.equalsIgnoreCase(master.get(GCCalculationConstant.Billing_Cycle_String).toString())) {
				estimationService.getMonthStartAndEndDate(billingPeriod);
			} else if (master.get(GCCalculationConstant.Billing_Cycle_String) != null 
					&& GCCalculationConstant.Quaterly_Billing_Period
					.equalsIgnoreCase(master.get(GCCalculationConstant.Billing_Cycle_String).toString())) {
				estimationService.getQuarterStartAndEndDate(billingPeriod);
			} else {
				LocalDateTime demandEndDate = LocalDateTime.now();
				demandEndDate = setCurrentDateValueToStartingOfDay(demandEndDate);
				Long endDaysMillis = master.get(GCCalculationConstant.Demand_End_Date_String) != null 
						? (Long) master.get(GCCalculationConstant.Demand_End_Date_String)
						: 0L;

				billingPeriod.put(GCCalculationConstant.STARTING_DATE_APPLICABLES,
						Timestamp.valueOf(demandEndDate).getTime() - endDaysMillis);
				billingPeriod.put(GCCalculationConstant.ENDING_DATE_APPLICABLES,
						Timestamp.valueOf(demandEndDate).getTime());
			}
		}
		log.info("Demand Expiry Date : {}", master.get(GCCalculationConstant.Demand_Expiry_Date_String));
		BigInteger expiryDate = new BigInteger(
				String.valueOf(master.get(GCCalculationConstant.Demand_Expiry_Date_String) != null 
						? master.get(GCCalculationConstant.Demand_Expiry_Date_String)
						: "0"));
		Long demandExpiryDateMillis = expiryDate.longValue();
		billingPeriod.put(GCCalculationConstant.Demand_Expiry_Date_String, demandExpiryDateMillis);
		masterMap.put(GCCalculationConstant.BILLING_PERIOD, billingPeriod);
		return masterMap;
	}
	/**
	 * 
	 * @param masterMap - MDMS master data
	 * @return master map contains demand start date, end date and expiry date
	 */
	public Map<String, Object> enrichBillingPeriodForFee(Map<String, Object> masterMap) {
		Map<String, Object> billingPeriod = new HashMap<>();
		billingPeriod.put(GCCalculationConstant.STARTING_DATE_APPLICABLES, System.currentTimeMillis());
		billingPeriod.put(GCCalculationConstant.ENDING_DATE_APPLICABLES,
				System.currentTimeMillis() + GCCalculationConstant.APPLICATION_FEE_DEMAND_END_DATE);
		billingPeriod.put(GCCalculationConstant.Demand_Expiry_Date_String, GCCalculationConstant.APPLICATION_FEE_DEMAND_EXP_DATE);
		masterMap.put(GCCalculationConstant.BILLING_PERIOD, billingPeriod);
		return masterMap;
	}

	/**
	 * 
	 * @param assessmentYear Assessment year
	 * @param masterList master list for that applicable
	 * @return master data for that assessment Year
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getApplicableMaster(Demand demand,String assessmentYear, List<Object> masterList) {

		Map<String, Object> objToBeReturned = null;
		String maxYearFromTheList = "0";
		long maxStartTime = 0L;
		log.info("Master List is " + masterList.toString());
		for (Object object : masterList) {

			Map<String, Object> objMap = (Map<String, Object>) object;
			String objFinYear = ((String) objMap.get(GCCalculationConstant.FROMFY_FIELD_NAME)).split("-")[0];
			if (!objMap.containsKey(GCCalculationConstant.STARTING_DATE_APPLICABLES)) {
				if (objFinYear.compareTo(assessmentYear.split("-")[0]) == 0)
					return objMap;

				else if (assessmentYear.split("-")[0].compareTo(objFinYear) > 0
						&& maxYearFromTheList.compareTo(objFinYear) <= 0) {
					maxYearFromTheList = objFinYear;
					objToBeReturned = objMap;
				}
			} else {
				String objStartDay = ((String) objMap.get(GCCalculationConstant.STARTING_DATE_APPLICABLES));
				if (assessmentYear.split("-")[0].compareTo(objFinYear) >= 0
						&& maxYearFromTheList.compareTo(objFinYear) <= 0) {
					maxYearFromTheList = objFinYear;
					long startTime = getStartDayInMillis(demand,objStartDay);
					long currentTime = System.currentTimeMillis();

					if (startTime < currentTime && maxStartTime < startTime) {
						objToBeReturned = objMap;
						maxStartTime = startTime;
					}
				}
			}
		}
		
		log.info("Master List selected is " + (null==objToBeReturned?"NA":objToBeReturned.toString()));

		return objToBeReturned;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getApplicableMasterCess(String assessmentYear, List<Object> masterList) {

		Map<String, Object> objToBeReturned = null;
		String maxYearFromTheList = "0";
		long maxStartTime = 0L;
		log.info("assessmentYear "+ assessmentYear);
		log.info("Master List is " + masterList.toString());
		for (Object object : masterList) {

			Map<String, Object> objMap = (Map<String, Object>) object;
			String objFinYear = ((String) objMap.get(GCCalculationConstant.FROMFY_FIELD_NAME)).split("-")[0];
			if (!objMap.containsKey(GCCalculationConstant.STARTING_DATE_APPLICABLES)) {
				if (objFinYear.compareTo(assessmentYear.split("-")[0]) == 0)
					return objMap;

				else if (assessmentYear.split("-")[0].compareTo(objFinYear) > 0
						&& maxYearFromTheList.compareTo(objFinYear) <= 0) {
					maxYearFromTheList = objFinYear;
					objToBeReturned = objMap;
				}
			} else {
				String objStartDay = ((String) objMap.get(GCCalculationConstant.STARTING_DATE_APPLICABLES));
				if (assessmentYear.split("-")[0].compareTo(objFinYear) >= 0
						&& maxYearFromTheList.compareTo(objFinYear) <= 0) {
					maxYearFromTheList = objFinYear;
					long startTime = getStartDayInMillis(objStartDay);
					long currentTime = System.currentTimeMillis();
					if (startTime < currentTime && maxStartTime < startTime) {
						objToBeReturned = objMap;
						maxStartTime = startTime;
					}
				}
			}
		}
		
		log.info("Master List selected is " + objToBeReturned.toString());

		return objToBeReturned;
	}

	/**
	 * Converts startDay to epoch
	 * 
	 * @param startDay
	 *            StartDay of applicable
	 * @return Returns the start date in milli seconds
	 */
//	private Long getStartDayInMillis(String startDay) {
//		Date date;
//		try {
//			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//			 date = df.parse(startDay);
//		} catch (ParseException e) {
//			throw new CustomException("INVALID_START_DAY", "The startDate of the penalty cannot be parsed");
//		}
//
//		return date.getTime();
//	}
	
	private Long getStartDayInMillis(Demand demand,String startDay) {
		Date date;
		if(startDay.contains("/")) {
			try {
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				 date = df.parse(startDay);
			} catch (ParseException e) {
				throw new CustomException("INVALID_START_DAY", "The startDate of the penalty cannot be parsed");
			}
		} else {
			Instant instant = Instant.ofEpochMilli(demand.getAuditDetails().getCreatedTime());
			ZoneId zoneId = ZoneId.systemDefault(); // Use the system default time zone
			LocalDate demandDate = instant.atZone(zoneId).toLocalDate();
			//LocalDate demandDate=LocalDate.ofEpochDay(demand.getAuditDetails().getCreatedTime());
			LocalDate penaltyApplicableDate=demandDate.plusDays(Long.parseLong(startDay));
		    log.info("Penalty/Interest is applicable after date " + penaltyApplicableDate.toString() + " for demand with ID " + demand.getId());
			return penaltyApplicableDate.atStartOfDay(zoneId).toInstant().toEpochMilli();
		}
		
		return date.getTime();
	}

	private Long getStartDayInMillis(String startDay) {
		Date date;
		
			try {
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				 date = df.parse(startDay);
			} catch (ParseException e) {
				throw new CustomException("INVALID_START_DAY", "The startDate of the penalty cannot be parsed");
			}
			return date.getTime();
	}

	/**
	 * Method to calculate exemption based on the Amount and exemption map
	 * 
	 * @param applicableAmount Application Amount
	 * @param config Tax Head Object
	 * @return Returns the calculated Amount
	 */
	public BigDecimal calculateApplicable(BigDecimal applicableAmount, Object config) {

		BigDecimal currentApplicable = BigDecimal.ZERO;

		if (null == config)
			return currentApplicable;

		@SuppressWarnings("unchecked")
		Map<String, Object> configMap = (Map<String, Object>) config;

		BigDecimal rate = null != configMap.get(GCCalculationConstant.RATE_FIELD_NAME)
				? BigDecimal.valueOf(((Number) configMap.get(GCCalculationConstant.RATE_FIELD_NAME)).doubleValue())
				: null;

		BigDecimal maxAmt = null != configMap.get(GCCalculationConstant.MAX_AMOUNT_FIELD_NAME) ? BigDecimal
				.valueOf(((Number) configMap.get(GCCalculationConstant.MAX_AMOUNT_FIELD_NAME)).doubleValue()) : null;

		BigDecimal minAmt = null != configMap.get(GCCalculationConstant.MIN_AMOUNT_FIELD_NAME) ? BigDecimal
				.valueOf(((Number) configMap.get(GCCalculationConstant.MIN_AMOUNT_FIELD_NAME)).doubleValue()) : null;

		BigDecimal flatAmt = null != configMap.get(GCCalculationConstant.FLAT_AMOUNT_FIELD_NAME)
				? BigDecimal
						.valueOf(((Number) configMap.get(GCCalculationConstant.FLAT_AMOUNT_FIELD_NAME)).doubleValue())
				: BigDecimal.ZERO;

		if (null == rate)
			currentApplicable = flatAmt.compareTo(applicableAmount) > 0 ? applicableAmount : flatAmt;
		else {
			currentApplicable = applicableAmount.multiply(rate.divide(GCCalculationConstant.HUNDRED));

			if (null != maxAmt && BigDecimal.ZERO.compareTo(maxAmt) < 0 && currentApplicable.compareTo(maxAmt) > 0)
				currentApplicable = maxAmt;
			else if (null != minAmt && currentApplicable.compareTo(minAmt) < 0)
				currentApplicable = minAmt;
		}
		return currentApplicable;
	}

	public LocalDateTime setCurrentDateValueToStartingOfDay(LocalDateTime localDateTime) {
		return localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
	}
	
	public JSONArray getMasterListOfReceiver(RequestInfo requestInfo, String tenantId) {
		ArrayList<String> masterDetails = new ArrayList<>();
		masterDetails.add(GCCalculationConstant.SMS_RECIEVER_MASTER);
		MdmsResponse response = mapper.convertValue(
				repository.fetchResult(calculatorUtils.getMdmsSearchUrl(), calculatorUtils
						.getMdmsReqCriteria(requestInfo, tenantId, masterDetails, GCCalculationConstant.WS_TAX_MODULE)),
				MdmsResponse.class);
		Map<String, JSONArray> res = response.getMdmsRes().get(GCCalculationConstant.WS_TAX_MODULE);
		return res.get(GCCalculationConstant.SMS_RECIEVER_MASTER);
	}
	
	/**
	 * 
	 * @param requestInfo RequestInfo
	 * @param tenantId TenantId
	 * @return all masters that is needed for calculation and demand generation.
	 */
	public Map<String, Object> loadMasterData(RequestInfo requestInfo, String tenantId) {
		Map<String, Object> master = getMasterMap(requestInfo, tenantId, GCCalculationConstant.SERVICE_FIELD_VALUE_WS);
		loadBillingSlabsAndTimeBasedExemptions(requestInfo, tenantId, master);
		loadBillingFrequencyMasterData(requestInfo, tenantId, master);
		return master;
	}
	
	/**
	 * 
	 * @param requestInfo Request Info
	 * @param tenantId Tenant Id
	 * @param masterMap MDMS Data
	 * @return Master For Billing Period
	 */
	public Map<String, Object> loadBillingFrequencyMasterData(RequestInfo requestInfo, String tenantId, Map<String, Object> masterMap) {
		MdmsCriteriaReq mdmsCriteriaReq = calculatorUtils.getBillingFrequency(requestInfo, tenantId);
		Object res = repository.fetchResult(calculatorUtils.getMdmsSearchUrl(), mdmsCriteriaReq);
		if (res == null) {
			throw new CustomException("MDMS_ERROR_FOR_BILLING_FREQUENCY", "Failed to fetch the billing frequency");
		}
		ArrayList<?> mdmsResponse = JsonPath.read(res, GCCalculationConstant.JSONPATH_ROOT_FOR_BilingPeriod);
		masterMap.put(GCCalculationConstant.Billing_Period_Master, mdmsResponse);
		return masterMap;
	}
	
	/**
	 * 
	 * @param requestInfo RequestInfo
	 * @param tenantId TenantId
	 * @return masterMap return master data with exception master data
	 */
	public Map<String, Object> loadExemptionMaster(RequestInfo requestInfo, String tenantId) {
		Map<String, Object> master = getMasterMap(requestInfo, tenantId, GCCalculationConstant.ONE_TIME_FEE_SERVICE_FIELD);
		MdmsResponse response = mapper.convertValue(
				repository.fetchResult(calculatorUtils.getMdmsSearchUrl(),
						calculatorUtils.getEstimationMasterCriteria(requestInfo, tenantId)),
				MdmsResponse.class);
		Map<String, JSONArray> res = response.getMdmsRes().get(GCCalculationConstant.WS_TAX_MODULE);
		for (Map.Entry<String, JSONArray> resp : res.entrySet()) {
			master.put(resp.getKey(), resp.getValue());
		}
		return master;
	}
	
}
