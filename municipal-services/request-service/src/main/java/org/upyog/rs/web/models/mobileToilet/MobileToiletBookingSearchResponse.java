package org.upyog.rs.web.models.mobileToilet;

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
public class MobileToiletBookingSearchResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("mobileToiletBookingDetails")
    private List<MobileToiletBookingDetail> mobileToiletBookingDetails;

    private Integer count;
}
