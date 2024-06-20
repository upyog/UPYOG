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
import org.egov.web.models.WMSPhysicalMileStoneActivityApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSPhysicalMileStoneActivityApplicationRowMapper implements ResultSetExtractor<List<WMSPhysicalMileStoneActivityApplication>> {
    public List<WMSPhysicalMileStoneActivityApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSPhysicalMileStoneActivityApplication> wmsPhysicalMileStoneActivityApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String pmaId = rs.getString("pPmaId");
            WMSPhysicalMileStoneActivityApplication wmsPhysicalMileStoneActivityApplication = wmsPhysicalMileStoneActivityApplicationMap.get(pmaId);

            if(wmsPhysicalMileStoneActivityApplication == null) {

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
                wmsPhysicalMileStoneActivityApplication = WMSPhysicalMileStoneActivityApplication.builder()
                        .pmaId(rs.getString("pPmaId"))
                        .descriptionOfTheItem(rs.getString("pDescriptionOfTheItem"))
                        .percentageWeightage(rs.getString("pPercentageWeightage"))
                        .startDate(rs.getString("pStartDate"))
                        .endDate(rs.getString("pEndDate"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsPhysicalMileStoneActivityApplicationMap.put(pmaId, wmsPhysicalMileStoneActivityApplication);
        }
        return new ArrayList<>(wmsPhysicalMileStoneActivityApplicationMap.values());
    }

}
