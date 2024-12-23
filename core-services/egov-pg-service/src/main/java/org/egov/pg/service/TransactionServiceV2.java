package org.egov.pg.service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.config.AppProperties;
import org.egov.pg.models.Transaction;
import org.egov.pg.models.TransactionDump;
import org.egov.pg.models.TransactionDumpRequest;
import org.egov.pg.producer.Producer;
import org.egov.pg.repository.TransactionRepository;
import org.egov.pg.validator.TransactionValidator;
import org.egov.pg.web.models.ResponseInfo;
import org.egov.pg.web.models.TransactionCriteriaV2;
import org.egov.pg.web.models.TransactionRequest;
import org.egov.pg.web.models.TransactionRequestV2;
import org.egov.pg.web.models.TransactionResponseV2;
import org.egov.pg.web.models.User;
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
public class TransactionServiceV2 {

	@Autowired
	private TransactionValidator validator;

	@Autowired
	private GatewayService gatewayService;

	@Autowired
	private Producer producer;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private PaymentsService paymentsService;

//	TransactionServiceV2(TransactionValidator validator, GatewayService gatewayService, Producer producer,
//			TransactionRepository transactionRepository, PaymentsService paymentsService,
//			EnrichmentService enrichmentService, AppProperties appProperties) {
//		this.validator = validator;
//		this.gatewayService = gatewayService;
//		this.producer = producer;
//		this.transactionRepository = transactionRepository;
//		this.paymentsService = paymentsService;
//		this.enrichmentService = enrichmentService;
//		this.appProperties = appProperties;
//	}

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
	public List<Transaction> initiateTransaction(TransactionRequestV2 transactionRequests) throws CustomException {

		if (CollectionUtils.isEmpty(transactionRequests.getTransactions())) {
			throw new CustomException("TRANSACTIONS_DETAILS_EMPTY", "Transactions details can't be empty");
		}

		RequestInfo requestInfo = transactionRequests.getRequestInfo();
		List<Transaction> transactions = new ArrayList<>();

		transactionRequests.getTransactions().forEach(transaction -> {

			validator.validateCreateTxn(
					TransactionRequest.builder().transaction(transaction).requestInfo(requestInfo).build());

			// Enrich transaction by generating txnid, audit details, default status
			enrichmentService.enrichCreateTransaction(
					TransactionRequest.builder().transaction(transaction).requestInfo(requestInfo).build());

//		RequestInfo requestInfo = transactionRequest.getRequestInfo();
//		Transaction transaction = transactionRequest.getTransaction();

			TransactionDump dump = TransactionDump.builder().txnId(transaction.getTxnId())
					.auditDetails(transaction.getAuditDetails()).build();

			if (validator.skipGateway(transaction)) {
				transaction.setTxnStatus(Transaction.TxnStatusEnum.SUCCESS);
				paymentsService.registerPayment(
						TransactionRequest.builder().transaction(transaction).requestInfo(requestInfo).build());
			} else {
				URI uri = gatewayService.initiateTxn(transaction);
				transaction.setRedirectUrl(uri.toString());

				dump.setTxnRequest(uri.toString());
			}

			// Persist transaction and transaction dump objects
			producer.push(appProperties.getSaveTxnTopic(),
					new org.egov.pg.models.TransactionRequest(requestInfo, transaction));
			producer.push(appProperties.getSaveTxnDumpTopic(), new TransactionDumpRequest(requestInfo, dump));

			transactions.add(transaction);
		});

		return transactions;
	}

	/**
	 * Fetches a list of transactions matching the current criteria
	 * <p>
	 * Currently has a hard limit of 5 records, configurable
	 *
	 * @param transactionCriteria Search Conditions that should be matched
	 * @return List of transactions matching the conditions.
	 */
	public List<Transaction> getTransactions(TransactionCriteriaV2 transactionCriteriaV2) {
		log.info(transactionCriteriaV2.toString());
		try {
			return transactionRepository.fetchTransactions(transactionCriteriaV2);
		} catch (DataAccessException e) {
			log.error("Unable to fetch data from the database for criteria: " + transactionCriteriaV2.toString(), e);
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

		List<Transaction> currentTxnStatuses = validator.validateUpdateTxnV2(requestParams);

		log.debug(currentTxnStatuses.toString());
		log.debug(requestParams.toString());

		List<Transaction> newTxns = new ArrayList<>();

		for (Transaction currentTxnStatus : currentTxnStatuses) {

			Transaction newTxn = null;

			if (validator.skipGateway(currentTxnStatus)) {
				newTxn = currentTxnStatus;

			} else {
				newTxn = gatewayService.getLiveStatus(currentTxnStatus, requestParams);

				// Enrich the new transaction status before persisting
				enrichmentService.enrichUpdateTransaction(new TransactionRequest(requestInfo, currentTxnStatus),
						newTxn);
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

			newTxns.add(newTxn);
		}
		if (CollectionUtils.isEmpty(newTxns)) {
			return null;
		}
		return newTxns;
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

	public TransactionResponseV2 prepareResponse(List<Transaction> transactions, ResponseInfo responseInfo) {
		BigDecimal totalPayableAmount = BigDecimal.ZERO;
		String callbackUrl = null; // TODO
		List<String> orderIdArray = new ArrayList<>();
		List<String> consumerCodeArray = new ArrayList<>();
		User user = null;

		if (!CollectionUtils.isEmpty(transactions) && null != transactions.get(0).getUser()) {
			user = transactions.get(0).getUser();
		}

		for (Transaction transaction : transactions) {
			orderIdArray.add(transaction.getOrderId());
			consumerCodeArray.add(transaction.getConsumerCode());
			totalPayableAmount = totalPayableAmount.add(new BigDecimal(transaction.getTxnAmount().toString()));
		}

		return TransactionResponseV2.builder().transactions(transactions).responseInfo(responseInfo)
				.totalPayableAmount(totalPayableAmount).callbackUrl(callbackUrl).orderIdArray(orderIdArray)
				.consumerCodeArray(consumerCodeArray).user(user).build();
	}

}
