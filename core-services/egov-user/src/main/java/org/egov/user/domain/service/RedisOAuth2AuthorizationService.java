package org.egov.user.domain.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.util.Assert;

import java.time.Duration;

public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String AUTHORIZATION_KEY_PREFIX = "oauth2:authorization:";
    private static final String TOKEN_KEY_PREFIX = "access_token:";

    public RedisOAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate) {
        Assert.notNull(redisTemplate, "redisTemplate cannot be null");
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        String authorizationKey = AUTHORIZATION_KEY_PREFIX + authorization.getId();
        redisTemplate.opsForValue().set(authorizationKey, authorization, Duration.ofMinutes(60));
        
        // Index by access token
        if (authorization.getAccessToken() != null) {
            String accessTokenKey = TOKEN_KEY_PREFIX + authorization.getAccessToken().getToken().getTokenValue();
            redisTemplate.opsForValue().set(accessTokenKey, authorization.getId(), Duration.ofMinutes(60));
        }
        
        // Index by refresh token
        if (authorization.getRefreshToken() != null) {
            String refreshTokenKey = TOKEN_KEY_PREFIX + authorization.getRefreshToken().getToken().getTokenValue();
            redisTemplate.opsForValue().set(refreshTokenKey, authorization.getId(), Duration.ofHours(24));
        }
    }

    @Override
    public void remove(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        String authorizationKey = AUTHORIZATION_KEY_PREFIX + authorization.getId();
        redisTemplate.delete(authorizationKey);
        
        if (authorization.getAccessToken() != null) {
            String accessTokenKey = TOKEN_KEY_PREFIX + authorization.getAccessToken().getToken().getTokenValue();
            redisTemplate.delete(accessTokenKey);
        }
        
        if (authorization.getRefreshToken() != null) {
            String refreshTokenKey = TOKEN_KEY_PREFIX + authorization.getRefreshToken().getToken().getTokenValue();
            redisTemplate.delete(refreshTokenKey);
        }
    }

    @Override
    public org.springframework.security.oauth2.server.authorization.OAuth2Authorization findById(String id) {
        String key = AUTHORIZATION_KEY_PREFIX + id;
        return (org.springframework.security.oauth2.server.authorization.OAuth2Authorization) redisTemplate.opsForValue().get(key);
    }

    @Override
    public org.springframework.security.oauth2.server.authorization.OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        Object tokenValue = redisTemplate.opsForValue().get(tokenKey);

        if (tokenValue != null) {
            String authorizationId;
            if (tokenValue instanceof String) {
                authorizationId = (String) tokenValue;
            } else {
                authorizationId = tokenValue.toString();
            }
            return findById(authorizationId);
        }

        return null;
    }
}