package org.egov.pg.service;

import static java.util.Collections.singletonMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.constants.PgConstants;
import org.egov.pg.constants.TransactionAdditionalFields;
import org.egov.pg.models.AuditDetails;
import org.egov.pg.models.BankAccount;
import org.egov.pg.models.Transaction;
import org.egov.pg.repository.BankAccountRepository;
import org.egov.pg.web.models.TransactionRequest;
import org.egov.pg.web.models.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static java.util.Collections.singletonMap;
import lombok.extern.slf4j.Slf4j;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

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

        BankAccount bankAccount = bankAccountRepository.getBankAccountsById(requestInfo, transaction.getTenantId(), transaction.getBusinessService());
        String accountWithSeprator = bankAccount.getAccountNumber();
        String accountNumber=accountWithSeprator;
        if(accountWithSeprator.contains("/"))
        accountNumber = accountWithSeprator.substring(accountWithSeprator.lastIndexOf("/")+1);
        transaction.setAdditionalFields(singletonMap(TransactionAdditionalFields.BANK_ACCOUNT_NUMBER, accountNumber));

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
      }  else{
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
        newTxn.setTxnStatusMsg(currentTxnStatus.getTxnStatusMsg());
        newTxn.setReceipt(currentTxnStatus.getReceipt());
        newTxn.setBusinessService(currentTxnStatus.getBusinessService());

    }

  
    /**
	 * Converts startDay to epoch
	 * 
	 * @param startDay
	 *            StartDay of applicable
	 * @return Returns start day in milli seconds
	 */
	private Long getStartDayInMillis(String startDay) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date date = sdf.parse(startDay);
			return date.getTime();
		} catch (ParseException e) {
			throw new CustomException("INVALID_START_DAY", "The startDate of the penalty cannot be parsed");
		}
    }

    void enrichUpdateTransactionForCCAvanue(RequestInfo requestInfo2, List<String> keyValueList, Transaction newTxn, Transaction currentTxnStatus) {
        RequestInfo requestInfo = requestInfo2;
        //Transaction currentTxnStatus = keyValueList.getTransaction();
      //;

        String transactionStatus= Arrays.stream(keyValueList.get(3).split("=")).skip(1).findFirst().orElse(null);
        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(currentTxnStatus.getAuditDetails().getCreatedBy())
                .createdTime(getStartDayInMillis(Arrays.stream(keyValueList.get(40).split("=")).skip(1).findFirst().orElse(null)))
                .lastModifiedBy(currentTxnStatus.getAuditDetails().getLastModifiedBy())
                .lastModifiedTime(System.currentTimeMillis()).build();
        newTxn.setAuditDetails(auditDetails);

        newTxn.setTxnId(Arrays.stream(keyValueList.get(0).split("=")).skip(1).findFirst().orElse(null));
        newTxn.setGateway(currentTxnStatus.getGateway());
        newTxn.setBillId(currentTxnStatus.getBillId());
        newTxn.setProductInfo(currentTxnStatus.getProductInfo());
        newTxn.setTenantId(currentTxnStatus.getTenantId());
        newTxn.setUser(currentTxnStatus.getUser());
        newTxn.setAdditionalDetails(currentTxnStatus.getAdditionalDetails());
        newTxn.setTaxAndPayments(currentTxnStatus.getTaxAndPayments());
        newTxn.setConsumerCode(currentTxnStatus.getConsumerCode());
        newTxn.setTxnStatusMsg(Arrays.stream(keyValueList.get(8).split("=")).skip(1).findFirst().orElse(null));
        //newTxn.setReceipt(currentTxnStatus.getReceipt());
        newTxn.setBusinessService(currentTxnStatus.getBusinessService());
        newTxn.setGatewayPaymentMode(Arrays.stream(keyValueList.get(5).split("=")).skip(1).findFirst().orElse(null));
        newTxn.setGatewayStatusMsg(Arrays.stream(keyValueList.get(8).split("=")).skip(1).findFirst().orElse(null));
        newTxn.setGatewayTxnId(Arrays.stream(keyValueList.get(0).split("=")).skip(1).findFirst().orElse(null));
        newTxn.setReceipt(currentTxnStatus.getReceipt());
        if(transactionStatus.contentEquals("Success")) {
        newTxn.setTxnStatus(Transaction.TxnStatusEnum.SUCCESS);
        }
    }


	
}
