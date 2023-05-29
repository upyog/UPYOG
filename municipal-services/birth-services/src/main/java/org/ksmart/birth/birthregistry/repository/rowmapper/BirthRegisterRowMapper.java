package org.ksmart.birth.birthregistry.repository.rowmapper;

import org.jsoup.internal.StringUtil;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
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
public class BirthRegisterRowMapper implements ResultSetExtractor<List<RegisterBirthDetail>>,BaseRegRowMapper,
    BirthRegFatherInfoRowMapper, BirthRegMotherInfoRowMapper, BirthRegStatInfoRowMapper {

    private final BirthRegPerAddRowMapper birthRegPerAddRowMapper;
    private final BirthRegPreAddRowMapper birthRegPreAddRowMapper;
    private final BirthRegPlaceRowMapper birthRegPlaceRowMapper;

    @Autowired
    BirthRegisterRowMapper(BirthRegPerAddRowMapper birthRegPerAddRowMapper, BirthRegPreAddRowMapper birthRegPreAddRowMapper,  BirthRegPlaceRowMapper birthRegPlaceRowMapper) {
        this.birthRegPerAddRowMapper = birthRegPerAddRowMapper;
        this.birthRegPreAddRowMapper = birthRegPreAddRowMapper;
        this.birthRegPlaceRowMapper = birthRegPlaceRowMapper;
    }
        @Override
        public List<RegisterBirthDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<RegisterBirthDetail> result = new ArrayList<>();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            while (rs.next()) {
                Date regDate = new Date(rs.getLong("registration_date"));
                Date dobDate = new Date(rs.getLong("dateofbirth"));
                result.add(RegisterBirthDetail.builder()
                        .id(rs.getString("id"))
                        .dateOfReport(rs.getLong("dateofreport"))
                        .dobStr(formatter.format(dobDate))
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
                        .gender(rs.getString("gender"))
                        .aadharNo(rs.getString("aadharno"))
                        .remarksEn(rs.getString("remarks_en"))
                        .remarksMl(rs.getString("remarks_ml"))
                        .esignUserCode(rs.getString("esign_user_code"))
                        .esignUserDesigCode(rs.getString("esign_user_desig_code"))
                        .isAdopted(rs.getBoolean("is_adopted"))
                        .isAbandoned(rs.getBoolean("is_abandoned"))
                        .isMultipleBirth(rs.getBoolean("is_multiple_birth"))
                        .isFatherInfoMissing(rs.getBoolean("is_father_info_missing"))
                        .isMotherInfoMissing(rs.getBoolean("is_mother_info_missing"))
                        .noOfAliveBirth(rs.getInt("no_of_alive_birth"))
                        .multipleBirthDetId(rs.getString("multiplebirthdetid"))
                        .isBornOutside(rs.getBoolean("is_born_outside"))
                        .otPassportNo(rs.getString("ot_passportno"))
                        .ackNumber(rs.getString("ack_no"))
                        .fullName(getFullNameEn(rs))
                        .fullNameMl(getFullNameMl(rs))
                        .registrationNo(rs.getString("registrationno"))
                        .registrationDate(rs.getLong("registration_date"))
                        .applicationType(rs.getString("applicationtype"))
                        .applicationId(rs.getString("applicationid"))
                        .isMigrated(rs.getBoolean("is_migrated"))
                        .migratedFrom(rs.getString("migrated_from"))

                        .registerBirthPlace(birthRegPlaceRowMapper.getRegBirthPlace(rs))
                        .registerBirthFather(getRegBirthFatherInfo(rs))
                        .registerBirthMother(getRegBirthMotherInfo(rs))
                        .registerBirthPermanent(birthRegPerAddRowMapper.getRegBirthPermanentAddress(rs))
                        .registerBirthPresent(birthRegPreAddRowMapper.getRegBirthPresentAddress(rs))
                        .auditDetails(getAuditDetails(rs))
                        .registrationDateStr(formatter.format(regDate))
                        .build());
            }
            return result;
        }

    private String getFullNameEn(ResultSet rs) throws SQLException {
        String fullName = new StringBuilder()
                .append(rs.getString("firstname_en") == null ? "" : rs.getString("firstname_en")+" ")
                .append(rs.getString("middlename_en") == null ? "" : rs.getString("middlename_en")+" ")
                .append(rs.getString("lastname_en") == null ? "" : rs.getString("lastname_en")).toString();
        if(StringUtil.isBlank(fullName)) {
            return "Name Not Registered";
        } else{
            return fullName;
        }
    }

    private String getFullNameMl(ResultSet rs) throws SQLException {
        String fullName = new StringBuilder()
                .append(rs.getString("firstname_ml") == null ? "" : rs.getString("firstname_ml")+" ")
                .append(rs.getString("middlename_ml") == null ? "" : rs.getString("middlename_ml")+" ")
                .append(rs.getString("lastname_ml") == null ? "" : rs.getString("lastname_ml")).toString();
        if(StringUtil.isBlank(fullName)) {
            return "പേര് രജിസ്റ്റർ ചെയ്തിട്ടില്ല";
        } else{
            return fullName;
        }
    }
}
