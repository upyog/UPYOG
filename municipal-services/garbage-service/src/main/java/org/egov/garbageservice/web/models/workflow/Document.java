package org.egov.garbageservice.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.garbageservice.web.models.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
/**
 * Contract model for a file attached to a workflow {@link ProcessInstance}.
 *
 * Behavior:
 * - Stores document metadata: documentType, fileStoreId (file-store service reference), documentUid.
 * - Listed on {@link ProcessInstance#getDocuments()} when submitting or returning workflow transitions.
 * - Maps to/from JSON via Jackson {@code @JsonProperty} fields.
 *
 * Notes:
 * - Binary content is not held here; only file-store references for the eGov file store.
 * - Used when workflow states require docUploadRequired on {@link State}.
 * - equals/hashCode is based on id only.
 */
public class Document {

    @JsonProperty("id")
    @CustomSafeHtml
    private String id = null;

    @JsonProperty("tenantId")
    @CustomSafeHtml
    private String tenantId = null;

    @JsonProperty("documentType")
    @CustomSafeHtml
    private String documentType = null;

    @JsonProperty("fileStoreId")
    @CustomSafeHtml
    private String fileStoreId = null;

    @JsonProperty("documentUid")
    @CustomSafeHtml
    private String documentUid = null;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails = null;


}

