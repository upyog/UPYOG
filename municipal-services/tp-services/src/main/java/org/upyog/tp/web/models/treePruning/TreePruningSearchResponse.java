package org.upyog.tp.web.models.treePruning;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import org.upyog.tp.web.models.ResponseInfo;

import java.util.List;


@Schema(description = "Store booking details")
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
