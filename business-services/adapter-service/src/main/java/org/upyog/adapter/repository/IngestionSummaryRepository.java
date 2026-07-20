package org.upyog.adapter.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Repository managing persistence operations for {@code ingestion_module_summary}.
 *
 * <p>Tracks and updates the last successfully ingested date per tenant and module.
 */
@Slf4j
@Repository
public class IngestionSummaryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SELECT_LAST_SUCCESSFUL_DATE =
            "SELECT last_successful_date FROM ingestion_module_summary WHERE tenant_id = ? AND module_name = ?";

    private static final String UPSERT_LAST_SUCCESSFUL_DATE =
            "INSERT INTO ingestion_module_summary (" +
            "   id, tenant_id, module_name, last_successful_date, created_by, created_time, last_modified_by, last_modified_time" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (tenant_id, module_name) " +
            "DO UPDATE SET last_successful_date = EXCLUDED.last_successful_date, " +
            "              last_modified_by = EXCLUDED.last_modified_by, " +
            "              last_modified_time = EXCLUDED.last_modified_time";

    /**
     * Retrieves the last successfully ingested date for the specified tenant and module.
     *
     * @param tenantId DIGIT tenant ID (e.g. "pg.citya" or "pg")
     * @param moduleName module short code (e.g. "PT")
     * @return Optional containing the last successful date, or empty if no entry exists
     */
    public Optional<LocalDate> findLastSuccessfulDate(String tenantId, String moduleName) {
        try {
            List<Date> dates = jdbcTemplate.query(
                    SELECT_LAST_SUCCESSFUL_DATE,
                    (rs, rowNum) -> rs.getDate("last_successful_date"),
                    tenantId, moduleName
            );
            if (!dates.isEmpty() && dates.get(0) != null) {
                return Optional.of(dates.get(0).toLocalDate());
            }
        } catch (Exception e) {
            log.error("IngestionSummaryRepository | Failed to query last successful date for tenant {} module {}", tenantId, moduleName, e);
        }
        return Optional.empty();
    }

    private static final String SELECT_SUCCESSFUL_DATES_IN_RANGE =
            "SELECT DISTINCT push_date FROM (" +
            "  SELECT push_date FROM ingestion_detail WHERE tenant_id = ? AND module_name = ? AND ingestion_status = 'SUCCESS' AND push_date >= ? AND push_date <= ? " +
            "  UNION " +
            "  SELECT push_month AS push_date FROM legacy_data_ingestion_detail WHERE tenant_id = ? AND module_name = ? AND ingestion_status = 'SUCCESS' AND push_month >= ? AND push_month <= ? " +
            ") combined_dates";

    private static final String UPDATE_MODULE_DETAIL_TABLE =
            "UPDATE ingestion_module_detail SET last_ingested_date = ?, is_legacy_data_ingested = TRUE, last_modified_time = ? " +
            "WHERE tenant_id = ? AND module_name = ?";

    /**
     * Queries all dates within the specified date range that have already been
     * successfully ingested (via daily or legacy pipelines) for the given tenant and module.
     *
     * @param tenantId DIGIT tenant ID
     * @param moduleName module short code
     * @param startDate start of range
     * @param endDate end of range
     * @return Set of LocalDate instances that are already marked SUCCESS
     */
    public java.util.Set<LocalDate> findSuccessfullyIngestedDates(String tenantId, String moduleName, LocalDate startDate, LocalDate endDate) {
        java.util.Set<LocalDate> result = new java.util.HashSet<>();
        try {
            Date sDate = Date.valueOf(startDate);
            Date eDate = Date.valueOf(endDate);
            List<Date> dates = jdbcTemplate.query(
                    SELECT_SUCCESSFUL_DATES_IN_RANGE,
                    (rs, rowNum) -> rs.getDate("push_date"),
                    tenantId, moduleName, sDate, eDate,
                    tenantId, moduleName, sDate, eDate
            );
            for (Date d : dates) {
                if (d != null) {
                    result.add(d.toLocalDate());
                }
            }
        } catch (Exception e) {
            log.error("IngestionSummaryRepository | Failed to query successfully ingested dates for tenant {} module {} range [{} to {}]",
                    tenantId, moduleName, startDate, endDate, e);
        }
        return result;
    }

    /**
     * Saves or updates the last successful ingestion date for the given tenant and module
     * across ingestion_module_summary and ingestion_module_detail tables.
     *
     * @param tenantId DIGIT tenant ID
     * @param moduleName module short code
     * @param successfulDate the date for which ingestion succeeded
     */
    public void saveOrUpdateLastSuccessfulDate(String tenantId, String moduleName, LocalDate successfulDate) {
        try {
            long now = System.currentTimeMillis();
            String id = UUID.randomUUID().toString();
            Date sqlDate = Date.valueOf(successfulDate);

            jdbcTemplate.update(
                    UPSERT_LAST_SUCCESSFUL_DATE,
                    id,
                    tenantId,
                    moduleName,
                    sqlDate,
                    "SYSTEM",
                    now,
                    "SYSTEM",
                    now
            );
            log.info("IngestionSummaryRepository | Updated ingestion_module_summary last_successful_date to {} for tenant {} module {}",
                    successfulDate, tenantId, moduleName);

            try {
                jdbcTemplate.update(UPDATE_MODULE_DETAIL_TABLE, sqlDate, now, tenantId, moduleName);
                log.info("IngestionSummaryRepository | Updated ingestion_module_detail last_ingested_date to {} for tenant {} module {}",
                        successfulDate, tenantId, moduleName);
            } catch (Exception ex) {
                log.debug("IngestionSummaryRepository | Could not update ingestion_module_detail (table may be unpopulated): {}", ex.getMessage());
            }

        } catch (Exception e) {
            log.error("IngestionSummaryRepository | Failed to update last successful date to {} for tenant {} module {}",
                    successfulDate, tenantId, moduleName, e);
        }
    }
}
