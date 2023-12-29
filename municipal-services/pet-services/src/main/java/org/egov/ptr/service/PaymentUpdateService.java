package org.egov.ptr.service;

import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.Property;
import org.egov.ptr.models.PropertyCriteria;
import org.egov.ptr.models.collection.Bill;
import org.egov.ptr.models.collection.PaymentDetail;
import org.egov.ptr.models.collection.PaymentRequest;
import org.egov.ptr.models.enums.Status;
import org.egov.ptr.models.workflow.ProcessInstanceRequest;
import org.egov.ptr.models.workflow.State;
import org.egov.ptr.producer.Producer;
import org.egov.ptr.repository.PropertyRepository;
import org.egov.ptr.util.PropertyUtil;
import org.egov.ptr.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

@Service
@Slf4j
public class PaymentUpdateService {

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private PetConfiguration config;

	@Autowired
	private WorkflowService wfIntegrator;

	@Autowired
	private Producer producer;

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private PropertyUtil util;
	
	@Autowired
	private NotificationService notifService;

	/**
	 * Process the message from kafka and updates the status to paid
	 * 
	 * @param record The incoming message from receipt create consumer
	 */
	public void process(HashMap<String, Object> record) {

		try {

			PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
			RequestInfo requestInfo = paymentRequest.getRequestInfo();

			List<PaymentDetail> paymentDetails = paymentRequest.getPayment().getPaymentDetails();
			String tenantId = paymentRequest.getPayment().getTenantId();

			for (PaymentDetail paymentDetail : paymentDetails) {
				
				Boolean isModuleMutation = paymentDetail.getBusinessService().equalsIgnoreCase(config.getMutationWfName());
				
				if (isModuleMutation) {

					updateWorkflowForMutationPayment(requestInfo, tenantId, paymentDetail);
				}
			}
		} catch (Exception e) {
			log.error("KAFKA_PROCESS_ERROR:", e);
		}

	}

	/**
	 * method to do workflow update for Property
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param paymentDetail
	 */
	private void updateWorkflowForMutationPayment(RequestInfo requestInfo, String tenantId, PaymentDetail paymentDetail) {
		
		Bill bill  = paymentDetail.getBill();
		
		PropertyCriteria criteria = PropertyCriteria.builder()
				.acknowledgementIds(Sets.newHashSet(bill.getConsumerCode()))
				.tenantId(tenantId)
				.build();
				
		List<Property> properties = propertyRepository.getPropertiesWithOwnerInfo(criteria, requestInfo, true);

		if (CollectionUtils.isEmpty(properties))
			throw new CustomException("INVALID RECEIPT",
					"No Properties found for the comsumerCode " + criteria.getPropertyIds());

		Role role = Role.builder().code("SYSTEM_PAYMENT").tenantId(tenantId).build();
		requestInfo.getUserInfo().getRoles().add(role);
		
		properties.forEach( property -> {
			
			PropertyRequest updateRequest = PropertyRequest.builder().requestInfo(requestInfo)
					.property(property).build();
			
			ProcessInstanceRequest wfRequest = util.getProcessInstanceForMutationPayment(updateRequest);
			State state = new State();
//			State state = wfIntegrator.callWorkFlow(wfRequest);
			property.setWorkflow(wfRequest.getProcessInstances().get(0));
			property.getWorkflow().setState(state);
			updateRequest.getProperty().setStatus(Status.fromValue(state.getApplicationStatus()));
			producer.push(config.getUpdatePropertyTopic(), updateRequest);			
			notifService.sendNotificationForMtPayment(updateRequest, bill.getTotalAmount());
		});
	}

}
