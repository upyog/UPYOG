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
public class BirthApplicationAdoptionRowMapper  implements ResultSetExtractor<List<NewBirthApplication>>, BaseRowMapper, BirthParentDetailRowMapper, InformatDetailsRowMapper, BirthParentAddressRowMapper, InitiatorDetailsRowMapper {

    @Override
    public List<NewBirthApplication> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<NewBirthApplication> result = new ArrayList<>();
        while (rs.next()) {
            result.add(NewBirthApplication.builder()
                    .id(rs.getString("id"))
                    .dateOfReport(rs.getLong("dateofreport"))
                    .dateOfBirth(rs.getLong("dateofbirth"))
                    .timeBirth(rs.getLong("timeofbirth"))
                    .gender(rs.getString("gender"))
                    .aadharNo(rs.getString("aadharno"))
                    .isChildName(isChildNameEntered(rs.getString("firstname_en")))
                    .firstNameEn(rs.getString("firstname_en"))
                    .firstNameMl(rs.getString("firstname_ml"))
                    .middleNameEn(rs.getString("middlename_en"))
                    .middleNameMl(rs.getString("middlename_ml"))
                    .lastNameEn(rs.getString("lastname_en"))
                    .lastNameMl(rs.getString("lastname_ml"))
                    .placeofBirthId(rs.getString("placeofbirthid"))
                    .institutionTypeId(rs.getString("institution_type_id"))
                    .institutionNameCode(rs.getString("institution_id"))
                    .hospitalId(rs.getString("hospitalid"))
                    .wardId(rs.getString("ward_id"))
                    .adrsPincode(rs.getString("ho_pinno"))
                    .adrsPostOffice(rs.getString("ho_poid"))
                    .adrsHouseNameEn(rs.getString("ho_housename_en"))
                    .adrsHouseNameMl(rs.getString("ho_housename_ml"))
                    .adrsLocalityNameEn(rs.getString("ho_locality_en"))
                    .adrsLocalityNameMl(rs.getString("ho_locality_ml"))
                    .adrsStreetNameEn(rs.getString("ho_street_name_en"))
                    .adrsStreetNameMl(rs.getString("ho_street_name_ml"))
                    .vehicleHaltplace(rs.getString("vehicle_haltplace_en"))
                    .vehicleHaltPlaceMl(rs.getString("vehicle_haltplace_ml"))
                    .vehicleFromEn(rs.getString("vehicle_from_en"))
                    .vehicleFromMl(rs.getString("vehicle_from_ml"))
                    .vehicleTypeid(rs.getString("vehicletypeid"))
                    .vehicleDesDetailsEn(rs.getString("vehicle_desc"))
                    .setadmittedHospitalEn(rs.getString("vehicle_hospitalid"))
                    .vehicleToEn(rs.getString("vehicle_to_en"))
                    .vehicleToMl(rs.getString("vehicle_to_ml"))
                    .vehicleRegistrationNo(rs.getString("vehicle_registration_no"))
                    .vehicleDesDetailsEn(rs.getString("vehicle_desc"))
                    .setadmittedHospitalEn(rs.getString("vehicle_hospitalid"))
                    .publicPlaceDecpEn(rs.getString("public_place_desc"))
                    .publicPlaceType(rs.getString("public_place_id"))
                    .localityNameEn(rs.getString("public_locality_en"))
                    .localityNameMl(rs.getString("public_locality_ml"))
                    .streetNameEn(rs.getString("public_street_name_en"))
                    .streetNameMl(rs.getString("public_street_name_ml"))
//                    .birthWeight(rs.getDouble("stat_weight_of_child"))
//                    .pregnancyDuration(rs.getInt("stat_duration_of_pregnancy_in_week"))
//                    .medicalAttensionSub(rs.getString("stat_nature_of_medical_attention"))
//                    .deliveryMethods(rs.getString("stat_delivery_method"))
                    .esignUserDesigCode(rs.getString("esign_user_desig_code"))
                    .tenantId(rs.getString("tenantid"))
                    .applicationType(rs.getString("applicationtype"))
//                    .businessService(rs.getString("ba_businessservice"))
//                    .workFlowCode(rs.getString("ba_workflowcode"))
                    .registrationNo(rs.getString("registrationno"))
                    .ampm(rs.getString("am_pm"))
                    .remarksEn(rs.getString("remarks_en"))
                    .remarksMl(rs.getString("aadharno"))
//                    .applicationNo(rs.getString("ba_applicationno"))
//                    .applicationType(rs.getString("ba_applicationtype"))
//                    .action(rs.getString("ba_action"))
//                    .applicationStatus(rs.getString("ba_status"))
                    .auditDetails(getAuditDetails(rs))
//                    .parentsDetails(KsmartBirthParentDetail(rs))
//                    .birthStatisticsUuid(rs.getString("stat_id"))
                    .birthPlaceUuid(rs.getString("pla_id"))
//                    .fileNumber(rs.getString("ba_fm_fileno"))
//                    .fileDate(rs.getLong("ba_file_date"))
//                    .fileStatus(rs.getString("ba_file_status"))
//                    .informatDetail(getInformatDetail(rs))
//                    .initiatorDetails(getInitiatorDetail(rs))
//                    .parentAddress(getKsmartBirthParentAddress(rs))
                    .build());
        }
        return result;
    }
    private Boolean isChildNameEntered(String name) {
        if (name==null) return true;
        else return false;
    }
}


