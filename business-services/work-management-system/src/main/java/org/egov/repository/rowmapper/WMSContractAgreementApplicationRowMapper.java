package org.egov.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.web.models.AgreementDocuments;
import org.egov.web.models.AgreementInfo;
import org.egov.web.models.AuditDetails;
import org.egov.web.models.Contractors;
import org.egov.web.models.Party1Details;
import org.egov.web.models.Party2Details;
import org.egov.web.models.Party2Witness;
import org.egov.web.models.SDPGBGDetails;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.TermsAndConditions;
import org.egov.web.models.WMSContractAgreementApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

@Component
public class WMSContractAgreementApplicationRowMapper implements ResultSetExtractor<List<WMSContractAgreementApplication>> {
    public List<WMSContractAgreementApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSContractAgreementApplication> wmsContractAgreementApplicationMap = new LinkedHashMap<>();
        //List<WMSContractAgreementApplication> wmsContractAgreementApplicationList=new ArrayList<>();
    	//ListMultimap<String,WMSContractAgreementApplication> wmsContractAgreementApplicationMap = ArrayListMultimap.create();
        
        //if (rs.next()){
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
            
            
            
			/*
			 * TermsAndConditions termsAndConditions = TermsAndConditions.builder()
			 * .srNo(rs.getInt("aSrNo"))
			 * .termsAndConditions(rs.getString("aTermsAndConditions")) .build();
			 */
            //Party2Details party2Details = Party2Details.builder()
                    
                    
            
			/*
			 * Contractors contractors=Contractors.builder()
			 * .vendorType(rs.getString("aVendorType"))
			 * .vendorName(rs.getString("aVendorName"))
			 * .representedBy(rs.getString("aRepresentedBy"))
			 * .primaryParty(rs.getString("aPrimaryParty")) .build();
			 */
            
			/*
			 * Party2Witness party2Witness=Party2Witness.builder()
			 * .witnessNameP2(rs.getString("aWitnessNameP2"))
			 * .addressP2(rs.getString("aAddressP2")) .uidP2(rs.getString("aUidP2"))
			 * .build();
			 */
            
			/*
			 * AgreementDocuments agreementDocuments = AgreementDocuments.builder()
			 * .documentDescription(rs.getString("aDocumentDescription"))
			 * .uploadDocument(rs.getString("aUploadDocument")) .build();
			 */
    		
    		  wmsContractAgreementApplication = WMSContractAgreementApplication.builder()
    		  .agreementNo(rs.getString("aAgreementNo"))
    		  .auditDetails(auditdetails)
    		  .build();
    		  
    		  wmsContractAgreementApplicationMap.put(agreementNo, wmsContractAgreementApplication);
            }// if null
        	
            
                AgreementInfo agreementInfo = AgreementInfo.builder()
                		.agrId(rs.getString("aAgrId"))
                		.agreementNo(agreementNo)
                        .agreementName(rs.getString("aAgreementName"))
                        .agreementDate(rs.getString("aAgreementDate"))
                        .departmentNameAi(rs.getString("aDepartmentNameAi"))
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
                        .build();
                wmsContractAgreementApplication.addAgreementInfoItem(agreementInfo);
               
                //wmsContractAgreementApplicationList.addAll(wmsContractAgreementApplicationMap.values());
                
                Party1Details party1Details = Party1Details.builder()
                		.party1Id(rs.getString("aParty1Id"))
                		.agreementNo(agreementNo)
                        .departmentNameParty1(rs.getString("aDepartmentNameParty1"))
                        .designation(rs.getString("aDesignation"))
                        .employeeName(rs.getString("aEmployeeName"))
                        .witnessNameP1(rs.getString("aWitnessNameP1"))
                        .addressP1(rs.getString("aAddressP1"))
                        .uidP1(rs.getString("aUidP1"))
                        .build();
                wmsContractAgreementApplication.addParty1Details(party1Details);
                
                SDPGBGDetails sDPGBGDetails = SDPGBGDetails.builder()
                		.sdpgId(rs.getString("aSdpgId"))
                		.agreementNo(agreementNo)
                		.depositType(rs.getString("aDepositType"))
                		.depositAmount(rs.getInt("aDepositAmount"))
                        .accountNo(rs.getLong("aAccountNo"))
                        .particulars(rs.getString("aParticulars"))
                        .validFromDate(rs.getString("aValidFromDate"))
                        .validTillDate(rs.getString("aValidTillDate"))
                        .bankBranchIfscCode(rs.getString("aBankBranchIfscCode"))
                        .paymentMode(rs.getString("aPaymentMode"))
                        .build();
                
                wmsContractAgreementApplication.addSDPGBGDetails(sDPGBGDetails);
                
                TermsAndConditions termsAndConditions = TermsAndConditions.builder()
                		.tncId(rs.getString("aTncId"))
                		.agreementNo(agreementNo)
                        .srNo(rs.getInt("aSrNo"))
                        .termsAndConditions(rs.getString("aTermsAndConditions"))
                        .build();
                
                wmsContractAgreementApplication.addTermsAndConditions(termsAndConditions);
              
                //wmsContractAgreementApplicationList.addAll(wmsContractAgreementApplicationMap.values());
                
                AgreementDocuments agreementDocuments = AgreementDocuments.builder()
                		.adId(rs.getString("aAdId"))
                		.agreementNo(agreementNo)
                        .documentDescription(rs.getString("aDocumentDescription"))
                        .uploadDocument(rs.getString("aUploadDocument"))
                        .build();
                wmsContractAgreementApplication.addAgreementDocuments(agreementDocuments);
                
                Party2Witness party2Witness=Party2Witness.builder()
                		.pwId(rs.getString("aPwId"))
                		.agreementNo(agreementNo)
                		.witnessNameP2(rs.getString("aWitnessNameP2"))
                        .addressP2(rs.getString("aAddressP2"))
                        .uidP2(rs.getString("aUidP2"))
                        .build();
                
                wmsContractAgreementApplication.addParty2Witness(party2Witness);
                
                Contractors contractors=Contractors.builder()
                		.conId(rs.getString("aConId"))
                		.agreementNo(agreementNo)
                		.vendorType(rs.getString("aVendorType"))
                        .vendorName(rs.getString("aVendorName"))
                        .representedBy(rs.getString("aRepresentedBy"))
                        .primaryParty(rs.getString("aPrimaryParty"))
                        .build();
                
                wmsContractAgreementApplication.addContractors(contractors);
                
            }//rs
                
        
                
                
               
                
                
                
				 
                
				
            //}//null
            //addChildrenToProperty(rs, sorApplication);
			/*
			 * wmsContractAgreementApplicationMap.put(agreementNo,
			 * wmsContractAgreementApplication);
			 * wmsContractAgreementApplicationList.addAll(wmsContractAgreementApplicationMap
			 * .values());
			 */
            //wmsContractAgreementApplicationMap.merge(agreementNo, wmsContractAgreementApplication, null);
        //}//if rs
        return new ArrayList<>(wmsContractAgreementApplicationMap.values());
        //return wmsContractAgreementApplicationList;
    }

}
