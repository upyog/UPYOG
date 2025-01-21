package org.egov.pg.service.jobs.weekreconcile;



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
    JobDetailFactoryBean weeklyReconciliationJobs() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(weeklyreconcilejob.class);
        jobDetailFactory.setName("weeklyreconcilejob"); // Explicitly set a unique job name
        jobDetailFactory.setGroup("weekly-status-update");   // Keep your group name consistent
        jobDetailFactory.setDurability(true);                // Ensure the job is durable
        return jobDetailFactory;
    }

 

    @Bean
    @Autowired
    CronTriggerFactoryBean weeklyReconciliationTrigger(JobDetail weeklyReconciliationJob) {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(weeklyReconciliationJob);
      //  cronTriggerFactoryBean.setCronExpression("0 0 0 ? * SAT"); // Every Saturday at 12:00 AM
        cronTriggerFactoryBean.setCronExpression("0 15 15 ? * TUE"); // At 12:28 PM every Tuesday
        cronTriggerFactoryBean.setGroup("weekly-status-update");
        return cronTriggerFactoryBean;
    }
}
