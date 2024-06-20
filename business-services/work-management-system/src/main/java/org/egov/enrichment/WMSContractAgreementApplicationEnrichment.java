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
import org.egov.web.models.AgreementDocuments;
import org.egov.web.models.AgreementInfo;
import org.egov.web.models.AuditDetails;
import org.egov.web.models.Contractors;
import org.egov.web.models.Party1Details;
import org.egov.web.models.Party2Witness;

import org.egov.web.models.SDPGBGDetails;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.TermsAndConditions;
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
			 
			 for (SDPGBGDetails sdbg : application.getSDPGBGDetails()) {
	        	 //Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
				 sdbg.setAgreementNo(application.getAgreementNo());
				 
       randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
	        	 
       sdbg.setSdpgId(Long.toString(randomNumber));
	        	 
	         }
			 
			 for (TermsAndConditions tnc : application.getTermsAndConditions()) {
	        	 //Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
				 tnc.setAgreementNo(application.getAgreementNo());
				 
       randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
	        	 
       tnc.setTncId(Long.toString(randomNumber));
	        	 
	         }
			 
			 for (AgreementDocuments ad : application.getAgreementDocuments()) {
	        	 //Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
				 ad.setAgreementNo(application.getAgreementNo());
				 
       randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
	        	 
       ad.setAdId(Long.toString(randomNumber));
	        	 
	         }
			 
			 for (Party2Witness pw: application.getParty2Witness()) {
	        	 //Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
				 pw.setAgreementNo(application.getAgreementNo());
				 
       randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
	        	 
       pw.setPwId(Long.toString(randomNumber));
	        	 
	         }
			 
			 for (Contractors con: application.getContractors()) {
	        	 //Long randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
				 con.setAgreementNo(application.getAgreementNo());
				 
       randomNumber=(long) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
	        	 
       con.setConId(Long.toString(randomNumber));
	        	 
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
				int l=0;
				int m=0;
				int n=0;
				int o=0;
				int p=0;
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
			
			for(l=0;l<application.getSDPGBGDetails().size();l++) {
				int currentIndex = l;
				List<SDPGBGDetails> existingApplicationSDPGBGDetails = existingApplicationResult.get(0).getSDPGBGDetails().stream().filter(x -> x.getSdpgId().equalsIgnoreCase(application.getSDPGBGDetails().get(currentIndex).getSdpgId())).collect(Collectors.toList());
				
				//if (!wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getParty1Details().stream().anyMatch(x -> x.getUidP1().equalsIgnoreCase(existingApplication.get(0).getParty1Details().get(0).getUidP1()))) {
				 if(existingApplicationSDPGBGDetails.size() > 0) {
					 
					 existingApplicationSDPGBGDetails.get(0).setDepositType(application.getSDPGBGDetails().get(l).getDepositType());
					 
					 existingApplicationSDPGBGDetails.get(0).setDepositAmount(application.getSDPGBGDetails().get(l).getDepositAmount());
					 existingApplicationSDPGBGDetails.get(0).setAccountNo(application.getSDPGBGDetails().get(l).getAccountNo());
					 existingApplicationSDPGBGDetails.get(0).setParticulars(application.getSDPGBGDetails().get(l).getParticulars());
					 existingApplicationSDPGBGDetails.get(0).setValidFromDate(application.getSDPGBGDetails().get(l).getValidFromDate());
					 existingApplicationSDPGBGDetails.get(0).setValidTillDate(application.getSDPGBGDetails().get(l).getValidTillDate());
					 existingApplicationSDPGBGDetails.get(0).setBankBranchIfscCode(application.getSDPGBGDetails().get(l).getBankBranchIfscCode());
					 existingApplicationSDPGBGDetails.get(0).setPaymentMode(application.getSDPGBGDetails().get(l).getPaymentMode());
					 
					 
			
			
				 }
			}
			
			for(m=0;m<application.getTermsAndConditions().size();m++) {
				int currentIndex = m;
				List<TermsAndConditions> existingApplicationTermsAndConditions = existingApplicationResult.get(0).getTermsAndConditions().stream().filter(x -> x.getTncId().equalsIgnoreCase(application.getTermsAndConditions().get(currentIndex).getTncId())).collect(Collectors.toList());
				
				//if (!wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getParty1Details().stream().anyMatch(x -> x.getUidP1().equalsIgnoreCase(existingApplication.get(0).getParty1Details().get(0).getUidP1()))) {
				 if(existingApplicationTermsAndConditions.size() > 0) {
					 existingApplicationTermsAndConditions.get(0).setSrNo(application.getTermsAndConditions().get(m).getSrNo());
					 existingApplicationTermsAndConditions.get(0).setTermsAndConditions(application.getTermsAndConditions().get(m).getTermsAndConditions());
			
			
				 }
			}
			
			
			for(o=0;o<application.getParty2Witness().size();o++) {
				int currentIndex = o;
				List<Party2Witness> existingApplicationParty2Witness = existingApplicationResult.get(0).getParty2Witness().stream().filter(x -> x.getPwId().equalsIgnoreCase(application.getParty2Witness().get(currentIndex).getPwId())).collect(Collectors.toList());
				
				//if (!wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getParty1Details().stream().anyMatch(x -> x.getUidP1().equalsIgnoreCase(existingApplication.get(0).getParty1Details().get(0).getUidP1()))) {
				 if(existingApplicationParty2Witness.size() > 0) {
					 
					 existingApplicationParty2Witness.get(0).setWitnessNameP2(application.getParty2Witness().get(o).getWitnessNameP2());
					 existingApplicationParty2Witness.get(0).setAddressP2(application.getParty2Witness().get(o).getAddressP2());
					 existingApplicationParty2Witness.get(0).setUidP2(application.getParty2Witness().get(o).getUidP2());
			
			
				 }
			}
			
			
			for(p=0;p<application.getContractors().size();p++) {
				int currentIndex = p;
				List<Contractors> existingApplicationContractors = existingApplicationResult.get(0).getContractors().stream().filter(x -> x.getConId().equalsIgnoreCase(application.getContractors().get(currentIndex).getConId())).collect(Collectors.toList());
				
				//if (!wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getParty1Details().stream().anyMatch(x -> x.getUidP1().equalsIgnoreCase(existingApplication.get(0).getParty1Details().get(0).getUidP1()))) {
				 if(existingApplicationContractors.size() > 0) {
					 
					 existingApplicationContractors.get(0).setVendorName(application.getContractors().get(p).getVendorName());
					 existingApplicationContractors.get(0).setVendorType(application.getContractors().get(p).getVendorType());
					 existingApplicationContractors.get(0).setRepresentedBy(application.getContractors().get(p).getRepresentedBy());
					 existingApplicationContractors.get(0).setPrimaryParty(application.getContractors().get(p).getPrimaryParty());
					 
			
			
				 }
			}
			
			for(n=0;n<application.getAgreementDocuments().size();n++) {
				int currentIndex = n;
				List<AgreementDocuments> existingApplicationAgreementDocuments = existingApplicationResult.get(0).getAgreementDocuments().stream().filter(x -> x.getAdId().equalsIgnoreCase(application.getAgreementDocuments().get(currentIndex).getAdId())).collect(Collectors.toList());
				
				//if (!wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getParty1Details().stream().anyMatch(x -> x.getUidP1().equalsIgnoreCase(existingApplication.get(0).getParty1Details().get(0).getUidP1()))) {
				 if(existingApplicationAgreementDocuments.size() > 0) {
			
					 existingApplicationAgreementDocuments.get(0).setDocumentDescription(application.getAgreementDocuments().get(n).getDocumentDescription());
					 existingApplicationAgreementDocuments.get(0).setUploadDocument(application.getAgreementDocuments().get(n).getUploadDocument());
			
//			application.setEndDate(date);
			// application.getAuditDetails().setLastModifiedBy(birthRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
				 }
			}
		}
			
		}
	}

}
