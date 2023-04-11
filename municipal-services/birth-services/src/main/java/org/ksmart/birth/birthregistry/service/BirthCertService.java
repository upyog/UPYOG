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
    @Autowired
    BirthCertService(MdmsUtil mdmsUtil, MdmsDataService mdmsDataService) {
        this.mdmsUtil = mdmsUtil;
        this.mdmsDataService = mdmsDataService;
    }
    public RegisterCertificateData setCertificateDetails(RegisterBirthDetail register, RequestInfo requestInfo) {
        Object mdmsData = mdmsUtil.mdmsCall(requestInfo);

        DateTimeFormatter dtDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtTime = DateTimeFormatter.ofPattern("HH:mm");

        ZonedDateTime zdt = CommonUtils.currentDate();
        System.out.println(zdt.toInstant().toEpochMilli());

        String dobInWords = null;

        String updatedDate = null;
        String updatedTime = null;
        if(register.getAuditDetails().getLastModifiedTime() == null) {
            ZonedDateTime ud = CommonUtils.LongToDate(register.getAuditDetails().getCreatedTime());
            updatedDate = dtDate.format(ud);
            updatedTime = dtTime.format(ud);
        } else{
            ZonedDateTime ud = CommonUtils.LongToDate(register.getAuditDetails().getLastModifiedTime());
            updatedDate = dtDate.format(ud);
            updatedTime = dtTime.format(ud);
        }
        if(register.getDateOfBirth() != null){
            ZonedDateTime dobDate = CommonUtils.LongToDate(1332892800000L);
            Date res = new Date(1332892800000L) ;
            System.out.println(dobDate);
            System.out.println(res);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String strDate= formatter.format(res);
            System.out.println(strDate);
            //System.out.println(dtDate.format((TemporalAccessor) res));
            String[] dobAry = dtDate.format(dobDate).split("/");
            try {

                dobInWords = NumToWordConverter.convertNumber(Long.parseLong(dobAry[0]));
                        //NumToWordConve.(dobAry[0].toCharArray()) + "/" + new SimpleDateFormat("MMMM").format(dobDate) + "/" + numberConverter.numberToWordConverter(dobAry[2].toCharArray());
            } catch(Exception e) {

            }
            System.out.println(dobInWords);
        }

        RegisterCertificateData registerCertificateData = new RegisterCertificateData();
        registerCertificateData.setId(register.getId());
        registerCertificateData.setDateOfBirth(register.getDateOfBirth());
        registerCertificateData.setDobStrWord(dobInWords);
        registerCertificateData.setWardCode(register.getRegisterBirthPlace().getWardId());
        registerCertificateData.setDateOfReport(register.getDateOfReport());
        registerCertificateData.setRegistrationDate(register.getRegistrationDate());
        registerCertificateData.setCurrentDate(dtDate.format(zdt));
        registerCertificateData.setCurrentTime(dtTime.format(zdt));
       // registerCertificateData.setDobStr(formatter.format(dobDate));
       // registerCertificateData.setRegistrationDateStr(formatter.format(regDate));
       // registerCertificateData.setDateOfReportStr(formatter.format(reportDate));
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
        registerCertificateData.setReportingDate(updatedDate);
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
