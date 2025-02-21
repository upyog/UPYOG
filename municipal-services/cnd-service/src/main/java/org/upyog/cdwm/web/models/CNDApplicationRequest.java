package org.upyog.cdwm.web.models;

import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CNDApplicationRequest {
	
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @Valid
    @JsonProperty("cndApplication")
    private CNDApplicationDetail cndApplication;
}
