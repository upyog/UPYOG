package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.NumberConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
public class BirthCertService {
    private final MdmsUtil mdmsUtil;
    private final MdmsDataService mdmsDataService;
    private final NumberConverter numberConverter;
    @Autowired
    BirthCertService(MdmsUtil mdmsUtil, MdmsDataService mdmsDataService, NumberConverter numberConverter) {
        this.mdmsUtil = mdmsUtil;
        this.mdmsDataService = mdmsDataService;
        this.numberConverter = numberConverter;
    }
    public RegisterCertificateData setCertificateDetails(RegisterBirthDetail register, RequestInfo requestInfo) {
        Object mdmsData = mdmsUtil.mdmsCall(requestInfo);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat formatterTime = new SimpleDateFormat("hh:mm");
        Date reportDate = new Date(register.getDateOfReport());
        Date dobDate = null;
        Date updatedDate = null;
        String updatedTime = null;
        String dobInWords = "";
        if(register.getAuditDetails().getLastModifiedTime() == null) {
            updatedDate = new Date(formatter.format(register.getAuditDetails().getCreatedTime()));
            updatedTime = formatterTime.format(updatedDate);
        } else{
            updatedDate = new Date(formatter.format(register.getAuditDetails().getLastModifiedTime()));
            updatedTime = formatterTime.format(updatedDate);
        }

        Date regDate = new Date(register.getRegistrationDate());
        long now = Instant.now().toEpochMilli();
        Date curDate = new Date(formatter.format(new Date()));
        String curTime = formatterTime.format(curDate);
        if(register.getDateOfBirth() != null){
            dobDate = new Date(register.getDateOfBirth());
            String[] dobAry = formatter.format(dobDate).split("/");
            try {
                dobInWords = numberConverter.numberToWordConverter(dobAry[0].toCharArray()) + "/" + new SimpleDateFormat("MMMM").format(dobDate) + "/" + numberConverter.numberToWordConverter(dobAry[2].toCharArray());
            } catch(Exception e) {

            }
            //System.out.println(dobInWords);
        }

        RegisterCertificateData registerCertificateData = new RegisterCertificateData();
        registerCertificateData.setId(register.getId());
        registerCertificateData.setDateOfBirth(register.getDateOfBirth());
        registerCertificateData.setDobStrWord(dobInWords);
        registerCertificateData.setWardCode(register.getRegisterBirthPlace().getWardId());
        registerCertificateData.setDateOfReport(register.getDateOfReport());
        registerCertificateData.setRegistrationDate(register.getRegistrationDate());
        registerCertificateData.setCurrentDate(formatter.format(curDate));
        registerCertificateData.setDobStr(formatter.format(dobDate));
        registerCertificateData.setRegistrationDateStr(formatter.format(regDate));
        registerCertificateData.setDateOfReportStr(formatter.format(reportDate));
        registerCertificateData.setRegistrationNo(register.getRegistrationNo());
        registerCertificateData.setApplicationType(register.getApplicationType());
        registerCertificateData.setApplicationId(register.getApplicationId());
        registerCertificateData.setFullName(register.getFullName());
        registerCertificateData.setFullNameMl(register.getFullNameMl());
        registerCertificateData.setGenderEn(register.getGender());
        registerCertificateData.setGenderMl(register.getGender()+"_ML");
        registerCertificateData.setAckNo(register.getAckNumber());
        registerCertificateData.setRemarksEn(register.getRemarksEn()==null?" ":register.getRemarksEn());
        registerCertificateData.setRemarksMl(register.getRemarksMl()==null?" ":register.getRemarksMl());
        registerCertificateData.setAadharNo(register.getAadharNo());
        registerCertificateData.setFatherDetails(register.getRegisterBirthFather().getFirstNameEn());
        registerCertificateData.setFatherDetailsMl(register.getRegisterBirthFather().getFirstNameMl());
        registerCertificateData.setMotherDetails(register.getRegisterBirthMother().getFirstNameEn());
        registerCertificateData.setMotherDetailsMl(register.getRegisterBirthMother().getFirstNameMl());
        registerCertificateData.setTenantId(register.getTenantId());
        registerCertificateData.setBirthPlaceId(register.getRegisterBirthPlace().getPlaceOfBirthId());
        registerCertificateData.setBirthPlaceHospitalId(register.getRegisterBirthPlace().getHospitalId());
        registerCertificateData.setBirthPlaceInstitutionId(register.getRegisterBirthPlace().getInstitutionId());
        registerCertificateData.setRegistarDetails("Registrar of Births and Deaths");
        registerCertificateData.setCurrentTime(curTime.toString());
        registerCertificateData.setCurrentDateLong(now);
        registerCertificateData.setReportingDate(formatter.format(updatedDate));
        registerCertificateData.setReportingTime(updatedTime);
        mdmsDataService.setTenantDetails(registerCertificateData, mdmsData);
        mdmsDataService.setPresentAddressDetailsEn(register, registerCertificateData, mdmsData);
        mdmsDataService.setPremananttAddressDetailsEn(register, registerCertificateData, mdmsData);
        Object mdmsLocData = mdmsUtil.mdmsCallForLocation(requestInfo, registerCertificateData.getTenantId());
        mdmsDataService.setBirthPlaceDetails(registerCertificateData, mdmsLocData);
        return registerCertificateData;

    }

    private void setBasicDetails() {

    }

    private void setAddressDetails() {

    }
}
