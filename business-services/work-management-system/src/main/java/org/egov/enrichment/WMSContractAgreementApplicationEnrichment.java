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
import org.egov.web.models.Party1Details;
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
            //List<AgreementInfo> agreementInfoList = new ArrayList<>();
            
           //for(int i=0;i<wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().size();i++) {
            
            //for(int i=0;i<wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().size();i++) {
            Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
            //String agName=wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getAgreementName();
            //System.out.println("Agreement name:"+agName);
            //AgreementInfo agreementInfo=AgreementInfo.builder().agreementNo(Long.toString(randomNumber)).agreementName(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getAgreementName()).agreementDate(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getAgreementDate()).agreementAmount(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getAgreementAmount()).departmentNameAi(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getDepartmentNameAi()).loaNo(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getLoaNo()).resolutionNo(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getResolutionNo()).resolutionDate(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getResolutionDate()).tenderNo(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getTenderNo()).tenderDate(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getTenderDate()).agreementType(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getAgreementType()).defectLiabilityPeriod(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getDefectLiabilityPeriod()).contractPeriod(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getContractPeriod()).paymentType(wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().get(i).getPaymentType()).build();
            //agreementInfoList.add(agreementInfo);
            //application.setAgreementInfo(agreementInfoList);
            //agreementInfoList.clear();
            //agreementInfo.setAgreementNo(Long.toString(randomNumber));
			 application.setAgreementNo(Long.toString(randomNumber));
			//application.setId(UUID.randomUUID().toString());
			//application.setWorkName(sorIdList.get(index++));
			//application.setStartDate(date);
			//application.setEndDate(date);
			 
			 for (AgreementInfo agreement : application.getAgreementInfo()) {
	        	 //Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
	        	 agreement.setAgreementNo(application.getAgreementNo());
	        	 
	         }
			 
			 for (Party1Details party1 : application.getParty1Details()) {
	        	 //Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
				 party1.setAgreementNo(application.getAgreementNo());
	        	 
	         }
            
		//}//for loop
            
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
			
			for(int i=0;i<wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().size();i++) {
				
				existingApplication.get(0).getAgreementInfo().get(i).setAgreementName(application.getAgreementInfo().get(i).getAgreementName());
				existingApplication.get(0).getAgreementInfo().get(i).setAgreementDate(application.getAgreementInfo().get(i).getAgreementDate());
				existingApplication.get(0).getAgreementInfo().get(i).setDepartmentNameAi(application.getAgreementInfo().get(i).getDepartmentNameAi());
				
				existingApplication.get(0).getAgreementInfo().get(i).setLoaNo(application.getAgreementInfo().get(i).getLoaNo());
				existingApplication.get(0).getAgreementInfo().get(i).setResolutionNo(application.getAgreementInfo().get(i).getResolutionNo());
				existingApplication.get(0).getAgreementInfo().get(i).setResolutionDate(application.getAgreementInfo().get(i).getResolutionDate());
				existingApplication.get(0).getAgreementInfo().get(i).setTenderNo(application.getAgreementInfo().get(i).getTenderNo());
				existingApplication.get(0).getAgreementInfo().get(i).setTenderDate(application.getAgreementInfo().get(i).getTenderDate());
				existingApplication.get(0).getAgreementInfo().get(i).setAgreementType(application.getAgreementInfo().get(i).getAgreementType());
				existingApplication.get(0).getAgreementInfo().get(i).setDefectLiabilityPeriod(application.getAgreementInfo().get(i).getDefectLiabilityPeriod());
				existingApplication.get(0).getAgreementInfo().get(i).setContractPeriod(application.getAgreementInfo().get(i).getContractPeriod());
				existingApplication.get(0).getAgreementInfo().get(i).setAgreementAmount(application.getAgreementInfo().get(i).getAgreementAmount());
				existingApplication.get(0).getAgreementInfo().get(i).setPaymentType(application.getAgreementInfo().get(i).getPaymentType());
				
			
			}
			
			for(int i=0;i<wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getParty1Details().size();i++) {
				
				existingApplication.get(0).getParty1Details().get(i).setDepartmentNameParty1(application.getParty1Details().get(i).getDepartmentNameParty1());
				existingApplication.get(0).getParty1Details().get(i).setDesignation(application.getParty1Details().get(i).getDesignation());
				existingApplication.get(0).getParty1Details().get(i).setEmployeeName(application.getParty1Details().get(i).getEmployeeName());
				existingApplication.get(0).getParty1Details().get(i).setWitnessNameP1(application.getParty1Details().get(i).getWitnessNameP1());
				existingApplication.get(0).getParty1Details().get(i).setAddressP1(application.getParty1Details().get(i).getAddressP1());
				existingApplication.get(0).getParty1Details().get(i).setUidP1(application.getParty1Details().get(i).getUidP1());
				
			}
			existingApplication.get(0).getSDPGBGDetails().setDepositType(application.getSDPGBGDetails().getDepositType());
			existingApplication.get(0).getSDPGBGDetails().setDepositAmount(application.getSDPGBGDetails().getDepositAmount());
			
			existingApplication.get(0).getSDPGBGDetails().setAccountNo(application.getSDPGBGDetails().getAccountNo());
			existingApplication.get(0).getSDPGBGDetails().setParticulars(application.getSDPGBGDetails().getParticulars());
			existingApplication.get(0).getSDPGBGDetails().setValidFromDate(application.getSDPGBGDetails().getValidFromDate());
			existingApplication.get(0).getSDPGBGDetails().setValidTillDate(application.getSDPGBGDetails().getValidTillDate());
			existingApplication.get(0).getSDPGBGDetails().setBankBranchIfscCode(application.getSDPGBGDetails().getBankBranchIfscCode());
			existingApplication.get(0).getSDPGBGDetails().setPaymentMode(application.getSDPGBGDetails().getPaymentMode());
			
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
