package org.upyog.externalaudit.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

/**
 * JDBC ShedLock so reconciliation and cleanup run on only one pod of the service
 * that owns {@code ug_external_api_*}. Inactive unless a job flag is enabled.
 */
@Configuration
@ConditionalOnClass({LockProvider.class, DataSource.class})
@ConditionalOnBean(DataSource.class)
@ConditionalOnExpression("${external.api.audit.reconciliation.enabled:false} || ${external.api.audit.cleanup.enabled:false}")
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
public class ExternalApiAuditSchedulerLockConfiguration {

    @Bean
    public LockProvider externalApiAuditLockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build());
    }
}
