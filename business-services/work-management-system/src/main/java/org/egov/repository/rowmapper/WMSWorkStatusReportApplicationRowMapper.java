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
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkStatusReportApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSWorkStatusReportApplicationRowMapper implements ResultSetExtractor<List<WMSWorkStatusReportApplication>> {
    public List<WMSWorkStatusReportApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSWorkStatusReportApplication> wmsWorkStatusReportApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String wsrId = rs.getString("wWsrId");
            WMSWorkStatusReportApplication wmsWorkStatusReportApplication = wmsWorkStatusReportApplicationMap.get(wsrId);

            if(wmsWorkStatusReportApplication == null) {

            	Long lastModifiedTime = rs.getLong("wLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("wCreatedBy"))
                        .createdTime(rs.getLong("wCreatedtime"))
                        .lastModifiedBy(rs.getString("wLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsWorkStatusReportApplication = WMSWorkStatusReportApplication.builder()
                        .wsrId(rs.getString("wWsrId"))
                        .projectName(rs.getString("wProjectName"))
                        .workName(rs.getString("wWorkName"))
                        .activityName(rs.getString("wActivityName"))
                        .roleName(rs.getString("wRoleName"))
                        .employeeName(rs.getString("wEmployeeName"))
                        .startDate(rs.getString("wStartDate"))
                        .endDate(rs.getString("wEndDate"))
                        .remarksContent(rs.getString("wRemarksContent"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsWorkStatusReportApplicationMap.put(wsrId, wmsWorkStatusReportApplication);
        }
        return new ArrayList<>(wmsWorkStatusReportApplicationMap.values());
    }

}
