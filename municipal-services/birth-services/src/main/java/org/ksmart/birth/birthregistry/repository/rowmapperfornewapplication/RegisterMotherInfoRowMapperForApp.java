package org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication;

import org.ksmart.birth.birthregistry.model.RegisterBirthMotherInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RegisterMotherInfoRowMapperForApp {
    default RegisterBirthMotherInfo getRegBirthMotherInfo(ResultSet rs) throws SQLException {
        return RegisterBirthMotherInfo.builder()
                .firstNameEn(rs.getString("mo_firstname_en"))
                .firstNameMl(rs.getString("mo_firstname_ml"))
                .aadharNo(rs.getString("mo_aadharno"))
                .otPassportNo(rs.getString("mo_ot_passportno"))
                //.birthDtlId(rs.getString("mo_birthdtlid"))
                .bioAdopt(rs.getString("mo_bio_adopt"))
                .build();
    }
}
