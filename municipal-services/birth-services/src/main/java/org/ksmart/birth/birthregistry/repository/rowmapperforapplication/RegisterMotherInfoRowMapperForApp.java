package org.ksmart.birth.birthregistry.repository.rowmapperforapplication;

import org.ksmart.birth.birthregistry.model.RegisterBirthMotherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RegisterMotherInfoRowMapperForApp {
    default RegisterBirthMotherInfo getRegBirthMotherInfo(ResultSet rs) throws SQLException {
        return RegisterBirthMotherInfo.builder()
                .firstNameEn(rs.getString("mo_mother_fn"))
                .firstNameMl(rs.getString("mo_mother_fn_ml"))
                .aadharNo(rs.getString("mo_aadharno"))
                .otPassportNo(rs.getString("mo_pass"))
                .birthDtlId(rs.getString("mo_birthdtlid"))
                .build();
    }
}
