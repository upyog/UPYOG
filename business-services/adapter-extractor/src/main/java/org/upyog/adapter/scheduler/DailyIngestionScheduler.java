package org.upyog.adapter.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.adapter.service.DailyIngestionService;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring-managed scheduler component that automates the daily ingestion of
 * Property Tax (PT) dashboard metrics.
 * 
 * <p>
 * This scheduler acts as the orchestrator trigger for raw database queries,
 * transforming the results through the adapter client pipeline, and pushing the
 * finalized payloads to the national dashboard endpoint.
 */
@Component
@Slf4j
public class DailyIngestionScheduler {

	@Autowired
	private DailyIngestionService ingestionService;

	/**
	 * Automatically invoked by Spring Task Scheduler on a daily schedule.
	 * 
	 * <p>
	 * The trigger time is configurable via the properties file using the key
	 * {@code daily.ingestion.cron} (typically set to run daily at 1:00 AM). Delays
	 * or failures are handled within the service level, logging execution results.
	 */
	@Scheduled(cron = "${daily.ingestion.cron}")
	public void executeDailyPTIngestion() {
		log.info("Daily Ingestion Scheduler triggered...");
		ingestionService.ingestDailyData();
	}
}
