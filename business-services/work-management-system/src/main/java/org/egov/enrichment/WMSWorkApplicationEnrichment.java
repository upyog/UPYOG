package org.egov.enrichment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.egov.repository.WMSWorkRepository;
import org.egov.util.IdgenUtil;
import org.egov.web.models.AuditDetails;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WMSWorkApplicationEnrichment {
	
	@Value("${egov.idgen.work.idname}")
    private String idGenName;
	
	@Value("${egov.idgen.work.idformat}")
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

	public void enrichWorkApplication(WMSWorkRequest wmsWorkRequest) { 
		List<String> sorIdList = idgenUtil.getIdList(wmsWorkRequest.getRequestInfo(), wmsWorkRequest.getWmsWorkApplications().get(0).getTenantId(), idGenName, idGenFormat, wmsWorkRequest.getWmsWorkApplications().size());
        Integer index = 0;
		for (WMSWorkApplication application : wmsWorkRequest.getWmsWorkApplications()) {
			// Enrich audit details
	            AuditDetails auditDetails = AuditDetails.builder().createdBy(wmsWorkRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(wmsWorkRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
	            application.setAuditDetails(auditDetails);

			// Enrich UUID
			application.setWorkId(UUID.randomUUID().toString());
			application.setWorkName(sorIdList.get(index++));
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

	public void enrichWorkApplicationUpdate(WMSWorkRequest wmsWorkRequest, List<WMSWorkApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSWorkApplication application : wmsWorkRequest.getWmsWorkApplications()) {
			application.setProjectId(existingApplication.get(0).getProjectId());
			application.setWorkName(existingApplication.get(0).getWorkName());
			application.setStartLocation(existingApplication.get(0).getStartLocation());
			application.setEndLocation(existingApplication.get(0).getEndLocation());
			application.setFinancialYear(existingApplication.get(0).getFinancialYear());
			application.setBudgetHead(existingApplication.get(0).getBudgetHead());
			
//			application.setEndDate(date);
			 application.getAuditDetails().setLastModifiedBy(wmsWorkRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
