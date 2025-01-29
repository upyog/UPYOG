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

//	Send notification to users on 1st april when their application has expired previous day
	@Scheduled(cron = "0 0 0 01 4 *")
	public void expirePetApplications() {
		log.info("Expire Pet Applications Scheduler running");
		ptrBatchService.processExpiredPetApplications();
	}

//	Send notifications to users 1 month before their pet application expires
	@Scheduled(cron = "0 0 0 01 3 *")
	public void sendNotificationBeforeExpiration() {

		log.info("Scheduler running to send notifications to users 1 month before expiration");
		ptrBatchService.notifyPetOwnersOfExpiringApplications();

	}

}
