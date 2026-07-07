package org.egov.wscalculation.consumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
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
	@KafkaListener(
		    topics = "${egov.watercalculatorservice.billgenerate.topic}",
		    containerFactory = "kafkaListenerContainerFactoryBatch"
		)
		public void listen(final List<ConsumerRecord<String, Object>> records) {
		    log.info("📥 WS bill generator consumer received batch with {} records", records.size());

		    for (int i = 0; i < records.size(); i++) {
		        ConsumerRecord<String, Object> record = records.get(i);
		        try {
		            log.info("🔸 [Record {}] key={}, offset={}, partition={}, value={}",
		                    i + 1, record.key(), record.offset(), record.partition(), record.value());

		            // Convert record value to BillGeneratorReq
		            BillGeneratorReq billGeneratorReq = mapper.convertValue(record.value(), BillGeneratorReq.class);

		            if (billGeneratorReq == null) {
		                log.warn("⚠️ Skipping record {} — unable to parse BillGeneratorReq", i + 1);
		                continue;
		            }

		            log.info("🧾 Parsed Request -> Consumers: {}, TenantId: {}",
		                    billGeneratorReq.getConsumerCodes(), billGeneratorReq.getTenantId());

		            if (billGeneratorReq.getConsumerCodes() == null ||
		                billGeneratorReq.getConsumerCodes().isEmpty() ||
		                billGeneratorReq.getTenantId() == null) {
		                log.warn("⚠️ Skipping record {} — missing consumerCodes or tenantId", i + 1);
		                continue;
		            }

		            log.info("🚀 Fetch Bill generator initiated for Consumers: {}", billGeneratorReq.getConsumerCodes());

		            List<String> failureConsumerCodes = new ArrayList<>();

		            List<String> fetchBillSuccessConsumercodes = demandService.fetchBillSchedulerSingle(
		                    billGeneratorReq.getConsumerCodes(),
		                    billGeneratorReq.getTenantId(),
		                    billGeneratorReq.getRequestInfoWrapper().getRequestInfo(),
		                    failureConsumerCodes,
		                    billGeneratorReq.getBillSchedular().getId(),
		                    billGeneratorReq.getBillSchedular().getLocality()
		            );

		            log.info("✅ Fetch Bill generator completed for consumers: {}", fetchBillSuccessConsumercodes);
		        } catch (Exception ex) {
		            log.error("❌ Exception while processing record {} -> {}", i + 1, ex.getMessage(), ex);
		        }
		    }
		}

}
