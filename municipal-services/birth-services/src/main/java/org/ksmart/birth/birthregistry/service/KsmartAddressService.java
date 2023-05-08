package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
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
                    .append(register.getRegisterBirthPresent().getOtAddress1En() == null ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtAddress1En(),true)+", ")
                    .append(register.getRegisterBirthPresent().getOtAddress2En()== null ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtAddress2En(),true)+", ")
                    .append(register.getRegisterBirthPresent().getOtStateRegionProvinceEn() == null ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtStateRegionProvinceEn(),true)+", ")
                    .append(register.getRegisterBirthPresent().getOtZipcode()  == null ? "" : register.getRegisterBirthPresent().getOtZipcode()+", ")
                    .append(register.getRegisterBirthPresent().getCountryId() == null ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        registerCert.setPresentAddDetails(address);
    }
    public void getAddressOutsideCountryPresentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder()
                .append(register.getRegisterBirthPresent().getOtAddress1Ml() == null ? "" : register.getRegisterBirthPresent().getOtAddress1Ml()+", ")
                .append(register.getRegisterBirthPresent().getOtAddress2Ml()== null ? "" : register.getRegisterBirthPresent().getOtAddress2Ml()+", ")
                .append(register.getRegisterBirthPresent().getOtStateRegionProvinceMl() == null ? "" : register.getRegisterBirthPresent().getOtStateRegionProvinceMl()+", ")
                .append(register.getRegisterBirthPresent().getOtZipcode()  == null ? "" : register.getRegisterBirthPresent().getOtZipcode()+", ")
                .append(register.getRegisterBirthPresent().getCountryId() == null ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        registerCert.setPresentAddDetailsMl(address);
    }

    public void getAddressOutsideCountryPermanentEn(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder()
                .append(register.getRegisterBirthPresent().getOtAddress1En() == null ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtAddress1En(),true)+", ")
                .append(register.getRegisterBirthPresent().getOtAddress2En()== null ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtAddress2En(),true)+", ")
                .append(register.getRegisterBirthPresent().getOtStateRegionProvinceEn() == null ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getOtStateRegionProvinceEn(),true)+", ")
                .append(register.getRegisterBirthPresent().getOtZipcode()  == null ? "" : register.getRegisterBirthPresent().getOtZipcode()+", ")
                .append(register.getRegisterBirthPresent().getCountryId() == null ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        registerCert.setPermenantAddDetails(address);
    }
    public void getAddressOutsideCountryPermanentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder()
                .append(register.getRegisterBirthPresent().getOtAddress1Ml() == null ? "" : register.getRegisterBirthPresent().getOtAddress1Ml()+", ")
                .append(register.getRegisterBirthPresent().getOtAddress2Ml()== null ? "" : register.getRegisterBirthPresent().getOtAddress2Ml()+", ")
                .append(register.getRegisterBirthPresent().getOtStateRegionProvinceMl() == null ? "" : register.getRegisterBirthPresent().getOtStateRegionProvinceMl()+", ")
                .append(register.getRegisterBirthPresent().getOtZipcode()  == null ? "" : register.getRegisterBirthPresent().getOtZipcode()+", ")
                .append(register.getRegisterBirthPresent().getCountryId() == null ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        registerCert.setPermenantAddDetailsMl(address);
    }
    public void getAddressInsideCountryPresentEn(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";

        address = new StringBuilder().append(register.getRegisterBirthPresent().getHouseNameEn() == null ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getHouseNameEn(),true)+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getLocalityEn()) || register.getRegisterBirthPresent().getLocalityEn().trim() == "" ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getLocalityEn(),true)+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getStreetNameEn()) || register.getRegisterBirthPresent().getStreetNameEn().trim() == ""? "" : CaseUtils.toCamelCase(register.getRegisterBirthPresent().getStreetNameEn(),true)+", ")
                .append(register.getRegisterBirthPresent().getPoId() == null ? "" : mdmsTenantService.getPostOfficeNameEn(mdmsData,register.getRegisterBirthPresent().getPoId())+ " "
                                                                                    +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPresent().getPoId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getDistrictId()) ? "" : mdmsTenantService.getDistrictNameEn(mdmsData, register.getRegisterBirthPresent().getDistrictId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getStateId()) ? "" : mdmsTenantService.getStateNameEn(mdmsData, register.getRegisterBirthPresent().getStateId())+", ")
                .append(StringUtil.isBlank(register.getRegisterBirthPresent().getCountryId()) ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        registerCert.setPresentAddDetails(address);
    }

    public void getAddressInsideCountryPresentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(register.getRegisterBirthPresent().getHouseNameMl() == null ? "" : register.getRegisterBirthPresent().getHouseNameMl()+", ")
                .append(register.getRegisterBirthPresent().getLocalityMl() == null ? "" : register.getRegisterBirthPresent().getLocalityMl()+", ")
                .append(register.getRegisterBirthPresent().getStreetNameMl() == null ? "" : register.getRegisterBirthPresent().getStreetNameMl()+", ")
                .append(register.getRegisterBirthPresent().getPoId() == null ? "" : mdmsTenantService.getPostOfficeNameMl(mdmsData,register.getRegisterBirthPresent().getPoId())+ " "
                        +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPresent().getPoId())+", ")
                .append(register.getRegisterBirthPresent().getDistrictId() == null ? "" : mdmsTenantService.getDistrictNameMl(mdmsData, register.getRegisterBirthPresent().getDistrictId())+", ")
                .append(register.getRegisterBirthPresent().getStateId() == null ? "" : mdmsTenantService.getStateNameMl(mdmsData, register.getRegisterBirthPresent().getStateId())+", ")
                .append(register.getRegisterBirthPresent().getCountryId() == null ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, register.getRegisterBirthPresent().getCountryId())).toString();
        registerCert.setPresentAddDetailsMl(address);
    }

    public void getAddressInsideCountryPermanentEn(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(register.getRegisterBirthPermanent().getHouseNameEn() == null ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPermanent().getHouseNameEn(),true)+", ")
                .append(register.getRegisterBirthPermanent().getLocalityEn() == null ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPermanent().getLocalityEn(),true)+", ")
                .append(register.getRegisterBirthPermanent().getStreetNameEn() == null ? "" : CaseUtils.toCamelCase(register.getRegisterBirthPermanent().getStreetNameEn(),true)+", ")
                .append(register.getRegisterBirthPermanent().getPoId() == null ? "" : mdmsTenantService.getPostOfficeNameEn(mdmsData,register.getRegisterBirthPermanent().getPoId())+ " "
                        +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPermanent().getPoId())+", ")
                .append(register.getRegisterBirthPermanent().getDistrictId() == null ? "" : mdmsTenantService.getDistrictNameEn(mdmsData, register.getRegisterBirthPermanent().getDistrictId())+", ")
                .append(register.getRegisterBirthPermanent().getStateId() == null ? "" : mdmsTenantService.getStateNameEn(mdmsData, register.getRegisterBirthPermanent().getStateId())+", ")
                .append(register.getRegisterBirthPermanent().getCountryId() == null ? "" :  mdmsTenantService.getCountryNameEn(mdmsData, register.getRegisterBirthPermanent().getCountryId())).toString();
        registerCert.setPermenantAddDetails(address);
    }

    public void getAddressInsideCountryPermanentMl(RegisterBirthDetail register, RegisterCertificateData registerCert, Object  mdmsData) {
        String address = "";
        address = new StringBuilder().append(register.getRegisterBirthPermanent().getHouseNameMl() == null ? "" : register.getRegisterBirthPermanent().getHouseNameMl()+", ")
                .append(register.getRegisterBirthPermanent().getLocalityMl() == null||register.getRegisterBirthPermanent().getLocalityMl() == "" ? "" : register.getRegisterBirthPermanent().getLocalityMl()+", ")
                .append(register.getRegisterBirthPermanent().getStreetNameMl() == null||register.getRegisterBirthPermanent().getStreetNameMl() == ""? "" : register.getRegisterBirthPermanent().getStreetNameMl()+", ")
                .append(register.getRegisterBirthPermanent().getPoId() == null ? "" : mdmsTenantService.getPostOfficeNameMl(mdmsData,register.getRegisterBirthPermanent().getPoId())+ " "
                        +mdmsTenantService.getPostOfficePinCode(mdmsData,register.getRegisterBirthPermanent().getPoId())+", ")
                .append(register.getRegisterBirthPermanent().getDistrictId() == null ? "" : mdmsTenantService.getDistrictNameMl(mdmsData, register.getRegisterBirthPermanent().getDistrictId())+", ")
                .append(register.getRegisterBirthPermanent().getStateId() == null ? "" : mdmsTenantService.getStateNameMl(mdmsData, register.getRegisterBirthPermanent().getStateId())+", ")
                .append(register.getRegisterBirthPermanent().getCountryId() == null ? "" :  mdmsTenantService.getCountryNameMl(mdmsData, register.getRegisterBirthPermanent().getCountryId())).toString();
        registerCert.setPermenantAddDetailsMl(address);
    }

}
