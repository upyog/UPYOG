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

public class RegisterBirthPlace {

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
    @JsonProperty("institution_type_id")
    private String institutionTypeId;

    @Size(max = 64)
    @JsonProperty("institution_id")
    private String institutionId;

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
    @JsonProperty("ho_houseno")
    private String hoHousenNo;

    @Size(max = 2500)
    @JsonProperty("housename_en")
    private String houseNameEn;

    @Size(max = 2500)
    @JsonProperty("housename_ml")
    private String houseNameMl;

    @Size(max = 64)
    @JsonProperty("ho_villageid")
    private String hoVillageId;

    @Size(max = 64)
    @JsonProperty("ho_talukid")
    private String hoTalukId;

    @Size(max = 64)
    @JsonProperty("ho_districtid")
    private String hoDistrictId;

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
    @Size(max = 1000)
    @JsonProperty("vehicle_haltplace_en")
    private String vehicleHaltplaceEn;
    @Size(max = 1000)
    @JsonProperty("vehicle_haltplace_ml")
    private String vehicleHaltplaceMl;

    @Size(max = 64)
    @JsonProperty("vehicle_hospitalid")
    private String vehicleHospitalid;

    @Size(max = 64)
    @JsonProperty("informant_addressline2")
    private String informantAddressline2;

    @Size(max = 1000)
    @JsonProperty("ho_locality_en")
    private String hoLocalityEn;

    @Size(max = 1000)
    @JsonProperty("ho_locality_ml")
    private String hoLocalityMl;

    @Size(max = 2000)
    @JsonProperty("ho_street_name_en")
    private String hoStreetEn;

    @Size(max = 2000)
    @JsonProperty("ho_street_name_ml")
    private String hoStreetMl;

    @Size(max = 2000)
    @JsonProperty("vehicle_desc")
    private String vehicleDesc;

    @Size(max = 2000)
    @JsonProperty("public_place_desc")
    private String publicPlaceDesc;

    @Size(max = 1000)
    @JsonProperty("public_locality_en")
    private String publicLocalityEn;

    @Size(max = 1000)
    @JsonProperty("public_locality_ml")
    private String publicLocalityMl;

    @Size(max = 1000)
    @JsonProperty("public_street_name_en")
    private String publicStreetEn;

    @Size(max = 1000)
    @JsonProperty("public_street_name_ml")
    private String publicStreetMl;

    @Size(max = 1000)
    @JsonProperty("ot_birth_place_en")
    private String otherBirthPlaceEn;

    @Size(max = 1000)
    @JsonProperty("ot_birth_place_ml")
    private String otherBirthPlaceMl;

    @Size(max = 2500)
    @JsonProperty("ot_address1_en")
    private String otherBirthAddress1En;

    @Size(max = 2500)
    @JsonProperty("ot_address1_ml")
    private String otherBirthAddress1Ml;

    @Size(max = 1000)
    @JsonProperty("ot_zipcode")
    private String otherZipcode;

    @Size(max = 2500)
    @JsonProperty("ot_address2_en")
    private String otherBirthAddress2En;

    @Size(max = 2500)
    @JsonProperty("ot_address2_ml")
    private String otherBirthAddress2Ml;

    @Size(max = 1000)
    @JsonProperty("ot_state_region_province_en")
    private String otherBirthProvinceEn;

    @Size(max = 1000)
    @JsonProperty("ot_state_region_province_ml")
    private String otherBirthProvinceMl;

    @Size(max = 128)
    @JsonProperty("ot_country")
    private String otherBirthCountry;

    @Size(max = 1000)
    @JsonProperty("ot_town_village_en")
    private String otherBirthVillageEn;

    @Size(max = 1000)
    @JsonProperty("ot_town_village_ml")
    private String otherBirthVillageMl;
}
