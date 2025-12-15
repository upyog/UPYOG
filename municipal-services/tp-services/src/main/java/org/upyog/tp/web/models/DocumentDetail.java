package org.upyog.tp.web.models;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.upyog.tp.web.models.AuditDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Document details of uploaded documents
 */

@Schema(description = "Document details of uploaded documents")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDetail {

    private String documentDetailId;

    private String bookingId;

    @NotBlank
    private String documentType;

    @NotBlank
    private String fileStoreId;

    private AuditDetails auditDetails;

}
