package org.egov.garbageservice;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

/**
 * Entry point for the HP Garbage Service Spring Boot application.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"org.egov.garbageservice", "org.egov.garbageservice.web.controllers",
        "org.egov.garbageservice.config", "org.egov.garbageservice.repository"})
@Import({TracerConfiguration.class})
@EnableKafka
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
public class GarbageServiceApplication {

    @Value("${app.timezone}")
    private String timeZone;

    /**
     * Main entry point for starting the Garbage Service application.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Initializes SpringApplication context.</li>
     *   <li>Configures embedded Tomcat server and loads application properties.</li>
     *   <li>Enables Spring auto-configuration and dispatches startup logs.</li>
     * </ol>
     *
     * @param args the args parameter
     * @return the output result
     */

    public static void main(String[] args) {
        SpringApplication.run(GarbageServiceApplication.class, args);
    }

    /**
     * Creates and configures the restTemplate Spring bean.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Initializes Spring bean configuration instance.</li>
     *   <li>Applies custom timeouts, interceptors, and properties.</li>
     *   <li>Registers bean in Spring application context.</li>
     * </ol>
     *
     * @return the output result
     */

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Creates and configures the lockProvider Spring bean.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Initializes Spring bean configuration instance.</li>
     *   <li>Applies custom timeouts, interceptors, and properties.</li>
     *   <li>Registers bean in Spring application context.</li>
     * </ol>
     *
     * @param dataSource the dataSource parameter
     * @return the output result
     */

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build()
        );
    }
}