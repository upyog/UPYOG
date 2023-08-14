package org.egov.enrichment;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.egov.util.IdgenUtil;
import org.egov.web.models.Project;
import org.egov.web.models.Scheme;
import org.egov.web.models.WMSProjectRequest;
import org.egov.web.models.WMSSchemeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProjectApplicationEnrichment {

	@Autowired
	private IdgenUtil idgenUtil;
//	    @Autowired
//	    private UserService userService;
//	    @Autowired
//	    private UserUtil userUtils;

	String pattern = "MM-dd-yyyy hh:mm:ss";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());

	public void enrichProjectApplication(WMSProjectRequest wmsProjectRequest) {
		List<String> projectMasterIdList = idgenUtil.getIdList(wmsProjectRequest.getRequestInfo(), wmsProjectRequest.getProjectApplications().get(0).getProjectNumber(),"wms.projectnumber","", wmsProjectRequest.getProjectApplications().size());
        Integer index = 0;
		for (Project application : wmsProjectRequest.getProjectApplications()) {
			// Enrich audit details
//	            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
//	            application.setAuditDetails(auditDetails);

			// Enrich UUID
			application.setProjectId((long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000));
			//application.setDescOfItem(projectMasterIdList.get(index++));
			application.setProjectStartDate(date);
			application.setProjectEndDate(date);

			// Set application number from IdGen
			 application.setProjectNumber(projectMasterIdList.get(index++));

			// Enrich registration Id
			// application.getAddress().setRegistrationId(application.getId());

			// Enrich address UUID
			// application.getAddress().setId(UUID.randomUUID().toString());
		}
	}

	public void enrichProjectApplicationUponUpdate(WMSProjectRequest wmsProjectRequest, List<Project> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (Project application : wmsProjectRequest.getProjectApplications()) {
			application.setSourceOfFund(existingApplication.get(0).getSourceOfFund());
			application.setProjectDescription(existingApplication.get(0).getProjectDescription());
			application.setDepartment(existingApplication.get(0).getDepartment());
			application.setProjectNumber(existingApplication.get(0).getProjectNumber());
			application.setStatus(existingApplication.get(0).getStatus());
			application.setProjectTimeline(existingApplication.get(0).getProjectTimeline());
			application.setProjectStartDate(existingApplication.get(0).getProjectStartDate());
			
			application.setProjectEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
