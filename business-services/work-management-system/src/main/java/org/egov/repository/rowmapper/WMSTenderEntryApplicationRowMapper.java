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
import org.egov.web.models.WMSTenderEntryApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSTenderEntryApplicationRowMapper implements ResultSetExtractor<List<WMSTenderEntryApplication>> {
    public List<WMSTenderEntryApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSTenderEntryApplication> wmsTenderEntryApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String tenderId = rs.getString("tTenderId");
            WMSTenderEntryApplication wmsTenderEntryApplication = wmsTenderEntryApplicationMap.get(tenderId);

            if(wmsTenderEntryApplication == null) {

            	Long lastModifiedTime = rs.getLong("tLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("tCreatedBy"))
                        .createdTime(rs.getLong("tCreatedtime"))
                        .lastModifiedBy(rs.getString("tLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsTenderEntryApplication = WMSTenderEntryApplication.builder()
                        .tenderId(rs.getString("ttenderId"))
                        .departmentName(rs.getString("tdepartmentName"))
                        .requestCategory(rs.getString("trequestCategory"))
                        .projectName(rs.getString("tprojectName"))
                        .resolutionNo(rs.getInt("tresolutionNo"))
                        .resolutionDate(rs.getString("tresolutionDate"))
                        .prebidMeetingDate(rs.getString("tprebidMeetingDate"))
                        .prebidMeetingLocation(rs.getString("tprebidMeetingLocation"))
                        .issueFromDate(rs.getString("tissueFromDate"))
                        .issueTillDate(rs.getString("tissueTillDate"))
                        .publishDate(rs.getString("tpublishDate"))
                        .technicalBidOpenDate(rs.getString("ttechnicalBidOpenDate"))
                        .financialBidOpenDate(rs.getString("tfinancialBidOpenDate"))
                        .validity(rs.getInt("tvalidity"))
                        .uploadDocument(rs.getString("tuploadDocument"))
						/*
						 * .workNo(rs.getString("tworkNo"))
						 * .workDescription(rs.getString("tworkDescription"))
						 * .estimatedCost(rs.getString("testimatedCost"))
						 * .tenderType(rs.getString("ttenderType")) .tenderFee(rs.getInt("ttenderFee"))
						 * .emd(rs.getString("temd")) .vendorClass(rs.getString("tvendorClass"))
						 * .workDuration(rs.getString("tworkDuration"))
						 */
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsTenderEntryApplicationMap.put(tenderId, wmsTenderEntryApplication);
        }
        return new ArrayList<>(wmsTenderEntryApplicationMap.values());
    }

}
