package org.upyog.tp.web.models.treePruning;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.*;
import org.springframework.validation.annotation.Validated;
import org.upyog.tp.web.models.ResponseInfo;

import java.util.List;


<<<<<<< HEAD
@Schema(description = "Store booking details")
=======
@ApiModel(description = "Store booking details")
>>>>>>> master-LTS
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TreePruningSearchResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("treePruningBookingDetails")
    private List<TreePruningBookingDetail> treePruningBookingDetails;

    private Integer count;
}
