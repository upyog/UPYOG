package org.upyog.chb.service;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;
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

	private final ObjectMapper mapper;
	private final CommunityHallBookingConfiguration configs;
	private final ServiceRequestRepository serviceRequestRepository;
	private final CommunityHallBookingService bookingService;
	private final CommunityHallBookingRepository bookingRepository;

	public PaymentNotificationService(ObjectMapper mapper,
			CommunityHallBookingConfiguration configs,
			ServiceRequestRepository serviceRequestRepository,
			CommunityHallBookingService bookingService,
			CommunityHallBookingRepository bookingRepository) {
		this.mapper = mapper;
		this.configs = configs;
		this.serviceRequestRepository = serviceRequestRepository;
		this.bookingService = bookingService;
		this.bookingRepository = bookingRepository;
	}

	public void process(Map<String, Object> paymentRecord, String topic) throws JsonProcessingException {
		log.info(" Receipt consumer class entry " + paymentRecord.toString() + " on topic " + topic);
		try {
			PaymentRequest paymentRequest = mapper.convertValue(paymentRecord, PaymentRequest.class);
			log.info("paymentRequest : " + paymentRequest);
			String businessService = paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService();
			log.info("Payment request processing in CHB method for businessService : " + businessService);
			if (configs.getBusinessServiceName()
					.equals(paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService())) {
				String bookingNo = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
				log.info("Updating payment status for CHB booking : " + bookingNo);
				
				log.info("Reciept no of payment : " + paymentRequest.getPayment().getPaymentDetails().get(0).getReceiptNumber());
				log.info("Payment date of payment : " + paymentRequest.getPayment().getPaymentDetails().get(0).getReceiptDate());
				VenueBookingDetail bookingDetail = VenueBookingDetail.builder().bookingNo(bookingNo)
						.build();
				VenueBookingRequest bookingRequest = VenueBookingRequest.builder()
						.requestInfo(paymentRequest.getRequestInfo()).venueBookingApplication(bookingDetail).build();
				
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
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		log.info(" URL for calling workflow service " + workflowReq.toString());
		Object workflow = serviceRequestRepository.fetchResult(url, workflowReq);
		ProcessInstanceResponse response = mapper.convertValue(workflow, ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}
	
	
   	public void processTransaction(Map<String, Object> paymentRecord, String topic, BookingStatusEnum status){

        TransactionRequest transactionRequest = mapper.convertValue(paymentRecord, TransactionRequest.class);

        RequestInfo requestInfo = transactionRequest.getRequestInfo();
        Transaction transaction = transactionRequest.getTransaction();
        
        log.info("Transaction in process transaction : " + transaction + " on topic " + topic);
        
        Transaction.TxnStatusEnum transactionStatus = transaction.getTxnStatus();
        String bookingNo = transaction.getConsumerCode();
        
        String moduleName = transaction.getModule();
        
        if(null == moduleName && null != bookingNo) {
        	moduleName = bookingNo.startsWith("CHB") ? configs.getBusinessServiceName() : null;
        }
        
        log.info("moduleName : " + moduleName + "  transactionStatus  : " + transactionStatus);
        
        if(configs.getBusinessServiceName()
				.equals(moduleName) && (Transaction.TxnStatusEnum.FAILURE.equals(transactionStatus) ||
						Transaction.TxnStatusEnum.PENDING.equals(transactionStatus))){
        	
        	if(Transaction.TxnStatusEnum.FAILURE.equals(transactionStatus)){
        		status = BookingStatusEnum.PAYMENT_FAILED;
        	}
        	
        	VenueBookingSearchCriteria bookingSearchCriteria = VenueBookingSearchCriteria.builder()
					.bookingNo(bookingNo).build();
			List<VenueBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
			if (bookingDetails.isEmpty()) {
				throw new CustomException("INVALID_BOOKING_CODE",
						"Booking no not valid. Failed to update booking status for : " + bookingNo);
			}
			VenueBookingDetail bookingDetail = bookingDetails.get(0);
        	
        	log.info("For booking no : " + bookingNo + " transaction id : " + transaction.getTxnId());
        	
			VenueBookingRequest bookingRequest = VenueBookingRequest.builder()
					.requestInfo(requestInfo).venueBookingApplication(bookingDetail).build();
			
			
			if(BookingStatusEnum.PAYMENT_FAILED.equals(status)) {
				bookingService.updateBookingSynchronously(bookingRequest, null, status, true);
			} else {
				bookingService.updateBookingSynchronously(bookingRequest, null, BookingStatusEnum.PENDING_FOR_PAYMENT, false);
				bookingRepository.updateBookingTimer(bookingDetail.getBookingId());
			}
            
        }
    }

}
