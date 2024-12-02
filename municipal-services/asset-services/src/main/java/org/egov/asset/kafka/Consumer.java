package org.egov.asset.kafka;

import java.util.HashMap;

import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Consumer {

	/*
	 * Uncomment the below line to start consuming record from kafka.topics.consumer
	 * Value of the variable kafka.topics.consumer should be overwritten in
	 * application.properties
	 */
	@KafkaListener(topics = { 	"${persister.update.assetdetails.topic}", 
								"${persister.save.assetdetails.topic}",
								"${persister.update.assetdetails.workflow.topic}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		ObjectMapper mapper = new ObjectMapper();
		AssetRequest assetRequest = new AssetRequest();
		Asset asset = new Asset();
		

		try {
			log.info("Consuming record: " + record.toString());
			assetRequest = mapper.convertValue(record, AssetRequest.class);
			asset = assetRequest.getAsset();
			//assetJpaRepository.save(asset);

		} catch (final Exception e) {
			log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
		}
		log.debug("Asset Received: " + asset.getApplicationNo());

	}
}
