package org.upyog.cdwm.util;

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
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.ResponseInfo;
import org.upyog.cdwm.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.AuditDetails;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

public class CNDServiceUtil {

	private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

	private CNDServiceUtil() {
	}

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	/**
     * Creates a ResponseInfo object from RequestInfo.
     */
	
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
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}

	public static String getRandomUUID() {
		return UUID.randomUUID().toString();
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

		return (booking.isEqual(start) || booking.isAfter(start)) && (booking.isEqual(end) || booking.isBefore(end));
	}

	public static boolean isDateRangeOverlap(String searchStart, String searchEnd, String bookedStart,
			String bookedEnd) {
		LocalDate searchStartDate = LocalDate.parse(searchStart);
		LocalDate searchEndDate = LocalDate.parse(searchEnd);
		LocalDate bookedStartDate = LocalDate.parse(bookedStart);
		LocalDate bookedEndDate = LocalDate.parse(bookedEnd);

		return !(searchStartDate.isAfter(bookedEndDate) || searchEndDate.isBefore(bookedStartDate));
	}

	public static String parseLocalDateToString(LocalDate date, String dateFormat) {
		if (dateFormat == null) {
			dateFormat = DATE_FORMAT;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		return date.format(formatter);
	}

	public static AuditDetails getAuditDetails(ResultSet rs) throws SQLException {
		return AuditDetails.builder().createdBy(rs.getString("created_by"))
				.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("last_modified_by"))
				.lastModifiedTime(rs.getLong("last_modified_time")).build();
	}

	public static String beuatifyJson(Object result) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static String getTenantId(String tenantId) {
		return tenantId.split("\\.")[0];
	}

	public static LocalDate getMonthsAgo(int month) {
		return LocalDate.now(SYSTEM_ZONE).minusMonths(month);
	}

	// To get the current financial year end date in epoch to set in Tax to in
	// demand
	public static long getFinancialYearEnd() {

		YearMonth currentYearMonth = YearMonth.now(SYSTEM_ZONE);
		int year = currentYearMonth.getYear();
		int month = currentYearMonth.getMonthValue();

		// If current month is Jan-March, end year should be current year
		if (month < Month.APRIL.getValue()) {
			year -= 1;
		}

		LocalDateTime endOfYear = LocalDateTime.of(year + 1, Month.MARCH, 31, 23, 59, 59, 999000000);
		return endOfYear.atZone(SYSTEM_ZONE).toInstant().toEpochMilli();

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
			return dateTime.atZone(SYSTEM_ZONE).toInstant().toEpochMilli();
		} else {
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate.atStartOfDay(SYSTEM_ZONE).toInstant().toEpochMilli();
		}
	}

	/**
	 * Checks whether the logged-in user is the same as the applicant in the given application request.
	 * <p>
	 * This is done by comparing the mobile number from the user info in the request with
	 * the mobile number provided in the applicant details of the application.
	 *
	 * @param applicationRequest The application request containing user and applicant details.
	 * @return true if the mobile numbers match (case-insensitive), false otherwise.
	 */
	public static boolean isCurrentUserApplicant(CNDApplicationRequest applicationRequest){
		String userMobileNumber = applicationRequest.getRequestInfo().getUserInfo().getMobileNumber();
		String applicationMobileNumber = applicationRequest.getCndApplication().getApplicantDetail().getMobileNumber();
		return userMobileNumber.equals(applicationMobileNumber);
	}

}
