package org.egov.infra.indexer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.egov.infra.indexer.service.IndexerService;
import org.egov.infra.indexer.util.DLQHandler;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CoreIndexMessageListener implements MessageListener<String, String> {

	private static final String CORRELATION_ID_MDC_STRING = "CORRELATION_ID";
	private static final String TENANTID_MDC_STRING = "TENANTID";

	@Autowired
	private IndexerService indexerService;

	@Autowired
	private DLQHandler dlqHandler;

	@Value("${egov.statelevel.tenantId}")
	private String stateLevelTenantId;

	@Override
	/**
	 * Messages listener which acts as consumer. This message listener is injected
	 * inside a kafkaContainer. This consumer is a start point to the following
	 * index jobs: 1. Re-index 2. Legacy Index 3. PGR custom index 4. PT custom
	 * index 5. Core indexing
	 */
	public void onMessage(ConsumerRecord<String, String> data) {
		log.info("Topic from CoreIndexMessageListener: " + data.topic());
		
		// Extract correlation ID from message body
		String correlationId = extractCorrelationId(data.value());
		if (correlationId != null) {
			MDC.put(CORRELATION_ID_MDC_STRING, correlationId);
		}
		
		// Adding in MDC so that tracer can add it in header
		MDC.put(TENANTID_MDC_STRING, stateLevelTenantId);
		
		try {
			indexerService.esIndexer(data.topic(), data.value());
		} catch (Exception e) {
			dlqHandler.handleError(data.value(), e, "CoreIndexMessageListener", data.topic());
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
