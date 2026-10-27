package org.upyog.dx.upyogdata.util;

import java.util.UUID;
import org.egov.common.contract.request.RequestInfo;
import org.upyog.dx.upyogdata.pfms.models.ResponseInfo;
import org.upyog.dx.upyogdata.constants.Constants;
import java.time.Instant;

/**
 * Utility class providing common helper methods used by the
 * UPYOG Data DX service.
 */

public class DxUtils {

    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
         * Builds the API response following the Upyog standard response structure.
         * The final PFMSTransactionResponse bundles the responseInfo, id and the per-transaction results list into a single
         * structured response object
    */
    public static ResponseInfo buildResponseInfo(RequestInfo requestInfo) {
        return ResponseInfo.builder()
                .apiId(requestInfo != null ? requestInfo.getApiId() : null)
                .ver(requestInfo != null ? requestInfo.getVer() : null)
                .ts(Instant.now().toEpochMilli())
                .msgId(requestInfo != null ? requestInfo.getMsgId() : null)
                .resMsgId(Constants.SUCCESS_MESSAGE)
                .status(Constants.SUCCESS)
                .build();
    }

    /**
     * Function to convert financial year to PFMS format
     * Example:
     * 2026-27 → suffix = "27" (2 chars) → "20" + "27" = "2027"
     * 2026-2027 → suffix = "2027" (4 chars) → directly "2027"
     */
    public static String extractPfmsFinancialYear(String financialYear) {
        if (financialYear == null) return null;
        int idx = financialYear.indexOf('-');
        if (idx == -1) return financialYear;
        String suffix = financialYear.substring(idx + 1); // "27" or "2027"
        return suffix.length() == 2 ? financialYear.substring(0, 2) + suffix : suffix;
    }
}
