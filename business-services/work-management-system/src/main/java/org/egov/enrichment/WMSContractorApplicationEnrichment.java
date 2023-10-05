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
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WMSContractorApplicationEnrichment {
	
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

	public void enrichContractorApplication(WMSContractorRequest wmsContractorRequest) { 
		//List<String> sorIdList = idgenUtil.getIdList(wmsContractorRequest.getRequestInfo(), wmsContractorRequest.getWmsWorkApplications().get(0).getTenantId(), idGenName, idGenFormat, wmsContractorRequest.getWmsWorkApplications().size());
        //Integer index = 0;
		for (WMSContractorApplication application : wmsContractorRequest.getWmsContractorApplications()) {
			AuditDetails auditDetails = AuditDetails.builder().createdBy(wmsContractorRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(wmsContractorRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

			// Enrich UUID
			application.setVendorId((int) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000));
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

	public void enrichContractorApplicationUpdate(WMSContractorRequest wmsContractorRequest, List<WMSContractorApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSContractorApplication application : wmsContractorRequest.getWmsContractorApplications()) {
			existingApplication.get(0).setMobileNumber(application.getMobileNumber());
			existingApplication.get(0).setUIDNumber(application.getUIDNumber());
			existingApplication.get(0).setVendorName(application.getVendorName());
			existingApplication.get(0).setVatNumber(application.getVatNumber());
			existingApplication.get(0).setEPFOAccountNumber(application.getEPFOAccountNumber());
			existingApplication.get(0).setEmail(application.getEmail());
			existingApplication.get(0).setGSTNumber(application.getGSTNumber());
			existingApplication.get(0).setPANNumber(application.getPANNumber());
			existingApplication.get(0).setBankAccountNumber(application.getBankAccountNumber());
			existingApplication.get(0).setAddress(application.getAddress());
			existingApplication.get(0).setVendorType(application.getVendorType());
			existingApplication.get(0).setBankBranchIfscCode(application.getBankBranchIfscCode());
			existingApplication.get(0).setFunction(application.getFunction());
			existingApplication.get(0).setVendorClass(application.getVendorClass());
			existingApplication.get(0).setVendorSubType(application.getVendorSubType());
			existingApplication.get(0).setPrimaryAccountHead(application.getPrimaryAccountHead());
			
			existingApplication.get(0).setPFMSVendorCode(application.getPFMSVendorCode());
			existingApplication.get(0).setPayTo(application.getPayTo());
			existingApplication.get(0).setVendorStatus(application.getVendorStatus());
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
