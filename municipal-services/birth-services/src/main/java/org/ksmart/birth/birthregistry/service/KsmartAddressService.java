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
                    .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress1En()) ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtAddress1En(),true)+", ")
                    .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress2En()) ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtAddress2En(),true)+", ")
                    .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtStateRegionProvinceEn()) ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtStateRegionProvinceEn(),true)+", ")
                    .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtZipcode()) ? "" : register.getRegisterBirthPresent().getOtZipcode()+", ")
                    .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
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
        }
        registerCert.setPresentAddDetailsMl(address);
    }

    public void getAddressOutsideCountryPermanentEn(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder()
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress1En()) ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtAddress1En(),true)+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtAddress2En()) ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtAddress2En(),true)+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtStateRegionProvinceEn()) ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtStateRegionProvinceEn(),true)+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getOtZipcode()) ? "" : register.getRegisterBirthPresent().getOtZipcode()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
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
        }
        registerCert.setPermenantAddDetailsMl(address);
    }
    public void getAddressInsideCountryPresentEn(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";

        address = new StringBuilder().append(StringUtil.isBlank(register.getRegisterBirthPresent().getHouseNameEn()) ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getHouseNameEn(),true)+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getLocalityEn()) || register.getRegisterBirthPresent().getLocalityEn().trim() == "" ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getLocalityEn(),true)+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getStreetNameEn()) || register.getRegisterBirthPresent().getStreetNameEn().trim() == ""? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getStreetNameEn(),true)+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getPoId()) ? "" : mdmsTenantService.getPostOfficeNameEn(mdmsData,register.getRegisterBirthPresent().getPoId())+ " "
                                                                                    +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPresent().getPoId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getDistrictId()) ? "" : mdmsTenantService.getDistrictNameEn(mdmsData, register.getRegisterBirthPresent().getDistrictId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getStateId()) ? "" : mdmsTenantService.getStateNameEn(mdmsData, register.getRegisterBirthPresent().getStateId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        }
        registerCert.setPresentAddDetails(address);
    }

    public void getAddressInsideCountryPresentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(StringUtil.isBlank(register.getRegisterBirthPresent().getHouseNameMl()) ? "" : register.getRegisterBirthPresent().getHouseNameMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getLocalityMl())? "" : register.getRegisterBirthPresent().getLocalityMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getStreetNameMl()) ? "" : register.getRegisterBirthPresent().getStreetNameMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getPoId()) ?"" : mdmsTenantService.getPostOfficeNameMl(mdmsData,register.getRegisterBirthPresent().getPoId())+ " "
                        +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPresent().getPoId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getDistrictId()) ? "" : mdmsTenantService.getDistrictNameMl(mdmsData, register.getRegisterBirthPresent().getDistrictId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getStateId()) ? "" : mdmsTenantService.getStateNameMl(mdmsData, register.getRegisterBirthPresent().getStateId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        }
        registerCert.setPresentAddDetailsMl(address);
    }

    public void getAddressInsideCountryPermanentEn(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(StringUtil.isBlank(register.getRegisterBirthPermanent().getHouseNameEn()) ? "" : StringUtils.capitalize(register.getRegisterBirthPermanent().getHouseNameEn())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getLocalityEn()) ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPermanent().getLocalityEn(),true)+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getStreetNameEn()) ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPermanent().getStreetNameEn(),true)+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getPoId()) ? "" : mdmsTenantService.getPostOfficeNameEn(mdmsData,register.getRegisterBirthPermanent().getPoId())+ " "
                        +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPermanent().getPoId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getDistrictId()) ? "" : mdmsTenantService.getDistrictNameEn(mdmsData, register.getRegisterBirthPermanent().getDistrictId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getStateId()) ? "" : mdmsTenantService.getStateNameEn(mdmsData, register.getRegisterBirthPermanent().getStateId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPermanent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        }
        registerCert.setPermenantAddDetails(address);
    }

    public void getAddressInsideCountryPermanentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(StringUtil.isBlank(register.getRegisterBirthPermanent().getHouseNameMl()) ? "" : register.getRegisterBirthPermanent().getHouseNameMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getLocalityMl()) ||register.getRegisterBirthPermanent().getLocalityMl() == "" ? "" : register.getRegisterBirthPermanent().getLocalityMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getStreetNameMl()) ||register.getRegisterBirthPermanent().getStreetNameMl() == ""? "" : register.getRegisterBirthPermanent().getStreetNameMl()+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getPoId()) ? "" : mdmsTenantService.getPostOfficeNameMl(mdmsData,register.getRegisterBirthPermanent().getPoId())+ " "
                        +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPermanent().getPoId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getDistrictId()) ? "" : mdmsTenantService.getDistrictNameMl(mdmsData, register.getRegisterBirthPermanent().getDistrictId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getStateId()) ? "" : mdmsTenantService.getStateNameMl(mdmsData, register.getRegisterBirthPermanent().getStateId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPermanent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, register.getRegisterBirthPermanent().getCountryId())).toString();
        if(!StringUtil.isBlank(address)){
            address = address.trim().replaceAll(",$", "");
        }
        registerCert.setPermenantAddDetailsMl(address);
    }

}
