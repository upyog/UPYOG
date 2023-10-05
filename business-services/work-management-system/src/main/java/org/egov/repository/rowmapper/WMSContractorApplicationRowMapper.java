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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSContractorApplicationRowMapper implements ResultSetExtractor<List<WMSContractorApplication>> {
    public List<WMSContractorApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer,WMSContractorApplication> wmsContractorApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            int vendorId = rs.getInt("vVendorId");
            WMSContractorApplication wmsContractorApplication = wmsContractorApplicationMap.get(vendorId);

            if(wmsContractorApplication == null) {

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
                wmsContractorApplication = WMSContractorApplication.builder()
                        .vendorId(rs.getInt("vVendorId"))
                        .vendorType(rs.getString("vVendorType"))
                        .vendorSubType(rs.getString("vVendorSubType"))
                        .vendorName(rs.getString("vVendorName"))
                        .vendorStatus(rs.getString("vVendorStatus"))
                        .PFMSVendorCode(rs.getString("vPfmsVendorCode"))
                        .payTo(rs.getString("vPayTo"))
                        .mobileNumber(rs.getLong("vMobileNumber"))
                        .email(rs.getString("vEmail"))
                        .UIDNumber(rs.getLong("vUidNumber"))
                        .GSTNumber(rs.getLong("vGstNumber"))
                        .PANNumber(rs.getString("vPanNumber"))
                        .bankBranchIfscCode(rs.getString("vBankBranchIfscCode"))
                        .bankAccountNumber(rs.getLong("vBankAccountNumber"))
                        .function(rs.getString("vFunction"))
                        .primaryAccountHead(rs.getString("vPrimaryAccountHead"))
                        .vendorClass(rs.getString("vVendorClass"))
                        .address(rs.getString("vAddress"))
                        .EPFOAccountNumber(rs.getString("vEpfoAccountNumber"))
                        .vatNumber(rs.getString("vVatNumber"))
                        .allowDirectPayment(rs.getString("vAllowDirectPayment"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsContractorApplicationMap.put(vendorId, wmsContractorApplication);
        }
        return new ArrayList<>(wmsContractorApplicationMap.values());
    }

}
