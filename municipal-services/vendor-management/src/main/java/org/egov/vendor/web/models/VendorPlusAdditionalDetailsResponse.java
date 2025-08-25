package org.egov.vendor.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.vendor.repository.dto.VendorDetailsDTO;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Contract class to send response. Array of items are used in case of search
 * results or response for create, whereas single item is used for update
 */
@ApiModel(description = "Contract class to send response. Array of items are used in case of search results or response for create, whereas single item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VendorPlusAdditionalDetailsResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty("VendorDetails")
    @Valid
    private List<VendorDetailsDTO> vendorDetailsDTO = null;

    public VendorPlusAdditionalDetailsResponse addVendorAdditionalDetailItem(VendorDetailsDTO item) {
        if (this.vendorDetailsDTO == null) {
            this.vendorDetailsDTO = new ArrayList<>();
        }
        this.vendorDetailsDTO.add(item);
        return this;
    }

    public VendorPlusAdditionalDetailsResponse VendorAdditionalDetail(List<VendorDetailsDTO> items) {
        this.vendorDetailsDTO = items;
        return this;
    }


}
