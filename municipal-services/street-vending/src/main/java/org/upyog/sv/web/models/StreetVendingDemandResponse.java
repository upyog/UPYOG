package org.upyog.sv.web.models;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.web.models.billing.Demand;
import org.upyog.sv.web.models.common.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Generate demands response for renewal
 */
@ApiModel(description = "Demand Response")
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StreetVendingDemandResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo = null;
    private List<Demand> demands;

}
