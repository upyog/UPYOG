package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class BirthRegisterRowMapper implements ResultSetExtractor<List<RegisterBirthDetail>>,BaseRegRowMapper,BirthRegPlaceRowMapper,
    BirthRegFatherInfoRowMapper, BirthRegMotherInfoRowMapper, BirthRegPerAddRowMapper, BirthRegPreAddRowMapper,BirthRegStatInfoRowMapper {
        @Override
        public List<RegisterBirthDetail> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
            List<RegisterBirthDetail> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RegisterBirthDetail.builder()
                        .id(rs.getString("id"))
                        .dateOfReport(rs.getLong("dateofreport"))
                        .dateOfBirth(rs.getLong("dateofbirth"))
                        .timeOfBirth(rs.getLong("timeofbirth"))
                        .ampm(rs.getString("am_pm"))
                        .firstNameEn(rs.getString("firstname_en"))
                        .firstNameMl(rs.getString("firstname_ml"))
                        .middleNameEn(rs.getString("middlename_en"))
                        .middleNameMl(rs.getString("middlename_ml"))
                        .lastNameEn(rs.getString("lastname_en"))
                        .lastNameMl(rs.getString("lastname_ml"))
                        .tenantId(rs.getString("tenantid"))
                        .gender(Integer.valueOf(rs.getString("gender")))
                        .aadharNo(rs.getString("aadharno"))
                        .remarksEn(rs.getString("remarks_en"))
                        .remarksMl(rs.getString("remarks_ml"))
                        .esignUserCode(rs.getString("esign_user_code"))
                        .esignUserDesigCode(rs.getString("esign_user_desig_code"))
                        .isAdopted(Boolean.valueOf(rs.getString("is_adopted")))
                        .isAbandoned(Boolean.valueOf(rs.getString("is_abandoned")))
                        .isMultipleBirth(Boolean.valueOf(rs.getString("is_multiple_birth")))
                        .isFatherInfoMissing(Boolean.valueOf(rs.getString("is_father_info_missing")))
                        .isMotherInfoMissing(Boolean.valueOf(rs.getString("is_mother_info_missing")))
                        .noOfAliveBirth(Integer.parseInt(rs.getString("no_of_alive_birth")))
                        .multipleBirthDetId(rs.getString("multiplebirthdetid"))
                        .isBornOutside(Boolean.valueOf(rs.getString("is_born_outside")))
                        .otPassportNo(rs.getString("ot_passportno"))
                        .fullName(rs.getString("firstname_en")+" "+rs.getString("middlename_en")+" "+rs.getString("lastname_en"))
                        .fullNameMl(rs.getString("firstname_ml")+" "+rs.getString("middlename_ml")+" "+rs.getString("lastname_ml"))
                        //.dateOfArrival(Long.valueOf(rs.getString("ot_dateofarrival")))
                        .registrationNo(rs.getString("registrationno"))
                        .registrationDate(Long.valueOf(rs.getString("registration_date")))
                        .registerBirthPlace(getRegBirthPlace(rs))
                        .registerBirthFather(getRegBirthFatherInfo(rs))
                        .registerBirthMother(getRegBirthMotherInfo(rs))
                        .registerBirthPermanent(getRegBirthPermanentAddress(rs))
                        .registerBirthPresent(getRegBirthPresentAddress(rs))
                        .registerBirthStatitical(getRegBirthStatisticalInfo(rs))
                        .auditDetails(getAuditDetails(rs))
                        .build());
            }

            return result;
        }
}
