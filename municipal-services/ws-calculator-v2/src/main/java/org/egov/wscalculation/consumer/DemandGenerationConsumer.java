package org.egov.wscalculation.consumer;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.egov.wscalculation.config.WSCalculationConfiguration;
import org.egov.wscalculation.validator.WSCalculationWorkflowValidator;
import org.egov.wscalculation.web.models.*;
import org.egov.wscalculation.web.models.CalculationReq;
import org.egov.wscalculation.producer.WSCalculationProducer;
import org.egov.wscalculation.service.BulkDemandAndBillGenService;
import org.egov.wscalculation.service.MasterDataService;
import org.egov.wscalculation.service.WSCalculationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DemandGenerationConsumer {

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private MasterDataService mstrDataService;

	@Autowired
	private BulkDemandAndBillGenService bulkDemandAndBillGenService;

	@Autowired
	private WSCalculationProducer producer;
	
	@Autowired
	private WSCalculationConfiguration config;
	@Autowired
	private WSCalculationServiceImpl wSCalculationServiceImpl;
	@Value("${kafka.topics.bulk.bill.generation.audit}")
	private String bulkBillGenAuditTopic;

	@Value("${persister.demand.based.dead.letter.error.topic}")
	private String demandGenerationErrorTopic;

	@Autowired
	private WSCalculationWorkflowValidator wsCalulationWorkflowValidator;
	/**
	 * Listen the topic for processing the batch records.
	 * 
	 * @param records
	 *            would be calculation criteria.
	 */
	/*
	 * Temp Fix for demand generation
	 * @KafkaListener(topics = { "${egov.watercalculatorservice.createdemand.topic}"
	 * }) public void processMessage(Map<String, Object>
	 * consumerRecord, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) { try{
	 * CalculationReq calculationReq = mapper.convertValue(consumerRecord,
	 * CalculationReq.class); generateDemandInBatch(calculationReq); }catch (final
	 * Exception e){ log.error("KAFKA_PROCESS_ERROR", e); } }
	 */
	@KafkaListener(
		    topics = {"${egov.watercalculatorservice.createdemand.topic}","${egov.watercalculatorservice.createsingledemand.topic}"},
		    containerFactory = "kafkaListenerContainerFactoryBatch",
		    concurrency = "${egov.watercalculatorservice.listener.concurrency}"
		)
		public void listen(final List<ConsumerRecord<String, Object>> records) {
		    log.info("\uD83D\uDCE6 Batch received: {} record(s)", records.size());
		    for (ConsumerRecord<String, Object> record : records) {
		        try {
		            log.info("\uD83D\uDD38 Key={}, Partition={}, Offset={}",
		                    record.key(), record.partition(), record.offset());

		            // ── Step 1: Deserialize — skip bad record, don't fail the batch ──
		            CalculationReq calculationReq;
		            try {
		                calculationReq = mapper.convertValue(record.value(), CalculationReq.class);
		            } catch (Exception e) {
		                log.error("\u274C Deserialization failed at offset={} — skipping record. Error: {}",
		                        record.offset(), e.getMessage(), e);
		                continue;
		            }

		            // ── Step 2: Null / empty guard ──────────────────────────────
		            if (calculationReq == null
		                    || calculationReq.getCalculationCriteria() == null
		                    || calculationReq.getCalculationCriteria().isEmpty()) {
		                log.error("\u274C Null or empty CalculationReq at offset={} — skipping.", record.offset());
		                continue;
		            }

		            // ── Step 3: Load master data — skip record on failure ──────────
		            String tenantId = calculationReq.getCalculationCriteria().get(0).getTenantId();
		            Map<String, Object> masterMap;
		            try {
		                masterMap = mstrDataService.loadMasterData(
		                        calculationReq.getRequestInfo(), tenantId);
		            } catch (Exception e) {
		                log.error("\u274C Failed to load masterData for tenant: {} at offset={} — skipping record. Error: {}",
		                        tenantId, record.offset(), e.getMessage(), e);
		                continue;
		            }

		            // ── Step 4: Generate demand (has its own internal try-catch) ───
		            generateDemandInBatch(calculationReq, masterMap, config.getDeadLetterTopicBatch());

		            log.info("\u2705 Processed tenant={} | criteriaCount={}",
		                    tenantId, calculationReq.getCalculationCriteria().size());

		        } catch (Exception e) {
		            // Last-resort catch — ensures the rest of the batch is always processed
		            log.error("\u274C Unexpected error processing record at offset={}: {}",
		                    record.offset(), e.getMessage(), e);
		        }
		    }
		}

	

	/**
	 * Generate demand in bulk on given criteria
	 * 
	 * @param request
	 *            Calculation request
	 * @param masterMap
	 *            master data
	 * @param errorTopic
	 *            error topic
	 */
	private void generateDemandInBatch(CalculationReq request, Map<String, Object> masterMap, String errorTopic) {
		try {
			wSCalculationServiceImpl.bulkDemandGeneration(request, masterMap);
			String connectionNoStrings = request.getCalculationCriteria().stream()
					.map(criteria -> criteria.getConnectionNo()).collect(Collectors.toSet()).toString();
			log.info("\u2705 Demand generated successfully for: {}", connectionNoStrings);
		} catch (Exception ex) {
			log.error("❌ Demand generation error: ", ex);
			// Push to dead-letter topic — wrap separately so a Kafka failure here
			// does NOT cause an infinite retry loop or crash the consumer thread.
			try {
				if (request.getMigrationCount() != null) {
					request.getMigrationCount().setMessage("Error: " + ex.getMessage());
				}
				producer.push(errorTopic, request);
			} catch (Exception pushEx) {
				log.error("❌ Failed to push to dead-letter topic '{}' (swallowing to protect consumer): {}",
						errorTopic, pushEx.getMessage(), pushEx);
			}
		}
	}

	
	
}
