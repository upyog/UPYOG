package org.ksmart.birth.birthapplication.repository.rowmapper;

import org.ksmart.birth.birthapplication.model.birth.BirthPlace;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthPlaceRowMapper {
    default BirthPlace getBirthPlace(ResultSet rs) throws SQLException {
        return BirthPlace.builder()
                .id(rs.getString("pla_id"))
                .birthDtlId(rs.getString("pla_birthdtlid"))
                .placeOfBirthId(rs.getString("pla_placeofbirthid"))
                .hospitalId(rs.getString("pla_hospitalid"))
                .vehicleTypeId(rs.getString("pla_vehicletypeid"))
                .vehicleRegistrationNo(rs.getString("pla_vehicle_registration_no"))
                .vehicleFromEn(rs.getString("pla_vehicle_from_en"))
                .vehicleToEn(rs.getString("pla_vehicle_to_en"))
                .vehicleFromMl(rs.getString("pla_vehicle_from_ml"))
                .vehicleFromMl(rs.getString("pla_vehicle_to_ml"))
                .vehicleOtherEn(rs.getString("pla_vehicle_other_en"))
                .vehicleOtherMl(rs.getString("pla_vehicle_other_ml"))
                .vehicleAdmitHospitalEn(rs.getString("pla_vehicle_admit_hospital_en"))
                .vehicleAdmitHospitalMl(rs.getString("pla_vehicle_admit_hospital_ml"))
                .publicPlaceId(rs.getString("pla_public_place_id"))
                .hoHouseHolderEn(rs.getString("pla_ho_householder_en"))
                .hoHouseHolderMl(rs.getString("pla_ho_householder_ml"))
                .hoBuildingNo(rs.getString("pla_ho_buildingno"))
                .hoResAssoNo(rs.getString("pla_ho_res_asso_no"))
                .hoHousenNo(rs.getString("pla_ho_houseno"))
                .hoHouseHolderEn(rs.getString("pla_ho_housename_en"))
                .houseNameMl(rs.getString("pla_ho_housename_ml"))
                .hoVillageId(rs.getString("pla_ho_villageid"))
                .hoTalukId(rs.getString("pla_ho_talukid"))
                .hoTalukId(rs.getString("pla_ho_districtid"))
                .hoStateId(rs.getString("pla_ho_stateid"))
                .hoPoId(rs.getString("pla_ho_poid"))
                .hoPinNo(rs.getString("pla_ho_pinno"))
                .hoCountryId(rs.getString("pla_ho_countryid"))
                .wardId(rs.getString("pla_ward_id"))
                .othDetailsEn(rs.getString("pla_oth_details_en"))
                .othDetailsMl(rs.getString("pla_oth_details_ml"))
                .institutionTypeId(rs.getString("pla_institution_type_id"))
                .institutionId(rs.getString("pla_institution_id"))
                .authOfficerId(rs.getString("pla_auth_officer_id"))
                .authOfficerDesigId(rs.getString("pla_auth_officer_desig_id"))
                .othAuthOfficerName(rs.getString("pla_oth_auth_officer_name"))
                .othAuthOfficerDesig(rs.getString("pla_oth_auth_officer_desig"))
                .informantsNameEn(rs.getString("pla_informantsname_en"))
                .informantsAddressMl(rs.getString("pla_informantsname_ml"))
                .informantsAddressEn(rs.getString("pla_informantsaddress_en"))
                .informantsAddressMl(rs.getString("pla_informantsaddress_ml"))
                .informantsMobileNo(rs.getString("pla_informants_mobileno"))
                .informantsAadhaarNo(rs.getString("pla_informants_aadhaar_no"))
                .build();
    }
}
