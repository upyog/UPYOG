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
                .middleNameEn(rs.getString("father_mn"))
                .middleNameMl(rs.getString("father_mn_ml"))
                .lastNameEn(rs.getString("father_ln"))
                .lastNameMl(rs.getString("father_fn_ml"))
                .aadharNo(rs.getString("fa_aadh"))
                .otPassportNo(rs.getString("fa_pass"))
                .emailId(rs.getString("fa_email"))
                .mobileNo(rs.getString("fa_mob"))
                .birthDtlId(rs.getString("birthdtlid"))
                .fullName(rs.getString("father_fn")+" "+rs.getString("father_mn")+" "+rs.getString("father_ln"))
                .fullNameMl(rs.getString("father_fn_ml")+" "+rs.getString("father_mn_ml")+" "+rs.getString("father_fn_ml"))

                .build();
    }
}
