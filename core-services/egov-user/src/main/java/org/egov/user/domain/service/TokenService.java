package org.egov.user.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.user.domain.exception.InvalidAccessTokenException;
import org.egov.user.domain.model.Action;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.domain.model.UserDetail;
import org.egov.user.domain.service.UserService;
import org.egov.user.persistence.repository.ActionRestRepository;
import org.egov.user.web.contract.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Service;

// REMOVED DEPRECATED IMPORTS:
// import org.springframework.security.oauth2.provider.OAuth2Authentication;
// import org.springframework.security.oauth2.provider.token.TokenStore;

@Service
@Slf4j
public class TokenService {

    // CHANGED: TokenStore -> OAuth2AuthorizationService
    private OAuth2AuthorizationService authorizationService;
    private ActionRestRepository actionRestRepository;
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${roles.state.level.enabled}")
    private boolean isRoleStateLevel;

    // UPDATED CONSTRUCTOR
    private TokenService(OAuth2AuthorizationService authorizationService,
                        ActionRestRepository actionRestRepository,
                        UserService userService) {
        this.authorizationService = authorizationService;
        this.actionRestRepository = actionRestRepository;
        this.userService = userService;
    }

    /**
     * Get UserDetails By AccessToken
     *
     * @param accessToken
     * @return
     */
    public UserDetail getUser(String accessToken) {
        if (StringUtils.isEmpty(accessToken)) {
            throw new InvalidAccessTokenException();
        }

        // Try OAuth2AuthorizationService first (for JWT tokens)
        OAuth2Authorization authorization = authorizationService.findByToken(accessToken, OAuth2TokenType.ACCESS_TOKEN);

        if (authorization != null) {
            // JWT token path - extract SecureUser from OAuth2Authorization
            Authentication authentication = authorization.getAttribute(Authentication.class.getName());
            if (authentication != null && authentication.getPrincipal() instanceof SecureUser) {
                SecureUser secureUser = (SecureUser) authentication.getPrincipal();
                return new UserDetail(secureUser, null);
            }
        }

        // Opaque token path - get token metadata directly from Redis
        UserDetail userDetail = getUserFromOpaqueToken(accessToken);

        // Add action resolution to prevent null pointer errors in other services
        if (userDetail != null && userDetail.getSecureUser() != null) {
            SecureUser secureUser = userDetail.getSecureUser();

            String tenantId = null;
            if (isRoleStateLevel && (secureUser.getTenantId() != null && secureUser.getTenantId().contains(".")))
                tenantId = secureUser.getTenantId().split("\\.")[0];
            else
                tenantId = secureUser.getTenantId();

            // Create RequestInfo with authToken for access-control authentication
            // This ensures external access-control services can authenticate the request
            org.egov.common.contract.request.RequestInfo requestInfo =
                org.egov.common.contract.request.RequestInfo.builder()
                    .apiId("egov-user")
                    .ver("1.0")
                    .ts(System.currentTimeMillis())
                    .msgId("egov-user-" + System.currentTimeMillis())
                    .authToken(accessToken)  // Include token for access-control authentication
                    .build();

            List<Action> actions = actionRestRepository.getActionByRoleCodes(secureUser.getRoleCodes(), tenantId, requestInfo);
            log.info("returning STATE-LEVEL roleactions for tenant: " + tenantId);
            return new UserDetail(secureUser, actions);
        }

        return userDetail;
    }
    
