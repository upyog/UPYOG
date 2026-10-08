package org.egov.wscalculation.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.wscalculation.config.WSCalculationConfiguration;
import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.repository.WSCalculationDao;
import org.egov.wscalculation.service.MasterDataService;
import org.egov.wscalculation.util.CalculatorUtil;
import org.egov.wscalculation.web.models.CalculationReq;
import org.egov.wscalculation.web.models.MeterReading;
import org.egov.wscalculation.web.models.MeterReadingSearchCriteria;
import org.egov.wscalculation.web.models.WaterConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WSCalculationValidator {

	private static final DateTimeFormatter BILLING_PERIOD_INPUT_FORMATTER =
			DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
	private static final DateTimeFormatter BILLING_PERIOD_OUTPUT_FORMATTER =
			DateTimeFormatter.ofPattern("dd/MM/uuuu");

	@Autowired
	private WSCalculationDao wSCalculationDao;
	
	@Autowired
	private CalculatorUtil calculationUtil;
	
	@Autowired
	private MasterDataService masterDataService;

	@Autowired
	private WSCalculationConfiguration config;

	/**
	 * 
	 * @param meterConnectionRequest
	 *            meterReadingConnectionRequest is request for create or update
	 *            meter reading connection
	 * @param isUpdate
	 *            True for create
	 */
	public void validateMeterReading(RequestInfo requestInfo,MeterReading meterReading, boolean isUpdate) {
		Map<String, String> errorMap = new HashMap<>();

		// Future Billing Period Check
		validateBillingPeriod(meterReading.getBillingPeriod());
  
		List<WaterConnection> waterConnectionList = calculationUtil.getWaterConnection(requestInfo,
				meterReading.getConnectionNo(), meterReading.getTenantId());
		WaterConnection connection = null;
		if(waterConnectionList != null){
			int size = waterConnectionList.size();
			connection = waterConnectionList.get(size-1);
		}

		if (meterReading.getGenerateDemand() && connection == null) {
			errorMap.put("INVALID_METER_READING_CONNECTION_NUMBER", "Invalid water connection number");
		}
		if (connection != null
				&& !WSCalculationConstant.meteredConnectionType.equalsIgnoreCase(connection.getConnectionType())) {
			errorMap.put("INVALID_WATER_CONNECTION_TYPE",
					"Meter reading can not be create for : " + connection.getConnectionType() + " connection");
		}
		Set<String> connectionNos = new HashSet<>();
		connectionNos.add(meterReading.getConnectionNo());
		MeterReadingSearchCriteria criteria = MeterReadingSearchCriteria.builder().
				connectionNos(connectionNos).tenantId(meterReading.getTenantId()).build();
		List<MeterReading> previousMeterReading = wSCalculationDao.searchCurrentMeterReadings(criteria);
		if (!CollectionUtils.isEmpty(previousMeterReading)) {
			Double currentMeterReading = previousMeterReading.get(0).getCurrentReading();
			if (meterReading.getCurrentReading() < currentMeterReading) {
				errorMap.put("INVALID_METER_READING_CONNECTION_NUMBER",
						"Current meter reading has to be greater than the past last readings in the meter reading!");
			}
		}

		if (meterReading.getCurrentReading() < meterReading.getLastReading()) {
			errorMap.put("INVALID_METER_READING_LAST_READING",
					"Current Meter Reading cannot be less than last meter reading");
		}

		if (StringUtils.isEmpty(meterReading.getMeterStatus())) {
			errorMap.put("INVALID_METER_READING_STATUS", "Meter status can not be null");
		}

		if (isUpdate && (meterReading.getCurrentReading() == null)) {
			errorMap.put("INVALID_CURRENT_METER_READING",
					"Current Meter Reading cannot be update without current meter reading");
		}

		if (isUpdate && !StringUtils.isEmpty(meterReading.getId())) {
			int n = wSCalculationDao.isMeterReadingConnectionExist(Arrays.asList(meterReading.getId()));
			if (n > 0) {
				errorMap.put("INVALID_METER_READING_CONNECTION", "Meter reading Id already present");
			}
		}
		if (StringUtils.isEmpty(meterReading.getBillingPeriod())) {
			errorMap.put("INVALID_BILLING_PERIOD", "Meter Reading cannot be updated without billing period");
		}

		int billingPeriodNumber = wSCalculationDao.isBillingPeriodExists(meterReading.getConnectionNo(),
				meterReading.getBillingPeriod());
		if (billingPeriodNumber > 0)
			errorMap.put("INVALID_METER_READING_BILLING_PERIOD", "Billing Period Already Exists");

		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
	}
	
	
	public Boolean validateMeterReadingBulk(RequestInfo requestInfo,MeterReading meterReading, boolean isUpdate) {
		Map<String, String> errorMap = new HashMap<>();

		// Future Billing Period Check
		validateBillingPeriod(meterReading.getBillingPeriod());
		String errorMessage="";
		List<WaterConnection> waterConnectionList = calculationUtil.getWaterConnection(requestInfo,
				meterReading.getConnectionNo(), meterReading.getTenantId());
		WaterConnection connection = null;
		if(waterConnectionList != null){
			int size = waterConnectionList.size();
			connection = waterConnectionList.get(size-1);
		}

		if (meterReading.getGenerateDemand() && connection == null) {
			errorMessage=errorMessage.concat("Invalid water connection number");
			errorMap.put("INVALID_METER_READING_CONNECTION_NUMBER", "Invalid water connection number");
		}
		if (connection != null
				&& !WSCalculationConstant.meteredConnectionType.equalsIgnoreCase(connection.getConnectionType())) {
			errorMessage=errorMessage.equalsIgnoreCase("")?errorMessage.concat("Meter reading can not be create for :").concat(connection.getConnectionType()).concat(" connection"):errorMessage.concat(", Meter reading can not be create for :").concat(connection.getConnectionType()).concat(" connection");

			errorMap.put("INVALID_WATER_CONNECTION_TYPE",
					"Meter reading can not be create for : " + connection.getConnectionType() + " connection");
		}
		Set<String> connectionNos = new HashSet<>();
		connectionNos.add(meterReading.getConnectionNo());
		MeterReadingSearchCriteria criteria = MeterReadingSearchCriteria.builder().
				connectionNos(connectionNos).tenantId(meterReading.getTenantId()).build();
		List<MeterReading> previousMeterReading = wSCalculationDao.searchCurrentMeterReadings(criteria);
		if (!CollectionUtils.isEmpty(previousMeterReading)) {
			Double currentMeterReading = previousMeterReading.get(0).getCurrentReading();
			if (meterReading.getCurrentReading() < currentMeterReading) {
				
				errorMessage=errorMessage.equalsIgnoreCase("")?errorMessage.concat("Current meter reading has to be greater than the past last readings in the meter reading!"):errorMessage.concat(", Current meter reading has to be greater than the past last readings in the meter reading!");

				errorMap.put("INVALID_METER_READING_CONNECTION_NUMBER",
						"Current meter reading has to be greater than the past last readings in the meter reading!");
			}
		}

		if (meterReading.getCurrentReading() < meterReading.getLastReading()) {
			errorMessage=errorMessage.equalsIgnoreCase("")?errorMessage.concat("Current Meter Reading cannot be less than last meter reading"):
				errorMessage.concat(", Current Meter Reading cannot be less than last meter reading");

			errorMap.put("INVALID_METER_READING_LAST_READING",
					"Current Meter Reading cannot be less than last meter reading");
		}

		if (StringUtils.isEmpty(meterReading.getMeterStatus())) {
			errorMessage=errorMessage.equalsIgnoreCase("")?errorMessage.concat("Meter status can not be null"):
				errorMessage.concat(", Meter status can not be null");
			errorMap.put("INVALID_METER_READING_STATUS", "Meter status can not be null");
		}

		if (isUpdate && (meterReading.getCurrentReading() == null)) {
			errorMessage=errorMessage.equalsIgnoreCase("")?errorMessage.concat("Current Meter Reading cannot be update without current meter reading"):
				errorMessage.concat(", Current Meter Reading cannot be update without current meter reading");
			errorMap.put("INVALID_CURRENT_METER_READING",
					"Current Meter Reading cannot be update without current meter reading");
		}

		if (isUpdate && !StringUtils.isEmpty(meterReading.getId())) {
			int n = wSCalculationDao.isMeterReadingConnectionExist(Arrays.asList(meterReading.getId()));
			if (n > 0) {
				errorMessage=errorMessage.equalsIgnoreCase("")?errorMessage.concat("Meter reading Id already present"):
					errorMessage.concat(", Meter reading Id already present");
				errorMap.put("INVALID_METER_READING_CONNECTION", "Meter reading Id already present");
			}
		}
		if (StringUtils.isEmpty(meterReading.getBillingPeriod())) {
			errorMessage=errorMessage.equalsIgnoreCase("")?errorMessage.concat("Meter Reading cannot be updated without billing period"):
				errorMessage.concat(", Meter Reading cannot be updated without billing period");
			errorMap.put("INVALID_BILLING_PERIOD", "Meter Reading cannot be updated without billing period");
		}

		int billingPeriodNumber = wSCalculationDao.isBillingPeriodExists(meterReading.getConnectionNo(),
				meterReading.getBillingPeriod());
		if (billingPeriodNumber > 0)
		{
			errorMessage=errorMessage.equalsIgnoreCase("")?errorMessage.concat("Billing Period Already Exists"):
				errorMessage.concat(", Billing Period Already Exists");
		
			errorMap.put("INVALID_METER_READING_BILLING_PERIOD", "Billing Period Already Exists");
		}
		if (!errorMap.isEmpty()) {
			meterReading.setStatus(errorMessage);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Billing Period Validation
	 */
	private void validateBillingPeriod(String billingPeriod) {
		if (StringUtils.isEmpty(billingPeriod))
			 throw new CustomException("BILLING_PERIOD_PARSING_ISSUE", "Billing can not empty!!");
		validateMeterReadingBillingPeriod(billingPeriod);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			ZoneId defaultZoneId = ZoneId.systemDefault();
			Date billingDate = sdf.parse(billingPeriod.split("-")[1].trim());
			Instant instant = billingDate.toInstant();
			LocalDate billingLocalDate = instant.atZone(defaultZoneId).toLocalDate();
			LocalDate localDateTime = LocalDate.now();
			if ((billingLocalDate.getYear() == localDateTime.getYear())
					&& (billingLocalDate.getMonthValue() > localDateTime.getMonthValue())) {
				throw new CustomException("BILLING_PERIOD_ISSUE", "Billing period can not be in future!!");
			}
			if ((billingLocalDate.getYear() > localDateTime.getYear())) {
				throw new CustomException("BILLING_PERIOD_ISSUE", "Billing period can not be in future!!");
			}

		} catch (CustomException | ParseException ex) {
			log.error("", ex);
			if (ex instanceof CustomException)
				throw new CustomException("BILLING_PERIOD_ISSUE", "Billing period can not be in future!!");
			throw new CustomException("BILLING_PERIOD_PARSING_ISSUE", "Billing period can not parsed!!");
		}
	}

	public void validateMeterReadingForDisconnection(CalculationReq request, MeterReading latestMeterReading) {
		validateLatestMeterReadingForDisconnection(latestMeterReading);
		validateDisconnectionDateWithLatestMeterReading(request, latestMeterReading);
	}

	public void validateMeterReadingBillingPeriod(String billingPeriod) {
		try {
			String[] dates = billingPeriod.split("-");

			if (dates.length != 2) {
				throw new CustomException("BILLING_PERIOD_PARSING_ISSUE",
						"Billing period format should be dd/MM/yyyy - dd/MM/yyyy");
			}

			LocalDate fromDate = LocalDate.parse(dates[0].trim(), BILLING_PERIOD_INPUT_FORMATTER);
			LocalDate toDate = LocalDate.parse(dates[1].trim(), BILLING_PERIOD_INPUT_FORMATTER);

			if (!toDate.isAfter(fromDate)) {
				throw new CustomException("INVALID_BILLING_PERIOD",
						"Billing period to date should be greater than from date");
			}

		} catch (CustomException ex) {
			throw ex;
		} catch (DateTimeParseException ex) {
			throw new CustomException("BILLING_PERIOD_PARSING_ISSUE",
					"Billing period cannot be parsed");
		}
	}

	public String normalizeBillingPeriod(String billingPeriod) {
		if (StringUtils.isEmpty(billingPeriod)) {
			throw new CustomException("BILLING_PERIOD_PARSING_ISSUE",
					"Billing period cannot be empty");
		}

		String[] dates = billingPeriod.split("\\s*-\\s*", -1);
		if (dates.length != 2) {
			throw new CustomException("BILLING_PERIOD_PARSING_ISSUE",
					"Billing period format should be dd/MM/yyyy - dd/MM/yyyy");
		}

		try {
			LocalDate fromDate = LocalDate.parse(dates[0].trim(), BILLING_PERIOD_INPUT_FORMATTER);
			LocalDate toDate = LocalDate.parse(dates[1].trim(), BILLING_PERIOD_INPUT_FORMATTER);
			return fromDate.format(BILLING_PERIOD_OUTPUT_FORMATTER) + " - "
					+ toDate.format(BILLING_PERIOD_OUTPUT_FORMATTER);
		} catch (DateTimeParseException ex) {
			throw new CustomException("BILLING_PERIOD_PARSING_ISSUE",
					"Billing period cannot be parsed");
		}
	}

	public void validateLatestMeterReadingForDisconnection(MeterReading meterReading) {

		String billingPeriod = meterReading.getBillingPeriod();

		String[] dates = billingPeriod.split("-");

		if (dates.length != 2) {
			throw new CustomException(
					"INVALID_METER_READING_BILLING_PERIOD",
					"Invalid billing period format in latest meter reading"
			);
		}

		try {
			LocalDate fromDate = LocalDate.parse(dates[0].trim(), BILLING_PERIOD_INPUT_FORMATTER);
			LocalDate toDate = LocalDate.parse(dates[1].trim(), BILLING_PERIOD_INPUT_FORMATTER);

			if (!toDate.isAfter(fromDate)) {
				throw new CustomException(
						"INVALID_DISCONNECTION_METER_READING",
						"Please add meter reading for a different billing cycle before disconnecting the connection"
				);
			}

		} catch (DateTimeParseException e) {
			throw new CustomException(
					"INVALID_METER_READING_BILLING_PERIOD",
					"Invalid billing period format in latest meter reading"
			);
		}
	}


		private void validateDisconnectionDateWithLatestMeterReading(CalculationReq request, MeterReading latestMeterReading) {

		Long disconnectionEffectiveDate = request.getCalculationCriteria()
				.get(0)
				.getWaterConnection()
				.getDateEffectiveFrom();

		Long latestReadingDate = latestMeterReading.getLastReadingDate();

		if (disconnectionEffectiveDate == null || disconnectionEffectiveDate <= 0) {
			throw new CustomException("INVALID_DISCONNECTION_DATE",
					"Disconnection effective date is required for disconnection calculation");
		}

		long allowedTillDate = latestReadingDate
				+ (config.getDisconnectionMeterReadingValidityDays() * 24L * 60L * 60L * 1000L);

		if (disconnectionEffectiveDate > allowedTillDate) {
			throw new CustomException("STALE_METER_READING",
					"Latest meter reading is older than "
							+ config.getDisconnectionMeterReadingValidityDays()
							+ " days. Please add latest meter reading before disconnecting the connection.");
		}
	}

}
