package org.egov.garbageservice.web.models.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.egov.garbageservice.enums.Status;
import org.egov.garbageservice.web.models.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

/**
 * Uploaded document metadata linked to an OwnerInfo or property record.
 * References files in eGov file-store via fileStoreId and tracks document type, uid, and lifecycle status.
 * Used when garbage applications require owner KYC or supporting proofs.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"fileStoreId", "documentUid", "id"})
public class Document {

    @JsonProperty("id")
    @CustomSafeHtml
    private String id;

    @JsonProperty("documentType")
    @NotNull
    @CustomSafeHtml
    private String documentType;

    @JsonProperty("fileStoreId")
    @NotNull
    @CustomSafeHtml
    private String fileStoreId;

    @JsonProperty("documentUid")
    @CustomSafeHtml
    private String documentUid;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("status")
    private Status status;
}
