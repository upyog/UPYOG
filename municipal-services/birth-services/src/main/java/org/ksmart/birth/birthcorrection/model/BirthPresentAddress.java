package org.ksmart.birth.birthcorrection.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BirthPresentAddress {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Size(max = 200)
    @JsonProperty("buildingno")
    private String buildingNo;

    @Size(max = 200)
    @JsonProperty("houseno")
    private String houseNo;

    @Size(max = 250)
    @JsonProperty("res_asso_no")
    private String resAssoNo;

    @Size(max = 2500)
    @JsonProperty("housename_en")
    private String houseNameEn;

    @Size(max = 2500)
    @JsonProperty("housename_ml")
    private String houseNameMl;

    @Size(max = 2500)
    @JsonProperty("ot_address1_en")
    private String otAddress1En;

    @Size(max = 2500)
    @JsonProperty("ot_address1_ml")
    private String otAddress1Ml;

    @Size(max = 2500)
    @JsonProperty("ot_address2_en")
    private String otAddress2En;

    @Size(max = 2500)
    @JsonProperty("ot_address2_ml")
    private String otAddress2Ml;

    @Size(max = 2500)
    @JsonProperty("locality_en")
    private String localityEn;

    @Size(max = 2500)
    @JsonProperty("locality_ml")
    private String localityMl;

    @Size(max = 2500)
    @JsonProperty("city_en")
    private String cityEn;

    @Size(max = 2500)
    @JsonProperty("city_ml")
    private String cityMl;

    @Size(max = 64)
    @JsonProperty("villageid")
    private String villageId;

    @Size(max = 64)
    @JsonProperty("tenantid")
    private String tenantId;

    @Size(max = 64)
    @JsonProperty("talukid")
    private String talukId;

    @Size(max = 64)
    @JsonProperty("districtid")
    private String districtId;

    @Size(max = 64)
    @JsonProperty("stateid")
    private String stateId;

    @Size(max = 64)
    @JsonProperty("poid")
    private String poId;

    @Size(max = 10)
    @JsonProperty("pinno")
    private String pinNo;

    @Size(max = 2500)
    @JsonProperty("ot_state_region_province_en")
    private String otStateRegionProvinceEn;

    @Size(max = 2500)
    @JsonProperty("ot_state_region_province_ml")
    private String otStateRegionProvinceMl;

    @Size(max = 64)
    @JsonProperty("countryid")
    private String countryId;

    @Size(max = 64)
    @JsonProperty("birthdtlid")
    private String birthDtlId;


}
