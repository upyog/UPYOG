package org.egov.refund.service;

import java.util.Comparator;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.refund.Repository.ServiceRequestRepository;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.model.Refund;
import org.egov.refund.util.PaymentWorkflowValidator;
import org.egov.refund.util.RefundConstants;
import org.egov.refund.web.contracat.Payment;
import org.egov.refund.web.contracat.PaymentRefundResponse;
import org.egov.refund.web.contracat.PaymentResponse;
import org.egov.refund.web.contracat.RefundPaymentResponse;
import org.egov.refund.web.factory.ResponseInfoFactory;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRefundService {

	
	private final PaymentWorkflowValidator paymentWorkflowValidator;
	private final PaymentGatewayService paymentService;
	private final ServiceRequestRepository serviceRequestRepository;
	private final ObjectMapper mapper;
	private final ApplicationProperties config;

	@Transactional
	public RefundPaymentResponse initiateRefund(Refund request, RequestInfo requestInfo) {

		Payment payment = getLatestPayment(request,requestInfo);

		paymentWorkflowValidator.validateForRefund(payment);

		PaymentRefundResponse refundResponse = null;

		if (RefundConstants.PAYMENT_MODE_ONLINE.equalsIgnoreCase(String.valueOf(payment.getPaymentMode()))) {

			refundResponse = paymentService.initiateRefund(payment, requestInfo);
		} else {

			throw new CustomException("INVALID_PAYMENT_MODE", "Refund is supported only for ONLINE payments.");
		}

		ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);

		return RefundPaymentResponse.builder().responseInfo(responseInfo)
				.paymentRefund(refundResponse.getPaymentRefund()).payments(List.of(payment)).build();
	}


	private Payment getLatestPayment(Refund refund, RequestInfo requestInfo) {
		StringBuilder url = new StringBuilder(config.getCollectionHost()).append(config.getCollectionSearch())
				.append("/").append(refund.getModuleName()).append("/_search").append("?tenantId=")
				.append(refund.getTenantId()).append("&ids=").append(refund.getPaymentId());

		Object responseObject = serviceRequestRepository.fetchResult(url, requestInfo);

		if (responseObject == null) {
			throw new CustomException("INVALID_WORKFLOW", "Empty workflow response received.");
		}

		PaymentResponse response = mapper.convertValue(responseObject, PaymentResponse.class);
		Payment payment = response.getPayments().stream().max(Comparator.comparingLong(Payment::getTransactionDate))
				.orElseThrow(
						() -> new CustomException("PAYMENT_NOT_FOUND", "No payment found for given consumer code."));
		return payment;

	}
}