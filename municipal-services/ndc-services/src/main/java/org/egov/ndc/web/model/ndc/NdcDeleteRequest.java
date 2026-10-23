package org.egov.ndc.web.model.ndc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.egov.common.contract.request.RequestInfo;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NdcDeleteRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("active")
    private Boolean active;
}