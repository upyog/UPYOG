package org.upyog.cdwm.web.models;

<<<<<<< HEAD

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
 * Store registration details
 */
<<<<<<< HEAD

@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2025-02-12T16:11:18.767+05:30")
=======
@ApiModel(description = "Store registration details")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2025-02-12T16:11:18.767+05:30")
>>>>>>> master-LTS
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CNDApplicationResponse {

        private ResponseInfo responseInfo;

        private CNDApplicationDetail cndApplicationDetails;


}

