package org.egov.filemgmnt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class ApplicantPersonalRowMapper implements ResultSetExtractor<List<ApplicantPersonal>>, BaseRowMapper {

    @Override
    public List<ApplicantPersonal> extractData(ResultSet rs) throws SQLException, DataAccessException { // NOPMD

        List<ApplicantPersonal> result = new ArrayList<>();
        while (rs.next()) {
            result.add(ApplicantPersonal.builder()
                                        .id(rs.getString("id"))
                                        .aadhaarNo(rs.getString("aadhaarno"))
                                        .email(rs.getString("email"))
                                        .firstName(rs.getString("firstname"))
                                        .lastName(rs.getString("lastname"))
                                        .title(rs.getString("title"))
                                        .mobileNo(rs.getString("mobileno"))
                                        .tenantId(rs.getString("tenantid"))
                                        .auditDetails(getAuditDetails(rs))
                                        .build());
        }

        return result;
    }
}
