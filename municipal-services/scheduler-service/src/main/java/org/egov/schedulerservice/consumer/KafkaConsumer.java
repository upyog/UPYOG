package org.egov.schedulerservice.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.schedulerservice.contract.bill.BillResponse;
import org.egov.schedulerservice.contract.bill.Demand;
import org.egov.schedulerservice.contract.bill.DemandRepository;
import org.egov.schedulerservice.contract.bill.DemandRequest;
import org.egov.schedulerservice.contract.bill.GenerateBillCriteria;
import org.egov.schedulerservice.producer.Producer;
import org.egov.schedulerservice.service.BillService;
import org.egov.schedulerservice.util.RequestInfoWrapper;
import org.egov.schedulerservice.util.SchedulerConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumer {

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private BillService billService;

	@Autowired
	private Producer producer;

	@Autowired
	private ObjectMapper objectMapper;

	@KafkaListener(topics = "test_save_demand")
	public void callDemandCreate(DemandRequest demandRequest) {

		String businessService = StringUtils.EMPTY;

		// Listener
		List<Demand> savedDemands = demandRepository.saveDemand(demandRequest.getRequestInfo(),
				demandRequest.getDemands());
		if (CollectionUtils.isEmpty(savedDemands)) {
			throw new CustomException("INVALID_CONSUMERCODE",
					"Bill not generated due to no Demand found for the given consumerCode");
		}
		// fetch/create bill request
		Set<String> consumerCodes = savedDemands.stream().map(demand -> demand.getConsumerCode())
				.collect(Collectors.toSet());
		businessService = savedDemands.stream().findAny().get().getBusinessService();
		GenerateBillCriteria generateBillCriteria = GenerateBillCriteria.builder()
				.tenantId(SchedulerConstants.STATE_LEVEL_TENANT_ID).businessService(businessService)
				.consumerCode(consumerCodes).build();
		Map<String, Object> billRequest = new HashMap<>();
		billRequest.put("requestInfo", demandRequest.getRequestInfo());
		billRequest.put("generateBillCriteria", generateBillCriteria);
		producer.push("save_bill", billRequest);

	}

	@KafkaListener(topics = "test_save_bill")
	public void callBillCreate(Map<String, Object> billRequest) {

		RequestInfo requestInfo = objectMapper.convertValue(billRequest.get("requestInfo"), RequestInfo.class);
		GenerateBillCriteria generateBillCriteria = objectMapper.convertValue(billRequest.get("generateBillCriteria"),
				GenerateBillCriteria.class);

		BillResponse billResponse = billService.generateBill(requestInfo, generateBillCriteria);

	}

}
