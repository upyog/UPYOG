package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthMotherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BirthRegMotherInfoRowMapper {
    default RegisterBirthMotherInfo getRegBirthMotherInfo(ResultSet rs) throws SQLException {
        return RegisterBirthMotherInfo.builder()
                .id(rs.getString("id"))
                .firstNameEn(rs.getString("mother_fn"))
                .firstNameMl(rs.getString("mother_fn_ml"))
                .aadharNo(rs.getString("mo_aadh"))
                .otPassportNo(rs.getString("mo_pass"))
                .birthDtlId(rs.getString("birthdtlid"))
                .build();
    }
}
