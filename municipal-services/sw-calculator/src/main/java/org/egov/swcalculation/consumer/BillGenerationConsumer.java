package org.egov.swcalculation.consumer;

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
				
				List<String> fetchBillSuccessConsumercodes = demandService.fetchBillSchedulerSingle(billGeneratorReq.getConsumerCodes(),billGeneratorReq.getTenantId() ,billGeneratorReq.getRequestInfoWrapper().getRequestInfo());
				log.info("Fetch Bill generator completed fetchBillConsumers: {}", fetchBillSuccessConsumercodes);
				long milliseconds = System.currentTimeMillis();
				
				if(fetchBillSuccessConsumercodes != null && !fetchBillSuccessConsumercodes.isEmpty()) {
					
					billGeneratorDao.insertBillSchedulerConnectionStatus(
							fetchBillSuccessConsumercodes, 
							billGeneratorReq.getBillSchedular().getId(), 
							billGeneratorReq.getBillSchedular().getLocality(), 
							SWCalculationConstant.SUCCESS, 
							billGeneratorReq.getBillSchedular().getTenantId(), 
							SWCalculationConstant.SUCCESS_MESSAGE, milliseconds);
					
				} 
				//Removing the fetch bill success consumercodes from billGenerate
				billGeneratorReq.getConsumerCodes().removeAll(fetchBillSuccessConsumercodes);
				if(!billGeneratorReq.getConsumerCodes().isEmpty()) {
					log.info("Bill generator failure consumercodes: {}", billGeneratorReq.getConsumerCodes());

					billGeneratorDao.insertBillSchedulerConnectionStatus(
							billGeneratorReq.getConsumerCodes().stream().collect(Collectors.toList()), 
							billGeneratorReq.getBillSchedular().getId(), 
							billGeneratorReq.getBillSchedular().getLocality(), 
							SWCalculationConstant.FAILURE, 
							billGeneratorReq.getBillSchedular().getTenantId(), 
							SWCalculationConstant.FAILURE_MESSAGE, milliseconds);
					
				}
				
			}
		}catch(Exception exception) {
			log.error("Exception occurred while generating bills in the sw bill generator consumer");
		}

	}

}
