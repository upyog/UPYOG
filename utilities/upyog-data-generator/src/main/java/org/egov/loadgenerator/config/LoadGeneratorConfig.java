package org.egov.loadgenerator.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * Central configuration component for the Load Generator application.
 *
 * <p>This class exposes all configurable application properties required by
 * the load generation framework. It acts as a single source of truth for
 * runtime configuration including thread pool settings, retry policies,
 * WebClient timeouts, module-specific service endpoints, authentication
 * credentials, and application-wide timezone initialization.
 *
 * <p>Configuration values are injected from the application's external
 * configuration (for example {@code application.yml} or environment
 * variables) using Spring's {@link Value} annotation. Centralizing these
 * properties allows the load generator to remain environment-independent
 * while making deployment-specific values configurable without code changes.
 *
 * <h3>Configuration Categories</h3>
 * <ul>
 *   <li>Application initialization (timezone)</li>
 *   <li>Thread pool and batch processing configuration</li>
 *   <li>Retry policy for failed requests</li>
 *   <li>HTTP client timeout configuration</li>
 *   <li>Module-specific API endpoints (PT, TL, PGR, WS, SW, SV, Asset)</li>
 *   <li>Internal authentication and system user credentials</li>
 * </ul>
 *
 * <h3>Lifecycle</h3>
 * <p>After all configuration properties have been injected by Spring,
 * {@link #initialize()} is invoked automatically to configure the default
 * JVM timezone for the application. This ensures all date and time
 * operations use a consistent timezone across request generation,
 * logging, and downstream service interactions.
 *
 * <h3>Thread Safety</h3>
 * <p>This component is managed as a singleton Spring bean and serves as a
 * read-only configuration holder after application startup.
 *
 * @see TracerConfiguration
 */
@Component
@Data
@NoArgsConstructor
@Import({TracerConfiguration.class})
public class LoadGeneratorConfig {

    @Value("${app.timezone}")
    private String timeZone;
    
    /**
     * Initializes the application's default JVM timezone.
     *
     * <p>This method is executed automatically after Spring injects all
     * configuration properties. The configured timezone is applied as the
     * JVM default so that all date and time operations use a consistent
     * timezone throughout the application.
     */
    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    // Thread pool config
    @Value("${load.generator.thread.pool.size:50}")
    private int threadPoolSize;

    @Value("${load.generator.batch.size:100}")
    private int batchSize;

    @Value("${load.generator.retry.max.attempts:3}")
    private int maxRetryAttempts;

    @Value("${load.generator.retry.delay.ms:500}")
    private long retryDelayMs;

    @Value("${load.generator.webclient.timeout.seconds:30}")
    private int webClientTimeoutSeconds;

    // Module API URLs
    @Value("${egov.pgr.host}")
    private String pgrHost;

    @Value("${egov.pgr.create.endpoint}")
    private String pgrCreateEndpoint;

    @Value("${egov.tl.host}")
    private String tlHost;

    @Value("${egov.tl.create.endpoint}")
    private String tlCreateEndpoint;

    @Value("${egov.pt.host}")
    private String ptHost;

    @Value("${egov.pt.create.endpoint}")
    private String ptCreateEndpoint;

    @Value("${egov.pt.update.endpoint}")
    private String ptUpdateEndpoint;

    @Value("${egov.pt.search.endpoint}")
    private String ptSearchEndpoint;

    @Value("${egov.ws.host}")
    private String wsHost;

    @Value("${egov.ws.create.endpoint}")
    private String wsCreateEndpoint;

    @Value("${egov.sw.host}")
    private String swHost;

    @Value("${egov.sw.create.endpoint}")
    private String swCreateEndpoint;

    @Value("${egov.sv.host}")
    private String svHost;

    @Value("${egov.sv.create.endpoint}")
    private String svCreateEndpoint;

    @Value("${egov.asset.host}")
    private String assetHost;

    @Value("${egov.asset.create.endpoint}")
    private String assetCreateEndpoint;

    // Auth token for internal calls
    @Value("${egov.internal.auth.token}")
    private String internalAuthToken;

    @Value("${egov.user.uuid}")
    private String systemUserUuid;

    @Value("${egov.user.id:4352}")
    private int systemUserId;
}
