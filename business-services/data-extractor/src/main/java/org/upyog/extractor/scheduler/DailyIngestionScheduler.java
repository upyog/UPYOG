package org.upyog.extractor.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.extractor.service.DailyIngestionService;

/**
 * Spring-managed scheduler component that automates the daily ingestion of 
 * Property Tax (PT) dashboard metrics.
 * 
 * <p>This scheduler acts as the orchestrator trigger for raw database queries,
 * transforming the results through the adapter client pipeline, and pushing the
 * finalized payloads to the national dashboard endpoint.
 */
/**
 * Class representing the DailyIngestionScheduler class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Component
public class DailyIngestionScheduler {

	@Autowired
	private DailyIngestionService ingestionService;

	/**
	 * Automatically invoked by Spring Task Scheduler on a daily schedule.
	 * 
	 * <p>The trigger time is configurable via the properties file using the key
	 * {@code daily.ingestion.cron} (typically set to run daily at 1:00 AM).
	 * Delays or failures are handled within the service level, logging execution
	 * results.
	 */
	@Scheduled(cron = "${daily.ingestion.cron}")
	public void executeDailyPTIngestion() {
		System.out.println("Daily Ingestion Scheduler triggered...");
		ingestionService.ingestDailyPTData();
	}
}