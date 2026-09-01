package org.upyog.dashboard.model;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Represents a single data record sent to the National Dashboard ingest endpoint.
 *
 * <p>Each {@code DashboardData} instance describes a snapshot of module metrics
 * for one ULB (Urban Local Body) on one date.  A single
 * {@link DashboardPayload} typically contains one {@code DashboardData} per
 * ULB-date combination being ingested in the same request.
 *
 * <h3>Structure</h3>
 * The record has two parts:
 * <ul>
 *   <li><strong>Contextual fields</strong> ({@link #date}, {@link #module},
 *       {@link #ward}, {@link #ulb}, {@link #region}, {@link #state}) — identify
 *       <em>where</em> and <em>when</em> the metrics apply.</li>
 *   <li><strong>Metrics dataMap</strong> ({@link #metrics}) — contains the
 *       module-specific KPI values as a flexible key-value store so that new
 *       metrics can be added without changing the model class.</li>
 * </ul>
 *
 * <h3>Serialization</h3>
 * Instances of this class are serialized to JSON by Jackson and embedded inside
 * {@link NationalDashboardIngestRequest#getData()}.  The field names use
 * lower-camelCase (Jackson default), which the National Dashboard endpoint expects.
 *
 * @see DashboardPayload
 * @see NationalDashboardIngestRequest
 */
/**
 * Class representing the DashboardData class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DashboardData {

    /**
     * Calendar date for which these metrics apply, in {@code yyyy-MM-dd} format.
     *
     * <p>For daily ingestion this is the specific day.
     * For legacy ingestion this is typically the first day of the month being backfilled.
     */
    private String date;

    /**
     * Short code identifying the module (e.g. {@code PT}, {@code TL}, {@code FSM}).
     *
     * <p>Must match one of the values defined in
     * {@link org.upyog.dashboard.common.constants.Module} and must be accepted
     * by the national dashboard endpoint for the given tenant.
     */
    private String module;

    /**
     * Ward identifier within the ULB for which the metrics apply.
     *
     * <p>May be {@code null} or an empty string for ULB-level aggregates that
     * do not break down by ward.  When present it should match the ward boundary
     * code in the location hierarchy.
     */
    private String ward;

    /**
     * ULB (Urban Local Body) code, e.g. {@code pb.amritsar} or {@code mh.pune}.
     *
     * <p>Validated as mandatory by {@link org.upyog.dashboard.validator.CommonValidator}.
     * This value is also stored as both {@code tenantId} and {@code ulbName} in
     * the {@link org.upyog.dashboard.entity.DailyIngestionData} audit record.
     */
    private String ulb;

    /**
     * Region / district code that the ULB belongs to within its state.
     *
     * <p>Validated as mandatory by {@link org.upyog.dashboard.validator.CommonValidator}.
     */
    private String region;

    /**
     * State code that the ULB belongs to (e.g. {@code pb} for Punjab,
     * {@code mh} for Maharashtra).
     *
     * <p>Validated as mandatory by {@link org.upyog.dashboard.validator.CommonValidator}.
     */
    private String state;

    /**
     * Module-specific KPI metrics as a flexible key-value dataMap.
     *
     * <p>Each key is a metric name and the value is typically a numeric figure
     * (integer or decimal) though the type is kept as {@link Object} to remain
     * compatible with multi-valued or nested metric structures.
     *
     * <p>Expected keys per module:
     * <ul>
     *   <li><strong>PT</strong> — {@code assessments}, {@code propertyTax},
     *       {@code cess}, {@code rebate}, {@code penalty}, {@code interest},
     *       {@code transactions}, {@code todaysCollection},
     *       {@code todaysTotalApplications}, {@code todaysClosedApplications},
     *       {@code todaysApprovedApplications},
     *       {@code todaysApprovedApplicationsWithinSLA},
     *       {@code avgDaysForApplicationApproval},
     *       {@code noOfPropertiesPaidToday}, {@code propertiesRegistered},
     *       {@code assessedProperties}.</li>
     *   <li><strong>TL</strong> — {@code licensesIssued} (and others TBD).</li>
     *   <li><strong>FSM</strong> — {@code vehicles} (and others TBD).</li>
     * </ul>
     *
     * <p>Validated as non-{@code null} by
     * {@link org.upyog.dashboard.validator.CommonValidator} and further validated
     * for required keys by the module-specific
     * {@link org.upyog.dashboard.validator.ModuleValidator}.
     */
    private Map<String, Object> metrics;
}
