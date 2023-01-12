package org.ksmart.birth.birthcorrection.repository.rowmapper;

import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class BirthCorrectionRowMapper implements ResultSetExtractor<List<BirthApplicationDetail>>, BaseRowMapper, BirthPlaceRowMapper,
        BirthFatherInfoRowMapper, BirthMotherInfoRowMapper, BirthPermanentAddressRowMapper,
        BirthPresentAddressRowMapper,BirthStatInfoRowMapper  {

    @Override
    public List<BirthApplicationDetail> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<BirthApplicationDetail> result = new ArrayList<>();
        while (rs.next()) {
            result.add(BirthApplicationDetail.builder()
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
                    .gender(rs.getString("gender"))
                    .remarksEn(rs.getString("remarks_en"))
                    .remarksMl(rs.getString("aadharno"))
                    .esignUserCode(rs.getString("esign_user_code"))
                    .esignUserDesigCode(rs.getString("esign_user_desig_code"))
                    .multipleBirthDetailsIid(rs.getString("multiplebirthdetid"))
                    .isBornOutside(Boolean.valueOf(rs.getString("is_born_outside")))
                    .passportNo(rs.getString("ot_passportno"))
                    //.dateOfArrival(Long.valueOf(rs.getString("ot_dateofarrival")))
                    .applicationType(rs.getString("applicationtype"))
                    .businessService(rs.getString("businessservice"))
                    .workFlowCode(rs.getString("workflowcode"))
                    .fmFileNo(rs.getString("fm_fileno"))
                    .fileDate(Long.valueOf(rs.getString("file_date")))
                    .applicationNo(rs.getString("applicationno"))
                    .registrationNo(rs.getString("registrationno"))
                    .registrationDate(Long.valueOf(rs.getString("registration_date")))
                    .action(rs.getString("action"))
                    .status(rs.getString("status"))
                    .birthPlace(getBirthPlace(rs))
                    .birthFatherInfo(getBirthFatherInfo(rs))
                    .birthMotherInfo(getBirthMotherInfo(rs))
                    .birthPermanentAddress(getBirthPermanentAddress(rs))
                    .birthPresentAddress(getBirthPresentAddress(rs))
                    .birthStatisticalInformation(getBirthStatisticalInfo(rs))
                    .auditDetails(getAuditDetails(rs))
                    .build());
        }

        return result;
    }
}
