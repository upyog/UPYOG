package org.ksmart.birth.birthregistry.repository.rowmapperforcorrectionapplication;

import org.ksmart.birth.birthregistry.model.RegisterBirthMotherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RegisterMotherInfoRowMapperForCorr {
    default RegisterBirthMotherInfo getRegBirthMotherInfo(ResultSet rs) throws SQLException {
        return RegisterBirthMotherInfo.builder()
                .firstNameEn(rs.getString("mo_firstname_en"))
                .firstNameMl(rs.getString("mo_firstname_ml"))
                .aadharNo(rs.getString("mo_aadharno"))
                .birthDtlId(rs.getString("mo_birthdtlid"))
                .build();
    }
}
