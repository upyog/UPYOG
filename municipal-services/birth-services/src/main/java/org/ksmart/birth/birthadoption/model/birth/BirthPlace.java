package org.ksmart.birth.birthadoption.model.birth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthPlace {
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Size(max = 64)
    @JsonProperty("birthdtlid")
    private String birthDtlId;

    @Size(max = 64)
    @JsonProperty("placeofbirthid")
    private String placeOfBirthId;

    @Size(max = 64)
    @JsonProperty("hospitalid")
    private String hospitalId;

    @Size(max = 64)
    @JsonProperty("vehicletypeid")
    private String vehicleTypeId;

    @Size(max = 64)
    @JsonProperty("vehicle_registration_no")
    private String vehicleRegistrationNo;

    @Size(max = 1000)
    @JsonProperty("vehicle_from_en")
    private String vehicleFromEn;

    @Size(max = 1000)
    @JsonProperty("vehicle_to_en")
    private String vehicleToEn;

    @Size(max = 1000)
    @JsonProperty("vehicle_from_ml")
    private String vehicleFromMl;

    @Size(max = 1000)
    @JsonProperty("vehicle_to_ml")
    private String vehicleToMl;

    @Size(max = 1000)
    @JsonProperty("vehicle_other_en")
    private String vehicleOtherEn;

    @Size(max = 1000)
    @JsonProperty("vehicle_other_ml")
    private String vehicleOtherMl;

    @Size(max = 1000)
    @JsonProperty("vehicle_admit_hospital_en")
    private String vehicleAdmitHospitalEn;

    @Size(max = 1000)
    @JsonProperty("vehicle_admit_hospital_ml")
    private String vehicleAdmitHospitalMl;

    @Size(max = 64)
    @JsonProperty("public_place_id")
    private String publicPlaceId;

    @Size(max = 2000)
    @JsonProperty("ho_householder_en")
    private String hoHouseHolderEn;

    @Size(max = 2000)
    @JsonProperty("ho_householder_ml")
    private String hoHouseHolderMl;

    @Size(max = 200)
    @JsonProperty("ho_buildingno")
    private String hoBuildingNo;

    @Size(max = 250)
    @JsonProperty("ho_res_asso_no")
    private String hoResAssoNo;

    @Size(max = 200)
    @JsonProperty("ho_houseno")
    private String hoHousenNo;

    @Size(max = 2500)
    @JsonProperty("housename_en")
    private String houseNameEn;

    @Size(max = 2500)
    @JsonProperty("housename_ml")
    private String houseNameMl;

    @Size(max = 2000)
    @JsonProperty("ho_locality_en")
    private String hoLocalityEn;

    @Size(max = 2000)
    @JsonProperty("ho_locality_ml")
    private String hoLocalityMl;

    @Size(max = 64)
    @JsonProperty("ho_villageid")
    private String hoVillageId;

    @Size(max = 64)
    @JsonProperty("ho_talukid")
    private String hoTalukId;

    @Size(max = 64)
    @JsonProperty("ho_districtid")
    private String hoDistrictId;

    @Size(max = 2000)
    @JsonProperty("ho_city_en")
    private String hoCityEn;

    @Size(max = 2000)
    @JsonProperty("ho_city_ml")
    private String hoCityMl;

    @Size(max = 64)
    @JsonProperty("ho_stateid")
    private String hoStateId;

    @Size(max = 200)
    @JsonProperty("ho_poid")
    private String hoPoId;

    @Size(max = 10)
    @JsonProperty("ho_pinno")
    private String hoPinNo;

    @Size(max = 64)
    @JsonProperty("ho_countryid")
    private String hoCountryId;

    @Size(max = 64)
    @JsonProperty("ward_id")
    private String wardId;

    @Size(max = 2000)
    @JsonProperty("oth_details_en")
    private String othDetailsEn;

    @Size(max = 2000)
    @JsonProperty("oth_details_ml")
    private String othDetailsMl;

    @Size(max = 64)
    @JsonProperty("institution_type_id")
    private String institutionTypeId;

    @Size(max = 64)
    @JsonProperty("institution_id")
    private String institutionId;

    @Size(max = 64)
    @JsonProperty("auth_officer_id")
    private String authOfficerId;

    @Size(max = 64)
    @JsonProperty("auth_officer_desig_id")
    private String authOfficerDesigId;

    @Size(max = 2000)
    @JsonProperty("oth_auth_officer_name")
    private String othAuthOfficerName;

    @Size(max = 2000)
    @JsonProperty("oth_auth_officer_desig")
    private String othAuthOfficerDesig;

    @Size(max = 1000)
    @JsonProperty("informantsname_en")
    private String informantsNameEn;

    @Size(max = 1000)
    @JsonProperty("informantsname_ml")
    private String informantsNameMl;

    @Size(max = 2000)
    @JsonProperty("informantsaddress_en")
    private String informantsAddressEn;

    @Size(max = 2000)
    @JsonProperty("informantsaddress_ml")
    private String informantsAddressMl;

    @Size(max = 12)
    @JsonProperty("informants_mobileno")
    private String informantsMobileNo;

    @Size(max = 20)
    @JsonProperty("informants_aadhaar_no")
    private String informantsAadhaarNo;
}
