package org.upyog.cdwm.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

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
