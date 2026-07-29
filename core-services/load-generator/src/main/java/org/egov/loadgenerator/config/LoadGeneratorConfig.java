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
 * Configuration class for the Load Generator application.
 *
 * <p>Loads application properties including thread pool configuration,
 * retry settings, WebClient timeout, module endpoints, authentication
 * details, and initializes the default application timezone.</p>
 */
@Component
@Data
@NoArgsConstructor
@Import({TracerConfiguration.class})
public class LoadGeneratorConfig {

    @Value("${app.timezone}")
    private String timeZone;

 /**
 * Initializes the default JVM timezone using the configured application timezone.
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
