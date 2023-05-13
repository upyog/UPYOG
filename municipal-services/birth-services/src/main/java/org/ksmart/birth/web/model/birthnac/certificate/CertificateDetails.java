package org.ksmart.birth.web.model.birthnac.certificate;

import javax.validation.constraints.Size;

import lombok.*;
import org.ksmart.birth.utils.enums.CertificateStatus;
import org.ksmart.birth.common.model.AuditDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateDetails {

	 @Size(max = 64)
     @JsonProperty("id")
     private String id;
	
	 @Size(max = 64)	  
	 @JsonProperty("tenantId")
	 private String tenantId;
	 
	 @Size(max = 64)	  
	 @JsonProperty("bussinessService")
	 private String bussinessService;
	 
	 @Size(max = 64)
	 @JsonProperty("certificateNo")
	 private String certificateNo;
	 
	 @Schema(type = "string",
	            allowableValues = { "ACTIVE", "CANCELLED", "FREE_DOWNLOAD", "PAID_DOWNLOAD", "PAID_PDF_GENERATED", "PAID" },
	            description = "Status of certificate")
	 @JsonProperty("certificateStatus")
	 private CertificateStatus certificateStatus;
	 
	 @Size(max = 64)
	 @JsonProperty("birthdtlId")
	 private String birthdtlId;
	 
	 @Size(max = 64)
	 @JsonProperty("filestoreId")
	 private String filestoreId;
	 
	 @JsonProperty("dateofIssue")
	 private Long dateofIssue;
	 
	 @JsonProperty("auditDetails")
	 private AuditDetails auditDetails;
}
