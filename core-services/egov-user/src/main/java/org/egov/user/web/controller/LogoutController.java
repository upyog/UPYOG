package org.egov.user.web.controller;

import org.egov.common.contract.response.Error;
import org.egov.common.contract.response.ErrorResponse;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.user.domain.model.TokenWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.web.bind.annotation.*;

// REMOVED DEPRECATED IMPORTS:
// import org.springframework.security.oauth2.core.OAuth2AccessToken;
// import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.Date;

@RestController
public class LogoutController {

    // CHANGED: TokenStore -> OAuth2AuthorizationService
    private OAuth2AuthorizationService authorizationService;

    // UPDATED CONSTRUCTOR
    public LogoutController(OAuth2AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * End-point to logout the session.
     * Updated to work with OAuth2AuthorizationService
     *
     * @param tokenWrapper containing the access token
     * @return ResponseInfo indicating success or failure
     * @throws Exception
     */
    @PostMapping("/_logout")
    public ResponseInfo deleteToken(@RequestBody TokenWrapper tokenWrapper) throws Exception {
        String accessToken = tokenWrapper.getAccessToken();
        
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token is required");
        }
        
        // UPDATED: Find authorization by access token
        OAuth2Authorization authorization = authorizationService.findByToken(accessToken, OAuth2TokenType.ACCESS_TOKEN);
        
        if (authorization != null) {
            // UPDATED: Remove the entire authorization (which includes all associated tokens)
            authorizationService.remove(authorization);
            return new ResponseInfo("", "", System.currentTimeMillis(), "", "", "Logout successfully");
        } else {
            // Token not found or already expired
            return new ResponseInfo("", "", System.currentTimeMillis(), "", "", "Token not found or already expired");
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleError(Exception ex) {
        ex.printStackTrace();
        ErrorResponse response = new ErrorResponse();
        ResponseInfo responseInfo = new ResponseInfo("", "", System.currentTimeMillis(), "", "", "Logout failed");
        response.setResponseInfo(responseInfo);
        Error error = new Error();
        error.setCode(400);
        error.setDescription("Logout failed: " + ex.getMessage());
        response.setError(error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
