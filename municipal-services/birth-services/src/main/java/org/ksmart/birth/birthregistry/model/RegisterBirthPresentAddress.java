package org.ksmart.birth.birthregistry.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.common.model.AuditDetails;

import javax.validation.constraints.Size;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RegisterBirthPresentAddress {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Size(max = 200)
    @JsonProperty("resdnce_addr_type")
    private String resdnceAddrType;

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

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
    @Size(max = 64)
    @JsonProperty("bio_adopt")
    private String bioAdopt;

    @Size(max = 250)
    @JsonProperty("res_asso_no_ml")
    private String resAssoNoMl;

    @Size(max = 1000)
    @JsonProperty("taluk_name")
    private String talukName;

    @Size(max = 1000)
    @JsonProperty("village_name")
    private String villageName;

    @Size(max = 64)
    @JsonProperty("ward_code")
    private String wardCode;

    @JsonProperty("doorno")
    private String doorNo;

    @Size(max = 10)
    @JsonProperty("subno")
    private String subNo;

    @Size(max = 10)
    @JsonProperty("ot_zipcode")
    private String otZipcode;

    @Size(max = 1000)
    @JsonProperty("main_place_en")
    private String mainPlaceEn;

    @Size(max = 1000)
    @JsonProperty("street_locality_area_en")
    private String streetLocalityAreaEn;

    @Size(max = 1000)
    @JsonProperty("main_place_ml")
    private String mainPlaceMl;
    @Size(max = 1000)
    @JsonProperty("street_locality_area_ml")
    private String streetLocalityAreaMl;

    @Size(max = 2500)
    private String presentAddress;
    @Size(max = 2500)
    private String presentAddressMl;

}
