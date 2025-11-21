package org.upyog.cdwm.calculator.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.cdwm.calculator.config.CalculatorConfig;
import org.upyog.cdwm.calculator.web.models.AuditDetails;
import org.upyog.cdwm.calculator.web.models.CNDApplicationDetail.StatusEnum;
import org.upyog.cdwm.calculator.web.models.ResponseInfo;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

/**
 * Utility class for performing various calculation-related operations.
 * This includes timestamp generation, date parsing, UUID generation, 
 * and creating audit and response information.
 */
@Component
public class CalculationUtils {

    @Autowired
    private CalculatorConfig config;

    /** Date format used for parsing strings to LocalDate */
    public final static String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Creates a {@link ResponseInfo} object based on the provided {@link RequestInfo}.
     *
     * @param requestInfo The request information object.
     * @param resMsg The response message to be included.
     * @param status The status of the response.
     * @return The created {@link ResponseInfo} object.
     */
    public static ResponseInfo createReponseInfo(final RequestInfo requestInfo, String resMsg, StatusEnum status) {
        final String apiId = requestInfo != null ? requestInfo.getApiId() : StringUtils.EMPTY;
        final String ver = requestInfo != null ? requestInfo.getVer() : StringUtils.EMPTY;
        Long ts = requestInfo != null ? requestInfo.getTs() : null;
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : StringUtils.EMPTY;

        return ResponseInfo.builder()
                .apiId(apiId)
                .ver(ver)
                .ts(ts)
                .msgId(msgId)
                .resMsgId(resMsg)
                .build();
    }

    /**
     * Gets the current timestamp in milliseconds.
     *
     * @return The current timestamp as a long value.
     */
    public static Long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Gets the current date.
     *
     * @return The current date as a {@link LocalDate}.
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * Generates an audit details object.
     *
     * @param by The user who performed the action.
     * @param isCreate Indicates whether this is a creation operation.
     * @return The generated {@link AuditDetails} object.
     */
    public static AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = getCurrentTimestamp();
        if (isCreate) {
            return AuditDetails.builder()
                    .createdBy(by)
                    .lastModifiedBy(by)
                    .createdTime(time)
                    .lastModifiedTime(time)
                    .build();
        } else {
            return AuditDetails.builder()
                    .lastModifiedBy(by)
                    .lastModifiedTime(time)
                    .build();
        }
    }

    /**
     * Generates a random UUID.
     *
     * @return A randomly generated UUID as a string.
     */
    public static String getRandonUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Parses a date string into a {@link LocalDate} object.
     *
     * @param date The date string in the format "yyyy-MM-dd".
     * @return The parsed {@link LocalDate} object.
     */
    public static LocalDate parseStringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return LocalDate.parse(date, formatter);
    }

    /**
     * Subtracts one day from the given date and returns the epoch timestamp.
     *
     * @param date The date to subtract a day from.
     * @return The resulting timestamp in milliseconds.
     */
    public static Long minusOneDay(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Gets the financial year-end date as an epoch timestamp.
     * The financial year follows the pattern of April 1st - March 31st.
     *
     * @return The financial year-end timestamp in milliseconds.
     */
    public static long getFinancialYearEnd() {
        YearMonth currentYearMonth = YearMonth.now();
        int year = currentYearMonth.getYear();
        int month = currentYearMonth.getMonthValue();

        // If current month is Jan-March, set end year to current year
        if (month < Month.APRIL.getValue()) {
            year -= 1;
        }

        LocalDateTime endOfYear = LocalDateTime.of(year + 1, Month.MARCH, 31, 23, 59, 59, 999000000);
        return endOfYear.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
