package org.egov.user.security.oauth2.custom;

import org.egov.user.domain.model.SecureUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomTokenEnhancer implements OAuth2TokenCustomizer<JwtEncodingContext> {

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
        claims.claim("UserRequest", secureUser.getUser());
        
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