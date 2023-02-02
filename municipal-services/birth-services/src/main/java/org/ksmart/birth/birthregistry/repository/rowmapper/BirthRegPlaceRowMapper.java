package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthPlace;
import org.springframework.stereotype.Component;
import static org.ksmart.birth.utils.BirthConstants.*;
import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class BirthRegPlaceRowMapper {
    public RegisterBirthPlace getRegBirthPlace(ResultSet rs) throws SQLException {
        return RegisterBirthPlace.builder()
                .id(rs.getString("id"))
                .birthDtlId(rs.getString("birthdtlid"))
                .placeOfBirthId(rs.getString("placeofbirthid"))
                .hospitalId(rs.getString("hospitalid"))
                .vehicleTypeId(rs.getString("vehicletypeid"))
                .vehicleRegistrationNo(rs.getString("vehicle_registration_no"))
                .vehicleFromEn(rs.getString("vehicle_from_en"))
                .vehicleToEn(rs.getString("vehicle_to_en"))
                .vehicleFromMl(rs.getString("vehicle_from_ml"))
                .vehicleFromMl(rs.getString("vehicle_to_ml"))
                .vehicleOtherEn(rs.getString("vehicle_other_en"))
                .vehicleOtherMl(rs.getString("vehicle_other_ml"))
                .vehicleAdmitHospitalEn(rs.getString("vehicle_admit_hospital_en"))
                .vehicleAdmitHospitalMl(rs.getString("vehicle_admit_hospital_ml"))
                .publicPlaceId(rs.getString("public_place_id"))
                .hoHouseHolderEn(rs.getString("ho_householder_en"))
                .hoHouseHolderMl(rs.getString("ho_householder_ml"))
                .hoResAssoNo(rs.getString("ho_res_asso_no"))
                .hoHousenNo(rs.getString("ho_houseno"))
                .hoHouseHolderEn(rs.getString("ho_housename_en"))
                .houseNameMl(rs.getString("ho_housename_ml"))
                .hoVillageId(rs.getString("ho_villageid"))
                .hoTalukId(rs.getString("ho_talukid"))
                .hoTalukId(rs.getString("ho_districtid"))
                .hoStateId(rs.getString("ho_stateid"))
                .hoPoId(rs.getString("ho_poid"))
                .hoPinNo(rs.getString("ho_pinno"))
                .hoCountryId(rs.getString("ho_countryid"))
                .wardId(rs.getString("ward_id"))
                .othDetailsEn(rs.getString("oth_details_en"))
                .othDetailsMl(rs.getString("oth_details_ml"))
                .institutionTypeId(rs.getString("institution_type_id"))
                .institutionId(rs.getString("institution_id"))
                .authOfficerId(rs.getString("auth_officer_id"))
                .authOfficerDesigId(rs.getString("auth_officer_desig_id"))
                .othAuthOfficerName(rs.getString("oth_auth_officer_name"))
                .othAuthOfficerDesig(rs.getString("oth_auth_officer_desig"))
                .informantsNameEn(rs.getString("informantsname_en"))
                .informantsAddressMl(rs.getString("informantsname_ml"))
                .informantsAddressEn(rs.getString("informantsaddress_en"))
                .informantsAddressMl(rs.getString("informantsaddress_ml"))
                .informantsMobileNo(rs.getString("informants_mobileno"))
                .informantsAadhaarNo(rs.getString("informants_aadhaar_no"))
                .hoDoorno(rs.getBoolean("ho_doorno"))
                .hoSubno(rs.getString("ho_subno"))
                .vehicleHaltplace(rs.getString("vehicle_haltplace"))
                .vehicleHospitalid(rs.getString("vehicle_hospitalid"))
                .informantAddressline2(rs.getString("informant_addressline2"))
                .hoResAssoNoMl(rs.getString("ho_res_asso_no_ml"))
                .hoMainPlaceEn(rs.getString("ho_main_place_en"))
                .hoStreetLocalityAreaEn(rs.getString("ho_street_locality_area_en"))
                .hoMainPlaceMl(rs.getString("ho_main_place_ml"))
                .hoStreetLocalityAreaMl(rs.getString("ho_street_locality_area_ml"))
                .placeDetailsEn(getPlaceDetailsEn(rs))
                .placeDetailsMl(getPlaceDetailsMl(rs))
                .build();
    }

    private String getPlaceDetailsEn(ResultSet rs) throws SQLException {System.out.println(rs.getString("placeofbirthid"));
        String address = "";
        if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_HOSPITAL)) {
            address = new StringBuilder()
                    .append(rs.getString("hospitalid") == null ? "" : rs.getString("hospitalid")+',').toString();
        } else if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_INSTITUTION)) {
            address = new StringBuilder()
                    .append(rs.getString("institution_id") == null ? "" : rs.getString("institution_id")+',').toString();
        } else if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_HOME)) {
            address = new StringBuilder()
                    .append(rs.getString("ho_housename_en") == null ? "" : rs.getString("ho_housename_en")+',')
                    .append(rs.getString("ho_res_asso_no") == null ? "" : rs.getString("ho_res_asso_no")+',')
                    .append(rs.getString("ho_main_place_en") == null ? "" : rs.getString("ho_main_place_en")+',')
                    .append(rs.getString("ho_street_locality_area_en") == null ? "" : rs.getString("ho_street_locality_area_en")+',')
                    .append(rs.getString("ho_poid") == null ? "" : rs.getString("ho_poid")+',')
                    .append(rs.getString("ho_pinno") == null ? "" : rs.getString("ho_pinno")+',')
                    .append(rs.getString("ho_districtid") == null ? "" : rs.getString("ho_districtid")+',')
                    .append(rs.getString("ho_stateid") == null ? "" : rs.getString("ho_stateid")+',')
                    .append(rs.getString("ho_countryid") == null ? "" : rs.getString("ho_countryid")).toString();
        } else if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_VEHICLE)){

        } else if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_PUBLIC)){

        } else if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_OTHERS_COUNTRY)){

        } else{
            address = "";
        }
        return address;
    }

    private String getPlaceDetailsMl(ResultSet rs) throws SQLException {
        String address = "";
        if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_HOSPITAL)){
            address = new StringBuilder()
                    .append(rs.getString("hospitalid") == null ? "" : rs.getString("hospitalid")+',').toString();
        } else if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_INSTITUTION)){

        } else if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_HOME)){
            address = new StringBuilder()
                    .append(rs.getString("ho_housename_ml") == null ? "" : rs.getString("ho_housename_ml")+',')
                    .append(rs.getString("ho_res_asso_no") == null ? "" : rs.getString("ho_res_asso_no")+',')
                    .append(rs.getString("ho_main_place_ml") == null ? "" : rs.getString("ho_main_place_ml")+',')
                    .append(rs.getString("ho_street_locality_area_ml") == null ? "" : rs.getString("ho_street_locality_area_ml")+',')
                    .append(rs.getString("ho_poid") == null ? "" : rs.getString("ho_poid") + "_ML"+',')
                    .append(rs.getString("ho_pinno") == null ? "" : rs.getString("ho_pinno")+',')
                    .append(rs.getString("ho_districtid") == null ? "" : rs.getString("ho_districtid") + "_ML"+',')
                    .append(rs.getString("ho_stateid") == null ? "" : rs.getString("ho_stateid") + "_ML"+',')
                    .append(rs.getString("ho_countryid") == null ? "" : rs.getString("ho_countryid") + "_ML").toString();
        } else if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_VEHICLE)){

        } else if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_PUBLIC)){

        } else if(rs.getString("placeofbirthid").contains(BIRTH_PLACE_OTHERS_COUNTRY)){

        } else{
            address = "";
        }
        return address;

    }
}
