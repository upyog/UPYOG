package org.upyog.sv.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.ServiceRequestRepository;
import org.upyog.sv.repository.StreetVendingRepository;
import org.upyog.sv.util.IdgenUtil;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.web.models.RenewalStatus;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;
import org.upyog.sv.web.models.workflow.ProcessInstance;
import org.upyog.sv.web.models.workflow.ProcessInstanceRequest;
import org.upyog.sv.web.models.workflow.ProcessInstanceResponse;
import org.upyog.sv.web.models.workflow.State;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {

	@Autowired
	private ObjectMapper mapper;

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsUrl;

	@Autowired
	private StreetVendingConfiguration configs;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private StreetVendingRepository streetVendingRepository;

	@Autowired
	private IdgenUtil idgenUtil;

	/**
	 *
	 * @param record
	 * @param topic
	 */

	public void process(HashMap<String, Object> record, String topic) throws JsonProcessingException {
		log.info(" Receipt consumer class entry " + record.toString());
		try {
			PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
			log.info("paymentRequest : " + paymentRequest);
			String businessService = paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService();
			log.info("Payment request processing in Street Vending method for businessService : " + businessService);
			if (configs.getModuleName()
					.equals(paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService())) {
				String applicationNo = paymentRequest.getPayment().getPaymentDetails().get(0).getBill()
						.getConsumerCode();
				log.info("Updating payment status for street vending booking : " + applicationNo);

				updateApplicationStatusAndWorkflow(paymentRequest);
			}
		} catch (IllegalArgumentException e) {
			log.error(
					"Illegal argument exception occured while sending notification Street Vending : " + e.getMessage());
		} catch (Exception e) {
			log.error("An unexpected exception occurred while sending notification Street Vending : ", e);
		}

	}

	public State updateWorkflowStatus(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = getProcessInstanceForSV(paymentRequest);
		log.info(" Process instance of street vending application " + processInstance.toString());
		ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(paymentRequest.getRequestInfo(),
				Collections.singletonList(processInstance));
		State state = callWorkFlow(workflowRequest);

		return state;

	}

	private ProcessInstance getProcessInstanceForSV(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = new ProcessInstance();
		processInstance
				.setBusinessId(paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode());
		processInstance.setAction(StreetVendingConstants.ACTION_PAY);
		processInstance.setModuleName(configs.getModuleName());
		processInstance.setTenantId(paymentRequest.getPayment().getTenantId());
		processInstance.setBusinessService(configs.getBusinessServiceName());
		processInstance.setDocuments(null);
		processInstance.setComment(null);
		processInstance.setAssignes(null);

		return processInstance;

	}

	public State callWorkFlow(ProcessInstanceRequest workflowReq) {
		log.info(" Workflow Request for street vending service for final step " + workflowReq.toString());
		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		log.info(" URL for calling workflow service " + workflowReq.toString());
		Object workflow = serviceRequestRepository.fetchResult(url, workflowReq);
		response = mapper.convertValue(workflow, ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}

	private void updateApplicationStatusAndWorkflow(PaymentRequest paymentRequest) {
		StreetVendingDetail streetVendingDetail = streetVendingRepository
				.getStreetVendingApplications(StreetVendingSearchCriteria.builder()
						.applicationNumber(
								paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode())
						.build())
				.get(0);
		long todayDateInMillis = System.currentTimeMillis();

		if (streetVendingDetail == null) {
			log.info("Application not found in consumer class while updating status");
		}
		String applicationStatus = null;
		if(streetVendingDetail.getRenewalStatus()!= RenewalStatus.RENEW_IN_PROGRESS) {
			State state = updateWorkflowStatus(paymentRequest);
			 applicationStatus = state.getApplicationStatus();
		}
		else {
			applicationStatus = StreetVendingConstants.APPLICATION_STATUS_RENEWED;
			streetVendingDetail.setRenewalStatus(RenewalStatus.RENEWED);
		}
		StreetVendingRequest vendingRequest = StreetVendingRequest.builder()
				.requestInfo(paymentRequest.getRequestInfo()).build();

		streetVendingDetail.getAuditDetails()
				.setLastModifiedBy(paymentRequest.getRequestInfo().getUserInfo().getUuid());
		streetVendingDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
		streetVendingDetail.setApplicationStatus(applicationStatus);
		streetVendingDetail.setApprovalDate(todayDateInMillis);
		streetVendingDetail.setValidityDateForPersisterDate(StreetVendingUtil.getCurrentDateFromYear(1).toString());// add validity date for post 1 year of approval date
		vendingRequest.setStreetVendingDetail(streetVendingDetail);
		enrichCertificateNumber(streetVendingDetail, vendingRequest.getRequestInfo(),
				streetVendingDetail.getTenantId()); // enriching certificate number when updating final status
		log.info("Street Vending Request to update application status in consumer : " + vendingRequest);

		streetVendingRepository.update(vendingRequest);

	}

	/**
	 * Method to enrich certificate number
	 *
	 * @param streetVendingDetail
	 * @param requestInfo
	 * @param tenantId
	 */
	private void enrichCertificateNumber(StreetVendingDetail streetVendingDetail, RequestInfo requestInfo,
			String tenantId) {
		if (streetVendingDetail.getCertificateNo() == null || streetVendingDetail.getCertificateNo().isEmpty()) {
			String certificateNo = idgenUtil.getIdList(requestInfo, tenantId, configs.getStreetVendingCertificateNoName(),
					configs.getStreetVendingCertificateNoFormat(), 1).get(0);
			streetVendingDetail.setCertificateNo(certificateNo);
		}
	}

}
