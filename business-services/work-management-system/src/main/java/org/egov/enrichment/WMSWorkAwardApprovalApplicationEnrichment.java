package org.egov.enrichment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.egov.repository.WMSWorkRepository;
import org.egov.util.IdgenUtil;
import org.egov.web.models.AuditDetails;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkAwardApprovalApplication;
import org.egov.web.models.WMSWorkAwardApprovalRequest;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WMSWorkAwardApprovalApplicationEnrichment {
	
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

	public void enrichWorkAwardApprovalApplication(WMSWorkAwardApprovalRequest wmsWorkAwardApprovalRequest) { 
		//List<String> sorIdList = idgenUtil.getIdList(wmsContractorRequest.getRequestInfo(), wmsContractorRequest.getWmsWorkApplications().get(0).getTenantId(), idGenName, idGenFormat, wmsContractorRequest.getWmsWorkApplications().size());
        //Integer index = 0;
		for (WMSWorkAwardApprovalApplication application : wmsWorkAwardApprovalRequest.getWmsWorkAwardApprovalApplications()) {
			// Enrich audit details
//	            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
//	            application.setAuditDetails(auditDetails);
			AuditDetails auditDetails = AuditDetails.builder().createdBy(wmsWorkAwardApprovalRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(wmsWorkAwardApprovalRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

			// Enrich UUID
            Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
			application.setWorkAwardId(Long.toString(randomNumber));
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
		}
	}

	public void enrichWorkAwardApprovalApplicationUpdate(WMSWorkAwardApprovalRequest wmsWorkAwardApprovalRequest, List<WMSWorkAwardApprovalApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSWorkAwardApprovalApplication application : wmsWorkAwardApprovalRequest.getWmsWorkAwardApprovalApplications()) {
			existingApplication.get(0).setWorkName(application.getWorkName());
			existingApplication.get(0).setWorkNo(application.getWorkNo());
			existingApplication.get(0).setPercentageType(application.getPercentageType());
			existingApplication.get(0).setQuotedPercentage(application.getQuotedPercentage());
			existingApplication.get(0).setAcceptedWorkCost(application.getAcceptedWorkCost());
			existingApplication.get(0).setContractorName(application.getContractorName());
			existingApplication.get(0).setNoOfDaysForAgreement(application.getNoOfDaysForAgreement());
			existingApplication.get(0).setLoaGeneration(application.getLoaGeneration());
			existingApplication.get(0).setAwardDate(application.getAwardDate());
			existingApplication.get(0).setDocumentUpload(application.getDocumentUpload());
			existingApplication.get(0).setAwardStatus(application.getAwardStatus());
			
//			
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
