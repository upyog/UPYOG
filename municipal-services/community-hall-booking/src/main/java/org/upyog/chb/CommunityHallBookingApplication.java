package org.upyog.chb;


import java.util.TimeZone;

import javax.sql.DataSource;

import org.egov.encryption.config.EncryptionConfiguration;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

/**
 * Spring Boot entry point for the Community Hall Booking municipal service.
 * <p>
 * Enables scheduling with ShedLock-backed distributed locks, imports tracer/encryption configuration,
 * and registers shared JSON/JDBC beans used across the module.
 */
@Import({
		TracerConfiguration.class , EncryptionConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.chb" })
@EnableTransactionManagement
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
public class CommunityHallBookingApplication {
	
	@Value("${app.timezone}")
	private String timeZone;

    public static void main(String[] args) {
    	System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(CommunityHallBookingApplication.class, args);
    }

	/**
	 * Shared {@link ObjectMapper} configured for API deserialization and timezone-aware timestamps.
	 *
	 * @return mapper that ignores unknown JSON properties and uses the configured application timezone
	 */
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.setTimeZone(TimeZone.getTimeZone(timeZone))
				.findAndRegisterModules();
	}

	/**
	 * Registers the HTTP message converter that uses the module {@link ObjectMapper}.
	 *
	 * @param objectMapper shared JSON mapper bean
	 * @return Jackson converter for Spring MVC responses
	 */
	@Bean
	public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	/**
	 * Configures the ShedLock LockProvider bean using JDBC.
	 * <p>
	 * This bean sets up ShedLock to use the application's shared database for acquiring and managing distributed locks.
	 * It uses Spring's JdbcTemplate and ensures all instances of the service check and update the same `shedlock` table,
	 * enabling only one instance to run a scheduled task at any given time.
	 * <p>
	 * The `.usingDbTime()` ensures that all instances rely on the database server's time to avoid clock drift issues
	 * across different machines.
	 *
	 * @param dataSource the shared application DataSource
	 * @return a configured LockProvider instance for ShedLock
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
