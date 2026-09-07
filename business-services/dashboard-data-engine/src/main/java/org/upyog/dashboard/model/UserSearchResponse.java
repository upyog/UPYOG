package org.upyog.dashboard.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Deserializes the response body returned by the eGov user search endpoint
 * ({@code /user/_search}).
 *
 * <p>The user search endpoint returns a JSON object with a {@code "user"} array
 * containing the matching {@link UserInfo} records.  Unknown fields are silently
 * ignored via {@code @JsonIgnoreProperties(ignoreUnknown = true)} to protect
 * against response-format changes.
 *
 * <h3>Usage in the adapter-service</h3>
 * {@link org.upyog.dashboard.service.OAuthTokenService} calls the user search
 * endpoint when the OAuth token response does not include the user profile
 * (which is typical for employee-type logins).  It then extracts the first
 * element from {@link #user} to cache as the {@code UserInfo} for outbound
 * {@link RequestInfo} blocks.
 *
 * <h3>Wire format (abbreviated)</h3>
 * <pre>{@code
 * {
 *   "user": [
 *     {
 *       "id": "123",
 *       "uuid": "...",
 *       "userName": "NDS1",
 *       ...
 *     }
 *   ]
 * }
 * }</pre>
 *
 * @see UserInfo
 * @see org.upyog.dashboard.service.OAuthTokenService
 */
/**
 * Class representing the UserSearchResponse class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserSearchResponse {

    /**
     * List of users returned by the search.
     *
     * <p>For a username-based search the dataList typically contains exactly one
     * element.  {@link org.upyog.dashboard.service.OAuthTokenService} treats the
     * first element as the system user profile and logs a warning if the dataList
     * is empty or {@code null}.
     *
     * <p>May be {@code null} if the server returns a response body without the
     * {@code "user"} key (e.g. on a 404 or empty-result response).
     */
    private List<UserInfo> user;
}
