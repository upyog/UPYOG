package org.upyog.extractor.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.extractor.service.DailyIngestionService;


@Component
public class DailyIngestionScheduler {

	@Autowired
	private DailyIngestionService ingestionService;

	/**
	 * Triggers every night at 1:00 AM (0 0 1 * * ?). Adjust the cron expression as
	 * needed for your pipeline schedules.
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void executeDailyPTIngestion() {
		System.out.println("Daily Ingestion Scheduler triggered...");
		ingestionService.ingestDailyPTData();
	}
}