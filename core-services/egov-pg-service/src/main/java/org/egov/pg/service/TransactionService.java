package org.egov.pg.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.config.AppProperties;
import org.egov.pg.models.Bill;
import org.egov.pg.models.BillDetail;
import org.egov.pg.models.CollectionPayment;
import org.egov.pg.models.CollectionPaymentDetail;
import org.egov.pg.models.Transaction;
import org.egov.pg.models.TransactionDetails;
import org.egov.pg.models.TransactionDetailsWrapper;
import org.egov.pg.models.TransactionDump;
import org.egov.pg.models.TransactionDumpRequest;
import org.egov.pg.producer.Producer;
import org.egov.pg.repository.TransactionDetailsRepository;
import org.egov.pg.repository.TransactionRepository;
import org.egov.pg.validator.TransactionValidator;
import org.egov.pg.web.models.TransactionCriteria;
import org.egov.pg.web.models.TransactionCriteriaV2;
import org.egov.pg.web.models.TransactionDetailsCriteria;
import org.egov.pg.web.models.TransactionRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import lombok.extern.slf4j.Slf4j;

/**
 * Handles all transaction related requests
 */
@Service
@Slf4j
public class TransactionService {
	
	@Autowired
	private TransactionDetailsRepository transactionDetailsRepository;

	private TransactionValidator validator;
	private GatewayService gatewayService;
	private Producer producer;
	private EnrichmentService enrichmentService;
	private AppProperties appProperties;
	private TransactionRepository transactionRepository;
	private PaymentsService paymentsService;

	@Autowired
	TransactionService(TransactionValidator validator, GatewayService gatewayService, Producer producer,
			TransactionRepository transactionRepository, PaymentsService paymentsService,
			EnrichmentService enrichmentService, AppProperties appProperties) {
		this.validator = validator;
		this.gatewayService = gatewayService;
		this.producer = producer;
		this.transactionRepository = transactionRepository;
		this.paymentsService = paymentsService;
		this.enrichmentService = enrichmentService;
		this.appProperties = appProperties;
	}

	/**
	 * Initiates a transaction by generating a gateway redirect URI for the request
	 * <p>
	 * 1. Validates transaction object 2. Enriches the request by assigning a TxnID
	 * and a default status of PENDING 3. If yes, calls the gateway's implementation
	 * to generate a redirect URI 4. Persists the transaction and a transaction dump
	 * with the RAW requests asynchronously 5. Returns the redirect URI
	 *
	 * @param transactionRequest Valid transaction request for which transaction
	 *                           needs to be initiated
	 * @return Redirect URI to the gateway for the particular transaction
	 * @throws RazorpayException 
	 */
	public Transaction initiateTransaction(TransactionRequest transactionRequest) throws CustomException {
		validator.validateCreateTxn(transactionRequest);
		// Enrich transaction by generating txnid, audit details, default status
		enrichmentService.enrichCreateTransaction(transactionRequest);

		RequestInfo requestInfo = transactionRequest.getRequestInfo();
		Transaction transaction = transactionRequest.getTransaction();

		TransactionDump dump = TransactionDump.builder().txnId(transaction.getTxnId())
				.auditDetails(transaction.getAuditDetails()).build();

		if (validator.skipGateway(transaction)) {
			transaction.setTxnStatus(Transaction.TxnStatusEnum.SUCCESS);
			paymentsService.registerPayment(transactionRequest);
		} else {
			URI uri = gatewayService.initiateTxn(transaction);
			transaction.setRedirectUrl(uri.toString());

			dump.setTxnRequest(uri.toString());
		}

		// Persist transaction and transaction dump objects
		producer.push(appProperties.getSaveTxnTopic(),
				new org.egov.pg.models.TransactionRequest(requestInfo, transaction));
		producer.push(appProperties.getSaveTxnDumpTopic(), new TransactionDumpRequest(requestInfo, dump));
		
		if (CollectionUtils.isEmpty(transaction.getTransactionDetails())) {
			// populate transaction Details object
			transaction.setTransactionDetails(
					Collections.singletonList(TransactionDetails.builder().txnAmount(transaction.getTxnAmount())
							.billId(transaction.getBillId()).consumerCode(transaction.getConsumerCode()).build()));
		}

		// Enrich transaction Details by generating uuid, txnid, audit details and
		// additional details
		if (!CollectionUtils.isEmpty(transaction.getTransactionDetails())) {
			enrichmentService.populateTransactionsDetails(transaction, requestInfo);
			transaction.getTransactionDetails().forEach(transactionDetails -> {
				producer.push(appProperties.getSaveTxnDetailsTopic(),
						TransactionDetailsWrapper.builder().transactionDetails(transactionDetails).build());
			});
		}

		return transaction;
	}

