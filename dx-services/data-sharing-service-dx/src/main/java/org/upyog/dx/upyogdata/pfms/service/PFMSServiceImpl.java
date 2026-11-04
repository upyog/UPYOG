package org.upyog.dx.upyogdata.pfms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.upyog.dx.upyogdata.constants.Constants;
import org.upyog.dx.upyogdata.pfms.enricher.PFMSDataEnricher;
import org.upyog.dx.upyogdata.pfms.exception.PfmsKafkaPublishException;
import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionKafkaMessage;
import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionRequest;
import org.upyog.dx.upyogdata.pfms.models.PFMSData;
import org.upyog.dx.upyogdata.pfms.models.PFMSTransaction;
import org.upyog.dx.upyogdata.pfms.models.PFMSTransactionResponse;
import org.upyog.dx.upyogdata.pfms.models.ResponseInfo;
import org.upyog.dx.upyogdata.pfms.producer.PFMSProducer;
import org.upyog.dx.upyogdata.pfms.repository.PFMSRepository;
import org.upyog.dx.upyogdata.util.DxUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Orchestrates PFMS transaction ingest: duplicate validation, API-to-entity transformation,
 * Kafka publish, and API response assembly.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PFMSServiceImpl implements PFMSService {

    private final PFMSProducer pfmsProducer;
    private final PFMSRepository pfmsRepository;
    private final PFMSDataEnricher enricher;

    /**
     * Validates the inbound contract payload, transforms it to persistence entities, publishes to Kafka,
     * and returns per-transaction ids assigned during enrichment.
     *
     * @param request validated API contract from the state system
     * @return {@link PFMSTransactionResponse} with assigned transaction ids
     * @throws IllegalArgumentException when duplicate vouchers are detected
     * @throws PfmsKafkaPublishException when Kafka publish fails
     */
    @Override
    public Object createTransaction(PFMSCreateTransactionRequest request) {
        int batchCount = request.getData() == null ? 0 : request.getData().size();
        int transactionCount = request.getData() == null ? 0 : request.getData().stream()
                .mapToInt(data -> data.getTransactions() == null ? 0 : data.getTransactions().size())
                .sum();
        log.info("PFMS ingest started | dataBatches={} transactions={}", batchCount, transactionCount);

        validateDuplicates(request);
        log.debug("PFMS duplicate validation passed | transactions={}", transactionCount);

        PFMSCreateTransactionKafkaMessage kafkaMessage = enricher.enrich(request);

        List<PFMSTransactionResponse.TransactionResult> results = kafkaMessage.getData().stream()
                .flatMap(data -> data.getTransactions().stream())
                .map(t -> PFMSTransactionResponse.TransactionResult.builder()
                        .id(t.getId())
                        .voucherNumber(t.getVoucherNumber())
                        .status(Constants.SUCCESS)
                        .build())
                .collect(Collectors.toList());

        try {
            pfmsProducer.push(kafkaMessage).get();
            log.info("PFMS ingest published to Kafka | transactions={}", results.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("PFMS Kafka publish interrupted | transactions={}", results.size(), e);
            throw new PfmsKafkaPublishException("Failed to publish PFMS data to Kafka", e);
        } catch (ExecutionException e) {
            log.error("PFMS Kafka publish failed | transactions={}", results.size(), e.getCause());
            throw new PfmsKafkaPublishException("Failed to publish PFMS data to Kafka", e.getCause());
        }

        ResponseInfo responseInfo = DxUtils.buildResponseInfo(request.getRequestInfo());
        return PFMSTransactionResponse.builder()
                .responseInfo(responseInfo)
                .transactions(results)
                .build();
    }

    /**
     * Rejects duplicate vouchers within the same request and against persisted rows
     * ({@code agencyCode} + {@code voucher_number} unique key).
     */
    private void validateDuplicates(PFMSCreateTransactionRequest request) {
        for (PFMSData data : request.getData()) {
            Set<String> voucherKeys = new HashSet<>();
            for (PFMSTransaction t : data.getTransactions()) {
                String key = t.getUlbCodeOrPFMSAgencyCode() + "|" + t.getVoucherNumber();
                if (!voucherKeys.add(key)) {
                    log.warn("Duplicate voucher in same request | agencyCode={} voucherNumber={}",
                            t.getUlbCodeOrPFMSAgencyCode(), t.getVoucherNumber());
                    throw new IllegalArgumentException(
                            "Duplicate voucher in request - ulbCode: " + t.getUlbCodeOrPFMSAgencyCode()
                                    + ", voucherNumber: " + t.getVoucherNumber());
                }
            }

            Map<String, List<String>> byAgency = data.getTransactions().stream()
                    .collect(Collectors.groupingBy(
                            PFMSTransaction::getUlbCodeOrPFMSAgencyCode,
                            Collectors.mapping(PFMSTransaction::getVoucherNumber, Collectors.toList())
                    ));

            for (Map.Entry<String, List<String>> entry : byAgency.entrySet()) {
                List<String> duplicates = pfmsRepository.findExistingVoucherNumbers(
                        entry.getValue(), entry.getKey());
                if (!duplicates.isEmpty()) {
                    log.warn("Duplicate voucher already persisted | agencyCode={} voucherNumbers={}",
                            entry.getKey(), duplicates);
                    throw new IllegalArgumentException(
                            "Duplicate vouchers for ulbCode " + entry.getKey() + ": "
                                    + String.join(", ", duplicates));
                }
            }
        }
    }
}
