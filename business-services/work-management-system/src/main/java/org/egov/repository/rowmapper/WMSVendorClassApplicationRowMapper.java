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
import org.egov.web.models.WMSContractorSubTypeApplication;
import org.egov.web.models.WMSVendorClassApplication;
import org.egov.web.models.WMSVendorTypeApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSVendorClassApplicationRowMapper implements ResultSetExtractor<List<WMSVendorClassApplication>> {
    public List<WMSVendorClassApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSVendorClassApplication> wmsVendorClassApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String vendorClassId = rs.getString("cVendorClassId");
            WMSVendorClassApplication wmsVendorClassApplication = wmsVendorClassApplicationMap.get(vendorClassId);

            if(wmsVendorClassApplication == null) {

            	Long lastModifiedTime = rs.getLong("cLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("cCreatedBy"))
                        .createdTime(rs.getLong("cCreatedtime"))
                        .lastModifiedBy(rs.getString("cLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsVendorClassApplication = WMSVendorClassApplication.builder()
                        .vendorClassId(rs.getString("cVendorClassId"))
                        .vendorClassName(rs.getString("cVendorClassName"))
                        .vendorClassStatus(rs.getString("cVendorClassStatus"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsVendorClassApplicationMap.put(vendorClassId, wmsVendorClassApplication);
        }
        return new ArrayList<>(wmsVendorClassApplicationMap.values());
    }

}
