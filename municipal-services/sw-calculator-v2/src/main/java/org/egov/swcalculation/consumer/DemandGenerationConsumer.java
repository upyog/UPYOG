package org.egov.swcalculation.consumer;

import java.util.*;
import java.util.stream.Collectors;

import org.egov.swcalculation.validator.SWCalculationWorkflowValidator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.egov.swcalculation.config.SWCalculationConfiguration;
import org.egov.swcalculation.web.models.CalculationCriteria;
import org.egov.swcalculation.web.models.CalculationReq;
import org.egov.swcalculation.producer.SWCalculationProducer;
import org.egov.swcalculation.service.BulkDemandAndBillGenService;
import org.egov.swcalculation.service.MasterDataService;
import org.egov.swcalculation.service.SWCalculationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DemandGenerationConsumer {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private BulkDemandAndBillGenService bulkDemandAndBillGenService;

	@Autowired
	private SWCalculationProducer producer;

	@Autowired
	private MasterDataService mDataService;

	@Autowired
	private SWCalculationWorkflowValidator swCalculationWorkflowValidator;
	
	@Autowired
	private SWCalculationConfiguration config;
	
	@Autowired
	private SWCalculationServiceImpl sWCalculationServiceImpl;
	
	@Value("${kafka.topics.bulk.bill.generation.audit}")
	private String bulkBillGenAuditTopic;
	
	/**
	 * Listen the topic for processing the batch records.
	 * 
	 * @param consumerRecord would be calculation criteria.
	 */
	// @KafkaListener(topics = { "${egov.seweragecalculatorservice.createdemand.topic}" })
	// public void processMessage(Map<String, Object> consumerRecord, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
	// 	try{
	// 		CalculationReq calculationReq = mapper.convertValue(consumerRecord, CalculationReq.class);
	// 		log.info(" Bulk bill Consumerbatch records log for batch :  "
	// 				+ calculationReq.getMigrationCount().getOffset() + " Count is : "
	// 				+ calculationReq.getMigrationCount().getLimit());
	// 		generateDemandInBatch(calculationReq);
	// 	}catch (final Exception e){
	// 		log.error("KAFKA_PROCESS_ERROR", e);
	// 	}
	// }
	
	/**
	 * Listen the topic for processing the batch records.
	 * 
	 * @param records
	 *            would be calculation criteria.
	 */
	@KafkaListener(
		    topics = {"${egov.seweragecalculatorservice.createdemand.topic}"},
		    containerFactory = "kafkaListenerContainerFactoryBatch",
		    concurrency = "${egov.sw.calculator.concurrency.count}"
		)
		public void listen(final List<ConsumerRecord<String, Object>> records) {
			log.info("📦 Batch received: {} sewerage record(s)", records.size());
			for (ConsumerRecord<String, Object> record : records) {
				try {
					log.info("🔹 Key={}, Partition={}, Offset={}",
							record.key(), record.partition(), record.offset());

					// ── Step 1: Deserialize — skip bad record, don't fail the batch ──
					CalculationReq calculationReq;
					try {
						calculationReq = mapper.convertValue(record.value(), CalculationReq.class);
					} catch (Exception e) {
						log.error("❌ Deserialization failed at offset={} — skipping record. Error: {}",
								record.offset(), e.getMessage(), e);
						continue;
					}

					// ── Step 2: Null / empty guard ──────────────────────────────
					if (calculationReq == null
							|| calculationReq.getCalculationCriteria() == null
							|| calculationReq.getCalculationCriteria().isEmpty()) {
						log.error("❌ Null or empty CalculationReq at offset={} — skipping.", record.offset());
						continue;
					}

					// ── Step 3: Load master data — skip record on failure ──────────
					String tenantId = calculationReq.getCalculationCriteria().get(0).getTenantId();
					Map<String, Object> masterMap;
					try {
						masterMap = mDataService.loadMasterData(
								calculationReq.getRequestInfo(), tenantId);
					} catch (Exception e) {
						log.error("❌ Failed to load masterData for tenant: {} at offset={} — skipping record. Error: {}",
								tenantId, record.offset(), e.getMessage(), e);
						continue;
					}

					// ── Step 4: Generate demand (has its own internal try-catch) ───
					generateDemandInBatch(calculationReq, masterMap, config.getDeadLetterTopicBatch());

					log.info("✅ Processed tenant={} | criteriaCount={}",
							tenantId, calculationReq.getCalculationCriteria().size());

				} catch (Exception e) {
					// Last-resort catch — ensures the rest of the batch is always processed
					log.error("❌ Unexpected error processing record at offset={}: {}",
							record.offset(), e.getMessage(), e);
				}
			}
		}

	/**
	 * Listens on the dead letter topic of the bulk request and processes every
	 * record individually and pushes failed records on error topic
	 * 
	 * @param records
	 *            failed batch processing
	 */
	// @KafkaListener(topics = {
	// 		"${persister.demand.based.dead.letter.topic.batch}" }, containerFactory = "kafkaListenerContainerFactory",
	// 				concurrency = "${egov.sw.calculator.concurrency.count}")
	public void listenDeadLetterTopic(final List<Message<?>> records) {
		CalculationReq calculationReq = mapper.convertValue(records.get(0).getPayload(), CalculationReq.class);
		Map<String, Object> masterMap = mDataService.loadMasterData(calculationReq.getRequestInfo(),
				calculationReq.getCalculationCriteria().get(0).getTenantId());
		records.forEach(record -> {
			log.info("Consuming record on dead letter topic : " + record);
			try {
				CalculationReq calcReq = mapper.convertValue(record.getPayload(), CalculationReq.class);
				
				calcReq.getCalculationCriteria().forEach(calcCriteria -> {
					CalculationReq request = CalculationReq.builder().calculationCriteria(Arrays.asList(calcCriteria))
							.requestInfo(calculationReq.getRequestInfo()).isconnectionCalculation(true).build();
					try {
						log.info("Generating Demand for Criteria : " + calcCriteria);
						// processing single
						generateDemandInBatch(request, masterMap, config.getDeadLetterTopicSingle());
					} catch (final Exception e) {
						StringBuilder builder = new StringBuilder();
						builder.append("Error while generating Demand for Criteria: ").append(calcCriteria);
						log.error(builder.toString(), e);
					}
				});
			} catch (final Exception e) {
				StringBuilder builder = new StringBuilder();
				builder.append("Error while listening to value: ").append(record).append(" on dead letter topic.");
				log.error(builder.toString(), e);
			}
		});
	}

	
	/**
	 * Generate demand in bulk on given criteria
	 * 
	 * @param request
	 *            Calculation request
	 * @param masterMap 
	 */
	private void generateDemandInBatch(CalculationReq request, Map<String, Object> masterMap, String errorTopic) {
		try {
			sWCalculationServiceImpl.bulkDemandGeneration(request, masterMap);
			String connectionNoStrings = request.getCalculationCriteria().stream()
					.map(criteria -> criteria.getConnectionNo()).collect(Collectors.toSet()).toString();
			log.info("✅ Sewerage Demand generated successfully for: {}", connectionNoStrings);
		} catch (Exception ex) {
			log.error("❌ Sewerage Demand generation error: ", ex);
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
