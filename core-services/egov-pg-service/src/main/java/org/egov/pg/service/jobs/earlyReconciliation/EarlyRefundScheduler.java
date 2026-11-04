package org.egov.pg.service.jobs.earlyReconciliation;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pg.config.AppProperties;
import org.egov.pg.constants.PgConstants;
import org.egov.pg.models.Refund;
import org.egov.pg.models.Refund.RefundStatusEnum;
import org.egov.pg.repository.RefundRepository;
import org.egov.pg.service.RefundService;
import org.egov.pg.web.models.RefundCriteria;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EarlyRefundScheduler implements Job {

	private static RequestInfo requestInfo;

	@Autowired
	private AppProperties appProperties;
	@Autowired
	private RefundRepository refundRepository;
	@Autowired
	private RefundService refundService;

	@PostConstruct
	public void init() {
		User userInfo = User.builder().uuid(appProperties.getEgovPgReconciliationSystemUserUuid()).type("SYSTEM")
				.roles(Collections.emptyList()).id(0L).build();

		requestInfo = new RequestInfo();
		requestInfo.setUserInfo(userInfo);
		System.out.println("EarlyRefundSchedulerConfig loaded");
	}

	@Override
	public void execute(JobExecutionContext context) {

		int intervalMinutes = appProperties.getEarlyReconcileJobRunIntervalRefund() * 60000;

		if (intervalMinutes <= 0) {
			throw new IllegalArgumentException("pg.earlyReconcileJobRunIntervalRefund.mins must be greater than 0");
		}

		Instant now = Instant.now();
		Instant lowerBound = now.minus(Duration.ofMinutes(intervalMinutes));

		RefundCriteria criteria = RefundCriteria.builder()
				.statuses(Arrays.asList(RefundStatusEnum.PENDING, RefundStatusEnum.INITIATED)).build();

		List<Refund> pendingRefundTxns = refundRepository.fetchRefundTransactionsByTimeRange(criteria,
				lowerBound.toEpochMilli(), now.toEpochMilli());

		log.info("Fetched {} refund transactions for last {} minutes", pendingRefundTxns.size(), intervalMinutes);

		for (Refund refund : pendingRefundTxns) {
			log.info(
					refundService
							.updateRefundTransaction(requestInfo,
									Collections.singletonMap(PgConstants.PG_REFUND_TXN_IN_LABEL, refund.getRefundId()))
							.toString());
		}
	}
}
