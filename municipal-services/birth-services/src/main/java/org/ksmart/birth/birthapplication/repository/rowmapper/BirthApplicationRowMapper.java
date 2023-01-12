package org.ksmart.birth.birthapplication.repository.rowmapper;

import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class BirthApplicationRowMapper implements ResultSetExtractor<List<BirthApplicationDetail>>, BaseRowMapper, BirthPlaceRowMapper,
                                                    BirthFatherInfoRowMapper, BirthMotherInfoRowMapper, BirthPermanentAddressRowMapper,
                                                    BirthPresentAddressRowMapper,BirthStatInfoRowMapper  {

    @Override
    public List<BirthApplicationDetail> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<BirthApplicationDetail> result = new ArrayList<>();
        while (rs.next()) {
            result.add(BirthApplicationDetail.builder()
                    .id(rs.getString("ba_id"))
                    .dateOfReport(rs.getLong("ba_dateofreport"))
                    .dateOfBirth(rs.getLong("ba_dateofbirth"))
                    .timeOfBirth(rs.getLong("ba_timeofbirth"))
                    .ampm(rs.getString("ba_am_pm"))
                    .firstNameEn(rs.getString("ba_firstname_en"))
                    .firstNameMl(rs.getString("ba_firstname_ml"))
                    .middleNameEn(rs.getString("ba_middlename_en"))
                    .middleNameMl(rs.getString("ba_middlename_ml"))
                    .lastNameEn(rs.getString("ba_lastname_en"))
                    .lastNameMl(rs.getString("ba_lastname_ml"))
                    .tenantId(rs.getString("ba_tenantid"))
                    .gender(rs.getString("ba_gender"))
                    .remarksEn(rs.getString("ba_remarks_en"))
                    .remarksMl(rs.getString("ba_aadharno"))
                    .esignUserCode(rs.getString("ba_esign_user_code"))
                    .esignUserDesigCode(rs.getString("ba_esign_user_desig_code"))
                    .isAdopted(Boolean.valueOf(rs.getString("ba_is_adopted")))
                    .isAbandoned(Boolean.valueOf(rs.getString("ba_is_abandoned")))
                    .isMultipleBirth(Boolean.valueOf(rs.getString("ba_is_multiple_birth")))
                    .isFatherInfoMissing(Boolean.valueOf(rs.getString("ba_is_father_info_missing")))
                    .isMotherInfoMissing(Boolean.valueOf(rs.getString("ba_is_mother_info_missing")))
                    .noOfAliveBirth(rs.getInt("ba_no_of_alive_birth"))
                    .multipleBirthDetailsIid(rs.getString("ba_multiplebirthdetid"))
                    .isBornOutside(rs.getBoolean("ba_is_born_outside"))
                    .passportNo(rs.getString("ba_ot_passportno"))
                    .dateOfArrival(rs.getLong("ba_ot_dateofarrival"))
                    .applicationType(rs.getString("ba_applicationtype"))
                    .businessService(rs.getString("ba_businessservice"))
                    .workFlowCode(rs.getString("ba_workflowcode"))
                    .fmFileNo(rs.getString("ba_fm_fileno"))
                    .fileDate(rs.getLong("ba_file_date"))
                    .applicationNo(rs.getString("ba_applicationno"))
                    .registrationNo(rs.getString("ba_registrationno"))
                    .registrationDate(rs.getLong("ba_registration_date"))
                    .action(rs.getString("ba_action"))
                    .status(rs.getString("ba_status"))
                    .birthPlace(getBirthPlace(rs))
                    .birthFatherInfo(getBirthFatherInfo(rs))
                    .birthMotherInfo(getBirthMotherInfo(rs))
                    .birthPermanentAddress(getBirthPermanentAddress(rs))
                    .birthPresentAddress(getBirthPresentAddress(rs))
                    .birthStatisticalInformation(getBirthStatisticalInfo(rs))
                    .auditDetails(getAuditDetails(rs))
                    .build());
        }
        return result;
    }
}
