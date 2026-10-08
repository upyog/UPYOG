package org.egov.nationaldashboardingest.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain model representing the location and tenant metadata of a bulk
 * ingestion target file.
 * <p>
 * Transferred as the payload of {@link BulkIngestInitRequest} and published
 * directly to the Kafka bulk ingestion topic to notify workers where to fetch
 * dataset files.
 * </p>
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BulkIngestInitDetail {

    public BulkIngestInitDetail(String fileName, String stateCode) {
        this.fileName = fileName;
        this.stateCode = stateCode;
    }

    /**
     * Relative or full Amazon S3 object key / storage path (e.g.
     * {@code "UPYOG/pg/PGR/pg_PGR_1710500000000.xlsx"}).
     */
    @JsonProperty("fileName")
    private String fileName;

    /**
     * Target state-level tenant identifier code (e.g.
     * {@code "pg"}, {@code "pb"}).
     */
    @JsonProperty("stateCode")
    private String stateCode;

    /**
     * Optional explicit delimiter override (e.g.
     * {@code "|"}, {@code ","}, {@code "\t"}). Completely optional; defaults to
     * {@code null} with automatic delimiter detection from Line 1.
     */
    @JsonProperty("delimiter")
    private String delimiter;

    /**
     * Optional explicit file format override (e.g.
     * {@code "CSV"}, {@code "PSV"}, {@code "EXCEL"}). Completely optional;
     * defaults to {@code null} with automatic file extension detection.
     */
    @JsonProperty("fileType")
    private String fileType;
}
