package org.egov.enc.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class to hold audit information for batch operations.
 * Used for batch insertion of audit records in encryption service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditData {
    private String operation;
    private String tenantId;
    private String oldRowJson;
    private String newRowJson;
} 