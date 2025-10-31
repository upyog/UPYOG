package org.egov.wscalculation.consumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.repository.BillGeneratorDao;
import org.egov.wscalculation.service.DemandService;
import org.egov.wscalculation.web.models.BillGeneratorReq;
import org.egov.wscalculation.web.models.BillScheduler.StatusEnum;
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
	"${egov.watercalculatorservice.billgenerate.topic}" }, containerFactory = "kafkaListenerContainerFactoryBatch")
	public void listen(final List<Message<?>> records) {
	    BillGeneratorReq billGeneratorReq = null;

	    try {
	        log.info("bill generator consumer received records: {}", records.size());

	        billGeneratorReq = mapper.convertValue(records.get(0).getPayload(), BillGeneratorReq.class);
	        log.info("Number of batch records: {}", billGeneratorReq.getConsumerCodes().size());

	        long timeStamp = System.currentTimeMillis();
	        if (billGeneratorReq.getConsumerCodes() != null && !billGeneratorReq.getConsumerCodes().isEmpty() 
	                && billGeneratorReq.getTenantId() != null) {

	            log.info("Fetch Bill generator initiated for Consumers: {}", billGeneratorReq.getConsumerCodes());
	            List<String> failureConsumerCodes = new ArrayList<>();

	            billGeneratorDao.insertBillSchedulerConnectionStatus(
	                    new ArrayList<>(billGeneratorReq.getConsumerCodes()),
	                    billGeneratorReq.getBillSchedular().getId(),
	                    billGeneratorReq.getBillSchedular().getLocality(),
	                    WSCalculationConstant.INPROGRESS,
	                    billGeneratorReq.getBillSchedular().getTenantId(),
	                    WSCalculationConstant.INPROGRESS,
	                    timeStamp
	            );

	            List<String> fetchBillSuccessConsumercodes = demandService.fetchBillSchedulerSingle(
	                    billGeneratorReq.getConsumerCodes(),
	                    billGeneratorReq.getTenantId(),
	                    billGeneratorReq.getRequestInfoWrapper().getRequestInfo(),
	                    failureConsumerCodes,
	                    billGeneratorReq.getBillSchedular().getId(),
	                    billGeneratorReq.getBillSchedular().getLocality()
	            );

	            log.info("Fetch Bill generator completed for consumers: {}", fetchBillSuccessConsumercodes);
	        }

	    } catch (Exception exception) {
	        log.error("Exception occurred while generating bills in the sw bill generator consumer", exception);
	    }

		/* Recheck and reprocess outside main try-catch, in separate safe block */
	    try {
	        if (billGeneratorReq != null) {
	            List<String> stillInitiatedConnections = billGeneratorDao.getConnectionsByStatus(
	                    billGeneratorReq.getBillSchedular().getId(),
	                    WSCalculationConstant.INITIATED
	            );

	            if (stillInitiatedConnections != null && !stillInitiatedConnections.isEmpty()) {
	                log.info("Reprocessing still initiated connections: {}", stillInitiatedConnections);

	                List<String> reprocessedConnections = demandService.fetchBillSchedulerSingle(
	                        new HashSet<>(stillInitiatedConnections),
	                        billGeneratorReq.getTenantId(),
	                        billGeneratorReq.getRequestInfoWrapper().getRequestInfo(),
	                        new ArrayList<>(),
	                        billGeneratorReq.getBillSchedular().getId(),
	                        billGeneratorReq.getBillSchedular().getLocality()
	                );
	                log.info("Reprocessing completed for connections: {}", reprocessedConnections);
	            }
	        }
	    } catch (Exception ex) {
	        log.error("Exception occurred while reprocessing initiated connections", ex);
	    }
	}


}
