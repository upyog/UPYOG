package org.upyog.rs.web.models.waterTanker;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.*;
import org.springframework.validation.annotation.Validated;
import org.upyog.rs.web.models.ResponseInfo;
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
public class WaterTankerBookingSearchResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("waterTankerBookingDetail")
    private List<WaterTankerBookingDetail> waterTankerBookingDetails;

    private Integer count;
}
