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
		    topics = "${egov.watercalculatorservice.createdemand.topic}",
		    containerFactory = "kafkaListenerContainerFactoryBatch",
		    concurrency = "${egov.watercalculatorservice.listener.concurrency}"
		)
		public void listen(final List<ConsumerRecord<String, Object>> records) {
		    log.info("📦 Number of batch records received: {}", records.size());
		    for (ConsumerRecord<String, Object> record : records) {
		        try {
		            log.info("🔸 Key={}, Partition={}, Offset={}", record.key(), record.partition(), record.offset());

		            CalculationReq calculationReq = mapper.convertValue(record.value(), CalculationReq.class);
		            Map<String, Object> masterMap = mstrDataService.loadMasterData(
		                calculationReq.getRequestInfo(),
		                calculationReq.getCalculationCriteria().get(0).getTenantId()
		            );

		            generateDemandInBatch(calculationReq, masterMap, config.getDeadLetterTopicBatch());

		            log.info("✅ Processed tenant={} | criteriaCount={}",
		                    calculationReq.getCalculationCriteria().get(0).getTenantId(),
		                    calculationReq.getCalculationCriteria().size());
		        } catch (Exception e) {
		            log.error("❌ Error processing record: {}", record.value(), e);
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
			StringBuilder str = new StringBuilder("Demand generated Successfully. For records : ")
					.append(connectionNoStrings);
			log.info(str.toString());
		} catch (Exception ex) {
			log.error("Demand generation error: ", ex);
			producer.push(errorTopic, request);
		}

	}

	
	
}
