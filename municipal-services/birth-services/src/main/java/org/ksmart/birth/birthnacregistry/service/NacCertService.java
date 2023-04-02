package org.ksmart.birth.birthnacregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
 
import org.ksmart.birth.birthnacregistry.model.RegisterCertificateData;
import org.ksmart.birth.birthnacregistry.model.RegisterNac;
 
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.NumberConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
	        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//	        Date reportDate = new Date(register.getDateOfReport());
	        Date dobDate = null;
	        String dobInWords = "";
	        Date regDate = new Date(register.getRegistrationDate());
	        Date curDate = new Date(formatter.format(new Date()));
	        if(register.getDateofbirth() != null){
	            dobDate = new Date(register.getDateofbirth());
	            String[] dobAry = formatter.format(dobDate).split("/");
	            try {
	                dobInWords = numberConverter.numberToWordConverter(dobAry[0].toCharArray()) + "/" + new SimpleDateFormat("MMMM").format(dobDate) + "/" + numberConverter.numberToWordConverter(dobAry[2].toCharArray());
	            } catch(Exception e) {

	            }
	            //System.out.println(dobInWords);
	        }

	        RegisterCertificateData registerCertificateData = new RegisterCertificateData();
	        registerCertificateData.setId(register.getId());
	        registerCertificateData.setDateOfBirth(register.getDateofbirth());
	        registerCertificateData.setDobStrWord(dobInWords);
//	        registerCertificateData.setWardCode(register.getRegisterBirthPlace().getWardId());
//	        registerCertificateData.setDateOfReport(register.getDateOfReport());
	        registerCertificateData.setRegistrationDate(register.getRegistrationDate());
	        registerCertificateData.setCurrentDate(formatter.format(curDate));
	        registerCertificateData.setDobStr(formatter.format(dobDate));
	        registerCertificateData.setRegistrationDateStr(formatter.format(regDate));
//	        registerCertificateData.setDateOfReportStr(formatter.format(reportDate));
	        registerCertificateData.setRegistrationNo(register.getRegistrationno());
	        registerCertificateData.setApplicationType(register.getApplicationtype());
//	        registerCertificateData.setApplicationId(register.getApplicationId());
	        registerCertificateData.setFullName(register.getFullName());
	        registerCertificateData.setFullNameMl(register.getFullNameMl());
	       
//	        registerCertificateData.setAckNo(register.getAckNumber());
	       
//	        registerCertificateData.setFatherDetails(register.getRegisterBirthFather().getFirstNameEn());
//	        registerCertificateData.setFatherDetailsMl(register.getRegisterBirthFather().getFirstNameMl());
	        registerCertificateData.setMotherDetails(register.getMothernameen());
//	        registerCertificateData.setMotherDetailsMl(register.getRegisterBirthMother().getFirstNameMl());
	        registerCertificateData.setTenantId(register.getTenantid());
	        registerCertificateData.setBirthPlaceId(register.getPlaceOfBirthId());
	        registerCertificateData.setBirthPlaceHospitalId(register.getHospitalId());
	        registerCertificateData.setBirthPlaceInstitutionId(register.getInstitutionId());
	        registerCertificateData.setRegistarDetails("Registrar of Births and Deaths");
	        mdmsDataService.setTenantDetails(registerCertificateData, mdmsData);
	        
	        Object mdmsLocData = mdmsUtil.mdmsCallForLocation(requestInfo, registerCertificateData.getTenantId());
	        mdmsDataService.setBirthPlaceDetails(registerCertificateData, mdmsLocData);
	        return registerCertificateData;

	    }

	    private void setBasicDetails() {

	    }

	    private void setAddressDetails() {

	    }
}