    /**
     * Get UserDetails from opaque token stored in Redis
     * 
     * @param accessToken
     * @return UserDetail
     */
    @SuppressWarnings("unchecked")
    private UserDetail getUserFromOpaqueToken(String accessToken) {
        String tokenKey = "access_token:" + accessToken;
        Map<String, Object> tokenMetadata = (Map<String, Object>) redisTemplate.opsForValue().get(tokenKey);
        
        if (tokenMetadata == null) {
            log.error("Token metadata not found in Redis for token: {}", accessToken.substring(0, Math.min(8, accessToken.length())) + "...");
            throw new InvalidAccessTokenException();
        }
        
        // Extract user information from token metadata
        Map<String, Object> userRequest = (Map<String, Object>) tokenMetadata.get("UserRequest");
        if (userRequest == null) {
            log.error("UserRequest not found in token metadata for token: {}", accessToken.substring(0, Math.min(8, accessToken.length())) + "...");
            throw new InvalidAccessTokenException();
        }
        
        // Convert to User contract object
        Object idObj = userRequest.get("id");
        Long userId = idObj instanceof Integer ? ((Integer) idObj).longValue() : (Long) idObj;
        
        User user = User.builder()
            .id(userId)
            .uuid((String) userRequest.get("uuid"))
            .userName((String) userRequest.get("userName"))
            .name((String) userRequest.get("name"))
            .mobileNumber((String) userRequest.get("mobileNumber"))
            .emailId((String) userRequest.get("emailId"))
            .locale((String) userRequest.get("locale"))
            .type((String) userRequest.get("type"))
            .tenantId((String) userRequest.get("tenantId"))
            .active((Boolean) userRequest.get("active"))
            .roles(extractRolesFromUserRequest(userRequest)) // Extract roles from token metadata
            .build();
        
        // Create SecureUser from the User object
        SecureUser secureUser = new SecureUser(user);
        log.info("BEFORE DECRYPTION: User {} has {} roles", user.getId(),
            user.getRoles() != null ? user.getRoles().size() : "NULL");

        // Decrypt user data using the same logic as /user/oauth/token and /user/_search
        try {
            log.info("Starting user decryption for opaque token. User: {}, encrypted userName: {}",
                user.getId(), user.getUserName());

            // Convert contract user to domain user for decryption
            org.egov.user.domain.model.User domainUser = convertContractToDomainUser(user);
            log.info("Converted to domain user, calling decryptUserWithContext");
            log.info("DomainUser has {} roles",
                domainUser.getRoles() != null ? domainUser.getRoles().size() : "NULL");

            // Decrypt user with proper authenticated context
            org.egov.user.domain.model.User decryptedDomainUser = userService.decryptUserWithContext(domainUser, user);
            log.info("Decryption completed, converting back to contract user");
            log.info("DecryptedDomainUser has {} roles",
                decryptedDomainUser.getRoles() != null ? decryptedDomainUser.getRoles().size() : "NULL");

            // Convert back to contract user and create new SecureUser
            User decryptedUser = convertToContractUser(decryptedDomainUser);
            log.info("DecryptedUser (contract) has {} roles",
                decryptedUser.getRoles() != null ? decryptedUser.getRoles().size() : "NULL");

            secureUser = new SecureUser(decryptedUser);
            log.info("Opaque token using decrypted user data. Decrypted userName: {}, roles: {}",
                decryptedUser.getUserName(), decryptedUser.getRoles() != null ? decryptedUser.getRoles().size() : "NULL");
        } catch (Exception e) {
            log.error("Failed to decrypt user for opaque token: {}", e.getMessage(), e);
            log.info("Falling back to encrypted user data for opaque token");
            // Continue with encrypted user data - secureUser already created above
        }

        log.info("FINAL: Successfully retrieved user from opaque token: userId={}, userName={}, roles={}",
            user.getId(), user.getUserName(),
            secureUser.getUser().getRoles() != null ? secureUser.getUser().getRoles().size() : "NULL");
        return new UserDetail(secureUser, null);
    }

    /**
     * Convert contract User to domain User for decryption
     */
    private org.egov.user.domain.model.User convertContractToDomainUser(User contractUser) {
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
                    .collect(Collectors.toSet()) : new HashSet<>()) // CRITICAL FIX: Never return null roles
            .build();
    }

    /**
     * Convert domain User to contract User
     */
    private User convertToContractUser(org.egov.user.domain.model.User domainUser) {
        if (domainUser == null) {
            return null;
        }

        return User.builder()
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
                    .collect(Collectors.toSet()) : new HashSet<>()) // Ensure empty set instead of null
            .build();
    }

    /**
     * Extract roles from UserRequest metadata for opaque tokens
     * Handles both Set and List formats from Redis deserialization
     */
    @SuppressWarnings("unchecked")
    private HashSet<org.egov.user.web.contract.auth.Role> extractRolesFromUserRequest(Map<String, Object> userRequest) {
        Object rolesObj = userRequest.get("roles");

        if (rolesObj == null) {
            log.warn("ROLE EXTRACTION: roles field is null in token metadata");
            return new HashSet<>();
        }

        log.debug("ROLE EXTRACTION: rolesObj type = {}", rolesObj.getClass().getName());

        try {
            // Handle Collection (Set or List) of role objects
            if (rolesObj instanceof java.util.Collection) {
                java.util.Collection<?> rolesCollection = (java.util.Collection<?>) rolesObj;

                log.info("ROLE EXTRACTION: Found {} roles in token metadata", rolesCollection.size());

                HashSet<org.egov.user.web.contract.auth.Role> roles = rolesCollection.stream()
                    .map(roleItem -> {
                        try {
                            if (roleItem instanceof Map) {
                                // Handle Map representation (from Redis JSON deserialization)
                                Map<String, Object> roleMap = (Map<String, Object>) roleItem;
                                return new org.egov.user.web.contract.auth.Role(
                                    (String) roleMap.get("code"),
                                    (String) roleMap.get("name"),
                                    (String) roleMap.get("tenantId")
                                );
                            } else if (roleItem instanceof org.egov.user.web.contract.auth.Role) {
                                // Handle direct Role object (if Redis preserves object type)
                                return (org.egov.user.web.contract.auth.Role) roleItem;
                            } else {
                                log.warn("ROLE EXTRACTION: Unexpected role item type: {}", roleItem.getClass().getName());
                                return null;
                            }
                        } catch (Exception e) {
                            log.error("ROLE EXTRACTION: Failed to convert role item: {}", e.getMessage());
                            return null;
                        }
                    })
                    .filter(role -> role != null)
                    .collect(Collectors.toCollection(HashSet::new));

                log.info("ROLE EXTRACTION: Successfully extracted {} roles", roles.size());
                return roles;
            } else {
                log.warn("ROLE EXTRACTION: rolesObj is not a Collection, type = {}", rolesObj.getClass().getName());
            }
        } catch (Exception e) {
            log.error("ROLE EXTRACTION: Failed to extract roles from token metadata", e);
        }

        log.warn("ROLE EXTRACTION: Returning empty roles set");
        return new HashSet<>();
    }
}