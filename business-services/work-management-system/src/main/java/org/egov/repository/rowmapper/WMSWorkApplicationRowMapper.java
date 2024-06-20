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
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;



@Component
public class WMSWorkApplicationRowMapper implements ResultSetExtractor<List<WMSWorkApplication>> {
    public List<WMSWorkApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, WMSWorkApplication> wmsWorkApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String workId = rs.getString("wWorkId");
            WMSWorkApplication wmsWorkApplication = wmsWorkApplicationMap.get(workId);

            if(wmsWorkApplication == null) {

                Long lastModifiedTime = rs.getLong("wlastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("wcreatedBy"))
                        .createdTime(rs.getLong("wcreatedTime"))
                        .lastModifiedBy(rs.getString("wlastModifiedBy"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                
                wmsWorkApplication = WMSWorkApplication.builder()
                        .workId(rs.getString("wWorkId"))
                        .projectId(rs.getInt("wProjectId"))
                        .workNo(rs.getString("wWorkNo"))
                        .workName(rs.getString("wWorkName"))
                        .projectName(rs.getString("wProjectName"))
                        .departmentName(rs.getString("wDepartmentName"))
                        .workType(rs.getString("wWorkType"))
                        .workCategory(rs.getString("wWorkCategory"))
                        .workSubtype(rs.getString("wWorkSubtype"))
                        .projectPhase(rs.getString("wProjectPhase"))
                        .deviationPercent(rs.getInt("wDeviationPercent"))
                        .startLocation(rs.getString("wStartLocation"))
                        .endLocation(rs.getString("wEndLocation"))
                        .financialYear(rs.getString("wFinancialYear"))
                        .budgetHead(rs.getString("wBudgetHead"))
                        .auditDetails(auditdetails)
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsWorkApplicationMap.put(workId, wmsWorkApplication);
        }
        return new ArrayList<>(wmsWorkApplicationMap.values());
    }

}
