package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
import org.upyog.pgrai.validation.SanitizeHtml;
=======
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.SafeHtml;
>>>>>>> master-LTS
import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * This object holds list of documents attached during the transaciton for a property
 */
<<<<<<< HEAD
@Schema(description = "This object holds list of documents attached during the transaciton for a property")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
=======
@ApiModel(description = "This object holds list of documents attached during the transaciton for a property")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document   {
<<<<<<< HEAD
        @SanitizeHtml
        @JsonProperty("id")
        private String id = null;

        @SanitizeHtml
        @JsonProperty("documentType")
        private String documentType = null;

        @SanitizeHtml
        @JsonProperty("fileStoreId")
        private String fileStoreId = null;

        @SanitizeHtml
=======
        @SafeHtml
        @JsonProperty("id")
        private String id = null;

        @SafeHtml
        @JsonProperty("documentType")
        private String documentType = null;

        @SafeHtml
        @JsonProperty("fileStoreId")
        private String fileStoreId = null;

        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("documentUid")
        private String documentUid = null;

        @JsonProperty("additionalDetails")
        private Object additionalDetails = null;


}

