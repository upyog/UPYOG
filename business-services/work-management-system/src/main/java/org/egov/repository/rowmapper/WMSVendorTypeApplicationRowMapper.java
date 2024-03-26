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
import org.egov.web.models.WMSVendorTypeApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSVendorTypeApplicationRowMapper implements ResultSetExtractor<List<WMSVendorTypeApplication>> {
    public List<WMSVendorTypeApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSVendorTypeApplication> wmsVendorTypeApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String vendorId = rs.getString("vVendorId");
            WMSVendorTypeApplication wmsVendorTypeApplication = wmsVendorTypeApplicationMap.get(vendorId);

            if(wmsVendorTypeApplication == null) {

            	Long lastModifiedTime = rs.getLong("vLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("vCreatedBy"))
                        .createdTime(rs.getLong("vCreatedtime"))
                        .lastModifiedBy(rs.getString("vLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsVendorTypeApplication = WMSVendorTypeApplication.builder()
                        .vendorId(rs.getString("vVendorId"))
                        .vendorTypeName(rs.getString("vVendorTypeName"))
                        .vendorTypeStatus(rs.getString("vVendorTypeStatus"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsVendorTypeApplicationMap.put(vendorId, wmsVendorTypeApplication);
        }
        return new ArrayList<>(wmsVendorTypeApplicationMap.values());
    }

}
