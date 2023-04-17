package org.ksmart.birth.birthregistry.service;


import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    MdmsDataService(RestTemplate restTemplate, MdmsTenantService mdmsTenantService, MdmsLocationService mdmsLocationService, KsmartAddressService ksmartAddressService) {

        this.restTemplate = restTemplate;
        this.mdmsTenantService = mdmsTenantService;
        this.mdmsLocationService = mdmsLocationService;
        this.ksmartAddressService = ksmartAddressService;
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

    public void setBirthPlaceDetails(RegisterBirthDetail registerMain, RegisterCertificateData register, Object  mdmsData) {
        if (register.getBirthPlaceId().contains(BIRTH_PLACE_HOSPITAL)) {
            String placeEn =new StringBuilder().append(mdmsLocationService.getHospitalNameEn(mdmsData, register.getBirthPlaceHospitalId()) )
                                               .append(",")
                                               .append(mdmsLocationService.getHospitalAddressEn(mdmsData, register.getBirthPlaceHospitalId())==null
                                                       ?"":mdmsLocationService.getHospitalAddressEn(mdmsData, register.getBirthPlaceHospitalId())).toString();
            String placeMl =new StringBuilder().append(mdmsLocationService.getHospitalNameMl(mdmsData, register.getBirthPlaceHospitalId()) )
                                               .append(",")
                                               .append(mdmsLocationService.getHospitalAddressMl(mdmsData, register.getBirthPlaceHospitalId())==null
                                                       ?"":mdmsLocationService.getHospitalAddressMl(mdmsData, register.getBirthPlaceHospitalId())).toString();

            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else if (register.getBirthPlaceId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeEn =new StringBuilder().append(mdmsLocationService.getInstitutionNameEn(mdmsData, register.getBirthPlaceInstitutionId()) )
                    .append(",")
                    .append(mdmsLocationService.getInstitutionTypeEn(mdmsData, register.getBirthPlaceInstitutionlTypeId())==null
                            ?"":mdmsLocationService.getInstitutionTypeEn(mdmsData, register.getBirthPlaceInstitutionlTypeId()))
                    .append(",")
                    .append(mdmsLocationService.getInstitutionAddressEn(mdmsData, register.getBirthPlaceInstitutionId())==null
                            ?"":mdmsLocationService.getInstitutionAddressEn(mdmsData, register.getBirthPlaceInstitutionId())).toString();

            String placeMl =new StringBuilder().append(mdmsLocationService.getInstitutionNameMl(mdmsData, register.getBirthPlaceInstitutionId()) )
                    .append(",")
                    .append(mdmsLocationService.getInstitutionTypeMl(mdmsData, register.getBirthPlaceInstitutionlTypeId())==null
                            ?"":mdmsLocationService.getInstitutionTypeMl(mdmsData, register.getBirthPlaceInstitutionlTypeId()))
                    .append(",")
                    .append(mdmsLocationService.getInstitutionAddressMl(mdmsData, register.getBirthPlaceInstitutionId())==null
                            ?"":mdmsLocationService.getInstitutionAddressMl(mdmsData, register.getBirthPlaceInstitutionId())).toString();

            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else if (register.getBirthPlaceId().contains(BIRTH_PLACE_HOME)) {
            String placeEn =new StringBuilder().append(register.getPlaceDetails())
                    .append(",")
                    .toString();
            String placeMl =new StringBuilder().append("").toString();
        }else if (register.getBirthPlaceId().contains(BIRTH_PLACE_VEHICLE)) {
            String placeEn =new StringBuilder().append("").toString();
            String placeMl =new StringBuilder().append("").toString();
        }else if (register.getBirthPlaceId().contains(BIRTH_PLACE_PUBLIC)) {
            String placeEn =new StringBuilder().append("").toString();
            String placeMl =new StringBuilder().append("").toString();
        }else if (register.getBirthPlaceId().contains(BIRTH_PLACE_OTHERS_COUNTRY)) {
            String placeEn =new StringBuilder().append("").toString();
            String placeMl =new StringBuilder().append("").toString();
        }
        else {
        }
    }

    public void setPresentAddressDetailsEn(RegisterBirthDetail register,RegisterCertificateData registerCert, Object  mdmsData) {
        if (register.getRegisterBirthPresent().getCountryId() != null) {
            if (register.getRegisterBirthPresent().getCountryId().contains(COUNTRY_CODE)) {
                ksmartAddressService.getAddressInsideCountryPresentEn(register, registerCert, mdmsData);
                ksmartAddressService.getAddressInsideCountryPresentMl(register, registerCert, mdmsData);
            } else {
                ksmartAddressService.getAddressOutsideCountryPresentEn(register, registerCert, mdmsData);
                ksmartAddressService.getAddressOutsideCountryPresentMl(register, registerCert, mdmsData);
            }
        } else {
            ksmartAddressService.getAddressOutsideCountryPresentEn(register, registerCert, mdmsData);
            ksmartAddressService.getAddressOutsideCountryPresentMl(register, registerCert, mdmsData);
        }

    }

    public void setPremananttAddressDetailsEn(RegisterBirthDetail register,RegisterCertificateData registerCert, Object  mdmsData) {
        if (!register.getRegisterBirthPermanent().getCountryId().isEmpty() || !register.getRegisterBirthPermanent().getCountryId().isEmpty()) {
            if (register.getRegisterBirthPermanent().getCountryId().contains(COUNTRY_CODE)) {
                ksmartAddressService.getAddressInsideCountryPermanentEn(register, registerCert, mdmsData);
                ksmartAddressService.getAddressInsideCountryPermanentMl(register, registerCert, mdmsData);
            } else {
                ksmartAddressService.getAddressOutsideCountryPermanentEn(register, registerCert, mdmsData);
                ksmartAddressService.getAddressOutsideCountryPermanentMl(register, registerCert, mdmsData);
            }
        }   else{
            ksmartAddressService.getAddressOutsideCountryPermanentEn(register, registerCert, mdmsData);
            ksmartAddressService.getAddressOutsideCountryPermanentMl(register, registerCert, mdmsData);
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
