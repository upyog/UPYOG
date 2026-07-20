package org.upyog.adapter.service;

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
 * egov.user.oauth.host               — base URL of the OAuth endpoint (e.g. https://host)
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
    private RestTemplate restTemplate;

    /** Base URL of the eGov OAuth token endpoint (e.g. {@code https://host}). */
    @Value("${egov.user.host}")
    private String oauthHost;

    /** Path and query string appended to {@link #oauthHost} to form the token URL. */
    @Value("${egov.user.oauth.path}")
    private String oauthPath;

    /**
     * Pre-formatted {@code Basic <base64>} header value used to authenticate the
     * OAuth client application (not the system user — that is sent in the form body).
     */
    @Value("${egov.user.oauth.basic.auth}")
    private String basicAuthHeader;

    /** Username of the system user used to obtain tokens. */
    @Value("${adapter.system.user.username}")
    private String username;

    /** Password of the system user. */
    @Value("${adapter.system.user.password}")
    private String password;

    /** Tenant ID the system user belongs to. */
    @Value("${adapter.system.user.tenantId}")
    private String tenantId;

    /** User type (e.g. {@code "EMPLOYEE"}). */
    @Value("${adapter.system.user.type}")
    private String userType;
    
    /** Path appended to {@link #userHost} to form the user-search URL. */
    @Value("${egov.user.search.path:/user/_search}")
    private String userSearchPath;

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
        this.expiresAt = Instant.now()
                .plusSeconds(Math.max(response.getExpiresIn() - EXPIRY_SAFETY_BUFFER_SECONDS, 0));
        log.info("OAuth token refreshed. Expires at {}", this.expiresAt);
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
        log.info("Fetching userInfo for system user [{}] via user search endpoint", username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "RequestInfo", Map.of(
                        "apiId", "Rainmaker",
                        "authToken", cachedToken.getAccessToken()),
                "tenantId", tenantId,
                "userType", userType,
                "userName", username);
        
        log.info("request to get the userInfo : {}", requestBody);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        String url = oauthHost + userSearchPath;

        try {
            ResponseEntity<UserSearchResponse> response = restTemplate.postForEntity(url, requestEntity, UserSearchResponse.class);
            UserSearchResponse body = response.getBody();

            if (body != null && body.getUser() != null && !body.getUser().isEmpty()) {
                this.cachedUserInfo = body.getUser().get(0);
                log.info("Fetched userInfo for [{}]: uuid={}", username, cachedUserInfo.getUuid());
            } else {
                log.error("User search at {} returned no user for username [{}] tenant [{}]. "
                        + "Confirm the correct endpoint/payload shape with the team.", url, username, tenantId);
            }
        } catch (Exception e) {
            log.error("User search request to {} failed: {}. "
                    + "userInfo will remain null — requests requiring it will likely fail server-side.",
                    url, e.getMessage());
        }
    }
}
