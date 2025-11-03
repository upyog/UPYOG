package org.egov.asset.web.models;

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
public class VendorSearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("vendorIds")
    private List<String> vendorIds;

    @JsonProperty("vendorNumbers")
    private List<String> vendorNumbers;

    @JsonProperty("vendorName")
    private String vendorName;

    @JsonProperty("contactNumber")
    private String contactNumber;

    @JsonProperty("gstin")
    private String gstin;

    @JsonProperty("pan")
    private String pan;

    @JsonProperty("status")
    private String status;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;
}