package org.egov.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.web.models.AuditDetails;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorSubTypeApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSContractorSubTypeApplicationRowMapper implements ResultSetExtractor<List<WMSContractorSubTypeApplication>> {
    public List<WMSContractorSubTypeApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSContractorSubTypeApplication> wmsContractorSubTypeApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String contractorId = rs.getString("cContractorId");
            WMSContractorSubTypeApplication wmsContractorSubTypeApplication = wmsContractorSubTypeApplicationMap.get(contractorId);

            if(wmsContractorSubTypeApplication == null) {

            	Long lastModifiedTime = rs.getLong("cLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("cCreatedBy"))
                        .createdTime(rs.getLong("cCreatedtime"))
                        .lastModifiedBy(rs.getString("cLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsContractorSubTypeApplication = WMSContractorSubTypeApplication.builder()
                        .contractorId(rs.getString("cContractorId"))
                        .contractorStypeName(rs.getString("cContractorStypeName"))
                        .contractorStypeStatus(rs.getString("cContractorStypeStatus"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsContractorSubTypeApplicationMap.put(contractorId, wmsContractorSubTypeApplication);
        }
        return new ArrayList<>(wmsContractorSubTypeApplicationMap.values());
    }

}
