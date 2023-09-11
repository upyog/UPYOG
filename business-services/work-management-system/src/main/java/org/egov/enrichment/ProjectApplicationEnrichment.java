package org.egov.enrichment;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.egov.util.IdgenUtil;
import org.egov.web.models.AuditDetails;
import org.egov.web.models.Project;
import org.egov.web.models.Scheme;
import org.egov.web.models.WMSProjectRequest;
import org.egov.web.models.WMSSchemeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProjectApplicationEnrichment {
	
	
	@Value("${egov.idgen.proj.idname}")
    private String idGenName;
	
	@Value("${egov.idgen.proj.idformat}")
    private String idGenFormat;

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
		
		  //List<String> projectMasterIdList =idgenUtil.getIdList(wmsProjectRequest.getRequestInfo(),wmsProjectRequest.getProjectApplications().get(0).getTenantId(), idGenName,idGenFormat,wmsProjectRequest.getProjectApplications().size());
		 
		
        //Integer index = 0;
		for (Project application : wmsProjectRequest.getProjectApplications()) {
			// Enrich audit details
//	            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
//	            application.setAuditDetails(auditDetails);
			
			AuditDetails auditDetails = AuditDetails.builder().createdBy(wmsProjectRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(wmsProjectRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

			// Enrich UUID
            Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
			application.setProjectId(Long.toString(randomNumber));
			//application.setId(UUID.randomUUID().toString());
			//application.setDescOfItem(projectMasterIdList.get(index++));
			//application.setProjectStartDate(date);
			//application.setProjectEndDate(date);

			// Set application number from IdGen
			 //application.setProjectNumber(projectMasterIdList.get(index++));

			// Enrich registration Id
			// application.getAddress().setRegistrationId(application.getId());

			// Enrich address UUID
			// application.getAddress().setId(UUID.randomUUID().toString());
		}
	}

	public void enrichProjectApplicationUponUpdate(WMSProjectRequest wmsProjectRequest, List<Project> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		/*
		 * for (Project application : wmsProjectRequest.getProjectApplications()) {
		 * application.setSourceOfFund(existingApplication.get(0).getSourceOfFund());
		 * application.setProjectDescription(existingApplication.get(0).
		 * getProjectDescription());
		 * application.setDepartment(existingApplication.get(0).getDepartment());
		 * application.setProjectNumber(existingApplication.get(0).getProjectNumber());
		 * application.setStatus(existingApplication.get(0).getStatus());
		 * application.setProjectTimeline(existingApplication.get(0).getProjectTimeline(
		 * )); application.setProjectStartDate(existingApplication.get(0).
		 * getProjectStartDate());
		 * 
		 * application.setProjectEndDate(date); //
		 * application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.
		 * getRequestInfo().getUserInfo().getUuid()); }
		 */
		
		for (Project application : wmsProjectRequest.getProjectApplications()) {
			
			existingApplication.get(0).setSourceOfFund(application.getSourceOfFund());
			existingApplication.get(0).setProjectDescription(application.getProjectDescription());
			existingApplication.get(0).setDepartment(application.getDepartment());
			existingApplication.get(0).setProjectNumber(application.getProjectNumber());
			existingApplication.get(0).setStatus(application.getStatus());
			existingApplication.get(0).setProjectTimeline(application.getProjectTimeline());
			existingApplication.get(0).setProjectStartDate(application.getProjectStartDate());
			existingApplication.get(0).setProjectEndDate(date);
			
			
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
		
		
		
	}

}
