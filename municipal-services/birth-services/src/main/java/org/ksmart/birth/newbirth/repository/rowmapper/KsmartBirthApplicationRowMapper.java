package org.ksmart.birth.newbirth.repository.rowmapper;

import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class KsmartBirthApplicationRowMapper implements ResultSetExtractor<List<NewBirthApplication>>, KsmartBaseRowMapper,KsmartBirthParentDetailRowMapper,KsmartInformatDetailsRowMapper,KsmartBirthParentAddressRowMapper{

    @Override
    public List<NewBirthApplication> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<NewBirthApplication> result = new ArrayList<>();

        while (rs.next()) {
            result.add(NewBirthApplication.builder()
                    .id(rs.getString("ba_id"))
                    .dateOfReport(rs.getLong("ba_dateofreport"))
                    .dateOfBirth(rs.getLong("ba_dateofbirth"))
                    .timeOfBirth(rs.getLong("ba_timeofbirth"))
                    .gender(rs.getString("ba_gender"))
                    .aadharNo(rs.getString("ba_aadharno"))
                    .isChildName(true)
                    .firstNameEn(rs.getString("ba_firstname_en"))
                    .firstNameMl(rs.getString("ba_firstname_ml"))
                    .middleNameEn(rs.getString("ba_middlename_en"))
                    .middleNameMl(rs.getString("ba_middlename_ml"))
                    .lastNameEn(rs.getString("ba_lastname_en"))
                    .lastNameMl(rs.getString("ba_lastname_ml"))
                    .placeofBirthId(rs.getString("pla_placeofbirthid"))
                    .institutionTypeId(rs.getString("pla_institution_type_id"))
                    .hospitalId(rs.getString("pla_hospitalid"))
//                    .hospitalName(rs.getString("pla_hospitalname"))
//                    .hospitalNameMl(rs.getString("pla_hospitalnameml"))
                    .institutionId(rs.getString("pla_institution_id"))

                    .wardId(rs.getString("pres_ward_code"))
//                    .wardNumber(rs.getString("pla_wardnumber"))
//                    .wardNameEn(rs.getString("pla_wardnamen"))
//                    .wardNameMl(rs.getString("pla_wardnameml"))

                    .adrsPincode(rs.getString("pla_ho_pinno"))
                    .adrsHouseNameEn(rs.getString("pla_ho_householder_en"))
                    .adrsHouseNameMl(rs.getString("pla_ho_householder_ml"))
                    .adrsPostOffice(rs.getString("pla_ho_poid"))
                    .adrsLocalityNameEn(rs.getString("pla_ho_locality_en"))
                    .adrsLocalityNameMl(rs.getString("pla_ho_locality_ml"))
                    .adrsStreetNameEn(rs.getString("pla_ho_street_name_en"))
                    .adrsStreetNameMl(rs.getString("pla_ho_street_name_ml"))
                    .vehicleHaltplace(rs.getString("pla_vehicleHaltplace"))
                    .vehicleHaltPlaceMl(rs.getString("pla_vehicleHaltplace_ml"))
                    .vehicleFromMl(rs.getString("pla_vehicleFromMl"))
                    .vehicleTypeid(rs.getString("pla_vehicleTypeid"))
                    .vehicleFromEn(rs.getString("pla_vehicleFromEn"))
                    .vehicleDesDetailsEn(rs.getString("pla_vehicleDesDetailsEn"))
                    .vehicleToEn(rs.getString("pla_vehicleToEn"))
                    .vehicleToMl(rs.getString("pla_vehicleToMl"))
                    .vehicleRegistrationNo(rs.getString("pla_vehicleRegistrationNo"))
                    .vehicleDesDetailsEn(rs.getString("pla_vehicleDesDetailsEn"))
                    .setadmittedHospitalEn(rs.getString("pla_setadmittedHospitalEn"))
                    .publicPlaceDecpEn(rs.getString("pla_publicPlaceDecpEn"))
                    .publicPlaceType(rs.getString("publicPlaceType"))
                    .birthWeight(rs.getDouble("stat_birthWeight"))
                    .pregnancyDuration(rs.getInt("stat_pregnancyDuration"))
                    .medicalAttensionSub(rs.getString("stat_nature_of_medical_attention"))
                    .deliveryMethods(rs.getString("stat_delivery_method"))
                    .esignUserDesigCode(rs.getString("ba_esign_user_desig_code"))
                    .tenantId(rs.getString("ba_tenantid"))
                    .applicationType(rs.getString("ba_applicationtype"))
                    .businessService(rs.getString("ba_businessservice"))
                    .workFlowCode(rs.getString("ba_workflowcode"))
                    .registrationNo(rs.getString("pla_vehicleRegistrationNo"))
                    .ampm(rs.getString("ba_am_pm"))
                    .remarksEn(rs.getString("ba_remarks_en"))
                    .remarksMl(rs.getString("ba_aadharno"))
                    .isAdopted(Boolean.valueOf(rs.getString("ba_is_adopted")))
                    .isAdopted(Boolean.valueOf(rs.getString("ba_is_father_info_missing")))
                    //.isMotherInfoMissing(Boolean.valueOf(rs.getString("ba_is_mother_info_missing")))
                    //.noOfAliveBirth(rs.getInt("ba_no_of_alive_birth"))
                  //  .multipleBirthDetailsIid(rs.getString("ba_multiplebirthdetid"))
                   // .isBornOutside(rs.getBoolean("ba_is_born_outside"))
                 //   .passportNo(rs.getString("ba_ot_passportno"))
                  //  .dateOfArrival(rs.getLong("ba_ot_dateofarrival")
                    .applicationNo(rs.getString("ba_applicationno"))
                    .applicationType(rs.getString("ba_applicationtype"))
                    .workFlowCode(rs.getString("ba_workflowcode"))
                    .action(rs.getString("ba_action"))
                    .applicationStatus(rs.getString("ba_status"))
                    .auditDetails(getAuditDetails(rs))
                    .parentsDetails(KsmartBirthParentDetail(rs))
                    .birthStatisticsUuid(rs.getString("stat_id"))
                    .birthPlaceUuid(rs.getString("pla_id"))
                    .fileNumber(rs.getString("ba_fm_fileno"))
                    .fileDate(rs.getLong("ba_file_date"))
                    .fileStatus(rs.getString("ba_file_status"))
                    .informatDetail(getKsmartInformatDetail(rs))
///                    .initiatorDetails(getKsmartInitiatorDetail(rs))
                    .parentAddress(getKsmartBirthParentAddress(rs))
                    .build());
        }
        return result;
    }
}


