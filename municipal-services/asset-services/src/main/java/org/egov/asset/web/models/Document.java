package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.*;
import org.springframework.validation.annotation.Validated;

/**
 * This object holds list of documents attached during the transaciton for a property
 */
<<<<<<< HEAD
@Schema(description = "This object holds list of documents attached during the transaciton for a property")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
=======
@ApiModel(description = "This object holds list of documents attached during the transaciton for a property")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {
    @JsonProperty("documentId")
    private String documentId = null;

    @JsonProperty("documentType")
    private String documentType = null;

    @JsonProperty("fileStoreId")
    private String fileStoreId = null;

    @JsonProperty("documentUid")
    private String documentUid = null;

    @JsonProperty("docDetails")
    private Object docDetails = null;


}

