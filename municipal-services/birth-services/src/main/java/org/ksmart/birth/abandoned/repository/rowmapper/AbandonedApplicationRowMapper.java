package org.ksmart.birth.abandoned.repository.rowmapper;

import org.ksmart.birth.web.model.abandoned.AbandonedApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class AbandonedApplicationRowMapper implements ResultSetExtractor<List<AbandonedApplication>>, AbandonedBaseRowMapper, AbandonedMotherDetailRowMapper, AbandonedInformatRowMapper, AbandonedInitiatorDetailsRowMapper, AbandonedCareTakerRowMapper {

    @Override
    public List<AbandonedApplication> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<AbandonedApplication> result = new ArrayList<>();

        while (rs.next()) {
            result.add(AbandonedApplication.builder()
                    .id(rs.getString("ba_id"))
                    .dateOfReport(rs.getLong("ba_dateofreport"))
                    .dateOfBirth(rs.getLong("ba_dateofbirth"))
                    .timeOfBirth(rs.getLong("ba_timeofbirth"))
                    .gender(rs.getString("ba_gender"))
                //   .isChildName(isChildNameEntered(rs.getString("ba_firstname_en")))
               //     .firstNameEn(rs.getString("ba_firstname_en"))
//                    .firstNameMl(rs.getString("ba_firstname_ml"))
//                    .middleNameEn(rs.getString("ba_middlename_en"))
//                    .middleNameMl(rs.getString("ba_middlename_ml"))
//                    .lastNameEn(rs.getString("ba_lastname_en"))
//                    .lastNameMl(rs.getString("ba_lastname_ml"))
                    .placeofBirthId(rs.getString("pla_placeofbirthid"))
                    .institutionTypeId(rs.getString("pla_institution_type_id"))
                    .institutionNameCode(rs.getString("pla_institution_id"))
                    .hospitalId(rs.getString("pla_hospitalid"))
                    .wardId(rs.getString("pla_ward_id"))
                    .adrsPincode(rs.getString("pla_ho_pinno"))
                    .adrsPostOffice(rs.getString("pla_ho_poid"))
                    .adrsHouseNameEn(rs.getString("pla_ho_housename_en"))
                    .adrsHouseNameMl(rs.getString("pla_ho_housename_ml"))
                    .adrsLocalityNameEn(rs.getString("pla_ho_locality_en"))
                    .adrsLocalityNameMl(rs.getString("pla_ho_locality_ml"))
                    .adrsStreetNameEn(rs.getString("pla_ho_street_name_en"))
                    .adrsStreetNameMl(rs.getString("pla_ho_street_name_ml"))
                    .vehicleHaltplace(rs.getString("pla_vehicle_haltplace_en"))
                    .vehicleHaltPlaceMl(rs.getString("pla_vehicle_haltplace_en"))
                    .vehicleFromEn(rs.getString("pla_vehicle_from_en"))
                    .vehicleFromMl(rs.getString("pla_vehicle_from_ml"))
                    .vehicleTypeid(rs.getString("pla_vehicletypeid"))
                    .vehicleDesDetailsEn(rs.getString("pla_vehicle_desc"))
                    .setadmittedHospitalEn(rs.getString("pla_vehicle_hospitalid"))
                    .vehicleToEn(rs.getString("pla_vehicle_to_en"))
                    .vehicleToMl(rs.getString("pla_vehicle_to_ml"))
                    .vehicleRegistrationNo(rs.getString("pla_vehicle_registration_no"))
                    .vehicleDesDetailsEn(rs.getString("pla_vehicle_desc"))
                    .setadmittedHospitalEn(rs.getString("pla_vehicle_hospitalid"))
                    .publicPlaceDecpEn(rs.getString("pla_public_place_desc"))
                    .publicPlaceType(rs.getString("pla_public_place_id"))
                    .localityNameEn(rs.getString("pla_public_locality_en"))
                    .localityNameMl(rs.getString("pla_public_locality_ml"))
                    .streetNameEn(rs.getString("pla_public_street_name_en"))
                    .streetNameMl(rs.getString("pla_public_street_name_ml"))
                    .birthWeight(rs.getDouble("stat_weight_of_child"))
                    .pregnancyDuration(rs.getInt("stat_duration_of_pregnancy_in_week"))
                    .medicalAttensionSub(rs.getString("stat_nature_of_medical_attention"))
                    .deliveryMethods(rs.getString("stat_delivery_method"))
                    .esignUserDesigCode(rs.getString("ba_esign_user_desig_code"))
                    .tenantId(rs.getString("ba_tenantid"))
                    .applicationType(rs.getString("ba_applicationtype"))
                    .businessService(rs.getString("ba_businessservice"))
                    .workFlowCode(rs.getString("ba_workflowcode"))
                    .registrationNo(rs.getString("ba_registrationno"))
                    .ampm(rs.getString("ba_am_pm"))
                    .remarksEn(rs.getString("ba_remarks_en"))
                    .remarksMl(rs.getString("ba_aadharno"))
                    .applicationNo(rs.getString("ba_applicationno"))
                    .applicationType(rs.getString("ba_applicationtype"))
                    .action(rs.getString("ba_action"))
                    .applicationStatus(rs.getString("ba_status"))
                    .auditDetails(getAuditDetails(rs))
                    .parentsDetails(KsmartBirthParentDetail(rs))
                    .birthStatisticsUuid(rs.getString("stat_id"))
                    .birthPlaceUuid(rs.getString("pla_id"))
                    .fileNumber(rs.getString("ba_fm_fileno"))
                    .fileDate(rs.getLong("ba_file_date"))
                    .fileStatus(rs.getString("ba_file_status"))
                    .informatDetail(getInformatDetail(rs))
                    //.initiatorDetails(getInitiatorDetail(rs))
                    .caretakerDetails(getCareTakerDetail(rs))
                    .build());
        }
        return result;
    }
    private Boolean isChildNameEntered(String name) {
        if(name.isEmpty()) return true;
        if (name==null) return true;
        else return false;
    }
}


