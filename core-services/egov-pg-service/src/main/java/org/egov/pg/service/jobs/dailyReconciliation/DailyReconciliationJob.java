package org.egov.pg.service.jobs.dailyReconciliation;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pg.config.AppProperties;
import org.egov.pg.constants.PgConstants;
import org.egov.pg.models.Transaction;
import org.egov.pg.repository.TransactionRepository;
import org.egov.pg.service.TransactionService;
import org.egov.pg.web.models.TransactionCriteria;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Daily Reconciliation of pending transactions
 */
@Component
@Slf4j
public class DailyReconciliationJob implements Job {

    private static RequestInfo requestInfo;

    @PostConstruct
    public void init() {
        User userInfo = User.builder()
                .uuid(appProperties.getEgovPgReconciliationSystemUserUuid())
                .type("SYSTEM")
                .roles(Collections.emptyList()).id(0L).build();

        requestInfo = new RequestInfo();
        requestInfo.setUserInfo(userInfo);
    //("", "", 0L, "", "", "", "", "", "", userInfo);
    }

    @Autowired
    private AppProperties appProperties;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Fetch live status for all pending transactions
     * except for ones which were created < 30 minutes ago, configurable value
     *
     * @param jobExecutionContext execution context with optional job parameters
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        List<Transaction> pendingTxns = transactionRepository.fetchTransactionsByTimeRange(TransactionCriteria.builder()
                        .txnStatus(Transaction.TxnStatusEnum.PENDING).build(), 0L,
                System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(appProperties.getEarlyReconcileJobRunInterval
                        () * 2));

        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now();
        LocalDateTime startTimeLocal;
        if (LocalTime.now().isBefore(LocalTime.of(12, 0))) {
            // morning job
        	startTimeLocal = today.minusDays(1).atTime(23, 0); // 11:00 PM yesterday
        } else {
            // evening job
        	startTimeLocal = today.atTime(11, 0);              // 11:00 AM today
        }

        long startTime = startTimeLocal.atZone(zone).toInstant().toEpochMilli();
        
        
        List<Transaction> failureTxns = transactionRepository.fetchTransactionsByTimeRange(TransactionCriteria.builder()
                .txnStatus(Transaction.TxnStatusEnum.FAILURE).build(), startTime,
        System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(appProperties.getEarlyReconcileJobRunInterval
                () * 2));
        
        log.info("Attempting to reconcile {} pending and {} failure transactions", pendingTxns.size(),failureTxns.size());

        for (Transaction txn : pendingTxns) {
            log.info(transactionService.updateTransaction(requestInfo, Collections.singletonMap(PgConstants.PG_TXN_IN_LABEL, txn
                    .getTxnId
                    ())).toString());
        }
        
        for (Transaction ftxn : failureTxns) {
            log.info(transactionService.updateTransaction(requestInfo, Collections.singletonMap(PgConstants.PG_TXN_IN_LABEL, ftxn
                    .getTxnId
                    ())).toString());
        }

    }
}
