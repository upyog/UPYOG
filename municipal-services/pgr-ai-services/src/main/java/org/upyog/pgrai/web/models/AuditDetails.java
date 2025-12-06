package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Collection of audit related fields used by most models
 */
<<<<<<< HEAD
@Schema(description = "Collection of audit related fields used by most models")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
=======
@ApiModel(description = "Collection of audit related fields used by most models")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditDetails   {
        @JsonProperty("createdBy")
        private String createdBy = null;

        @JsonProperty("lastModifiedBy")
        private String lastModifiedBy = null;

        @JsonProperty("createdTime")
        private Long createdTime = null;

        @JsonProperty("lastModifiedTime")
        private Long lastModifiedTime = null;


}

