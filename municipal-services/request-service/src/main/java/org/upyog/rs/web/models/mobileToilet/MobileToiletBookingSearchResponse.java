package org.upyog.rs.web.models.mobileToilet; // NOSONAR java:S120 - package name kept to preserve imports/API // NOSONAR

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import org.upyog.rs.web.models.ResponseInfo;

import java.util.List;


@Schema(description = "Store booking details")
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("java:S120")
public class MobileToiletBookingSearchResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("mobileToiletBookingDetails")
    private List<MobileToiletBookingDetail> mobileToiletBookingDetails;

    private Integer count;
}
