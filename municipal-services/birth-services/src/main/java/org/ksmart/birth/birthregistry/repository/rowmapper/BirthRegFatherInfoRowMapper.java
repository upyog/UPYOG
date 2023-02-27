package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthFatherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthRegFatherInfoRowMapper {
    default RegisterBirthFatherInfo getRegBirthFatherInfo(ResultSet rs) throws SQLException {
        return RegisterBirthFatherInfo.builder()
                .id(rs.getString("id"))
                .firstNameEn(rs.getString("father_fn"))
                .firstNameMl(rs.getString("father_fn_ml"))
                .aadharNo(rs.getString("fa_aadh"))
                .otPassportNo(rs.getString("fa_pass"))
                .birthDtlId(rs.getString("birthdtlid"))
                .build();
    }
}
