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
import org.egov.web.models.WMSProjectRegisterApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSProjectRegisterApplicationRowMapper implements ResultSetExtractor<List<WMSProjectRegisterApplication>> {
    public List<WMSProjectRegisterApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSProjectRegisterApplication> wmsProjectRegisterApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String registerId = rs.getString("pRegisterId");
            WMSProjectRegisterApplication wmsProjectRegisterApplication = wmsProjectRegisterApplicationMap.get(registerId);

            if(wmsProjectRegisterApplication == null) {

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
                wmsProjectRegisterApplication = WMSProjectRegisterApplication.builder()
                        .registerId(rs.getString("pRegisterId"))
                        .schemeName(rs.getString("pSchemeName"))
                        .projectName(rs.getString("pProjectName"))
                        .workName(rs.getString("pWorkName"))
                        .workType(rs.getString("pWorkType"))
                        .estimatedNumber(rs.getInt("pEstimatedNumber"))
                        .estimatedWorkCost(rs.getString("pEstimatedWorkCost"))
                        .sanctionedTenderAmount(rs.getLong("pSanctionedTenderAmount"))
                        .statusName(rs.getString("pStatusName"))
                        .billReceivedTillDate(rs.getString("pBillReceivedTillDate"))
                        //.paymentReceivedTillDate(rs.getString("pPaymentReceivedTillDate"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsProjectRegisterApplicationMap.put(registerId, wmsProjectRegisterApplication);
        }
        return new ArrayList<>(wmsProjectRegisterApplicationMap.values());
    }

}
