package org.egov.pt.service.jobs;

import java.util.TimeZone;

import org.egov.pt.config.scheduler.DashboardDataPush;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
public class DailyDashboardDatapushConfig {

	@Bean(name="dashboardDatapushJob")
	JobDetailFactoryBean dashbordDatapush() {
		JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(DashboardDataPush.class);
		jobDetailFactory.setGroup("Dashboard-data");
		jobDetailFactory.setDurability(true);
		return jobDetailFactory;
	}

	@Bean
	@Autowired
	CronTriggerFactoryBean processDashboardDataTrigger(@Qualifier("dashboardDatapushJob") JobDetail dashboardDatapush) {
		CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(dashboardDatapush);
		//cronTriggerFactoryBean.setCronExpression("1 * * * * ?"); // runs every minute for testing
		cronTriggerFactoryBean.setCronExpression("0 55 23 * * ?");
		cronTriggerFactoryBean.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		cronTriggerFactoryBean.setGroup("Dashboard-data");
		return cronTriggerFactoryBean;
	}

}
