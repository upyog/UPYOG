package org.ksmart.birth.stillbirth.repository.rowmapper;

import org.ksmart.birth.web.model.ParentsDetail;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface StillBirthParentDetailRowMapper {
    default ParentsDetail KsmartBirthParentDetail(ResultSet rs) throws SQLException {
        return ParentsDetail.builder()
                .fatherUuid(rs.getString("fa_id"))
                .fatherFirstNameEn(rs.getString("fa_firstname_en"))
                .fatherFirstNameMl(rs.getString("fa_firstname_ml"))
                .fatherAadharno(rs.getString("fa_aadharno"))
                .fatherProffessionid(rs.getString("stat_father_proffessionid"))
                .fatherEucationid(rs.getString("stat_father_educationid"))
                .fatherNationalityid(rs.getString("stat_father_nationalityid"))
                .isFatherInfoMissing(Boolean.valueOf(rs.getString("ba_is_father_info_missing")))
                .motherAadhar(rs.getString("mo_aadharno"))
                .motherEducationid(rs.getString("stat_mother_educationid"))
                .motherAgeDelivery(rs.getInt("stat_mother_age_delivery"))
                .motherAgeMarriage(rs.getInt("stat_mother_age_marriage"))
                .motherNationalityid(rs.getString("stat_mother_nationalityid"))
                .motherProffessionid(rs.getString("stat_mother_proffessionid"))
                .firstNameEn(rs.getString("mo_firstname_en"))
                .firstNameMl(rs.getString("mo_firstname_ml"))
                .isMotherInfoMissing(Boolean.valueOf(rs.getString("ba_is_mother_info_missing")))
                .religionId(rs.getString("stat_religionid"))
                .motherOrderCurChild(rs.getInt("stat_mother_order_cur_child"))
                .motherMaritalStatus(rs.getString("stat_mother_maritalstatusid"))
                .familyMobileNo(rs.getString("per_family_mobileno"))
                .familyEmailid(rs.getString("per_family_emailid"))
                .isFatherInfoMissing(rs.getBoolean("ba_is_father_info_missing"))
                .isMotherInfoMissing(rs.getBoolean("ba_is_mother_info_missing"))
                .build();
    }


}
