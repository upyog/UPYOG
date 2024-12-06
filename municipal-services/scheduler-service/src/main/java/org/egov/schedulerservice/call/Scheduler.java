package org.egov.schedulerservice.call;

import org.egov.common.contract.request.RequestInfo;
import org.egov.schedulerservice.service.GarbageService;
import org.egov.schedulerservice.util.RequestInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Scheduler {

	@Autowired
	private RequestInfoUtils requestInfoUtils;

	@Autowired
	private GarbageService garbageService;

//	@Scheduled(cron = "${cron.job.default.garbage.bill.generator}", zone = "IST")
	public void generateGarbageBills() {
		log.info("generateGarbageBills CRON JOB Starts");
		RequestInfo requestInfo = requestInfoUtils.getSystemRequestInfo();
		garbageService.generateGarbageBills(requestInfo);
		log.info("generateGarbageBills CRON JOB Ends");
	}

}
