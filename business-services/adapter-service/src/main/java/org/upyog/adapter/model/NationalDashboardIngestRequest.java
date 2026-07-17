package org.upyog.adapter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

/**
 * Request body POSTed to the National Dashboard ingest endpoint
 * ({@code /national-dashboard/metric/_ingest}).
 *
 * <p>This is the outermost envelope that the downstream National Dashboard API
 * expects.  It follows the standard DIGIT request contract with a top-level
 * {@code RequestInfo} block for authentication and an upper-case {@code Data}
 * array carrying the module metric records.
 *
 * <h3>Wire format (abbreviated)</h3>
 * <pre>{@code
 * {
 *   "RequestInfo": {
 *     "apiId": "Rainmaker",
 *     "authToken": "<bearer-token>",
 *     "userInfo": { ... },
 *     "msgId": "1720000000000|en_IN"
 *   },
 *   "Data": [
 *     {
 *       "date": "2024-01-15",
 *       "module": "PT",
 *       "ulb": "pb.amritsar",
 *       "metrics": { ... }
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h3>Field naming</h3>
 * Both fields use {@code @JsonProperty} to produce the upper-case JSON keys
 * ({@code "RequestInfo"}, {@code "Data"}) required by the API, while keeping
 * the Java field names following standard camelCase conventions.
 *
 * @see RequestInfo
 * @see DashboardData
 * @see org.upyog.adapter.loader.impl.HttpLoader
 */
@Data
@Builder
public class NationalDashboardIngestRequest {

    /**
     * Authentication and tracing context for the request.
     *
     * <p>Carries the OAuth2 bearer token, the system user's {@link UserInfo},
     * and metadata such as the API ID and message ID.  Built by
     * {@link org.upyog.adapter.loader.impl.HttpLoader#buildRequest} using
     * credentials obtained from {@link org.upyog.adapter.service.OAuthTokenService}.
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

