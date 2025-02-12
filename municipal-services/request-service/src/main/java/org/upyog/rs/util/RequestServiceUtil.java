package org.upyog.rs.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Component;
import org.upyog.rs.web.models.AuditDetails;
import org.upyog.rs.web.models.ResponseInfo;
import org.upyog.rs.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

@Component
public class RequestServiceUtil {
	
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

	// To get the current financial year end date in epoch to set in Tax to in demand
	public static long getFinancialYearEnd() {

		YearMonth currentYearMonth = YearMonth.now();
		int year = currentYearMonth.getYear();
		int month = currentYearMonth.getMonthValue();

		// If current month is Jan-March, end year should be current year
		if (month < Month.APRIL.getValue()) {
			year -= 1;
		}

		LocalDateTime endOfYear = LocalDateTime.of(year + 1, Month.MARCH, 31, 23, 59, 59, 999000000);
		return endOfYear.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

	}
	
	public static String extractTenantId(String tenantId) {
		return tenantId.split("\\.")[0];
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
