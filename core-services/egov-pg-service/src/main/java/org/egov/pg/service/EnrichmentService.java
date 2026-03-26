package org.egov.pg.service;

import static java.util.Collections.singletonMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.egov.pg.models.Refund;
import org.egov.pg.models.Refund.RefundStatusEnum;
import org.egov.pg.models.RefundRequest;
import org.egov.pg.models.Transaction;
import org.egov.pg.repository.BankAccountRepository;
import org.egov.pg.repository.TransactionRepository;
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

//	public void enrichInitiateRefundRequest(@Valid RefundRequest refundRequest) {
//		 RequestInfo requestInfo = refundRequest.getRequestInfo();
//		 Refund refund = refundRequest.getRefund();
//		 
//		 refund.setId(UUID.randomUUID().toString());
//		 // Generate ID from ID Gen service and assign to refund object
//		 setIdFromIdGen(refundRequest);
//		 attachOriginalTransactionDetails(refund);
//		 refund.setStatus(Refund.RefundStatusEnum.INITIATED);
//		 
//		 AuditDetails auditDetails = AuditDetails.builder()
//	                .createdBy(requestInfo.getUserInfo() != null ? requestInfo.getUserInfo().getUuid() : null)
//	                .createdTime(System.currentTimeMillis())
//	                .build();
//	        refund.setAuditDetails(auditDetails);
//	}
//
//	private void attachOriginalTransactionDetails(Refund refund) {
//		TransactionCriteria criteria = TransactionCriteria.builder().txnId(refund.getOriginalTxnId()).build();
//		List<Transaction> statuses = transactionRepository.fetchTransactions(criteria);
//
//		if (statuses == null || statuses.isEmpty()) {
//		    throw new CustomException("TXN_NOT_FOUND", "No transaction found for given criteria");
//		}
//
//		String atomTxnId = statuses.get(0).getAtomTxnId();
//		String consumerCode = statuses.get(0).getConsumerCode();
//		refund.setAtomTxnId(atomTxnId);
//        refund.setConsumerCode(consumerCode);
//
//	}

	private void setIdFromIdGen(@Valid RefundRequest refundRequest) {
//		String refundId = idGenService.generateRefundId(refundRequest);
		String refundId = "PG-1212-refund";
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
		  Transaction transaction = transactions.get(0);
		  Refund refund = refundRequest.getRefund();
		  refundRequest.setRequestInfo(requestInfo);
		  
		  refund.setId(UUID.randomUUID().toString());
		  setIdFromIdGen(refundRequest);
		  
		  refund.setOriginalTxnId(transaction.getTxnId());
		  refund.setRefundAmount(transaction.getTxnAmount());
		  refund.setOriginalAmount(transaction.getTxnAmount());
		  refund.setAtomTxnId(transaction.getAtomTxnId());
		  refund.setConsumerCode(transaction.getConsumerCode());
		  refund.setGateway(transaction.getGateway());
		  refund.setGatewayTxnId(transaction.getGatewayTxnId());
		  
		  refund.setStatus(Refund.RefundStatusEnum.INITIATED);
			 
			 AuditDetails auditDetails = AuditDetails.builder()
		                .createdBy(requestInfo.getUserInfo() != null ? requestInfo.getUserInfo().getUuid() : null)
		                .createdTime(System.currentTimeMillis())
		                .build();
		        refund.setAuditDetails(auditDetails);
		  
		return refundRequest;
	}


}
