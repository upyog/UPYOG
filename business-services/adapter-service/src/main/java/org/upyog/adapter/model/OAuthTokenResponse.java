package org.upyog.adapter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Deserializes the JSON body returned by the eGov OAuth2 token endpoint
 * ({@code /user/oauth/token}).
 *
 * <p>The token endpoint returns a standard OAuth2 password-grant response augmented
 * with a {@code UserRequest} block that contains the authenticated user's profile.
 * Unknown fields are silently ignored via {@code @JsonIgnoreProperties(ignoreUnknown = true)}
 * so that changes to the token response format do not break deserialization.
 *
 * <h3>Wire format (abbreviated)</h3>
 * <pre>{@code
 * {
 *   "access_token": "eyJhbGc...",
 *   "token_type":   "bearer",
 *   "refresh_token": "...",
 *   "expires_in":   3599,
 *   "scope":        "read",
 *   "UserRequest":  { ...UserInfo fields... }
 * }
 * }</pre>
 *
 * <p>Note that the field names use {@code snake_case} ({@code access_token},
 * {@code expires_in}) as returned by the OAuth server.  Lombok's {@code @Data}
 * generates standard camelCase getters, which is why explicit getters
 * ({@link #getAccessToken()}, {@link #getExpiresIn()}) are provided alongside
 * Lombok's generated ones.
 *
 * @see OAuthTokenService
 * @see UserInfo
 */
/**
 * Class representing the OAuthTokenResponse class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthTokenResponse {

    /**
     * The OAuth2 bearer token string.
     *
     * <p>This value is used as the {@code Authorization: Bearer <token>} header
     * in subsequent API calls, and is also embedded in the
     * {@link RequestInfo#getAuthToken()} field of outbound requests.
     *
     * <p>JSON field: {@code access_token}.
     */
    private String access_token;

    /**
     * Token type returned by the OAuth server; typically {@code "bearer"}.
     *
     * <p>JSON field: {@code token_type}.
     */
    private String token_type;

    /**
     * Refresh token that can be used to obtain a new access token without
     * re-supplying credentials.  Not currently used by the adapter-service;
     * the service performs a full password-grant re-authentication when the
     * access token expires.
     *
     * <p>JSON field: {@code refresh_token}.
     */
    private String refresh_token;

    /**
     * Lifetime of the access token in seconds from the time it was issued.
     *
     * <p>Used by {@link org.upyog.adapter.service.OAuthTokenService} to
     * pre-calculate the expiry instant with a safety buffer so the token is
     * refreshed before it actually expires.
     *
     * <p>JSON field: {@code expires_in}.
     */
    private long expires_in;

    /**
     * OAuth2 scope granted for this token (e.g. {@code "read"}).
     *
     * <p>JSON field: {@code scope}.
     */
    private String scope;

    /**
     * User profile embedded in the token response by the eGov OAuth server.
     *
     * <p>Contains the system user's UUID, roles, and tenant information.
     * May be {@code null} for employee-type logins — in that case
     * {@link org.upyog.adapter.service.OAuthTokenService} fetches the user
     * profile separately via the user search endpoint.
     *
     * <p>JSON field: {@code UserRequest} (upper-case U, as returned by the server).
     */
    private UserInfo UserRequest;

    /**
     * Returns the OAuth2 bearer token string.
     *
     * <p>Convenience method that mirrors the snake_case JSON field name
     * ({@code access_token}) with a proper camelCase accessor that can be used
     * alongside Lombok-generated methods.
     *
     * @return the access token string; {@code null} if the response was incomplete
     */
    public String getAccessToken() {
        return access_token;
    }

    /**
     * Returns the token lifetime in seconds.
     *
     * <p>Convenience method that mirrors the snake_case JSON field name
     * ({@code expires_in}) with a proper camelCase accessor.
     *
     * @return the number of seconds until this token expires
     */
    public long getExpiresIn() {
        return expires_in;
    }

    /**
     * Returns the user profile embedded in the token response.
     *
     * <p>Provides access to the {@code UserRequest} field using a camelCase
     * accessor name consistent with Java conventions.
     *
     * @return the {@link UserInfo} from the token response, or {@code null}
     *         if the server did not include it
     */
    public UserInfo getUserRequest() {
        return UserRequest;
    }
}
