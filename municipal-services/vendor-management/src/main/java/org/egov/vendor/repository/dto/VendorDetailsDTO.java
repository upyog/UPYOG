package org.egov.vendor.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.vendor.web.models.vendorcontract.location.Address;
import org.egov.vendor.web.models.VendorAdditionalDetails;
import org.egov.vendor.web.models.vendorcontract.vendor.Vendor;

@Data
@Builder
@NoArgsConstructor
//@AllArgsConstructor
public class VendorDetailsDTO {
    private Vendor vendor;
    private VendorAdditionalDetails vendorAdditionalDetails;


    public VendorDetailsDTO(Vendor vendor, VendorAdditionalDetails vendorAdditionalDetails) {
        this.vendor = vendor;
        this.vendorAdditionalDetails = vendorAdditionalDetails;
    }
}
