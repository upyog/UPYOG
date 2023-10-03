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
import org.egov.web.models.WMSPrimaryAccountHeadApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSPrimaryAccountHeadApplicationRowMapper implements ResultSetExtractor<List<WMSPrimaryAccountHeadApplication>> {
    public List<WMSPrimaryAccountHeadApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSPrimaryAccountHeadApplication> wmsPrimaryAccountHeadApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String primaryAccountHeadId = rs.getString("pPrimaryAccountHeadId");
            WMSPrimaryAccountHeadApplication wmsPrimaryAccountHeadApplication = wmsPrimaryAccountHeadApplicationMap.get(primaryAccountHeadId);

            if(wmsPrimaryAccountHeadApplication == null) {

            	Long lastModifiedTime = rs.getLong("pLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("pCreatedBy"))
                        .createdTime(rs.getLong("pCreatedtime"))
                        .lastModifiedBy(rs.getString("pLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsPrimaryAccountHeadApplication = WMSPrimaryAccountHeadApplication.builder()
                        .primaryAccountHeadId(rs.getString("pPrimaryAccountHeadId"))
                        .primaryAccountHeadName(rs.getString("pPrimaryAccountHeadName"))
                        .primaryAccountHeadAccountno(rs.getString("pPrimaryAccountHeadAccountno"))
                        .primaryAccountHeadLocation(rs.getString("pPrimaryAccountHeadLocation"))
                        .accountStatus(rs.getString("pAccountStatus"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsPrimaryAccountHeadApplicationMap.put(primaryAccountHeadId, wmsPrimaryAccountHeadApplication);
        }
        return new ArrayList<>(wmsPrimaryAccountHeadApplicationMap.values());
    }

}
