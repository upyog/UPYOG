package org.egov.user.security.oauth2.custom;

import org.egov.user.domain.model.SecureUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CustomOpaqueTokenGenerator implements OAuth2TokenGenerator<OAuth2AccessToken> {

    private RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public OAuth2AccessToken generate(OAuth2TokenContext context) {
        if (!context.getTokenType().getValue().equals("access_token")) {
            return null;
        }

        // Generate UUID-based token
        String tokenValue = UUID.randomUUID().toString();
        
        // Store token metadata in Redis
        storeTokenMetadata(tokenValue, context);
        
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(Duration.ofMinutes(604800)); // Use configured expiry
        
        return new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            tokenValue,
            issuedAt,
            expiresAt
        );
    }

    private void storeTokenMetadata(String tokenValue, OAuth2TokenContext context) {
        Map<String, Object> tokenMetadata = new HashMap<>();
        
        Authentication principal = context.getPrincipal();
        if (principal.getPrincipal() instanceof SecureUser) {
            SecureUser secureUser = (SecureUser) principal.getPrincipal();
            
            // Store minimal user data
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", secureUser.getUser().getId());
            userInfo.put("uuid", secureUser.getUser().getUuid());
            userInfo.put("userName", secureUser.getUser().getUserName());
            userInfo.put("name", secureUser.getUser().getName());
            userInfo.put("mobileNumber", secureUser.getUser().getMobileNumber());
            userInfo.put("emailId", secureUser.getUser().getEmailId());
            userInfo.put("locale", secureUser.getUser().getLocale());
            userInfo.put("type", secureUser.getUser().getType());
            userInfo.put("tenantId", secureUser.getUser().getTenantId());
            userInfo.put("active", secureUser.getUser().isActive());
            
            tokenMetadata.put("UserRequest", userInfo);
            tokenMetadata.put("userId", secureUser.getUser().getId());
            tokenMetadata.put("userName", secureUser.getUser().getUserName());
            tokenMetadata.put("userType", secureUser.getUser().getType());
            tokenMetadata.put("tenantId", secureUser.getUser().getTenantId());
        }
        
        // Add ResponseInfo
        Map<String, Object> responseInfo = new HashMap<>();
        responseInfo.put("api_id", "");
        responseInfo.put("ver", "1.0");
        responseInfo.put("ts", System.currentTimeMillis());
        responseInfo.put("res_msg_id", "");
        responseInfo.put("msg_id", "");
        responseInfo.put("status", "Access Token generated successfully");
        tokenMetadata.put("ResponseInfo", responseInfo);
        
        // Store in Redis with expiration - using oauth2:token: prefix to match RedisOAuth2AuthorizationService
        String key = "oauth2:token:" + tokenValue;
        redisTemplate.opsForValue().set(key, tokenMetadata, 604800, TimeUnit.MINUTES);
    }

    public Map<String, Object> getTokenMetadata(String tokenValue) {
        String key = "oauth2:token:" + tokenValue;
        return (Map<String, Object>) redisTemplate.opsForValue().get(key);
    }

    public void revokeToken(String tokenValue) {
        String key = "oauth2:token:" + tokenValue;
        redisTemplate.delete(key);
    }
}