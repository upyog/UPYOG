package org.egov.infra.persist.consumer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.egov.infra.persist.service.PersistService;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service	
@Slf4j
public class PersisterMessageListener implements AcknowledgingMessageListener<String, Object> {

	@Autowired
	private PersistService persistService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CustomKafkaTemplate kafkaTemplate;
	@Autowired
	private Acknowledgment acknowledgment;
	@Value("${audit.persist.kafka.topic}")
	private String persistAuditKafkaTopic;

	@Value("${audit.generate.kafka.topic}")
	private String auditGenerateKafkaTopic;

	@Override
	public void onMessage(ConsumerRecord<String, Object> data, Acknowledgment acknowledgment) {
		String rcvData = null;

		try {
			rcvData = objectMapper.writeValueAsString(data.value());
			persistService.persist(data.topic(), rcvData);
			acknowledgment.acknowledge();
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize incoming message", e);
		}

		if (!data.topic().equalsIgnoreCase(persistAuditKafkaTopic)) {
			Map<String, Object> producerRecord = new HashMap<>();
			producerRecord.put("topic", data.topic());
			producerRecord.put("value", data.value());
			kafkaTemplate.send(auditGenerateKafkaTopic, producerRecord);
		}
	}

	
		// TODO Auto-generated method stub
		
	

}
