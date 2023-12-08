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
		}
	}

	public void enrichRunningAccountFinalBillApplicationUpdate(WMSRunningAccountFinalBillRequest wmsRunningAccountFinalBillRequest, List<WMSRunningAccountFinalBillApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSRunningAccountFinalBillApplication application : wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()) {
			existingApplication.get(0).setProjectName(application.getProjectName());
			existingApplication.get(0).setWorkName(application.getWorkName());
			existingApplication.get(0).setMbNo(application.getMbNo());
			existingApplication.get(0).setMbDate(application.getMbDate());
			existingApplication.get(0).setMbAmount(application.getMbAmount());
			existingApplication.get(0).setEstimatedCost(application.getEstimatedCost());
			existingApplication.get(0).setTenderType(application.getTenderType());
			existingApplication.get(0).setValue(application.getValue());
			existingApplication.get(0).setPercentageType(application.getPercentageType());
			existingApplication.get(0).setAwardAmount(application.getAwardAmount());
			existingApplication.get(0).setBillDate(application.getBillDate());
			existingApplication.get(0).setBillNo(application.getBillNo());
			existingApplication.get(0).setBillAmount(application.getBillAmount());
			existingApplication.get(0).setDeductionAmount(application.getDeductionAmount());
			existingApplication.get(0).setRemark(application.getRemark());
			existingApplication.get(0).setSrNo(application.getSrNo());
			existingApplication.get(0).setDeductionDescription(application.getDeductionDescription());
			existingApplication.get(0).setAdditionDeduction(application.getAdditionDeduction());
			existingApplication.get(0).setCalculationMethod(application.getCalculationMethod());
			existingApplication.get(0).setPercentage(application.getPercentage());
			existingApplication.get(0).setPercentageValue(application.getPercentageValue());
			existingApplication.get(0).setTaxAmount(application.getTaxAmount());
			existingApplication.get(0).setTaxCategory(application.getTaxCategory());
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
