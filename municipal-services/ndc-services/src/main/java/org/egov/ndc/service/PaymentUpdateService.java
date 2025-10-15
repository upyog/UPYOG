package org.egov.ndc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.NDCRepository;
import org.egov.ndc.util.NDCConstants;
import org.egov.ndc.util.NDCUtil;
import org.egov.ndc.web.model.Workflow;
import org.egov.ndc.web.model.bill.PaymentDetail;
import org.egov.ndc.web.model.bill.PaymentRequest;
import org.egov.ndc.web.model.ndc.ApplicantRequest;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.egov.ndc.workflow.WorkflowIntegrator;
import org.egov.ndc.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Service
@Slf4j
public class PaymentUpdateService {

	private NDCService ndcService;

	private NDCConfiguration config;

	private NDCRepository repository;

	private WorkflowIntegrator wfIntegrator;

	private EnrichmentService enrichmentService;

	private ObjectMapper mapper;

	private WorkflowService workflowService;

	private NDCUtil util;

//	@Value("${workflow.bpa.businessServiceCode.fallback_enabled}")
//	private Boolean pickWFServiceNameFromTradeTypeOnly;

	@Autowired
	public PaymentUpdateService(NDCService service, NDCConfiguration config, NDCRepository repository,
								WorkflowIntegrator wfIntegrator, EnrichmentService enrichmentService, ObjectMapper mapper,
								WorkflowService workflowService, NDCUtil util) {
		this.ndcService = service;
		this.config = config;
		this.repository = repository;
		this.wfIntegrator = wfIntegrator;
		this.enrichmentService = enrichmentService;
		this.mapper = mapper;
		this.workflowService = workflowService;
		this.util = util;
	}



	/**
	 * Process the message from kafka and updates the status to paid
	 * 
	 * @param record The incoming message from receipt create consumer
	 */
	public void process(PaymentRequest record) {

		log.info("Start PaymentUpdateService.process method.");
		try {
			PaymentRequest paymentRequest = mapper.convertValue(record,PaymentRequest.class);
			RequestInfo requestInfo = paymentRequest.getRequestInfo();
			List<PaymentDetail> paymentDetails = paymentRequest.getPayment().getPaymentDetails();
			String tenantIdFromPaymentDetails = paymentRequest.getPayment().getTenantId();
			for(PaymentDetail paymentDetail : paymentDetails){
				if (paymentDetail.getBusinessService().equalsIgnoreCase(NDCConstants.NDC_BUSINESS_SERVICE )|| paymentDetail.getBusinessService().equalsIgnoreCase(NDCConstants.NDC_MODULE )) {
					NdcApplicationSearchCriteria searchCriteria = new NdcApplicationSearchCriteria();
					searchCriteria.setTenantId(tenantIdFromPaymentDetails);
					searchCriteria.setApplicationNo(Collections.singletonList(paymentDetail.getBill().getConsumerCode()));
					List<Application> applications = ndcService.searchNdcApplications(searchCriteria, requestInfo);

					String tenantIdFromSearch = applications.get(0).getTenantId();

                    applications.forEach(application -> {
								Workflow workflow=new Workflow();
								workflow.setAction(NDCConstants.ACTION_PAY);
								application.setWorkflow(workflow);
								application.setAction(NDCConstants.ACTION_PAY);
							}
						);

					Role role = Role.builder().code("SYSTEM_PAYMENT").tenantId(tenantIdFromSearch).build();
					requestInfo.getUserInfo().getRoles().add(role);
					NdcApplicationRequest updateRequest = NdcApplicationRequest.builder()
							.requestInfo(requestInfo)
							.applications(applications)
							.build();

					/*
					 * calling workflow to update status
					 */
					wfIntegrator.callWorkFlow(updateRequest,NDCConstants.NDC_BUSINESS_SERVICE);
                    log.info(" applications uuid is : {}", updateRequest.getApplications().get(0).getApplicationNo());
                    log.info(" the status of the applications is : {}", updateRequest.getApplications().get(0).getApplicationStatus());
					enrichmentService.postStatusEnrichment(updateRequest,NDCConstants.NDC_BUSINESS_SERVICE);

					/*
					 * calling repository to update the object in ndc tables
					 */
					ndcService.updateNdcApplication(true , updateRequest);
			}
		 }
		} catch (Exception e) {
			log.error("KAFKA_PROCESS_ERROR", e);
		}

	}

}
