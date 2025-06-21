package org.egov.pg.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Set;

import org.egov.pg.models.Transaction;
import org.egov.pg.models.enums.TxnSettlementStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TransactionCriteria {

    private String tenantId;

    private String txnId;

    private String billId;

    private String userUuid;

    private String receipt;

    private String consumerCode;
    
    private String gateway;

    @JsonIgnore
    private Long createdTime;

    private Transaction.TxnStatusEnum txnStatus;
    
    @JsonIgnore
    private Long startDateTime;
    
    private String txnSettlementStatus;
    
    private Set<String> txnSettlementStatusses;
    
    private Set<String> notTxnSettlementStatusses;
    
    @JsonIgnore
    private Long endDateTime;

    @JsonIgnore
    @Builder.Default
    private int limit = 500;

    @JsonIgnore
    @Builder.Default
    private int offset = 0;
    
    @Builder.Default
	private Boolean isSchedulerCall = false;

}
