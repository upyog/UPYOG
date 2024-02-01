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
import org.egov.web.models.WMSProjectRegisterApplication;
import org.egov.web.models.WMSProjectRegisterRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WMSProjectRegisterApplicationEnrichment {
	
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

	public void enrichProjectRegisterApplication(WMSProjectRegisterRequest wmsProjectRegisterRequest) { 
		//List<String> sorIdList = idgenUtil.getIdList(wmsContractorRequest.getRequestInfo(), wmsContractorRequest.getWmsWorkApplications().get(0).getTenantId(), idGenName, idGenFormat, wmsContractorRequest.getWmsWorkApplications().size());
        //Integer index = 0;
		for (WMSProjectRegisterApplication application : wmsProjectRegisterRequest.getWmsProjectRegisterApplications()) {
			AuditDetails auditDetails = AuditDetails.builder().createdBy(wmsProjectRegisterRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(wmsProjectRegisterRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

			// Enrich UUID
            Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
			application.setRegisterId(Long.toString(randomNumber));
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

	public void enrichProjectRegisterApplicationUpdate(WMSProjectRegisterRequest wmsProjectRegisterRequest, List<WMSProjectRegisterApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSProjectRegisterApplication application : wmsProjectRegisterRequest.getWmsProjectRegisterApplications()) {
			existingApplication.get(0).setBillReceivedTillDate(application.getBillReceivedTillDate());
			existingApplication.get(0).setEstimatedNumber(application.getEstimatedNumber());
			existingApplication.get(0).setEstimatedWorkCost(application.getEstimatedWorkCost());
			//existingApplication.get(0).setPaymentReceivedTillDate(application.getPaymentReceivedTillDate());
			existingApplication.get(0).setProjectName(application.getProjectName());
			existingApplication.get(0).setSanctionedTenderAmount(application.getSanctionedTenderAmount());
			existingApplication.get(0).setStatusName(application.getStatusName());
			existingApplication.get(0).setWorkName(application.getWorkName());
			existingApplication.get(0).setWorkType(application.getWorkType());
			existingApplication.get(0).setSchemeName(application.getSchemeName());
			
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
