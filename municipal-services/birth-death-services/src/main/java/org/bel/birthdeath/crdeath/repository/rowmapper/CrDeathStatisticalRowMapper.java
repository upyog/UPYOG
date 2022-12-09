package org.bel.birthdeath.crdeath.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bel.birthdeath.crdeath.web.models.CrDeathStatistical;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

/**
     * Creates CrDeathStatisticalRowMapper
     * Rakhi S IKM
     * on  09/12/2022
     */
    
@Component
public class CrDeathStatisticalRowMapper 
        implements ResultSetExtractor  , BaseRowMapper{
    @Override
    public CrDeathStatistical extractData(ResultSet rs) throws SQLException, DataAccessException { // STATISTICAL

        return CrDeathStatistical.builder()
                            .id(rs.getString("statid"))
                            .deathDtlId(rs.getString("death_dtl_id"))
                            .residenceLocalBody(rs.getString("residencelocalbody"))
                            .residencePlaceType(rs.getString("residence_place_type"))
                            .residenceDistrict(rs.getString("residencedistrict"))
                            .residenceState(rs.getString("residencestate"))
                            .religion(rs.getString("religion"))
                            .religionOther(rs.getString("religion_other"))
                            .occupation(rs.getString("occupation"))
                            .occupationOther(rs.getString("occupation_other"))
                            .medicalAttentionType(rs.getString("medical_attention_type"))
                            .deathMedicallyCertified(rs.getInt("death_medically_certified"))
                            .deathCauseMain(rs.getString("death_cause_main"))
                            .deathCauseSub(rs.getString("death_cause_sub"))
                            .deathCauseOther(rs.getString("death_cause_other"))
                            .deathDuringDelivery(rs.getInt("death_during_delivery"))
                            .smokingNumYears(rs.getInt("smoking_num_years"))
                            .tobaccoNumYears(rs.getInt("tobacco_num_years"))
                            .arecanutNumYears(rs.getInt("arecanut_num_years"))
                            .alcoholNumYears(rs.getInt("alcohol_num_years"))
                            .nationality(rs.getString("nationality"))
                            .build();
    }

    
}
