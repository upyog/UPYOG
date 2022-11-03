package org.egov.filemgmnt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.filemgmnt.web.models.ApplicantService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class ApplicantServiceRowMapper implements ResultSetExtractor<List<ApplicantService>>, BaseRowMapper {

    @Override
    public List<ApplicantService> extractData(ResultSet rs) throws SQLException, DataAccessException { // NOPMD

        List<ApplicantService> result = new ArrayList<>();
        while (rs.next()) {
            result.add(ApplicantService.builder()
                                       .id(rs.getString("id"))
                                       .applicantPersonalId(rs.getString("applicantpersonalid"))
                                       .serviceId(rs.getString("serviceid"))
                                       .serviceCode(rs.getString("servicecode"))
                                       .businessService(rs.getString("businessservice"))
                                       .workflowCode(rs.getString("workflowcode"))
                                       .auditDetails(getAuditDetails(rs))
                                       .build());
        }

        return result;
    }
}