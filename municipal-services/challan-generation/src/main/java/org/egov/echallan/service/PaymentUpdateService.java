package org.egov.echallan.service;


import java.util.List;
import java.util.Map;
import org.egov.common.contract.request.RequestInfo;
import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.model.AuditDetails;
import org.egov.echallan.model.Challan;
import org.egov.echallan.model.Challan.StatusEnum;
import org.egov.echallan.model.ChallanRequest;
import org.egov.echallan.model.SearchCriteria;
import org.egov.echallan.producer.Producer;
import org.egov.echallan.util.ChallanConstants;
import org.egov.echallan.util.CommonUtils;
import org.egov.echallan.web.models.collection.PaymentDetail;
import org.egov.echallan.web.models.collection.PaymentRequest;
import org.egov.echallan.web.models.workflow.Workflow;
import org.egov.echallan.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PaymentUpdateService {

	private final ObjectMapper mapper;
	private final ChallanService challanService;
	private final Producer producer;
	private final ChallanConfiguration config;
	private final CommonUtils commUtils;
	private final WorkflowIntegrator workflowIntegrator;

	@Autowired
	public PaymentUpdateService(ObjectMapper mapper, ChallanService challanService, Producer producer,
			ChallanConfiguration config, CommonUtils commUtils,
			@Autowired(required = false) WorkflowIntegrator workflowIntegrator) {
		this.mapper = mapper;
		this.challanService = challanService;
		this.producer = producer;
		this.config = config;
		this.commUtils = commUtils;
		this.workflowIntegrator = workflowIntegrator;
	}

	public void process(Map<String, Object> messagePayload) {

		try {
			log.info("Process for object"+ messagePayload);
			PaymentRequest paymentRequest = mapper.convertValue(messagePayload, PaymentRequest.class);
			RequestInfo requestInfo = paymentRequest.getRequestInfo();
			//Update the echallan only when the payment is fully done.
			if( paymentRequest.getPayment().getTotalAmountPaid().compareTo(paymentRequest.getPayment().getTotalDue())!=0)
				return;
			List<PaymentDetail> paymentDetails = paymentRequest.getPayment().getPaymentDetails();
			for (PaymentDetail paymentDetail : paymentDetails) {
				SearchCriteria criteria = new SearchCriteria();
				criteria.setTenantId(paymentRequest.getPayment().getTenantId());
				criteria.setChallanNo(paymentDetail.getBill().getConsumerCode());
				criteria.setBusinessService(paymentDetail.getBusinessService());
				List<Challan> challans = challanService.search(criteria, requestInfo);
				//update echallan only if payment is done for echallan.
				if(!CollectionUtils.isEmpty(challans) ) {
					String uuid = requestInfo.getUserInfo().getUuid();
				    AuditDetails auditDetails = commUtils.getAuditDetails(uuid, true);
					for(Challan challan: challans){

						Workflow workflow=new Workflow();
						workflow.setAction(ChallanConstants.ACTION_PAY);
						challan.setWorkflow(workflow);

						String nextStatus = workflowIntegrator.transition(requestInfo,
								challan,
								challan.getWorkflow().getAction());

						challan.setApplicationStatus(StatusEnum.PAID);
						challan.setChallanStatus(nextStatus);
						challan.setReceiptNumber(paymentDetail.getReceiptNumber());
					}
					challans.get(0).setAuditDetails(auditDetails);
					ChallanRequest request = ChallanRequest.builder().requestInfo(requestInfo).challan(challans.get(0)).build();
					producer.push(config.getUpdateChallanTopic(), request);
				}
			}
		} catch (Exception e) {
			log.error("Exception while processing payment update: ",e);
		}

	}

}
