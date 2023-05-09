package org.ksmart.birth.bornoutside.repository.rowmapper;

import org.jsoup.internal.StringUtil;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class BornOutsideApplicationRowMapper implements ResultSetExtractor<List<BornOutsideApplication>>, BornOutsideBaseRowMapper,
        BornOutsideParentDetailRowMapper, BornOutsideParentAddressRowMapper,
        BornOutsideStatRowMapper {

    @Override
    public List<BornOutsideApplication> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<BornOutsideApplication> result = new ArrayList<>();

        while (rs.next()) {
            result.add(BornOutsideApplication.builder()
                    .id(rs.getString("ba_id"))
                    .dateOfReport(rs.getLong("ba_dateofreport"))
                    .dateOfBirth(rs.getLong("ba_dateofbirth"))
                    .timeOfBirth(rs.getLong("ba_timeofbirth"))
                    .ampm(rs.getString("ba_am_pm"))
                    .isChildName(isChildNameEntered(rs.getString("ba_firstname_en")))
                    .firstNameEn(rs.getString("ba_firstname_en"))
                    .firstNameMl(rs.getString("ba_firstname_ml"))
                    .middleNameEn(rs.getString("ba_middlename_en"))
                    .middleNameMl(rs.getString("ba_middlename_ml"))
                    .lastNameEn(rs.getString("ba_lastname_en"))
                    .lastNameMl(rs.getString("ba_lastname_ml"))
                    .tenantId(rs.getString("ba_tenantid"))
                    .gender(rs.getString("ba_gender"))
                    .remarksEn(rs.getString("ba_remarks_en"))
                    .remarksMl(rs.getString("ba_remarks_ml"))
                    .aadharNo(rs.getString("ba_aadharno"))
                    .esignUserCode(rs.getString("ba_esign_user_code"))
                    .esignUserDesigCode(rs.getString("ba_esign_user_desig_code"))
                    .childPassportNo(rs.getString("ba_ot_passportno"))
                    .childArrivalDate(rs.getLong("ba_ot_dateofarrival"))
                    .applicationType(rs.getString("ba_applicationtype"))
                    .businessService(rs.getString("ba_businessservice"))
                    .workFlowCode(rs.getString("ba_workflowcode"))
                    .fileNumber(rs.getString("ba_fm_fileno"))
                    .fileDate(rs.getLong("ba_file_date"))
                    .fileStatus(rs.getString("ba_file_status"))
                    .applicationNo(rs.getString("ba_applicationno"))
                    .action(rs.getString("ba_action"))
                    .applicationStatus(rs.getString("ba_status"))
                    .placeofBirthId(rs.getString("pla_placeofbirthid"))
                    .wardId(rs.getString("pla_ward_id"))
                    .outsideBirthPlaceEn(rs.getString("pla_ot_birth_place_en"))
                    .outsideBirthPlaceMl(rs.getString("pla_ot_birth_place_ml"))
                    .provinceEn(rs.getString("pla_ot_state_region_province_en"))
                    .provinceMl(rs.getString("pla_ot_state_region_province_ml"))
                    .postCode(rs.getString("pla_ot_zipcode"))
                    .cityTown(rs.getString("pla_ot_town_village_en"))
                    .cityTownMl(rs.getString("pla_ot_town_village_ml"))
                    .country(rs.getString("pla_ot_country"))
                    .isBornOutside(rs.getBoolean("pla_is_born_outside"))
                    .parentsDetails(getKsmartBirthParentDetail(rs))
                    .parentAddress(getKsmartBirthParentAddress(rs))
                    .bornOutsideStaticInformant(getStatInformation(rs))
                    .auditDetails(getAuditDetails(rs))
                    .build());
        }
        return result;
    }
    private Boolean isChildNameEntered(String name) {
        if(StringUtil.isBlank(name)) return true;
        if (name==null) return true;
        else return false;
    }
}


