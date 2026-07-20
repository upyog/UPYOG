package org.egov.pg.service;

import static java.util.Collections.singletonMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.constants.PgConstants;
import org.egov.pg.constants.TransactionAdditionalFields;
import org.egov.pg.models.AuditDetails;
import org.egov.pg.models.BankAccount;
import org.egov.pg.models.CollectionPayment;
import org.egov.pg.models.PaymentRefund;
import org.egov.pg.models.Refund;
import org.egov.pg.models.Refund.RefundStatusEnum;
import org.egov.pg.models.RefundRequest;
import org.egov.pg.models.Transaction;
import org.egov.pg.repository.BankAccountRepository;
import org.egov.pg.repository.TransactionRepository;
import org.egov.pg.service.gateways.nttdata.AtomSignature;
import org.egov.pg.service.gateways.nttdata.HeadDetails;
import org.egov.pg.service.gateways.nttdata.MerchDetails;
import org.egov.pg.service.gateways.nttdata.PayDetails;
import org.egov.pg.service.gateways.nttdata.PayInstrument;
import org.egov.pg.service.gateways.nttdata.ProdDetails;
import org.egov.pg.service.gateways.nttdata.RefundTransaction;
import org.egov.pg.web.models.TransactionCriteria;
import org.egov.pg.web.models.TransactionRequest;
import org.egov.tracer.model.CustomException;
import org.egov.pg.web.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnrichmentService {

    private IdGenService idGenService;
    private BankAccountRepository bankAccountRepository;
    private ObjectMapper objectMapper;
    private UserService userService;
    private TransactionRepository transactionRepository;

    @Autowired
    EnrichmentService(IdGenService idGenService, BankAccountRepository bankAccountRepository, ObjectMapper objectMapper, UserService userService,TransactionRepository transactionRepository) {
        this.idGenService = idGenService;
        this.bankAccountRepository = bankAccountRepository;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.transactionRepository=transactionRepository;
    }

    void enrichCreateTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = transactionRequest.getTransaction();
        RequestInfo requestInfo = transactionRequest.getRequestInfo();

        BankAccount bankAccount = bankAccountRepository.getBankAccountsById(requestInfo, transaction.getTenantId());
        transaction.setAdditionalFields(singletonMap(TransactionAdditionalFields.BANK_ACCOUNT_NUMBER, bankAccount.getAccountNumber()));

        // Generate ID from ID Gen service and assign to txn object
        String txnId = idGenService.generateTxnId(transactionRequest);
        transaction.setTxnId(txnId);
        transaction.setUser(userService.createOrSearchUser(transactionRequest));
        transaction.setTxnStatus(Transaction.TxnStatusEnum.PENDING);
        transaction.setTxnStatusMsg(PgConstants.TXN_INITIATED);

        if(Objects.isNull(transaction.getAdditionalDetails())){
            transaction.setAdditionalDetails(objectMapper.createObjectNode());
            ((ObjectNode) transaction.getAdditionalDetails()).set("taxAndPayments",
                    objectMapper.valueToTree(transaction.getTaxAndPayments()));
        }
        else{
            Map<String, Object> additionDetailsMap = objectMapper.convertValue(transaction.getAdditionalDetails(), Map.class);
            additionDetailsMap.put("taxAndPayments",(Object) transaction.getTaxAndPayments());
            transaction.setAdditionalDetails(objectMapper.convertValue(additionDetailsMap,Object.class));
        }
        
        String uri = UriComponentsBuilder
                .fromHttpUrl(transaction.getCallbackUrl())
                .queryParams(new LinkedMultiValueMap<>(singletonMap(PgConstants.PG_TXN_IN_LABEL,
                        Collections.singletonList(txnId))))
                .build()
                .toUriString();
        transaction.setCallbackUrl(uri);
        log.info("callback uri: "+uri);

        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(requestInfo.getUserInfo() != null ? requestInfo.getUserInfo().getUuid() : null)
                .createdTime(System.currentTimeMillis())
                .build();
        transaction.setAuditDetails(auditDetails);
    }

    void enrichUpdateTransaction(TransactionRequest transactionRequest, Transaction newTxn) {
        RequestInfo requestInfo = transactionRequest.getRequestInfo();
        Transaction currentTxnStatus = transactionRequest.getTransaction();

        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(currentTxnStatus.getAuditDetails().getCreatedBy())
                .createdTime(currentTxnStatus.getAuditDetails().getCreatedTime())
                .lastModifiedBy(requestInfo.getUserInfo() != null ? requestInfo.getUserInfo().getUuid() : null)
                .lastModifiedTime(System.currentTimeMillis()).build();
        newTxn.setAuditDetails(auditDetails);

        newTxn.setTxnId(currentTxnStatus.getTxnId());
        newTxn.setGateway(currentTxnStatus.getGateway());
        newTxn.setBillId(currentTxnStatus.getBillId());
        newTxn.setProductInfo(currentTxnStatus.getProductInfo());
        newTxn.setTenantId(currentTxnStatus.getTenantId());
        newTxn.setUser(currentTxnStatus.getUser());
        newTxn.setAdditionalDetails(currentTxnStatus.getAdditionalDetails());
        newTxn.setTaxAndPayments(currentTxnStatus.getTaxAndPayments());
        newTxn.setConsumerCode(currentTxnStatus.getConsumerCode());
        newTxn.setTxnStatusMsg(currentTxnStatus.getTxnStatusMsg());
        newTxn.setReceipt(currentTxnStatus.getReceipt());

    }


	private void setIdFromIdGen(RefundRequest refundRequest) {
//		int min = 1000;
//		int max = 10000;
//		int randomNum = (int)(Math.random() * (max - min + 1) + min);
//		String refundId = "PG_RF_"+randomNum+"_TEST";
		String refundId = idGenService.generateRefundId(refundRequest);
		refundRequest.getRefund().setRefundId(refundId);
	}

	public void enrichupdateRefundTransaction(Refund currentRefund) {
		TransactionCriteria criteria = TransactionCriteria.builder().txnId(currentRefund.getOriginalTxnId()).build();
		List<Transaction> statuses = transactionRepository.fetchTransactions(criteria);

		if (statuses == null || statuses.isEmpty()) {
		    throw new CustomException("TXN_NOT_FOUND", "No transaction found for given criteria");
		}

		String atomTxnId = statuses.get(0).getAtomTxnId();
		String consumerCode = statuses.get(0).getConsumerCode();
		currentRefund.setAtomTxnId(atomTxnId);
		
	}

	public RefundRequest enrichRefundRequest(List<Transaction> transactions, RequestInfo requestInfo) {
		 RefundRequest refundRequest = new RefundRequest();
		    refundRequest.setRequestInfo(requestInfo);

		    Transaction transaction = transactions.get(0);

		    Refund refund = new Refund();
		  refund.setTenantId(transaction.getTenantId());
		  refundRequest.setRequestInfo(requestInfo);
		  
		  refund.setId(UUID.randomUUID().toString());
		  
		  
		  refund.setOriginalTxnId(transaction.getTxnId());
		  refund.setRefundAmount(new BigDecimal(transaction.getTxnAmount()));
		  refund.setOriginalAmount(new BigDecimal(transaction.getTxnAmount()));
		  refund.setAtomTxnId(transaction.getAtomTxnId());
		  refund.setConsumerCode(transaction.getConsumerCode());
		  refund.setGateway(transaction.getGateway());
		  refund.setGatewayTxnId(transaction.getGatewayTxnId());
		  refund.setServiceCode(PgConstants.PG_REFUND);
		  
		  
		  refund.setStatus(Refund.RefundStatusEnum.INITIATED);
			 
			 AuditDetails auditDetails = AuditDetails.builder()
		                .createdBy(requestInfo.getUserInfo() != null ? requestInfo.getUserInfo().getUuid() : null)
		                .createdTime(System.currentTimeMillis())
		                .build();
		        refund.setAuditDetails(auditDetails);
		  refundRequest.setRefund(refund);
		  setIdFromIdGen(refundRequest);
		return refundRequest;
	}

	
	public RefundTransaction enrichRefundTransaction(Refund refundRequest,String merchantId) {
	    String password = "Test@123";
	    String currency = "INR";
	    String api = "REFUNDINIT";
	    String source = "OTS";

	    String merchantTxnId = refundRequest.getOriginalTxnId();
	    String atomTxnId = refundRequest.getAtomTxnId();

	    // MerchDetails
	    MerchDetails merchDetails = new MerchDetails();
	    merchDetails.setMerchId(merchantId);
	    merchDetails.setMerchTxnId(merchantTxnId);
	    merchDetails.setPassword(password);

	    // ProdDetails
	    ProdDetails prodDetails = new ProdDetails();
	    prodDetails.setProdRefundId(refundRequest.getRefundId());
	    prodDetails.setProdName("NSE");
	    prodDetails.setProdRefundAmount(refundRequest.getRefundAmount().doubleValue());

	    List<ProdDetails> prodDetailsList = new ArrayList();
	    prodDetailsList.add(prodDetails);

	    // PayDetails
	    PayDetails payDetails = new PayDetails();
	    payDetails.setTxnCurrency(currency);
	    payDetails.setAtomTxnId(atomTxnId);
	    payDetails.setProdDetails(prodDetailsList);

	    double totalRefundAmount = calculateTotalRefundAmount(prodDetailsList);
	    payDetails.setTotalRefundAmount(totalRefundAmount);

	    // Signature 
	    
	    String refundAmount = formatAmountSmart(refundRequest.getRefundAmount());
	    String signature = generateSignature(merchantId, password, merchantTxnId,
	            refundAmount, currency, api);
	    payDetails.setSignature(signature);

	    // HeadDetails
	    HeadDetails headDetails = new HeadDetails();
	    headDetails.setApi(api);
	    headDetails.setSource(source);

	    // PayInstrument
	    PayInstrument payInstrument = new PayInstrument();
	    payInstrument.setMerchDetails(merchDetails);
	    payInstrument.setPayDetails(payDetails);
	    payInstrument.setHeadDetails(headDetails);

	    RefundTransaction refundTxn = new RefundTransaction();
	    refundTxn.setPayInstrument(payInstrument);

	    return refundTxn;
	}
	
	private String formatAmountSmart(BigDecimal bd) {
	    //BigDecimal bd = new BigDecimal(amount);

	    if (bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0) {
	        return bd.setScale(1, RoundingMode.UNNECESSARY).toPlainString();
	    }

	    return bd.stripTrailingZeros().toPlainString();
	}
	
	private String generateSignature(String merchantId, String password, String merchantTxnId, String amount,
			String currency, String api) {
		String reqHashKey = "KEY123657234";

		String raw = merchantId + password + merchantTxnId + amount + currency + api;
		return AtomSignature.generateSignature(reqHashKey, raw);
	}

	private double calculateTotalRefundAmount(List<ProdDetails> prodDetailsList) {

		if (prodDetailsList == null || prodDetailsList.isEmpty()) {
			throw new CustomException("TOTAL_REFUND_AMOUNT_ERROR", "Product refund details cannot be empty");
		}
		BigDecimal total = prodDetailsList.stream().map(ProdDetails::getProdRefundAmount).map(BigDecimal::valueOf)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return total.doubleValue();
	}
	
	
	public RefundTransaction enrichRefundStatusTransaction(Refund refundRequest,String merchantId) {

	    String api = "REFUNDSTATUS";
	    String source = "OTS_ARS";

	    String atomTxnId = refundRequest.getAtomTxnId();

	    String password = "Test@123";
	    String encodedPassword = generateBase64Password(password);

	    // MerchDetails
	    MerchDetails merchDetails = new MerchDetails();
	    merchDetails.setMerchId(merchantId);
	    merchDetails.setPassword(encodedPassword);

	    // ProdDetails
	    ProdDetails prodDetails = new ProdDetails();
	    prodDetails.setProdName("NSE");

	    List<ProdDetails> prodDetailsList = new ArrayList<>();
	    prodDetailsList.add(prodDetails);

	    // PayDetails
	    PayDetails payDetails = new PayDetails();
	    payDetails.setAtomTxnId(atomTxnId);
	    payDetails.setProdDetails(prodDetailsList);

	    // HeadDetails
	    HeadDetails headDetails = new HeadDetails();
	    headDetails.setApi(api);
	    headDetails.setSource(source);

	    // PayInstrument
	    PayInstrument payInstrument = new PayInstrument();
	    payInstrument.setHeadDetails(headDetails);
	    payInstrument.setMerchDetails(merchDetails);
	    payInstrument.setPayDetails(payDetails);

	    // RefundTransaction
	    RefundTransaction refundTxn = new RefundTransaction();
	    refundTxn.setPayInstrument(payInstrument);

	    return refundTxn;
	}
	
	private String generateBase64Password(String password) {
		return Base64.getEncoder()
                .encodeToString(password.getBytes());
	}

	public void paymentRefundResponse(Refund initiateRefund, PaymentRefund paymentRefund, CollectionPayment collectionPayment) {
		paymentRefund.setTenantId(collectionPayment.getTenantId());
		paymentRefund.setRefundId(initiateRefund.getRefundId());
		paymentRefund.setRefundStatus(initiateRefund.getStatus().toString());
		paymentRefund.setTransactionId(initiateRefund.getOriginalTxnId());
		paymentRefund.setGatewayStausMsg(initiateRefund.getGatewayStatusMsg());
		paymentRefund.setGatewayStatusCode(initiateRefund.getGatewayStatusCode());
		
	}

}