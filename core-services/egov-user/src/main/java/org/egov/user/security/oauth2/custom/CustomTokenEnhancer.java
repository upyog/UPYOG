package org.egov.user.security.oauth2.custom;

import org.egov.user.domain.model.SecureUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomTokenEnhancer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Value("${jwt.token.optimize-size:true}")
    private boolean optimizeTokenSize;

    @Override
    public void customize(JwtEncodingContext context) {
        // Only enhance access tokens
        if (!"access_token".equals(context.getTokenType().getValue())) {
            return;
        }

        Authentication principal = context.getPrincipal();
        
        // Check the grant type
        AuthorizationGrantType grantType = context.getAuthorizationGrantType();
        
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(grantType)) {
            // For client credentials, add basic custom claims
            addBasicCustomClaims(context);
        } else {
            // For user-based grants (authorization_code, password, etc.)
            if (principal.getPrincipal() instanceof SecureUser) {
                SecureUser secureUser = (SecureUser) principal.getPrincipal();
                addUserCustomClaims(context, secureUser);
            } else {
                // Fallback for other authentication types
                addBasicCustomClaims(context);
            }
        }
    }
    
    private void addUserCustomClaims(JwtEncodingContext context, SecureUser secureUser) {
        var claims = context.getClaims();
        
        // Add your custom user information to JWT claims
        Map<String, Object> responseInfo = new LinkedHashMap<>();
        responseInfo.put("api_id", "");
        responseInfo.put("ver", "1.0");
        responseInfo.put("ts", System.currentTimeMillis());
        responseInfo.put("res_msg_id", "");
        responseInfo.put("msg_id", "");
        responseInfo.put("status", "Access Token generated successfully");
        
        // Add custom claims to the JWT
        claims.claim("ResponseInfo", responseInfo);
        
        if (optimizeTokenSize) {
            // Create minimal user info instead of full user object to reduce token size
            Map<String, Object> minimalUserInfo = new LinkedHashMap<>();
            minimalUserInfo.put("id", secureUser.getUser().getId());
            minimalUserInfo.put("uuid", secureUser.getUser().getUuid());
            minimalUserInfo.put("userName", secureUser.getUser().getUserName());
            minimalUserInfo.put("name", secureUser.getUser().getName());
            minimalUserInfo.put("mobileNumber", secureUser.getUser().getMobileNumber());
            minimalUserInfo.put("emailId", secureUser.getUser().getEmailId());
            minimalUserInfo.put("locale", secureUser.getUser().getLocale());
            minimalUserInfo.put("type", secureUser.getUser().getType());
            minimalUserInfo.put("tenantId", secureUser.getUser().getTenantId());
            minimalUserInfo.put("active", secureUser.getUser().isActive());
            // CRITICAL: Include roles even in optimized token to fix authorization
            minimalUserInfo.put("roles", secureUser.getUser().getRoles());

            claims.claim("UserRequest", minimalUserInfo);
        } else {
            // Include full user object with roles (legacy behavior)
            claims.claim("UserRequest", secureUser.getUser());
        }
        
        // Add user-specific information
        claims.claim("userId", secureUser.getUser().getId());
        claims.claim("userName", secureUser.getUser().getUserName());
        claims.claim("userType", secureUser.getUser().getType());
        claims.claim("tenantId", secureUser.getUser().getTenantId());
    }
    
    private void addBasicCustomClaims(JwtEncodingContext context) {
        var claims = context.getClaims();
        
        // Add basic custom information for client credentials
        Map<String, Object> responseInfo = new LinkedHashMap<>();
        responseInfo.put("api_id", "");
        responseInfo.put("ver", "1.0");
        responseInfo.put("ts", System.currentTimeMillis());
        responseInfo.put("res_msg_id", "");
        responseInfo.put("msg_id", "");
        responseInfo.put("status", "Client Access Token generated successfully");
        
        claims.claim("ResponseInfo", responseInfo);
        claims.claim("grant_type", "client_credentials");
    }
}