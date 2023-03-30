package org.ksmart.birth.birthregistry.repository.rowmapperforcorrectionapplication;

import org.ksmart.birth.birthregistry.model.RegisterBirthFatherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RegisterFatherInfoRowMapperForCorr {
    default RegisterBirthFatherInfo getRegBirthFatherInfo(ResultSet rs) throws SQLException {
        return RegisterBirthFatherInfo.builder()
                .firstNameEn(rs.getString("fa_firstname_en"))
                .firstNameMl(rs.getString("fa_firstname_ml"))
                .aadharNo(rs.getString("fa_aadharno"))
                .birthDtlId(rs.getString("fa_birthdtlid"))
                .build();
    }
}
