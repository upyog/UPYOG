package org.egov.asset.calculator.utils;

import org.egov.asset.calculator.web.models.AuditDetails;
import org.springframework.stereotype.Component;

@Component
public class CalculationUtils {

    /**
     * Returns audit details for create or update flows.
     *
     * @param by user identifier performing the operation
     * @param isCreate true when creating a new record, false when updating
     * @return AuditDetails populated for the requested operation
     */
    public AuditDetails getAuditDetails(String by, boolean isCreate) {
        Long time = System.currentTimeMillis();
        if (isCreate) {
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time).build();
        }
        return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
    }
}
