package org.egov.ndc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.ndc.util.NDCConstants;
import org.egov.ndc.web.model.Workflow;
import org.egov.ndc.web.model.bill.PaymentDetail;
import org.egov.ndc.web.model.bill.PaymentRequest;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.egov.ndc.workflow.WorkflowIntegrator;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class PaymentUpdateService {

	private final NDCService ndcService;
	private final WorkflowIntegrator wfIntegrator;
	private final EnrichmentService enrichmentService;
	private final ObjectMapper mapper;

	public PaymentUpdateService(NDCService ndcService, WorkflowIntegrator wfIntegrator,
			EnrichmentService enrichmentService, ObjectMapper mapper) {
		this.ndcService = ndcService;
		this.wfIntegrator = wfIntegrator;
		this.enrichmentService = enrichmentService;
		this.mapper = mapper;
	}

	/**
	 * Process the message from kafka and updates the status to paid
	 *
	 * @param paymentRequest The incoming message from receipt create consumer
	 */
	public void process(PaymentRequest paymentRequest) {

		log.info("Start PaymentUpdateService.process method.");
		try {
			PaymentRequest parsedRequest = mapper.convertValue(paymentRequest, PaymentRequest.class);
			RequestInfo requestInfo = parsedRequest.getRequestInfo();
			List<PaymentDetail> paymentDetails = parsedRequest.getPayment().getPaymentDetails();
			String tenantIdFromPaymentDetails = parsedRequest.getPayment().getTenantId();
			for (PaymentDetail paymentDetail : paymentDetails) {
				if (!paymentDetail.getBusinessService().equalsIgnoreCase(NDCConstants.NDC_BUSINESS_SERVICE)
						&& !paymentDetail.getBusinessService().equalsIgnoreCase(NDCConstants.NDC_MODULE)) {
					continue;
				}
				NdcApplicationSearchCriteria searchCriteria = new NdcApplicationSearchCriteria();
				searchCriteria.setTenantId(tenantIdFromPaymentDetails);
				searchCriteria.setApplicationNo(Collections.singletonList(paymentDetail.getBill().getConsumerCode()));
				List<Application> applications = ndcService.searchNdcApplications(searchCriteria, requestInfo);

				String tenantIdFromSearch = applications.get(0).getTenantId();

				applications.forEach(application -> {
					Workflow workflow = new Workflow();
					workflow.setAction(NDCConstants.ACTION_PAY);
					application.setWorkflow(workflow);
					application.setAction(NDCConstants.ACTION_PAY);
				});

				Role role = Role.builder().code("SYSTEM_PAYMENT").tenantId(tenantIdFromSearch).build();
				requestInfo.getUserInfo().getRoles().add(role);
				NdcApplicationRequest updateRequest = NdcApplicationRequest.builder()
						.requestInfo(requestInfo)
						.applications(applications)
						.build();

				wfIntegrator.callWorkFlow(updateRequest, NDCConstants.NDC_BUSINESS_SERVICE);
				log.info(" applications uuid is : {}", updateRequest.getApplications().get(0).getApplicationNo());
				log.info(" the status of the applications is : {}",
						updateRequest.getApplications().get(0).getApplicationStatus());
				enrichmentService.postStatusEnrichment(updateRequest, NDCConstants.NDC_BUSINESS_SERVICE);
				ndcService.updateNdcApplication(true, updateRequest);
			}
		} catch (Exception e) {
			log.error("KAFKA_PROCESS_ERROR", e);
		}

	}

}
