package upyog;


import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "upyog", "upyog.web.controllers" , "upyog.config"})
@EnableFeignClients(basePackages = "upyog.*")
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
public class EstateManagement {

	/**
	 * Creates a JDBC-based ShedLock provider to ensure that scheduled
	 * jobs are executed by only one application instance at a time.
	 * Uses database time to avoid clock synchronization issues.
	 *
	 * @param dataSource application data source
	 * @return configured lock provider
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

    public static void main(String[] args) throws Exception {
        SpringApplication.run(EstateManagement.class, args);
    }

}
