package org.egov.enrichment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.egov.repository.WMSWorkRepository;
import org.egov.util.IdgenUtil;
import org.egov.web.models.AuditDetails;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractAgreementApplication;
import org.egov.web.models.WMSContractAgreementRequest;
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
public class WMSContractAgreementApplicationEnrichment {
	
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

	public void enrichContractAgreementApplication(WMSContractAgreementRequest wmsContractAgreementRequest) { 
		//List<String> sorIdList = idgenUtil.getIdList(wmsContractorRequest.getRequestInfo(), wmsContractorRequest.getWmsWorkApplications().get(0).getTenantId(), idGenName, idGenFormat, wmsContractorRequest.getWmsWorkApplications().size());
        //Integer index = 0;
		for (WMSContractAgreementApplication application : wmsContractAgreementRequest.getWmsContractAgreementApplications()) {
			// Enrich audit details
//	            AuditDetails auditDetails = AuditDetails.builder().createdBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
//	            application.setAuditDetails(auditDetails);
			
			AuditDetails auditDetails = AuditDetails.builder().createdBy(wmsContractAgreementRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(wmsContractAgreementRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

			// Enrich UUID
            Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
			application.setAgreementNo(Long.toString(randomNumber));
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

	public void enrichContractAgreementApplicationUpdate(WMSContractAgreementRequest wmsContractAgreementRequest, List<WMSContractAgreementApplication> existingApplication) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (WMSContractAgreementApplication application : wmsContractAgreementRequest.getWmsContractAgreementApplications()) {
			existingApplication.get(0).setAgreementName(application.getAgreementName());
			existingApplication.get(0).setAgreementDate(application.getAgreementDate());
			existingApplication.get(0).setDepartmentName(application.getDepartmentName());
			existingApplication.get(0).setLoaNo(application.getLoaNo());
			existingApplication.get(0).setResolutionNo(application.getResolutionNo());
			existingApplication.get(0).setResolutionDate(application.getResolutionDate());
			existingApplication.get(0).setTenderNo(application.getTenderNo());
			existingApplication.get(0).setTenderDate(application.getTenderDate());
			existingApplication.get(0).setAgreementType(application.getAgreementType());
			existingApplication.get(0).setDefectLiabilityPeriod(application.getDefectLiabilityPeriod());
			existingApplication.get(0).setContractPeriod(application.getContractPeriod());
			existingApplication.get(0).setAgreementAmount(application.getAgreementAmount());
			existingApplication.get(0).setPaymentType(application.getPaymentType());
			existingApplication.get(0).setDepositType(application.getDepositType());
			existingApplication.get(0).setDepositAmount(application.getDepositAmount());
			existingApplication.get(0).setWorkDescription(application.getWorkDescription());
			existingApplication.get(0).setAccountNo(application.getAccountNo());
			existingApplication.get(0).setParticulars(application.getParticulars());
			existingApplication.get(0).setValidFromDate(application.getValidFromDate());
			existingApplication.get(0).setValidTillDate(application.getValidTillDate());
			existingApplication.get(0).setBankBranchIfscCode(application.getBankBranchIfscCode());
			existingApplication.get(0).setPaymentMode(application.getPaymentMode());
			existingApplication.get(0).setDesignation(application.getDesignation());
			existingApplication.get(0).setEmployeeName(application.getEmployeeName());
			existingApplication.get(0).setWitnessName(application.getWitnessName());
			existingApplication.get(0).setAddress(application.getAddress());
			existingApplication.get(0).setUid(application.getUid());
			existingApplication.get(0).setVendorType(application.getVendorType());
			existingApplication.get(0).setVendorName(application.getVendorName());
			existingApplication.get(0).setRepresentedBy(application.getRepresentedBy());
			existingApplication.get(0).setPrimaryParty(application.getPrimaryParty());
			existingApplication.get(0).setSrNo(application.getSrNo());
			existingApplication.get(0).setTermsAndConditions(application.getTermsAndConditions());
			existingApplication.get(0).setDocumentDescription(application.getDocumentDescription());
			existingApplication.get(0).setUploadDocument(application.getUploadDocument());
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
