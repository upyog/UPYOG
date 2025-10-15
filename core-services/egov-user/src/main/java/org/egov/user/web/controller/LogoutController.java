package org.egov.user.web.controller;

import org.egov.common.contract.response.Error;
import org.egov.common.contract.response.ErrorResponse;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.user.domain.model.TokenWrapper;
import org.egov.user.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
public class LogoutController {

    private TokenStore tokenStore;

    @Autowired
    private CookieUtil cookieUtil;

    public LogoutController(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    /**
     * End-point to logout the session.
     * Now also clears HttpOnly cookies for secure token removal
     *
     * @param tokenWrapper
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/_logout")
    public ResponseInfo deleteToken(@RequestBody TokenWrapper tokenWrapper,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        // Get access token from request body or cookie
        String accessToken = tokenWrapper.getAccessToken();

        // If not in body, try to get from cookie
        if (accessToken == null || accessToken.isEmpty()) {
            accessToken = cookieUtil.getAccessTokenFromCookie(request);
        }

        // Remove token from Redis/TokenStore
        if (accessToken != null && !accessToken.isEmpty()) {
            OAuth2AccessToken redisToken = tokenStore.readAccessToken(accessToken);
            if (redisToken != null) {
                tokenStore.removeAccessToken(redisToken);
            }
        }

        // Clear authentication cookies
        cookieUtil.clearAuthCookies(response);

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