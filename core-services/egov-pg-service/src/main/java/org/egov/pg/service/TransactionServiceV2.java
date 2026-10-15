package org.egov.pg.service;

import static java.util.Collections.singletonMap;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.config.AppProperties;
import org.egov.pg.constants.PgConstants;
import org.egov.pg.models.Transaction;
import org.egov.pg.models.TransactionDetails;
import org.egov.pg.models.TransactionDetailsWrapper;
import org.egov.pg.models.TransactionDump;
import org.egov.pg.models.TransactionDumpRequest;
import org.egov.pg.producer.Producer;
import org.egov.pg.repository.TransactionDetailsRepository;
import org.egov.pg.repository.TransactionRepository;
import org.egov.pg.validator.TransactionValidator;
import org.egov.pg.validator.TransactionValidatorV2;
import org.egov.pg.web.models.ResponseInfo;
import org.egov.pg.web.models.TransactionCriteriaV2;
import org.egov.pg.web.models.TransactionDetailsCriteria;
import org.egov.pg.web.models.TransactionRequest;
import org.egov.pg.web.models.TransactionRequestV2;
import org.egov.pg.service.gateways.paytm.PaymentStatusResponse;
import org.egov.pg.web.models.TransactionResponseV2;
import org.egov.pg.web.models.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.egov.pg.models.DemandAmountInfo;
import org.egov.pg.models.BankAccount;
import org.egov.pg.models.BankAccountResponse;
import org.egov.pg.models.BankAccountSearchCriteria;
import org.egov.pg.models.Bill;
import org.egov.pg.models.BillResponse;
import org.egov.pg.models.Demand;import org.egov.pg.service.gateways.razorpay.models.PaymentResponse;
import org.egov.pg.service.gateways.razorpay.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.pg.web.models.ChequeTransactionRequest;

import lombok.extern.slf4j.Slf4j;
import org.egov.pg.models.DemandResponse;
import org.egov.pg.models.DemandDetail;
/**
 * Handles all transaction related requests
 */
@Service
@Slf4j
public class TransactionServiceV2 {

	@Autowired
	private TransactionValidator validator;
	
	@Autowired
	private TransactionValidatorV2 validatorv2;
	
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
	private TransactionDetailsRepository transactionDetailsRepository;

	@Autowired
	private PaymentsService paymentsService;
	
	@Autowired
	private BillingService billingService;
	
	@Autowired
	private RazorpayGateway RazorpayGateway;
	
	private ObjectMapper ObjectMapper;
	
	@Autowired
	private BankAccountService bankAccountService;


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

			validatorv2.validateCreateTxn(
					TransactionRequest.builder().transaction(transaction).requestInfo(requestInfo).build());

			// Enrich transaction by generating txnid, audit details, default status
			enrichmentService.enrichCreateTransaction(
					TransactionRequest.builder().transaction(transaction).requestInfo(requestInfo).build());

			TransactionDump dump = TransactionDump.builder().txnId(transaction.getTxnId())
					.auditDetails(transaction.getAuditDetails()).build();
			
			log.info("***PG SERVICE Request*** ==> Amount: {}, billId: {}",transaction.getTxnAmount(),transaction.getBillId());

			if ("CHEQUE".equalsIgnoreCase(transaction.getGatewayPaymentMode())) {
				transaction.setTxnStatus(Transaction.TxnStatusEnum.PROCESSING);
				transaction.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
				updatePaymentProcessing(transaction, true);
			}
			else if (validator.skipGateway(transaction)) {
				transaction.setTxnStatus(Transaction.TxnStatusEnum.SUCCESS);
				transaction.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
				paymentsService.registerPayment(
						TransactionRequest.builder().transaction(transaction).requestInfo(requestInfo).build());
			}
			else if (transaction.getGateway().equalsIgnoreCase("OFFLINE")) {

			    transaction.setTxnStatus(Transaction.TxnStatusEnum.SUCCESS);
			    
			    enrichmentService.enrichUpdateTransaction(
			        TransactionRequest.builder()
			            .transaction(transaction)
			            .requestInfo(requestInfo)
			            .build(),
			        transaction
			    );

			    paymentsService.registerPayment(
			        TransactionRequest.builder()
			            .transaction(transaction)
			            .requestInfo(requestInfo)
			            .build()
			    );
			}
			else {
	
				Set<String> tenantIds = Collections.singleton(transaction.getTenantId());;
				BankAccountResponse bankAccountResponse = null;
				BankAccountSearchCriteria bankAccountSearchCriteria = BankAccountSearchCriteria.builder()
						.requestInfo(requestInfo).tenantIds(tenantIds).active(true).build();
		
				// fetch all bank account
				bankAccountResponse = bankAccountService.searchBankAccount(bankAccountSearchCriteria);
				 if (!CollectionUtils.isEmpty(bankAccountResponse.getBankAccounts())) {
				        BankAccount bankAccount = bankAccountResponse.getBankAccounts().get(0);
				        transaction.setPayTo(bankAccount.getPayTo());
				    }
				URI uri = gatewayService.initiateTxn(transaction);
				transaction.setRedirectUrl(uri.toString());

				dump.setTxnRequest(uri.toString());
			}

