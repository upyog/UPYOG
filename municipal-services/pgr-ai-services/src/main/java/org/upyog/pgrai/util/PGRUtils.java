package org.upyog.pgrai.util;

import org.egov.common.utils.MultiStateInstanceUtil;
import org.upyog.pgrai.web.models.AuditDetails;
import org.upyog.pgrai.web.models.Service;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utility class for common operations in the PGR (Public Grievance Redressal) system.
 * Provides methods for generating audit details and replacing schema placeholders in queries.
 */
@Component
public class PGRUtils {

    /** Utility for handling multi-state instance operations. */
    private MultiStateInstanceUtil multiStateInstanceUtil;

    /**
     * Constructor for PGRUtils.
     *
     * @param multiStateInstanceUtil Utility for multi-state instance operations.
     */
    @Autowired
    public PGRUtils(MultiStateInstanceUtil multiStateInstanceUtil) {
        this.multiStateInstanceUtil = multiStateInstanceUtil;
    }

    /**
     * Generates audit details for create or update operations.
     *
     * @param by      The user performing the operation.
     * @param service The service object containing existing audit details (for update operations).
     * @param isCreate Flag indicating whether the operation is a create operation.
     * @return The generated AuditDetails object.
     */
    public AuditDetails getAuditDetails(String by, Service service, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if (isCreate) {
            return AuditDetails.builder()
                    .createdBy(by)
                    .lastModifiedBy(by)
                    .createdTime(time)
                    .lastModifiedTime(time)
                    .build();
        } else {
            return AuditDetails.builder()
                    .createdBy(service.getAuditDetails().getCreatedBy())
                    .lastModifiedBy(by)
                    .createdTime(service.getAuditDetails().getCreatedTime())
                    .lastModifiedTime(time)
                    .build();
        }
    }

    /**
     * Replaces the schema placeholder in a query with the appropriate schema name based on the tenant ID.
     *
     * @param query    The query containing the schema placeholder.
     * @param tenantId The tenant ID used to determine the schema name.
     * @return The query with the schema placeholder replaced.
     * @throws CustomException If the tenant ID is invalid.
     */
    public String replaceSchemaPlaceholder(String query, String tenantId) {
        String finalQuery = null;
        try {
            finalQuery = multiStateInstanceUtil.replaceSchemaPlaceholder(query, tenantId);
        } catch (Exception e) {
            throw new CustomException("INVALID_TENANTID", "Invalid tenantId: " + tenantId);
        }
        return finalQuery;
    }
}