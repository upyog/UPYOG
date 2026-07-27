package org.upyog.adapter.service;

import org.upyog.adapter.client.UserFeignClient;

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
import org.upyog.adapter.model.OAuthTokenResponse;
import org.upyog.adapter.model.UserInfo;
import org.upyog.adapter.model.UserSearchResponse;
import org.upyog.adapter.util.RetryUtil;
import org.upyog.adapter.config.AdapterProperties;

/**
 * Thread-safe service that manages the OAuth2 access token and user profile for
 * the adapter-service system user.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Authenticates against the eGov OAuth2 token endpoint using the
 *       password-grant flow with the credentials configured in
 *       {@code application.properties}.</li>
 *   <li>Caches the returned access token and its expiry time so that multiple
 *       concurrent requests share one token without making redundant HTTP calls.</li>
 *   <li>Proactively refreshes the token before it expires (using a 60-second
 *       safety buffer).</li>
 *   <li>Fetches and caches the user profile ({@link UserInfo}) separately via
 *       the user search endpoint when the OAuth response does not include it
 *       (which is typical for employee-type logins).</li>
 * </ul>
 *
 * <h3>Thread safety</h3>
 * A {@link ReentrantLock} is used to prevent multiple threads from racing to
 * refresh the token simultaneously.  The pattern is double-checked locking:
 * a fast volatile read first, then a lock-guarded re-check before executing
 * the refresh.
 *
 * <h3>Configuration properties</h3>
 * <pre>
 * egov.user.oauth.host               — base URL of the OAuth endpoint (exception.g. https://host)
 * egov.user.oauth.path               — path + query string for the token endpoint
 * egov.user.oauth.basic.auth         — Base64-encoded "Basic ..." header value
 * adapter.system.user.username       — login username of the system user
 * adapter.system.user.password       — password of the system user
 * adapter.system.user.tenantId       — tenant ID the system user belongs to
 * adapter.system.user.type           — user type (typically EMPLOYEE)
 * egov.user.host                     — base URL for the user search endpoint
 * egov.user.search.path              — path for the user search endpoint
 * </pre>
 *
 * @see OAuthTokenResponse
 * @see UserInfo
 * @see UserSearchResponse
 */
