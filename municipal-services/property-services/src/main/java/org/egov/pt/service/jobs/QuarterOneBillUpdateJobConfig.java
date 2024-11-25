package org.egov.pt.service.jobs;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

/**
 * Scheduled to run at 12am and 12pm on a daily basis
 */
@Configuration
public class QuarterOneBillUpdateJobConfig {

    @Bean
    JobDetailFactoryBean quarterOneBillUpdateJob() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(QuarterOneBillUpdateJob.class);
        jobDetailFactory.setGroup("status-update");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    @Autowired
    CronTriggerFactoryBean processStatusUpdateTrigger(JobDetail quarterOneBillUpdateJob) {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(quarterOneBillUpdateJob);
        cronTriggerFactoryBean.setCronExpression("0 0 0,12 * * ?");
        cronTriggerFactoryBean.setGroup("status-update");
        return cronTriggerFactoryBean;
    }

}
