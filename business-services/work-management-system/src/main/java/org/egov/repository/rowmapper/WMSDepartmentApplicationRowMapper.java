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
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSDepartmentApplicationRowMapper implements ResultSetExtractor<List<WMSDepartmentApplication>> {
    public List<WMSDepartmentApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSDepartmentApplication> wmsDepartmentApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String deptId = rs.getString("dDeptId");
            WMSDepartmentApplication wmsDepartmentApplication = wmsDepartmentApplicationMap.get(deptId);

            if(wmsDepartmentApplication == null) {

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
                wmsDepartmentApplication = WMSDepartmentApplication.builder()
                        .deptId(rs.getString("dDeptId"))
                        .deptName(rs.getString("dDeptName"))
                        .deptStatus(rs.getString("dDeptStatus"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsDepartmentApplicationMap.put(deptId, wmsDepartmentApplication);
        }
        return new ArrayList<>(wmsDepartmentApplicationMap.values());
    }

}
