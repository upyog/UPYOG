package org.upyog.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Component;
import org.upyog.web.models.ResponseInfo;
import org.upyog.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

@Slf4j
@Component
public class CommonDetailUtil {

	private CommonDetailUtil() {
	}

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	public static ResponseInfo createReponseInfo(final RequestInfo requestInfo, String resMsg, StatusEnum status) {

		final String apiId = requestInfo != null ? requestInfo.getApiId() : StringUtils.EMPTY;
		final String ver = requestInfo != null ? requestInfo.getVer() : StringUtils.EMPTY;
		Long ts = null;
		if (requestInfo != null)
			ts = requestInfo.getTs();
		final String msgId = requestInfo != null ? requestInfo.getMsgId() : StringUtils.EMPTY;

		return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).msgId(msgId).resMsgId(resMsg)
				.status(status).build();
	}

	public static Long getCurrentTimestamp() {
		return Instant.now().toEpochMilli();
	}

	public static LocalDate getCurrentDate() {
		return LocalDate.now(ZoneId.systemDefault());
	}

	public static String getRandonUUID() {
		return UUID.randomUUID().toString();
	}

	public static LocalDate parseStringToLocalDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		return LocalDate.parse(date, formatter);
	}

	public static Long minusOneDay(LocalDate date) {
		return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static String parseLocalDateToString(LocalDate date, String dateFormat) {
		if (dateFormat == null) {
			dateFormat = DATE_FORMAT;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		return date.format(formatter);
	}

	public static String beuatifyJson(Object result) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			log.error("Failed to beautify JSON", e);
		}
		return null;
	}

	public static String getTenantId(String tenantId) {
		return tenantId.split("\\.")[0];
	}

	private static final String ADDRESS_LEADING_OR_TRAILING_COMMA = "(^,\\s*)|(,\\s*$)";
	private static final String ADDRESS_DUPLICATE_COMMA = "(,\\s*,)";

	public static String normalizeCommaSeparatedAddress(String fullAddress) {
		return fullAddress.replaceAll(ADDRESS_LEADING_OR_TRAILING_COMMA, "")
				.replaceAll(ADDRESS_DUPLICATE_COMMA, ",");
	}

	public static ChronoLocalDate getMonthsAgo(int months) {
		return LocalDate.now(ZoneId.systemDefault()).minusMonths(months);
	}

	public static String convertToFormattedDate(String epochString, String dateFormat) {
		try {
			long epoch = Long.parseLong(epochString);
			return Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault())
					.format(DateTimeFormatter.ofPattern(dateFormat));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static String addOneYearToEpoch(String epochString) {
		try {
			long epochMillis = Long.parseLong(epochString);
			LocalDate date = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate updatedDate = date.plusYears(1);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			return updatedDate.format(formatter);
		} catch (NumberFormatException ex) {
			log.error("Invalid epoch value: {}", epochString);
		} catch (DateTimeParseException ex) {
			log.error("Error parsing date: {}", epochString);
		}

		return null;
	}

	/**
	 * Masks the given mobile number, showing the first 3 and last 3 digits while masking the middle digits.
	 * Example: Input - 9999007890, Output - 999****890
	 *
	 * @author Shivank-NIUA
	 * @param mobileNumber The original mobile number.
	 * @return Masked mobile number or "NA" if input is invalid.
	 */
	public static String maskMobileNumber(String mobileNumber) {
		if (mobileNumber == null || mobileNumber.length() < 7) {
			return "NA";
		}
		return mobileNumber.substring(0, 3) + "****" + mobileNumber.substring(mobileNumber.length() - 3);
	}

	/**
	 * Converts date string to long using LocalDateTime
	 *
	 * @param date   Date string to be parsed
	 * @param format Format of the date string
	 * @return Long value of date in milliseconds
	 */
	public static Long dateTolong(String date, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

		if (format.contains("H") || format.contains("m") || format.contains("s")) {
			LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
			return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		}
		LocalDate localDate = LocalDate.parse(date, formatter);
		return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
}
