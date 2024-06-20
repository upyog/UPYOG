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
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSBankDetailsApplicationRowMapper implements ResultSetExtractor<List<WMSBankDetailsApplication>> {
    public List<WMSBankDetailsApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSBankDetailsApplication> wmsBankDetailsApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String bankId = rs.getString("bBankId");
            WMSBankDetailsApplication wmsBankDetailsApplication = wmsBankDetailsApplicationMap.get(bankId);

            if(wmsBankDetailsApplication == null) {

            	Long lastModifiedTime = rs.getLong("bLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("bCreatedBy"))
                        .createdTime(rs.getLong("bCreatedtime"))
                        .lastModifiedBy(rs.getString("bLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsBankDetailsApplication = WMSBankDetailsApplication.builder()
                        .bankId(rs.getString("bBankId"))
                        .bankName(rs.getString("bBankName"))
                        .bankBranch(rs.getString("bBankBranch"))
                        .bankIfscCode(rs.getString("bBankIfscCode"))
                        .bankBranchIfscCode(rs.getString("bBankBranchIfscCode"))
                        .status(rs.getString("bStatus"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsBankDetailsApplicationMap.put(bankId, wmsBankDetailsApplication);
        }
        return new ArrayList<>(wmsBankDetailsApplicationMap.values());
    }

}
