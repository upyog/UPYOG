package org.egov.hrms.web.contract;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AuthenticateUserInputRequest {
    private String userName;
    private String tokenName;
    //private String serviceName;

    // Constructors
    public AuthenticateUserInputRequest() {}

    public AuthenticateUserInputRequest(String userName, String tokenName, String serviceName) {
        this.userName = userName;
        this.tokenName = tokenName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenname(String tokenName) {
        this.tokenName = tokenName;
    }

}
