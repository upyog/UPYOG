package org.upyog.adv.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.adv.web.models.AuditDetails;
import org.upyog.adv.web.models.ResponseInfo;
import org.upyog.adv.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

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

	/**
	 * Auto‑detects the applicable per‑day rate from the advertisement.
	 * <p>
	 * Checks which period‑amount field is populated and divides accordingly:
	 * <ul>
	 *   <li>{@code monthlyAmount} → divided by days in the reference month</li>
	 *   <li>{@code weeklyAmount}  → divided by 7</li>
	 *   <li>{@code yearlyAmount}  → divided by days in the reference year</li>
	 *   <li>{@code biannualAmount} → divided by 182</li>
	 *   <li>none of the above → {@code amount} used as‑is (daily rate)</li>
	 * </ul>
	 *
	 * @param adv           the advertisement from MDMS
	 * @param referenceDate used for variable‑length periods (month / year length)
	 * @return per‑day amount, or null if no amount is available
	 */
	public static BigDecimal getPerDayRate(org.upyog.adv.web.models.Advertisements adv, LocalDate referenceDate) {
		if (adv == null) return null;

		BigDecimal periodAmount;
		int divisor;

		if (adv.getMonthlyAmount() != null) {
			periodAmount = adv.getMonthlyAmount();
			divisor = (referenceDate != null) ? getDaysInMonth(referenceDate) : 30;
		} else if (adv.getWeeklyAmount() != null) {
			periodAmount = adv.getWeeklyAmount();
			divisor = 7;
		} else if (adv.getYearlyAmount() != null) {
			periodAmount = adv.getYearlyAmount();
			divisor = (referenceDate != null) ? referenceDate.lengthOfYear() : 365;
		} else if (adv.getBiannualAmount() != null) {
			periodAmount = adv.getBiannualAmount();
			divisor = 182;
		} else {
			// No period amount — treat as daily rate
			return adv.getAmount();
		}

		if (periodAmount == null || divisor <= 0) return adv.getAmount();
		return periodAmount.divide(BigDecimal.valueOf(divisor), 2, RoundingMode.CEILING);
	}

	public static String parseLocalDateToString(LocalDate date, String dateFormat) {
		if(dateFormat == null) {
			dateFormat = DATE_FORMAT;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
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
			e.printStackTrace();
		}
		return data;
	}

	public static String getTenantId(String tenantId) {
		return tenantId.split("\\.")[0];
	}
	
	public static LocalDate getMonthsAgo(int month) {
		LocalDate currentDate = LocalDate.now();
		LocalDate monthsAgo = currentDate.minusMonths(month);
		return monthsAgo;
	}
	
	public static int getDaysInMonth(LocalDate date) {
		return YearMonth.from(date).lengthOfMonth();
	}
	
	public static long getDaysBetween(LocalDate start, LocalDate end) {
		if (start == null || end == null || end.isBefore(start)) {
			return 0;
		}
		return ChronoUnit.DAYS.between(start, end) + 1;
	}
	
	public static List<LocalDate> expandDateRange(LocalDate start, LocalDate end) {
		List<LocalDate> dates = new ArrayList<>();
		if (start == null || end == null || end.isBefore(start)) {
			return dates;
		}
		LocalDate current = start;
		while (!current.isAfter(end)) {
			dates.add(current);
			current = current.plusDays(1);
		}
		return dates;
	}

	/**
	 * Expands booking dates into individual daily slots.
	 * <p>
	 * If {@code endDate} is provided and differs from {@code startDate},
	 * the range is expanded into one entry per day (inclusive).
	 * Otherwise returns a single‑element list for {@code startDate}.
	 * </p>
	 */
	public static List<LocalDate> expandBookingDates(LocalDate startDate, LocalDate endDate) {
		if (startDate == null) return Collections.emptyList();
		if (endDate != null && !endDate.equals(startDate) && !endDate.isBefore(startDate)) {
			return expandDateRange(startDate, endDate);
		}
		return Collections.singletonList(startDate);
	}

}