			// Persist transaction and transaction dump objects
			producer.push(appProperties.getSaveTxnTopic(),
					new org.egov.pg.models.TransactionRequest(requestInfo, transaction));
			producer.push(appProperties.getSaveTxnDumpTopic(), new TransactionDumpRequest(requestInfo, dump));

			// Enrich transaction Details by generating uuid, txnid, audit details and
			// additional details
			if (!CollectionUtils.isEmpty(transaction.getTransactionDetails())) {
				enrichmentService.populateTransactionsDetails(transaction, requestInfo);
				transaction.getTransactionDetails().forEach(transactionDetails -> {
					producer.push(appProperties.getSaveTxnDetailsTopic(),
							TransactionDetailsWrapper.builder().transactionDetails(transactionDetails).build());
				});
			}

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
		List<Transaction> transactions = new ArrayList<>();
		try {
			if (!CollectionUtils.isEmpty(transactionCriteriaV2.getBillIds())
					|| !CollectionUtils.isEmpty(transactionCriteriaV2.getConsumerCodes())) {
				TransactionDetailsCriteria transactionDetailsCriteria = TransactionDetailsCriteria.builder()
						.billIds(transactionCriteriaV2.getBillIds())
						.consumerCodes(transactionCriteriaV2.getConsumerCodes()).build();

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
				transactions = transactionRepository.fetchTransactions(transactionCriteriaV2);

				mapTransactionDetails(transactions);
			}

			return transactions;
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
				Object razorpayRawResponse = newTxn.getResponseJson();

				// Enrich the new transaction status before persisting
				enrichmentService.enrichUpdateTransaction(new TransactionRequest(requestInfo, currentTxnStatus),
						newTxn);

				// Set payment response from gateway
				if (razorpayRawResponse != null) {
				    newTxn.setRazorpayResponse(razorpayRawResponse);
				}

			}
			
