package org.egov.dx.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import org.egov.dx.util.Configurations;
import org.egov.dx.web.models.IdGenerationResponse;
import org.egov.dx.web.models.RequestInfoWrapper;
import org.egov.dx.web.models.Transaction;
import org.egov.dx.repository.TransactionRepository;
import org.egov.common.contract.request.RequestInfo;
import org.egov.dx.repository.IdGenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IdGenService {


    private IdGenRepository idGenRepository;
    

    private TransactionRepository transactionRepository;
    
    @Autowired
    private Configurations config;

    @Autowired
    public IdGenService(IdGenRepository idGenRepository, TransactionRepository transactionRepository, Configurations
            config) {
        this.idGenRepository = idGenRepository;
        this.config = config;
    }

    /**
     * Generate a transaction id from the ID Gen service
     * *
     *
     * @param transactionRequest Request for which ID should be generated
     * @return Transaction ID
     */
    String generateTxnId(RequestInfoWrapper requestInfoWrapper) {
    	
        RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
        Transaction transaction = requestInfoWrapper.getTransaction();
        
        IdGenerationResponse response = idGenRepository.getId(requestInfo, transaction.getTenantId(),
                config.getIdGenName(), config.getIdGenFormat(), 1);

        String txnId = response.getIdResponses().get(0).getId();
        log.info("Transaction ID Generated: " + txnId);
//        transactionRequest.getTransaction().setTxnId(txnId);

        return txnId;

    }


}

