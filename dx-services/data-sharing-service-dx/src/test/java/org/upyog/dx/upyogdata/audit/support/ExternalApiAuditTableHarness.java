package org.upyog.dx.upyogdata.audit.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.upyog.externalaudit.config.ExternalApiAuditProperties;
import org.upyog.externalaudit.masking.SensitivePayloadMasker;
import org.upyog.externalaudit.model.ExternalApiAuditDetail;
import org.upyog.externalaudit.model.ExternalApiAuditDetailWrapper;
import org.upyog.externalaudit.model.ExternalApiErrorDetails;
import org.upyog.externalaudit.producer.ExternalApiAuditPublisher;
import org.upyog.externalaudit.repository.IntegrationAuditRepository;
import org.upyog.externalaudit.service.ExternalApiAuditLogger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Embedded Postgres plus the exact upsert SQL from the library {@code external-api-audit-persister.yml}.
 * Kafka is replaced by a synchronous publisher so tests can assert table rows.
 */
public final class ExternalApiAuditTableHarness implements AutoCloseable {

    private static final String MESSAGE_SQL = """
            INSERT INTO ug_external_api_message_detail(id, correlation_id, tenant_id, external_api_name, direction, request_time, status, http_status_code, response_time, duration_ms, retry_count, created_time, last_modified_time)
            VALUES (COALESCE(cast(? as uuid), gen_random_uuid()), ?, ?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, 0), COALESCE(?, 0), ?)
            ON CONFLICT (correlation_id) DO UPDATE SET
              tenant_id = COALESCE(EXCLUDED.tenant_id, ug_external_api_message_detail.tenant_id),
              external_api_name = COALESCE(EXCLUDED.external_api_name, ug_external_api_message_detail.external_api_name),
              direction = COALESCE(EXCLUDED.direction, ug_external_api_message_detail.direction),
              request_time = COALESCE(EXCLUDED.request_time, ug_external_api_message_detail.request_time),
              status = CASE
                WHEN COALESCE(EXCLUDED.retry_count, 0) > COALESCE(ug_external_api_message_detail.retry_count, 0) THEN EXCLUDED.status
                WHEN ug_external_api_message_detail.status IN ('SUCCESS', 'FAILED') THEN ug_external_api_message_detail.status
                ELSE EXCLUDED.status
              END,
              http_status_code = COALESCE(EXCLUDED.http_status_code, ug_external_api_message_detail.http_status_code),
              response_time = COALESCE(EXCLUDED.response_time, ug_external_api_message_detail.response_time),
              duration_ms = COALESCE(EXCLUDED.duration_ms, ug_external_api_message_detail.duration_ms),
              retry_count = GREATEST(COALESCE(EXCLUDED.retry_count, 0), COALESCE(ug_external_api_message_detail.retry_count, 0)),
              last_modified_time = EXCLUDED.last_modified_time
            """;

    private static final String RAW_SQL = """
            INSERT INTO ug_external_api_message_raw_detail(id, correlation_id, request_payload, response_payload, payload_size_bytes, created_time, last_modified_time)
            VALUES (COALESCE(cast(? as uuid), gen_random_uuid()), ?, cast(? as jsonb), cast(? as jsonb), ?, COALESCE(?, 0), ?)
            ON CONFLICT (correlation_id) DO UPDATE SET
              request_payload = COALESCE(EXCLUDED.request_payload, ug_external_api_message_raw_detail.request_payload),
              response_payload = COALESCE(EXCLUDED.response_payload, ug_external_api_message_raw_detail.response_payload),
              payload_size_bytes = COALESCE(EXCLUDED.payload_size_bytes, ug_external_api_message_raw_detail.payload_size_bytes),
              last_modified_time = EXCLUDED.last_modified_time
            """;

    private static final String ERROR_SQL = """
            INSERT INTO ug_external_api_error_detail(id, correlation_id, error_code, error_type, error_message, created_time)
            VALUES (cast(? as uuid), ?, ?, ?, ?, ?)
            """;

