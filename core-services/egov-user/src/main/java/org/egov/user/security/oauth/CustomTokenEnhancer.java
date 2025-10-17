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

        // Return the enhanced token (includes both additionalInformation and cookies)
        return token;
    }
}
