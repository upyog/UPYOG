package org.egov.swcalculation.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.swcalculation.constants.SWCalculationConstant;
import org.egov.swcalculation.repository.BillGeneratorDao;
import org.egov.swcalculation.service.DemandService;
import org.egov.swcalculation.web.models.BillGeneratorReq;
import org.egov.swcalculation.web.models.BillScheduler.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BillGenerationConsumer {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DemandService demandService;

	@Autowired
	private BillGeneratorDao billGeneratorDao;

	/**
	 * Listen the topic for processing the batch records.
	 * 
	 * @param records
	 *            would be bill generator request.
	 */
	@KafkaListener(topics = {
	"${egov.swcalculatorservice.billgenerate.topic}" }, containerFactory = "kafkaListenerContainerFactoryBatch")
	public void listen(final List<Message<?>> records) {
		try {
			log.info("bill generator consumer received records:  " + records.size());

			BillGeneratorReq billGeneratorReq = mapper.convertValue(records.get(0).getPayload(), BillGeneratorReq.class);
			log.info("Number of batch records:  " + billGeneratorReq.getConsumerCodes().size());

			if(billGeneratorReq.getConsumerCodes() != null && !billGeneratorReq.getConsumerCodes().isEmpty() && billGeneratorReq.getTenantId() != null) {
				log.info("Fetch Bill generator initiated for Consumers: {}", billGeneratorReq.getConsumerCodes());
				
				List<String> failureConsumerCodes = new ArrayList<>();

				long milliseconds = System.currentTimeMillis();
				 billGeneratorDao.insertBillSchedulerConnectionStatus(
			                new ArrayList<>(billGeneratorReq.getConsumerCodes()),
				            billGeneratorReq.getBillSchedular().getId(),
				            billGeneratorReq.getBillSchedular().getLocality(),
				            SWCalculationConstant.INITIATED,
				            billGeneratorReq.getBillSchedular().getTenantId(),
				            SWCalculationConstant.INITIATED_MESSAGE,
				            milliseconds
				    );
				
				 List<String> fetchBillSuccessConsumercodes = demandService.fetchBillSchedulerSingle(
			                billGeneratorReq.getConsumerCodes(),
			                billGeneratorReq.getTenantId(),
			                billGeneratorReq.getRequestInfoWrapper().getRequestInfo(),
			                failureConsumerCodes,
			                billGeneratorReq.getBillSchedular().getId(),
			                billGeneratorReq.getBillSchedular().getLocality()
			        );

			        log.info("✅ Fetch Bill generator completed. Success count: {}, Failures: {}",
			                fetchBillSuccessConsumercodes.size(), failureConsumerCodes.size());
			        log.info("Successful consumerCodes: {}", fetchBillSuccessConsumercodes);
			        log.info("Failed consumerCodes: {}", failureConsumerCodes);
			}
		}catch(Exception exception) {
			log.error("Exception occurred while generating bills in the sw bill generator consumer");
		}

	}

}
