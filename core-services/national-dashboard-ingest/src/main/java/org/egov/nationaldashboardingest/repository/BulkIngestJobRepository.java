package org.egov.nationaldashboardingest.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.producer.Producer;
import org.egov.nationaldashboardingest.repository.querybuilder.BulkIngestJobQueryBuilder;
import org.egov.nationaldashboardingest.web.models.BulkIngestJob;
import org.egov.nationaldashboardingest.web.models.BulkIngestJobRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository component for managing bulk file ingestion execution state in {@code ug_bulk_ingest_job}.
 * <p>
 * Architecture design:
 * <ul>
 *   <li>Reads (SELECT) are performed synchronously via {@link JdbcTemplate} using query constants
 *       defined in {@link BulkIngestJobQueryBuilder#SELECT_JOB_BY_FILE_NAME}.</li>
 *   <li>Writes (INSERT / UPDATE) are asynchronously pushed to Kafka topics via {@link Producer}
 *       and handled by the egov-persister framework using {@code bulk-ingest-job-persister.yml}.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Repository
public class BulkIngestJobRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Producer producer;

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Queries the latest bulk ingestion job record by S3 file name using {@link BulkIngestJobQueryBuilder#SELECT_JOB_BY_FILE_NAME}.
     *
     * @param fileName S3 object key or file path identifier
     * @return {@link Optional} containing matching {@link BulkIngestJob} if found, empty otherwise
     */
    public Optional<BulkIngestJob> findByFileName(String fileName) {
        try {
            // Execute SELECT query using query string constant from BulkIngestJobQueryBuilder
            BulkIngestJob job = jdbcTemplate.queryForObject(
                    BulkIngestJobQueryBuilder.SELECT_JOB_BY_FILE_NAME,
                    new BeanPropertyRowMapper<>(BulkIngestJob.class),
                    fileName
            );
            return Optional.ofNullable(job);
        } catch (EmptyResultDataAccessException e) {
            // Return empty optional if no prior job execution record exists for this file
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error executing SELECT query for file: {}", fileName, e);
            return Optional.empty();
        }
    }

    /**
     * Publishes an insert event for a new bulk ingestion job record to the egov-persister Kafka topic.
     *
     * @param job {@link BulkIngestJob} entity containing initial execution details
     */
    public void save(BulkIngestJob job) {
        if (job == null) {
            log.error("Cannot save null BulkIngestJob record.");
            return;
        }

        // Wrap domain job entity into persister event request wrapper
        BulkIngestJobRequest request = BulkIngestJobRequest.builder()
                .bulkIngestJob(job)
                .build();

        String topic = applicationProperties.getSaveBulkIngestJobTopic();
        log.info("Pushing bulk ingest job save event to Kafka persister topic: {}, jobId: {}", topic, job.getId());

        // Publish event to Kafka for asynchronous persistence via egov-persister
        producer.push(topic, request);
    }

    /**
     * Publishes an update event for an existing bulk ingestion job execution to the egov-persister Kafka topic.
     *
     * @param id                 unique job execution identifier
     * @param status             updated status (e.g. COMPLETED, FAILED)
     * @param totalRowsProcessed total count of rows processed
     * @param failedRowsCount    total count of rows that encountered errors
     * @param lastModifiedTime   epoch timestamp of update
     */
    public void updateJobStatus(String id, String status, int totalRowsProcessed, int failedRowsCount, long lastModifiedTime) {
        BulkIngestJob updatedJob = BulkIngestJob.builder()
                .id(id)
                .status(status)
                .totalRowsProcessed(totalRowsProcessed)
                .failedRowsCount(failedRowsCount)
                .lastModifiedTime(lastModifiedTime)
                .build();

        // Wrap updated job entity into persister event request wrapper
        BulkIngestJobRequest request = BulkIngestJobRequest.builder()
                .bulkIngestJob(updatedJob)
                .build();

        String topic = applicationProperties.getUpdateBulkIngestJobTopic();
        log.info("Pushing bulk ingest job update event to Kafka persister topic: {}, jobId: {}, status: {}", topic, id, status);

        // Publish update event to Kafka for asynchronous table modification via egov-persister
        producer.push(topic, request);
    }
}
