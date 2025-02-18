package org.egov.schedulerservice.call;

import org.egov.common.contract.request.RequestInfo;
import org.egov.schedulerservice.service.BillService;
import org.egov.schedulerservice.service.GarbageService;
import org.egov.schedulerservice.service.PGRService;
import org.egov.schedulerservice.service.PropertyService;
import org.egov.schedulerservice.util.RequestInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Scheduler {

	@Autowired
	private RequestInfoUtils requestInfoUtils;

	@Autowired
	private GarbageService garbageService;

	@Autowired
	private BillService billService;

	@Autowired
	private PropertyService propertyService;

	@Autowired
	private PGRService pgrService;

	@Scheduled(cron = "${cron.job.default.garbage.bill.generator}", zone = "IST")
	public void generateGarbageBills() {
		log.info("generateGarbageBills CRON JOB Starts");
		RequestInfo requestInfo = requestInfoUtils.getSystemRequestInfo();
		garbageService.generateGarbageBills(requestInfo);
		log.info("generateGarbageBills CRON JOB Ends");
	}

	@Scheduled(cron = "${cron.job.default.expire.bill}", zone = "IST")
	public void expireEligibleBill() {
		log.info("expireEligibleBill CRON JOB Starts");
		RequestInfo requestInfo = requestInfoUtils.getSystemRequestInfo();
		billService.expireEligibleBill(requestInfo);
		log.info("expireEligibleBill CRON JOB Ends");
	}

	@Scheduled(cron = "${cron.job.default.property.tax.generator}", zone = "IST")
	public void generatePropertyTax() {
		log.info("generatePropertyTax CRON JOB Starts");
		RequestInfo requestInfo = requestInfoUtils.getSystemRequestInfo();
		propertyService.generatePropertyTax(requestInfo);
		log.info("generatePropertyTax CRON JOB Ends");
	}

	@Scheduled(cron = "${cron.job.default.pgr.request.escalator}", zone = "IST")
	public void escalatePGRRequest() {
		log.info("escalatePGRRequest CRON JOB Starts");
		RequestInfo requestInfo = requestInfoUtils.getSystemRequestInfo();
		pgrService.escalatePGRRequest(requestInfo);
		log.info("escalatePGRRequest CRON JOB Ends");
	}

	@Scheduled(cron = "${cron.job.default.pgr.notification.sender}", zone = "IST")
	public void pgrNotificationSender() {
		log.info("pgrNotificationSender CRON JOB Starts");
		RequestInfo requestInfo = requestInfoUtils.getSystemRequestInfo();
		pgrService.sendPgrNotification(requestInfo);
		log.info("pgrNotificationSender CRON JOB Ends");
	}

	@Scheduled(cron = "${cron.job.default.pgr.delete.notification}", zone = "IST")
	public void deletePgrNotification() {
		log.info("deletePgrNotification CRON JOB Starts");
		RequestInfo requestInfo = requestInfoUtils.getSystemRequestInfo();
		pgrService.deletePgrNotification(requestInfo);
		log.info("deletePgrNotification CRON JOB Ends");
	}

}
