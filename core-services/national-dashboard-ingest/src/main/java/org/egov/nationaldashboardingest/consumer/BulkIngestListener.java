package org.egov.nationaldashboardingest.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.nationaldashboardingest.service.BulkFileProcessorService;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Kafka Consumer listener for processing asynchronous bulk data ingestion initialization requests.
 * <p>
 * Subscribes to the bulk ingest topic (configured via {@code bulk.ingest.topic}, default {@code bulk-ingest-init}).
 * Acts as the entry point for the asynchronous ingestion pipeline:
 * <ol>
 *   <li>Receives event notifications published when a dataset file is staged in AWS S3.</li>
 *   <li>Converts the generic map payload into a strongly-typed {@link BulkIngestInitDetail} model.</li>
 *   <li>Delegates download, row streaming, Elasticsearch indexing, and audit tracking to {@link BulkFileProcessorService}.</li>
 * </ol>
 * Catches deserialization and runtime exceptions to ensure consumer loop resilience.
 * </p>
 */
@Slf4j
@Component
public class BulkIngestListener {

    @Autowired
    private BulkFileProcessorService bulkFileProcessorService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Listens for Kafka events on the configured bulk ingest initialization topic.
     * <p>
     * Converts the received generic map payload into a {@link BulkIngestInitDetail} DTO and initiates
     * the file downloading and streaming ingestion workflow via {@link BulkFileProcessorService#processBulkFile}.
     * </p>
     *
     * @param record deserialized Kafka record payload containing file name (S3 path) and state code
     * @param key    Kafka message key (typically the S3 object key used for partition routing)
     * @param topic  name of the Kafka topic from which this record was consumed
     */
    @KafkaListener(topics = "${bulk.ingest.topic:bulk-ingest-init}", groupId = "${spring.kafka.consumer.group-id:national-dashboard-ingest}")
    public void listen(
            final HashMap<String, Object> record,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Header(value = KafkaHeaders.RECEIVED_TOPIC, required = false) String topic) {

        log.info("Received Kafka bulk ingest message on topic: {}, key: {}, payload: {}", topic, key, record);

        try {
            BulkIngestInitDetail bulkIngestInitDetail = objectMapper.convertValue(record, BulkIngestInitDetail.class);
            bulkFileProcessorService.processBulkFile(bulkIngestInitDetail);
        } catch (Exception e) {
            log.error("Failed to parse Kafka record into BulkIngestInitDetail: {}", record, e);
        }
    }
}
