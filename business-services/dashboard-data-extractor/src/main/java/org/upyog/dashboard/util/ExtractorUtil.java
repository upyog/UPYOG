package org.upyog.dashboard.util;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.constants.DashboardExtractorConstants;

/**
 * Utility class providing common helper methods for module extractors,
 * including date formatting, epoch timestamp computation, parameter source creation,
 * and tenant ID resolution.
 */
public final class ExtractorUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT);

    private ExtractorUtil() {
        // Prevent instantiation
    }

    /**
     * Resolves the effective comma-separated tenant ID string from the provided tenant list,
     * or throws an exception if missing or empty.
     *
     * @param tenantIds the list of tenant identifiers (e.g. ULBs)
     * @param module    the target module
     * @return comma-separated tenant string
     * @throws IllegalArgumentException if no tenant IDs are provided
     */
    public static String resolveEffectiveTenantId(List<String> tenantIds, Module module) {
        if (tenantIds != null && !tenantIds.isEmpty()) {
            return String.join(",", tenantIds);
        }
        throw new IllegalArgumentException("Tenant (ULB) missing for module " + module + " in ug_ingestion_module_detail table. Please run the tenant sync API or configure tenants manually.");
    }

    /**
     * Formats a {@link LocalDate} according to the standardized dashboard extractor date format (dd-MM-yyyy).
     *
     * @param date the local date to format
     * @return formatted date string
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Computes the epoch millisecond start timestamp (00:00:00.000 UTC) for a given date.
     *
     * @param date the target date
     * @return epoch milliseconds at start of day in UTC
     */
    public static long getStartOfDayUtcEpochMillis(LocalDate date) {
        return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    /**
     * Computes the epoch millisecond end timestamp (23:59:59.999 UTC) for a given date.
     *
     * @param date the target date
     * @return epoch milliseconds at end of day in UTC
     */
    public static long getEndOfDayUtcEpochMillis(LocalDate date) {
        return date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;
    }

    /**
     * Builds a standard {@link MapSqlParameterSource} populated with {@code startTime},
     * {@code endTime}, and {@code tenantId}.
     *
     * @param effectiveTenantId comma-separated effective tenant string
     * @param targetDate        the extraction target date
     * @return populated MapSqlParameterSource
     */
    public static MapSqlParameterSource buildStandardQueryParams(String effectiveTenantId, LocalDate targetDate) {
        long startTime = getStartOfDayUtcEpochMillis(targetDate);
        long endTime = getEndOfDayUtcEpochMillis(targetDate);

        return new MapSqlParameterSource()
                .addValue(DashboardExtractorConstants.PARAM_START_TIME, startTime)
                .addValue(DashboardExtractorConstants.PARAM_END_TIME, endTime)
                .addValue(DashboardExtractorConstants.PARAM_TENANT_ID, effectiveTenantId);
    }
}
