package org.egov.pt.service.jobs;

import java.util.TimeZone;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

/**
 *  Scheduled to run at 12am to get the bills Assesed in the current financial year and send bill reminder
 */
@Configuration
public class DailyBillUpdateJobConfig {

    @Bean
    JobDetailFactoryBean quarterOneBillUpdateJob() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(DailyBillUpdateJob.class);
        jobDetailFactory.setGroup("bill-update");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    @Autowired
    CronTriggerFactoryBean processBillUpdateTrigger(JobDetail quarterOneBillUpdateJob) {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(quarterOneBillUpdateJob);
        cronTriggerFactoryBean.setCronExpression("0 0 0 * * ?"); //Run every at night
        //cronTriggerFactoryBean.setCronExpression("1 * * * * ?"); // runs every minute for testing
        cronTriggerFactoryBean.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        cronTriggerFactoryBean.setGroup("bill-update");
        return cronTriggerFactoryBean;
    }

}
