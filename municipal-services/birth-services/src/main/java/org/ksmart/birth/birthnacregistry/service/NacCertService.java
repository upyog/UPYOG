package org.ksmart.birth.birthnacregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
 
import org.ksmart.birth.birthnacregistry.model.RegisterCertificateData;
import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.ksmart.birth.utils.CommonUtils;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.NumToWordConverter;
import org.ksmart.birth.utils.NumberConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class NacCertService {
	
	  private final MdmsUtil mdmsUtil;
	    private final MdmsDataServiceNac mdmsDataService;
	    private final NumberConverter numberConverter;
	    @Autowired
	    NacCertService(MdmsUtil mdmsUtil, MdmsDataServiceNac mdmsDataService, NumberConverter numberConverter) {
	        this.mdmsUtil = mdmsUtil;
	        this.mdmsDataService = mdmsDataService;
	        this.numberConverter = numberConverter;
	    }

	    
	    public RegisterCertificateData setCertificateDetails(RegisterNac register, RequestInfo requestInfo) {
	        Object mdmsData = mdmsUtil.mdmsCall(requestInfo);
	        
	        String strDate=null;
			DateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter dtDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		    DateTimeFormatter dtTime = DateTimeFormatter.ofPattern("HH:mm");
			Calendar calenderBefore = Calendar.getInstance(), calenderAfter = Calendar.getInstance();
			Date dobDate = new Date(register.getDateofbirth());
			calenderBefore.setTime(dobDate);
			calenderAfter.setTime(dobDate);
			calenderBefore.add(Calendar.YEAR, -3);
			calenderAfter.add(Calendar.YEAR, 3);
			Date beforeDate = calenderBefore.getTime();
			Date afterDate = calenderAfter.getTime();
//		    ZonedDateTime zdt = CommonUtils.currentDate();
		    
		
		    String dobInWords = null;
		
		    Long updatedDate = null;
		    String updatedTime = null;
//		    
//	        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
////	        Date reportDate = new Date(register.getDateOfReport());
//	        Date dobDate = null;
//	        String dobInWords = "";
////	        Date regDate = new Date(register.getRegistrationDate());
//	        Date curDate = new Date(formatter.format(new Date()));
	        if(register.getDateofbirth() != null){
	            Date res = new Date(register.getDateofbirth()) ;
	            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	            strDate= formatter.format(res);
	            String[] dobAry = strDate.split("/");
	            try {
	                dobInWords = NumToWordConverter.convertNumber(Long.parseLong(dobAry[0])) + "/" + new SimpleDateFormat("MMMM").format(res) + "/" + NumToWordConverter.convertNumber(Long.parseLong(dobAry[2]));;
	            } catch(Exception e) {
	            }
	        } 
	        RegisterCertificateData registerCertificateData = new RegisterCertificateData();
	        registerCertificateData.setId(register.getId());
	        registerCertificateData.setDateOfBirth(register.getDateofbirth());
	        registerCertificateData.setDobStrWord(dobInWords);
	        registerCertificateData.setCareofapplicantnameen(register.getCareofapplicantnameen());
	        
	        registerCertificateData.setApplicantnameen(register.getApplicantnameen());
	        registerCertificateData.setDateOfReport(register.getDateOfReport());
	        registerCertificateData.setRegistrationDate(register.getRegistrationDate());
	        registerCertificateData.setCurrentDate(dtDate.format(CommonUtils.currentDate()));
	        registerCertificateData.setCurrentTime(CommonUtils.currentTime());

	        registerCertificateData.setDobStr(strDate);
	        registerCertificateData.setBirthplaceen(register.getBirthplaceen());
	        registerCertificateData.setChildnameen(register.getChildnameen());
	        registerCertificateData.setRegistrationNo(register.getRegistrationno());
	        registerCertificateData.setApplicationType(register.getApplicationtype());
	        registerCertificateData.setMothernameen(register.getMothernameen());
	        registerCertificateData.setFathernameen(register.getFathernameen());
	        registerCertificateData.setPermAddress(register.getPermenantAddDetails());
	        registerCertificateData.setFullName(register.getFullName());
	        registerCertificateData.setFullNameMl(register.getFullNameMl());
	       
	        registerCertificateData.setAckNo(register.getAckNumber());
	       
//	        registerCertificateData.setFatherDetails(register.getRegisterBirthFather().getFirstNameEn());
//	        registerCertificateData.setFatherDetailsMl(register.getRegisterBirthFather().getFirstNameMl());
	        registerCertificateData.setMotherDetails(register.getMothernameen());
	        registerCertificateData.setBirthPlaceId(register.getPlaceOfBirthId());
	        registerCertificateData.setTenantId(register.getTenantid());
			registerCertificateData.setPeriod(formatterDate.format(beforeDate) + " to " + formatterDate.format(afterDate));
//	        registerCertificateData.setBirthPlaceId(register.getPlaceOfBirthId());
//	        registerCertificateData.setBirthPlaceHospitalId(register.getHospitalId());
//	        registerCertificateData.setBirthPlaceInstitutionId(register.getInstitutionId());
	        registerCertificateData.setRegistarDetails("Registrar of Births and Deaths");
	        registerCertificateData.setIsBirthNAC(register.getIsBirthNAC());
	        registerCertificateData.setIsBirthNIA(register.getIsBirthNIA());
	        mdmsDataService.setTenantDetails(registerCertificateData, mdmsData);
	        
	        Object mdmsLocData = mdmsUtil.mdmsCallForLocation(requestInfo, registerCertificateData.getTenantId());
//	        mdmsDataService.setBirthPlaceDetails(registerCertificateData, mdmsLocData);
	        return registerCertificateData;

	    }

	    private void setBasicDetails() {

	    }

	    private void setAddressDetails() {

	    }
}
