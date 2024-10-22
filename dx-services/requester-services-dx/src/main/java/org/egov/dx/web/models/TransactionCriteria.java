package org.egov.dx.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import org.egov.dx.web.models.Transaction;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TransactionCriteria {

    private String tenantId;

    private String txnId;

}
