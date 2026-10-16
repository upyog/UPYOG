package org.upyog.dx.upyogdata.pfms.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.upyog.dx.upyogdata.config.KafkaConfig;
import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionKafkaMessage;

import java.util.concurrent.CompletableFuture;

/**
 * Publishes enriched PFMS persistence payloads to the egov-persister Kafka topic.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PFMSProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaConfig kafkaConfig;

    /**
     * Sends a {@link PFMSCreateTransactionKafkaMessage} to {@code upyog.data.kafka.topic}.
     *
     * @param message enriched persistence payload (not the raw API contract)
     * @return Kafka send future; callers should {@code .get()} to surface broker errors
     */
    public CompletableFuture<SendResult<String, Object>> push(PFMSCreateTransactionKafkaMessage message) {
        int batchCount = message.getData() == null ? 0 : message.getData().size();
        log.info("Publishing PFMS message to Kafka | topic={} dataBatches={}", kafkaConfig.getTopic(), batchCount);
        return kafkaTemplate.send(kafkaConfig.getTopic(), message)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("PFMS Kafka send failed | topic={}", kafkaConfig.getTopic(), error);
                        return;
                    }
                    if (result != null && result.getRecordMetadata() != null) {
                        log.debug("PFMS Kafka send acknowledged | topic={} partition={} offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
