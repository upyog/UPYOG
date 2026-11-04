package org.upyog.dx.upyogdata.pfms.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.upyog.dx.upyogdata.pfms.entity.PfmsTransactionEntity;
import org.upyog.dx.upyogdata.pfms.exception.PfmsDataMappingException;
import org.upyog.dx.upyogdata.pfms.models.SchedulerLog;
import org.upyog.dx.upyogdata.pfms.repository.row_mapper.PFMSTransactionRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles all database operations for PFMS transactions and scheduler logs.
 * All SQL queries are defined in PFMSQuery constants class.
 */


@Repository
@RequiredArgsConstructor
@Slf4j
public class PFMSRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PFMSTransactionRowMapper transactionRowMapper;


    /**
     * Checks if any of the given voucher numbers already exist for the given date.
     * Used to catch duplicates before pushing to Kafka.
     *
     * @param voucherNumbers list of voucher numbers to check
     * @param pfmsAgencyCode    the pfmsagencycode to check against
     * @return list of voucher numbers that already exist, empty if none
     */
    public List<String> findExistingVoucherNumbers(List<String> voucherNumbers, String pfmsAgencyCode) {
        return jdbcTemplate.queryForList(PFMSQuery.FIND_EXISTING_VOUCHER_NUMBERS, String.class,
                voucherNumbers.toArray(new String[0]), pfmsAgencyCode);
    }

    /**
     * Fetches IDs of transactions with INITIATED status, oldest first.
     *
     * @param batchSize max number of records to fetch
     * @return list of transaction UUIDs
     */
    public List<String> fetchInitiatedTransactionIds(int batchSize) {
        return jdbcTemplate.queryForList(PFMSQuery.FETCH_INITIATED_TRANSACTION_IDS, String.class, batchSize);
    }

    /**
     * Fetches full transaction details by its UUID.
     *
     * @param id transaction UUID
     * @return PfmsTransactionEntity mapped from the database row
     */
    public PfmsTransactionEntity fetchTransactionById(String id) {
        try {
            return jdbcTemplate.queryForObject(PFMSQuery.FETCH_TRANSACTION_BY_ID, transactionRowMapper, id);
        } catch (DataAccessException ex) {
            log.error("Failed to fetch PFMS transaction | id={}", id, ex);
            throw new PfmsDataMappingException("Failed to load PFMS transaction id=" + id, ex);
        }
    }

    /**
     * Updates the status and ingestion date of a transaction.
     * Pass null for ingestionDate when setting status to PROCESSING.
     *
     * @param id            transaction UUID
     * @param status        new status value
     * @param ingestionDate timestamp when PFMS responded, or null
     */
    public void updateTransactionStatus(String id, String status, LocalDateTime ingestionDate) {
        jdbcTemplate.update(PFMSQuery.UPDATE_TRANSACTION_STATUS, status, ingestionDate, id);
    }

    /**
     * Fetches IDs of transactions with FAILED status, oldest modified first.
     *
     * @param batchSize max number of records to fetch
     * @return list of transaction UUIDs
     */
    public List<String> fetchFailedTransactionIds(int batchSize) {
        return jdbcTemplate.queryForList(PFMSQuery.FETCH_FAILED_TRANSACTION_IDS, String.class, batchSize);
    }

    /**
     * Inserts a scheduler run log into ug_pfms_scheduler_log.
     * @param log the scheduler log object to save
     */
    public void saveSchedulerLog(SchedulerLog log) {
        jdbcTemplate.update(PFMSQuery.INSERT_SCHEDULER_LOG,
                log.getSchedulerType(), log.getStartedAt(), log.getEndedAt(),
                log.getDurationMs(), log.getStatus(),
                log.getTotalPicked(), log.getSuccessCount(), log.getFailedCount(),
                log.getCreatedBy(), log.getStartedAt());
    }
}
