package org.egov.user.web.controller;

import org.egov.common.contract.response.Error;
import org.egov.common.contract.response.ErrorResponse;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.user.domain.model.TokenWrapper;
import org.egov.user.persistence.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
public class LogoutController {

    private TokenStore tokenStore;

	 private UserRepository userRepository;
	  
	    public LogoutController(TokenStore tokenStore, UserRepository userRepository) {
	        this.tokenStore = tokenStore;
	        this.userRepository = userRepository;
	    }
    /**
     * End-point to logout the session.
     *
     * @param
     * @return
     * @throws Exception
     */
    @PostMapping("/_logout_old")
    public ResponseInfo deleteToken(@RequestBody TokenWrapper tokenWrapper) throws Exception {
        String accessToken = tokenWrapper.getAccessToken();
        OAuth2AccessToken redisToken = tokenStore.readAccessToken(accessToken);
        tokenStore.removeAccessToken(redisToken);
        return new ResponseInfo("", "", System.currentTimeMillis(), "", "", "Logout successfully");
    }

    
    @PostMapping("/_logout")
    public ResponseInfo deleteToken(@RequestParam("access_token") String accessToken) throws Exception {
      // String accessToken = tokenWrapper.getAccessToken();
        OAuth2AccessToken redisToken = tokenStore.readAccessToken(accessToken);
        Map<String, Object> additionalInfo = redisToken.getAdditionalInformation();
        if (additionalInfo != null && additionalInfo.containsKey("UserRequest")) {
            org.egov.user.web.contract.auth.User userInfo =
                    (org.egov.user.web.contract.auth.User) additionalInfo.get("UserRequest");

            // Update logout time (manual logout)
            userRepository.updateUserLogoutSession(userInfo.getUuid(), false);
        }
        tokenStore.removeAccessToken(redisToken);
        return new ResponseInfo("", "", System.currentTimeMillis(), "", "", "Logout successfully");
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleError(Exception ex) {
        ex.printStackTrace();
        ErrorResponse response = new ErrorResponse();
        ResponseInfo responseInfo = new ResponseInfo("", "", System.currentTimeMillis(), "", "", "Logout failed");
        response.setResponseInfo(responseInfo);
        Error error = new Error();
        error.setCode(400);
        error.setDescription("Logout failed");
        response.setError(error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}