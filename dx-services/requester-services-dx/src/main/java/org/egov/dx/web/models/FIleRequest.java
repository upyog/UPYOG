package org.egov.dx.web.models;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FIleRequest {


	 @JsonProperty("RequestInfo")
     @NotNull
     private RequestInfo requestInfo;

     @JsonProperty("TokenReq")
     @Valid
     private  TokenReq tokenReq;

}
