package org.ksmart.birth.ksmartbirthapplication.repository.rowmapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
import org.ksmart.birth.birthapplication.repository.rowmapper.*;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.model.Document;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class KsmartBirthApplicationRowMapper implements ResultSetExtractor<List<KsmartBirthAppliactionDetail>>, KsmartBaseRowMapper,KsmartBirthParentDetailRowMapper,KsmartInformatDetailsRowMapper,KsmartInitiatorDetailsRowMapper{

    @Override
    public List<KsmartBirthAppliactionDetail> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<KsmartBirthAppliactionDetail> result = new ArrayList<>();

        while (rs.next()) {
            result.add(KsmartBirthAppliactionDetail.builder()
                    .id(rs.getString("ba_id"))
                    .dateOfReport(rs.getLong("ba_dateofreport"))
                    .dateOfBirth(rs.getLong("ba_dateofbirth"))
                    .timeOfBirth(rs.getLong("ba_timeofbirth"))
                    .gender(rs.getString("ba_gender"))
                    .aadharNo(rs.getString("ba_aadharno"))
                    .isChildName(Boolean.valueOf(rs.getString("ba_is_childname")))
                    .firstNameEn(rs.getString("ba_firstname_en"))
                    .firstNameMl(rs.getString("ba_firstname_ml"))
                    .middleNameEn(rs.getString("ba_middlename_en"))
                    .middleNameMl(rs.getString("ba_middlename_ml"))
                    .lastNameEn(rs.getString("ba_lastname_en"))
                    .lastNameMl(rs.getString("ba_lastname_ml"))
                    .placeofBirthId(rs.getString("placeofbirthid"))
                    .hospitalId(rs.getString("ba_hospitalId"))
                    .hospitalName(rs.getString("ba_hospitalname"))
                    .hospitalNameMl(rs.getString("ba_hospitalnameml"))
                    .institution(rs.getString("ba_institution"))
                    .institutionId(rs.getString("ba_institutionid"))
                    .institutionIdMl(rs.getString("ba_institutiontype"))
                    .institutionNameCode(rs.getString("ba_institutionnamecode"))
                    .wardId(rs.getString("ba_wardid"))
                    .wardNumber(rs.getString("ba_wardnumber"))
                    .wardNameEn(rs.getString("ba_wardnamen"))
                    .wardNameMl(rs.getString("ba_wardnameml"))
                    .adrsPincode(rs.getString("ba_adrsPincode"))
                    .adrsHouseNameEn(rs.getString("ba_adrsHouseNameEn"))
                    .adrsHouseNameMl(rs.getString("ba_adrsHouseNameMl"))
                    .adrsPostOffice(rs.getString("ba_adrsPostOffice"))
                    .adrsLocalityNameEn(rs.getString("ba_adrsLocalityNameEn"))
                    .adrsLocalityNameMl(rs.getString("ba_adrsLocalityNameMl"))
                    .adrsStreetNameEn(rs.getString("ba_adrsStreetNameEn"))
                    .adrsStreetNameMl(rs.getString("ba_adrsStreetNameMl"))
                    .vehicleHaltplace(rs.getString("ba_vehicleHaltplace"))
                    .vehicleFromMl(rs.getString("ba_vehicleFromMl"))
                    .vehicleTypeid(rs.getString("ba_vehicleTypeid"))
                    .vehicleFromEn(rs.getString("ba_vehicleFromEn"))
                    .vehicleDesDetailsEn(rs.getString("ba_vehicleDesDetailsEn"))
                    .vehicleToEn(rs.getString("ba_vehicleToEn"))
                    .vehicleToMl(rs.getString("ba_vehicleToMl"))
                    .vehicleRegistrationNo(rs.getString("ba_vehicleRegistrationNo"))
                    .vehicleDesDetailsEn(rs.getString("ba_vehicleDesDetailsEn"))
                    .setadmittedHospitalEn(rs.getString("ba_setadmittedHospitalEn"))
                    .publicPlaceDecpEn(rs.getString("ba_publicPlaceDecpEn"))
                    .publicPlaceType(rs.getString("publicPlaceType"))
                    .localityNameEn(rs.getString("ba_localityNameEn"))
                    .localityNameMl(rs.getString("ba_localityNameMl"))
                    .streetNameEn(rs.getString("ba_streetNameEn"))
                    .streetNameMl(rs.getString("ba_streetNameMl"))
                    .birthWeight(rs.getDouble("ba_birthWeight"))
                    .pregnancyDuration(rs.getInt("ba_pregnancyDuration"))
                    .medicalAttensionSub(rs.getString("ba_medicalAttensionSub"))
                    .deliveryMethods(rs.getString("ba_deliveryMethods"))
                    .esignUserCode(rs.getString("ba_esign_user_code"))
                    .esignUserDesigCode(rs.getString("ba_esign_user_desig_code"))
                    .tenantId(rs.getString("ba_tenantid"))
                    .applicationType(rs.getString("ba_applicationtype"))
                    .businessService(rs.getString("ba_businessservice"))
                    .workFlowCode(rs.getString("ba_workflowcode"))
                    .fileNumber(rs.getString("ba_fileNumber"))
                    .fileDate(rs.getLong("ba_file_date"))
                    .applicationNo(rs.getString("ba_applicationno"))
                    .registrationNo(rs.getString("ba_registrationno"))
                    .registrationDate(rs.getLong("ba_registration_date"))
                    .action(rs.getString("ba_action"))
                    .status(rs.getString("ba_status"))
                    .fmFileNo(rs.getString("ba_fm_fileno"))
                    .ampm(rs.getString("ba_am_pm"))
                    .remarksEn(rs.getString("ba_remarks_en"))
                    .remarksMl(rs.getString("ba_aadharno"))
                    .birthInitiatorUuid(rs.getString("ba_birthInitiatorUuid"))
                    .birthPlaceUuid(rs.getString("ba_birthPlaceUuid"))
                    .birthStatisticsUuid(rs.getString("ba_birthStatisticsUuid"))
                    .addressUuid(rs.getString("ba_addressUuid"))
                    .comment(rs.getString("ba_comment"))
                   // .isAdopted(Boolean.valueOf(rs.getString("ba_is_adopted")))
                  //  .isAbandoned(Boolean.valueOf(rs.getString("ba_is_abandoned")))
                  //  .isMultipleBirth(Boolean.valueOf(rs.getString("ba_is_multiple_birth")))
                  //  .isFatherInfoMissing(Boolean.valueOf(rs.getString("ba_is_father_info_missing")))
                   // .isMotherInfoMissing(Boolean.valueOf(rs.getString("ba_is_mother_info_missing")))
                   // .noOfAliveBirth(rs.getInt("ba_no_of_alive_birth"))
                  //  .multipleBirthDetailsIid(rs.getString("ba_multiplebirthdetid"))
                  //  .isBornOutside(rs.getBoolean("ba_is_born_outside"))
                 //   .passportNo(rs.getString("ba_ot_passportno"))
                  //  .dateOfArrival(rs.getLong("ba_ot_dateofarrival"))

                    .auditDetails(getAuditDetails(rs))
                    .parentsDetails(KsmartBirthParentDetail(rs))
                    .informatDetail(getKsmartInformatDetail(rs))
                    .initiatorDetails(getKsmartInitiatorDetail(rs))
                    .build());
        }
        return result;
    }
}


