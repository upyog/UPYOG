package org.upyog.adapter.model;

import lombok.Builder;
import lombok.Data;

/**
 * Standard DIGIT {@code RequestInfo} block included in every outbound API request.
 *
 * <p>The National Dashboard ingest endpoint (and all other DIGIT/UPYOG APIs)
 * require this envelope in the request body.  It carries authentication context
 * (bearer token and user profile) as well as API metadata used for request
 * tracing and auditing.
 *
 * <h3>Wire format</h3>
 * <pre>{@code
 * "RequestInfo": {
 *   "apiId":     "Rainmaker",
 *   "ver":       null,
 *   "ts":        null,
 *   "action":    null,
 *   "did":       null,
 *   "key":       null,
 *   "msgId":     "1720000000000|en_IN",
 *   "authToken": "eyJhbGc...",
 *   "userInfo":  { ...UserInfo fields... }
 * }
 * }</pre>
 *
 * <p>Fields that are not required by the adapter-service (e.g. {@code ver},
 * {@code ts}, {@code action}, {@code did}, {@code key}) are left {@code null}
 * and omitted from the wire if Jackson's
 * {@code @JsonInclude(NON_NULL)} is applied at the object-mapper level.
 *
 * @see NationalDashboardIngestRequest
 * @see UserInfo
 * @see org.upyog.adapter.loader.impl.HttpLoader
 */
@Data
@Builder
public class RequestInfo {

    /**
     * Identifier of the calling application.
     *
     * <p>Always set to {@code "Rainmaker"} for requests originating from the
     * adapter-service, matching the convention used across DIGIT services.
     */
    private String apiId;

    /**
     * API version string.
     *
     * <p>Optional; {@code null} for adapter-service requests as the version is
     * not required by the national dashboard ingest endpoint.
     */
    private String ver;

    /**
     * Request timestamp.
     *
     * <p>Optional; {@code null} for adapter-service requests.  Some older DIGIT
     * APIs expect an epoch-millis string here; the national dashboard endpoint
     * does not.
     */
    private String ts;

    /**
     * Action being performed (e.g. {@code "_create"}, {@code "_search"}).
     *
     * <p>Optional; {@code null} for adapter-service ingest requests.
     */
    private String action;

    /**
     * Device ID of the originating client.
     *
     * <p>Optional; {@code null} for server-to-server adapter calls.
     */
    private String did;

    /**
     * Signing key or API key.
     *
     * <p>Optional; {@code null} for adapter-service requests.
     */
    private String key;

    /**
     * Unique message ID for request correlation and distributed tracing.
     *
     * <p>Set to {@code "<epochMillis>|en_IN"} by
     * {@link org.upyog.adapter.loader.impl.HttpLoader#buildRequest} so each
     * request gets a distinct, time-ordered identifier that can be used to
     * correlate adapter logs with national dashboard access logs.
     */
    private String msgId;

    /**
     * OAuth2 bearer token for the authenticated system user.
     *
     * <p>Obtained from {@link org.upyog.adapter.service.OAuthTokenService#getToken()}
     * and refreshed automatically before expiry.  The national dashboard endpoint
     * uses this token to authorize the ingest operation.
     */
    private String authToken;

    /**
     * Profile of the authenticated system user.
     *
     * <p>Obtained from {@link org.upyog.adapter.service.OAuthTokenService#getUserInfo()}.
     * Contains the user UUID, roles, tenant, and contact details.  Required by
     * some DIGIT API implementations for audit trail creation.
     */
    private UserInfo userInfo;
}
