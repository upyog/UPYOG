package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.jsoup.internal.StringUtil;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class KsmartAddressService {
    private final MdmsTenantService mdmsTenantService;

    @Autowired
    KsmartAddressService(MdmsTenantService mdmsTenantService) {
        this.mdmsTenantService = mdmsTenantService;
    }
    public void getAddressOutsideCountryPresentEn(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder()
                    .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress1En()) ? "" : StringUtils.capitalize(register.getRegisterBirthPresent().getOtAddress1En())+", ")
                    .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress2En()) ? "" : StringUtils.capitalize(register.getRegisterBirthPresent().getOtAddress2En())+", ")
                    .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtStateRegionProvinceEn()) ? "" : StringUtils.capitalize(register.getRegisterBirthPresent().getOtStateRegionProvinceEn())+", ")
                    .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtZipcode()) ? "" : register.getRegisterBirthPresent().getOtZipcode()+", ")
                    .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        } else{
            address = "Not Recorded";
        }
        registerCert.setPresentAddDetails(address);
    }
    public void getAddressOutsideCountryPresentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder()
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress1Ml()) ? "" : register.getRegisterBirthPresent().getOtAddress1Ml()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress2Ml()) ? "" : register.getRegisterBirthPresent().getOtAddress2Ml()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtStateRegionProvinceMl()) ? "" : register.getRegisterBirthPresent().getOtStateRegionProvinceMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtZipcode()) ? "" : register.getRegisterBirthPresent().getOtZipcode()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        } else{
            address = "രേഖപ്പെടുത്തിയിട്ടില്ല";
        }
        registerCert.setPresentAddDetailsMl(address);
    }

    public void getAddressOutsideCountryPermanentEn(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder()
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress1En()) ? "" : StringUtils.capitalize(register.getRegisterBirthPresent().getOtAddress1En())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress2En()) ? "" : StringUtils.capitalize(register.getRegisterBirthPresent().getOtAddress2En())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtStateRegionProvinceEn()) ? "" : StringUtils.capitalize(register.getRegisterBirthPresent().getOtStateRegionProvinceEn())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtZipcode()) ? "" : register.getRegisterBirthPresent().getOtZipcode()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        } else{
            address = "Not Recorded";
        }
        registerCert.setPermenantAddDetails(address);
    }
    public void getAddressOutsideCountryPermanentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder()
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress1Ml()) ? "" : register.getRegisterBirthPresent().getOtAddress1Ml()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress2Ml()) ? "" : register.getRegisterBirthPresent().getOtAddress2Ml()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtStateRegionProvinceMl()) ? "" : register.getRegisterBirthPresent().getOtStateRegionProvinceMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtZipcode()) ? "" : register.getRegisterBirthPresent().getOtZipcode()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        } else{
            address = "രേഖപ്പെടുത്തിയിട്ടില്ല";
        }
        registerCert.setPermenantAddDetailsMl(address);
    }
    public void getAddressInsideCountryPresentEn(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";

        address = new StringBuilder().append(StringUtil.isBlank(register.getRegisterBirthPresent().getHouseNameEn()) ? "" : StringUtils.capitalize(register.getRegisterBirthPresent().getHouseNameEn())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getResAssNoEn()) ? "  " : StringUtils.capitalize(register.getRegisterBirthPresent().getResAssNoEn())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getLocalityEn()) || register.getRegisterBirthPresent().getLocalityEn().trim() == "" ? "" : StringUtils.capitalize(register.getRegisterBirthPresent().getLocalityEn())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getStreetNameEn()) || register.getRegisterBirthPresent().getStreetNameEn().trim() == ""? "" : StringUtils.capitalize(register.getRegisterBirthPresent().getStreetNameEn())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getPoId()) ? register.getRegisterBirthPresent().getPostofficeEn() : mdmsTenantService.getPostOfficeNameEn(mdmsData,register.getRegisterBirthPresent().getPoId())+ " "
                                                                                    +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPresent().getPoId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getDistrictId()) ? "" : mdmsTenantService.getDistrictNameEn(mdmsData, register.getRegisterBirthPresent().getDistrictId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getStateId()) ? "" : mdmsTenantService.getStateNameEn(mdmsData, register.getRegisterBirthPresent().getStateId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        } else{
            address = "Not Recorded";
        }
        registerCert.setPresentAddDetails(address);
    }

    public void getAddressInsideCountryPresentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(StringUtil.isBlank(register.getRegisterBirthPresent().getHouseNameMl()) ? "" : register.getRegisterBirthPresent().getHouseNameMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getResAssNoMl()) ? "  " : StringUtils.capitalize(register.getRegisterBirthPresent().getResAssNoMl())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getLocalityMl())? "" : register.getRegisterBirthPresent().getLocalityMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getStreetNameMl()) ? "" : register.getRegisterBirthPresent().getStreetNameMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getPoId()) ? register.getRegisterBirthPresent().getPostofficeMl() : mdmsTenantService.getPostOfficeNameMl(mdmsData,register.getRegisterBirthPresent().getPoId())+ " "
                        +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPresent().getPoId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getDistrictId()) ? "" : mdmsTenantService.getDistrictNameMl(mdmsData, register.getRegisterBirthPresent().getDistrictId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getStateId()) ? "" : mdmsTenantService.getStateNameMl(mdmsData, register.getRegisterBirthPresent().getStateId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        } else{
            address = "രേഖപ്പെടുത്തിയിട്ടില്ല";
        }
        registerCert.setPresentAddDetailsMl(address);
    }

    public void getAddressInsideCountryPermanentEn(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(StringUtil.isBlank(register.getRegisterBirthPermanent().getHouseNameEn()) ? "" : StringUtils.capitalize(register.getRegisterBirthPermanent().getHouseNameEn())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getResAssNoEn()) ? "  " : StringUtils.capitalize(register.getRegisterBirthPermanent().getResAssNoEn())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getLocalityEn()) ? "" : StringUtils.capitalize(register.getRegisterBirthPermanent().getLocalityEn())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getStreetNameEn()) ? "" : StringUtils.capitalize(register.getRegisterBirthPermanent().getStreetNameEn())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getPoId()) ? register.getRegisterBirthPermanent().getPostofficeEn() : mdmsTenantService.getPostOfficeNameEn(mdmsData,register.getRegisterBirthPermanent().getPoId())+ " "
                        +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPermanent().getPoId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getDistrictId()) ? "" : mdmsTenantService.getDistrictNameEn(mdmsData, register.getRegisterBirthPermanent().getDistrictId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getStateId()) ? "" : mdmsTenantService.getStateNameEn(mdmsData, register.getRegisterBirthPermanent().getStateId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPermanent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        } else{
            address = "Not Recorded";
        }
        registerCert.setPermenantAddDetails(address);
    }

    public void getAddressInsideCountryPermanentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(StringUtil.isBlank(register.getRegisterBirthPermanent().getHouseNameMl()) ? "" : register.getRegisterBirthPermanent().getHouseNameMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getResAssNoMl()) ? "  " : StringUtils.capitalize(register.getRegisterBirthPermanent().getResAssNoMl())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getLocalityMl()) ||register.getRegisterBirthPermanent().getLocalityMl() == "" ? "" : register.getRegisterBirthPermanent().getLocalityMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getStreetNameMl()) ||register.getRegisterBirthPermanent().getStreetNameMl() == ""? "" : register.getRegisterBirthPermanent().getStreetNameMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getPoId()) ? register.getRegisterBirthPermanent().getPostofficeMl() : mdmsTenantService.getPostOfficeNameMl(mdmsData,register.getRegisterBirthPermanent().getPoId())+ " "
                        +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPermanent().getPoId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getDistrictId()) ? "" : mdmsTenantService.getDistrictNameMl(mdmsData, register.getRegisterBirthPermanent().getDistrictId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getStateId()) ? "" : mdmsTenantService.getStateNameMl(mdmsData, register.getRegisterBirthPermanent().getStateId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, register.getRegisterBirthPermanent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        } else{
            address = "രേഖപ്പെടുത്തിയിട്ടില്ല";
        }
        registerCert.setPermenantAddDetailsMl(address);
    }

}
