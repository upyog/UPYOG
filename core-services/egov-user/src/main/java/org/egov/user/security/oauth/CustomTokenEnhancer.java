package org.egov.user.security.oauth;

import lombok.extern.slf4j.Slf4j;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Token enhancer that:
 * 1. Revokes old tokens before setting new ones (logout-on-login pattern)
 * 2. Sets OAuth2 access tokens as HttpOnly cookies for security
 * 3. Adds ResponseInfo and UserRequest to token response for backward compatibility
 */
@Component
@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private TokenStore tokenStore;

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
            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();
            log.info("ServletRequestAttributes found, response object: {}",
                response != null ? "Available" : "NULL");

            if (response != null && request != null) {
                // SECURITY: Logout-on-login pattern - revoke old tokens before setting new ones
                // This prevents session fixation attacks and ensures clean session state
                revokeOldTokensIfPresent(request);

                // Clear old cookies before setting new ones
                log.info("Clearing old authentication cookies before setting new ones");
                cookieUtil.clearAuthCookies(response);

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

    /**
     * Revokes old tokens from Redis TokenStore if they exist in the request cookies
     * This implements the logout-on-login security pattern
     *
     * Security Benefits:
     * 1. Prevents session fixation attacks
     * 2. Ensures only one active session per user at a time
     * 3. Invalidates old tokens in backend (not just browser)
     * 4. Provides clean session state on each login
     *
     * @param request HttpServletRequest containing potential old cookies
     */
    private void revokeOldTokensIfPresent(HttpServletRequest request) {
        try {
            // Check if old access token exists in cookies
            String oldAccessToken = cookieUtil.getAccessTokenFromCookie(request);

            if (oldAccessToken != null && !oldAccessToken.isEmpty()) {
                log.info("Found existing access_token in cookies, revoking from Redis...");

                // Read the token from Redis
                OAuth2AccessToken redisToken = tokenStore.readAccessToken(oldAccessToken);

                if (redisToken != null) {
                    // Remove access token and associated refresh token from Redis
                    tokenStore.removeAccessToken(redisToken);
                    log.info("Old access_token revoked successfully from Redis");

                    // If there's a refresh token, revoke it too
                    if (redisToken.getRefreshToken() != null) {
                        tokenStore.removeRefreshToken(redisToken.getRefreshToken());
                        log.info("Old refresh_token revoked successfully from Redis");
                    }
                } else {
                    log.info("Old access_token not found in Redis (may have already expired)");
                }
            } else {
                log.info("No existing access_token found in cookies (first login or cookies cleared)");
            }
        } catch (Exception e) {
            // Don't fail the login if token revocation fails
            log.error("Error while revoking old tokens (continuing with login): {}", e.getMessage());
        }
    }
}
