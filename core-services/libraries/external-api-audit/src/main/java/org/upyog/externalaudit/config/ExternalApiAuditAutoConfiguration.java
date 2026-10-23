package org.upyog.externalaudit.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Spring Boot 3 auto-configuration for the external API audit library.
 * <p>
 * Loaded via {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}.
 * Activates when {@link KafkaTemplate} is present, binds {@code external.api.audit.*},
 * and component-scans {@code org.egov.externalaudit}. Consuming services do not
 * {@code @Import} this class unless auto-configuration is disabled.
 * </p>
 */
@AutoConfiguration
@ConditionalOnClass(KafkaTemplate.class)
@EnableConfigurationProperties(ExternalApiAuditProperties.class)
@ComponentScan(basePackages = "org.upyog.externalaudit")
public class ExternalApiAuditAutoConfiguration {
}
