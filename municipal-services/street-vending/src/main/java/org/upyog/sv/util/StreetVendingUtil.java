package org.upyog.sv.util;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.upyog.sv.web.models.common.AuditDetails;
import org.upyog.sv.web.models.common.ResponseInfo;
import org.upyog.sv.web.models.common.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

@Slf4j
@Component
public class StreetVendingUtil {

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

	public static LocalDate getCurrentDateFromYear(int years) {
		return LocalDate.now().plusYears(years);
	}

	public static LocalDate getCurrentDateFromMonths(int months) {
		return LocalDate.now().plusMonths(months);
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

	public static ChronoLocalDate getMonthsAgo(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public String convertToFormattedDate(String epochString, String dateFormat) {
		try {
			long epoch = Long.parseLong(epochString);
			Date date = new Date(epoch);
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			return formatter.format(date);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public String addOneYearToEpoch(String epochString) {

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate date = LocalDate.parse(epochString, formatter);
			// Add one year
			LocalDate updatedDate = date.plusYears(1);

			return updatedDate.format(formatter);

		} catch (DateTimeParseException ex) {
			System.err.println("Invalid date format: " + epochString);
		}

		return null; // Return null if both parsing attempts fail
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
	
	/**
	 * Converts an epoch timestamp in milliseconds to a formatted date string.
	 *
	 * @param epochMillis The epoch time in milliseconds (e.g., from System.currentTimeMillis()).
	 * @param format The desired date format (e.g., "dd-MM-yyyy", "yyyy/MM/dd").
	 * @return A formatted date string, or "NA" if the epoch value is null or zero.
	 *
	 * Example:
	 * <pre>
	 *     convertEpochToFormattedDate(1673827200000L, "dd-MM-yyyy") returns "16-01-2023"
	 * </pre>
	 */
	
	public String convertEpochToFormattedDate(Long epochMillis, String format) {
	    if (epochMillis == null || epochMillis == 0) return "NA";

	    return Instant.ofEpochMilli(epochMillis)
	                  .atZone(ZoneId.systemDefault())
	                  .toLocalDate()
	                  .format(DateTimeFormatter.ofPattern(format));
	}

 
	/**
	 * Formats a SQL date from a ResultSet column to a string using the specified pattern.
	 *
	 * <p>This method retrieves a {@link java.time.LocalDate} from the given column name
	 * and formats it using the provided {@link java.time.format.DateTimeFormatter} pattern.
	 * If the date is null or an exception occurs, it returns {@code null}.
	 *
	 * @param rs The {@link java.sql.ResultSet} containing the date column.
	 * @param columnName The name of the column containing the SQL date.
	 * @param pattern The desired date format pattern (e.g., "dd-MM-yyyy", "yyyy/MM/dd").
	 * @return The formatted date string, or {@code null} if the column is null or an error occurs.
	 *
	 * Example:
	 * <pre>
	 *     formatSqlDateToString(rs, "dob", "dd-MM-yyyy") // returns "15-05-2024"
	 * </pre>
	 */
	
	public String formatSqlDateToString(ResultSet rs, String columnName, String pattern) {
	    try {
	        LocalDate date = rs.getObject(columnName, LocalDate.class);
	        if (date != null) {
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	            return date.format(formatter);
	        }
	    } catch (SQLException e) {
	        log.info("Error while formatting date to string " + e);
	    }
	    return null;
	}

}
