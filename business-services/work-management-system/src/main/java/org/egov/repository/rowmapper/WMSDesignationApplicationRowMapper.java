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
import org.egov.web.models.WMSDepartmentApplication;
import org.egov.web.models.WMSDesignationApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSDesignationApplicationRowMapper implements ResultSetExtractor<List<WMSDesignationApplication>> {
    public List<WMSDesignationApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSDesignationApplication> wmsDesignationApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String desnId = rs.getString("dDesnId");
            WMSDesignationApplication wmsDesignationApplication = wmsDesignationApplicationMap.get(desnId);

            if(wmsDesignationApplication == null) {

            	Long lastModifiedTime = rs.getLong("dLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("dCreatedBy"))
                        .createdTime(rs.getLong("dCreatedtime"))
                        .lastModifiedBy(rs.getString("dLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsDesignationApplication = WMSDesignationApplication.builder()
                        .desnId(rs.getString("dDesnId"))
                        .desnName(rs.getString("dDesnName"))
                        .desnStatus(rs.getString("dDesnStatus"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsDesignationApplicationMap.put(desnId, wmsDesignationApplication);
        }
        return new ArrayList<>(wmsDesignationApplicationMap.values());
    }

}
