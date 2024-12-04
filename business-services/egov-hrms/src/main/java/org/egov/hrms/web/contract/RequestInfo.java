package org.egov.hrms.web.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestInfo {

    @JsonProperty("apiId")
    private String apiId;

    @JsonProperty("ver")
    private String ver;

    @JsonProperty("action")
    private String action;

    @JsonProperty("did")
    private String did;

    @JsonProperty("key")
    private String key;

    @JsonProperty("msgId")
    private String msgId;

    @JsonProperty("requesterId")
    private String requesterId;

    @JsonProperty("authToken")
    private String authToken;
}
