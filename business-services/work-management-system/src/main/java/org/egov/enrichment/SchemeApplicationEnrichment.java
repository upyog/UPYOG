package org.egov.enrichment;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.egov.util.IdgenUtil;
import org.egov.web.models.Scheme;
import org.egov.web.models.WMSSchemeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchemeApplicationEnrichment {

	@Autowired
	private IdgenUtil idgenUtil;
//	    @Autowired
//	    private UserService userService;
//	    @Autowired
//	    private UserUtil userUtils;

	String pattern = "MM-dd-yyyy hh:mm:ss";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());

	public void enrichSchemeApplication(WMSSchemeRequest wmsSchemeRequest) {
		//List<String> birthRegistrationIdList = idgenUtil.getIdList(wmsSORRequest.getRequestInfo(), wmsSORRequest.getScheduleOfRateApplications().get(0).getSorId(), "pj.sor.receipt.id", "", wmsSORRequest.getScheduleOfRateApplications().size());
        //Integer index = 0;
		for (Scheme application : wmsSchemeRequest.getSchemeApplications()) {
			// Enrich audit details
//	            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
//	            application.setAuditDetails(auditDetails);

			// Enrich UUID
			application.setId((long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000));
			//application.setDescOfItem(birthRegistrationIdList.get(index++));
			application.setStartDate(date);
			application.setEndDate(date);

			// Set application number from IdGen
			// application.setApplicationNumber(birthRegistrationIdList.get(index++));

			// Enrich registration Id
			// application.getAddress().setRegistrationId(application.getId());

			// Enrich address UUID
			// application.getAddress().setId(UUID.randomUUID().toString());
		}
	}

	public void enrichSchemeApplicationUponUpdate(WMSSchemeRequest wmsSchemeRequest, List<Scheme> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (Scheme application : wmsSchemeRequest.getSchemeApplications()) {
			application.setSourceOfFund(existingApplication.get(0).getSourceOfFund());
			application.setSchemeDescription(existingApplication.get(0).getSchemeDescription());
			application.setUploadDocument(existingApplication.get(0).getUploadDocument());
			application.setFund(existingApplication.get(0).getFund());
			application.setStartDate(existingApplication.get(0).getStartDate());
			
			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
