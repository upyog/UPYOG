package org.egov.pg.web.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.egov.pg.models.Transaction;
import org.egov.pg.service.GatewayService;
import org.egov.pg.service.TransactionServiceV2;
import org.egov.pg.utils.ResponseInfoFactory;
import org.egov.pg.web.models.RequestInfoWrapper;
import org.egov.pg.web.models.ResponseInfo;
import org.egov.pg.web.models.TransactionCriteriaV2;
import org.egov.pg.web.models.TransactionRequestV2;
import org.egov.pg.web.models.TransactionResponseV2;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Endpoints to deal with all payment related operations
 */

@Slf4j
@RestController
public class TransactionsApiControllerV2 {

	@Autowired
	private TransactionServiceV2 transactionServiceV2;

	@Autowired
	private GatewayService gatewayService;

	/**
	 * Initiates a new payment transaction, on successful validation, a redirect is
	 * issued to the payment gateway.
	 *
	 * @param transactionRequest Request containing all information necessary for
	 *                           initiating payment
	 * @return Transaction that has been created
	 * @throws RazorpayException
	 */

	@PostMapping(value = "/transaction/v2/_create")
	public ResponseEntity<TransactionResponseV2> transactionsV2CreatePost(
			@Valid @RequestBody TransactionRequestV2 transactionRequests) throws CustomException {

		List<Transaction> transactions = transactionServiceV2.initiateTransaction(transactionRequests);
		ResponseInfo responseInfo = ResponseInfoFactory
				.createResponseInfoFromRequestInfo(transactionRequests.getRequestInfo(), true);
		TransactionResponseV2 response = transactionServiceV2.prepareResponse(transactions, responseInfo);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Returns the current status of a transaction in our systems; This does not
	 * guarantee live payment gateway status.
	 *
	 * @param requestInfoWrapper  Request Info
	 * @param transactionCriteria Search Conditions that should be matched
	 * @return List of transactions matching the search criteria
	 */

	@PostMapping(value = "/transaction/v2/_search")
	public ResponseEntity<TransactionResponseV2> transactionsV2SearchPost(
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute TransactionCriteriaV2 transactionCriteriaV2) {
		List<Transaction> transactions = transactionServiceV2.getTransactions(transactionCriteriaV2);
		ResponseInfo responseInfo = ResponseInfoFactory
				.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
		TransactionResponseV2 response = transactionServiceV2.prepareResponse(transactions, responseInfo);

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	/**
	 * Updates the status of the transaction from the gateway
	 *
	 * @param params Parameters posted by the gateway
	 * @return The current transaction status of the transaction
	 */

	@PostMapping(value = "/transaction/v2/_update")
	public ResponseEntity<TransactionResponseV2> transactionsV2UpdatePost(
			@RequestBody RequestInfoWrapper requestInfoWrapper, @RequestParam Map<String, String> params) {
		List<Transaction> transactions = transactionServiceV2.updateTransaction(requestInfoWrapper.getRequestInfo(),
				params);
		ResponseInfo responseInfo = ResponseInfoFactory
				.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
		TransactionResponseV2 response = transactionServiceV2.prepareResponse(transactions, responseInfo);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Active payment gateways that can be used for payments
	 *
	 * @return list of active gateways that can be used for payments
	 */

	@PostMapping(value = "/gateway/v2/_search")
	public ResponseEntity<Set<String>> transactionsV2AvailableGatewaysPost() {

		Set<String> gateways = gatewayService.getActiveGateways();
		log.debug("Available gateways : " + gateways);
		return new ResponseEntity<>(gateways, HttpStatus.OK);
	}

}
