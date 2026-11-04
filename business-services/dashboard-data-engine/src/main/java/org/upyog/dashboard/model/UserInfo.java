package org.upyog.dashboard.model;
import org.upyog.dashboard.common.constants.DashboardConstants;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Represents the authenticated user's profile as returned by the eGov user
 * service and OAuth token endpoint.
 *
 * <p>In the adapter-service, {@code UserInfo} is used in two contexts:
 * <ol>
 *   <li><strong>Authentication</strong> — obtained from
 *       {@link org.upyog.dashboard.service.OAuthTokenService#getUserInfo()} and
 *       embedded in every outbound {@link RequestInfo} so the National Dashboard
 *       endpoint can identify the caller and perform RBAC checks.</li>
 *   <li><strong>Deserialization</strong> — deserialized from the OAuth token
 *       response ({@link OAuthTokenResponse#getUserRequest()}) and from the
 *       user search endpoint response ({@link UserSearchResponse#getUser()}).</li>
 * </ol>
 *
 * <p>All fields use standard Java types and Lombok {@code } provides
 * getters, setters, {@code equals}, {@code hashCode}, and {@code toString}.
 *
 * @see OAuthTokenResponse
 * @see UserSearchResponse
 * @see RequestInfo
 * @see Roles
 */
/**
 * Class representing the UserInfo class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserInfo {

    /**
     * Numeric primary-key ID of the user in the eGov user service database.
     *
     * <p>This is the legacy integer ID.  Prefer {@link #uuid} for unique
     * identification in distributed systems.
     */
    private String id;

    /**
     * UUID of the user — the preferred unique identifier across all services.
     *
     * <p>Used when logging user identity and when storing
     * {@link org.upyog.dashboard.entity.DailyIngestionData#getUserId()} if the user
     * context is available.
     */
    private String uuid;

    /**
     * Login username of the system user (e.g. {@code "NDS1"}).
     *
     * <p>Configured via the {@code adapter.system.user.username} application
     * property and used to authenticate with the OAuth endpoint.
     */
    private String userName;

    /**
     * Full display name of the user (e.g. {@code "National Dashboard System User"}).
     */
    private String name;

    /**
     * Mobile phone number registered for the user account.
     *
     * <p>May be {@code null} for system/machine accounts.
     */
    private String mobileNumber;

    /**
     * Email address registered for the user account.
     *
     * <p>May be {@code null} for system/machine accounts.
     */
    private String emailId;

    /**
     * BCP-47 locale string (e.g. {@code "en_IN"}) indicating the user's
     * preferred language and regional settings.
     */
    private String locale;

    /**
     * User type as defined by the eGov user service
     * (e.g. {@code "EMPLOYEE"}, {@code "CITIZEN"}, {@code DashboardConstants.SYSTEM_USER}).
     *
     * <p>The adapter system user is configured as {@code "EMPLOYEE"} via the
     * {@code adapter.system.user.type} application property.
     */
    private String type;

    /**
     * Whether the user account is currently active.
     *
     * <p>Inactive accounts cannot authenticate and should not be used for
     * outbound API calls.
     */
    private Boolean active;

    /**
     * Primary tenant identifier for the user
     * (e.g. {@code "pg"} for the Punjab state tenant).
     *
     * <p>Configured via the {@code adapter.system.user.tenantId} application
     * property.
     */
    private String tenantId;

    /**
     * List of roles assigned to this user, each scoped to a tenant.
     *
     * <p>The national dashboard endpoint checks these roles to decide whether
     * the user is authorized to push data.  The adapter system user must have
     * a role that grants ingest access (e.g. {@code NATIONAL_DASHBOARD_ADMIN}).
     *
     * @see Roles
     */
    private List<Roles> roles;
}
