package org.upyog.chb.web.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bank account details used for refund purpose
 */
@ApiModel(description = "Bank account details used for refund purpose")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankDetails   {
	
	    private String bankDetailId;
	    
	    private String bookingId;
	
        @JsonProperty("accountNumber")
        private String accountNumber = null;

        @JsonProperty("ifscCode")
        private String ifscCode = null;

        @JsonProperty("bankName")
        private String bankName = null;

        @JsonProperty("bankBranchName")
        private String bankBranchName = null;

        @JsonProperty("accountHolderName")
        private String accountHolderName = null;
        
        private String refundStatus;
        
        private AuditDetails auditDetails;

}

