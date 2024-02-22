package org.egov.enrichment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	        	 
	        	 randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
	        	 
	        	 agreement.setAgrId(Long.toString(randomNumber));
	        	 
	         }
			 
			 for (Party1Details party1 : application.getParty1Details()) {
	        	 //Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
				 party1.setAgreementNo(application.getAgreementNo());
				 
       randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
	        	 
       party1.setParty1Id(Long.toString(randomNumber));
	        	 
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
			 List<WMSContractAgreementApplication> existingApplicationResult = existingApplication.stream().filter(x -> x.getAgreementNo().equalsIgnoreCase(application.getAgreementNo())).collect(Collectors.toList());
				
			if(existingApplicationResult.size() > 0) {
				int k=0;
				int j=0;
			 for(k=0;k<application.getAgreementInfo().size();k++) {
				 int currentIndex = k;
				 List<AgreementInfo> existingApplicationAgreement = existingApplicationResult.get(0).getAgreementInfo().stream().filter(x -> x.getAgrId().equalsIgnoreCase(application.getAgreementInfo().get(currentIndex).getAgrId())).collect(Collectors.toList());
				
				//if (!application.getAgreementInfo().stream().anyMatch(x -> x.getAgreementName().equalsIgnoreCase(existingApplication.get(0).getAgreementInfo().get(i).getAgreementName()))) {
				 if(existingApplicationAgreement.size() > 0) {
					 existingApplicationAgreement.get(0).setAgreementName(application.getAgreementInfo().get(k).getAgreementName());
					 existingApplicationAgreement.get(0).setAgreementDate(application.getAgreementInfo().get(k).getAgreementDate());
					 existingApplicationAgreement.get(0).setDepartmentNameAi(application.getAgreementInfo().get(k).getDepartmentNameAi());
				
					 existingApplicationAgreement.get(0).setLoaNo(application.getAgreementInfo().get(k).getLoaNo());
					 existingApplicationAgreement.get(0).setResolutionNo(application.getAgreementInfo().get(k).getResolutionNo());
					 existingApplicationAgreement.get(0).setResolutionDate(application.getAgreementInfo().get(k).getResolutionDate());
					 existingApplicationAgreement.get(0).setTenderNo(application.getAgreementInfo().get(k).getTenderNo());
					 existingApplicationAgreement.get(0).setTenderDate(application.getAgreementInfo().get(k).getTenderDate());
					 existingApplicationAgreement.get(0).setAgreementType(application.getAgreementInfo().get(k).getAgreementType());
					 existingApplicationAgreement.get(0).setDefectLiabilityPeriod(application.getAgreementInfo().get(k).getDefectLiabilityPeriod());
					 existingApplicationAgreement.get(0).setContractPeriod(application.getAgreementInfo().get(k).getContractPeriod());
					 existingApplicationAgreement.get(0).setAgreementAmount(application.getAgreementInfo().get(k).getAgreementAmount());
					 existingApplicationAgreement.get(0).setPaymentType(application.getAgreementInfo().get(k).getPaymentType());
				
				}
			}
			 	
			for(j=0;j<application.getParty1Details().size();j++) {
				int currentIndex = j;
				List<Party1Details> existingApplicationPartyDetails = existingApplicationResult.get(0).getParty1Details().stream().filter(x -> x.getParty1Id().equalsIgnoreCase(application.getParty1Details().get(currentIndex).getParty1Id())).collect(Collectors.toList());
				
				//if (!wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getParty1Details().stream().anyMatch(x -> x.getUidP1().equalsIgnoreCase(existingApplication.get(0).getParty1Details().get(0).getUidP1()))) {
				 if(existingApplicationPartyDetails.size() > 0) {
					 existingApplicationPartyDetails.get(0).setDepartmentNameParty1(application.getParty1Details().get(j).getDepartmentNameParty1());
					 existingApplicationPartyDetails.get(0).setDesignation(application.getParty1Details().get(j).getDesignation());
					 existingApplicationPartyDetails.get(0).setEmployeeName(application.getParty1Details().get(j).getEmployeeName());
					 existingApplicationPartyDetails.get(0).setWitnessNameP1(application.getParty1Details().get(j).getWitnessNameP1());
					 existingApplicationPartyDetails.get(0).setAddressP1(application.getParty1Details().get(j).getAddressP1());
					 existingApplicationPartyDetails.get(0).setUidP1(application.getParty1Details().get(j).getUidP1());
				
				}
				
			}
			existingApplicationResult.get(0).getSDPGBGDetails().setDepositType(application.getSDPGBGDetails().getDepositType());
			existingApplicationResult.get(0).getSDPGBGDetails().setDepositAmount(application.getSDPGBGDetails().getDepositAmount());
			
			existingApplicationResult.get(0).getSDPGBGDetails().setAccountNo(application.getSDPGBGDetails().getAccountNo());
			existingApplicationResult.get(0).getSDPGBGDetails().setParticulars(application.getSDPGBGDetails().getParticulars());
			existingApplicationResult.get(0).getSDPGBGDetails().setValidFromDate(application.getSDPGBGDetails().getValidFromDate());
			existingApplicationResult.get(0).getSDPGBGDetails().setValidTillDate(application.getSDPGBGDetails().getValidTillDate());
			existingApplicationResult.get(0).getSDPGBGDetails().setBankBranchIfscCode(application.getSDPGBGDetails().getBankBranchIfscCode());
			existingApplicationResult.get(0).getSDPGBGDetails().setPaymentMode(application.getSDPGBGDetails().getPaymentMode());
			
			existingApplicationResult.get(0).getParty2Witness().setWitnessNameP2(application.getParty2Witness().getWitnessNameP2());
			existingApplicationResult.get(0).getParty2Witness().setAddressP2(application.getParty2Witness().getAddressP2());
			existingApplicationResult.get(0).getParty2Witness().setUidP2(application.getParty2Witness().getUidP2());
			existingApplicationResult.get(0).getContractors().setVendorType(application.getContractors().getVendorType());
			existingApplicationResult.get(0).getContractors().setVendorName(application.getContractors().getVendorName());
			existingApplicationResult.get(0).getContractors().setRepresentedBy(application.getContractors().getRepresentedBy());
			existingApplicationResult.get(0).getContractors().setPrimaryParty(application.getContractors().getPrimaryParty());
			existingApplicationResult.get(0).getTermsAndConditions().setSrNo(application.getTermsAndConditions().getSrNo());
			existingApplicationResult.get(0).getTermsAndConditions().setTermsAndConditions(application.getTermsAndConditions().getTermsAndConditions());
			existingApplicationResult.get(0).getAgreementDocuments().setDocumentDescription(application.getAgreementDocuments().getDocumentDescription());
			existingApplicationResult.get(0).getAgreementDocuments().setUploadDocument(application.getAgreementDocuments().getUploadDocument());
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		}
			
		}
	}

}