    private final EmbeddedPostgres postgres;
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper objectMapper;
    private final IntegrationAuditRepository repository;

    private ExternalApiAuditTableHarness(EmbeddedPostgres postgres, DataSource dataSource, ObjectMapper objectMapper) {
        this.postgres = postgres;
        this.jdbc = new JdbcTemplate(dataSource);
        this.transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        this.objectMapper = objectMapper;
        this.repository = new IntegrationAuditRepository(jdbc);
        applySchema();
    }

    public static ExternalApiAuditTableHarness start() {
        try {
            EmbeddedPostgres postgres = EmbeddedPostgres.builder().start();
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            return new ExternalApiAuditTableHarness(postgres, postgres.getPostgresDatabase(), objectMapper);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to start embedded PostgreSQL for audit integration tests", exception);
        }
    }

    public ExternalApiAuditLogger newLogger(boolean capturePayload) {
        return newLogger(capturePayload, persistingPublisher());
    }

    public ExternalApiAuditLogger newLogger(boolean capturePayload, ExternalApiAuditPublisher publisher) {
        ExternalApiAuditProperties properties = new ExternalApiAuditProperties();
        properties.setDetailTopic("external-api-audit-details");
        properties.setCapturePayloadEnabled(capturePayload);
        properties.setStaleThresholdMs(600000);
        properties.getCleanup().setRetentionMs(2592000000L);
        return new ExternalApiAuditLogger(publisher, properties, objectMapper,
                new SensitivePayloadMasker(objectMapper, properties));
    }

    public ExternalApiAuditPublisher persistingPublisher() {
        return (topic, event) -> persist(event);
    }

    public ExternalApiAuditPublisher throwingPublisher() {
        return (topic, event) -> {
            throw new IllegalStateException("Kafka unavailable");
        };
    }

    public void persist(Object event) {
        if (!(event instanceof ExternalApiAuditDetailWrapper wrapper) || wrapper.getApiAuditDetail() == null) {
            return;
        }
        persistDetail(wrapper.getApiAuditDetail());
    }

    public void persistDetail(ExternalApiAuditDetail detail) {
        transactionTemplate.executeWithoutResult(status -> {
            jdbc.update(MESSAGE_SQL,
                    detail.getId(),
                    detail.getCorrelationId(),
                    detail.getTenantId(),
                    detail.getExternalApiName(),
                    detail.getDirection(),
                    detail.getRequestTime(),
                    detail.getStatus(),
                    detail.getHttpStatusCode(),
                    detail.getResponseTime(),
                    detail.getDurationMs(),
                    detail.getRetryCount(),
                    detail.getCreatedTime(),
                    detail.getLastModifiedTime());
            jdbc.update(RAW_SQL,
                    detail.getRawDetailId(),
                    detail.getCorrelationId(),
                    toJson(detail.getRequestPayload()),
                    toJson(detail.getResponsePayload()),
                    detail.getPayloadSizeBytes(),
                    detail.getCreatedTime(),
                    detail.getLastModifiedTime());
            ExternalApiErrorDetails errorDetails = detail.getErrorDetails();
            if (errorDetails != null) {
                jdbc.update(ERROR_SQL,
                        errorDetails.getId(),
                        errorDetails.getCorrelationId(),
                        errorDetails.getErrorCode(),
                        errorDetails.getErrorType(),
                        errorDetails.getErrorMessage(),
                        errorDetails.getCreatedTime());
            }
        });
    }

    public void truncate() {
        jdbc.execute("""
                TRUNCATE TABLE ug_external_api_error_detail,
                               ug_external_api_message_raw_detail,
                               ug_external_api_message_detail
                """);
    }

    public JdbcTemplate jdbc() {
        return jdbc;
    }

    public IntegrationAuditRepository repository() {
        return repository;
    }

    public int messageCount() {
        return count("ug_external_api_message_detail");
    }

    public int rawCount() {
        return count("ug_external_api_message_raw_detail");
    }

