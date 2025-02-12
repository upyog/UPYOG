package org.egov.user.domain.service;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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

		OAuth2AccessToken redisToken = tokenStore.readAccessToken(accessToken);
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) redisToken;
		token.setExpiration(new Date(System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(20)));
		tokenStore.storeAccessToken(redisToken, authentication);

		SecureUser secureUser = ((SecureUser) authentication.getPrincipal());
		return new UserDetail(secureUser, null);
		//		String tenantId = null;
		//		if (isRoleStateLevel && (secureUser.getTenantId() != null && secureUser.getTenantId().contains(".")))
		//			tenantId = secureUser.getTenantId().split("\\.")[0];
		//		else
		//			tenantId = secureUser.getTenantId();
		//
		//		List<Action> actions = actionRestRepository.getActionByRoleCodes(secureUser.getRoleCodes(), tenantId);
		//		log.info("returning STATE-LEVEL roleactions for tenant: "+tenantId);
		//		return new UserDetail(secureUser, actions);
	}
}