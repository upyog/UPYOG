package org.egov.user.security.oauth2.custom;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CustomOpaqueRefreshTokenGenerator implements OAuth2TokenGenerator<OAuth2RefreshToken> {

    private RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public OAuth2RefreshToken generate(OAuth2TokenContext context) {
        if (!context.getTokenType().getValue().equals("refresh_token")) {
            return null;
        }

        // Generate UUID-based refresh token
        String tokenValue = UUID.randomUUID().toString();
        
        // Store refresh token metadata in Redis
        storeRefreshTokenMetadata(tokenValue, context);
        
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(Duration.ofMinutes(1209600)); // Use configured expiry
        
        return new OAuth2RefreshToken(tokenValue, issuedAt, expiresAt);
    }

    private void storeRefreshTokenMetadata(String tokenValue, OAuth2TokenContext context) {
        Map<String, Object> tokenMetadata = new HashMap<>();
        tokenMetadata.put("tokenType", "refresh_token");
        tokenMetadata.put("issuedAt", Instant.now().toEpochMilli());
        
        // Store minimal data for refresh tokens
        if (context.getPrincipal() != null) {
            tokenMetadata.put("subject", context.getPrincipal().getName());
            tokenMetadata.put("clientId", context.getRegisteredClient().getClientId());
        }
        
        // Store in Redis with expiration
        String key = "refresh_token:" + tokenValue;
        redisTemplate.opsForValue().set(key, tokenMetadata, 1209600, TimeUnit.MINUTES);
    }

    public Map<String, Object> getRefreshTokenMetadata(String tokenValue) {
        String key = "refresh_token:" + tokenValue;
        return (Map<String, Object>) redisTemplate.opsForValue().get(key);
    }

    public void revokeRefreshToken(String tokenValue) {
        String key = "refresh_token:" + tokenValue;
        redisTemplate.delete(key);
    }
}