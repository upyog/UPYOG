package org.upyog.dashboard.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.service.DailyIngestionService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring-managed scheduler component that automates the daily ingestion of
 * dashboard metrics across all enabled modules.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DailyIngestionScheduler {

	private final DailyIngestionService ingestionService;

	@Value("${daily.ingestion.cron:0 0 2 * * ?}")
	private String dailyCron;

	/**
	 * Scheduled method triggered by the cron expression defined in
	 * {@code daily.ingestion.cron}. Invokes {@link DailyIngestionService#executeScheduledIngestion(String)}
	 * to perform metrics extraction and ingestion for all enabled modules for the previous day.
	 */
	@Scheduled(cron = "${daily.ingestion.cron}")
	@SchedulerLock(name = "daily_dashboard_ingestion_lock", lockAtMostFor = "PT2H", lockAtLeastFor = "PT5M")
	public void executeDailyPTIngestion() {
		log.info("Daily Ingestion Scheduler triggered with cron: {}", dailyCron);
		ingestionService.executeScheduledIngestion(dailyCron);
	}
}
