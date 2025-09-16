package org.egov.user.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.user.domain.exception.InvalidAccessTokenException;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.domain.model.UserDetail;
import org.egov.user.persistence.repository.ActionRestRepository;
import org.egov.user.web.contract.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
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
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${roles.state.level.enabled}")
    private boolean isRoleStateLevel;

    // UPDATED CONSTRUCTOR
    private TokenService(OAuth2AuthorizationService authorizationService, 
                        ActionRestRepository actionRestRepository) {
        this.authorizationService = authorizationService;
        this.actionRestRepository = actionRestRepository;
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
        return getUserFromOpaqueToken(accessToken);

        // COMMENTED OUT: Original role-based logic (can be re-enabled if needed)
        /*
        String tenantId = null;
        if (isRoleStateLevel && (secureUser.getTenantId() != null && secureUser.getTenantId().contains(".")))
            tenantId = secureUser.getTenantId().split("\\.")[0];
        else
            tenantId = secureUser.getTenantId();

        List<Action> actions = actionRestRepository.getActionByRoleCodes(secureUser.getRoleCodes(), tenantId);
        log.info("returning STATE-LEVEL roleactions for tenant: " + tenantId);
        return new UserDetail(secureUser, actions);
        */
    }
    
    /**
     * Get UserDetails from opaque token stored in Redis
     * 
     * @param accessToken
     * @return UserDetail
     */
    @SuppressWarnings("unchecked")
    private UserDetail getUserFromOpaqueToken(String accessToken) {
        String tokenKey = "oauth2:token:" + accessToken;
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
            .build();
        
        // Create SecureUser from the User object
        SecureUser secureUser = new SecureUser(user);
        
        log.info("Successfully retrieved user from opaque token: userId={}, userName={}", user.getId(), user.getUserName());
        return new UserDetail(secureUser, null);
    }
}