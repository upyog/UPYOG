package org.egov.enrichment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.repository.WMSWorkRepository;
import org.egov.util.IdgenUtil;
import org.egov.web.models.AgreementInfo;
import org.egov.web.models.AuditDetails;
import org.egov.web.models.PreviousRunningBillInfo;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractAgreementApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSRunningAccountFinalBillApplication;
import org.egov.web.models.WMSRunningAccountFinalBillRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WMSRunningAccountFinalBillApplicationEnrichment {
	
	/*
	 * @Value("${egov.idgen.work.idname}") private String idGenName;
	 * 
	 * @Value("${egov.idgen.work.idformat}") private String idGenFormat;
	 */

	@Autowired
	private IdgenUtil idgenUtil;
//	    @Autowired
//	    private UserService userService;
//	    @Autowired
//	    private UserUtil userUtils;

	String pattern = "MM-dd-yyyy hh:mm:ss";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());

	public void enrichRunningAccountFinalBillApplication(WMSRunningAccountFinalBillRequest wmsRunningAccountFinalBillRequest) { 
		//List<String> sorIdList = idgenUtil.getIdList(wmsContractorRequest.getRequestInfo(), wmsContractorRequest.getWmsWorkApplications().get(0).getTenantId(), idGenName, idGenFormat, wmsContractorRequest.getWmsWorkApplications().size());
        //Integer index = 0;
		for (WMSRunningAccountFinalBillApplication application : wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()) {
			// Enrich audit details
//	            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
//	            application.setAuditDetails(auditDetails);
			AuditDetails auditDetails = AuditDetails.builder().createdBy(wmsRunningAccountFinalBillRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(wmsRunningAccountFinalBillRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

			// Enrich UUID
            Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
			application.setRunningAccountId(Long.toString(randomNumber));
			//application.setId(UUID.randomUUID().toString());
			//application.setWorkName(sorIdList.get(index++));
			//application.setStartDate(date);
			//application.setEndDate(date);

			// Set application number from IdGen
			// application.setApplicationNumber(birthRegistrationIdList.get(index++));

			// Enrich registration Id
			// application.getAddress().setRegistrationId(application.getId());

			// Enrich address UUID
			// application.getAddress().setId(UUID.randomUUID().toString());
			
			for (PreviousRunningBillInfo bill : application.getPreviousRunningBillInfo()) {
	        	 //Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
				bill.setRunningAccountId(application.getRunningAccountId());
	        	 
	        	 randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
	        	 
	        	 bill.setPrbiId(Long.toString(randomNumber));
	        	 
	         }
			
			
		}
	}


	public void enrichRunningAccountFinalBillApplicationUpdate(WMSRunningAccountFinalBillRequest wmsRunningAccountFinalBillRequest, List<WMSRunningAccountFinalBillApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSRunningAccountFinalBillApplication application : wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()) {
			 List<WMSRunningAccountFinalBillApplication> existingApplicationResult = existingApplication.stream().filter(x -> x.getRunningAccountId().equalsIgnoreCase(application.getRunningAccountId())).collect(Collectors.toList());
				
			if(existingApplicationResult.size() > 0) {
				int k=0;
				//int j=0;
				//int l=0;
				//int m=0;
				//int n=0;
				//int o=0;
				//int p=0;
			 for(k=0;k<application.getPreviousRunningBillInfo().size();k++) {
				 int currentIndex = k;
				 List<PreviousRunningBillInfo> existingApplicationBill = existingApplicationResult.get(0).getPreviousRunningBillInfo().stream().filter(x -> x.getPrbiId().equalsIgnoreCase(application.getPreviousRunningBillInfo().get(currentIndex).getPrbiId())).collect(Collectors.toList());
				
				//if (!application.getAgreementInfo().stream().anyMatch(x -> x.getAgreementName().equalsIgnoreCase(existingApplication.get(0).getAgreementInfo().get(i).getAgreementName()))) {
				 if(existingApplicationBill.size() > 0) {
					 existingApplicationBill.get(0).setRunningAccountBillDate(application.getPreviousRunningBillInfo().get(k).getRunningAccountBillDate());
					 existingApplicationBill.get(0).setRunningAccountBillNo(application.getPreviousRunningBillInfo().get(k).getRunningAccountBillNo());
					 existingApplicationBill.get(0).setRunningAccountBillAmount(application.getPreviousRunningBillInfo().get(k).getRunningAccountBillAmount());
				
					 existingApplicationBill.get(0).setTaxAmount(application.getPreviousRunningBillInfo().get(k).getTaxAmount());
					 existingApplicationBill.get(0).setRemark(application.getPreviousRunningBillInfo().get(k).getRemark());
					 
				
				}
			}
			 	
			
			
			
			
			
			
			
			
			
			
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
}
	
