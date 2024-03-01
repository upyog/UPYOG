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
import org.egov.web.models.Party2Witness;
import org.egov.web.models.PreviousRunningBillInfo;
import org.egov.web.models.SDPGBGDetails;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.TermsAndConditions;
import org.egov.web.models.WMSContractAgreementApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSRunningAccountFinalBillApplication;
import org.egov.web.models.WMSWorkApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class WMSRunningAccountFinalBillApplicationRowMapper implements ResultSetExtractor<List<WMSRunningAccountFinalBillApplication>> {
    public List<WMSRunningAccountFinalBillApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,WMSRunningAccountFinalBillApplication> wmsRunningAccountFinalBillApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
        	
        	
        	String runningAccountId = rs.getString("bRunningAccountId");
        	WMSRunningAccountFinalBillApplication wmsRunningAccountFinalBillApplication = wmsRunningAccountFinalBillApplicationMap.get(runningAccountId);
            if(wmsRunningAccountFinalBillApplication == null) {
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
            
            
            
            
            
            //Party2Details party2Details = Party2Details.builder()
                    
                    
            
           
            
            
            
            
    		
            wmsRunningAccountFinalBillApplication = WMSRunningAccountFinalBillApplication.builder()
    		  .runningAccountId(rs.getString("bRunningAccountId"))
    		  .auditDetails(auditdetails)
    		  .percentageType(rs.getString("bPercentageType"))
    		  .deductionAmount(rs.getInt("bDeductionAmount"))
    		  .deductionDescription(rs.getString("bDeductionDescription"))
    		  .calculationMethod(rs.getString("bCalculationMethod"))
    		  .build();
    		  
    		  wmsRunningAccountFinalBillApplicationMap.put(runningAccountId, wmsRunningAccountFinalBillApplication);
            }// if null
        	
            
                
               
                
                
               /* Party1Details party1Details = Party1Details.builder()
                		.party1Id(rs.getString("aParty1Id"))
                		.agreementNo(agreementNo)
                        .departmentNameParty1(rs.getString("aDepartmentNameParty1"))
                        .designation(rs.getString("aDesignation"))
                        .employeeName(rs.getString("aEmployeeName"))
                        .witnessNameP1(rs.getString("aWitnessNameP1"))
                        .addressP1(rs.getString("aAddressP1"))
                        .uidP1(rs.getString("aUidP1"))
                        .build();
                wmsRunningAccountFinalBill.addParty1Details(party1Details);*/
                
                
                
                
                
                
                
                
                
            PreviousRunningBillInfo previousRunningBillInfo=PreviousRunningBillInfo.builder()
                		.prbiId(rs.getString("bPrbiId"))
                		.runningAccountId(runningAccountId)
                		.runningAccountBillDate(rs.getString("bRunningAccountBillDate"))
                        .runningAccountBillNo(rs.getString("bRunningAccountBillNo"))
                        .runningAccountBillAmount(rs.getString("bRunningAccountBillAmount"))
                        .taxAmount(rs.getString("bTaxAmount"))
                        .remark(rs.getString("bRemark"))
                        .build();
                
            wmsRunningAccountFinalBillApplication.addRunningAccountFinalBill(previousRunningBillInfo);
        }
                
            //rs next
        return new ArrayList<>(wmsRunningAccountFinalBillApplicationMap.values());
    }

    }

