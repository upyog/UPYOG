package org.egov.pg.service;

import static java.util.Collections.singletonMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.constants.PgConstants;
import org.egov.pg.constants.TransactionAdditionalFields;
import org.egov.pg.models.AuditDetails;
import org.egov.pg.models.BankAccount;
import org.egov.pg.models.Transaction;
import org.egov.pg.models.TransactionDetails;
import org.egov.pg.repository.BankAccountRepository;
import org.egov.pg.web.models.TransactionDetailsCriteria;
import org.egov.pg.web.models.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnrichmentService {

    private IdGenService idGenService;
    private BankAccountRepository bankAccountRepository;
    private ObjectMapper objectMapper;
    private UserService userService;

    @Autowired
    EnrichmentService(IdGenService idGenService, BankAccountRepository bankAccountRepository, ObjectMapper objectMapper, UserService userService) {
        this.idGenService = idGenService;
        this.bankAccountRepository = bankAccountRepository;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    void enrichCreateTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = transactionRequest.getTransaction();
        RequestInfo requestInfo = transactionRequest.getRequestInfo();

//        BankAccount bankAccount = bankAccountRepository.getBankAccountsById(requestInfo, transaction.getTenantId());
//        transaction.setAdditionalFields(singletonMap(TransactionAdditionalFields.BANK_ACCOUNT_NUMBER, bankAccount.getAccountNumber()));

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
        newTxn.setOrderId(currentTxnStatus.getOrderId());
        newTxn.setModule(currentTxnStatus.getModule());
        newTxn.setIsMultiTransaction(currentTxnStatus.getIsMultiTransaction());
        newTxn.setTxnStatusMsg(currentTxnStatus.getTxnStatusMsg());
        newTxn.setReceipt(currentTxnStatus.getReceipt());

    }

	public void populateTransactionsDetails(Transaction transaction, RequestInfo requestInfo) {

		AuditDetails auditDetails = AuditDetails.builder()
				.createdBy(requestInfo.getUserInfo() != null ? requestInfo.getUserInfo().getUuid() : null)
				.createdTime(System.currentTimeMillis()).build();

		transaction.getTransactionDetails().forEach(transactionDetails -> {
			transactionDetails.setUuid(UUID.randomUUID().toString());
			transactionDetails.setTxnId(transaction.getTxnId());
			if(null != transactionDetails.getModule()) {
				transactionDetails.setModule(transaction.getModule());
			}
			if(null != transactionDetails.getModuleId()) {
				transactionDetails.setModuleId(transaction.getModule());
			}
			transactionDetails.setAuditDetails(auditDetails);
		});
	}
	
	public TransactionRequest convertTransactionDetailtoTransactionRequest(TransactionRequest transactionRequest,TransactionDetails transactionDetails ) {
		 AuditDetails auditDetails = AuditDetails.builder()
	                .createdBy(transactionRequest.getTransaction().getAuditDetails().getCreatedBy())
	                .createdTime(transactionRequest.getTransaction().getAuditDetails().getCreatedTime())
	                .lastModifiedBy(transactionRequest.getRequestInfo().getUserInfo() != null ? transactionRequest.getRequestInfo().getUserInfo().getUuid() : null)
	                .lastModifiedTime(System.currentTimeMillis()).build();
		 Transaction transaction = transactionRequest.getTransaction();
		 
		 transaction.setAuditDetails(auditDetails);
	
		 transaction.setTxnId(transactionRequest.getTransaction().getTxnId());
		 transaction.setTxnAmount(transactionDetails.getTxnAmount());
		 transaction.setGateway(transactionRequest.getTransaction().getGateway());
		 transaction.setBillId(transactionDetails.getBillId());
		 transaction.setProductInfo(transactionRequest.getTransaction().getProductInfo());
		 transaction.setTenantId(transactionRequest.getTransaction().getTenantId());
		 transaction.setUser(transactionRequest.getTransaction().getUser());
		 transaction.setAdditionalDetails(transactionRequest.getTransaction().getAdditionalDetails());
		 transaction.setTaxAndPayments(transactionRequest.getTransaction().getTaxAndPayments());
		 transaction.setConsumerCode(transactionDetails.getConsumerCode());
		 transaction.setOrderId(transactionRequest.getTransaction().getOrderId());
		 transaction.setModule(transactionRequest.getTransaction().getModule());
		 transaction.setIsMultiTransaction(transactionRequest.getTransaction().getIsMultiTransaction());
		 transaction.setTxnStatusMsg(transactionRequest.getTransaction().getTxnStatusMsg());
		 transaction.setReceipt(transactionRequest.getTransaction().getReceipt());
		 
		 
		 
		 
		 
//		 
//		 transaction.set
//		 Transaction transaction = Transaction.builder()
//				 .txnAmount(transactionDetails.getTxnAmount())
//				 .txnStatus(transactionRequest.getTransaction().getTxnStatus())
//				 .txnId(transactionRequest.getTransaction().getTxnId())
//				 .gateway(transactionRequest.getTransaction().getGateway())
//				 .billId(transactionDetails.getBillId())
//				 .productInfo(transactionRequest.getTransaction().getProductInfo())
//				 .tenantId(transactionRequest.getTransaction().getTenantId())
//				 .user(transactionRequest.getTransaction().getUser())
//				 .taxAndPayments(transactionRequest.getTransaction().getTaxAndPayments())
//				 .consumerCode(transactionDetails.getConsumerCode())
//				 .orderId(transactionRequest.getTransaction().getOrderId())
//				 .module(transactionDetails.getModule())
//				 .isMultiTransaction(transactionRequest.getTransaction().getIsMultiTransaction())
//				 .txnStatusMsg(transactionRequest.getTransaction().getTxnStatusMsg())
//				 .receipt(transactionRequest.getTransaction().getReceipt())
//				 .additionalDetails(transactionRequest.getTransaction().getAdditionalDetails())
//				 .auditDetails(auditDetails).build();
		 TransactionRequest request = TransactionRequest.builder()
				 .requestInfo(transactionRequest.getRequestInfo())
				 .transaction(transaction)
				.build();
		 return request;
	}

}
