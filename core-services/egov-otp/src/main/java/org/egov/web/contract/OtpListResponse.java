package org.egov.web.contract;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.domain.model.Token;

@Getter
public class OtpListResponse {
    private ResponseInfo responseInfo;
    private List<Otp> otp;

    public OtpListResponse(List<Token> token) {
    	
    	        if (token != null) {
    	            otp = token.stream().map(x->new Otp(x)).collect(Collectors.toList());
    	        }
    	    
    }
}


