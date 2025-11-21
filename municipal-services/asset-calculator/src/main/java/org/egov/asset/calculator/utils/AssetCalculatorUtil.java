package org.egov.asset.calculator.utils;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.web.models.AuditDetails;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class AssetCalculatorUtil {

    /**
     * Method to return auditDetails for create/update flows
     *
     * @param by
     * @param isCreate
     * @return AuditDetails
     */
    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if (isCreate)
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
                    .build();
        else
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
    }
}
