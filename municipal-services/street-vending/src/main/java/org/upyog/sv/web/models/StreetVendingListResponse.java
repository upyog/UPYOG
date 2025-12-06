package org.upyog.sv.web.models;

import java.util.List;

import org.springframework.validation.annotation.Validated;
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
 * Store registration details
 */
<<<<<<< HEAD
@Schema(description = "Store registration details")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-10-16T13:19:19.125+05:30")
=======
@ApiModel(description = "Store registration details")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-10-16T13:19:19.125+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StreetVendingListResponse   {
        @JsonProperty("ResponseInfo")
        private ResponseInfo responseInfo = null;

        @JsonProperty("SVDetail")
        private List<StreetVendingDetail> streetVendingDetail = null;

    	private Integer count;

}

