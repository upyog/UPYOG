package org.ksmart.birth.ksmartbirthapplication.repository.rowmapper;

import org.ksmart.birth.birthapplication.model.birth.BirthFatherInfo;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthParentDetail;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface KsmartBirthParentDetailRowMapper {
    default KsmartBirthParentDetail KsmartBirthParentDetail(ResultSet rs) throws SQLException {
        return KsmartBirthParentDetail.builder()
                .fatherUuid(rs.getString("fa_id"))
                .fatherFirstNameEn(rs.getString("fa_firstname_en"))
                .fatherFirstNameMl(rs.getString("fa_firstname_ml"))
                .fatherAadharno(rs.getString("fa_aadharno"))
                .fatherPassport(rs.getString("fa_ot_passportno"))
                .fatherEucationid(rs.getString("fa_fatherEucationid"))
                .fatherNationalityid(rs.getString("fa_fatherNationalityid"))
                .fatherProffessionid(rs.getString("fa_fatherProffessionid"))
                .familyEmailid(rs.getString("fa_familyEmailid"))
                .familyMobileNo(rs.getString("fa_familyMobileNo"))
                .isFatherInfoMissing(Boolean.valueOf(rs.getString("fa_isFatherInfoMissing")))
                .motherAadhar(rs.getString("mo_motherAadhar"))
                .motherEducationid(rs.getString("mo_motherEducationid"))
                .motherUuid(rs.getString("mo_motherUuid"))
                .motherAgeDelivery(rs.getInt("mo_motherAgeDelivery"))
                .motherAgeMarriage(rs.getInt("mo_motherAgeMarriage"))
                .motherNationalityid(rs.getString("mo_motherNationalityid"))
                .motherPassport(rs.getString("motherPassport"))
                .motherProffessionid(rs.getString("motherProffessionid"))
                .motherOrderCurChild(rs.getInt("motherOrderCurChild"))
                .firstNameEn(rs.getString("firstNameEn"))
                .firstNameMl(rs.getString("firstNameMl"))
                .isMotherInfoMissing(Boolean.valueOf(rs.getString("fa_isMotherInfoMissing")))
                .religionId(rs.getString("religionId"))
                .build();
    }

}
