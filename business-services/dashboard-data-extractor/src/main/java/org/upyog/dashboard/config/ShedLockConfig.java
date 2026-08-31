package org.upyog.dashboard.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

/**
 * Configuration class for initializing ShedLock and enabling Spring Scheduling.
 * <p>
 * Provides the central {@link net.javacrumbs.shedlock.core.LockProvider} bean
 * backed by the default Postgres DataSource. This is used for both
 * {@code @SchedulerLock} annotations on background cron tasks and dynamic
 * programmatic locking for manual API batches.
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
public class ShedLockConfig {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }
}
