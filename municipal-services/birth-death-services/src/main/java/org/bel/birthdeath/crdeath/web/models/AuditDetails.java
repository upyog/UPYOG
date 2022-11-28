package org.bel.birthdeath.crdeath.web.models;
import com.fasterxml.jackson.annotation.JsonProperty;
//import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Schema(description = "Collection of audit related fields used by most models")
/**
     * Creates Audit details Model 
     * Rakhi S IKM
     * 
     */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditDetails {
    @Size(max = 64)
    @JsonProperty("createdBy")
    private String createdBy;

    @Size(max = 64)
    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;

    @JsonProperty("createdTime")
    private Long createdTime;

    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
}
