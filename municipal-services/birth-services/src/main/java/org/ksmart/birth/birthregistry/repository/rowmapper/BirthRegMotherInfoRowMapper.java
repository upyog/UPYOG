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
                .middleNameEn(rs.getString("mother_mn"))
                .middleNameMl(rs.getString("mother_mn_ml"))
                .lastNameEn(rs.getString("mother_ln"))
                .lastNameMl(rs.getString("mother_ln_ml"))
                .aadharNo(rs.getString("mo_aadh"))
                .otPassportNo(rs.getString("mo_pass"))
                .emailId(rs.getString("mo_email"))
                .mobileNo(rs.getString("mo_mob"))
                .birthDtlId(rs.getString("birthdtlid"))
                .motherFullName(new StringBuilder().append(rs.getString("mother_fn")==null?"":rs.getString("mother_fn"))
                                                    .append(" ")
                                                    .append(rs.getString("mother_mn")==null?"":rs.getString("mother_mn"))
                                                    .append(" ")
                                                    .append(rs.getString("mother_ln")==null?"":rs.getString("mother_ln")).toString())
                .motherFullNameMl(new StringBuilder().append(rs.getString("mother_fn_ml")==null?"":rs.getString("mother_fn_ml"))
                                                    .append(" ")
                                                    .append(rs.getString("mother_mn_ml")==null?"":rs.getString("mother_mn_ml"))
                                                    .append(" ")
                                                    .append(rs.getString("mother_ln_ml")==null?"":rs.getString("mother_ln_ml")).toString())


                .build();
    }
}
