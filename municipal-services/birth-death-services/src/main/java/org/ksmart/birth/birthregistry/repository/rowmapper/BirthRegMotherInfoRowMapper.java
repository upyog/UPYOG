package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthMotherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthRegMotherInfoRowMapper {
    default RegisterBirthMotherInfo getRegBirthMotherInfo(ResultSet rs) throws SQLException {
        return RegisterBirthMotherInfo.builder()
                .id(rs.getString("id"))
                .firstNameEn(rs.getString("firstname_en"))
                .firstNameMl(rs.getString("firstname_ml"))
                .middleNameEn(rs.getString("middlename_en"))
                .middleNameMl(rs.getString("middlename_ml"))
                .lastNameEn(rs.getString("lastname_en"))
                .lastNameMl(rs.getString("lastname_ml"))
                .aadharNo(rs.getString("aadharno"))
                .otPassportNo(rs.getString("ot_passportno"))
                .emailId(rs.getString("emailid"))
                .mobileNo(rs.getString("mobileno"))
                .birthDtlId(rs.getString("birthdtlid"))
                .build();
    }
}
