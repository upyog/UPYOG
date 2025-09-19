package org.egov.user.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.user.domain.exception.InvalidAccessTokenException;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.domain.model.UserDetail;
import org.egov.user.persistence.repository.ActionRestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TokenService {

    private TokenStore tokenStore;

    private ActionRestRepository actionRestRepository;

    @Value("${roles.state.level.enabled}")
    private boolean isRoleStateLevel;

    @Value("${access.token.validity.in.minutes}")
    private int accessTokenValidityInMinutes;

    
    private TokenService(TokenStore tokenStore, ActionRestRepository actionRestRepository) {
        this.tokenStore = tokenStore;
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

        OAuth2Authentication authentication = tokenStore.readAuthentication(accessToken);

        if (authentication == null) {
            throw new InvalidAccessTokenException();
        }

        // 🔹 Reset expiry if token is valid
        OAuth2AccessToken token = tokenStore.readAccessToken(accessToken);
        if (token != null && !token.isExpired()) {
            DefaultOAuth2AccessToken refreshed = new DefaultOAuth2AccessToken(token);
            refreshed.setExpiration(new java.util.Date(
                System.currentTimeMillis() + (accessTokenValidityInMinutes * 60 * 1000L)
            ));
            tokenStore.storeAccessToken(refreshed, authentication);
        }

        SecureUser secureUser = (SecureUser) authentication.getPrincipal();
        return new UserDetail(secureUser, null);
    }


}