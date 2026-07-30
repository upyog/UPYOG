package org.egov.loadgenerator.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.loadgenerator.model.JobStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository responsible for persisting and retrieving
 * {@link JobStatus} information from the database.
 *
 * <p>This repository performs CRUD operations for load generation
 * jobs using Spring's {@link JdbcTemplate}. It maintains execution
 * statistics, job progress, and performance metrics required for
 * monitoring load generation activities.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Persist newly created load generation jobs.</li>
 *   <li>Update job execution statistics.</li>
 *   <li>Retrieve job details by job identifier.</li>
 *   <li>Retrieve recently executed jobs.</li>
 *   <li>Delete job records for a specific module and tenant.</li>
 * </ul>
 *
 * @see JobStatus
 * @see JdbcTemplate
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JobStatusRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL =
            "INSERT INTO eg_load_generator_jobs (job_id, module, tenant_id, total_records, success_count, " +
            "failure_count, status, start_time_ms, end_time_ms, throughput_per_sec, avg_response_time_ms, error_summary) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE eg_load_generator_jobs SET success_count=?, failure_count=?, status=?, " +
            "end_time_ms=?, throughput_per_sec=?, avg_response_time_ms=?, error_summary=? WHERE job_id=?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM eg_load_generator_jobs WHERE job_id=?";

    private static final String SELECT_ALL =
            "SELECT * FROM eg_load_generator_jobs ORDER BY start_time_ms DESC LIMIT 50";

    private static final String DELETE_BY_MODULE_TENANT =
            "DELETE FROM eg_load_generator_jobs WHERE module=? AND tenant_id=?";

    /**
     * Persists a new load generation job in the database.
     *
     * @param job the job status to be saved
     */
    public void save(JobStatus job) {
        jdbcTemplate.update(INSERT_SQL,
                job.getJobId(), job.getModule(), job.getTenantId(),
                job.getTotalRecords(), job.getSuccessCount(), job.getFailureCount(),
                job.getStatus(), job.getStartTimeMs(), job.getEndTimeMs(),
                job.getThroughputPerSec(), job.getAvgResponseTimeMs(), job.getErrorSummary());
    }

    /**
     * Updates the execution status and metrics of an existing job.
     *
     * @param job the updated job status
     */
    public void update(JobStatus job) {
        jdbcTemplate.update(UPDATE_SQL,
                job.getSuccessCount(), job.getFailureCount(), job.getStatus(),
                job.getEndTimeMs(), job.getThroughputPerSec(), job.getAvgResponseTimeMs(),
                job.getErrorSummary(), job.getJobId());
    }

    /**
     * Retrieves a load generation job by its unique identifier.
     *
     * @param jobId the unique job identifier
     * @return an {@link Optional} containing the job if found;
     *         otherwise an empty Optional
     */
    public Optional<JobStatus> findById(String jobId) {
        List<JobStatus> results = jdbcTemplate.query(SELECT_BY_ID, new JobStatusRowMapper(), jobId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Retrieves the most recently executed load generation jobs.
     *
     * @return a list of recent job status records
     */
    public List<JobStatus> findAll() {
        return jdbcTemplate.query(SELECT_ALL, new JobStatusRowMapper());
    }

    /**
     * Deletes all job records for the specified module and tenant.
     *
     * @param module the module name
     * @param tenantId the tenant identifier
     * @return the number of records deleted
     */
    public int deleteByModuleAndTenant(String module, String tenantId) {
        return jdbcTemplate.update(DELETE_BY_MODULE_TENANT, module.toUpperCase(), tenantId);
    }

    /**
     * Maps database rows to {@link JobStatus} objects.
     */
    private static class JobStatusRowMapper implements RowMapper<JobStatus> {

        /**
         * Converts the current database row into a {@link JobStatus} instance.
         *
         * @param rs the result set positioned at the current row
         * @param rowNum the current row number
         * @return the mapped JobStatus object
         * @throws SQLException if a database access error occurs
         */
        @Override
        public JobStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
            return JobStatus.builder()
                    .jobId(rs.getString("job_id"))
                    .module(rs.getString("module"))
                    .tenantId(rs.getString("tenant_id"))
                    .totalRecords(rs.getInt("total_records"))
                    .successCount(rs.getInt("success_count"))
                    .failureCount(rs.getInt("failure_count"))
                    .status(rs.getString("status"))
                    .startTimeMs(rs.getLong("start_time_ms"))
                    .endTimeMs(rs.getLong("end_time_ms"))
                    .throughputPerSec(rs.getDouble("throughput_per_sec"))
                    .avgResponseTimeMs(rs.getDouble("avg_response_time_ms"))
                    .errorSummary(rs.getString("error_summary"))
                    .build();
        }
    }
}
