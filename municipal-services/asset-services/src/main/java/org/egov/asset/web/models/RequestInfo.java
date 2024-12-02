package org.egov.asset.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.egov.asset.web.models.UserInfo;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * RequestInfo should be used to carry meta information about the requests to the server as described in the fields below. All eGov APIs will use requestinfo as a part of the request body to carry this meta information. Some of this information will be returned back from the server as part of the ResponseInfo in the response body to ensure correlation.
 */
@ApiModel(description = "RequestInfo should be used to carry meta information about the requests to the server as described in the fields below. All eGov APIs will use requestinfo as a part of the request body to carry this meta information. Some of this information will be returned back from the server as part of the ResponseInfo in the response body to ensure correlation.")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestInfo   {
        @JsonProperty("apiId")
        private String apiId = null;

        @JsonProperty("ver")
        private String ver = null;

        @JsonProperty("ts")
        private Long ts = null;

        @JsonProperty("action")
        private String action = null;

        @JsonProperty("did")
        private String did = null;

        @JsonProperty("key")
        private String key = null;

        @JsonProperty("msgId")
        private String msgId = null;

        @JsonProperty("requesterId")
        private String requesterId = null;

        @JsonProperty("authToken")
        private String authToken = null;

        @JsonProperty("userInfo")
        private UserInfo userInfo = null;

        @JsonProperty("correlationId")
        private String correlationId = null;


}

