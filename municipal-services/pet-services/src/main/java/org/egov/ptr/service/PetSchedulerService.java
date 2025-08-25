package org.egov.ptr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Slf4j
@Service
public class PetSchedulerService {

	@Autowired
	private PTRBatchService ptrBatchService;

	/**
	 * Send notification to users on 1st april when their application has expired previous day
	 * Uses ShedLock to ensure only one instance runs this job across multiple service instances
	 */
	@Scheduled(cron = "0 0 0 01 4 *")
	@SchedulerLock(
		name = "ptrExpirePetApplicationsJob",
		lockAtLeastFor = "PT1M",  // Hold the lock for at least 1 minute
		lockAtMostFor = "PT30M"   // Auto-release after 30 minutes if job crashes
	)
	public void expirePetApplications() {
		log.info("Expire Pet Applications Scheduler running");
		ptrBatchService.processExpiredPetApplications();
	}

	/**
	 * Send notifications to users 1 month before their pet application expires
	 * Uses ShedLock to ensure only one instance runs this job across multiple service instances
	 */
	@Scheduled(cron = "0 0 0 01 3 *")
	@SchedulerLock(
		name = "ptrNotificationBeforeExpirationJob",
		lockAtLeastFor = "PT1M",  // Hold the lock for at least 1 minute
		lockAtMostFor = "PT30M"   // Auto-release after 30 minutes if job crashes
	)
	public void sendNotificationBeforeExpiration() {
		log.info("Scheduler running to send notifications to users 1 month before expiration");
		ptrBatchService.notifyPetOwnersOfExpiringApplications();
	}

}
