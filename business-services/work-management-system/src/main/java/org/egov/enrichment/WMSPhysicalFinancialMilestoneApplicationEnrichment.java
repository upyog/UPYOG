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
import org.egov.web.models.WMSPhysicalFinancialMilestoneApplication;
import org.egov.web.models.WMSPhysicalFinancialMilestoneRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WMSPhysicalFinancialMilestoneApplicationEnrichment {
	
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

	public void enrichPhysicalFinancialMilestoneApplication(WMSPhysicalFinancialMilestoneRequest wmsPhysicalFinancialMilestoneRequest) { 
		//List<String> sorIdList = idgenUtil.getIdList(wmsContractorRequest.getRequestInfo(), wmsContractorRequest.getWmsWorkApplications().get(0).getTenantId(), idGenName, idGenFormat, wmsContractorRequest.getWmsWorkApplications().size());
        //Integer index = 0;
		for (WMSPhysicalFinancialMilestoneApplication application : wmsPhysicalFinancialMilestoneRequest.getWmsPhysicalFinancialMilestoneApplications()) {
			// Enrich audit details
//	            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
//	            application.setAuditDetails(auditDetails);
			AuditDetails auditDetails = AuditDetails.builder().createdBy(wmsPhysicalFinancialMilestoneRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(wmsPhysicalFinancialMilestoneRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

			// Enrich UUID
            Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
			application.setMilestoneId(Long.toString(randomNumber));
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

	public void enrichPhysicalFinancialMilestoneApplicationUpdate(WMSPhysicalFinancialMilestoneRequest wmsPhysicalFinancialMilestoneRequest, List<WMSPhysicalFinancialMilestoneApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSPhysicalFinancialMilestoneApplication application : wmsPhysicalFinancialMilestoneRequest.getWmsPhysicalFinancialMilestoneApplications()) {
			existingApplication.get(0).setProjectName(application.getProjectName());
			existingApplication.get(0).setWorkName(application.getWorkName());
			existingApplication.get(0).setMilestoneName(application.getMilestoneName());
			//existingApplication.get(0).setSrNo(application.getSrNo());
			//existingApplication.get(0).setActivityDescription(application.getActivityDescription());
			existingApplication.get(0).getPhysicalMileStoneActivity().setPercentageWeightage(application.getPhysicalMileStoneActivity().getPercentageWeightage());
			existingApplication.get(0).getPhysicalMileStoneActivity().setDescriptionOfTheItem(application.getPhysicalMileStoneActivity().getDescriptionOfTheItem());
			existingApplication.get(0).getPhysicalMileStoneActivity().setStartDate(application.getPhysicalMileStoneActivity().getStartDate());
			existingApplication.get(0).getPhysicalMileStoneActivity().setEndDate(application.getPhysicalMileStoneActivity().getEndDate());
			/*
			 * existingApplication.get(0).setPlannedStartDate(application.
			 * getPlannedStartDate());
			 * existingApplication.get(0).setPlannedEndDate(application.getPlannedEndDate())
			 * ;
			 * existingApplication.get(0).setTotalWeightage(application.getTotalWeightage())
			 * ; existingApplication.get(0).setMilestoneDescription(application.
			 * getMilestoneDescription());
			 * existingApplication.get(0).setActualStartDate(application.getActualStartDate(
			 * ));
			 * existingApplication.get(0).setActualEndDate(application.getActualEndDate());
			 * existingApplication.get(0).setProgressUpdateDate(application.
			 * getProgressUpdateDate());
			 * existingApplication.get(0).setCompletedPercentage(application.
			 * getCompletedPercentage());
			 */
			
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
