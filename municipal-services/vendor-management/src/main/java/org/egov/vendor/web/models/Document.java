package org.egov.vendor.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This object holds list of documents attached during the transaciton for a property
 */
@ApiModel(description = "This object holds list of documents attached during the transaciton for a property")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")



@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "eg_vendor_documents")
public class Document {

    @Id
    @JsonProperty("documentId")
    @Column(name = "documentId")
    private String documentId = null;

    @JsonProperty("documentType")
    @Column(name = "documentType")
    private String documentType = null;

    @JsonProperty("fileStoreId")
    @Column(name = "fileStoreId")
    private String fileStoreId = null;

    @JsonProperty("documentUid")
    @Column(name = "documentUid")
    private String documentUid = null;

    @Column(name = "vendor_additional_details_id", nullable = false)
    private String vendorAdditionalDetailsId;

}

