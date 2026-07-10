package org.upyog.as.service;

import org.upyog.as.model.payload.UserInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthTokenResponse {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private long expires_in;
    private String scope;
    private UserInfo UserRequest;

    public String getAccessToken() { return access_token; }
    public long getExpiresIn() { return expires_in; }
    public UserInfo getUserRequest() { return UserRequest; }
}