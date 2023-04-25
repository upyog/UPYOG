package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthPlace;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.stereotype.Component;
import static org.ksmart.birth.utils.BirthConstants.*;
import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class BirthRegPlaceRowMapper {

    public RegisterBirthPlace getRegBirthPlace(ResultSet rs) throws SQLException {
        return RegisterBirthPlace.builder()
                .id(rs.getString("pla_id"))
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
                .houseNameEn(rs.getString("ho_housename_en"))
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
                .vehicleHaltplaceEn(rs.getString("vehicle_haltplace_en"))
                .vehicleHaltplaceMl(rs.getString("vehicle_haltplace_ml"))
                .vehicleHospitalid(rs.getString("vehicle_hospitalid"))
                .informantAddressline2(rs.getString("informant_addressline2"))
                .hoLocalityEn(rs.getString("ho_locality_en"))
                .hoStreetEn(rs.getString("ho_street_name_en"))
                .hoLocalityMl(rs.getString("ho_locality_ml"))
                .hoStreetMl(rs.getString("ho_street_name_ml"))
                .vehicleDesc(rs.getString("vehicle_desc"))
                .publicPlaceDesc(rs.getString("public_place_desc"))
                .build();
    }

}
