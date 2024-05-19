package org.egov.wscalculation.consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	@Autowired
	private WSCalculationWorkflowValidator wsCalulationWorkflowValidator;
	/**
	 * Listen the topic for processing the batch records.
	 * 
	 * @param records
	 *            would be calculation criteria.
	 */
	@KafkaListener(topics = { "${egov.watercalculatorservice.createdemand.topic}" })
	public void processMessage(Map<String, Object> consumerRecord, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		try{
			CalculationReq calculationReq = mapper.convertValue(consumerRecord, CalculationReq.class);
			log.info(" Bulk bill Consumerbatch records log for batch :  "
					+ calculationReq.getMigrationCount().getOffset() + "Count is : "
					+ calculationReq.getMigrationCount().getLimit());
			generateDemandInBatch(calculationReq);
		}catch (final Exception e){
			log.error("KAFKA_PROCESS_ERROR", e);
		}
	}

	/**
	 * Listens on the dead letter topic of the bulk request and processes every
	 * record individually and pushes failed records on error topic
	 * 
	 * @param records
	 *            failed batch processing
	 */
	@KafkaListener(topics = {
			"${persister.demand.based.dead.letter.topic.batch}" }, containerFactory = "kafkaListenerContainerFactory")
	public void listenDeadLetterTopic(final List<Message<?>> records) {
		CalculationReq calculationReq = mapper.convertValue(records.get(0).getPayload(), CalculationReq.class);
		Map<String, Object> masterMap = mstrDataService.loadMasterData(calculationReq.getRequestInfo(),
				calculationReq.getCalculationCriteria().get(0).getTenantId());
		records.forEach(record -> {
			try {
				log.info("Consuming record on dead letter topic : " + mapper.writeValueAsString(record));
				CalculationReq calcReq = mapper.convertValue(record.getPayload(), CalculationReq.class);

				calcReq.getCalculationCriteria().forEach(calcCriteria -> {
					CalculationReq request = CalculationReq.builder().calculationCriteria(Arrays.asList(calcCriteria))
							.requestInfo(calculationReq.getRequestInfo()).isconnectionCalculation(true)
							.taxPeriodFrom(calcCriteria.getFrom()).taxPeriodTo(calcCriteria.getTo()).build();
					try {
						log.info("Generating Demand for Criteria : " + mapper.writeValueAsString(calcCriteria));
						// processing single
						generateDemandInBatch(request, masterMap, config.getDeadLetterTopicSingle());
					} catch (final Exception e) {
						StringBuilder builder = new StringBuilder();
						try {
							builder.append("Error while generating Demand for Criteria: ")
									.append(mapper.writeValueAsString(calcCriteria));
						} catch (JsonProcessingException e1) {
							e1.printStackTrace();
						}
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
	 *            master data
	 * @param errorTopic
	 *            error topic
	 */
	private void generateDemandInBatch(CalculationReq request, Map<String, Object> masterMap, String errorTopic) {
		try {
//			for(CalculationCriteria criteria : request.getCalculationCriteria()){
//				Boolean genratedemand = true;
////				Application validation removing for migrated connection as of now 
////				wsCalulationWorkflowValidator.applicationValidation(request.getRequestInfo(),criteria.getTenantId(),criteria.getConnectionNo(),genratedemand);
//			}
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

	/**
	 * Generate demand in bulk on given criteria
	 * 
	 * @param request Calculation request
	 * @param masterMap master data
	 * @param errorTopic error topic
	 */

	private void generateDemandInBatch(CalculationReq request) {
		/*
		 * this topic will be used by billing service to post message
		 */
		request.getMigrationCount().setAuditTopic(bulkBillGenAuditTopic);
		request.getMigrationCount().setAuditTime(System.currentTimeMillis());
		try {
			bulkDemandAndBillGenService.bulkDemandGeneration(request);
		} catch (Exception ex) {
			/*
			 * Error with message goes to audit topic
			 */
			log.error("Failed in DemandGenerationConsumer with error : " + ex.getMessage());
			log.info("Bulk bill Errorbatch records log for batch : " + request.getMigrationCount().getOffset()
					+ "Count is : " + request.getMigrationCount().getRecordCount());
			request.getMigrationCount().setMessage("Failed in DemandGenerationConsumer with error : " + ex.getMessage());
			producer.push(bulkBillGenAuditTopic, request.getMigrationCount());
		}
	}
}
