package org.egov.user.security.oauth;

import lombok.extern.slf4j.Slf4j;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Token enhancer that:
 * 1. Implements SINGLE-SESSION authentication (only one device active per user)
 * 2. Revokes ALL old tokens for user before setting new ones
 * 3. Sets OAuth2 access tokens as HttpOnly cookies for security
 * 4. Adds ResponseInfo and UserRequest to token response for backward compatibility
 */
@Component
@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Redis key prefix for tracking user's active tokens
    private static final String USER_TOKEN_KEY_PREFIX = "user:session:";

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
                // SECURITY: SINGLE-SESSION - Revoke ALL tokens for this user (logout other devices)
                String username = authentication.getName();
                revokeAllUserTokens(username);

                // Store new token mapping for this user in Redis
                storeUserTokenMapping(username, accessToken);

                // Note: No need to explicitly clear cookies - setting a cookie with same name/path
                // automatically replaces the old one in browser. Clearing causes conflicts.

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
     * SINGLE-SESSION IMPLEMENTATION
     * Revokes ALL tokens for a given username across all devices
     *
     * When user logs in on Device 2, this will:
     * 1. Find all tokens associated with this username
     * 2. Remove those tokens from Redis TokenStore
     * 3. Effectively logout the user from all other devices
     *
     * Security Benefits:
     * 1. Only one active session per user at a time
     * 2. Prevents token theft/reuse across devices
     * 3. User can "kick out" attacker by logging in again
     * 4. High-security pattern for sensitive applications
     *
     * @param username Username whose tokens should be revoked
     */
    private void revokeAllUserTokens(String username) {
        try {
            log.info("=== SINGLE-SESSION: Revoking all existing tokens for user: {} ===", username);

            // Get Redis key for this user's token mapping
            String userTokenKey = USER_TOKEN_KEY_PREFIX + username;

            // Get all token IDs stored for this user
            Set<String> tokenIds = redisTemplate.opsForSet().members(userTokenKey);

            if (tokenIds != null && !tokenIds.isEmpty()) {
                log.info("Found {} existing token(s) for user: {}", tokenIds.size(), username);

                int revokedCount = 0;
                for (String tokenId : tokenIds) {
                    try {
                        // Read token from TokenStore
                        OAuth2AccessToken token = tokenStore.readAccessToken(tokenId);

                        if (token != null) {
                            // Remove access token
                            tokenStore.removeAccessToken(token);
                            revokedCount++;

                            // Remove refresh token if exists
                            if (token.getRefreshToken() != null) {
                                tokenStore.removeRefreshToken(token.getRefreshToken());
                            }

                            log.debug("✓ Revoked token: {}...", tokenId.substring(0, Math.min(20, tokenId.length())));
                        }
                    } catch (Exception e) {
                        log.warn("Failed to revoke token {}: {}", tokenId, e.getMessage());
                    }
                }

                // Clear the user's token set in Redis
                redisTemplate.delete(userTokenKey);

                log.info("✓ Successfully revoked {} token(s) for user: {}", revokedCount, username);
                log.info("✓ All other devices for user {} are now logged out", username);
            } else {
                log.info("No existing tokens found for user: {} (first login)", username);
            }
        } catch (Exception e) {
            // Don't fail the login if token revocation fails
            log.error("Error while revoking user tokens (continuing with login): {}", e.getMessage(), e);
        }
    }

    /**
     * Stores mapping of username to token ID in Redis
     * This allows us to find and revoke all tokens for a user during next login
     *
     * @param username Username to map
     * @param accessToken Token to store
     */
    private void storeUserTokenMapping(String username, OAuth2AccessToken accessToken) {
        try {
            String userTokenKey = USER_TOKEN_KEY_PREFIX + username;
            String tokenId = accessToken.getValue();

            // Add token ID to user's set in Redis
            redisTemplate.opsForSet().add(userTokenKey, tokenId);

            // Set expiration matching token expiration (24 hours + buffer)
            redisTemplate.expire(userTokenKey, 25, TimeUnit.HOURS);

            log.info("✓ Stored token mapping for user: {} → token: {}...",
                username, tokenId.substring(0, Math.min(20, tokenId.length())));
        } catch (Exception e) {
            log.error("Error storing user-token mapping: {}", e.getMessage());
        }
    }
}
