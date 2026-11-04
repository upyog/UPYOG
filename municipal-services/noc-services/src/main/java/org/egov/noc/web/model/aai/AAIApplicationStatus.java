package org.egov.noc.web.model.aai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for NOC application status from AAI NOCAS
 * Maps to the Data array items in the AAI API response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AAIApplicationStatus {

    /**
     * AAI NOCAS ID (e.g., "GUWA/NORTH_EAST/B/17122025/634977")
     */
    private String nocasId;

    /**
     * Unique ID - BPA Application Number (e.g., "PG-BP-2025-11-21-000602")
     * This maps to sourceRefId in NOC table
     */
    private String uniqueId;

    /**
     * Authority Name (e.g., "ASSAMULB")
     */
    private String authorityName;

    /**
     * Status from AAI (e.g., "INPROCESS", "ISSUED", "REJECTED")
     */
    private String status;

    /**
     * PTE field from AAI response
     */
    private String pte;

    /**
     * Issue Date from AAI
     */
    private String issueDate;

    /**
     * Airport Name
     */
    private String airportName;

    /**
     * Remarks from AAI
     */
    private String remark;

    /**
     * File Name
     */
    private String fileName;

    /**
     * Action Type
     */
    private String actionType;

    /**
     * Query Type
     */
    private String queryType;

    /**
     * Search Type
     */
    private String searchType;

    /**
     * Error Code
     */
    private String errorCode;

    /**
     * Message
     */
    private String message;

    private Boolean statusFlag;
}

