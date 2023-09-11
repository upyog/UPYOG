package org.egov.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.egov.web.models.AuditDetails;
import org.egov.web.models.Scheme;

@Component
public class SchemeRowMapper implements ResultSetExtractor<List<Scheme>> {
    public List<Scheme> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,Scheme> schemeApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String Id = rs.getString("sSchemeId");
            Scheme schemeApplication = schemeApplicationMap.get(Id);

            if(schemeApplication == null) {

            	Long lastModifiedTime = rs.getLong("sLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("sCreatedBy"))
                        .createdTime(rs.getLong("sCreatedtime"))
                        .lastModifiedBy(rs.getString("sLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                schemeApplication = Scheme.builder()
                        .id(rs.getString("sSchemeId"))
                        .schemeNameEn(rs.getString("sSchemeName"))
                        .schemeNameReg(rs.getString("sSchemeNameReg"))
                        .startDate(rs.getString("sSchemeStartDate"))
                        .endDate(rs.getString("sSchemeEndDate"))
                        .sourceOfFund(rs.getString("sSchemeSourceOfFund"))
                        .fund(rs.getString("sSchemeFund"))
                        .schemeDescription(rs.getString("sSchemeDescription"))
                        .uploadDocument(rs.getString("sSchemeUploadDocument"))
                        .build();
            }
            
            schemeApplicationMap.put(Id, schemeApplication);
        }
        return new ArrayList<>(schemeApplicationMap.values());
    }

}
