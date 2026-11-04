package org.upyog.dashboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * Centralized configuration properties component for dashboard data engine and extractors.
 * <p>
 * Binds environment configurations defined across application properties files:
 * <ul>
 *   <li>UPYOG OAuth user authentication endpoints, system credentials, and retry policies.</li>
 *   <li>National dashboard ingestion engine URLs and bulk initialization endpoints ({@code bulkInitUrl}).</li>
 *   <li>Module-specific classification mappings for Property Tax (PT) tax heads, usage categories, and digital payments.</li>
 *   <li>AWS S3 connection settings (bucket, region, access credentials, root folder prefix).</li>
 *   <li>Kafka topic destinations for asynchronous persistence of audit records and error logs.</li>
 *   <li>Upload mode strategies (API vs. S3) for daily incremental and legacy batch workflows.</li>
 * </ul>
 * </p>
 */
@Getter
@Component
public class DashboardProperties {

    // eGov User / OAuth settings
    @Value("${egov.user.host}")
    private String oauthHost;

    @Value("${egov.user.oauth.path}")
    private String oauthPath;

    @Value("${egov.user.oauth.basic.auth}")
    private String basicAuthHeader;

    @Value("${egov.user.search.path}")
    private String userSearchPath;

    // System User settings
    @Value("${dashboard-data.system.user.username}")
    private String username;

    @Value("${dashboard-data.system.user.password}")
    private String password;

    @Value("${dashboard-data.system.user.tenantId}")
    private String tenantId;

    @Value("${dashboard-data.system.user.type}")
    private String userType;

    // OAuth retry config
    @Value("${dashboard-data.oauth-retry.max-attempts}")
    private int oauthMaxAttempts;

    @Value("${dashboard-data.oauth-retry.base-delay-ms}")
    private long oauthBaseDelayMs;

    @Value("${dashboard-data.oauth-retry.max-delay-ms}")
    private long oauthMaxDelayMs;

    // Ingest API settings
    @Value("${national.dashboard.ingest.url}")
    private String dashboardIngestUrl;

    @Value("${national.dashboard.bulk.init.url}")
    private String bulkInitUrl;

    // HTTP Ingestion retry config
    @Value("${dashboard-data.retry.enabled:false}")
    private boolean ingestRetryEnabled;

    @Value("${dashboard-data.retry.max-attempts}")
    private int ingestMaxAttempts;

    @Value("${dashboard-data.retry.base-delay-ms}")
    private long ingestBaseDelayMs;

    @Value("${dashboard-data.retry.max-delay-ms}")
    private long ingestMaxDelayMs;

    // Ingestion date configs
    @Value("${dashboard-data.ingestion.default-start-date}")
    private String defaultStartDateStr;

    // Legacy migration config
    @Value("${legacy.ingestion.enabled}")
    private boolean legacyIngestionEnabled;

    @Value("${legacy.ingestion.default-months}")
    private int legacyDefaultMonths;

    // State-specific usage categories for Property Tax (PT) module
    @Value("${dashboard-data.pt.usage-categories}")
    private java.util.List<String> ptUsageCategories;

    // State-specific Tax Heads mappings
    @Value("${dashboard-data.pt.tax-heads.tax}")
    private java.util.List<String> ptTaxHeads;

    @Value("${dashboard-data.pt.tax-heads.cess}")
    private java.util.List<String> ptCessHeads;

    @Value("${dashboard-data.pt.tax-heads.rebate}")
    private java.util.List<String> ptRebateHeads;

    @Value("${dashboard-data.pt.tax-heads.penalty}")
    private java.util.List<String> ptPenaltyHeads;

    @Value("${dashboard-data.pt.tax-heads.interest}")
    private java.util.List<String> ptInterestHeads;

    // State-specific digital payment modes
    @Value("${dashboard-data.pt.digital-payment-modes}")
    private java.util.List<String> ptDigitalPaymentModes;

    // Metric Location Context
    @Value("${dashboard-data.metric.ulb}")
    private String metricUlb;

