package org.upyog.cdwm.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD

=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

<<<<<<< HEAD

=======
@ApiModel(description = "Store booking details")
>>>>>>> master-LTS
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CNDServiceSearchResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty("cndApplicationDetail")
    private List<CNDApplicationDetail> cndApplicationDetails = null;

    private Integer count;
}
