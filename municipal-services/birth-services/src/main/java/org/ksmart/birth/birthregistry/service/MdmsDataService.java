package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.ksmart.birth.common.services.MdmsLocationService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.ksmart.birth.utils.BirthConstants.*;

@Slf4j
@Service
public class MdmsDataService {

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsUrl;

    @Value("${egov.mdms.module.name}")
    private String moduleName;

    private final MdmsTenantService mdmsTenantService;
    private final KsmartAddressService ksmartAddressService;
    private final TcsAddressService tcsAddressService;

    @Autowired
    MdmsDataService(MdmsTenantService mdmsTenantService,  KsmartAddressService ksmartAddressService, TcsAddressService tcsAddressService) {
        this.mdmsTenantService = mdmsTenantService;
        this.ksmartAddressService = ksmartAddressService;
        this.tcsAddressService = tcsAddressService;
    }
    public void setTenantDetails(RegisterCertificateData registerCert, Object  mdmsData) {
        String lbCode = mdmsTenantService.getTenantLbType(mdmsData, registerCert.getTenantId());
        registerCert.setTenantLbType(mdmsTenantService.getLbTypeNameEn(mdmsData,lbCode));
        registerCert.setTenantLbTypeMl(mdmsTenantService.getLbTypeNameMl(mdmsData,lbCode));
        String distCode = mdmsTenantService.getTenantDistrict(mdmsData, registerCert.getTenantId());
        registerCert.setTenantDistrict(mdmsTenantService.getDistrictNameEn(mdmsData, distCode));
        registerCert.setTenantDistrictMl(mdmsTenantService.getDistrictNameMl(mdmsData, distCode));
        String talukCode = mdmsTenantService.getTenantTaluk(mdmsData, registerCert.getTenantId());
        registerCert.setTenantTaluk(mdmsTenantService.getTalukNameEn(mdmsData, talukCode));
        registerCert.setTenantTalukMl(mdmsTenantService.getTalukNameMl(mdmsData, talukCode));
        String stateCode = mdmsTenantService.getTenantState(mdmsData, registerCert.getTenantId());
        registerCert.setTenantState(mdmsTenantService.getStateNameEn(mdmsData, stateCode));
        registerCert.setTenantStateMl(mdmsTenantService.getStateNameMl(mdmsData, stateCode));
    }

    private boolean validateRegistrationDatePresent(RegisterCertificateData registerCert,Long regDate) { //registration_date < '2007-06-01'---->address not available(present_address)

        if(regDate < 1180636200000L){
            registerCert.setPresentAddDetails("Not Available");
            registerCert.setPresentAddDetailsMl("ലഭ്യമല്ല");
            return  false;
        }
        return  true;
    }

    private boolean validateRegistrationDatePermanenet(RegisterCertificateData registerCert,Long regDate) { // registration_date > 1999 and registration_date < '2007-06-01'---->address not available(permanent_address)
        if(regDate > 915129000000L && regDate < 1180636200000L){
            registerCert.setPermenantAddDetails("Not Available");
            registerCert.setPermenantAddDetailsMl("ലഭ്യമല്ല");
            return false;
        }
        return true;
    }
    public void setPresentAddressDetailsEn(RegisterBirthDetail register,RegisterCertificateData registerCert, Object  mdmsData, Long regDate) {
        Boolean newDate = true;
        if(register.getIsMigrated()){
            newDate = validateRegistrationDatePresent(registerCert, regDate);
        }
        if(newDate) {
            if (!StringUtils.isEmpty(register.getRegisterBirthPresent().getCountryId())) {
                if (register.getRegisterBirthPresent().getCountryId().contains(COUNTRY_CODE)) {
                    ksmartAddressService.getAddressInsideCountryPresentEn(register, registerCert, mdmsData);
                    if(registerCert.getMigratedFrom().equals(MIGRATED_FROM_TCS)) {
                        tcsAddressService.getAddressInsideCountryPresentMl(register, registerCert, mdmsData);
                    } else{
                        ksmartAddressService.getAddressInsideCountryPresentMl(register, registerCert, mdmsData);
                    }
                } else {
                    ksmartAddressService.getAddressOutsideCountryPresentEn(register, registerCert, mdmsData);
                    ksmartAddressService.getAddressOutsideCountryPresentMl(register, registerCert, mdmsData);
                }
            } else {
                ksmartAddressService.getAddressInsideCountryPresentEn(register, registerCert, mdmsData);
                ksmartAddressService.getAddressInsideCountryPresentMl(register, registerCert, mdmsData);
            }
        }
    }

    public void setPremananttAddressDetailsEn(RegisterBirthDetail register,RegisterCertificateData registerCert, Object  mdmsData, Long regDate) {
        Boolean newDate = true;

        if(register.getIsMigrated()){
            newDate = validateRegistrationDatePermanenet(registerCert, regDate);
        }
        if(newDate) {
            if (!StringUtils.isEmpty(register.getRegisterBirthPermanent().getCountryId())) {
                if (register.getRegisterBirthPermanent().getCountryId().contains(COUNTRY_CODE)) {
                    ksmartAddressService.getAddressInsideCountryPermanentEn(register, registerCert, mdmsData);
                    if(registerCert.getMigratedFrom().equals(MIGRATED_FROM_TCS)) {
                        tcsAddressService.getAddressInsideCountryPermanentMl(register, registerCert, mdmsData);
                    } else{
                        ksmartAddressService.getAddressInsideCountryPermanentMl(register, registerCert, mdmsData);
                    }
                } else {
                    ksmartAddressService.getAddressOutsideCountryPermanentEn(register, registerCert, mdmsData);
                    ksmartAddressService.getAddressOutsideCountryPermanentMl(register, registerCert, mdmsData);
                }
            } else {
                ksmartAddressService.getAddressInsideCountryPermanentEn(register, registerCert, mdmsData);
                ksmartAddressService.getAddressInsideCountryPermanentMl(register, registerCert, mdmsData);
            }
        }
    }
}
