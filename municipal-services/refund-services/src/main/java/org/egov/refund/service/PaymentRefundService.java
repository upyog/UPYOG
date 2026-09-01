package org.egov.refund.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.refund.Repository.PaymentRepository;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.util.PaymentWorkflowValidator;
import org.egov.refund.util.RefundConstants;
import org.egov.refund.web.contracat.Payment;
import org.egov.refund.web.contracat.PaymentRefundResponse;
import org.egov.refund.web.contracat.PaymentSearchCriteria;
import org.egov.refund.web.contracat.PaymentWorkflow;
import org.egov.refund.web.contracat.PaymentWorkflow.PaymentAction;
import org.egov.refund.web.contracat.PaymentWorkflowRequest;
import org.egov.refund.web.contracat.RefundPaymentResponse;
import org.egov.refund.web.factory.ResponseInfoFactory;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRefundService {

	private final PaymentRepository paymentRepository;
	private final PaymentWorkflowValidator paymentWorkflowValidator;
	private final PaymentGatewayService paymentService;
	private final ApplicationProperties applicationProperties;

	@Transactional
	public RefundPaymentResponse initiateRefund(PaymentWorkflowRequest request) {

		validateRequest(request);

		PaymentAction action = request.getPaymentWorkflows().get(0).getAction();
		String tenantId = request.getPaymentWorkflows().get(0).getTenantId();

		Map<String, PaymentWorkflow> workflowMap = new HashMap<>();
		Set<String> paymentIds = new HashSet<>();

		prepareWorkflowData(request.getPaymentWorkflows(), workflowMap, paymentIds);

		List<Payment> payments = paymentRepository.fetchPayments(PaymentSearchCriteria.builder().ids(paymentIds)
				.tenantId(tenantId).offset(0).limit(applicationProperties.getSearchDefaultLimit()).build());

		Set<String> consumerCodes = extractConsumerCodes(payments);

		switch (action) {

		case REFUND:
			return processRefund(workflowMap, consumerCodes, tenantId, request);

		default:
			throw new CustomException("INVALID_ACTION", "Unsupported workflow action : " + action);
		}
	}

	private RefundPaymentResponse processRefund(Map<String, PaymentWorkflow> workflowMap, Set<String> consumerCodes,
			String tenantId, PaymentWorkflowRequest request) {

		Payment payment = getLatestPayment(consumerCodes, tenantId);

		paymentWorkflowValidator.validateForRefund(List.copyOf(workflowMap.values()), payment);

		PaymentRefundResponse refundResponse = null;

		if (RefundConstants.PAYMENT_MODE_ONLINE.equalsIgnoreCase(String.valueOf(payment.getPaymentMode()))) {

			refundResponse = paymentService.initiateRefund(payment, request.getRequestInfo());

		} else {

			throw new CustomException("INVALID_PAYMENT_MODE", "Refund is supported only for ONLINE payments.");
		}

		ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
				true);

		return RefundPaymentResponse.builder().responseInfo(responseInfo)
				.paymentRefund(refundResponse.getPaymentRefund()).payments(List.of(payment)).build();
	}

	private void validateRequest(PaymentWorkflowRequest request) {

		if (request == null || request.getPaymentWorkflows() == null || request.getPaymentWorkflows().isEmpty()) {

			throw new CustomException("INVALID_REQUEST", "Payment workflow request cannot be empty.");
		}
	}

	private void prepareWorkflowData(List<PaymentWorkflow> workflows, Map<String, PaymentWorkflow> workflowMap,
			Set<String> paymentIds) {

		PaymentAction action = workflows.get(0).getAction();
		String tenantId = workflows.get(0).getTenantId();

		for (PaymentWorkflow workflow : workflows) {

			if (!action.equals(workflow.getAction())) {
				throw new CustomException("PAYMENT_WORKFLOW_SINGLE_ACTION_ALLOWED",
						"All workflow requests must have the same action.");
			}

			if (!tenantId.equalsIgnoreCase(workflow.getTenantId())) {
				throw new CustomException("CROSS_TENANT_OP_NOT_ALLOWED", "Cross tenant operation is not allowed.");
			}

			paymentIds.add(workflow.getPaymentId());
			workflowMap.put(workflow.getPaymentId(), workflow);
		}
	}

	private Set<String> extractConsumerCodes(List<Payment> payments) {

		Set<String> consumerCodes = new HashSet<>();

		payments.forEach(payment -> payment.getPaymentDetails()
				.forEach(detail -> consumerCodes.add(detail.getBill().getConsumerCode())));

		return consumerCodes;
	}

	private Payment getLatestPayment(Set<String> consumerCodes, String tenantId) {

		return paymentRepository
				.fetchPayments(PaymentSearchCriteria.builder().tenantId(tenantId).consumerCodes(consumerCodes).build())
				.stream().max(Comparator.comparingLong(Payment::getTransactionDate)).orElseThrow(
						() -> new CustomException("PAYMENT_NOT_FOUND", "No payment found for given consumer code."));
	}
}