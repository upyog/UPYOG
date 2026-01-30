package org.egov.user.security.oauth2.custom;

import lombok.extern.slf4j.Slf4j;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.domain.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
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
    private UserService userService;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
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

            // CRITICAL FIX: Decrypt user data BEFORE storing in Redis
            // This ensures CITIZEN users get decrypted mobile numbers in token metadata
            org.egov.user.web.contract.auth.User user = secureUser.getUser();
            org.egov.user.web.contract.auth.User userToStore = user;

            if (userService != null) {
                try {
                    log.info("REDIS STORE: Attempting to decrypt user data before storing. User ID: {}, Type: {}",
                        user.getId(), user.getType());
                    org.egov.user.domain.model.User domainUser = convertContractToDomainUser(user);
                    org.egov.user.domain.model.User decryptedDomainUser = userService.decryptUserWithContext(domainUser, user);
                    userToStore = convertToContractUser(decryptedDomainUser);

                    // Check if mobile number was successfully decrypted
                    boolean mobileDecrypted = userToStore.getMobileNumber() != null &&
                        !userToStore.getMobileNumber().contains("|");
                    log.info("REDIS STORE: User data decryption completed. Mobile decrypted: {}", mobileDecrypted);
                } catch (Exception e) {
                    log.warn("REDIS STORE: Failed to decrypt user data, storing encrypted data. Error: {} - {}",
                        e.getClass().getSimpleName(), e.getMessage());
                    userToStore = user;
                }
            } else {
                log.warn("REDIS STORE: UserService not available, storing user data as-is");
            }

            // CRITICAL: Log roles before storing in Redis
            log.info("REDIS STORE: User {} has {} roles", userToStore.getUserName(),
                     userToStore.getRoles() != null ? userToStore.getRoles().size() : "null");
            if (userToStore.getRoles() != null) {
                userToStore.getRoles().forEach(role ->
                    log.info("REDIS STORE: Role - code: {}, name: {}, tenantId: {}",
                             role.getCode(), role.getName(), role.getTenantId())
                );
            }

            // Store minimal user data (now decrypted if decryption succeeded)
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userToStore.getId());
            userInfo.put("uuid", userToStore.getUuid());
            userInfo.put("userName", userToStore.getUserName());
            userInfo.put("name", userToStore.getName());
            userInfo.put("mobileNumber", userToStore.getMobileNumber());
            userInfo.put("emailId", userToStore.getEmailId());
            userInfo.put("locale", userToStore.getLocale());
            userInfo.put("type", userToStore.getType());
            userInfo.put("tenantId", userToStore.getTenantId());
            userInfo.put("active", userToStore.isActive());

            // CRITICAL FIX: Convert roles to serializable format for Redis
            // Redis cannot properly serialize/deserialize Set<Role> objects
            // So we convert to List<Map<String, String>> which preserves all role data
            if (userToStore.getRoles() != null && !userToStore.getRoles().isEmpty()) {
                List<Map<String, String>> rolesList = userToStore.getRoles().stream()
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
            tokenMetadata.put("userId", userToStore.getId());
            tokenMetadata.put("userName", userToStore.getUserName());
            tokenMetadata.put("userType", userToStore.getType());
            tokenMetadata.put("tenantId", userToStore.getTenantId());
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

    /**
     * Convert contract User to domain User for decryption
     */
    private org.egov.user.domain.model.User convertContractToDomainUser(org.egov.user.web.contract.auth.User contractUser) {
        if (contractUser == null) {
            return null;
        }

        return org.egov.user.domain.model.User.builder()
            .id(contractUser.getId())
            .uuid(contractUser.getUuid())
            .username(contractUser.getUserName())
            .name(contractUser.getName())
            .mobileNumber(contractUser.getMobileNumber())
            .emailId(contractUser.getEmailId())
            .locale(contractUser.getLocale())
            .active(contractUser.isActive())
            .type(contractUser.getType() != null ?
                org.egov.user.domain.model.enums.UserType.fromValue(contractUser.getType()) : null)
            .tenantId(contractUser.getTenantId())
            .roles(contractUser.getRoles() != null ?
                contractUser.getRoles().stream()
                    .map(role -> org.egov.user.domain.model.Role.builder()
                        .name(role.getName())
                        .code(role.getCode())
                        .tenantId(role.getTenantId())
                        .build())
                    .collect(Collectors.toSet()) : new HashSet<>())
            .build();
    }

    /**
     * Convert domain User to contract User
     */
    private org.egov.user.web.contract.auth.User convertToContractUser(org.egov.user.domain.model.User domainUser) {
        if (domainUser == null) {
            return null;
        }

        return org.egov.user.web.contract.auth.User.builder()
            .id(domainUser.getId())
            .uuid(domainUser.getUuid())
            .userName(domainUser.getUsername())
            .name(domainUser.getName())
            .mobileNumber(domainUser.getMobileNumber())
            .emailId(domainUser.getEmailId())
            .locale(domainUser.getLocale())
            .active(domainUser.getActive())
            .type(domainUser.getType() != null ? domainUser.getType().name() : null)
            .tenantId(domainUser.getTenantId())
            .roles(domainUser.getRoles() != null ?
                domainUser.getRoles().stream()
                    .map(role -> new org.egov.user.web.contract.auth.Role(role))
                    .collect(Collectors.toSet()) : new HashSet<>())
            .build();
    }
}