package org.ksmart.birth.birthregistry.repository.rowmapperforapplication;

import org.ksmart.birth.birthregistry.model.RegisterBirthFatherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RegisterFatherInfoRowMapperForApp {
    default RegisterBirthFatherInfo getRegBirthFatherInfo(ResultSet rs) throws SQLException {
        return RegisterBirthFatherInfo.builder()
                .firstNameEn(rs.getString("fa_father_fn"))
                .firstNameMl(rs.getString("fa_father_fn_ml"))
                .aadharNo(rs.getString("fa_aadharno"))
                //.otPassportNo(rs.getString("fa_fa_pass"))
                .birthDtlId(rs.getString("fa_birthdtlid"))
                .build();
    }
}
