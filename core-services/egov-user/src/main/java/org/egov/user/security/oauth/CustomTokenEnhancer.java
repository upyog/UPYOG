package org.egov.user.security.oauth;

import lombok.extern.slf4j.Slf4j;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Token enhancer that:
 * 1. Sets OAuth2 access tokens as HttpOnly cookies for security
 * 2. Adds ResponseInfo and UserRequest to token response for backward compatibility
 */
@Component
@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    private CookieUtil cookieUtil;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;

        // Add ResponseInfo and UserRequest to token (for backward compatibility)
        SecureUser su = (SecureUser) authentication.getUserAuthentication().getPrincipal();
        final Map<String, Object> info = new LinkedHashMap<String, Object>();
        final Map<String, Object> responseInfo = new LinkedHashMap<String, Object>();

        responseInfo.put("api_id", "");
        responseInfo.put("ver", "");
        responseInfo.put("ts", "");
        responseInfo.put("res_msg_id", "");
        responseInfo.put("msg_id", "");
        responseInfo.put("status", "Access Token generated successfully");
        info.put("ResponseInfo", responseInfo);
        info.put("UserRequest", su.getUser());

        token.setAdditionalInformation(info);

        // Set cookies for secure web authentication
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        log.info("=== CustomTokenEnhancer: Starting cookie creation for user: {} ===",
            authentication.getName());
        log.info("Access Token (first 20 chars): {}...",
            accessToken.getValue().substring(0, Math.min(20, accessToken.getValue().length())));

        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();
            log.info("ServletRequestAttributes found, response object: {}",
                response != null ? "Available" : "NULL");

            if (response != null) {
                // Set access token as HttpOnly cookie
                log.info("Setting access_token cookie with path: /");
                cookieUtil.setAccessTokenCookie(response, accessToken.getValue());

                // Set refresh token as HttpOnly cookie if available
                if (accessToken.getRefreshToken() != null) {
                    log.info("Setting refresh_token cookie with path: /user/oauth");
                    cookieUtil.setRefreshTokenCookie(response,
                        accessToken.getRefreshToken().getValue());
                } else {
                    log.warn("No refresh token available to set in cookie");
                }

                // Add SameSite attribute
                cookieUtil.addSameSiteAttribute(response);

                // Log all Set-Cookie headers
                String setCookieHeader = response.getHeader("Set-Cookie");
                log.info("Set-Cookie header after setting: {}", setCookieHeader);

                log.info("=== OAuth2 tokens successfully set as HttpOnly cookies for user: {} ===",
                    authentication.getName());
            } else {
                log.error("HttpServletResponse is NULL - cookies cannot be set!");
            }
        } else {
            log.error("ServletRequestAttributes is NULL - not in HTTP request context!");
        }

        // Return the enhanced token (includes both additionalInformation and cookies)
        return token;
    }
}
