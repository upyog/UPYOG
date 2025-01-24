package org.egov.pg.service.jobs.earlyReconciliation;

import java.util.Date;

import org.egov.pg.config.AppProperties;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

/**
 * Scheduled to run frequent interval, configurable
 */
@Configuration
public class EarlyReconciliationJobConfig {

    @Autowired
    private AppProperties appProperties;

    @Bean
    JobDetailFactoryBean earlyReconciliationJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(EarlyReconciliationJob.class);
        jobDetailFactory.setName("earlyReconciliationJob"); // Unique job name
        jobDetailFactory.setGroup("status-update"); // Group name
        jobDetailFactory.setDurability(true); // Ensure job durability
        return jobDetailFactory;
    }

    @Bean
    CronTriggerFactoryBean earlyReconciliationTrigger(JobDetail earlyReconciliationJobDetail) {
        CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
        int runEvery = appProperties.getEarlyReconcileJobRunInterval();
        Integer runEveryMinutes, runEveryHours;
        runEveryHours = runEvery / 60;
        runEveryMinutes = runEvery % 60;


        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(earlyReconciliationJobDetail);
//        cronTriggerFactoryBean.setCronExpression("0 0/" + appProperties.getReconciliationTimeout().toString() + " * * * ?");
        cronTriggerFactoryBean.setCronExpression("0 " + runEveryHours + "/" + runEveryMinutes + " * * * ?");
        cronTriggerFactoryBean.setGroup("status-update");
        return cronTriggerFactoryBean;
    }

}
