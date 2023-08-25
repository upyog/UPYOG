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
import org.egov.web.models.WMSTenderEntryApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSTenderEntryApplicationRowMapper implements ResultSetExtractor<List<WMSTenderEntryApplication>> {
    public List<WMSTenderEntryApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer,WMSTenderEntryApplication> wmsTenderEntryApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            int tenderId = rs.getInt("tTenderId");
            WMSTenderEntryApplication wmsTenderEntryApplication = wmsTenderEntryApplicationMap.get(tenderId);

            if(wmsTenderEntryApplication == null) {

                Date lastModifiedTime = rs.getDate("tpublishDate");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                wmsTenderEntryApplication = WMSTenderEntryApplication.builder()
                        .tenderId(rs.getInt("ttenderId"))
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
                        .workNo(rs.getString("tworkNo"))
                        .workDescription(rs.getString("tworkDescription"))
                        .estimatedCost(rs.getString("testimatedCost"))
                        .tenderType(rs.getString("ttenderType"))
                        .tenderFee(rs.getInt("ttenderFee"))
                        .emd(rs.getString("temd"))
                        .vendorClass(rs.getString("tvendorClass"))
                        .workDuration(rs.getString("tworkDuration"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsTenderEntryApplicationMap.put(tenderId, wmsTenderEntryApplication);
        }
        return new ArrayList<>(wmsTenderEntryApplicationMap.values());
    }

}
