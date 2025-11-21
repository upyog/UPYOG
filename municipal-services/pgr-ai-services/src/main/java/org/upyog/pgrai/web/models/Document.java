package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.upyog.pgrai.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * This object holds list of documents attached during the transaciton for a property
 */
@Schema(description = "This object holds list of documents attached during the transaciton for a property")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document   {
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
        @JsonProperty("documentUid")
        private String documentUid = null;

        @JsonProperty("additionalDetails")
        private Object additionalDetails = null;


}

