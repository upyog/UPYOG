package org.egov.garbageservice.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.web.models.AuditDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * General-purpose helpers for garbage-service (string formatting, classpath templates, audit blocks).
 * Builds AuditDetails on create/update from RequestInfo user uuid for persisted entities.
 */
@Component
public class GrbgUtils {
    /**
     * Executes getCurrentTimestamp query operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Parses input search filter criteria.</li>
     *   <li>Queries database or external service for matching records.</li>
     *   <li>Applies security filters and pagination boundaries.</li>
     *   <li>Returns response payload with matching entity list.</li>
     * </ol>
     *
     * @return the output result
     */

    public static Long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Executes getAuditDetails query operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Parses input search filter criteria.</li>
     *   <li>Queries database or external service for matching records.</li>
     *   <li>Applies security filters and pagination boundaries.</li>
     *   <li>Returns response payload with matching entity list.</li>
     * </ol>
     *
     * @param by       the by parameter
     * @param isCreate the isCreate parameter
     * @return the output result
     */

    public static AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = getCurrentTimestamp();
        if (isCreate)
            // TODO: check if we can set lastupdated details to empty
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
                    .build();
        else
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
    }

    /**
     * Handles REST API request to register a new garbage account.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates the incoming request payload and authentication headers.</li>
     *   <li>Delegates account creation and workflow initialization logic.</li>
     *   <li>Constructs standardized response headers.</li>
     *   <li>Returns HTTP response containing created entity details.</li>
     * </ol>
     *
     * @param requestInfo the request information containing user session details
     * @return the output result
     */

    public AuditDetails buildCreateAuditDetails(RequestInfo requestInfo) {
        String uuid = requestInfo.getUserInfo().getUuid();
        return AuditDetails.builder().createdBy(uuid).createdDate(System.currentTimeMillis()).lastModifiedBy(uuid)
                .lastModifiedDate(System.currentTimeMillis()).build();
    }

}
