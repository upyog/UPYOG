package org.upyog.chb.service;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.transaction.Transaction;
import org.upyog.chb.web.models.transaction.TransactionRequest;
import org.upyog.chb.web.models.workflow.ProcessInstance;
import org.upyog.chb.web.models.workflow.ProcessInstanceRequest;
import org.upyog.chb.web.models.workflow.ProcessInstanceResponse;
import org.upyog.chb.web.models.workflow.State;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentNotificationService {

	@Autowired
	private ObjectMapper mapper;

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsUrl;

	@Autowired
	private CommunityHallBookingConfiguration configs;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private CommunityHallBookingService bookingService;
	
	@Autowired
	private CommunityHallBookingRepository bookingRepository;

	/**
	 * This service class handles payment-related notifications in the
	 * Community Hall Booking module.
	 * 
	 * Purpose:
	 * - To send notifications to users based on payment events such as successful payments or failures.
	 * - To update booking statuses based on payment outcomes.
	 * 
	 * Dependencies:
	 * - CommunityHallBookingRepository: Handles database operations for bookings.
	 * - ServiceRequestRepository: Sends HTTP requests to external services for payment updates.
	 * - CommunityHallBookingConfiguration: Provides configuration properties for payment notifications.
	 * - ObjectMapper: Serializes and deserializes JSON objects for requests and responses.
	 * 
	 * Features:
	 * - Processes payment events and updates booking statuses accordingly.
	 * - Sends notifications to users about payment success or failure.
	 * - Fetches booking details and validates payment-related data.
	 * - Logs payment processing and notification details for debugging and monitoring purposes.
	 * 
	 * Methods:
	 * 1. processPaymentNotification:
	 *    - Processes payment events and updates the booking status.
	 *    - Sends notifications to users based on the payment outcome.
	 * 
	 * 2. updateBookingStatus:
	 *    - Updates the booking status in the database based on the payment event.
	 * 
	 * 3. sendNotification:
	 *    - Sends payment-related notifications to users via configured channels.
	 * 
	 * Usage:
	 * - This class is automatically managed by Spring and injected wherever payment notification
	 *   operations are required.
	 * - It ensures consistent and reusable logic for handling payment notifications in the module.
	 */
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
			log.info("Payment request processing in CHB method for businessService : " + businessService);
			if (configs.getBusinessServiceName()
					.equals(paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService())) {
				String bookingNo = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
				log.info("Updating payment status for CHB booking : " + bookingNo);
				/**
				 * Workflow will come into picture once hall location is changes or booking is
				 * cancelled otherwise after payment booking will be auto approved
				 * 
				 */
				
				log.info("Reciept no of payment : " + paymentRequest.getPayment().getPaymentDetails().get(0).getReceiptNumber());
				log.info("Payment date of payment : " + paymentRequest.getPayment().getPaymentDetails().get(0).getReceiptDate());
				CommunityHallBookingDetail bookingDetail = CommunityHallBookingDetail.builder().bookingNo(bookingNo)
						.build();
				CommunityHallBookingRequest bookingRequest = CommunityHallBookingRequest.builder()
						.requestInfo(paymentRequest.getRequestInfo()).hallsBookingApplication(bookingDetail).build();
				
				//now updating booking status directly using jdbc template
				//deleting booking timer
				bookingService.updateBookingSynchronously(bookingRequest, paymentRequest.getPayment().getPaymentDetails().get(0), BookingStatusEnum.BOOKED,
						true);
				
			}
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument exception occured while sending notification CHB : " + e.getMessage());
		} catch (Exception e) {
			log.error("An unexpected exception occurred while sending notification CHB : ", e);
		}

	}

	public void updateWorkflowStatus(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = getProcessInstanceForCHB(paymentRequest);
		log.info(" Process instance of chb application " + processInstance.toString());
		ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(paymentRequest.getRequestInfo(),
				Collections.singletonList(processInstance));
		callWorkFlow(workflowRequest);

	}

	private ProcessInstance getProcessInstanceForCHB(PaymentRequest paymentRequest) {

		ProcessInstance processInstance = new ProcessInstance();
		processInstance
				.setBusinessId(paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode());
		processInstance.setAction("PAY");
		processInstance.setModuleName(configs.getModuleName());
		processInstance.setTenantId(paymentRequest.getPayment().getTenantId());
		processInstance.setBusinessService(configs.getBusinessServiceName());
		processInstance.setDocuments(null);
		processInstance.setComment(null);
		processInstance.setAssignes(null);

		return processInstance;

	}

	public State callWorkFlow(ProcessInstanceRequest workflowReq) {
		log.info(" Workflow Request for CHB service for final step " + workflowReq.toString());
		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		log.info(" URL for calling workflow service " + workflowReq.toString());
		Object workflow = serviceRequestRepository.fetchResult(url, workflowReq);
		response = mapper.convertValue(workflow, ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}
	
	
   	public void processTransaction(HashMap<String, Object> record, String topic, BookingStatusEnum status){

        TransactionRequest transactionRequest = mapper.convertValue(record, TransactionRequest.class);

        RequestInfo requestInfo = transactionRequest.getRequestInfo();
        Transaction transaction = transactionRequest.getTransaction();
        
        log.info("Transaction in process transaction : " + transaction);
        
        Transaction.TxnStatusEnum transactionStatus = transaction.getTxnStatus();
        String bookingNo = transaction.getConsumerCode();
        
        String moduleName = transaction.getModule();
        
        //Payment failure status JSON does not contain module name so added this condition
        if(null == moduleName && null != bookingNo) {
        	//Update module name from consumer code
        	moduleName = bookingNo.startsWith("CHB") ? configs.getBusinessServiceName() : null;
        }
        
        log.info("moduleName : " + moduleName + "  transactionStatus  : " + transactionStatus);
        
        if(configs.getBusinessServiceName()
				.equals(moduleName) && (Transaction.TxnStatusEnum.FAILURE.equals(transactionStatus) ||
						Transaction.TxnStatusEnum.PENDING.equals(transactionStatus))){
        	
        	if(Transaction.TxnStatusEnum.FAILURE.equals(transactionStatus)){
        		status = BookingStatusEnum.PAYMENT_FAILED;
        	}
        	
        	CommunityHallBookingSearchCriteria bookingSearchCriteria = CommunityHallBookingSearchCriteria.builder()
					.bookingNo(bookingNo).build();
			List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
			if (bookingDetails.size() == 0) {
				throw new CustomException("INVALID_BOOKING_CODE",
						"Booking no not valid. Failed to update booking status for : " + bookingNo);
			}
			CommunityHallBookingDetail bookingDetail = bookingDetails.get(0);
        	
        	log.info("For booking no : " + bookingNo + " transaction id : " + transaction.getTxnId());
        	
        	
			CommunityHallBookingRequest bookingRequest = CommunityHallBookingRequest.builder()
					.requestInfo(requestInfo).hallsBookingApplication(bookingDetail).build();
			
			
			if(BookingStatusEnum.PAYMENT_FAILED.equals(status)) {
				bookingService.updateBookingSynchronously(bookingRequest, null, status, true);
			} else {
				bookingService.updateBookingSynchronously(bookingRequest, null, BookingStatusEnum.PENDING_FOR_PAYMENT, false);
				bookingRepository.updateBookingTimer(bookingDetail.getBookingId());
			}
			
            
        }
    }

}
