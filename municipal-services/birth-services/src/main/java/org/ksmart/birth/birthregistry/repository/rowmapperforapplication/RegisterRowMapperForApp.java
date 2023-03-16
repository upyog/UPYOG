package org.ksmart.birth.birthregistry.repository.rowmapperforapplication;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.repository.rowmapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class RegisterRowMapperForApp implements ResultSetExtractor<List<RegisterBirthDetail>>, BaseRegRowMapper,
        BirthRegFatherInfoRowMapper, BirthRegMotherInfoRowMapper, BirthRegStatInfoRowMapper {

    private final BirthRegPerAddRowMapper birthRegPerAddRowMapper;
    private final BirthRegPreAddRowMapper birthRegPreAddRowMapper;
    private final BirthRegPlaceRowMapper birthRegPlaceRowMapper;

    @Autowired
    RegisterRowMapperForApp(BirthRegPerAddRowMapper birthRegPerAddRowMapper, BirthRegPreAddRowMapper birthRegPreAddRowMapper, BirthRegPlaceRowMapper birthRegPlaceRowMapper) {
        this.birthRegPerAddRowMapper = birthRegPerAddRowMapper;
        this.birthRegPreAddRowMapper = birthRegPreAddRowMapper;
        this.birthRegPlaceRowMapper = birthRegPlaceRowMapper;
    }


        @Override
        public List<RegisterBirthDetail> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
            List<RegisterBirthDetail> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RegisterBirthDetail.builder()
                        .dateOfReport(rs.getLong("ba_dateofreport"))
                        .dateOfBirth(rs.getLong("ba_dateofbirth"))
                        .timeOfBirth(rs.getLong("ba_timeofbirth"))
                        .ampm(rs.getString("ba_am_pm"))
                        .firstNameEn(rs.getString("ba_firstname_en"))
                        .firstNameMl(rs.getString("ba_firstname_ml"))
                        .middleNameEn(rs.getString("ba_middlename_en"))
                        .middleNameMl(rs.getString("ba_middlename_ml"))
                        .lastNameEn(rs.getString("ba_lastname_en"))
                        .lastNameMl(rs.getString("ba_lastname_ml"))
                        .tenantId(rs.getString("ba_tenantid"))
                        .gender(rs.getString("ba_gender"))
                        .aadharNo(rs.getString("ba_aadharno"))
                        .remarksEn(rs.getString("ba_remarks_en"))
                        .remarksMl(rs.getString("ba_remarks_ml"))
                        .esignUserCode(rs.getString("ba_esign_user_code"))
                        .esignUserDesigCode(rs.getString("ba_esign_user_desig_code"))
                        .isAdopted(rs.getBoolean("ba_is_adopted"))
                        .isAbandoned(rs.getBoolean("ba_is_abandoned"))
                        .isMultipleBirth(rs.getBoolean("ba_is_multiple_birth"))
                        .isFatherInfoMissing(rs.getBoolean("ba_is_father_info_missing"))
                        .isMotherInfoMissing(rs.getBoolean("ba_is_mother_info_missing"))
                        .noOfAliveBirth(Integer.parseInt(rs.getString("ba_no_of_alive_birth")))
                        .multipleBirthDetId(rs.getString("ba_multiplebirthdetid"))
                        .isBornOutside(rs.getBoolean("ba_is_born_outside"))
                        .otPassportNo(rs.getString("ba_ot_passportno"))
                        .ackNumber(rs.getString("ba_applicationno"))
                        .registrationNo(rs.getString("ba_registrationno"))
                        .registrationDate(rs.getLong("ba_registration_date"))
                        .registerBirthPlace(birthRegPlaceRowMapper.getRegBirthPlace(rs))
                        .registerBirthFather(getRegBirthFatherInfo(rs))
                        .registerBirthMother(getRegBirthMotherInfo(rs))
                        .registerBirthPermanent(birthRegPerAddRowMapper.getRegBirthPermanentAddress(rs))
                        .registerBirthPresent(birthRegPreAddRowMapper.getRegBirthPresentAddress(rs))
                        //.registerBirthStatitical(getRegBirthStatisticalInfo(rs))
                        .auditDetails(getAuditDetails(rs))
                        .build());
            }
            return result;
        }
}
