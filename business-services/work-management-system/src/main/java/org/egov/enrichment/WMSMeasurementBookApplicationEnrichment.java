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
import org.egov.web.models.WMSMeasurementBookApplication;
import org.egov.web.models.WMSMeasurementBookRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WMSMeasurementBookApplicationEnrichment {
	
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

	public void enrichMeasurementBookApplication(WMSMeasurementBookRequest wmsMeasurementBookRequest) { 
		//List<String> sorIdList = idgenUtil.getIdList(wmsContractorRequest.getRequestInfo(), wmsContractorRequest.getWmsWorkApplications().get(0).getTenantId(), idGenName, idGenFormat, wmsContractorRequest.getWmsWorkApplications().size());
        //Integer index = 0;
		for (WMSMeasurementBookApplication application : wmsMeasurementBookRequest.getWmsMeasurementBookApplications()) {
			// Enrich audit details
//	            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
//	            application.setAuditDetails(auditDetails);
			
			AuditDetails auditDetails = AuditDetails.builder().createdBy(wmsMeasurementBookRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(wmsMeasurementBookRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

			// Enrich UUID
            Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
			application.setMeasurementBookId(Long.toString(randomNumber));
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

	public void enrichMeasurementBookApplicationUpdate(WMSMeasurementBookRequest wmsMeasurementBookRequest, List<WMSMeasurementBookApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSMeasurementBookApplication application : wmsMeasurementBookRequest.getWmsMeasurementBookApplications()) {
			existingApplication.get(0).setWorkOrderNo(application.getWorkOrderNo());
			existingApplication.get(0).setContractorName(application.getContractorName());
			existingApplication.get(0).setWorkName(application.getWorkName());
			existingApplication.get(0).setMeasurementBookNo(application.getMeasurementBookNo());
			existingApplication.get(0).setStatus(application.getStatus());
			existingApplication.get(0).setAgreementNo(application.getAgreementNo());
			existingApplication.get(0).setProjectName(application.getProjectName());
			existingApplication.get(0).setWorkOrderAmount(application.getWorkOrderAmount());
			existingApplication.get(0).setWorkOrderDate(application.getWorkOrderDate());
			existingApplication.get(0).setMeasurementDate(application.getMeasurementDate());
			existingApplication.get(0).setDescriptionOfMb(application.getDescriptionOfMb());
			existingApplication.get(0).setJeName(application.getJeName());
			existingApplication.get(0).setChapter(application.getChapter());
			existingApplication.get(0).setItemNo(application.getItemNo());
			existingApplication.get(0).setDescriptionOfTheItem(application.getDescriptionOfTheItem());
			existingApplication.get(0).setEstimatedQuantity(application.getEstimatedQuantity());
			existingApplication.get(0).setCumulativeQuantity(application.getCumulativeQuantity());
			existingApplication.get(0).setUnit(application.getUnit());
			existingApplication.get(0).setRate(application.getRate());
			existingApplication.get(0).setConsumedQuantity(application.getConsumedQuantity());
			existingApplication.get(0).setAmount(application.getAmount());
			existingApplication.get(0).setAddMb(application.getAddMb());
			existingApplication.get(0).setItemDescription(application.getItemDescription());
			existingApplication.get(0).setNos(application.getNos());
			existingApplication.get(0).setL(application.getL());
			existingApplication.get(0).setBw(application.getBw());
			existingApplication.get(0).setDh(application.getDh());
			existingApplication.get(0).setItemCode(application.getItemCode());
			existingApplication.get(0).setDescription(application.getDescription());
			existingApplication.get(0).setCommulativeQuantity(application.getCommulativeQuantity());
			existingApplication.get(0).setRemark(application.getRemark());
			existingApplication.get(0).setOverheadDescription(application.getOverheadDescription());
			existingApplication.get(0).setValueType(application.getValueType());
			existingApplication.get(0).setEstimatedAmount(application.getEstimatedAmount());
			existingApplication.get(0).setActualAmount(application.getActualAmount());
			existingApplication.get(0).setDocumentDescription(application.getDocumentDescription());
			
			
			
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
