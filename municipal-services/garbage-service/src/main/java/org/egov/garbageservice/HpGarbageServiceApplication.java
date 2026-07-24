package org.egov.garbageservice;

import javax.sql.DataSource;
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

/** Entry point for the HP Garbage Service Spring Boot application. */
@SpringBootApplication
@ComponentScan(basePackages = { "org.egov.garbageservice", "org.egov.garbageservice.controller",
        "org.egov.garbageservice.config", "org.egov.garbageservice.repository" })
@Import({ TracerConfiguration.class })
@EnableKafka
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
public class HpGarbageServiceApplication {
	
	@Value("${app.timezone}")
    private String timeZone;

	public static void main(String[] args) {
		SpringApplication.run(HpGarbageServiceApplication.class, args);
	}
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

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