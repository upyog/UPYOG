package org.egov.wscalculation.consumer;

import java.util.*;
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
	@KafkaListener(topics = {
			"${egov.watercalculatorservice.createdemand.topic}" }, containerFactory = "kafkaListenerContainerFactoryBatch")
	@SuppressWarnings("unchecked")
	public void listen(final List<Message<?>> records) {
		CalculationReq calculationReq = mapper.convertValue(records.get(0).getPayload(), CalculationReq.class);
		Map<String, Object> masterMap = mstrDataService.loadMasterData(calculationReq.getRequestInfo(), calculationReq.getCalculationCriteria().get(0).getTenantId());
		List<CalculationCriteria> calculationCriteria = new ArrayList<>();
		records.forEach(record -> {
			try {
				CalculationReq calcReq = mapper.convertValue(record.getPayload(), CalculationReq.class);
				calculationCriteria.addAll(calcReq.getCalculationCriteria());
				log.info("Consuming record: " + record);
			} catch (final Exception e) {
				StringBuilder builder = new StringBuilder();
				builder.append("Error while listening to value: ").append(record).append(" on topic: ").append(e);
				log.error(builder.toString());
			}
		});
		CalculationReq request = CalculationReq.builder().calculationCriteria(calculationCriteria)
				.requestInfo(calculationReq.getRequestInfo()).isconnectionCalculation(true)
				.taxPeriodFrom(calculationReq.getTaxPeriodFrom())
				.taxPeriodTo(calculationReq.getTaxPeriodTo())
				.migrationCount(calculationReq.getMigrationCount())
				.build();

		generateDemandInBatch(request,masterMap, config.getDemandGenerationErrorTopic());
		//log.info("Number of batch records:  " + records.size());
		log.info("Number of batch records in the consumer:  " + calculationReq.getCalculationCriteria().size());
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
