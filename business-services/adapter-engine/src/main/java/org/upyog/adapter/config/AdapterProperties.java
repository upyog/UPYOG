package org.upyog.adapter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

/**
 * Centralized application properties configuration class.
 *
 * <p>Retrieves properties from {@code application.properties} without default fallbacks
 * to ensure that missing configuration keys cause a fail-fast startup failure.
 */
@Getter
@Component
public class AdapterProperties {

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
    @Value("${adapter.system.user.username}")
    private String username;

    @Value("${adapter.system.user.password}")
    private String password;

    @Value("${adapter.system.user.tenantId}")
    private String tenantId;

    @Value("${adapter.system.user.type}")
    private String userType;

    // OAuth retry config
    @Value("${adapter.oauth-retry.max-attempts}")
    private int oauthMaxAttempts;

    @Value("${adapter.oauth-retry.base-delay-ms}")
    private long oauthBaseDelayMs;

    @Value("${adapter.oauth-retry.max-delay-ms}")
    private long oauthMaxDelayMs;

    // Ingest API settings
    @Value("${national.dashboard.ingest.url}")
    private String dashboardIngestUrl;

    // HTTP Ingestion retry config
    @Value("${adapter.retry.enabled:false}")
    private boolean ingestRetryEnabled;

    @Value("${adapter.retry.max-attempts}")
    private int ingestMaxAttempts;

    @Value("${adapter.retry.base-delay-ms}")
    private long ingestBaseDelayMs;

    @Value("${adapter.retry.max-delay-ms}")
    private long ingestMaxDelayMs;

    // Ingestion date configs
    @Value("${adapter.ingestion.default-start-date}")
    private String defaultStartDateStr;

    // Legacy migration config
    @Value("${legacy.ingestion.enabled}")
    private boolean legacyIngestionEnabled;

    @Value("${legacy.ingestion.default-months}")
    private int legacyDefaultMonths;

    // State-specific usage categories for Property Tax (PT) module
    @Value("${adapter.pt.usage-categories}")
    private java.util.List<String> ptUsageCategories;

    // State-specific Tax Heads mappings
    @Value("${adapter.pt.tax-heads.tax}")
    private java.util.List<String> ptTaxHeads;

    @Value("${adapter.pt.tax-heads.cess}")
    private java.util.List<String> ptCessHeads;

    @Value("${adapter.pt.tax-heads.rebate}")
    private java.util.List<String> ptRebateHeads;

    @Value("${adapter.pt.tax-heads.penalty}")
    private java.util.List<String> ptPenaltyHeads;

    @Value("${adapter.pt.tax-heads.interest}")
    private java.util.List<String> ptInterestHeads;

    // State-specific digital payment modes
    @Value("${adapter.pt.digital-payment-modes}")
    private java.util.List<String> ptDigitalPaymentModes;

    // Metric Location Context
    @Value("${adapter.metric.ulb}")
    private String metricUlb;

    @Value("${adapter.metric.ward}")
    private String metricWard;

    @Value("${adapter.metric.region}")
    private String metricRegion;

    @Value("${adapter.metric.state}")
    private String metricState;

    // DB Retry configuration
    @Value("${adapter.db-retry.max-attempts}")
    private int dbMaxAttempts;

    @Value("${adapter.db-retry.base-delay-ms}")
    private long dbBaseDelayMs;

    @Value("${adapter.db-retry.max-delay-ms}")
    private long dbMaxDelayMs;

    // Daily catch-up limit
    @Value("${adapter.daily.catch-up-limit-days}")
    private int dailyCatchUpLimitDays;

    // Toggle for persister vs direct JDBC
    @Value("${adapter.persister.enabled:true}")
    private boolean persisterEnabled;
}
