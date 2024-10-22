package org.upyog.sv.web.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.web.models.common.AuditDetails;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Details of the community halls booking
 */
@ApiModel(description = "Details of the bank details")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BankDetail   {
	
	private String id;
	
	private String applicationId;
    @NotBlank
    @Size(min = 8, max = 18)
    private String accountNumber;

    @NotBlank
    private String ifscCode;

    @NotBlank
    private String bankName;

    @NotBlank
    private String bankBranchName;

    @NotBlank
    private String accountHolderName;
    
    private String refundType;
    
    private String refundStatus;
    
    private AuditDetails auditDetails;
    
}

