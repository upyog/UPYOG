package org.egov.infra.indexer.util;

import org.egov.infra.indexer.service.IndexerException;
import org.egov.tracer.kafka.ErrorQueueProducer;
import org.egov.tracer.model.ErrorQueueContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class DLQHandler {

    @Autowired
    private ErrorQueueProducer errorQueueProducer;

    @Value("${tracer.errorsTopic}")
    private String errorTopic;

    @Value("${tracer.errorsPublish}")
    private boolean publishErrors;

    /**
     * Complete error handling logic matching CoreIndexMessageListener pattern
     * @param messageBody The original kafka message
     * @param exception The exception that occurred
     * @param source The source/listener name
     * @param topic The kafka topic name
     */
    public void handleError(String messageBody, Exception exception, String source, String topic) {
        log.error("error while processing in {}: ", source, exception);
        
        // Send directly to DLQ using tracer's ErrorQueueProducer
        if (publishErrors) {
            try {
                // Extract actual index names from IndexerException if available
                String targetIndexNames = "unknown";
                if (exception instanceof IndexerException) {
                    IndexerException indexerEx = (IndexerException) exception;
                    targetIndexNames = indexerEx.getTargetIndexNames();
                }
                
                String enhancedSource = source + " -> " + targetIndexNames;
                String correlationId = extractCorrelationId(messageBody);
                
                log.info("DLQ Debug - Topic: {}, TargetIndexes: {}, Source: {}", topic, targetIndexNames, enhancedSource);
                sendToDLQ(messageBody, exception, correlationId, enhancedSource);
                log.info("Successfully sent failed message to DLQ topic: {} for indexes: {}", errorTopic, targetIndexNames);
                // Don't re-throw - message has been handled and sent to DLQ
                return;
            } catch (Exception dlqException) {
                log.error("Failed to send message to DLQ: ", dlqException);
                // Fall back to re-throwing if DLQ fails
            }
        }
        
        throw new RuntimeException("Failed to process message in " + source + " - routing to DLQ", exception);
    }

    /**
     * Send failed message to DLQ using tracer's ErrorQueueProducer
     */
    private void sendToDLQ(String messageBody, Exception exception, String correlationId, String enhancedSource) {
        try {
            // Create ErrorQueueContract using the correct field names from tracer library
            StackTraceElement[] elements = exception.getStackTrace();
            
            ErrorQueueContract errorContract = ErrorQueueContract.builder()
                    .id(UUID.randomUUID().toString())
                    .correlationId(correlationId)
                    .body(messageBody)
                    .source(enhancedSource)
                    .ts(new Date().getTime())
                    .exception(Arrays.asList(elements))
                    .message(exception.getMessage())
                    .build();
            
            // Send to DLQ using tracer's ErrorQueueProducer
            errorQueueProducer.sendMessage(errorContract);
        } catch (Exception e) {
            log.error("Failed to create or send ErrorQueueContract to DLQ", e);
            throw e;
        }
    }

    /**
     * Extract correlation ID from Kafka message body
     */
    private String extractCorrelationId(String messageBody) {
        try {
            // Try to extract correlation ID from RequestInfo
            return JsonPath.read(messageBody, "$.RequestInfo.correlationId");
        } catch (Exception e) {
            try {
                // Fallback: try to extract from different path
                return JsonPath.read(messageBody, "$.correlationId");
            } catch (Exception ex) {
                log.debug("Could not extract correlation ID from message: {}", ex.getMessage());
                return null;
            }
        }
    }
}