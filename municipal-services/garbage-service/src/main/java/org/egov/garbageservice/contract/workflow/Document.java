package org.egov.garbageservice.contract.workflow;

import org.egov.garbageservice.model.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class Document   {

//        @Size(max=64)
        @JsonProperty("id")
        @CustomSafeHtml
        private String id = null;

//        @Size(max=64)
        @JsonProperty("tenantId")
        @CustomSafeHtml
        private String tenantId = null;

//        @Size(max=64)
        @JsonProperty("documentType")
        @CustomSafeHtml
        private String documentType = null;

//        @Size(max=64)
        @JsonProperty("fileStoreId")
        @CustomSafeHtml
        private String fileStoreId = null;

//        @Size(max=64)
        @JsonProperty("documentUid")
        @CustomSafeHtml
        private String documentUid = null;

        @JsonProperty("auditDetails")
        private AuditDetails auditDetails = null;


}

