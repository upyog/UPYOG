package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.ksmart.birth.common.services.MdmsLocationService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.web.model.abandoned.AbandonedApplication;
import org.ksmart.birth.web.model.adoption.AdoptionApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.ksmart.birth.web.model.stillbirth.StillBirthApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.ksmart.birth.utils.BirthConstants.*;

@Slf4j
@Service
public class MdmsDataService {

     private RestTemplate restTemplate;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsUrl;

    @Value("${egov.mdms.module.name}")
    private String moduleName;

    private final MdmsTenantService mdmsTenantService;

    private final MdmsLocationService mdmsLocationService;

    private final KsmartAddressService ksmartAddressService;
    private final TcsAddressService tcsAddressService;

    @Autowired
    MdmsDataService(RestTemplate restTemplate, MdmsTenantService mdmsTenantService, MdmsLocationService mdmsLocationService,
                    KsmartAddressService ksmartAddressService, TcsAddressService tcsAddressService) {
        this.restTemplate = restTemplate;
        this.mdmsTenantService = mdmsTenantService;
        this.mdmsLocationService = mdmsLocationService;
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
        if (registerCert.getAckNo().startsWith(TCS_CODE_1, 0) || registerCert.getAckNo().startsWith(TCS_CODE_2, 0)) {
            if(register.getRegisterBirthPresent().getHouseNameMl().contains(TCS_ERROR_STR)) {
                tcsAddressService.trimAddressHouseNamePresent(register.getRegisterBirthPresent().getHouseNameMl(), register, TCS_ERROR_STR);
            }
        }
        if(register.getIsMigrated()){
            newDate = validateRegistrationDatePresent(registerCert, regDate);
        }
        if(newDate) {
            if (!StringUtils.isEmpty(register.getRegisterBirthPresent().getCountryId())) {
                if (register.getRegisterBirthPresent().getCountryId().contains(COUNTRY_CODE)) {
                    ksmartAddressService.getAddressInsideCountryPresentEn(register, registerCert, mdmsData);
                    ksmartAddressService.getAddressInsideCountryPresentMl(register, registerCert, mdmsData);
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
        if (registerCert.getAckNo().startsWith(TCS_CODE_1, 0) || registerCert.getAckNo().startsWith(TCS_CODE_2, 0)) {
            if(register.getRegisterBirthPermanent().getHouseNameMl().contains(TCS_ERROR_STR)) {
                tcsAddressService.trimAddressHouseNamePermanent(register.getRegisterBirthPermanent().getHouseNameMl(), register, TCS_ERROR_STR);
            }
        }
        if(register.getIsMigrated()){
            newDate = validateRegistrationDatePermanenet(registerCert, regDate);
        }
        if(newDate) {
            if (!StringUtils.isEmpty(register.getRegisterBirthPermanent().getCountryId())) {
                if (register.getRegisterBirthPermanent().getCountryId().contains(COUNTRY_CODE)) {
                    ksmartAddressService.getAddressInsideCountryPermanentEn(register, registerCert, mdmsData);
                    ksmartAddressService.getAddressInsideCountryPermanentMl(register, registerCert, mdmsData);
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
    public void setKsmartLocationDetails(NewBirthApplication register, Object mdmsData) {
         if (register.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
            String placeEn = mdmsLocationService.getHospitalAddressEn(mdmsData, register.getHospitalId());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getHospitalId());
            register.setHospitalName(placeEn);
            register.setHospitalNameMl(placeMl);
        } else if (register.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, register.getInstitutionId());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getInstitutionId());
            register.setInstitution(placeEn);
            register.setInstitutionIdMl(placeMl);
        } else {
        }
    }
    public void setAdoptionLocationDetails(AdoptionApplication register, Object mdmsData) {
        if (register.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
           String placeEn = mdmsLocationService.getHospitalAddressEn(mdmsData, register.getHospitalId());
           String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getHospitalId());
           register.setHospitalName(placeEn);
           register.setHospitalNameMl(placeMl);
       } else if (register.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
           String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, register.getInstitutionId());
           String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getInstitutionId());
           register.setInstitution(placeEn);
           register.setInstitutionIdMl(placeMl);
       } else {
       }
   }

    public void setAbandonedLocationDetails(AbandonedApplication register, Object mdmsData) {
        if (register.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
            String placeEn = mdmsLocationService.getHospitalAddressEn(mdmsData, register.getHospitalId());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getHospitalId());
            register.setHospitalName(placeEn);
            register.setHospitalNameMl(placeMl);
        } else if (register.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, register.getInstitutionId());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getInstitutionId());
            register.setInstitution(placeEn);
            register.setInstitutionIdMl(placeMl);
        } else {
        }
    }


    public void setStillLocationDetails(StillBirthApplication register, Object mdmsData) {
        if (register.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
            String placeEn = mdmsLocationService.getHospitalAddressEn(mdmsData, register.getHospitalId());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getHospitalId());
            register.setHospitalName(placeEn);
            register.setHospitalNameMl(placeMl);
        } else if (register.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, register.getInstitutionId());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getInstitutionId());
            register.setInstitution(placeEn);
            register.setInstitutionIdMl(placeMl);
        } else {
        }
    }

    public void setOutLocationDetails(BornOutsideApplication register, Object mdmsData) {
//        if (register.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
//            String placeEn = mdmsLocationService.getHospitalAddressEn(mdmsData, register.getHospitalId());
//            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getHospitalId());
//            register.setHospitalName(placeEn);
//            register.setHospitalNameMl(placeMl);
//        } else if (register.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
//            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, register.getInstitutionId());
//            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getInstitutionId());
//            register.setInstitution(placeEn);
//            register.setInstitutionIdMl(placeMl);
//        } else {
//        }
    }
    public void setKsmartTenantDetails(NewBirthApplication register,  Object mdmsData) {
//        if (register.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
//            String placeEn = mdmsLocationService.getHospitalAddressEn(mdmsData, register.getHospitalId());
//            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getHospitalId());
//            register.setHospitalName(placeEn);
//            register.setHospitalNameMl(placeMl);
//        } else if (register.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
//            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, register.getInstitutionId());
//            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getInstitutionId());
//            register.setInstitution(placeEn);
//            register.setInstitutionIdMl(placeMl);
//        } else {
//        }
    }




}
