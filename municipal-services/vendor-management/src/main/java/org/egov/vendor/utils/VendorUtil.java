package org.egov.vendor.utils;

import lombok.extern.slf4j.Slf4j;
import org.egov.vendor.repository.VendorAdditionalDetailsRepository;
import org.egov.vendor.web.models.AuditDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VendorUtil {

    @Autowired
    private VendorAdditionalDetailsRepository assetRepository;

    /**
     * Method to return auditDetails for create/update flows.
     *
     * @param by       The user performing the operation.
     * @param isCreate Whether the operation is create or update.
     * @return AuditDetails object with relevant fields set.
     */
    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
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
                    .lastModifiedBy(by)
                    .lastModifiedTime(time)
                    .build();
        }
    }

}
