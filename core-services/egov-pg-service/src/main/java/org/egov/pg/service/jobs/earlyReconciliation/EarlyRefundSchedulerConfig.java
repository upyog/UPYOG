package org.egov.pg.service.jobs.earlyReconciliation;

import org.egov.pg.config.AppProperties;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class EarlyRefundSchedulerConfig {

    @Autowired
    private AppProperties appProperties;

    @Bean(name = "earlyRefundJobDetail")
    JobDetailFactoryBean refundStatusJob() {
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(EarlyRefundScheduler.class);
        factory.setGroup("refund-status");
        factory.setDurability(true);
        return factory;
    }

  
    @Bean(name = "earlyRefundCronTrigger")
    public CronTriggerFactoryBean earlyRefundCronTrigger(
            @Qualifier("earlyRefundJobDetail") JobDetail earlyReconciliationJob) {
        int runEvery = appProperties.getEarlyReconcileJobRunIntervalRefund();
        Integer runEveryMinutes, runEveryHours;
        runEveryHours = runEvery / 60;
        runEveryMinutes = runEvery % 60;


        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(earlyReconciliationJob);
//        cronTriggerFactoryBean.setCronExpression("0 0/" + appProperties.getReconciliationTimeout().toString() + " * * * ?");
        cronTriggerFactoryBean.setCronExpression("0 " + runEveryHours + "/" + runEveryMinutes + " * * * ?");
        cronTriggerFactoryBean.setGroup("refund-status");
        return cronTriggerFactoryBean;
    }

}
