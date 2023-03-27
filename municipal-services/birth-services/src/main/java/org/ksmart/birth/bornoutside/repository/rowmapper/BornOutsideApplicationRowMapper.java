package org.ksmart.birth.bornoutside.repository.rowmapper;

import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class BornOutsideApplicationRowMapper implements ResultSetExtractor<List<BornOutsideApplication>>, BornOutsideBaseRowMapper, BornOutsideParentDetailRowMapper, BornOutsideInformatDetailsRowMapper, BornOutsideParentAddressRowMapper {

    @Override
    public List<BornOutsideApplication> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<BornOutsideApplication> result = new ArrayList<>();

        while (rs.next()) {
            result.add(BornOutsideApplication.builder()
                    .id(rs.getString("ba_id"))
                    .dateOfReport(rs.getLong("ba_dateofreport"))
                    .dateOfBirth(rs.getLong("ba_dateofbirth"))
                    .timeOfBirth(rs.getLong("ba_timeofbirth"))
                    .gender(rs.getString("ba_gender"))
                    .aadharNo(rs.getString("ba_aadharno"))
                    .isChildName(isChildNameEntered(rs.getString("ba_firstname_en")))
                    .firstNameEn(rs.getString("ba_firstname_en"))
                    .firstNameMl(rs.getString("ba_firstname_ml"))
                    .middleNameEn(rs.getString("ba_middlename_en"))
                    .middleNameMl(rs.getString("ba_middlename_ml"))
                    .lastNameEn(rs.getString("ba_lastname_en"))
                    .lastNameMl(rs.getString("ba_lastname_ml"))
                    .placeofBirthId(rs.getString("pla_placeofbirthid"))
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
                    .parentAddress(getKsmartBirthParentAddress(rs))
                    .build());
        }
        return result;
    }

    private Boolean isChildNameEntered(String name) {
        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else return false;
    }
}


