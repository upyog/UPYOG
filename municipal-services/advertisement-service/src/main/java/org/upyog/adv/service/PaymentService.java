package org.upyog.adv.service;


import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.repository.impl.BookingRepositoryImpl;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.transaction.Transaction;
import org.upyog.adv.web.models.transaction.TransactionRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for handling payment-related operations in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Processes payments for advertisement bookings.
 * - Updates booking statuses based on payment success or failure.
 * - Interacts with external services for payment processing and transaction management.
 * 
 * Dependencies:
 * - ObjectMapper: Used for JSON serialization and deserialization.
 * - BookingConfiguration: Provides configuration properties for payment processing.
 * - BookingService: Handles booking-related operations.
 * - BookingRepository: Interacts with the database for booking updates.
 * - ServiceRequestRepository: Makes external service requests for payment and transaction processing.
 * 
 * Methods:
 * - Processes payment requests and updates booking details accordingly.
 * - Handles payment success and failure scenarios.
 * - Fetches and validates payment-related data from external services.
 * 
 * Annotations:
 * - @Service: Marks this class as a Spring-managed service component.
 * - @Slf4j: Enables logging for debugging and monitoring payment processes.
 */
@Slf4j
@Service
public class PaymentService {

	private final ObjectMapper mapper;

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsUrl;

	private final BookingConfiguration configs;
	private final BookingService bookingService;
	private final BookingRepositoryImpl bookingRepo;
	private final PaymentTimerService paymentTimerService;

	public PaymentService(ObjectMapper mapper, BookingConfiguration configs, BookingService bookingService,
			BookingRepositoryImpl bookingRepo, PaymentTimerService paymentTimerService) {
		this.mapper = mapper;
		this.configs = configs;
		this.bookingService = bookingService;
		this.bookingRepo = bookingRepo;
		this.paymentTimerService = paymentTimerService;
	}

	/**
	 * Processes payment notification records from the receipt consumer.
	 *
	 * <p>When a payment record belongs to an advertisement booking, this method
	 * updates the booking status and removes the associated timer hold.</p>
	 *
	 * @param messagePayload raw payment notification data
	 * @param topic Kafka topic name or source identifier
	 * @throws JsonProcessingException when message parsing fails
	 */
	public void process(Map<String, Object> messagePayload, String topic) throws JsonProcessingException {
		log.info(" Receipt consumer class entry " + messagePayload.toString());
		try {
			PaymentRequest paymentRequest = mapper.convertValue(messagePayload, PaymentRequest.class);
			log.info("paymentRequest : " + paymentRequest);
			String businessService = paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService();
			log.info("Payment request processing in ADV method for businessService : " + businessService);
			if (configs.getBusinessServiceName()
					.equals(paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService())) {
				String bookingNo = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
				log.info("Updating payment status for ADV booking : " + bookingNo);
				log.info("Reciept no of payment : " + paymentRequest.getPayment().getPaymentDetails().get(0).getReceiptNumber());
				log.info("Payment date of payment : " + paymentRequest.getPayment().getPaymentDetails().get(0).getReceiptDate());
				BookingDetail bookingDetail = BookingDetail.builder().bookingNo(bookingNo)
						.build();
				BookingRequest bookingRequest = BookingRequest.builder()
						.requestInfo(paymentRequest.getRequestInfo()).bookingApplication(bookingDetail).build();
				bookingService.updateBookingSynchronously(bookingRequest, paymentRequest.getPayment().getPaymentDetails().get(0), BookingStatusEnum.BOOKED);
				paymentTimerService.deleteBookingIdForTimer(bookingRequest.getBookingApplication().getBookingId(),
						paymentRequest.getRequestInfo());
			}
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument exception occured while sending notification ADV : " + e.getMessage());
		} catch (Exception e) {
			log.error("An unexpected exception occurred while sending notification ADV : ", e);
		}

	}

	/**
	 * Processes transaction messages to update booking status based on payment result.
	 *
	 * <p>This method handles both failure and pending payment transaction states,
	 * and keeps timer-aware booking records in sync when payment is not completed.</p>
	 *
	 * @param messagePayload raw transaction message data
	 * @param topic message source or topic name
	 * @param status expected booking status to apply while processing
	 */
	public void processTransaction(Map<String, Object> messagePayload, String topic, BookingStatusEnum status) {

		TransactionRequest transactionRequest = mapper.convertValue(messagePayload, TransactionRequest.class);

		RequestInfo requestInfo = transactionRequest.getRequestInfo();
		Transaction transaction = transactionRequest.getTransaction();

		log.info("Transaction in process transaction : " + transaction);

		Transaction.TxnStatusEnum transactionStatus = transaction.getTxnStatus();
		String bookingNo = transaction.getConsumerCode();

		String moduleName = transaction.getModule();

		if (null == moduleName && null != bookingNo) {
			moduleName = bookingNo.startsWith("ADV") ? configs.getBusinessServiceName() : null;
		}

		log.info("moduleName : " + moduleName + "  transactionStatus  : " + transactionStatus);

		if (configs.getBusinessServiceName().equals(moduleName)
				&& (Transaction.TxnStatusEnum.FAILURE.equals(transactionStatus)
						|| Transaction.TxnStatusEnum.PENDING.equals(transactionStatus))) {

			if (Transaction.TxnStatusEnum.FAILURE.equals(transactionStatus)) {
				status = BookingStatusEnum.PAYMENT_FAILED;

				bookingRepo.updateStatusForTimer(BookingStatusEnum.PAYMENT_FAILED.toString(), bookingNo);
			}

			if (Transaction.TxnStatusEnum.PENDING.equals(transactionStatus)) {

				bookingRepo.updateStatusForTimer(BookingStatusEnum.PENDING_FOR_PAYMENT.toString(), bookingNo);
			}
			log.info("For booking no : " + bookingNo + " transaction id : " + transaction.getTxnId());

			BookingDetail bookingDetail = BookingDetail.builder().bookingNo(bookingNo).build();
			BookingRequest bookingRequest = BookingRequest.builder().requestInfo(requestInfo)
					.bookingApplication(bookingDetail).build();
			bookingService.updateBooking(bookingRequest, null, status);

		}
	}

}
