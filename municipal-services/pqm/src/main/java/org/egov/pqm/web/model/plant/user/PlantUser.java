package org.egov.pqm.web.model.plant.user;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.pqm.web.model.AuditDetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Valid
public class PlantUser {
    @JsonProperty("id")
    @Size(
            min = 2,
            max = 64
    )
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("plantCode")
    private String plantCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("plantUserType")
    @NotNull(message = "PlantUserType cannot be null")
    private PlantUserType plantUserType;
    
    @JsonProperty("plantUserUuid")
    @Size(
            min = 2,
            max = 64
    )
    private String plantUserUuid;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
