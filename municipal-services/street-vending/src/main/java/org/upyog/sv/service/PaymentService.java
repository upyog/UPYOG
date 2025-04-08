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

import static org.upyog.sv.web.models.RenewalStatus.RENEWED;

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
		try {
			PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
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
			log.error("Illegal argument exception occured while sending notification Street Vending : " + e.getMessage());
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

	/**
	 * Updates the application status and workflow of a Street Vending application
	 * based on the payment received. Handles the following cases:
	 *
	 * <ul>
	 *   <li><b>New Application:</b> Updates status via workflow, assigns validity for 1 year,
	 *       and generates a certificate number.</li>
	 *   <li><b>Renewal (Direct Payment):</b> Updates validity by 1 year and marks as renewed.</li>
	 *   <li><b>Renewal (Edited Application):</b> Carries forward old certificate details and updates validity.</li>
	 * </ul>
	 *
	 * @param paymentRequest The payment request containing request info and consumer code
	 */
	private void updateApplicationStatusAndWorkflow(PaymentRequest paymentRequest) {
		log.info("Updating status for payment: {}", paymentRequest);

		String appNo = extractApplicationNumber(paymentRequest);
		StreetVendingDetail detail = fetchApplication(appNo);
		if (detail == null) {
			log.warn("Application not found for applicationNumber: {}", appNo);
			return;
		}

		RequestInfo requestInfo = paymentRequest.getRequestInfo();
		long currentTimestamp = StreetVendingUtil.getCurrentTimestamp();
		String updatedStatus;

		log.info("Processing application: {}, renewal status: {}", appNo, detail.getRenewalStatus());

		switch (detail.getRenewalStatus()) {
			case RENEW_IN_PROGRESS:
				updatedStatus = updateOldApplicationForRenewal(detail);
				break;
			case RENEW_APPLICATION_CREATED:
				updatedStatus = updateNewApplicationForRenewal(paymentRequest, detail);
				break;
			default:
				updatedStatus = updateNewApplication(detail, paymentRequest, requestInfo);
				break;
		}

		updateAuditFields(detail, requestInfo, currentTimestamp, updatedStatus);
		persistApplicationUpdate(requestInfo, detail);
	}

	/**
	 * Extracts application number from payment request.
	 */
	private String extractApplicationNumber(PaymentRequest paymentRequest) {
		return paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
	}

	/**
	 * Fetches application by application number.
	 */
	private StreetVendingDetail fetchApplication(String appNo) {
		log.info("Fetching application: {}", appNo);
		return streetVendingRepository
				.getStreetVendingApplications(StreetVendingSearchCriteria.builder().applicationNumber(appNo).build())
				.stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * Handles direct renewal by adding one year to validity.
	 */
	private String updateOldApplicationForRenewal(StreetVendingDetail detail) {
		log.info("Processing direct renewal for: {}", detail.getApplicationNo());
		detail.setRenewalStatus(RENEWED);
		detail.setValidityDateForPersisterDate(detail.getValidityDate().plusYears(1).toString());
		return StreetVendingConstants.REGISTRATION_COMPLETED;
	}

	/**
	 * Handles renewal where edited application was submitted before payment.
	 * Copies old certificate, extends validity, marks old app as renewed.
	 */
	private String updateNewApplicationForRenewal(PaymentRequest paymentRequest, StreetVendingDetail detail) {
		log.info("Processing edited renewal for: {}", detail.getApplicationNo());
		detail.setRenewalStatus(null);
        State state = updateWorkflowStatus(paymentRequest);
        String applicationStatus = state.getApplicationStatus();
		StreetVendingDetail oldDetail = fetchApplication(detail.getOldApplicationNo());
		if (oldDetail != null) {
			log.info("Fetched old application: {}. Copying certificate and marking as renewed", oldDetail.getApplicationNo());

			detail.setCertificateNo(oldDetail.getCertificateNo());
			detail.setValidityDateForPersisterDate(oldDetail.getValidityDate().plusYears(1).toString());

			oldDetail.setRenewalStatus(RENEWED);
			updateAuditFields(oldDetail, paymentRequest.getRequestInfo(), StreetVendingUtil.getCurrentTimestamp(), oldDetail.getApplicationStatus());
			persistApplicationUpdate(paymentRequest.getRequestInfo(), oldDetail);
		} else {
			log.warn("Old application not found: {}", detail.getOldApplicationNo());
		}

		return applicationStatus;
	}

	/**
	 * Handles new application: triggers workflow, sets validity, and generates certificate.
	 */
	private String updateNewApplication(StreetVendingDetail detail, PaymentRequest paymentRequest, RequestInfo requestInfo) {
		log.info("Processing new application: {}", detail.getApplicationNo());
		State state = updateWorkflowStatus(paymentRequest);
		String applicationStatus = state.getApplicationStatus();

		detail.setValidityDateForPersisterDate(StreetVendingUtil.getCurrentDateFromYear(1).toString());
		enrichCertificateNumber(detail, requestInfo, detail.getTenantId());

		return applicationStatus;
	}

	/**
	 * Sets audit fields and status.
	 */
	private void updateAuditFields(StreetVendingDetail detail, RequestInfo requestInfo, long timestamp, String status) {
		log.debug("Updating audit and status for: {}", detail.getApplicationNo());
		detail.getAuditDetails().setLastModifiedBy(requestInfo.getUserInfo().getUuid());
		detail.getAuditDetails().setLastModifiedTime(timestamp);
		detail.setApplicationStatus(status);
		detail.setApprovalDate(timestamp);
	}

	/**
	 * Persists application update.
	 */
	private void persistApplicationUpdate(RequestInfo requestInfo, StreetVendingDetail detail) {
		log.info("Persisting update for application: {}", detail.getApplicationNo());
		StreetVendingRequest request = StreetVendingRequest.builder()
				.requestInfo(requestInfo)
				.streetVendingDetail(detail)
				.build();
		streetVendingRepository.update(request);
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
