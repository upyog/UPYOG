package org.upyog.sv.web.models;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.web.models.billing.Demand;
import org.upyog.sv.web.models.common.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Generate demands response for renewal
 */
<<<<<<< HEAD
@Schema(description = "Demand Response")
=======
@ApiModel(description = "Demand Response")
>>>>>>> master-LTS
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
