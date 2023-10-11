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
import org.egov.web.models.WMSTenderCategoryApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSTenderCategoryApplicationRowMapper implements ResultSetExtractor<List<WMSTenderCategoryApplication>> {
    public List<WMSTenderCategoryApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSTenderCategoryApplication> wmsTenderCategoryApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String categoryId = rs.getString("tCategoryId");
            WMSTenderCategoryApplication wmsTenderCategoryApplication = wmsTenderCategoryApplicationMap.get(categoryId);

            if(wmsTenderCategoryApplication == null) {

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
                wmsTenderCategoryApplication = WMSTenderCategoryApplication.builder()
                        .categoryId(rs.getString("tCategoryId"))
                        .categoryName(rs.getString("tCategoryName"))
                        .categoryStatus(rs.getString("tCategoryStatus"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsTenderCategoryApplicationMap.put(categoryId, wmsTenderCategoryApplication);
        }
        return new ArrayList<>(wmsTenderCategoryApplicationMap.values());
    }

}
