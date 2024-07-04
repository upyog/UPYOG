package org.upyog.chb.web.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
	
	    @NotBlank
        @JsonProperty("accountNumber")
	    @Size(min = 8, max = 18)
        private String accountNumber = null;

	    @NotBlank
        @JsonProperty("ifscCode")
        private String ifscCode = null;

	    @NotBlank
        @JsonProperty("bankName")
        private String bankName = null;

	    @NotBlank
        @JsonProperty("bankBranchName")
        private String bankBranchName = null;

	    @NotBlank
        @JsonProperty("accountHolderName")
        private String accountHolderName = null;
        
        private String refundStatus;
        
        private AuditDetails auditDetails;

}

