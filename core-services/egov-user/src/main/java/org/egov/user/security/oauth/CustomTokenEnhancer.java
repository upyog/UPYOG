package org.egov.user.security.oauth;

import lombok.extern.slf4j.Slf4j;
import org.egov.user.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 * Token enhancer that sets OAuth2 access tokens as HttpOnly cookies
 * This provides additional security by preventing JavaScript access to tokens
 */
@Component
@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    private CookieUtil cookieUtil;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        // Check if we're in a web request context
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();

            if (response != null) {
                // Set access token as HttpOnly cookie
                cookieUtil.setAccessTokenCookie(response, accessToken.getValue());

                // Set refresh token as HttpOnly cookie if available
                if (accessToken.getRefreshToken() != null) {
                    cookieUtil.setRefreshTokenCookie(response,
                        accessToken.getRefreshToken().getValue());
                }

                // Add SameSite attribute
                cookieUtil.addSameSiteAttribute(response);

                log.info("OAuth2 tokens set as HttpOnly cookies for user: {}",
                    authentication.getName());
            }
        }

        // Return the original token (still included in response body for backward compatibility)
        return accessToken;
    }
}
