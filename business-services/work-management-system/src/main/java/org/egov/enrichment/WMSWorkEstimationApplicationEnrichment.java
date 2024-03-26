package org.egov.enrichment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.egov.repository.WMSWorkRepository;
import org.egov.util.IdgenUtil;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkEstimationApplication;
import org.egov.web.models.WMSWorkEstimationRequest;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WMSWorkEstimationApplicationEnrichment {
	
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

	public void enrichWorkEstimationApplication(WMSWorkEstimationRequest wmsWorkEstimationRequest) { 
		//List<String> sorIdList = idgenUtil.getIdList(wmsContractorRequest.getRequestInfo(), wmsContractorRequest.getWmsWorkApplications().get(0).getTenantId(), idGenName, idGenFormat, wmsContractorRequest.getWmsWorkApplications().size());
        //Integer index = 0;
		for (WMSWorkEstimationApplication application : wmsWorkEstimationRequest.getWmsWorkEstimationApplications()) {
			// Enrich audit details
//	            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
//	            application.setAuditDetails(auditDetails);

			// Enrich UUID
            Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
			application.setEstimateId(Long.toString(randomNumber));
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

	public void enrichWorkEstimationApplicationUpdate(WMSWorkEstimationRequest wmsWorkEstimationRequest, List<WMSWorkEstimationApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSWorkEstimationApplication application : wmsWorkEstimationRequest.getWmsWorkEstimationApplications()) {
			existingApplication.get(0).setWorkEstimationNo(application.getWorkEstimationNo());
			existingApplication.get(0).setProjectName(application.getProjectName());
			existingApplication.get(0).setWorkName(application.getWorkName());
			existingApplication.get(0).setFromDate(application.getFromDate());
			existingApplication.get(0).setToDate(application.getToDate());
			existingApplication.get(0).setEstimateType(application.getEstimateType());
			existingApplication.get(0).setSorName(application.getSorName());
			existingApplication.get(0).setDownloadTemplate(application.getDownloadTemplate());
			existingApplication.get(0).setUploadTemplate(application.getUploadTemplate());
			existingApplication.get(0).setChapter(application.getChapter());
			existingApplication.get(0).setItemNo(application.getItemNo());
			existingApplication.get(0).setDescriptionOfTheItem(application.getDescriptionOfTheItem());
			existingApplication.get(0).setLength(application.getLength());
			existingApplication.get(0).setBw(application.getBw());
			existingApplication.get(0).setDh(application.getDh());
			existingApplication.get(0).setNoS(application.getNoS());
			existingApplication.get(0).setQuantity(application.getQuantity());
			existingApplication.get(0).setUnit(application.getUnit());
			existingApplication.get(0).setRate(application.getRate());
			existingApplication.get(0).setEstimateAmount(application.getEstimateAmount());
			existingApplication.get(0).setSerialNo(application.getSerialNo());
			existingApplication.get(0).setParticularsOfItem(application.getParticularsOfItem());
			existingApplication.get(0).setCalculationType(application.getCalculationType());
			existingApplication.get(0).setAdditionDeduction(application.getAdditionDeduction());
			existingApplication.get(0).setLf(application.getLf());
			existingApplication.get(0).setBwf(application.getBwf());
			existingApplication.get(0).setDhf(application.getDhf());
			existingApplication.get(0).setSubTotal(application.getSubTotal());
			existingApplication.get(0).setGrandTotal(application.getGrandTotal());
			existingApplication.get(0).setEstimatedQuantity(application.getEstimatedQuantity());
			existingApplication.get(0).setRemarks(application.getRemarks());
			existingApplication.get(0).setOverheadCode(application.getOverheadCode());
			existingApplication.get(0).setOverheadDescription(application.getOverheadDescription());
			existingApplication.get(0).setValueType(application.getValueType());
			existingApplication.get(0).setEstimatedAmount(application.getEstimatedAmount());
			existingApplication.get(0).setDocumentDescription(application.getDocumentDescription());
			existingApplication.get(0).setUploadDocument(application.getUploadDocument());
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