    @Value("${dashboard-data.metric.ward}")
    private String metricWard;

    @Value("${dashboard-data.metric.region}")
    private String metricRegion;

    @Value("${dashboard-data.metric.state}")
    private String metricState;

    // DB Retry configuration
    @Value("${dashboard-data.db-retry.max-attempts}")
    private int dbMaxAttempts;

    @Value("${dashboard-data.db-retry.base-delay-ms}")
    private long dbBaseDelayMs;

    @Value("${dashboard-data.db-retry.max-delay-ms}")
    private long dbMaxDelayMs;

    // Daily catch-up limit
    @Value("${dashboard-data.daily.catch-up-limit-days}")
    private int dailyCatchUpLimitDays;

    // Toggle for persister vs direct JDBC
    @Value("${dashboard-data.persister.enabled}")
    private boolean persisterEnabled;

    // Daily upload mode (API or S3)
    @Value("${dashboard-data.daily.upload-mode}")
    private String dailyUploadMode;

    // Legacy upload mode (API or S3)
    @Value("${dashboard-data.legacy.upload-mode}")
    private String legacyUploadMode;

    // Generic delimited file configuration (e.g. .psv, .csv, .txt)
    @Value("${dashboard-data.delimited-file.enabled:${dashboard-data.pipe-file.enabled:false}}")
    private boolean delimitedFileEnabled;

    @Value("${dashboard-data.delimited-file.delimiter:${dashboard-data.pipe-file.delimiter:|}}")
    private String fileDelimiter;

    @Value("${dashboard-data.delimited-file.file-extension:.psv}")
    private String delimitedFileExtension;

    @Value("${dashboard-data.delimited-file.keep-file:${dashboard-data.pipe-file.keep-file:false}}")
    private boolean delimitedKeepFile;

    /**
     * Legacy alias getter returning whether flat delimited file generation is enabled.
     *
     * @return true if delimited file generation is enabled
     */
    public boolean isPipeFileEnabled() {
        return delimitedFileEnabled;
    }

    /**
     * Legacy alias getter returning the configured dataset field delimiter.
     *
     * @return delimiter character string
     */
    public String getPipeFileDelimiter() {
        return fileDelimiter;
    }

    /**
     * Resolves the effective upload mode strategy for daily incremental ingestion batches.
     * <p>
     * Checks configured property {@code dashboard-data.daily.upload-mode}.
     * If unconfigured or blank, defaults to {@code "API"} (direct HTTP multipart POST).
     * </p>
     *
     * @return normalized upload mode string (e.g. "API", "S3", or "FILESTORE")
     */
    public String getEffectiveDailyUploadMode() {
        return (dailyUploadMode != null && !dailyUploadMode.trim().isEmpty()) ? dailyUploadMode.trim() : "API";
    }

    /**
     * Resolves the effective upload mode strategy for historical legacy backfill batches.
     * <p>
     * Checks configured property {@code dashboard-data.legacy.upload-mode}.
     * If unconfigured or blank, defaults to {@code "S3"} (upload Excel file to AWS S3 and trigger bulk init).
     * </p>
     *
     * @return normalized upload mode string (e.g. "S3", "API", or "FILESTORE")
     */
    public String getEffectiveLegacyUploadMode() {
        return (legacyUploadMode != null && !legacyUploadMode.trim().isEmpty()) ? legacyUploadMode.trim() : "S3";
    }

    // S3 properties
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

    // Kafka Topics configuration
    @Value("${kafka.topics.save.ingestion.detail}")
    private String saveIngestionDetailTopic;

    @Value("${kafka.topics.save.module.ingestion.detail}")
    private String saveLegacyIngestionDetailTopic;

    @Value("${kafka.topics.update.module.ingestion.detail}")
    private String updateLegacyIngestionDetailTopic;

    @Value("${kafka.topics.save.dashboard-data.error.log}")
    private String saveAdapterErrorLogTopic;

    @Value("${kafka.topics.update.dashboard-data.module.summary}")
    private String updateAdapterModuleSummaryTopic;
}
