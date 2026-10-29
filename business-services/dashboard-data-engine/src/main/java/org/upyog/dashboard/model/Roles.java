package org.upyog.dashboard.model;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Represents a single role assigned to a {@link UserInfo}.
 *
 * <p>Roles control what actions a user is authorized to perform within a
 * given tenant.  Each {@link UserInfo} object can carry a dataList of
 * {@code Roles}, scoped to a specific tenant identified by {@link #tenantId}.
 *
 * <p>In the adapter-service, {@code Roles} objects are deserialized as part of
 * the {@link UserInfo} returned by:
 * <ul>
 *   <li>The OAuth token response ({@link OAuthTokenResponse#getUserRequest()}).</li>
 *   <li>The user search endpoint response ({@link UserSearchResponse#getUser()}).</li>
 * </ul>
 * They are then embedded verbatim inside the {@link RequestInfo#getUserInfo()}
 * field of every outbound ingest request so the national dashboard can perform
 * role-based authorization on the server side.
 *
 * @see UserInfo
 * @see RequestInfo
 */
/**
 * Class representing the Roles class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Roles {

    /**
     * Human-readable display name of the role (e.g. {@code "System User"},
     * {@code "National Dashboard Admin"}).
     *
     * <p>Used for display purposes; not used for authorization checks in the
     * adapter-service itself.
     */
    private String name;

    /**
     * Machine-readable role code that the authorization framework uses
     * for access control decisions (e.g. {@code "NATIONAL_DASHBOARD_ADMIN"},
     * {@code "SYSTEM_VIEWER"}).
     *
     * <p>This is the value checked by the national dashboard ingest endpoint
     * to determine whether the requesting user is allowed to push data.
     */
    private String code;

    /**
     * Tenant identifier to which this role assignment is scoped
     * (e.g. {@code "pg"} for the Punjab state tenant).
     *
     * <p>A user can have different roles in different tenants.  The adapter-service
     * system user is configured with a role in the tenant specified by the
     * {@code adapter.system.user.tenantId} application property.
     */
    private String tenantId;
}
