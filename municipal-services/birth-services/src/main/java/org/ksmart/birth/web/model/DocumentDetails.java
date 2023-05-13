package org.ksmart.birth.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.hibernate.validator.constraints.SafeHtml;
import org.ksmart.birth.common.model.AuditDetails;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;

/**
 * A Object holds the basic data for a Trade License
 */
@ApiModel(description = "A Object holdsdocuments of a birth ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class DocumentDetails {

    @Size(max=64)
    @SafeHtml
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("documentName")
    private String documentName;
    
    @JsonProperty("documentDescription")
    private String documentDescription;

    @JsonProperty("active")
    private Boolean active;

    @Size(max=64)    
    @JsonProperty("tenantId")
    private String tenantId;

    @Size(max=64)    
    @JsonProperty("documentType")
    private String documentType;

    @Size(max=64)   
    @JsonProperty("fileStoreId")
    private String fileStoreId;
    
    @JsonProperty("documentLink")
    private String documentLink;
    
    @JsonProperty("fileType")
    private String fileType;
    
    @JsonProperty("fileSize")
    private String fileSize;

    @JsonProperty("proceedNoRDO")
    private String proceedNoRDO;

    @JsonProperty("regNoNAC")
    private String regNoNAC;

    @Size(max=64)   
    @JsonProperty("birthdtlid")
    private String birthdtlid;

    @Size(max = 64)
	@JsonProperty("parentBrthDtlId")
	private String parentBrthDtlId;
    
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}

