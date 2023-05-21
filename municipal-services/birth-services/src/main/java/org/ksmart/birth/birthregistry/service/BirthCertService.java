package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.ksmart.birth.utils.CommonUtils;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.NumToWordConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

@Slf4j
@Service
public class BirthCertService {
    private final MdmsUtil mdmsUtil;
    private final MdmsDataService mdmsDataService;
    private final KsmartBirthPlaceService ksmartBirthPlace;
    @Autowired
    BirthCertService(MdmsUtil mdmsUtil, MdmsDataService mdmsDataService, KsmartBirthPlaceService ksmartBirthPlace) {
        this.mdmsUtil = mdmsUtil;
        this.mdmsDataService = mdmsDataService;
        this.ksmartBirthPlace = ksmartBirthPlace;
    }
    public RegisterCertificateData setCertificateDetails(RegisterBirthDetail register, RequestInfo requestInfo) {
        Object mdmsData = mdmsUtil.mdmsCall(requestInfo);
        String strDate=null;
        String regDate=null;

        DateTimeFormatter dtDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dobInWords = null;

        Long updatedDate = null;
        String updatedTime = null;

        if(register.getDateOfBirth() != null){
            Date res = new Date(register.getDateOfBirth()) ;
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
        registerCertificateData.setIsMigrated(register.getIsMigrated());
        registerCertificateData.setDateOfBirth(register.getDateOfBirth());
        registerCertificateData.setDobStrWord(dobInWords.toUpperCase());
        registerCertificateData.setWardCode(register.getRegisterBirthPlace().getWardId());
        registerCertificateData.setDateOfReport(register.getDateOfReport());
        registerCertificateData.setRegistrationDate(register.getRegistrationDate());
        registerCertificateData.setCurrentDate(dtDate.format(CommonUtils.currentDate()));
        registerCertificateData.setCurrentTime(CommonUtils.currentTime());
        registerCertificateData.setDobStr(strDate);
        registerCertificateData.setCurrentDateLong(CommonUtils.currentDateTime());
        registerCertificateData.setRegistrationNo(register.getRegistrationNo());
        registerCertificateData.setApplicationType(register.getApplicationType());
        registerCertificateData.setApplicationId(register.getApplicationId());
        registerCertificateData.setFullName(register.getFullName());
        registerCertificateData.setFullNameMl(register.getFullNameMl());
        registerCertificateData.setGenderEn(register.getGender());
        registerCertificateData.setGenderMl(register.getGender()+"_ML");
        registerCertificateData.setAckNo(register.getAckNumber());
        if(registerCertificateData.getIsMigrated()) {
            registerCertificateData.setKeyNo(register.getAckNumber());
        }
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
        registerCertificateData.setBirthPlaceInstitutionlTypeId(register.getRegisterBirthPlace().getInstitutionTypeId());
        registerCertificateData.setRegistarDetails("Registrar of Births and Deaths");
        if(register.getAuditDetails().getLastModifiedTime() == null) {
            updatedDate = register.getAuditDetails().getCreatedTime();
        } else{
            updatedDate = register.getAuditDetails().getLastModifiedTime();
        }
        registerCertificateData.setUpdatingDate(updatedDate);
        registerCertificateData.setUpdatingTime(CommonUtils.timeLongToStringhh(updatedDate));
        mdmsDataService.setTenantDetails(registerCertificateData, mdmsData);
        mdmsDataService.setPresentAddressDetailsEn(register, registerCertificateData, mdmsData, register.getRegistrationDate());
        mdmsDataService.setPremananttAddressDetailsEn(register, registerCertificateData, mdmsData, register.getRegistrationDate());
        Object mdmsLocData = mdmsUtil.mdmsCallForLocation(requestInfo, registerCertificateData.getTenantId());
        ksmartBirthPlace.setBirthPlaceDetails(register,registerCertificateData, mdmsLocData,mdmsData);
        return registerCertificateData;

    }

    private void setBasicDetails() {

    }

    private void setAddressDetails() {

    }
}
