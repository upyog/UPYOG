package org.upyog.rs.web.models.mobileToilet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import org.upyog.rs.web.models.ResponseInfo;

import java.util.List;


@ApiModel(description = "Store booking details")
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
