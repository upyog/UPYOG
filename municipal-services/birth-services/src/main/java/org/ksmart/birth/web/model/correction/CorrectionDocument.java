package org.ksmart.birth.web.model.correction;

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
public class CorrectionDocument {

    @Size(max=64)
    @SafeHtml
    @JsonProperty("id")
    private String id;
    @Size(max=64)
    @SafeHtml
    @JsonProperty("birthId")
    private String birthId;
    @JsonProperty("correctionId")
    private String correctionId;
    @Size(max=128)
    @SafeHtml
    @JsonProperty("correctionFieldName")
    private String correctionFieldName = null;
    @Size(max=128)
    @SafeHtml
    @JsonProperty("documentType")
    private String documentType = null;
    @Size(max=1024)
    @SafeHtml
    @JsonProperty("fileStoreId")
    private String fileStoreId = null;
    @Size(max=64)
    @SafeHtml
    @JsonProperty("active")
    private Boolean active = true;
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}

