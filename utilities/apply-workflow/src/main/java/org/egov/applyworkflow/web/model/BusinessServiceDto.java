package org.egov.applyworkflow.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessServiceDto {

    @NotNull
    private String tenantId;

    @NotNull
    private String uniqueIdentifier;

    @NotNull
    private String applyType;
}
