package org.upyog.chb.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Component;
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.ResponseInfo;
import org.upyog.chb.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

/**
 * This utility class provides common utility methods for the Community Hall Booking module.
 * 
 * Purpose:
 * - To centralize reusable utility methods that are frequently used across the module.
 * - To simplify operations such as date formatting, UUID generation, and response creation.
 * 
 * Features:
 * - Provides methods for creating standardized ResponseInfo objects.
 * - Handles date and time formatting using predefined patterns.
 * - Generates unique identifiers (UUIDs) for various entities.
 * - Logs utility operations for debugging and monitoring purposes.
 * 
 * Constants:
 * - DATE_FORMAT: Defines the standard date format ("yyyy-MM-dd") used across the module.
 * 
 * Methods:
 * 1. createReponseInfo:
 *    - Creates a standardized ResponseInfo object based on the provided RequestInfo.
 *    - Populates fields such as apiId, version, timestamp, and status.
 * 
 * 2. generateUUID:
 *    - Generates a unique identifier (UUID) for use in booking or other entities.
 * 
 * 3. formatDate:
 *    - Formats a given LocalDate or LocalDateTime into a string using the standard date format.
 * 
 * Usage:
 * - This class is used throughout the module to perform common operations, ensuring consistency and reusability.
 */
public class CommunityHallBookingUtil {

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

	public static AuditDetails getAuditDetails(String by, Boolean isCreate) {
		Long time = getCurrentTimestamp();
		if (isCreate)
			// TODO: check if we can set lastupdated details to empty
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
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

	public static String convertDateFormat(String date, String dateFormat) {
		if (dateFormat == null) {
			dateFormat = DATE_FORMAT;
		}
		LocalDate localDate = parseStringToLocalDate(date);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		// Format the LocalDate
		String formattedDate = localDate.format(formatter);
		return formattedDate;
	}

	public static AuditDetails getAuditDetails(ResultSet rs) throws SQLException {
		AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdBy"))
				.createdTime(rs.getLong("createdTime")).lastModifiedBy(rs.getString("lastModifiedBy"))
				.lastModifiedTime(rs.getLong("lastModifiedTime")).build();
		return auditdetails;
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

	public static LocalDate getMonthsAgo(int month) {
		LocalDate currentDate = LocalDate.now();
		// Calculate the date given months ago
		LocalDate monthsAgo = currentDate.minusMonths(month);

		return monthsAgo;
	}

	/**
	 * Converts the given minutes to seconds.
	 * 
	 * @param minutes The number of minutes to convert to seconds.
	 * @return The equivalent seconds for the given minutes.
	 */
	public static int getSeconds(int minutes) {
		if (minutes < 0) {
			throw new IllegalArgumentException("Minutes cannot be negative");
		}
		return minutes * 60;
	}

	public static long calculateDifferenceInSeconds(long time1, long time2) {
		long differenceInMillis = time1 - time2; // Subtract the values
		return differenceInMillis / 1000; // Convert milliseconds to minutes
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
