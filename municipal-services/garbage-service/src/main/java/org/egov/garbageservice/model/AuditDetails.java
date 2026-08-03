package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.annotations.CustomSafeHtml;

/**
 * Common audit block for who created or last modified a garbage domain record and when.
 * Stores user ids and epoch timestamps used across accounts, bills, and nested Grbg* entities.
 */
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditDetails {

    @CustomSafeHtml
    private String createdBy;

    private Long createdDate;

    @CustomSafeHtml
    private String lastModifiedBy;

    private Long lastModifiedDate;

    private Long createdTime;

    private Long lastModifiedTime;
}
