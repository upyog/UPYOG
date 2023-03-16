package org.ksmart.birth.birthregistry.repository.rowmapperforapplication;

import org.ksmart.birth.birthregistry.model.RegisterBirthFatherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RegisterFatherInfoRowMapperForApp {
    default RegisterBirthFatherInfo getRegBirthFatherInfo(ResultSet rs) throws SQLException {
        return RegisterBirthFatherInfo.builder()
                .firstNameEn(rs.getString("fa_firstname_en"))
                .firstNameMl(rs.getString("fa_firstname_ml"))
                .aadharNo(rs.getString("fa_aadharno"))
                //.otPassportNo(rs.getString("fa_fa_pass"))
                .birthDtlId(rs.getString("fa_birthdtlid"))
                .bioAdopt(rs.getString("fa_bio_adopt"))
                .build();
    }
}
