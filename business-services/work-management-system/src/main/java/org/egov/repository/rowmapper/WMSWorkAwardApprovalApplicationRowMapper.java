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
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkAwardApprovalApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSWorkAwardApprovalApplicationRowMapper implements ResultSetExtractor<List<WMSWorkAwardApprovalApplication>> {
    public List<WMSWorkAwardApprovalApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSWorkAwardApprovalApplication> wmsWorkAwardApprovalApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String workAwardId = rs.getString("wWorkAwardId");
            WMSWorkAwardApprovalApplication wmsWorkAwardApprovalApplication = wmsWorkAwardApprovalApplicationMap.get(workAwardId);

            if(wmsWorkAwardApprovalApplication == null) {

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
                wmsWorkAwardApprovalApplication = WMSWorkAwardApprovalApplication.builder()
                        .workAwardId(rs.getString("wWorkAwardId"))
                        .workNo(rs.getString("wWorkNo"))
                        .workName(rs.getString("wWorkName"))
                        .percentageType(rs.getString("wPercentageType"))
                        .quotedPercentage(rs.getString("wQuotedPercentage"))
                        .acceptedWorkCost(rs.getString("wAcceptedWorkCost"))
                        .contractorName(rs.getString("wContractorName"))
                        .noOfDaysForAgreement(rs.getInt("wNoOfDaysForAgreement"))
                        .loaGeneration(rs.getString("wLoaGeneration"))
                        .awardDate(rs.getString("wAwardDate"))
                        .documentUpload(rs.getString("wDocumentUpload"))
                        .awardStatus(rs.getString("wAwardStatus"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsWorkAwardApprovalApplicationMap.put(workAwardId, wmsWorkAwardApprovalApplication);
        }
        return new ArrayList<>(wmsWorkAwardApprovalApplicationMap.values());
    }

}
