package org.upyog.wms.web.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Size(max = 64)
    private String id;

    @Size(max = 64)
    private String tenantId;

    @Size(max = 64)
    private String recommendedBy;

    @Size(max = 256)
    private String recommendedByName;

    private Boolean isActive;

    private Object additionalDetails;

    @Valid
    private List<Document> documents;

    private AuditDetails auditDetails;
}
