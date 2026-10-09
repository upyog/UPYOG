package org.egov.nationaldashboardingest.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Centralized configuration properties component for the National Dashboard Ingest service.
 * <p>
 * Holds Spring {@code @Value} injected configuration values governing:
 * <ul>
 *   <li>Elasticsearch cluster connectivity and credentials.</li>
 *   <li>Kafka messaging topics for metrics ingestion, key data persistence, bulk ingestion initiation ({@code bulkIngestTopic}), and audit logging.</li>
 *   <li>Module indexing and allowed group-by attribute schema mappings.</li>
 *   <li>Usage type categorizations and payment channels across municipal domains (PT, WS, FSM, NOC, etc.).</li>
 *   <li>AWS S3 client credentials and storage bucket configurations for downloading bulk datasets.</li>
 *   <li>External API audit logging payload constraints and stale transaction timeout thresholds.</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
@Component
public class ApplicationProperties {

    @Value("${egov.es.host}")
    private String elasticSearchHost;

    @Value("${egov.es.username}")
    private String userName;

    @Value("${egov.es.password}")
    private String password;

    @Value("${master.data.index}")
    private String masterDataIndex;

    @Value("${ingest.data.key.persist.topic}")
    private String keyDataTopic;

    @Value("${bulk.ingest.topic}")
    private String bulkIngestTopic;

    @Value("${save.bulk.ingest.job.topic}")
    private String saveBulkIngestJobTopic;

    @Value("${update.bulk.ingest.job.topic}")
    private String updateBulkIngestJobTopic;

    @Value("${ingest.error.queue}")
    private String ingestErrorQueue;

    @Value("#{${module.index.mapping}}")
    private Map<String, String> moduleIndexMapping;

    @Value("#{${module.fields.mapping}}")
    private Map<String, Map<String, String>> moduleFieldsMapping;

    @Value("#{${module.allowed.groupby.fields.mapping}}")
    private Map<String, List<String>> moduleAllowedGroupByFieldsMapping;

    @Value("#{${master.module.fields.mapping}}")
    private Map<String, Map<String, String>> masterModuleFieldsMapping;

    @Value("${max.data.list.size}")
    private Long maxDataListSize;

    @Value("${max.data.size.kafka}")
    private Integer maxDataSizeKafka;

    @Value("${adaptor.ingest.system.role}")
    private String adaptorIngestSystemRole;

    @Value("#{${national.dashboard.user}}")
    private Map<String, String> nationalDashboardUser;

    @Value("#{${national.dashboard.usageTypePT}}")
    private List<String> nationalDashboardUsageTypePT;

    @Value("#{${national.dashboard.usageTypeWS}}")
    private List<String> nationalDashboardUsageTypeWS;

    @Value("#{${national.dashboard.usageTypeFSM}}")
    private List<String> nationalDashboardUsageTypeFSM;

    @Value("#{${national.dashboard.usageTypeNOC}}")
    private List<String> nationalDashboardUsageTypeNOC;

    @Value("#{${national.dashboard.paymentChannel}}")
    private List<String> nationalDashboardpaymentChannel;

    @Value("#{${national.dashboard.paymentChannelMISC}}")
    private List<String> nationalDashboardpaymentChannelMISC;

    @Value("${national.dashboard.legacy.version}")
    private Boolean isLegacyVersionES;

    @Value("${kafka.topics.notification.email}")
    private String emailNotifTopic;

    @Value("${external.api.audit.detail.topic}")
    private String integrationAuditDetailTopic;

    @Value("${external.api.audit.max.payload.bytes}")
    private int integrationAuditMaxPayloadBytes;

    @Value("${external.api.audit.stale.threshold.ms}")
    private long integrationAuditStaleThresholdMs;

    @Value("${aws.s3.access-key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret-key}")
    private String awsS3SecretKey;

    @Value("${aws.s3.region}")
    private String awsS3Region;

    @Value("${aws.s3.bucket}")
    private String awsS3Bucket;

    @Value("${aws.s3.folder}")
    private String awsS3Folder;

    @Value("${bulk.ingest.temp.dir}")
    private String bulkIngestTempDir;
    
    
    @Value("${bulk.ingest.batch.size}")
    private Integer batchSize;

}

