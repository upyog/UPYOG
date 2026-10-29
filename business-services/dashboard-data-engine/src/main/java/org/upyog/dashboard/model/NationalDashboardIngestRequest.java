package org.upyog.dashboard.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Request body POSTed to the National Dashboard ingest endpoint
 * ({@code /national-dashboard/metric/_ingest}).
 *
 * <p>This is the outermost envelope that the downstream National Dashboard API
 * expects.  It follows the standard request contract with a top-level
 * {@code RequestInfo} block for authentication and an upper-case {@code Data}
 * array carrying the module metric records.
 *
 * <h3>Field naming</h3>
 * Both fields use {@code @JsonProperty} to produce the upper-case JSON keys
 * ({@code "RequestInfo"}, {@code "Data"}) required by the API, while keeping
 * the Java field names following standard camelCase conventions (lowercase first letter)
 * to avoid Jackson duplicate key serialization bugs.
 *
 * @see RequestInfo
 * @see DashboardData
 * @see org.upyog.dashboard.loader.impl.HttpLoader
 */
/**
 * Class representing the NationalDashboardIngestRequest class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class NationalDashboardIngestRequest {

    /**
     * Authentication and tracing context for the request.
     *
     * <p>Carries the OAuth2 bearer token, the system user's {@link UserInfo},
     * and metadata such as the API ID and message ID.  Built by
     * {@link org.upyog.dashboard.loader.impl.HttpLoader#buildRequest} using
     * credentials obtained from {@link org.upyog.dashboard.service.OAuthTokenService}.
     *
     * <p>Serialized to JSON as the upper-case key {@code "RequestInfo"}.
     */
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    /**
     * List of module metric records to ingest.
     *
     * <p>Each element is a {@link DashboardData} snapshot for one ULB on one date.
     * Directly sourced from {@link DashboardPayload#getData()} after transformation
     * and validation.
     *
     * <p>Serialized to JSON as the upper-case key {@code "Data"}.
     */
    @JsonProperty("Data")
    private List<DashboardData> data;
}
