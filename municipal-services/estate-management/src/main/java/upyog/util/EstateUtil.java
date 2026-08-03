package upyog.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.models.AuditDetails;
import org.egov.tracer.model.CustomException;
import upyog.config.ServiceConstants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class EstateUtil {
    public final static String DATE_FORMAT = "dd-MM-yyyy";

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


    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public static LocalDate parseStringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return LocalDate.parse(date, formatter);
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

    public static LocalDate getMonthsAgo(int month) {
        LocalDate currentDate = LocalDate.now();
        return currentDate.minusMonths(month);
    }

    /**
     * Extracts the base tenant ID before the dot from a full tenant ID string.
     * Throws a CustomException if the tenant ID format is invalid.
     *
     * @param fullTenantId full tenant ID in the format "state.city"
     * @return the base tenant ID (before the dot)
     */
    public static String extractBaseTenantId(String fullTenantId) {
        if (fullTenantId == null || fullTenantId.trim().isEmpty()) {
            throw new CustomException(ServiceConstants.EstateConstants.INVALID_TENANT, "Tenant ID cannot be null or empty");
        }

        String[] parts = fullTenantId.split("\\.");
        if (parts.length < 2) {
            throw new CustomException(ServiceConstants.EstateConstants.INVALID_TENANT, "Tenant ID must be in the format 'state.city'");
        }

        return parts[0];
    }

    private static final String PAGINATION_WRAPPER =
            "SELECT * FROM (SELECT *, DENSE_RANK() OVER (ORDER BY createdtime DESC) offset_ FROM ({}) result) result_offset WHERE offset_ > ? AND offset_ <= ? ORDER BY createdtime DESC";

    /**
     * Adds a pagination wrapper to the select query.
     * 
     * How it works:
     * 1. Checks if the requested limit and offset are null; if so, returns the original query.
     * 2. Sets a default limit of 20 (capped at a maximum of 100) and a default offset of 0.
     * 3. Adds the calculated offset and (limit + offset) values to the SQL statement parameters list.
     * 4. Wraps the original query in a nested select statement using DENSE_RANK() ordered by createdtime descending.
     */
    public static String addPaginationWrapper(String query, List<Object> preparedStmtList, Integer limitVal, Integer offsetVal) {
        int limit = 20;
        int offset = 0;

        if (limitVal == null && offsetVal == null) {
            limit = -1;
        }

        if (limitVal != null && limitVal <= 100)
            limit = limitVal;

        if (limitVal != null && limitVal > 100) {
            limit = 100;
        }

        if (offsetVal != null)
            offset = offsetVal;

        if (limit == -1) {
            return query;
        } else {
            preparedStmtList.add(offset);
            preparedStmtList.add(limit + offset);
        }

        return PAGINATION_WRAPPER.replace("{}", query);
    }
}
