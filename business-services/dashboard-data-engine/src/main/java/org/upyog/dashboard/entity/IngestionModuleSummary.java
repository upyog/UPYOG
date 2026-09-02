package org.upyog.dashboard.entity;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Entity object representing a row in the {@code ingestion_module_summary} database table.
 *
 * <p>Tracks the most recent calendar date for which metrics were successfully
 * ingested for a specific tenant and module.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class IngestionModuleSummary {

    /** Primary key UUID. */
    private String id;

    /** tenant identifier (e.g. {@code "pg.citya"} or {@code "pg"}). */
    private String tenantId;

    /** Short code identifying the module (e.g. {@code "PT"}, {@code "TL"}). */
    private String moduleName;

    /** The latest date for which ingestion succeeded. */
    private String lastSuccessfulDate;
    
    private String lastAttemptedDate;

    /** Identity of the user or system that created the row. */
    private String createdBy;

    /** Epoch timestamp (millis) when the row was created. */
    private Long createdTime;

    /** Identity of the user or system that last updated the row. */
    private String lastModifiedBy;

    /** Epoch timestamp (millis) of the last update. */
    private Long lastModifiedTime;
}
