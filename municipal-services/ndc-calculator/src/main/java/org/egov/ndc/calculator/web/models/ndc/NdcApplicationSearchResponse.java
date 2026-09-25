package org.egov.ndc.calculator.web.models.ndc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NdcApplicationSearchResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;
    @JsonProperty("Applications")
    private List<NdcApplicationRequest> applications;
    private Integer totalCount;
}