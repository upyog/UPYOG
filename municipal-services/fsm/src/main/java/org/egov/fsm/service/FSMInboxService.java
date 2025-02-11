package org.egov.fsm.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.fsm.config.FSMConfiguration;
import org.egov.fsm.fsmProducer.FSMProducer;
import org.egov.fsm.repository.FSMInboxRepository;
import org.egov.fsm.util.FSMErrorConstants;
import org.egov.fsm.web.model.FSMEvent;
import org.egov.fsm.web.model.FSMRequest;
import org.egov.fsm.web.model.vehicle.trip.VehicleTripSearchCriteria;
import org.egov.fsm.web.model.workflow.ProcessInstance;
import org.egov.fsm.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FSMInboxService {

	@Autowired
	private FSMInboxRepository fsmInboxRepository;

	@Autowired
	private FSMProducer producer;

	@Autowired
	private FSMConfiguration config;

	@Autowired
	private WorkflowService workflowService;

	public List<String> fetchApplicationIds(VehicleTripSearchCriteria vehicleTripSearchCriteria) {

		if (vehicleTripSearchCriteria.getTenantId().split("\\.").length == 1) {
			throw new CustomException(FSMErrorConstants.INVALID_TENANT, " Tenant is not available");
		}
		log.info("applicationStatus() :::: " + vehicleTripSearchCriteria.getApplicationStatus());
		List<String> vehicleTripDetailList = fsmInboxRepository.fetchVehicleStateMap(vehicleTripSearchCriteria);
		if (null == vehicleTripDetailList)
			return new ArrayList<>();

		return vehicleTripDetailList;

	}

	/**
	 * create and Updates the Inbox indexer in elastic database
	 * 
	 * @param fsmRequest Create and update Request
	 * @return
	 */
	public void inboxEvent(FSMRequest fsmRequest) {
		ProcessInstance processInstance = null;

		if (config.getIsExternalWorkFlowEnabled())
			processInstance = workflowService.getProcessInstance(fsmRequest.getFsm(), fsmRequest.getRequestInfo());

		if (!ObjectUtils.isEmpty(processInstance))
			log.info("Incoming process instance:" + processInstance.toString());
		else
			log.info("PROCESS INSTANCE NOT FOUND!!");
		FSMEvent fsmEvent = new FSMEvent(fsmRequest);
		fsmEvent.getFsmRequest().getFsm().setProcessInstance(processInstance);
		producer.push(config.getFsmEventTopic(), fsmEvent);
	}

}