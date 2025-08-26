package org.upyog.sv.service;

import static org.upyog.sv.web.models.RenewalStatus.RENEWED;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
import org.upyog.sv.web.models.PaymentScheduleStatus;
import org.upyog.sv.web.models.RenewalStatus;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;
import org.upyog.sv.web.models.VendorPaymentSchedule;
import org.upyog.sv.web.models.VendorPaymentScheduleRequest;
import org.upyog.sv.web.models.Workflow;
import org.upyog.sv.web.models.workflow.ProcessInstance;
import org.upyog.sv.web.models.workflow.ProcessInstanceRequest;
import org.upyog.sv.web.models.workflow.ProcessInstanceResponse;
import org.upyog.sv.web.models.workflow.State;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentRequest;
import digit.models.coremodels.UserDetailResponse;
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
	
	@Autowired
	private DemandService demandService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private StreetyVendingNotificationService notificationService;

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
			if (configs.getModuleName().equals(businessService)) {
				String applicationNo = paymentRequest.getPayment().getPaymentDetails().get(0).getBill()
						.getConsumerCode();
				log.info("Updating payment status for street vending booking : " + applicationNo);

				updateApplicationStatusAndWorkflow(paymentRequest);
				
			}
		String receiptNumber = paymentRequest.getPayment().getPaymentDetails().get(0).getReceiptNumber();
			if(receiptNumber.contains(StreetVendingConstants.MONTHLY) || receiptNumber.contains(StreetVendingConstants.QUATERLY)){
				updateVendorPaymentStatus(paymentRequest);
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
		String updatedStatus = "";

		log.info("Processing application: {}, renewal status: {}", appNo, detail.getRenewalStatus());

		// Handle null renewal status as a new application
		RenewalStatus renewalStatus = detail.getRenewalStatus();
		if (renewalStatus == null) {
			updatedStatus = updateNewApplication(detail, paymentRequest);
		} else {
			switch (renewalStatus) {
				case RENEW_IN_PROGRESS:
					updatedStatus = updateOldApplicationForRenewal(detail);
					break;
				case RENEW_APPLICATION_CREATED:
					updatedStatus = updateNewApplicationForRenewal(paymentRequest, detail);
					break;
			}
		}

		updateAuditFields(detail, requestInfo, StreetVendingUtil.getCurrentTimestamp(), updatedStatus);
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
	private String updateNewApplication(StreetVendingDetail detail, PaymentRequest paymentRequest) {
		log.info("Processing new application: {}", detail.getApplicationNo());
		State state = updateWorkflowStatus(paymentRequest);
		String applicationStatus = state.getApplicationStatus();

		detail.setValidityDateForPersisterDate(StreetVendingUtil.getCurrentDateFromYear(1).toString());
		enrichCertificateNumber(detail, paymentRequest.getRequestInfo(), detail.getTenantId());
		
		VendorPaymentSchedule schedule = enrichAndSetPaymentSchedule(detail);
		VendorPaymentScheduleRequest scheduleRequest = VendorPaymentScheduleRequest.builder()
			.requestInfo(paymentRequest.getRequestInfo())
			.vendorPaymentSchedules(Collections.singletonList(schedule))
			.build();

		streetVendingRepository.savePaymentSchedule(scheduleRequest);

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
	
	/**
	 * Constructs and returns a new {@link VendorPaymentSchedule} using information from the provided {@link StreetVendingDetail}.
	 *
	 * <p>
	 * The method initializes the payment schedule with:
	 * <ul>
	 *   <li>A randomly generated UUID as the schedule ID</li>
	 *   <li>The certificate number and vendor ID from the first element in the vendor details list</li>
	 *   <li>A due date set to one month from the current date</li>
	 *   <li>{@code null} values for {@code lastPaymentDate} and {@code paymentReceiptNo}</li>
	 *   <li>Status set to {@link PaymentScheduleStatus#PENDING_DEMAND_GENERATION}</li>
	 *   <li>Audit details copied from the {@code StreetVendingDetail}</li>
	 * </ul>
	 * </p>
	 *
	 * <p><b>Note:</b> It assumes that {@code detail.getVendorDetail()} is non-null and contains at least one item.</p>
	 *
	 * @param detail The {@link StreetVendingDetail} from which to extract data for creating the schedule.
	 * @return A fully initialized {@link VendorPaymentSchedule} instance.
	 */
	
	private VendorPaymentSchedule enrichAndSetPaymentSchedule(StreetVendingDetail detail) {
		
		return VendorPaymentSchedule.builder()
				.id(StreetVendingUtil.getRandonUUID())
				.certificateNo(detail.getCertificateNo())
				.vendorId(detail.getVendorDetail().get(0).getId())
				.applicationNo(detail.getApplicationNo())
				.lastPaymentDate(null)
				.dueDate(LocalDate.now().plusMonths(1))
				.paymentReceiptNo(null)
				.status(PaymentScheduleStatus.PENDING_DEMAND_GENERATION)
				.auditDetails(detail.getAuditDetails())
				.build();
	}
	
	/**
	 * Processes all vendor payment schedules that are due for demand generation.
	 *
	 * <p>
	 * This method retrieves a list of {@link VendorPaymentSchedule} objects that are due for demand
	 * generation, based on the current date and the {@link PaymentScheduleStatus#PENDING_DEMAND_GENERATION} status.
	 * For each due schedule, it invokes the {@link #processSchedule(VendorPaymentSchedule, PaymentRequest)} method
	 * to handle the payment processing.
	 * </p>
	 *
	 * @param paymentRequest The {@link PaymentRequest} containing details about the payment. This is passed to
	 *                       the {@link #processSchedule} method for further processing.
	 */
	
	public void processDueVendorPayments(PaymentRequest paymentRequest) {
		
		log.info("Scheduler started for vendor payment schedule inside processDueVendorPayments method");
		log.info("Payment Request " + paymentRequest);
		
	    List<VendorPaymentSchedule> dueSchedules = streetVendingRepository
	            .getVendorPayScheduleForDueDateAndStatus(LocalDate.now(), PaymentScheduleStatus.PENDING_DEMAND_GENERATION);

	    for (VendorPaymentSchedule schedule : dueSchedules) {
	        processSchedule(schedule, paymentRequest);
	    }
	}
	
	public void updateVendorPaymentStatus(PaymentRequest paymentRequest) {
		
		String applicationNo = paymentRequest.getPayment().getPaymentDetails().get(0).getBill()
				.getConsumerCode();
	    List<VendorPaymentSchedule> dueSchedules = streetVendingRepository
	            .getVendorPaymentScheduleApplication(applicationNo, PaymentScheduleStatus.PENDING_PAYMENT);
	    for (VendorPaymentSchedule schedule : dueSchedules) {
	        processSchedule(schedule, paymentRequest);
	    }
	}
	
	
	/**
	 * Processes the given {@link VendorPaymentSchedule} based on the presence of a {@link PaymentRequest}.
	 * <p>
	 * If the {@code paymentRequest} is {@code null}, this method treats it as a demand creation
	 * scenario and updates the schedule accordingly by creating a new demand and triggering notifications.
	 * If {@code paymentRequest} is provided, it marks the schedule as paid and processes creation
	 * of the next applicable schedule.
	 * </p>
	 *
	 * @param schedule        the vendor payment schedule to be processed. Must not be null.
	 * @param paymentRequest  the payment request object if the payment has been made; if null, it triggers demand creation.
	 */
	
	private void processSchedule(VendorPaymentSchedule schedule, PaymentRequest paymentRequest) {
	    log.info("Inside processSchedule method for payment schedule " + schedule);

	    List<StreetVendingDetail> detailList = getStreetVendingDetailByCertificateNo(schedule.getCertificateNo());

	    if (detailList == null || detailList.isEmpty()) {
	        log.warn("No StreetVendingDetail found for certificate: " + schedule.getCertificateNo());
	        return;
	    }

	    StreetVendingDetail detail = detailList.get(0);

	    if (paymentRequest == null) {
	        handleVendorPaymentSchedule(schedule, detail);
	    } else {
	        updateVendorSchedulePaymentStatus(schedule, detail);
	    }
	}

	/**
	 * Handles the vendor payment schedule when there is no existing payment request.
	 * <p>
	 * This method calculates the next due date based on the vendor's payment frequency
	 * and creates a new demand up to the actual end date (either the vendor's validity date or the next due date).
	 * It sets the schedule status to {@code PENDING_PAYMENT}, updates the schedule, initializes workflow if needed,
	 * and triggers notification processing for the vendor.
	 * </p>
	 *
	 * @param schedule the {@link VendorPaymentSchedule} to be updated with demand and status. Must not be null.
	 * @param detail   the {@link StreetVendingDetail} associated with the vendor's certificate. Must not be null.
	 */
	private void handleVendorPaymentSchedule(VendorPaymentSchedule schedule, StreetVendingDetail detail) {
	    LocalDate currentDueDate = schedule.getDueDate();
	    LocalDate validityDate = detail.getValidityDate();
	    int paymentCycleInMonths = getPaymentCycleInMonths(detail.getVendorPaymentFrequency());
	    LocalDate nextDueDate = currentDueDate.plusMonths(paymentCycleInMonths);

	    boolean isPartialCycle = validityDate.isBefore(nextDueDate);
	    LocalDate actualEndDate = isPartialCycle ? validityDate : nextDueDate;

	    UserDetailResponse systemUser = userService.searchByUserName(configs.getInternalMicroserviceUserName(), configs.getStateLevelTenantId());

	    if (systemUser == null || systemUser.getUser() == null || systemUser.getUser().isEmpty()) {
	        log.error("System user not found");
	        return;
	    }

	    StreetVendingRequest streetVendingRequest = StreetVendingRequest.builder()
	        .requestInfo(RequestInfo.builder().userInfo(systemUser.getUser().get(0)).build())
	        .streetVendingDetail(detail)
	        .build();

	    demandService.createDemand(streetVendingRequest, null);

	    schedule.setStatus(PaymentScheduleStatus.PENDING_PAYMENT);
	    updateSchedule(schedule);

	    if (detail.getWorkflow() == null) {
	        detail.setWorkflow(new Workflow());
	    }
	    detail.getWorkflow().setAction(StreetVendingConstants.ACTION_STATUS_SCHEDULE_PAYMENT);

	    notificationService.process(streetVendingRequest, null);
	}

	/**
	 * Updates the vendor payment schedule status to {@code PAID} and processes the creation
	 * of the next payment schedule if applicable.
	 * <p>
	 * This method is typically invoked when a payment has been successfully made.
	 * It first marks the current schedule as paid, persists the update, and then checks if
	 * a subsequent schedule needs to be generated based on the vendor's details.
	 * </p>
	 *
	 * @param schedule the {@link VendorPaymentSchedule} to mark as paid. Must not be null.
	 * @param detail   the {@link StreetVendingDetail} containing vendor information used for determining next schedule.
	 */
	
	private void updateVendorSchedulePaymentStatus(VendorPaymentSchedule schedule, StreetVendingDetail detail) {
	    schedule.setStatus(PaymentScheduleStatus.PAID);
	    updateSchedule(schedule);
	    createNextScheduleIfApplicable(schedule, detail);
	}

	
	/**
	 * Creates the next vendor payment schedule if the next due date falls within the vendor's validity period.
	 * <p>
	 * This method calculates the next due date based on the vendor's payment frequency and the current due date.
	 * If the next due date is before or equal to the validity date of the vendor, a new payment schedule is created
	 * and persisted with a status of {@code PENDING_DEMAND_GENERATION}.
	 *
	 * @param schedule the current {@link VendorPaymentSchedule} containing the latest payment information
	 * @param detail   the {@link StreetVendingDetail} containing vendor validity and payment frequency
	 */

	private void createNextScheduleIfApplicable(VendorPaymentSchedule schedule, StreetVendingDetail detail) {
	    LocalDate currentDueDate = schedule.getDueDate();
	    LocalDate validityDate = detail.getValidityDate();
	    int paymentCycleInMonths = getPaymentCycleInMonths(detail.getVendorPaymentFrequency());

	    LocalDate nextDueDate = currentDueDate.plusMonths(paymentCycleInMonths);

	    if (!validityDate.isBefore(nextDueDate)) {
	        VendorPaymentSchedule newSchedule = VendorPaymentSchedule.builder()
	            .id(StreetVendingUtil.getRandonUUID())
	            .vendorId(schedule.getVendorId())
	            .certificateNo(schedule.getCertificateNo())
	            .applicationNo(detail.getApplicationNo())
	            .lastPaymentDate(LocalDate.now())
	            .dueDate(nextDueDate)
	            .status(PaymentScheduleStatus.PENDING_DEMAND_GENERATION)
	            .build();

	        VendorPaymentScheduleRequest paymentSchedule = VendorPaymentScheduleRequest.builder()
	            .requestInfo(null)
	            .vendorPaymentSchedules(Arrays.asList(newSchedule))
	            .build();

	        streetVendingRepository.savePaymentSchedule(paymentSchedule);
	    }
	}

	/**
	 * Updates the status of a single vendor payment schedule in the repository.
	 *
	 * <p>
	 * Wraps the given {@link VendorPaymentSchedule} in a request object and delegates the update
	 * operation to the {@code streetVendingRepository}. This is typically used to update the
	 * status of a schedule to {@code PENDING_PAYMENT} or {@code PAID} based on its processing stage.
	 * </p>
	 *
	 * @param schedule The vendor payment schedule to be updated.
	 */
	
	private void updateSchedule(VendorPaymentSchedule schedule) {
	    VendorPaymentScheduleRequest updateSchedule = VendorPaymentScheduleRequest.builder()
	        .requestInfo(null)
	        .vendorPaymentSchedules(Arrays.asList(schedule))
	        .build();

	    streetVendingRepository.updatePaymentSchedule(updateSchedule);
	}


	/**
	 * Retrieves a list of {@link StreetVendingDetail} records associated with the given certificate number.
	 *
	 * <p>
	 * This method constructs a {@link StreetVendingSearchCriteria} using the provided certificate number
	 * and delegates the search to the {@code streetVendingRepository}. The returned list typically includes
	 * validity date and payment cycle details used for further demand and schedule processing.
	 * </p>
	 *
	 * @param certificateNo The certificate number used to search for street vending application details.
	 * @return A list of {@link StreetVendingDetail} objects matching the certificate number, or an empty list if none found.
	 */
	
	private List<StreetVendingDetail> getStreetVendingDetailByCertificateNo(String certificateNo) {
		StreetVendingSearchCriteria searchCriteria = StreetVendingSearchCriteria.builder().certificateNo(certificateNo)
				.build();

		return streetVendingRepository.getStreetVendingApplications(searchCriteria);
	}

	/**
	 * Converts a vendor's payment cycle string into its corresponding duration in months.
	 *
	 * <p>
	 * This method maps predefined payment frequency constants (e.g., "monthly", "quaterly", etc.)
	 * to the number of months they represent. If the input is {@code null} or does not match any
	 * known frequency, the method returns {@code 0}.
	 * </p>
	 *
	 * @param paymentCycle The vendor's payment frequency as a string. Expected values are defined in {@link StreetVendingConstants}.
	 * @return The number of months corresponding to the payment cycle, or {@code 0} if the cycle is unknown or null.
	 */
	
	private int getPaymentCycleInMonths(String paymentCycle) {
	    if (paymentCycle == null) return 0;
	    switch (paymentCycle) {
	        case StreetVendingConstants.MONTHLY:
	            return 1;
	        case StreetVendingConstants.QUATERLY:
	            return 3;
	        case StreetVendingConstants.HALFYEARLY:
	            return 6;
	        case StreetVendingConstants.YEARLY:
	            return 12;
	        default:
	            return 0;
	    }
	}


}
