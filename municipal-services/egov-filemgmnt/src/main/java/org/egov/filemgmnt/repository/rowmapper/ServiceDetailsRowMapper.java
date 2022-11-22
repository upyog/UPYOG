package org.egov.filemgmnt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.filemgmnt.web.models.ServiceDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class ServiceDetailsRowMapper implements ResultSetExtractor<List<ServiceDetails>>, BaseRowMapper {

    @Override
    public List<ServiceDetails> extractData(ResultSet rs) throws SQLException, DataAccessException { // NOPMD

        List<ServiceDetails> result = new ArrayList<>();
        while (rs.next()) {
            result.add(ServiceDetails.builder()
                                     .id(rs.getString("id"))
                                     .applicantPersonalId(rs.getString("applicantpersonalid"))
                                     .serviceId(rs.getString("serviceid"))
                                     .serviceCode(rs.getString("servicecode"))
                                     .auditDetails(getAuditDetails(rs))
                                     .build());
        }

        return result;
    }

}
