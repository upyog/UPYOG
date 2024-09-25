package org.egov.pg.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.pg.config.AppProperties;
import org.egov.pg.constants.PgConstants;
import org.egov.pg.models.Transaction;
import org.egov.pg.models.TransactionDump;
import org.egov.pg.models.TransactionDumpRequest;
import org.egov.pg.producer.Producer;
import org.egov.pg.repository.TransactionRepository;
import org.egov.pg.service.gateways.paygov.PayGovGatewayStatusResponse;
import org.egov.pg.utils.Utils;
import org.egov.pg.validator.TransactionValidator;
import org.egov.pg.web.models.TransactionCriteria;
import org.egov.pg.web.models.TransactionRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Criteria;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all transaction related requests
 */
@Service
@Slf4j
public class TransactionService {

	private TransactionValidator validator;
	private GatewayService gatewayService;
	private Producer producer;
	private EnrichmentService enrichmentService;
	private AppProperties appProperties;
	private TransactionRepository transactionRepository;
	private PaymentsService paymentsService;

	@Value("${paygov.merchant.secret.key}")
	private String secretKey;



	@Autowired
	TransactionService(TransactionValidator validator, GatewayService gatewayService, Producer producer,
			TransactionRepository
			transactionRepository, PaymentsService paymentsService,
			EnrichmentService enrichmentService,
			AppProperties appProperties) {
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
	 * 1. Validates transaction object
	 * 2. Enriches the request by assigning a TxnID and a default status of PENDING
	 * 3. If yes, calls the gateway's implementation to generate a redirect URI
	 * 4. Persists the transaction and a transaction dump with the RAW requests asynchronously
	 * 5. Returns the redirect URI
	 *
	 * @param transactionRequest Valid transaction request for which transaction needs to be initiated
	 * @return Redirect URI to the gateway for the particular transaction
	 */
	public Transaction initiateTransaction(TransactionRequest transactionRequest) {
		validator.validateCreateTxn(transactionRequest);

		// Enrich transaction by generating txnid, audit details, default status
		enrichmentService.enrichCreateTransaction(transactionRequest);

		RequestInfo requestInfo = transactionRequest.getRequestInfo();
		Transaction transaction = transactionRequest.getTransaction();

		TransactionDump dump = TransactionDump.builder()
				.txnId(transaction.getTxnId())
				.auditDetails(transaction.getAuditDetails())
				.build();

		if(validator.skipGateway(transaction)){
		//if(true){	
			transaction.setTxnStatus(Transaction.TxnStatusEnum.SUCCESS);
			paymentsService.registerPayment(transactionRequest);
		}
		else{
			URI uri = gatewayService.initiateTxn(transaction);
			transaction.setRedirectUrl(uri.toString());

			dump.setTxnRequest(uri.toString());
		}

		// Persist transaction and transaction dump objects
		producer.push(appProperties.getSaveTxnTopic(), new org.egov.pg.models.TransactionRequest
				(requestInfo, transaction));
		producer.push(appProperties.getSaveTxnDumpTopic(), new TransactionDumpRequest(requestInfo, dump));

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
		try {
			return transactionRepository.fetchTransactions(transactionCriteria);
		} catch (DataAccessException e) {
			log.error("Unable to fetch data from the database for criteria: " + transactionCriteria.toString(), e);
			throw new CustomException("FETCH_TXNS_FAILED", "Unable to fetch transactions from store");
		}
	}

	/**
	 * Updates the status of the transaction from the gateway
	 * <p>
	 * 1. Fetch TXN ID from the request params, if not found, exit!
	 * 2. Fetch current transaction status from DB, if not found, exit!
	 * 3. Fetch the current transaction status from the payment gateway
	 * 4. Verify the amount returned from the gateway matches our records
	 * 5. If successful, generate receipt
	 * 6. Persist the updated transaction status and raw gateway transaction response
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

		if(validator.skipGateway(currentTxnStatus)) {
			newTxn = currentTxnStatus;

		} else{
			newTxn = gatewayService.getLiveStatus(currentTxnStatus, requestParams);

			// Enrich the new transaction status before persisting
			enrichmentService.enrichUpdateTransaction(new TransactionRequest(requestInfo, currentTxnStatus), newTxn);
		}

		// Check if transaction is successful, amount matches etc
		if (validator.shouldGenerateReceipt(currentTxnStatus, newTxn)) {
			TransactionRequest request = TransactionRequest.builder().requestInfo(requestInfo).transaction(newTxn).build();
			paymentsService.registerPayment(request);
		}

		TransactionDump dump = TransactionDump.builder()
				.txnId(currentTxnStatus.getTxnId())
				.txnResponse(newTxn.getResponseJson())
				.auditDetails(newTxn.getAuditDetails())
				.build();

		producer.push(appProperties.getUpdateTxnTopic(), new org.egov.pg.models.TransactionRequest(requestInfo, newTxn));
		producer.push(appProperties.getUpdateTxnDumpTopic(), new TransactionDumpRequest(requestInfo, dump));

		return Collections.singletonList(newTxn);
	}




	public String updateTransactionForPushResponse(HttpServletRequest req, HttpServletResponse resp) {

		RequestInfo requestInfo;
		User userInfo = User.builder()
				.uuid(appProperties.getEgovPgReconciliationSystemUserUuid())
				.type("SYSTEM")
				.roles(Collections.emptyList()).id(0L).build();

		requestInfo = new RequestInfo();
		requestInfo.setUserInfo(userInfo);
		String response="200|Y|SUCCESSFUL";
		try {

			Map<String, String> requestParams = new TreeMap<>() ;

			String message=req.getParameter("msg");
			String auth=req.getParameter("auth");

			//Set Fixed token For Authentication
			//Check Token Validation 
			//Throw Authentication Failure



			//Chek transactionid location accrding to flag for S its 4 For F check the message

			String transactionId="";
			String transactionAmmount="";
			if(message.split("[|]")[0].equalsIgnoreCase("S")) {
				transactionId= message.split("[|]")[4];
				transactionAmmount=message.split("[|]")[6];
			}
			else if(message.split("[|]")[0].equalsIgnoreCase("F")) {
				transactionId= message.split("[|]")[2];
			}

			requestParams.put("transactionId", transactionId );
			requestParams.put("transactionAmmount", transactionAmmount);
			Transaction currentTxnStatus = validator.validateUpdateTxn(requestParams);
			Transaction newTxn = null;

			if(validator.skipGateway(currentTxnStatus)) {
				newTxn = currentTxnStatus;

			} else{
				try {
					newTxn=	gatewayService.getTransanformedTransaction(message, currentTxnStatus, secretKey);
				} catch ( IOException e) {
					response="400|N|"+"System Exception";
				} 
				// Enrich the new transaction status before persisting
				enrichmentService.enrichUpdateTransaction(new TransactionRequest(requestInfo, currentTxnStatus), newTxn);
			}

			// Check if transaction is successful, amount matches etc


			try {
					
				if (validator.shouldGenerateReceipt(currentTxnStatus, newTxn)) {
					TransactionRequest request=TransactionRequest.builder().requestInfo(requestInfo).transaction(newTxn).build(); 
					paymentsService.registerPayment(request); 
				}

				TransactionDump dump=TransactionDump.builder().txnId(currentTxnStatus.getTxnId()).txnResponse(
						newTxn.getResponseJson()).auditDetails(newTxn.getAuditDetails()) .build();

				producer.push(appProperties.getUpdateTxnTopic(), new TransactionRequest(requestInfo, newTxn));
				producer.push(appProperties.getUpdateTxnDumpTopic(), new TransactionDumpRequest(requestInfo, dump)); 
			} 
			catch (CustomException e) {

				if(e.getCode().equals("EXTERNAL_SERVICE_EXCEPTION")) {
					if(e.getMessage()!=null) {

						ObjectMapper map = new ObjectMapper();

						JsonNode node; 

						try { 
							node = map.readTree(e.getMessage()); 
							JsonNode a =node.get("Errors").findParent("code");

							if(a.get("code").toString().replaceAll("\"","").equalsIgnoreCase("BILL_ALREADY_PAID")) 
							{ 
								response="200|Y|SUCCESSFUL" ; 
							}
							else 
							{
								response="400|N|"+"System Exception";
							} 
						} 
						catch (JsonMappingException e1) {
							// TODO Auto-generated catch block e1.printStackTrace();
							response="400|N|"+"System Exception"; 
						} 
						catch (JsonProcessingException e1) {
							// TODO Auto-generated catch block e1.printStackTrace();
							response="400|N|"+"System Exception"; 
						}

					}

				}


				else { 
					response="400|N|"+"System Exception"; 
				} 
			}

		}
		catch (CustomException e) {
			if(e.getCode().equals("MISSING_UPDATE_TXN_ID")) {
				response="400|N|"+"Invalid Order id" ;
			}
			else if(e.getCode().equals("CHECKSUM_VALIDATION_FAILED")){
				response="400|N|"+"Invalid Check Sum" ;
			}
			else if(e.getCode().equals("BILL_ALREADY_PAID")) {
				response="200|Y|SUCCESSFUL" ;
			}
			else if(e.getCode().equals("TXN_UPDATE_NOT_FOUND"))
				response="400|N|TXN_UPDATE_NOT_FOUND" ;
			else if(e.getCode().equals("TXN_AMMOUNT_MISSMATCH"))
				response="400|N|TXN_AMMOUNT_MISSMATCH" ;
			else {
				response="400|N|"+"System Exception";
			}
		}



		// For failed given from merchant 


		return response;

		// return Collections.singletonList(newTxn);
	}




}
