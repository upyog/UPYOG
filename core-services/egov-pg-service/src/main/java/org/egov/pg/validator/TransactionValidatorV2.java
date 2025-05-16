package org.egov.pg.validator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.pg.web.models.TransactionCriteria;
import org.egov.pg.web.models.TransactionDetailsCriteria;
import org.egov.pg.web.models.TransactionRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.egov.pg.models.TaxAndPayment;
import org.egov.pg.models.Transaction;
import org.egov.pg.models.TransactionDetails;
import org.egov.pg.service.PaymentsService;
import org.egov.pg.repository.TransactionDetailsRepository;
import org.egov.pg.config.AppProperties;
import org.springframework.stereotype.Service;


@Service
public class TransactionValidatorV2 {
	
	@Autowired
	private AppProperties props;

	@Autowired
	private TransactionValidator validator;
	
	@Autowired
	private TransactionDetailsRepository transactionDetailsRepository;
	
	@Autowired
	private PaymentsService paymentsService;


	/**
	 * Validate the transaction, Check if gateway is available and active Check if
	 * module specific order id is unique
	 *
	 * @param transactionRequest txn object to be validated
	 */
	public void validateCreateTxn(TransactionRequest transactionRequest) {
		Map<String, String> errorMap = new HashMap<>();
		validator.isUserDetailPresent(transactionRequest, errorMap);
		validator.isGatewayActive(transactionRequest.getTransaction(), errorMap);
		validateIfTxnExistsForBill(transactionRequest, errorMap);
		validateTxnAmount(transactionRequest, errorMap);

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
		else {
			paymentsService.validatePayment(transactionRequest);
		}

	}
	
	public void validateTxnAmount(TransactionRequest transactionRequest,Map<String, String> errorMap) {
		Transaction txn = transactionRequest.getTransaction();
		BigDecimal totalPaid = BigDecimal.ZERO;

		for (TaxAndPayment taxAndPayment : txn.getTaxAndPayments()) {
			totalPaid = totalPaid.add(taxAndPayment.getAmountPaid());
		}
		if (totalPaid.compareTo(new BigDecimal(txn.getTxnAmount())) != 0)
			errorMap.put("TXN_CREATE_INVALID_TXN_AMT",
					"Transaction amount should be equal to sum of all " + " amountPaids in taxAndPayments");
	}

	private void validateIfTxnExistsForBill(TransactionRequest transactionRequest, Map<String, String> errorMap) {
		List<TransactionDetails> txnDetails = transactionRequest.getTransaction().getTransactionDetails();
		
		Set<String> billIds = txnDetails.stream()
			    .map(TransactionDetails::getBillId)
			    .collect(Collectors.toSet());
		
		TransactionDetailsCriteria criteria = TransactionDetailsCriteria.builder().billIds(billIds)
		   		            .build();
        List<TransactionDetails> existingTxnsForBill = transactionDetailsRepository.fetchTransactionDetails(criteria);
	        
	        for (TransactionDetails curr : existingTxnsForBill) {
				if (curr.getStatus().equals("PENDING")) {
					errorMap.put("TXN_ABRUPTLY_DISCARDED",
							"A transaction for this bill has been abruptly discarded, please retry after "
									+ (props.getEarlyReconcileJobRunInterval() * 2) + " mins");
				}
				if (curr.getStatus().equals("SUCCESS")) {
					errorMap.put("TXN_CREATE_BILL_ALREADY_PAID", "Bill has already been paid or is in pending state");
				}
			}

	        // You can do something with existingTxnsForBill here
	        System.out.println("Fetched " + existingTxnsForBill.size() + " transactions.");
//	    });
		
//		TransactionCriteria criteria = TransactionCriteria.builder().billId(txn.getBillId()).build();
//	
//		List<Transaction> existingTxnsForBill = transactionRepository.fetchTransactions(criteria);
	
//		for (Transaction curr : existingTxnsForBill) {
//			if (curr.getTxnStatus().equals(Transaction.TxnStatusEnum.PENDING)) {
//				errorMap.put("TXN_ABRUPTLY_DISCARDED",
//						"A transaction for this bill has been abruptly discarded, please retry after "
//								+ (props.getEarlyReconcileJobRunInterval() * 2) + " mins");
//			}
//			if (curr.getTxnStatus().equals(Transaction.TxnStatusEnum.SUCCESS)) {
//				errorMap.put("TXN_CREATE_BILL_ALREADY_PAID", "Bill has already been paid or is in pending state");
//			}
//		}
	
	}

}