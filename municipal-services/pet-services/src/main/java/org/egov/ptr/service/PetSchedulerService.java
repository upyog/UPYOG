package org.egov.ptr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PetSchedulerService {

	@Autowired
	private PTRBatchService ptrBatchService;

	@Scheduled(cron = "0 0 0 01 4 *")
	public void expirePetApplications() {
		log.info("Expire Pet Applications Scheduler running");
		ptrBatchService.processPetApplicationExpiration();
	}

//	Send notifications to users 1 month before their pet application expires
	@Scheduled(cron = "0 0 0 01 3 *")
	public void sendNotificationBeforeExpiration() {

		log.info("Scheduler running to send notifications to users 1 month before expiration");
		ptrBatchService.sendAdvanceNotificationsToPetOwners();

	}

}
