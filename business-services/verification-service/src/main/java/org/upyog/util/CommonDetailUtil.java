package org.upyog.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Component;
import org.upyog.web.models.ResponseInfo;
import org.upyog.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

@Component
public class CommonDetailUtil {

	public final static String DATE_FORMAT = "yyyy-MM-dd";

	public static ResponseInfo createReponseInfo(final RequestInfo requestInfo, String resMsg, StatusEnum status) {

		final String apiId = requestInfo != null ? requestInfo.getApiId() : StringUtils.EMPTY;
		final String ver = requestInfo != null ? requestInfo.getVer() : StringUtils.EMPTY;
		Long ts = null;
		if (requestInfo != null)
			ts = requestInfo.getTs();
		final String msgId = requestInfo != null ? requestInfo.getMsgId() : StringUtils.EMPTY;

		ResponseInfo responseInfo = ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).msgId(msgId).resMsgId(resMsg)
				.status(status).build();

		return responseInfo;
	}

	public static Long getCurrentTimestamp() {
		return Instant.now().toEpochMilli();
	}

	public static LocalDate getCurrentDate() {
		return LocalDate.now();
	}

	/*
	 * Commented and used Instant public static Long getCurrentTimestamp() { return
	 * System.currentTimeMillis(); }
	 */

	public static String getRandonUUID() {
		return UUID.randomUUID().toString();
	}

	public static LocalDate parseStringToLocalDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDate localDate = LocalDate.parse(date, formatter);
		return localDate;
	}

	public static Long minusOneDay(LocalDate date) {
		return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static String parseLocalDateToString(LocalDate date, String dateFormat) {
		if (dateFormat == null) {
			dateFormat = DATE_FORMAT;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		// Format the LocalDate
		String formattedDate = date.format(formatter);
		return formattedDate;
	}

	public static String beuatifyJson(Object result) {
		ObjectMapper mapper = new ObjectMapper();
		String data = null;
		try {
			data = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public static String getTenantId(String tenantId) {
		return tenantId.split("\\.")[0];
	}

	public static ChronoLocalDate getMonthsAgo(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String convertToFormattedDate(String epochString, String dateFormat) {
		try {
			long epoch = Long.parseLong(epochString);
			Date date = new Date(epoch);
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			return formatter.format(date);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static String addOneYearToEpoch(String epochString) {
		try {
			// Parse the epoch string to a long value (milliseconds)
			long epochMillis = Long.parseLong(epochString);

			// Convert epoch milliseconds to LocalDate
			LocalDate date = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate();

			// Add one year
			LocalDate updatedDate = date.plusYears(1);

			// Format the updated date to "dd-MM-yyyy"
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			return updatedDate.format(formatter);

		} catch (NumberFormatException ex) {
			System.err.println("Invalid epoch value: " + epochString);
		} catch (DateTimeParseException ex) {
			System.err.println("Error parsing date: " + epochString);
		}

		return null; // Return null if parsing fails
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
	    if (mobileNumber == null || mobileNumber.length() < 7) { // Ensure mobile number has at least 7 digits
	        return "NA";
	    }
	    // Extract first 3 and last 3 digits, mask the middle
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

		// If format includes time, use LocalDateTime; otherwise, use LocalDate
		if (format.contains("H") || format.contains("m") || format.contains("s")) {
			LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
			return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		} else {
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		}
	}
}
