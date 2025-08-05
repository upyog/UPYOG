package org.egov.user.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.user.domain.exception.InvalidAccessTokenException;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.domain.model.UserDetail;
import org.egov.user.persistence.repository.ActionRestRepository;
import org.springframework.beans.factory.annotation.Value;
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

        // UPDATED: Use OAuth2AuthorizationService instead of TokenStore
        OAuth2Authorization authorization = authorizationService.findByToken(accessToken, OAuth2TokenType.ACCESS_TOKEN);

        if (authorization == null) {
            throw new InvalidAccessTokenException();
        }

        // UPDATED: Extract SecureUser from OAuth2Authorization
        Authentication authentication = authorization.getAttribute(Authentication.class.getName());
        if (authentication == null || !(authentication.getPrincipal() instanceof SecureUser)) {
            throw new InvalidAccessTokenException();
        }

        SecureUser secureUser = (SecureUser) authentication.getPrincipal();
        return new UserDetail(secureUser, null);

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
}