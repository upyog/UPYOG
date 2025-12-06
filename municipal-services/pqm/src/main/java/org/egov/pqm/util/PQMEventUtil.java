package org.egov.pqm.util;

import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.pqmProducer.PqmProducer;
import org.egov.pqm.web.model.TestRequest;
//import org.egov.hash.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PQMEventUtil {

	@Autowired
	private PqmProducer producer;

	 @Autowired
	  private ServiceConfiguration config;

	public void processPQMEvent(TestRequest testEvent) {

		producer.push(config.getTestSaveEventTopic(), testEvent);

		log.info("Send event on topic - " + config.getTestSaveEventTopic() + " Request body for topic " + testEvent);

	}

}
