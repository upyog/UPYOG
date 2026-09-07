package org.upyog.dashboard.model;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Summary response object returned after executing a bulk historical / legacy ingestion run.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LegacyIngestionResponse {

    /** Total number of calendar dates in the requested date range. */
    private int totalDatesRequested;

    /** Number of dates skipped because they were already successfully ingested. */
    private int datesSkipped;

    /** Number of dates successfully processed during this run. */
    private int datesProcessedSuccessfully;

    /** Number of dates that failed during this run. */
    private int datesFailed;

    /** List of skipped date strings (in YYYY-MM-DD format). */
    private List<String> skippedDates;

    /** Details of all ingestion attempts executed during this run. */
    private List<IngestionResult> processedResults;
}
