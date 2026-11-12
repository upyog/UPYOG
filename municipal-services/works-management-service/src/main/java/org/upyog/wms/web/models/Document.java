package org.upyog.wms.web.models;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DIGIT document contract. The file itself lives in egov-filestore. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Size(max = 64)
    private String id;

    @Size(max = 64)
    private String tenantId;

    @Size(max = 64)
    private String documentType;

    @Size(max = 64)
    private String fileStoreId;

    @Size(max = 64)
    private String documentUid;

    private Boolean isActive;

    private AuditDetails auditDetails;
}
