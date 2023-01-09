package org.ksmart.birth.birthapplication.repository.rowmapper;

import org.ksmart.birth.birthapplication.model.birth.BirthMotherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthMotherInfoRowMapper {
    default BirthMotherInfo getBirthMotherInfo(ResultSet rs) throws SQLException {
        return BirthMotherInfo.builder()
                .id(rs.getString("mo_id"))
                .firstNameEn(rs.getString("mo_firstname_en"))
                .firstNameMl(rs.getString("mo_firstname_ml"))
                .middleNameEn(rs.getString("mo_middlename_en"))
                .middleNameMl(rs.getString("mo_middlename_ml"))
                .lastNameEn(rs.getString("mo_lastname_en"))
                .lastNameMl(rs.getString("mo_lastname_ml"))
                .aadharNo(rs.getString("mo_aadharno"))
                .passportNo(rs.getString("mo_ot_passportno"))
                .emailId(rs.getString("mo_emailid"))
                .mobileNo(rs.getString("mo_mobileno"))
                .birthDtlId(rs.getString("mo_birthdtlid"))
                .build();
    }
}
