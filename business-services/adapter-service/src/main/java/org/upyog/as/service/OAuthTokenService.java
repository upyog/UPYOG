package org.upyog.as.service;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.upyog.as.model.payload.UserInfo;
import org.upyog.as.model.payload.UserSearchResponse;

@Service
public class OAuthTokenService {

	private static final Logger log = LoggerFactory.getLogger(OAuthTokenService.class);
	private static final long EXPIRY_SAFETY_BUFFER_SECONDS = 60L;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${egov.user.oauth.host}")
	private String oauthHost;

	@Value("${egov.user.oauth.path}")
	private String oauthPath;

	@Value("${egov.user.oauth.basic.auth}")
	private String basicAuthHeader;

	@Value("${adapter.system.user.username}")
	private String username;

	@Value("${adapter.system.user.password}")
	private String password;

	@Value("${adapter.system.user.tenantId}")
	private String tenantId;

	@Value("${adapter.system.user.type}")
	private String userType;

	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.search.path:/user/_search}")
	private String userSearchPath;

	private final ReentrantLock lock = new ReentrantLock();

	private volatile OAuthTokenResponse cachedToken;
	private volatile Instant expiresAt;
	private volatile UserInfo cachedUserInfo;

	/**
	 * Returns a valid access token for the configured system user.
	 *
	 * @return the access token string
	 */
	public String getToken() {
		return getValidToken().getAccessToken();
	}

	/**
	 * Returns user profile information, refreshing it when needed.
	 *
	 * @return the resolved user information payload
	 */
	public UserInfo getUserInfo() {
		getValidToken();
		if (cachedUserInfo == null) {
			lock.lock();
			try {
				if (cachedUserInfo == null) {
					fetchUserInfo();
				}
			} finally {
				lock.unlock();
			}
		}
		return cachedUserInfo;
	}

	private OAuthTokenResponse getValidToken() {
		if (isTokenValid()) {
			return cachedToken;
		}
		lock.lock();
		try {
			if (!isTokenValid()) {
				fetchNewToken();
				cachedUserInfo = null;
			}
			return cachedToken;
		} finally {
			lock.unlock();
		}
	}

	private boolean isTokenValid() {
		return cachedToken != null && expiresAt != null && Instant.now().isBefore(expiresAt);
	}

	private void fetchNewToken() {
		log.info("Fetching new OAuth token for system user [{}] tenant [{}]", username, tenantId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", basicAuthHeader);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("username", username);
		body.add("password", password);
		body.add("tenantId", tenantId);
		body.add("userType", userType);
		body.add("scope", "read");
		body.add("grant_type", "password");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
		String url = oauthHost + oauthPath;

		ResponseEntity<OAuthTokenResponse> responseEntity;
		try {
			responseEntity = restTemplate.exchange(url, HttpMethod.POST, request, OAuthTokenResponse.class);
		} catch (Exception e) {
			log.error("OAuth token request to {} failed: {}", url, e.getMessage());
			throw new IllegalStateException("Failed to reach OAuth endpoint " + url, e);
		}

		OAuthTokenResponse response = responseEntity.getBody();
		if (response == null || response.getAccessToken() == null) {
			log.error("OAuth response missing access_token. Raw response: {}", response);
			throw new IllegalStateException(
					"Failed to obtain OAuth token from " + url + " — no access_token in response");
		}
		if (response.getUserRequest() == null) {
			log.info("OAuth token response did not include userInfo (expected for employee-type logins) — "
					+ "will fetch it separately via the user search endpoint.");
		}

		this.cachedToken = response;
		this.expiresAt = Instant.now().plusSeconds(Math.max(response.getExpiresIn() - EXPIRY_SAFETY_BUFFER_SECONDS, 0));
		log.info("OAuth token refreshed. Expires at {}", this.expiresAt);
	}

	private void fetchUserInfo() {
		log.info("Fetching userInfo for system user [{}] via user search endpoint", username);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> requestBody = Map.of("RequestInfo",
				Map.of("apiId", "Rainmaker", "authToken", cachedToken.getAccessToken()), "tenantId", tenantId,
				"userType", userType, "username", username);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
		String url = userHost + userSearchPath;

		try {
			ResponseEntity<UserSearchResponse> response = restTemplate.postForEntity(url, request,
					UserSearchResponse.class);
			UserSearchResponse body = response.getBody();

			if (body != null && body.getUser() != null && !body.getUser().isEmpty()) {
				this.cachedUserInfo = body.getUser().get(0);
				log.info("Fetched userInfo for [{}]: uuid={}", username, cachedUserInfo.getUuid());
			} else {
				log.error("User search at {} returned no user for username [{}] tenant [{}]. "
						+ "Confirm the correct endpoint/payload shape with the team.", url, username, tenantId);
			}
		} catch (Exception e) {
			log.error(
					"User search request to {} failed: {}. "
							+ "userInfo will remain null — requests requiring it will likely fail server-side.",
					url, e.getMessage());
		}
	}
}