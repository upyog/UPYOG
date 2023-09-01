package org.egov.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        Map<Integer,WMSWorkAwardApprovalApplication> wmsWorkAwardApprovalApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            int workAwardId = rs.getInt("wWorkAwardId");
            WMSWorkAwardApprovalApplication wmsWorkAwardApprovalApplication = wmsWorkAwardApprovalApplicationMap.get(workAwardId);

            if(wmsWorkAwardApprovalApplication == null) {

                Date lastModifiedTime = rs.getDate("wAwardDate");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                wmsWorkAwardApprovalApplication = WMSWorkAwardApprovalApplication.builder()
                        .workAwardId(rs.getInt("wWorkAwardId"))
                        .workName(rs.getString("wWorkName"))
                        .percentageType(rs.getString("wPercentageType"))
                        .quotedPercentage(rs.getString("wQuotedPercentage"))
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
