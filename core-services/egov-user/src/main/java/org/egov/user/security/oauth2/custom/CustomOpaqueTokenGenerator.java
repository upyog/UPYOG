package org.egov.user.security.oauth2.custom;

import lombok.extern.slf4j.Slf4j;
import org.egov.user.domain.model.SecureUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
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

            // CRITICAL: Log roles before storing in Redis
            log.info("REDIS STORE: User {} has {} roles", secureUser.getUser().getUserName(),
                     secureUser.getUser().getRoles() != null ? secureUser.getUser().getRoles().size() : "null");
            if (secureUser.getUser().getRoles() != null) {
                secureUser.getUser().getRoles().forEach(role ->
                    log.info("REDIS STORE: Role - code: {}, name: {}, tenantId: {}",
                             role.getCode(), role.getName(), role.getTenantId())
                );
            }

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

            // CRITICAL FIX: Convert roles to serializable format for Redis
            // Redis cannot properly serialize/deserialize Set<Role> objects
            // So we convert to List<Map<String, String>> which preserves all role data
            if (secureUser.getUser().getRoles() != null && !secureUser.getUser().getRoles().isEmpty()) {
                List<Map<String, String>> rolesList = secureUser.getUser().getRoles().stream()
                    .map(role -> {
                        Map<String, String> roleMap = new HashMap<>();
                        roleMap.put("code", role.getCode());
                        roleMap.put("name", role.getName());
                        roleMap.put("tenantId", role.getTenantId());
                        return roleMap;
                    })
                    .collect(Collectors.toList());
                userInfo.put("roles", rolesList);
                log.info("REDIS STORE: Converted {} roles to Map format for Redis storage", rolesList.size());
                rolesList.forEach(roleMap ->
                    log.info("  REDIS STORE: Storing role - code: {}, name: {}, tenantId: {}",
                        roleMap.get("code"), roleMap.get("name"), roleMap.get("tenantId"))
                );
            } else {
                userInfo.put("roles", new ArrayList<>());
                log.warn("REDIS STORE: User has null/empty roles, storing empty list");
            }

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

        // Store in Redis with expiration - using access_token: prefix to match RedisOAuth2AuthorizationService
        String key = "access_token:" + tokenValue;
        redisTemplate.opsForValue().set(key, tokenMetadata, 604800, TimeUnit.MINUTES);
        System.out.println("REDIS STORE: Token metadata stored in Redis with key: " + key);
    }

    public Map<String, Object> getTokenMetadata(String tokenValue) {
        String key = "access_token:" + tokenValue;
        return (Map<String, Object>) redisTemplate.opsForValue().get(key);
    }

    public void revokeToken(String tokenValue) {
        String key = "access_token:" + tokenValue;
        redisTemplate.delete(key);
    }
}