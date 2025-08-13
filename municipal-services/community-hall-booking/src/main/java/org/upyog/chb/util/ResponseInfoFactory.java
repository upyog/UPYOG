package org.upyog.chb.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

/**
 * This utility class is responsible for creating standardized ResponseInfo objects
 * for the Community Hall Booking module.
 * 
 * Purpose:
 * - To generate ResponseInfo objects based on the provided RequestInfo and operation status.
 * - To ensure consistency in API responses across the module.
 * 
 * Dependencies:
 * - RequestInfo: Contains metadata about the incoming API request.
 * - ResponseInfo: Represents metadata about the API response.
 * 
 * Features:
 * - Extracts relevant fields from the RequestInfo object to populate the ResponseInfo.
 * - Sets the response status based on the success or failure of the operation.
 * - Provides a reusable method for creating ResponseInfo objects for all API responses.
 * 
 * Methods:
 * 1. createResponseInfoFromRequestInfo:
 *    - Accepts a RequestInfo object and a success flag.
 *    - Generates a ResponseInfo object with fields such as apiId, version, timestamp, and status.
 * 
 * Usage:
 * - This class is used throughout the module to create ResponseInfo objects for API responses.
 * - It ensures consistent and reusable logic for response metadata generation.
 */
@Component
public class ResponseInfoFactory {

    public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final Boolean success) {

        final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        final String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = null;
        if(requestInfo!=null)
            ts = requestInfo.getTs();
        final String resMsgId = "uief87324"; // FIXME : Hard-coded
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        final String responseStatus = success ? "successful" : "failed";

        return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(resMsgId).msgId(msgId).resMsgId(resMsgId)
                .status(responseStatus).build();
    }

}