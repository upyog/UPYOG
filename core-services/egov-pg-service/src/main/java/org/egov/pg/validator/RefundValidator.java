package org.egov.pg.validator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.pg.models.Refund;
import org.egov.pg.models.Transaction;
import org.egov.pg.repository.RefundRepository;
import org.egov.pg.web.models.RefundCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefundValidator {

	private RefundRepository repository;
    
	public RefundValidator(RefundRepository repository) {
		this.repository = repository;
	}
	
	
	public void validateTransaction(List<Transaction> transactions) {
		Map<String, String> errorMap = new HashMap<>();
		validateTxnId(transactions,errorMap);
		validateRefundNotAlreadyInitiated(transactions.get(0),errorMap);
	}

	private void validateTxnId(List<Transaction> transactions, Map<String, String> errorMap) {
		
		if (transactions == null || transactions.isEmpty()) {
			errorMap.put("TXN_NOT_FOUND", "No transaction found for given criteria");
		}
		
		Transaction txn = transactions.get(0);

		if (txn.getTxnId() == null || txn.getTxnId().isBlank()) {
		    errorMap.put("INVALID_TXN_ID", "Transaction ID is missing");
		}
		if (txn.getGatewayTxnId() == null || txn.getGatewayTxnId().isBlank()) {
			errorMap.put("INVALID_GATEWAY_TXN_ID", "Gateway Trasaction ID is missing");
		}

		if (txn.getConsumerCode() == null || txn.getConsumerCode().isBlank()) {
		    errorMap.put("INVALID_CONSUMER_CODE", "Consumer code is missing");
		}

		if (txn.getBillId() == null || txn.getBillId().isBlank()) {
		    errorMap.put("INVALID_BILL_ID", "Bill ID is missing");
		}
		
	}

	private void validateRefundNotAlreadyInitiated(Transaction txn, Map<String, String> errorMap) {
		RefundCriteria criteria = RefundCriteria.builder().originalTxnId(txn.getTxnId()).build();
		List<Refund> refundTransactions = repository.fetchRefundTransactions(criteria);
		if (!refundTransactions.isEmpty()) {
			for (Refund refund : refundTransactions) {
				if (refund.getStatus().equals(Refund.RefundStatusEnum.SUCCESS.toString())) {
					errorMap.put("REFUND_ALREADY_PAID", "Refund has already been paid");
				} else if (refund.getStatus().equals(Refund.RefundStatusEnum.PENDING.toString())) {
					errorMap.put("REFUND_ALREADY_INITIATED", "Already applied for refund for this payment");
				}
			}
		}
	}

//	private void validateRefundAmount(RefundRequest refundRequest, Map<String, String> errorMap) {
//		Refund refund = refundRequest.getRefund();
//		BigDecimal totalPaid = BigDecimal.ZERO;
//		BigDecimal originalAmt = new BigDecimal(refund.getOriginalAmount());
//		BigDecimal refundAmt = new BigDecimal(refund.getRefundAmount());
//
//		if (refund.getOriginalAmount() == null || refund.getRefundAmount() == null) {
//			errorMap.put("REFUND_CREATE_INVALID_REFUND_AMT", "Refund amount or Original should not be Null");
//		}
//
//		if (totalPaid.compareTo(new BigDecimal(refund.getOriginalAmount())) != 0)
//			errorMap.put("REFUND_CREATE_INVALID_REFUND_AMT", "Refund amount should be greater than 0");
//
//		if (refundAmt.compareTo(originalAmt) > 0) {
//			errorMap.put("REFUND_CREATE_INVALID_REFUND_AMT", "Refund Amount cannot be greater than originalAmount");
//		}
//
//	}

//	private void isGatewayActive(Refund refund, Map<String, String> errorMap) {
//		if (!gatewayService.isGatewayActive(refund.getGateway()))
//			errorMap.put("INVALID_PAYMENT_GATEWAY", "Invalid or inactive payment gateway provided");
//	}

	public Refund validateUpdateRefundTransaction(Map<String, String> params) {
		String refundId = params.get("refundId");
		if (refundId == null || refundId.isEmpty()) {
			throw new CustomException("INVALID_REQUEST", "refundId is mandatory");
		}
		RefundCriteria criteria = RefundCriteria.builder().refundId(refundId).build();
		List<Refund> refundTransactions = repository.fetchRefundTransactions(criteria);
		if (refundTransactions == null || refundTransactions.isEmpty()) {
			throw new CustomException("REFUND_ID_NOT_FOUND", "No refund transaction found for given criteria");

		}

		return refundTransactions.get(0);
	}

	public boolean skipGateway(Refund currentRefund) {
		
		return currentRefund.getRefundAmount().compareTo(BigDecimal.ZERO) == 0;
	}

}
