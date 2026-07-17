package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
/**
 * Supporting document metadata attached to a garbage application (fileStoreId, doc type).
 * Persisted with the account for verification and audit.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrbgDocument {

    @CustomSafeHtml
    private String uuid;
    @CustomSafeHtml
    private String docRefId;
    @CustomSafeHtml
    private String docName;
    @CustomSafeHtml
    private String docType;
    @CustomSafeHtml
    private String docCategory;
    @CustomSafeHtml
    private String tblRefUuid;

    // new payload fields matching frontend document structure
    @CustomSafeHtml
    private String documentType;
    @CustomSafeHtml
    private String fileStoreId;
    @CustomSafeHtml
    private String documentUid;
    private Long garbageId;
}
