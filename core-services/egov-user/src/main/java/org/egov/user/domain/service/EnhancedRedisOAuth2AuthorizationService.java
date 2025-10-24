package org.egov.user.domain.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;


public class EnhancedRedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String AUTHORIZATION_KEY_PREFIX = "oauth2:authorization:";
    private static final String TOKEN_KEY_PREFIX = "access_token:";
    private static final String USER_TOKEN_KEY_PREFIX = "access_token:user:tokens:"; // NEW: For user token tracking

    public EnhancedRedisOAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        String authorizationKey = AUTHORIZATION_KEY_PREFIX + authorization.getId();
        redisTemplate.opsForValue().set(authorizationKey, authorization, Duration.ofMinutes(60));
        
        // Index by access token
        if (authorization.getAccessToken() != null) {
            String accessTokenKey = TOKEN_KEY_PREFIX + authorization.getAccessToken().getToken().getTokenValue();
            redisTemplate.opsForValue().set(accessTokenKey, authorization.getId(), Duration.ofMinutes(60));
            
            // NEW: Track tokens by user for easy removal
            String userTokenKey = USER_TOKEN_KEY_PREFIX + authorization.getPrincipalName();
            redisTemplate.opsForSet().add(userTokenKey, authorization.getId());
            redisTemplate.expire(userTokenKey, Duration.ofHours(24));
        }
        
        // Index by refresh token
        if (authorization.getRefreshToken() != null) {
            String refreshTokenKey = TOKEN_KEY_PREFIX + authorization.getRefreshToken().getToken().getTokenValue();
            redisTemplate.opsForValue().set(refreshTokenKey, authorization.getId(), Duration.ofHours(24));
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        String authorizationKey = AUTHORIZATION_KEY_PREFIX + authorization.getId();
        redisTemplate.delete(authorizationKey);
        
        // Remove token indexes
        if (authorization.getAccessToken() != null) {
            String accessTokenKey = TOKEN_KEY_PREFIX + authorization.getAccessToken().getToken().getTokenValue();
            redisTemplate.delete(accessTokenKey);
        }
        
        if (authorization.getRefreshToken() != null) {
            String refreshTokenKey = TOKEN_KEY_PREFIX + authorization.getRefreshToken().getToken().getTokenValue();
            redisTemplate.delete(refreshTokenKey);
        }
        
        // Remove from user token tracking
        String userTokenKey = USER_TOKEN_KEY_PREFIX + authorization.getPrincipalName();
        redisTemplate.opsForSet().remove(userTokenKey, authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        String key = AUTHORIZATION_KEY_PREFIX + id;
        return (OAuth2Authorization) redisTemplate.opsForValue().get(key);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        String authorizationId = (String) redisTemplate.opsForValue().get(tokenKey);
        
        if (authorizationId != null) {
            return findById(authorizationId);
        }
        
        return null;
    }

    // NEW: Custom method to find authorizations by username
    public List<OAuth2Authorization> findByPrincipalName(String principalName) {
        String userTokenKey = USER_TOKEN_KEY_PREFIX + principalName;
        Set<Object> authorizationIds = redisTemplate.opsForSet().members(userTokenKey);
        
        List<OAuth2Authorization> authorizations = new ArrayList<>();
        if (authorizationIds != null) {
            for (Object authorizationId : authorizationIds) {
                OAuth2Authorization authorization = findById((String) authorizationId);
                if (authorization != null) {
                    authorizations.add(authorization);
                }
            }
        }
        
        return authorizations;
    }

    // NEW: Custom method to remove all tokens for a user
    public void removeTokensByPrincipalName(String principalName) {
        List<OAuth2Authorization> userAuthorizations = findByPrincipalName(principalName);
        for (OAuth2Authorization authorization : userAuthorizations) {
            remove(authorization);
        }
    }
}
