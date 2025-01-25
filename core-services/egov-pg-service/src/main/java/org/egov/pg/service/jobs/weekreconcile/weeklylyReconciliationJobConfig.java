package org.egov.pg.service.jobs.weekreconcile;



import java.util.Date;

import org.egov.pg.config.AppProperties;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

/**
 * Scheduled to run weekly reconciliation job
 */
@Configuration
public class weeklylyReconciliationJobConfig {

    @Autowired
    private AppProperties appProperties;

    @Bean
    JobDetailFactoryBean weeklyReconciliationJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(weeklyreconcilejob.class);
        jobDetailFactory.setName("weeklyReconciliationJob"); // Unique job name
        jobDetailFactory.setGroup("status-update"); // Group name
        jobDetailFactory.setDurability(true); // Ensure job durability
        return jobDetailFactory;
    }

    @Bean
    CronTriggerFactoryBean weeklyReconciliationTrigger(JobDetail weeklyReconciliationJobDetail) {
        CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
        triggerFactory.setJobDetail(weeklyReconciliationJobDetail);
        triggerFactory.setCronExpression("0 0 0 ? * SAT"); // Every Saturday at 12:00 AM
      //  triggerFactory.setCronExpression("0 0/1 * * * ?"); // Cron expression (every 5 minutes as an example)

        // Delay start by 2 minutes
      //  triggerFactory.setStartTime(new Date(System.currentTimeMillis() + 2 * 60 * 1000)); // Current time + 2 minutes
        triggerFactory.setName("weeklyReconciliationTrigger"); // Unique trigger name
        triggerFactory.setGroup("status-update");
        return triggerFactory;
    }
    
}
