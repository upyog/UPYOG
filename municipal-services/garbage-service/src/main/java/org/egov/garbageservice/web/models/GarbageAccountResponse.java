package org.egov.garbageservice.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
/**
 * API response returned after garbage account create, update, or search operations.
 * Includes ResponseInfo, matching garbageAccounts, and optional dashboard count aggregates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GarbageAccountResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    private List<GarbageAccount> garbageAccounts;

    @JsonProperty("applicationCount")
    private Integer applicationCount;
}
