package org.egov.user.web.contract;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.user.config.UserServiceConstants;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Size(max = 64, min = 1)
    @JsonProperty("username")
    private String username;

    @Size(max = 64)
    @JsonProperty("password")
    private String password;

    @JsonProperty("otp")
    private String otp;

    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
    @Size(max = 50, min = 1)
    private String tenantId;

    @JsonProperty("userType")
	 private String userType ;
}
