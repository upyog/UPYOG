package org.egov.dx.web.models;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

	@JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo=null;

    @JsonProperty("TokenRes")
    @Valid
    private TokenRes tokenRes=null;
    
    @JsonProperty("UserRes")
    @Valid
    private UserRes userRes=null;
    
    @JsonProperty("IssuedDoc")
    @Valid
    private List<IssuedDocument> issuedDocument=null;
    }
