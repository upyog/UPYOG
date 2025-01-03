package org.egov.pg.web.models;

//import java.util.List;
import java.util.Map;

//
//import org.egov.common.contract.request.RequestInfo;
//import org.egov.pg.models.Transaction;
//
//import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//
//import org.egov.common.contract.request.RequestInfo;
//import org.egov.pg.models.Transaction;
//
//import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckSumTransaction {
    private String paytmMid;
    private String paytmTid;
    private String transactionDateTime;
    private String merchantTransactionId;
    private String merchantReferenceNo;
    private String transactionAmount;
    private Map<String, String> merchantExtendedInfo;
}
