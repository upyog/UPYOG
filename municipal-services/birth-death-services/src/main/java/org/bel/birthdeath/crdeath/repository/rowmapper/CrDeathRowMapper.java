package org.bel.birthdeath.crdeath.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

/**
     * Creates CrDeathService
     * Rakhi S IKM
     * on  05/12/2022
     */
    
@Component
public class CrDeathRowMapper implements ResultSetExtractor<List<CrDeathDtl>>, BaseRowMapper{
   
    @Override
    public List<CrDeathDtl> extractData(ResultSet rs) throws SQLException, DataAccessException { // NOPMD

        List<CrDeathDtl> result = new ArrayList<>();
        while (rs.next()) {
            result.add(CrDeathDtl.builder()
                                        .id(rs.getString("id"))
                                        .deceasedTitle(rs.getString("deceased_title"))                                        
                                        .deceasedFirstNameEn(rs.getString("deceased_firstname_en"))
                                        .deceasedMiddleNameEn(rs.getString("deceased_middlename_en"))
                                        .deceasedLastNameEn(rs.getString("deceased_lastname_en"))
                                        .deceasedAadharNumber(rs.getString("deceased_aadhar_number"))
                                        .tenantId(rs.getString("tenantid"))
                                        .auditDetails(getAuditDetails(rs))
                                        .build());
        }

        return result;
    }
}
