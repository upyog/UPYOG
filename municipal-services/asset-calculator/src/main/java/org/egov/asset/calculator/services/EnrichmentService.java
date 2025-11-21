package org.egov.asset.calculator.services;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.utils.AssetCalculatorUtil;
import org.egov.asset.calculator.web.models.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EnrichmentService {


    @Autowired
    AssetCalculatorUtil assetCalculatorUtil;
    /**
     * Enriches other Asset operations (e.g., assignment, disposal) by adding audit details and unique identifiers.
     *
     * @param requestInfo The request object containing asset operation details to be enriched.
     */
    public AuditDetails enrichOtherOperations(RequestInfo requestInfo) {
        log.info("Enriching Other Operations Request");
        // Set audit details for the asset assignment
        return assetCalculatorUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
    }

}