	/**
	 * Fetches a list of transactions matching the current criteria
	 * <p>
	 * Currently has a hard limit of 5 records, configurable
	 *
	 * @param transactionCriteria Search Conditions that should be matched
	 * @return List of transactions matching the conditions.
	 */
	public List<Transaction> getTransactions(TransactionCriteria transactionCriteria) {
		log.info(transactionCriteria.toString());
		List<Transaction> transactions = new ArrayList<>();
		try {
			if (!StringUtils.isEmpty(transactionCriteria.getBillId())) {
				TransactionDetailsCriteria transactionDetailsCriteria = TransactionDetailsCriteria.builder()
						.billIds(Collections.singleton(transactionCriteria.getBillId())).build();

				List<TransactionDetails> transactionDetails = transactionDetailsRepository
						.fetchTransactionDetails(transactionDetailsCriteria);

				Set<String> txnIds = transactionDetails.stream().map(TransactionDetails::getTxnId)
						.collect(Collectors.toSet());
				Map<String, List<TransactionDetails>> transactionDetailsMap = transactionDetails.stream()
						.collect(Collectors.groupingBy(TransactionDetails::getTxnId));

				if (!CollectionUtils.isEmpty(txnIds)) {
					TransactionCriteriaV2 criteriaV2 = TransactionCriteriaV2.builder().txnIds(txnIds).build();
					transactions = transactionRepository.fetchTransactions(criteriaV2);
				}

				transactions.forEach(transaction -> {
					transaction.setTransactionDetails(transactionDetailsMap.get(transaction.getTxnId()));
				});
			} else {
				transactions = transactionRepository.fetchTransactions(transactionCriteria);

				mapTransactionDetails(transactions);
			}

			return transactions;
		} catch (DataAccessException e) {
			log.error("Unable to fetch data from the database for criteria: " + transactionCriteria.toString(), e);
			throw new CustomException("FETCH_TXNS_FAILED", "Unable to fetch transactions from store");
		}
	}

	/**
	 * Updates the status of the transaction from the gateway
	 * <p>
	 * 1. Fetch TXN ID from the request params, if not found, exit! 2. Fetch current
	 * transaction status from DB, if not found, exit! 3. Fetch the current
	 * transaction status from the payment gateway 4. Verify the amount returned
	 * from the gateway matches our records 5. If successful, generate receipt 6.
	 * Persist the updated transaction status and raw gateway transaction response
	 *
	 * @param requestInfo
	 * @param requestParams Response parameters posted by the gateway
	 * @return Updated transaction
	 */
	public List<Transaction> updateTransaction(RequestInfo requestInfo, Map<String, String> requestParams) {

		Transaction currentTxnStatus = validator.validateUpdateTxn(requestParams);

		log.debug(currentTxnStatus.toString());
		log.debug(requestParams.toString());

		Transaction newTxn = null;

		if (validator.skipGateway(currentTxnStatus)) {
			newTxn = currentTxnStatus;

		} else {
			newTxn = gatewayService.getLiveStatus(currentTxnStatus, requestParams);

			// Enrich the new transaction status before persisting
			enrichmentService.enrichUpdateTransaction(new TransactionRequest(requestInfo, currentTxnStatus), newTxn);
		}

		// Check if transaction is successful, amount matches etc
		if (validator.shouldGenerateReceipt(currentTxnStatus, newTxn)) {
			TransactionRequest request = TransactionRequest.builder().requestInfo(requestInfo).transaction(newTxn)
					.build();
			paymentsService.registerPayment(request);
		}

		TransactionDump dump = TransactionDump.builder().txnId(currentTxnStatus.getTxnId())
				.txnResponse(newTxn.getResponseJson()).auditDetails(newTxn.getAuditDetails()).build();

		producer.push(appProperties.getUpdateTxnTopic(),
				new org.egov.pg.models.TransactionRequest(requestInfo, newTxn));
		producer.push(appProperties.getUpdateTxnDumpTopic(), new TransactionDumpRequest(requestInfo, dump));

		// update demands and bill
		updateDemandsAndBillByTransactionDetails(newTxn, requestInfo);
		
		mapTransactionDetails(Collections.singletonList(newTxn));
		
		return Collections.singletonList(newTxn);
	}
	
	private void mapTransactionDetails(List<Transaction> transactions) {
		Set<String> txnIdsToFetch = transactions.stream().map(Transaction::getTxnId).collect(Collectors.toSet());

		if (!txnIdsToFetch.isEmpty()) {
			TransactionDetailsCriteria transactionDetailsCriteria = TransactionDetailsCriteria.builder()
					.txnIds(txnIdsToFetch).build();
			List<TransactionDetails> transactionDetails = transactionDetailsRepository
					.fetchTransactionDetails(transactionDetailsCriteria);

			Map<String, List<TransactionDetails>> transactionDetailsMap = transactionDetails.stream()
					.collect(Collectors.groupingBy(TransactionDetails::getTxnId));

			transactions.forEach(transaction -> {
				transaction.setTransactionDetails(transactionDetailsMap.get(transaction.getTxnId()));
			});
		}
	}
	
	private void updateDemandsAndBillByTransactionDetails(Transaction newTxn, RequestInfo requestInfo) {
		
		if (StringUtils.equalsIgnoreCase(newTxn.getTxnStatus().name(), "SUCCESS")) {
			
			// make request
			Map<String, Object> inputs = new HashMap();
			inputs.put("tenantId", newTxn.getTenantId());
			inputs.put("businessService", newTxn.getModule());
			inputs.put("consumerCode", newTxn.getTenantId());
			inputs.put("isPaymentCompleted", true);
			inputs.put("txnAmount", newTxn.getTxnAmount());
			
			Map<String, Object> request = new HashMap<>();
			request.put("requestInfo", requestInfo);
			request.put("inputs", inputs);
			
			// rest call to billing service
			
			
		}
		
	}

}
