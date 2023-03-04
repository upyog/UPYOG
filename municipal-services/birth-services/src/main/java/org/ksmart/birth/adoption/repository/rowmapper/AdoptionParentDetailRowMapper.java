package org.ksmart.birth.adoption.repository.rowmapper;

import org.ksmart.birth.web.model.ParentsDetail;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface AdoptionParentDetailRowMapper {
    default ParentsDetail KsmartBirthParentDetail(ResultSet rs) throws SQLException {
        return ParentsDetail.builder()
                .fatherUuid(rs.getString("fa_id"))
                .fatherFirstNameEn(rs.getString("fa_firstname_en"))
                .fatherFirstNameMl(rs.getString("fa_firstname_ml"))
                .fatherAadharno(rs.getString("fa_aadharno"))
                .fatherPassport(rs.getString("fa_ot_passportno"))
                .fatherProffessionid(rs.getString("stat_father_proffessionid"))
                .familyEmailid(rs.getString("fa_familyEmailid"))
                .familyMobileNo(rs.getString("fa_familyMobileNo"))
                .isFatherInfoMissing(Boolean.valueOf(rs.getString("ba_is_father_info_missing")))
                .motherAadhar(rs.getString("mo_aadharno"))
                .motherEducationid(rs.getString("stat_mother_educationid"))
                .motherAgeDelivery(rs.getInt("stat_mother_age_delivery"))
                .motherAgeMarriage(rs.getInt("stat_mother_age_marriage"))
                .motherNationalityid(rs.getString("stat_mother_nationalityid"))
                .motherPassport(rs.getString("mo_ot_passportno"))
                .motherProffessionid(rs.getString("stat_mother_proffessionid"))
                .firstNameEn(rs.getString("mo_firstname_en"))
                .firstNameMl(rs.getString("mo_firstname_ml"))
                .isMotherInfoMissing(Boolean.valueOf(rs.getString("ba_is_mother_info_missing")))
                .religionId(rs.getString("stat_religionid"))
                .build();
    }

}
