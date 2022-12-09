package org.bel.birthdeath.crdeath.enrichment;

import org.bel.birthdeath.crdeath.web.models.AuditDetails;

interface BaseEnrichment {

    default AuditDetails buildAuditDetails(String by, Boolean create) {
        AuditDetails auditDetails;

        Long currentTime = Long.valueOf(System.currentTimeMillis());
        if (create) {
            auditDetails = AuditDetails.builder()
                                       .createdBy(by)
                                       .createdTime(currentTime)
                                       .lastModifiedBy(by)
                                       .lastModifiedTime(currentTime)
                                       .build();
        } else {
            auditDetails = AuditDetails.builder()
                                       .lastModifiedBy(by)
                                       .lastModifiedTime(currentTime)
                                       .build();
        }
        return auditDetails;
    }
    
}
