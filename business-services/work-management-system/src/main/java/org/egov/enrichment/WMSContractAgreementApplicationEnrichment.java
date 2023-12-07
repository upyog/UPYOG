package org.egov.enrichment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.egov.repository.WMSWorkRepository;
import org.egov.util.IdgenUtil;
import org.egov.web.models.AgreementInfo;
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
            List<AgreementInfo> agreementInfoList = new ArrayList<>();
            //for(int i=0;i<wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().size();i++) {
            for(int i=0;i<wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().size();i++) {
            Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
            AgreementInfo agreementInfo=AgreementInfo.builder().agreementNo(Long.toString(randomNumber)).agreementName(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getAgreementName()).agreementDate(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getAgreementDate()).agreementAmount(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getAgreementAmount()).departmentNameAi(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getDepartmentNameAi()).loaNo(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getLoaNo()).resolutionNo(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getResolutionNo()).resolutionDate(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getResolutionDate()).tenderNo(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getTenderNo()).tenderDate(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getTenderDate()).agreementType(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getAgreementType()).defectLiabilityPeriod(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getDefectLiabilityPeriod()).contractPeriod(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getContractPeriod()).paymentType(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getPaymentType()).build();
            agreementInfoList.add(agreementInfo);
            application.setAgreementInfo(agreementInfoList);
            agreementInfoList.clear();
            //agreementInfo.setAgreementNo(Long.toString(randomNumber));
			//application.setAgreementNo(Long.toString(randomNumber));;
			//application.setId(UUID.randomUUID().toString());
			//application.setWorkName(sorIdList.get(index++));
			//application.setStartDate(date);
			//application.setEndDate(date);
            }//for loop
            
            //application.setAgreementInfo(agreementInfoList);
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
			existingApplication.get(0).getAgreementInfo().get(0).setAgreementName(application.getAgreementInfo().get(0).getAgreementName());
			existingApplication.get(0).getAgreementInfo().get(0).setAgreementDate(application.getAgreementInfo().get(0).getAgreementDate());
			existingApplication.get(0).getAgreementInfo().get(0).setDepartmentNameAi(application.getAgreementInfo().get(0).getDepartmentNameAi());
			existingApplication.get(0).getParty1Details().setDepartmentNameParty1(application.getParty1Details().getDepartmentNameParty1());
			existingApplication.get(0).getAgreementInfo().get(0).setLoaNo(application.getAgreementInfo().get(0).getLoaNo());
			existingApplication.get(0).getAgreementInfo().get(0).setResolutionNo(application.getAgreementInfo().get(0).getResolutionNo());
			existingApplication.get(0).getAgreementInfo().get(0).setResolutionDate(application.getAgreementInfo().get(0).getResolutionDate());
			existingApplication.get(0).getAgreementInfo().get(0).setTenderNo(application.getAgreementInfo().get(0).getTenderNo());
			existingApplication.get(0).getAgreementInfo().get(0).setTenderDate(application.getAgreementInfo().get(0).getTenderDate());
			existingApplication.get(0).getAgreementInfo().get(0).setAgreementType(application.getAgreementInfo().get(0).getAgreementType());
			existingApplication.get(0).getAgreementInfo().get(0).setDefectLiabilityPeriod(application.getAgreementInfo().get(0).getDefectLiabilityPeriod());
			existingApplication.get(0).getAgreementInfo().get(0).setContractPeriod(application.getAgreementInfo().get(0).getContractPeriod());
			existingApplication.get(0).getAgreementInfo().get(0).setAgreementAmount(application.getAgreementInfo().get(0).getAgreementAmount());
			existingApplication.get(0).getAgreementInfo().get(0).setPaymentType(application.getAgreementInfo().get(0).getPaymentType());
			existingApplication.get(0).getSDPGBGDetails().setDepositType(application.getSDPGBGDetails().getDepositType());
			existingApplication.get(0).getSDPGBGDetails().setDepositAmount(application.getSDPGBGDetails().getDepositAmount());
			
			existingApplication.get(0).getSDPGBGDetails().setAccountNo(application.getSDPGBGDetails().getAccountNo());
			existingApplication.get(0).getSDPGBGDetails().setParticulars(application.getSDPGBGDetails().getParticulars());
			existingApplication.get(0).getSDPGBGDetails().setValidFromDate(application.getSDPGBGDetails().getValidFromDate());
			existingApplication.get(0).getSDPGBGDetails().setValidTillDate(application.getSDPGBGDetails().getValidTillDate());
			existingApplication.get(0).getSDPGBGDetails().setBankBranchIfscCode(application.getSDPGBGDetails().getBankBranchIfscCode());
			existingApplication.get(0).getSDPGBGDetails().setPaymentMode(application.getSDPGBGDetails().getPaymentMode());
			existingApplication.get(0).getParty1Details().setDesignation(application.getParty1Details().getDesignation());
			existingApplication.get(0).getParty1Details().setEmployeeName(application.getParty1Details().getEmployeeName());
			existingApplication.get(0).getParty1Details().setWitnessNameP1(application.getParty1Details().getWitnessNameP1());
			existingApplication.get(0).getParty1Details().setAddressP1(application.getParty1Details().getAddressP1());
			existingApplication.get(0).getParty1Details().setUidP1(application.getParty1Details().getUidP1());
			existingApplication.get(0).getParty2Witness().setWitnessNameP2(application.getParty2Witness().getWitnessNameP2());
			existingApplication.get(0).getParty2Witness().setAddressP2(application.getParty2Witness().getAddressP2());
			existingApplication.get(0).getParty2Witness().setUidP2(application.getParty2Witness().getUidP2());
			existingApplication.get(0).getContractors().setVendorType(application.getContractors().getVendorType());
			existingApplication.get(0).getContractors().setVendorName(application.getContractors().getVendorName());
			existingApplication.get(0).getContractors().setRepresentedBy(application.getContractors().getRepresentedBy());
			existingApplication.get(0).getContractors().setPrimaryParty(application.getContractors().getPrimaryParty());
			existingApplication.get(0).getTermsAndConditions().setSrNo(application.getTermsAndConditions().getSrNo());
			existingApplication.get(0).getTermsAndConditions().setTermsAndConditions(application.getTermsAndConditions().getTermsAndConditions());
			existingApplication.get(0).getAgreementDocuments().setDocumentDescription(application.getAgreementDocuments().getDocumentDescription());
			existingApplication.get(0).getAgreementDocuments().setUploadDocument(application.getAgreementDocuments().getUploadDocument());
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
	}

}
