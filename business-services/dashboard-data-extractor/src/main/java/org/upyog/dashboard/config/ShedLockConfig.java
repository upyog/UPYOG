package org.upyog.dashboard.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Configuration class for initializing Database beans, ShedLock, and enabling Spring Scheduling.
 * <p>
 * Provides the {@link NamedParameterJdbcTemplate} and {@link net.javacrumbs.shedlock.core.LockProvider}
 * backed by the primary DataSource.
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
public class ShedLockConfig {

    /**
     * Creates and registers the {@link NamedParameterJdbcTemplate} bean backed by the application's primary DataSource.
     *
     * @param dataSource the configured primary database {@link DataSource}
     * @return the initialized {@link NamedParameterJdbcTemplate}
     */
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Creates and registers the ShedLock {@link LockProvider} bean backed by JDBC template.
     *
     * @param dataSource the configured primary database {@link DataSource}
     * @return the initialized {@link LockProvider} for distributed scheduler locking
     */
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }
}

