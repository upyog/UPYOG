package org.upyog.adv.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.adv.web.models.AuditDetails;
import org.upyog.adv.web.models.ResponseInfo;
import org.upyog.adv.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
/**
 * Utility class for common operations in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Provides utility methods for creating response information, timestamps, and dates.
 * - Handles common operations such as generating UUIDs and formatting dates.
 * 
 * Methods:
 * - `createReponseInfo`: Creates a standardized ResponseInfo object for API responses.
 * - `getCurrentTimestamp`: Returns the current timestamp in milliseconds.
 * - `getCurrentDate`: Returns the current date in the system's default timezone.
 * 
 * Constants:
 * - `DATE_FORMAT`: Defines the standard date format used across the service.
 * 
 * Dependencies:
 * - RequestInfo: Used to extract API request metadata for creating ResponseInfo.
 * - ResponseInfo: Represents metadata for API responses.
 * 
 * This class is designed to centralize reusable logic and reduce code duplication.
 */
public class BookingUtil {
	
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
	
	/*Commented and used Instant
	 * public static Long getCurrentTimestamp() { return System.currentTimeMillis();
	 * }
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
	
	public static boolean isDateWithinRange(String startDate, String endDate, String bookingDate) {
	    LocalDate start = LocalDate.parse(startDate);
	    LocalDate end = LocalDate.parse(endDate);
	    LocalDate booking = LocalDate.parse(bookingDate);

	    return (booking.isEqual(start) || booking.isAfter(start)) &&
	           (booking.isEqual(end) || booking.isBefore(end));
	}
	
	
	public static boolean isDateRangeOverlap(String searchStart, String searchEnd, String bookedStart, String bookedEnd) {
	    LocalDate searchStartDate = LocalDate.parse(searchStart);
	    LocalDate searchEndDate = LocalDate.parse(searchEnd);
	    LocalDate bookedStartDate = LocalDate.parse(bookedStart);
	    LocalDate bookedEndDate = LocalDate.parse(bookedEnd);

	    return !(searchStartDate.isAfter(bookedEndDate) || searchEndDate.isBefore(bookedStartDate));
	}

	public static String parseLocalDateToString(LocalDate date, String dateFormat) {
		if(dateFormat == null) {
			dateFormat = DATE_FORMAT;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		// Format the LocalDate
		String formattedDate = date.format(formatter);
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

}