    public int errorCount() {
        return count("ug_external_api_error_detail");
    }

    public Map<String, Object> message(String correlationId) {
        return jdbc.queryForMap("""
                SELECT correlation_id, tenant_id, external_api_name, direction, status,
                       http_status_code, retry_count, request_time, response_time, duration_ms
                FROM ug_external_api_message_detail
                WHERE correlation_id = ?
                """, correlationId);
    }

    public Map<String, Object> raw(String correlationId) {
        return jdbc.queryForMap("""
                SELECT correlation_id, request_payload::text AS request_payload,
                       response_payload::text AS response_payload, payload_size_bytes
                FROM ug_external_api_message_raw_detail
                WHERE correlation_id = ?
                """, correlationId);
    }

    public List<Map<String, Object>> errors(String correlationId) {
        return jdbc.queryForList("""
                SELECT correlation_id, error_code, error_type, error_message
                FROM ug_external_api_error_detail
                WHERE correlation_id = ?
                ORDER BY created_time
                """, correlationId);
    }

    public List<Map<String, Object>> messagesByApi(String externalApiName) {
        return jdbc.queryForList("""
                SELECT correlation_id, tenant_id, external_api_name, direction, status,
                       http_status_code, retry_count
                FROM ug_external_api_message_detail
                WHERE external_api_name = ?
                ORDER BY created_time
                """, externalApiName);
    }

    public String onlyCorrelationId() {
        List<String> ids = jdbc.queryForList("SELECT correlation_id FROM ug_external_api_message_detail", String.class);
        if (ids.size() != 1) {
            throw new AssertionError("Expected exactly 1 audit row, found " + ids.size() + ": " + ids);
        }
        return ids.get(0);
    }

    @Override
    public void close() {
        try {
            postgres.close();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to stop embedded PostgreSQL", exception);
        }
    }

    private int count(String table) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
        return count == null ? 0 : count;
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to serialize audit payload", exception);
        }
    }

    private void applySchema() {
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS ug_external_api_message_detail (
                    id UUID PRIMARY KEY,
                    correlation_id VARCHAR(128) NOT NULL UNIQUE,
                    tenant_id VARCHAR(256) NOT NULL,
                    external_api_name VARCHAR(256) NOT NULL,
                    direction VARCHAR(32) NOT NULL,
                    request_time BIGINT NOT NULL,
                    response_time BIGINT,
                    duration_ms BIGINT,
                    status VARCHAR(32) NOT NULL,
                    http_status_code INTEGER,
                    retry_count INTEGER NOT NULL DEFAULT 0,
                    created_time BIGINT NOT NULL,
                    last_modified_time BIGINT NOT NULL
                )
                """);
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS ug_external_api_message_raw_detail (
                    id UUID PRIMARY KEY,
                    correlation_id VARCHAR(128) NOT NULL,
                    request_payload JSONB,
                    response_payload JSONB,
                    payload_size_bytes BIGINT,
                    created_time BIGINT NOT NULL,
                    last_modified_time BIGINT NOT NULL,
                    CONSTRAINT fk_ug_external_api_message_raw_detail_correlation
                        FOREIGN KEY (correlation_id) REFERENCES ug_external_api_message_detail (correlation_id)
                )
                """);
        jdbc.execute("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_ug_external_api_message_raw_detail_correlation_id_unique
                    ON ug_external_api_message_raw_detail (correlation_id)
                """);
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS ug_external_api_error_detail (
                    id UUID PRIMARY KEY,
                    correlation_id VARCHAR(128) NOT NULL,
                    error_code VARCHAR(256) NOT NULL,
                    error_type VARCHAR(32) NOT NULL,
                    error_message TEXT NOT NULL,
                    created_time BIGINT NOT NULL,
                    CONSTRAINT fk_ug_external_api_error_detail_correlation
                        FOREIGN KEY (correlation_id) REFERENCES ug_external_api_message_detail (correlation_id)
                )
                """);
    }
}
