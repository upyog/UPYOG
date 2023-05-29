package org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication;

import org.ksmart.birth.birthregistry.model.RegisterBirthPlace;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RegisterPlaceRowMapperForApp {

    public RegisterBirthPlace getRegAppPlace(ResultSet rs) throws SQLException {
        return RegisterBirthPlace.builder()
                //place
                .placeOfBirthId(rs.getString("pla_placeofbirthid"))
                .wardId(rs.getString("pla_ward_id"))

                //hospital
                .hospitalId(rs.getString("pla_hospitalid"))

                //vehicle
                .vehicleTypeId(rs.getString("pla_vehicletypeid"))
                .vehicleRegistrationNo(rs.getString("pla_vehicle_registration_no"))
                .vehicleFromEn(rs.getString("pla_vehicle_from_en"))
                .vehicleToEn(rs.getString("pla_vehicle_to_en"))
                .vehicleFromEn(rs.getString("pla_vehicle_from_en"))
                .vehicleFromMl(rs.getString("pla_vehicle_to_ml"))
                .vehicleAdmitHospitalEn(rs.getString("pla_vehicle_admit_hospital_en"))
                .vehicleHaltplaceEn(rs.getString("pla_vehicle_haltplace_en"))
                .vehicleHaltplaceMl(rs.getString("pla_vehicle_haltplace_ml"))
                .vehicleHospitalid(rs.getString("pla_vehicle_hospitalid"))
                .vehicleDesc(rs.getString("pla_vehicle_desc"))

                //HOME
                .hoHouseHolderEn(rs.getString("pla_ho_householder_en"))
                .houseNameEn(rs.getString("pla_ho_housename_en"))
                .houseNameMl(rs.getString("pla_ho_housename_ml"))
                .hoVillageId(rs.getString("pla_ho_villageid"))
                .hoTalukId(rs.getString("pla_ho_talukid"))
                .hoTalukId(rs.getString("pla_ho_districtid"))
                .hoStateId(rs.getString("pla_ho_stateid"))
                .hoPoId(rs.getString("pla_ho_poid"))
                .hoPinNo(rs.getString("pla_ho_pinno"))
                .hoCountryId(rs.getString("pla_ho_countryid"))
                .hoLocalityEn(rs.getString("pla_ho_locality_en"))
                .hoStreetEn(rs.getString("pla_ho_street_name_en"))
                .hoLocalityMl(rs.getString("pla_ho_locality_ml"))
                .hoStreetMl(rs.getString("pla_ho_street_name_ml"))

                //INSTITUTION
                .institutionTypeId(rs.getString("pla_institution_type_id"))
                .institutionId(rs.getString("pla_institution_id"))

                //INFORMANT DETAILS
                .authOfficerId(rs.getString("pla_auth_officer_id"))
                .authOfficerDesigId(rs.getString("pla_auth_officer_desig_id"))
                .othAuthOfficerName(rs.getString("pla_oth_auth_officer_name"))
                .othAuthOfficerDesig(rs.getString("pla_oth_auth_officer_desig"))
                .informantsAddressEn(rs.getString("pla_informantsaddress_en"))
                .informantsMobileNo(rs.getString("pla_informants_mobileno"))
                .informantsAadhaarNo(rs.getString("pla_informants_aadhaar_no"))

                //public place
                .publicPlaceId(rs.getString("pla_public_place_id"))
                .publicPlaceDesc(rs.getString("pla_public_place_desc"))
                .publicLocalityEn(rs.getString("pla_public_locality_en"))
                .publicLocalityMl(rs.getString("pla_public_locality_ml"))
                .publicStreetEn(rs.getString("pla_public_street_name_en"))
                .publicStreetMl(rs.getString("pla_public_street_name_ml"))

                //OUTSIDE INDIA
                .otherBirthPlaceEn(rs.getString("pla_ot_birth_place_en"))
                .otherBirthPlaceMl(rs.getString("pla_ot_birth_place_ml"))
                .otherBirthAddress1En(rs.getString("pla_ot_address1_en"))
                .otherBirthAddress1Ml(rs.getString("pla_ot_address1_ml"))
                .otherBirthAddress2En(rs.getString("pla_ot_address2_en"))
                .otherBirthAddress2Ml(rs.getString("pla_ot_address2_ml"))
                .otherBirthProvinceEn(rs.getString("pla_ot_state_region_province_en"))
                .otherBirthProvinceMl(rs.getString("pla_ot_state_region_province_ml"))
                .otherBirthVillageEn(rs.getString("pla_ot_town_village_en"))
                .otherBirthVillageMl(rs.getString("pla_ot_town_village_ml"))
                .otherZipcode(rs.getString("pla_ot_zipcode"))
                .otherBirthCountry(rs.getString("pla_ot_country"))

                .build();
    }

}
