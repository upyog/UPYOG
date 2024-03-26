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
import org.egov.web.models.WMSFunctionApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSFunctionApplicationRowMapper implements ResultSetExtractor<List<WMSFunctionApplication>> {
    public List<WMSFunctionApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSFunctionApplication> wmsFunctionApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String functionId = rs.getString("fFunctionId");
            WMSFunctionApplication wmsFunctionApplication = wmsFunctionApplicationMap.get(functionId);

            if(wmsFunctionApplication == null) {

            	Long lastModifiedTime = rs.getLong("fLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("fCreatedBy"))
                        .createdTime(rs.getLong("fCreatedtime"))
                        .lastModifiedBy(rs.getString("fLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsFunctionApplication = WMSFunctionApplication.builder()
                        .functionId(rs.getString("fFunctionId"))
                        .functionName(rs.getString("fFunctionName"))
                        .functionCode(rs.getString("fFunctionCode"))
                        .functionLevel(rs.getInt("fFunctionLevel"))
                        .status(rs.getString("fStatus"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsFunctionApplicationMap.put(functionId, wmsFunctionApplication);
        }
        return new ArrayList<>(wmsFunctionApplicationMap.values());
    }

}
