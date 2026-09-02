package org.upyog.dashboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * Centralized application properties configuration class.
 *
 * <p>
 * Retrieves properties from {@code application.properties} without default
 * fallbacks to ensure that missing configuration keys cause a fail-fast startup
 * failure.
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

    public String getEffectiveDailyUploadMode() {
        return (dailyUploadMode != null && !dailyUploadMode.trim().isEmpty()) ? dailyUploadMode.trim() : "API";
    }

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
