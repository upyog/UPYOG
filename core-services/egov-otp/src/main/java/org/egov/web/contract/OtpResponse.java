package org.egov.web.contract;

import lombok.Getter;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.domain.model.Token;

@Getter
public class OtpResponse {
    private ResponseInfo responseInfo;
    private Otp otp;
    private String accessToken;

    public OtpResponse(Token token) {
        if (token != null) {
            otp = new Otp(token);
            accessToken = token.getAccessToken();
        }
    }
}