			String tenantId = currentTxnStatus.getTenantId();
			String BillId = currentTxnStatus.getBillId();
			try {
				DemandAmountInfo demandAmountInfo = fetchDemandAmountsForBill(requestInfo, tenantId, BillId);
			} catch (Exception e) {
			    log.error("Unable to fetch demand amounts for bill {}", BillId, e);
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

		mapTransactionDetails(newTxns);

		return newTxns;
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

	public TransactionResponseV2 prepareResponse(List<Transaction> transactions, ResponseInfo responseInfo) {
		BigDecimal totalPayableAmount = BigDecimal.ZERO;
		String callbackUrl = null;
		List<String> orderIdArray = new ArrayList<>();
		List<String> consumerCodeArray = new ArrayList<>();
		User user = null;

		if (!CollectionUtils.isEmpty(transactions) && null != transactions.get(0).getUser()) {
			user = transactions.get(0).getUser();
			callbackUrl = transactions.get(0).getCallbackUrl();
		}

		for (Transaction transaction : transactions) {
			orderIdArray.add(transaction.getOrderId());
			totalPayableAmount = totalPayableAmount.add(new BigDecimal(transaction.getTxnAmount().toString()));
			if (!CollectionUtils.isEmpty(transaction.getTransactionDetails())) {
				for (TransactionDetails transactionDetails : transaction.getTransactionDetails()) {
					consumerCodeArray.add(transactionDetails.getConsumerCode());
				}
			}
		}

		if (null != callbackUrl) {
			callbackUrl = UriComponentsBuilder
					.fromHttpUrl(Arrays.asList(callbackUrl.split(PgConstants.PG_TXN_IN_LABEL)).get(0))
					.queryParams(new LinkedMultiValueMap<>(
							singletonMap(PgConstants.PG_TXN_IN_LABEL, Collections.singletonList(transactions.stream()
									.map(Transaction::getTxnId).collect(Collectors.joining(","))))))
					.build().toUriString();
		}

		return TransactionResponseV2.builder().transactions(transactions).responseInfo(responseInfo)
				.totalPayableAmount(totalPayableAmount).callbackUrl(callbackUrl).orderIdArray(orderIdArray)
				.consumerCodeArray(consumerCodeArray).user(user).build();
	}
	
	public DemandAmountInfo fetchDemandAmountsForBill(
	        RequestInfo requestInfo,
	        String tenantId,
	        String billId) {
	
	    BillResponse billResponse = billingService.searchBillById(requestInfo, tenantId, billId);
	
	    if (billResponse == null || billResponse.getBill() == null || billResponse.getBill().isEmpty()) {
	        throw new CustomException("INVALID_BILL", "No bill found for billId: " + billId);
	    }
	
	    Bill bill = billResponse.getBill().get(0);
	    String consumerCode = bill.getConsumerCode();
	
	    DemandResponse demandResponse = billingService.searchDemand(requestInfo, tenantId, consumerCode);
	
	    if (demandResponse == null || demandResponse.getDemands() == null || demandResponse.getDemands().isEmpty()) {
	        throw new CustomException("DEMAND_NOT_FOUND", "No demand found for consumerCode: " + consumerCode);
	    }
	
	    BigDecimal taxAmount = BigDecimal.ZERO;
	    BigDecimal collectionAmount = BigDecimal.ZERO;
	
	    for (Demand demand : demandResponse.getDemands()) {
	
	        if (demand.getDemandDetails() == null) continue;
	
	        for (DemandDetail detail : demand.getDemandDetails()) {
	            taxAmount = taxAmount.add(detail.getTaxAmount());
	            collectionAmount = collectionAmount.add(detail.getCollectionAmount());
	        }
	    }
	
	    return new DemandAmountInfo(taxAmount, collectionAmount);
	}
	
	public String updateChequeTransaction(ChequeTransactionRequest request) {

		TransactionCriteriaV2 transactionSearchCriteria = TransactionCriteriaV2.builder()
				.billIds(Collections.singleton(request.getBillId())).build();

		List<Transaction> transactions = getTransactions(transactionSearchCriteria);
		if (CollectionUtils.isEmpty(transactions)) {
			throw new CustomException("TRANSACTION_NOT_FOUND", "Transaction not found");
		}
		Transaction txn = transactions.get(0);

		if (!"CHEQUE".equalsIgnoreCase(txn.getGatewayPaymentMode())) {
			throw new CustomException("INVALID_TRANSACTION", "Transaction is not a cheque transaction");
		}

		if (txn.getTxnStatus() != Transaction.TxnStatusEnum.PROCESSING) {
			throw new CustomException("INVALID_TRANSACTION_STATUS", "Cheque transaction must be in PROCESSING state");
		}

		if (Transaction.TxnStatusEnum.SUCCESS.equals(request.getAction())) {
			txn.setTxnStatus(Transaction.TxnStatusEnum.SUCCESS);
			txn.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
			paymentsService.registerPayment(
					TransactionRequest.builder().transaction(txn).requestInfo(request.getRequestInfo()).build());

			updateChequeTransactionStatus(txn, request.getRequestInfo());
			updatePaymentProcessing(txn, false);

			return "Cheque payment for Bill ID " + request.getBillId()
					+ " has been successfully verified and payment processing has been completed.";

		} else if (Transaction.TxnStatusEnum.FAILURE.equals(request.getAction())) {

			txn.setTxnStatus(Transaction.TxnStatusEnum.FAILURE);
			txn.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());

			updateChequeTransactionStatus(txn, request.getRequestInfo());
			updatePaymentProcessing(txn, false);

			return "Cheque payment for Bill ID " + request.getBillId() + " has been rejected and marked as failed.";
		}

		return "Invalid action for cheque payment for Bill ID " + request.getBillId();
	}
	
	private void updateChequeTransactionStatus(Transaction txn, RequestInfo requestInfo) {
		producer.push(appProperties.getUpdateTxnTopic(), new org.egov.pg.models.TransactionRequest(requestInfo, txn));
	}
	
	private void updatePaymentProcessing(Transaction transaction, boolean isPaymentProcessing) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("billId", transaction.getBillId());
		payload.put("txnId", transaction.getTxnId());
		payload.put("isPaymentProcessing", isPaymentProcessing);
		payload.put("txnAmount", transaction.getTxnAmount());

		if ("GB".equalsIgnoreCase(transaction.getProductInfo())) {
			producer.push(appProperties.getGrbgPaymentProcessingTopic(), payload);
		} else if ("PROPERTY".equalsIgnoreCase(transaction.getProductInfo())) {
			producer.push(appProperties.getPropertyPaymentProcessingTopic(), payload);
		}
	}

}
