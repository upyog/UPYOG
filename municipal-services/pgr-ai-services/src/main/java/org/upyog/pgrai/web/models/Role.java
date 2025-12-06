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
 * minimal representation of the Roles in the system to be carried along in UserInfo with RequestHeader meta data. Actual authorization service to extend this to have more role related attributes 
 */
<<<<<<< HEAD
@Schema(description = "minimal representation of the Roles in the system to be carried along in UserInfo with RequestHeader meta data. Actual authorization service to extend this to have more role related attributes ")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
=======
@ApiModel(description = "minimal representation of the Roles in the system to be carried along in UserInfo with RequestHeader meta data. Actual authorization service to extend this to have more role related attributes ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role   {
        @JsonProperty("name")
        private String name = null;

        @JsonProperty("code")
        private String code = null;

        @JsonProperty("tenantId")
        private String tenantId = null;

        @JsonProperty("description")
        private String description = null;


}

