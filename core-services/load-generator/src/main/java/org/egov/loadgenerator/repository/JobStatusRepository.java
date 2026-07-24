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

    public void save(JobStatus job) {
        jdbcTemplate.update(INSERT_SQL,
                job.getJobId(), job.getModule(), job.getTenantId(),
                job.getTotalRecords(), job.getSuccessCount(), job.getFailureCount(),
                job.getStatus(), job.getStartTimeMs(), job.getEndTimeMs(),
                job.getThroughputPerSec(), job.getAvgResponseTimeMs(), job.getErrorSummary());
    }

    public void update(JobStatus job) {
        jdbcTemplate.update(UPDATE_SQL,
                job.getSuccessCount(), job.getFailureCount(), job.getStatus(),
                job.getEndTimeMs(), job.getThroughputPerSec(), job.getAvgResponseTimeMs(),
                job.getErrorSummary(), job.getJobId());
    }

    public Optional<JobStatus> findById(String jobId) {
        List<JobStatus> results = jdbcTemplate.query(SELECT_BY_ID, new JobStatusRowMapper(), jobId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<JobStatus> findAll() {
        return jdbcTemplate.query(SELECT_ALL, new JobStatusRowMapper());
    }

    public int deleteByModuleAndTenant(String module, String tenantId) {
        return jdbcTemplate.update(DELETE_BY_MODULE_TENANT, module.toUpperCase(), tenantId);
    }

    private static class JobStatusRowMapper implements RowMapper<JobStatus> {
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
