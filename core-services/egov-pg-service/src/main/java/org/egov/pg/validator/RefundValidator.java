package org.egov.pg.validator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.egov.pg.models.Refund;
import org.egov.pg.models.RefundRequest;
import org.egov.pg.models.Transaction;
import org.egov.pg.repository.RefundRepository;
import org.egov.pg.repository.TransactionRepository;
import org.egov.pg.service.GatewayService;
import org.egov.pg.web.models.RefundCriteria;
import org.egov.pg.web.models.TransactionCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefundValidator {

	private GatewayService gatewayService;
	private RefundRepository repository;
    private TransactionRepository transactionRepository;
    
	@Autowired
	public RefundValidator(GatewayService gatewayService, RefundRepository repository,TransactionRepository transactionRepository) {
		this.gatewayService = gatewayService;
		this.repository = repository;
        this.transactionRepository = transactionRepository;

	}

	public void validateInitiateRefundRequest(@Valid RefundRequest refundRequest) {
		Refund refundTxn = refundRequest.getRefund();
		Map<String, String> errorMap = new HashMap<>();
		validateRefundAmount(refundRequest, errorMap);
		isGatewayActive(refundRequest.getRefund(), errorMap);
		validateRefundStatus(refundTxn, errorMap);
		validateTxnId(refundTxn, errorMap);
	}

	private void validateTxnId(Refund refundTxn, Map<String, String> errorMap) {
		TransactionCriteria criteria = TransactionCriteria.builder().txnId(refundTxn.getOriginalTxnId()).build();
		List<Transaction> statuses = transactionRepository.fetchTransactions(criteria);

		if (statuses == null || statuses.isEmpty()) {
			errorMap.put("TXN_NOT_FOUND", "No transaction found for given criteria");
		}
		
	}

	private void validateRefundStatus(Refund refundTxn, Map<String, String> errorMap) {
		RefundCriteria criteria = RefundCriteria.builder().originalTxnId(refundTxn.getOriginalTxnId()).build();
		List<Refund> fetchRefundTransactions = repository.fetchRefundTransactions(criteria);
		for (Refund refund : fetchRefundTransactions) {
			if (refund.getStatus().equals(Refund.RefundStatusEnum.SUCCESS.toString())) {
				errorMap.put("REFUND_ALREADY_PAID", "Refund has already been paid");
			}
		}
	}

	private void validateRefundAmount(RefundRequest refundRequest, Map<String, String> errorMap) {
		Refund refund = refundRequest.getRefund();
		BigDecimal totalPaid = BigDecimal.ZERO;
		BigDecimal originalAmt = new BigDecimal(refund.getOriginalAmount());
		BigDecimal refundAmt = new BigDecimal(refund.getRefundAmount());

		if (refund.getOriginalAmount() == null || refund.getRefundAmount() == null) {
			errorMap.put("REFUND_CREATE_INVALID_REFUND_AMT", "Refund amount or Original should not be Null");
		}

		if (totalPaid.compareTo(new BigDecimal(refund.getOriginalAmount())) != 0)
			errorMap.put("REFUND_CREATE_INVALID_REFUND_AMT", "Refund amount should be greater than 0");

		if (refundAmt.compareTo(originalAmt) > 0) {
			errorMap.put("REFUND_CREATE_INVALID_REFUND_AMT", "Refund Amount cannot be greater than originalAmount");
		}

	}

	private void isGatewayActive(Refund refund, Map<String, String> errorMap) {
		if (!gatewayService.isGatewayActive(refund.getGateway()))
			errorMap.put("INVALID_PAYMENT_GATEWAY", "Invalid or inactive payment gateway provided");
	}

}
