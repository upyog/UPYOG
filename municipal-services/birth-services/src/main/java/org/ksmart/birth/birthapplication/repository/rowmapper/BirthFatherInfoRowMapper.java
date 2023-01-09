package org.ksmart.birth.birthapplication.repository.rowmapper;

import org.ksmart.birth.birthapplication.model.birth.BirthFatherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthFatherInfoRowMapper {
    default BirthFatherInfo getBirthFatherInfo(ResultSet rs) throws SQLException {
        return BirthFatherInfo.builder()
                .id(rs.getString("fa_id"))
                .firstNameEn(rs.getString("fa_firstname_en"))
                .firstNameMl(rs.getString("fa_firstname_ml"))
                .middleNameEn(rs.getString("fa_middlename_en"))
                .middleNameMl(rs.getString("fa_middlename_ml"))
                .lastNameEn(rs.getString("fa_lastname_en"))
                .lastNameMl(rs.getString("fa_lastname_ml"))
                .aadharNo(rs.getString("fa_aadharno"))
                .passportNo(rs.getString("fa_ot_passportno"))
                .emailId(rs.getString("fa_emailid"))
                .mobileNo(rs.getString("fa_mobileno"))
                .birthDtlId(rs.getString("fa_birthdtlid"))
                .build();
    }

}
