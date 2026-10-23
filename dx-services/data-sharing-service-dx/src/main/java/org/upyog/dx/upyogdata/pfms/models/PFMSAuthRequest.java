package org.upyog.dx.upyogdata.pfms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body sent to PFMS authentication API.
 * Contains the username and password needed to get a Bearer token.
 */

@Getter
@Setter
@AllArgsConstructor
public class PFMSAuthRequest {

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("Password")
    private String password;
}
