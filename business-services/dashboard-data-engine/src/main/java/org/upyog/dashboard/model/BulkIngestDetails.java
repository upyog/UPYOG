package org.upyog.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object encapsulating the metadata required to initialize a bulk data ingestion process.
 * <p>
 * Transferred within {@link BulkIngestRequest} to inform the downstream national dashboard
 * ingestion engine of the newly uploaded spreadsheet or delimited dataset location in AWS S3 and its associated tenant jurisdiction.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkIngestDetails {

    /**
     * Complete Amazon S3 object key / storage path (e.g. {@code "UPYOG/pg/PGR/pg_PGR_1710500000000.xlsx"}).
     */
    @JsonProperty("fileName")
    private String fileName;

    /**
     * Target state-level tenant identifier code (e.g. {@code "pg"}, {@code "pb"}).
     */
    @JsonProperty("stateCode")
    private String stateCode;

    /**
     * Optional delimiter character used in flat dataset files (e.g. {@code "|"}, {@code ","}).
     * Omitted from serialized JSON when {@code null}.
     */
    @JsonProperty("delimiter")
    private String delimiter;
}
