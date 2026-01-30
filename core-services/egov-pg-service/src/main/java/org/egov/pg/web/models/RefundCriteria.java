package org.egov.pg.web.models;

import org.egov.pg.models.Refund;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RefundCriteria {

	    private String tenantId;

	    private String refundId;
	    
	    private String originalTxnId;

	    @JsonIgnore
	    private Long createdTime;

	    private Refund.RefundStatusEnum status;

	    @JsonIgnore
	    private int limit;

	    @JsonIgnore
	    private int offset;
}
