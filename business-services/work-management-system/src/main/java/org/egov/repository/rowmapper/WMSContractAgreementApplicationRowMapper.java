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
import org.egov.web.models.WMSContractAgreementApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSContractAgreementApplicationRowMapper implements ResultSetExtractor<List<WMSContractAgreementApplication>> {
    public List<WMSContractAgreementApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSContractAgreementApplication> wmsContractAgreementApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String agreementNo = rs.getString("aAgreementNo");
            WMSContractAgreementApplication wmsContractAgreementApplication = wmsContractAgreementApplicationMap.get(agreementNo);

            if(wmsContractAgreementApplication == null) {

            	Long lastModifiedTime = rs.getLong("aLastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("aCreatedBy"))
                        .createdTime(rs.getLong("aCreatedtime"))
                        .lastModifiedBy(rs.getString("aLastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                wmsContractAgreementApplication = WMSContractAgreementApplication.builder()
                        .agreementNo(rs.getString("aAgreementNo"))
                        .agreementName(rs.getString("aAgreementName"))
                        .agreementDate(rs.getString("aAgreementDate"))
                        .departmentName(rs.getString("aDepartmentName"))
                        .loaNo(rs.getString("aLoaNo"))
                        .resolutionNo(rs.getInt("aResolutionNo"))
                        .resolutionDate(rs.getString("aResolutionDate"))
                        .tenderNo(rs.getInt("aTenderNo"))
                        .tenderDate(rs.getString("aTenderDate"))
                        .agreementType(rs.getString("aAgreementType"))
                        .defectLiabilityPeriod(rs.getString("aDefectLiabilityPeriod"))
                        .contractPeriod(rs.getString("aContractPeriod"))
                        .agreementAmount(rs.getInt("aAgreementAmount"))
                        .paymentType(rs.getString("aPaymentType"))
                        .depositType(rs.getString("aDepositType"))
                        .depositAmount(rs.getInt("aDepositAmount"))
                        .workDescription(rs.getString("aWorkDescription"))
                        .accountNo(rs.getLong("aAccountNo"))
                        .particulars(rs.getString("aParticulars"))
                        .validFromDate(rs.getString("aValidFromDate"))
                        .validTillDate(rs.getString("aValidTillDate"))
                        .bankBranchIfscCode(rs.getString("aBankBranchIfscCode"))
                        .paymentMode(rs.getString("aPaymentMode"))
                        .designation(rs.getString("aDesignation"))
                        .employeeName(rs.getString("aEmployeeName"))
                        .witnessName(rs.getString("aWitnessName"))
                        .address(rs.getString("aAddress"))
                        .uid(rs.getString("aUid"))
                        .vendorType(rs.getString("aVendorType"))
                        .vendorName(rs.getString("aVendorName"))
                        .representedBy(rs.getString("aRepresentedBy"))
                        .primaryParty(rs.getString("aPrimaryParty"))
                        .srNo(rs.getInt("aSrNo"))
                        .termsAndConditions(rs.getString("aTermsAndConditions"))
                        .documentDescription(rs.getString("aDocumentDescription"))
                        .uploadDocument(rs.getString("aUploadDocument"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            wmsContractAgreementApplicationMap.put(agreementNo, wmsContractAgreementApplication);
        }
        return new ArrayList<>(wmsContractAgreementApplicationMap.values());
    }

}
