package org.egov.garbageservice.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;

/**
 * API request wrapper for creating or updating one or more garbage accounts.
 * Carries RequestInfo plus garbageAccounts list and flags for migration or child-only creation.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GarbageAccountRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    private List<GarbageAccount> garbageAccounts;

    @Builder.Default
    private Boolean fromMigration = false;

    @Builder.Default
    private Boolean createChildAccountOnly = false;

}
