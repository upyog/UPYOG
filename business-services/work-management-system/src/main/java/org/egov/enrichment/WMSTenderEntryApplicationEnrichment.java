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
import org.egov.web.models.WMSTenderEntryApplication;
import org.egov.web.models.WMSTenderEntryRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WMSTenderEntryApplicationEnrichment {
	
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

	public void enrichTenderEntryApplication(WMSTenderEntryRequest wmsTenderEntryRequest) { 
		//List<String> sorIdList = idgenUtil.getIdList(wmsContractorRequest.getRequestInfo(), wmsContractorRequest.getWmsWorkApplications().get(0).getTenantId(), idGenName, idGenFormat, wmsContractorRequest.getWmsWorkApplications().size());
        //Integer index = 0;
		for (WMSTenderEntryApplication application : wmsTenderEntryRequest.getWmsTenderEntryApplications()) {
			// Enrich audit details
//	            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
//	            application.setAuditDetails(auditDetails);
			AuditDetails auditDetails = AuditDetails.builder().createdBy(wmsTenderEntryRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(wmsTenderEntryRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

			// Enrich UUID
			Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
			application.setTenderId(Long.toString(randomNumber));
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

	public void enrichTenderEntryApplicationUpdate(WMSTenderEntryRequest wmsTenderEntryRequest, List<WMSTenderEntryApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSTenderEntryApplication application : wmsTenderEntryRequest.getWmsTenderEntryApplications()) {
			existingApplication.get(0).setDepartmentName(application.getDepartmentName());
			existingApplication.get(0).setRequestCategory(application.getRequestCategory());
			existingApplication.get(0).setProjectName(application.getProjectName());
			existingApplication.get(0).setResolutionNo(application.getResolutionNo());
			existingApplication.get(0).setResolutionDate(application.getResolutionDate());
			existingApplication.get(0).setPrebidMeetingDate(application.getPrebidMeetingDate());
			existingApplication.get(0).setPrebidMeetingLocation(application.getPrebidMeetingLocation());
			existingApplication.get(0).setIssueFromDate(application.getIssueFromDate());
			existingApplication.get(0).setIssueTillDate(application.getIssueTillDate());
			existingApplication.get(0).setPublishDate(application.getPublishDate());
			existingApplication.get(0).setTechnicalBidOpenDate(application.getTechnicalBidOpenDate());
			existingApplication.get(0).setFinancialBidOpenDate(application.getFinancialBidOpenDate());
			existingApplication.get(0).setValidity(application.getValidity());
			existingApplication.get(0).setUploadDocument(application.getUploadDocument());
			/*
			 * existingApplication.get(0).setWorkNo(application.getWorkNo());
			 * existingApplication.get(0).setWorkDescription(application.getWorkDescription(
			 * ));
			 * existingApplication.get(0).setEstimatedCost(application.getEstimatedCost());
			 * existingApplication.get(0).setTenderType(application.getTenderType());
			 * existingApplication.get(0).setTenderFee(application.getTenderFee());
			 * existingApplication.get(0).setEmd(application.getEmd());
			 * existingApplication.get(0).setVendorClass(application.getVendorClass());
			 * existingApplication.get(0).setWorkDuration(application.getWorkDuration());
			 */
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
