package org.egov.noc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.repository.NOCRepository;
import org.egov.noc.util.NOCConstants;
import org.egov.noc.util.NOCUtil;
import org.egov.noc.web.model.Workflow;
import org.egov.noc.web.model.bill.PaymentDetail;
import org.egov.noc.web.model.bill.PaymentRequest;
import org.egov.noc.web.model.Noc;
import org.egov.noc.web.model.NocRequest;
import org.egov.noc.web.model.NocSearchCriteria;
import org.egov.noc.workflow.WorkflowIntegrator;
import org.egov.noc.workflow.WorkflowService;
import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.repository.NOCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
@Slf4j
public class PaymentUpdateService {

	private NOCService nocService;

	private NOCConfiguration config;

	private NOCRepository repository;

	private WorkflowIntegrator wfIntegrator;

	private EnrichmentService enrichmentService;

	private ObjectMapper mapper;

	private WorkflowService workflowService;

	private NOCUtil util;

//	@Value("${workflow.bpa.businessServiceCode.fallback_enabled}")
//	private Boolean pickWFServiceNameFromTradeTypeOnly;

	@Autowired
	public PaymentUpdateService(NOCService service, NOCConfiguration config, NOCRepository repository,
                                WorkflowIntegrator wfIntegrator, EnrichmentService enrichmentService, ObjectMapper mapper,
                                WorkflowService workflowService, NOCUtil util) {
		this.nocService = service;
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
				if (paymentDetail.getBusinessService().equalsIgnoreCase(NOCConstants.NOC_BUSINESS_SERVICE )|| paymentDetail.getBusinessService().equalsIgnoreCase(NOCConstants.NOC_MODULE )) {
					log.info("Start PaymentUpdateService.process method.");
					NocSearchCriteria searchCriteria = new NocSearchCriteria();
					searchCriteria.setTenantId(tenantIdFromPaymentDetails);
					searchCriteria.setApplicationNo(paymentDetail.getBill().getConsumerCode());
					List<Noc> nocs = nocService.search(searchCriteria, requestInfo);

					String tenantIdFromSearch = nocs.get(0).getTenantId();

                    nocs.forEach(application -> {
								Workflow workflow=new Workflow();
								workflow.setAction(NOCConstants.ACTION_PAY);
								application.setWorkflow(workflow);
//								application.setAction(NOCConstants.ACTION_PAY);
							}
						);

					Role role = Role.builder().code("SYSTEM_PAYMENT").tenantId(tenantIdFromSearch).build();
					requestInfo.getUserInfo().getRoles().add(role);
//					NocRequest updateRequest = NocRequest.builder()
//							.requestInfo(requestInfo)
//							.noc(nocs.get(0))
//							.build();
					NocRequest updateRequest = new NocRequest();
						updateRequest.setRequestInfo(requestInfo);
						updateRequest.setNoc(nocs.get(0));

					/*
					 * calling workflow to update status
					 */
//					wfIntegrator.callWorkFlow(updateRequest,NOCConstants.NOC_BUSINESS_SERVICE);
//                    log.info(" applications uuid is : {}", updateRequest.getApplications().get(0).getUuid());
//                    log.info(" the status of the applications is : {}", updateRequest.getApplications().get(0).getApplicationStatus());
					enrichmentService.postStatusEnrichment(updateRequest,NOCConstants.NOC_BUSINESS_SERVICE);

					/*
					 * calling repository to update the object in ndc tables
					 */
					nocService.update(updateRequest);
			}
		 }
		} catch (Exception e) {
			log.error("KAFKA_PROCESS_ERROR", e);
		}

	}

}
