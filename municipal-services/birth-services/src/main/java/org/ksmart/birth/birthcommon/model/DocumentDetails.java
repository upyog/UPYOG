package org.ksmart.birth.birthcommon.model;

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
@ApiModel(description = "A Object holds the basic data for a Trade License")
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
    @Size(max=64)
    @SafeHtml
    @JsonProperty("tenantId")
    private String tenantId = null;
    @Size(max=2500)
    @SafeHtml
    @JsonProperty("documentName")
    private String documentName = null;
    @Size(max=64)
    @SafeHtml
    @JsonProperty("documentType")
    private String documentType = null;
    @SafeHtml
    @JsonProperty("documentLink")
    private String documentLink= null;
    @Size(max = 250)
    @JsonProperty("description")
    private String description;
    @Size(max=64)
    @SafeHtml
    @JsonProperty("fileStoreId")
    private String fileStoreId = null;
    @JsonProperty("fileType")
    private String fileType;
    @JsonProperty("fileSize")
    private Long fileSize;
    @Size(max=64)
    @SafeHtml
    @JsonProperty("documentUid")
    private String documentUid;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails = null;

}

