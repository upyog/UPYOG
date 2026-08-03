package org.egov.noc.web.model.aai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request model for AAI NOCAS API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AAIStatusRequest {

    @NotNull
    private String tokenKey;

    @NotNull
    private List<String> applicationNumbers;

    private String tenantId;
}