/**
 * Class representing the OAuthTokenService class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Service
public class OAuthTokenService {

    private static final Logger log = LoggerFactory.getLogger(OAuthTokenService.class);

    /**
     * Number of seconds to subtract from the token's {@code expires_in} when
     * calculating the cached expiry time.  This ensures the token is refreshed
     * before it actually expires, preventing race conditions with in-flight
     * requests.
     */
    private static final long EXPIRY_SAFETY_BUFFER_SECONDS = 60L;

    /** HTTP client used for both the token and user-search requests. */
    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private AdapterProperties adapterProperties;

    /** Lock used to serialize token refresh and user-info fetch operations. */
    private final ReentrantLock lock = new ReentrantLock();

    /** Cached OAuth token response; {@code volatile} for safe cross-thread visibility. */
    private volatile OAuthTokenResponse cachedToken;

    /** Expiry instant for the cached token; {@code volatile} for safe cross-thread visibility. */
    private volatile Instant expiresAt;

    /** Cached user profile; {@code volatile} for safe cross-thread visibility. */
    private volatile UserInfo cachedUserInfo;

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Returns a valid OAuth2 access token for the configured system user.
     *
     * <p>The token is returned from the in-memory cache when it is still valid.
     * If the cache is empty or the token has expired (minus the safety buffer),
     * a fresh token is fetched from the OAuth endpoint before returning.
     *
     * @return the access token string; never {@code null}
     * @throws IllegalStateException if the OAuth endpoint is unreachable or
     *                               returns a response without an access token
     */
    public String getToken() {
        return getValidToken().getAccessToken();
    }

    /**
     * Returns the {@link UserInfo} profile for the configured system user,
     * fetching it on demand if it has not been loaded yet.
     *
     * <p>This method first ensures the cached token is valid (calling
     * {@link #getValidToken()} internally).  If the user profile has not been
     * loaded — either because it was not included in the token response or
     * because the token was just refreshed — a separate call to the user search
     * endpoint is made under a lock to avoid duplicate fetches.
     *
     * <p>If the user search fails, {@code null} is returned and an error is
     * logged.  Callers should handle a {@code null} return gracefully.
     *
     * @return the {@link UserInfo} of the system user, or {@code null} if the
     *         user search endpoint returned no results or threw an exception
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

    // =========================================================================
    // Private helpers
    // =========================================================================

    /**
     * Returns the cached {@link OAuthTokenResponse} if valid, or refreshes it
     * first using double-checked locking.
     *
     * <p>Pattern:
     * <ol>
     *   <li>Fast path — volatile read of {@link #cachedToken} and
     *       {@link #expiresAt}; return immediately if still valid.</li>
     *   <li>Slow path — acquire {@link #lock}, re-check validity (another thread
     *       may have already refreshed), and call {@link #fetchNewToken()} if
     *       still invalid.  The cached user info is also cleared on refresh so
     *       it is re-fetched with the new token on the next {@link #getUserInfo()}
     *       call.</li>
     * </ol>
     *
     * @return the current valid {@link OAuthTokenResponse}; never {@code null}
     * @throws IllegalStateException if token fetch fails
     */
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

    /**
     * Returns {@code true} if the cached token is non-null and its pre-calculated
     * expiry instant is in the future.
     *
     * @return {@code true} if the cached token can be used without refreshing
     */
    private boolean isTokenValid() {
        return cachedToken != null
                && expiresAt != null
                && Instant.now().isBefore(expiresAt);
    }

    /**
     * Performs a synchronous password-grant OAuth2 token request and updates the
     * {@link #cachedToken} and {@link #expiresAt} fields.
     *
     * <p>Steps:
     * <ol>
     *   <li>Builds form-encoded request body with {@code username}, {@code password},
     *       {@code tenantId}, {@code userType}, {@code scope=read}, and
     *       {@code grant_type=password}.</li>
     *   <li>Sets the {@code Authorization: Basic <...>} header from
     *       {@link #basicAuthHeader}.</li>
     *   <li>POSTs to {@code oauthHost + oauthPath}.</li>
     *   <li>Validates the response is non-null and contains an access token.</li>
     *   <li>Calculates {@link #expiresAt} as
     *       {@code now + max(expires_in - SAFETY_BUFFER, 0)} seconds.</li>
     * </ol>
     *
     * <p>Must be called while holding {@link #lock}.
     *
     * @throws IllegalStateException if the HTTP call fails or the response does
     *                               not contain an {@code access_token}
     */
    private void fetchNewToken() {
        log.info("Fetching new OAuth token for system user [{}] tenant [{}]", adapterProperties.getUsername(), adapterProperties.getTenantId());

        java.util.Map<String, String> body = java.util.Map.of(
                "username", adapterProperties.getUsername(),
                "password", adapterProperties.getPassword(),
                "tenantId", adapterProperties.getTenantId(),
                "userType", adapterProperties.getUserType(),
                "scope", "read",
                "grant_type", "password"
        );
        String url = adapterProperties.getOauthHost() + adapterProperties.getOauthPath();

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                OAuthTokenResponse response = userFeignClient.fetchToken(
                        java.net.URI.create(url),
                        adapterProperties.getBasicAuthHeader(),
                        body
                );
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
                this.expiresAt = Instant.now()
                        .plusSeconds(Math.max(response.getExpiresIn() - EXPIRY_SAFETY_BUFFER_SECONDS, 0));
                log.info("OAuth token refreshed. Expires at {}", this.expiresAt);
                return;
            } catch (Exception exception) {
                if (exception instanceof feign.FeignException feignEx && feignEx.status() >= 400 && feignEx.status() < 500) {
                    log.error("OAuth token request to {} failed with client error status {}: {}", url, feignEx.status(), exception.getMessage());
                    throw new IllegalStateException("Failed to obtain OAuth token due to client error", exception);
                }

                if (attempt >= adapterProperties.getOauthMaxAttempts()) {
                    log.error("OAuth token request to {} failed after {} attempts.", url, attempt, exception);
                    if (exception instanceof IllegalStateException) {
                        throw (IllegalStateException) exception;
                    }
                    throw new IllegalStateException("Failed to reach OAuth endpoint " + url + " after maximum attempts", exception);
                }

                long backoff = RetryUtil.calculateBackoffWithJitter(attempt, adapterProperties.getOauthBaseDelayMs(), adapterProperties.getOauthMaxDelayMs());
                log.warn("OAuth token request failed (attempt {}/{}). Retrying in {} ms. Error: {}",
                        attempt, adapterProperties.getOauthMaxAttempts(), backoff, exception.getMessage());
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("OAuth token refresh retry interrupted", ie);
                }
            }
        }
    }

    /**
     * Fetches the system user's profile from the eGov user search endpoint and
     * stores it in {@link #cachedUserInfo}.
     *
     * <p>Called when {@link #cachedUserInfo} is {@code null} after a successful
     * token fetch.  Builds a search request using the cached token for
     * authentication and the configured {@code username}, {@code tenantId}, and
     * {@code userType} as search criteria.
     *
     * <p>If the search succeeds and returns at least one user, the first result
     * is stored in {@link #cachedUserInfo}.  If the search returns no results or
     * the HTTP call fails, an error is logged and {@link #cachedUserInfo} remains
     * {@code null}.  Downstream callers of {@link #getUserInfo()} must handle
     * {@code null} gracefully.
     *
     * <p>Must be called while holding {@link #lock}.
     */
    private void fetchUserInfo() {
        log.info("Fetching userInfo for system user [{}] via user search endpoint", adapterProperties.getUsername());

        Map<String, Object> requestBody = Map.of(
                "RequestInfo", Map.of(
                        "apiId", "Rainmaker",
                        "authToken", cachedToken.getAccessToken()),
                "tenantId", adapterProperties.getTenantId(),
                "userType", adapterProperties.getUserType(),
                "userName", adapterProperties.getUsername());
        
        log.info("request to get the userInfo : {}", requestBody);

        String url = adapterProperties.getOauthHost() + adapterProperties.getUserSearchPath();

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                UserSearchResponse body = userFeignClient.searchUser(
                        java.net.URI.create(url),
                        requestBody
                );

                if (body != null && body.getUser() != null && !body.getUser().isEmpty()) {
                    this.cachedUserInfo = body.getUser().get(0);
                    log.info("Fetched userInfo for [{}]: uuid={}", adapterProperties.getUsername(), cachedUserInfo.getUuid());
                    return;
                } else {
                    log.error("User search at {} returned no user for username [{}] tenant [{}]. "
                            + "Confirm the correct endpoint/payload shape with the team.", url, adapterProperties.getUsername(), adapterProperties.getTenantId());
                    return;
                }
            } catch (Exception exception) {
                if (exception instanceof feign.FeignException feignEx && feignEx.status() >= 400 && feignEx.status() < 500) {
                    log.error("User search request to {} failed with client error status {}: {}", url, feignEx.status(), exception.getMessage());
                    break;
                }

                if (attempt >= adapterProperties.getOauthMaxAttempts()) {
                    log.error("User search request to {} failed after {} attempts: {}", url, attempt, exception.getMessage());
                    break;
                }

                long backoff = RetryUtil.calculateBackoffWithJitter(attempt, adapterProperties.getOauthBaseDelayMs(), adapterProperties.getOauthMaxDelayMs());
                log.warn("User search request failed (attempt {}/{}). Retrying in {} ms. Error: {}",
                        attempt, adapterProperties.getOauthMaxAttempts(), backoff, exception.getMessage());
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("User search retry interrupted", ie);
                    break;
                }
            }
        }
    }

}
