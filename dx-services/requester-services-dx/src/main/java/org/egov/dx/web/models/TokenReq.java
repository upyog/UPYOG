package org.egov.dx.web.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenReq {


	 @JsonProperty("code")
     private String code;

     @JsonProperty("code_verifier")
     private  String codeVerifier;

     @JsonProperty("module")
     private  String module;
     
     @JsonProperty("authToken")
     private  String authToken;
     
     @JsonProperty("id")
     private  String id;
}
