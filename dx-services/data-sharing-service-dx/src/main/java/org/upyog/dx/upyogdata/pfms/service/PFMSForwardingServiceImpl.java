package org.upyog.dx.upyogdata.pfms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.upyog.dx.upyogdata.config.PFMSProperties;
import org.upyog.dx.upyogdata.constants.Constants;
import org.upyog.dx.upyogdata.pfms.client.PFMSApiClient;
import org.upyog.dx.upyogdata.pfms.enums.SchedulerType;
import org.upyog.dx.upyogdata.pfms.entity.PfmsTransactionEntity;
import org.upyog.dx.upyogdata.pfms.models.SchedulerLog;
import org.upyog.dx.upyogdata.pfms.repository.PFMSRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import feign.FeignException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Forwards stored PFMS transactions to the external PFMS APIs.
 * <p>
 * Auth and data-push HTTP is audited inside {@link PFMSApiClient}. This class only
 * decides when a 401 retry must reuse the same audit {@code correlation_id}.
 * A later scheduler cycle always allocates a new UUID (new audit row).
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PFMSForwardingServiceImpl implements PFMSForwardingService {

    private final PFMSRepository pfmsRepository;
    private final PFMSApiClient pfmsApiClient;
    private final PFMSProperties pfmsProperties;

    private String currentToken;

    @Override
    public void forwardInitiatedTransactions(String triggeredBy) {
        processBatch(pfmsRepository.fetchInitiatedTransactionIds(pfmsProperties.getScheduler().getBatchSize()),
                SchedulerType.DAILY, triggeredBy);
    }

    @Override
    public void retryFailedTransactions(String triggeredBy) {
        processBatch(pfmsRepository.fetchFailedTransactionIds(pfmsProperties.getScheduler().getBatchSize()),
                SchedulerType.RETRY, triggeredBy);
    }

    private void processBatch(List<String> ids, SchedulerType type, String triggeredBy) {
        log.info("Pfms Scheduler [{}] Starting cycle", type);

        if (ids.isEmpty()) {
            log.info("Pfms Scheduler [{}] No transactions found. Skipping.", type);
            return;
        }

        log.info("Pfms Scheduler [{}] Found {} transaction(s)", type, ids.size());
        LocalDateTime startedAt = LocalDateTime.now();
        try {
            currentToken = pfmsApiClient.fetchAccessToken();
        } catch (Exception e) {
            log.error("Pfms Scheduler [{}] Auth failed, aborting cycle", type, e);
            return;
        }

        int success = 0, failed = 0;
        for (String id : ids) {
            try {
                PfmsTransactionEntity transaction = pfmsRepository.fetchTransactionById(id);
                pfmsRepository.updateTransactionStatus(id, Constants.PROCESSING, null);
                log.info("Pfms [{}] Processing | id={} voucherNumber={}", type, id, transaction.getVoucherNumber());

                String pfmsResponse = pushWithTokenRefreshOnExpiry(transaction);

                pfmsRepository.updateTransactionStatus(id, pfmsResponse.trim(), LocalDateTime.now());
                log.info("Pfms [{}] Success | id={} response={}", type, id, pfmsResponse);
                success++;

            } catch (Exception e) {
                log.error("Pfms[{}] Failed | id={}", type, id, e);
                pfmsRepository.updateTransactionStatus(id, Constants.FAILED, LocalDateTime.now());
                failed++;
            }
        }

        LocalDateTime endedAt = LocalDateTime.now();
        String cycleStatus = (failed == 0) ? Constants.COMPLETED : (success == 0) ? Constants.ALL_FAILED : Constants.PARTIAL;

        log.info("Pfms Scheduler [{}] Cycle complete | total={} success={} failed={} status={}",
                type, ids.size(), success, failed, cycleStatus);

        pfmsRepository.saveSchedulerLog(SchedulerLog.builder()
                .schedulerType(type.name())
                .startedAt(startedAt)
                .endedAt(endedAt)
                .durationMs(ChronoUnit.MILLIS.between(startedAt, endedAt))
                .status(cycleStatus)
                .totalPicked(ids.size())
                .successCount(success)
                .failedCount(failed)
                .createdBy(triggeredBy)
                .build());
    }

    /**
     * First push uses retryCount 0. On 401 the same {@code auditCorrelationId} is reused with retryCount 1
     * so persister updates one {@code pfms-data-push} row instead of inserting a second.
     */
    private String pushWithTokenRefreshOnExpiry(PfmsTransactionEntity transaction) {
        String auditCorrelationId = UUID.randomUUID().toString();
        try {
            return pfmsApiClient.pushTransaction(transaction, currentToken, auditCorrelationId, 0);
        } catch (Exception e) {
            if (!isUnauthorized(e)) {
                throw e;
            }
            log.warn("Pfms Token expired (401), refreshing | voucherNumber={}", transaction.getVoucherNumber());
            currentToken = pfmsApiClient.fetchAccessToken();
            return pfmsApiClient.pushTransaction(transaction, currentToken, auditCorrelationId, 1);
        }
    }

    private boolean isUnauthorized(Exception exception) {
        if (exception instanceof HttpClientErrorException.Unauthorized) {
            return true;
        }
        return exception instanceof FeignException feignException && feignException.status() == 401;
    }
}
