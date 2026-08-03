package org.upyog.adv.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.adv.web.models.AuditDetails;
import org.upyog.adv.web.models.ResponseInfo;
import org.upyog.adv.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BookingUtil {

	private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	private BookingUtil() {
		throw new IllegalStateException("Utility class");
	}

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
		return LocalDate.now(SYSTEM_ZONE);
	}

	public static AuditDetails getAuditDetails(String by, boolean isCreate) {
		Long time = getCurrentTimestamp();
		if (isCreate)
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}

	public static String getRandonUUID() {
		return java.util.UUID.randomUUID().toString();
	}

	public static LocalDate parseStringToLocalDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		return LocalDate.parse(date, formatter);
	}

	public static Long minusOneDay(LocalDate date) {
		return date.atStartOfDay(SYSTEM_ZONE).toInstant().toEpochMilli();
	}

	public static boolean isDateWithinRange(String startDate, String endDate, String bookingDate) {
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		LocalDate booking = LocalDate.parse(bookingDate);

		return (booking.isEqual(start) || booking.isAfter(start))
				&& (booking.isEqual(end) || booking.isBefore(end));
	}

	public static boolean isDateRangeOverlap(String searchStart, String searchEnd, String bookedStart, String bookedEnd) {
		LocalDate searchStartDate = LocalDate.parse(searchStart);
		LocalDate searchEndDate = LocalDate.parse(searchEnd);
		LocalDate bookedStartDate = LocalDate.parse(bookedStart);
		LocalDate bookedEndDate = LocalDate.parse(bookedEnd);

		return !(searchStartDate.isAfter(bookedEndDate) || searchEndDate.isBefore(bookedStartDate));
	}

	public static String parseLocalDateToString(LocalDate date, String dateFormat) {
		String pattern = dateFormat != null ? dateFormat : DATE_FORMAT;
		return date.format(DateTimeFormatter.ofPattern(pattern));
	}

	public static AuditDetails getAuditDetails(ResultSet rs) throws SQLException {
		return AuditDetails.builder().createdBy(rs.getString("createdBy"))
				.createdTime(rs.getLong("createdTime")).lastModifiedBy(rs.getString("lastModifiedBy"))
				.lastModifiedTime(rs.getLong("lastModifiedTime")).build();
	}

	public static String beuatifyJson(Object result) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			log.error("Failed to beautify JSON payload", e);
		}
		return null;
	}

	public static String getTenantId(String tenantId) {
		return tenantId.split("\\.")[0];
	}

	public static LocalDate getMonthsAgo(int month) {
		return LocalDate.now(SYSTEM_ZONE).minusMonths(month);
	}

}
