package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.ksmart.birth.utils.MdmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date reportDate = new Date(register.getDateOfReport());
        Date dobDate = new Date(register.getDateOfBirth());
        Date regDate = new Date(register.getRegistrationDate());
        Date curDate = new Date();
        RegisterCertificateData registerCertificateData = new RegisterCertificateData();
        registerCertificateData.setId(register.getId());
        registerCertificateData.setDateOfBirth(register.getDateOfBirth());
        registerCertificateData.setDateOfReport(register.getDateOfReport());
        registerCertificateData.setRegistrationDate(register.getRegistrationDate());
        registerCertificateData.setCurrentDate(formatter.format(curDate));
        registerCertificateData.setDobStr(formatter.format(dobDate));
        registerCertificateData.setRegistrationDateStr(formatter.format(regDate));
        registerCertificateData.setDateOfReportStr(formatter.format(reportDate));
        registerCertificateData.setRegistrationNo(register.getRegistrationNo());
        registerCertificateData.setFullName(register.getFullName());
        registerCertificateData.setFullNameMl(register.getFullNameMl());
        registerCertificateData.setGender(register.getGender());
        registerCertificateData.setAckNo(register.getAckNumber());
        registerCertificateData.setRemarksEn(register.getRemarksEn());
        registerCertificateData.setRemarksMl(register.getRemarksMl());
        registerCertificateData.setAadharNo(register.getAadharNo());
        registerCertificateData.setFatherDetails(register.getRegisterBirthFather().getFirstNameEn());
        registerCertificateData.setFatherDetailsMl(register.getRegisterBirthFather().getFirstNameMl());
        registerCertificateData.setMotherDetails(register.getRegisterBirthMother().getFirstNameEn());
        registerCertificateData.setMotherDetailsMl(register.getRegisterBirthMother().getFirstNameMl());
        registerCertificateData.setTenantId(register.getTenantId());
        registerCertificateData.setBirthPlaceId(register.getRegisterBirthPlace().getPlaceOfBirthId());
        registerCertificateData.setBirthPlaceHospitalId(register.getRegisterBirthPlace().getHospitalId());
        registerCertificateData.setBirthPlaceInstitutionId(register.getRegisterBirthPlace().getInstitutionId());
        mdmsDataService.setTenantDetails(registerCertificateData, mdmsData);
        mdmsDataService.setPresentAddressDetailsEn(register, registerCertificateData, mdmsData);
        Object mdmsLocData = mdmsUtil.mdmsCallForLocation(requestInfo, registerCertificateData.getTenantId());
        mdmsDataService.setBirthPlaceDetails(registerCertificateData, mdmsLocData);
        return registerCertificateData;

    }

    private void setBasicDetails() {

    }

    private void setAddressDetails() {

    }
}
