package org.egov.user.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.stereotype.Component;

// REMOVE THESE DEPRECATED IMPORTS:
// import org.springframework.security.oauth2.common.util.OAuth2Utils;
// import org.springframework.security.oauth2.provider.OAuth2Authentication;
// import org.springframework.security.oauth2.provider.OAuth2Request;
// import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;

@Component
public class CustomAuthenticationKeyGenerator {
    
    private static final String CLIENT_ID = "client_id";
    private static final String SCOPE = "scope";
    private static final String USERNAME = "username";

    @Value("${key.generator.hash.algorithm}")
    private String hashAlgorithm;

    // NEW METHOD: For modern OAuth2Authorization
    public String extractKey(OAuth2Authorization authorization) {
        Map<String, String> values = new LinkedHashMap<>();
        
        // Extract client ID
        values.put(CLIENT_ID, authorization.getRegisteredClientId());
        
        // Extract username from principal
        if (authorization.getPrincipalName() != null) {
            values.put(USERNAME, authorization.getPrincipalName());
        }
        
        // Extract scopes
        if (authorization.getAuthorizedScopes() != null && !authorization.getAuthorizedScopes().isEmpty()) {
            values.put(SCOPE, String.join(" ", authorization.getAuthorizedScopes()));
        }
        
        // Extract tenantId from attributes if available
        Map<String, Object> attributes = authorization.getAttributes();
        if (attributes != null && attributes.containsKey("tenantId")) {
            Object tenantId = attributes.get("tenantId");
            if (tenantId != null && !tenantId.toString().isEmpty()) {
                values.put("tenantId", tenantId.toString());
            }
        }

        return generateHash(values);
    }

    // LEGACY METHOD: For backward compatibility (if still needed elsewhere)
    // You can remove this if not used by other parts of your application
    public String extractKeyFromAuthentication(Authentication authentication, String clientId, java.util.Set<String> scopes) {
        Map<String, String> values = new LinkedHashMap<>();
        
        if (authentication != null && authentication.getName() != null) {
            values.put(USERNAME, authentication.getName());
        }
        
        if (clientId != null) {
            values.put(CLIENT_ID, clientId);
        }
        
        if (scopes != null && !scopes.isEmpty()) {
            values.put(SCOPE, String.join(" ", scopes));
        }

        return generateHash(values);
    }

    // UTILITY METHOD: Generate hash from values map
    private String generateHash(Map<String, String> values) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(hashAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(hashAlgorithm + " algorithm not available. Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(values.toString().getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available. Fatal (should be in the JDK).");
        }
    }
}