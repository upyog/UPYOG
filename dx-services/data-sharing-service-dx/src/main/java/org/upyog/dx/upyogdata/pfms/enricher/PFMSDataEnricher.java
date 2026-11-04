package org.upyog.dx.upyogdata.pfms.enricher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionKafkaMessage;
import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionRequest;
import org.upyog.dx.upyogdata.pfms.service.PFMSTransactionTransformer;

/**
 * Converts validated API contract requests into Kafka-ready persistence entities.
 * Does not validate business rules or publish to Kafka.
 */
@Component
@RequiredArgsConstructor
public class PFMSDataEnricher {

    private final PFMSTransactionTransformer transformer;

    /**
     * Maps {@link PFMSCreateTransactionRequest} to {@link PFMSCreateTransactionKafkaMessage},
     * assigning ids, {@code INITIATED} status, and {@code additionalDetails} for JSONB storage.
     *
     * @param request validated inbound API payload
     * @return message consumed by egov-persister
     */
    public PFMSCreateTransactionKafkaMessage enrich(PFMSCreateTransactionRequest request) {
        return transformer.toKafkaMessage(request);
    }
}
