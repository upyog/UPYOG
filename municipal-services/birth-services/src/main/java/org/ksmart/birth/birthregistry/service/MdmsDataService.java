package org.ksmart.birth.birthregistry.service;


import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.ksmart.birth.common.services.MdmsLocationService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthAppliactionDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

    @Autowired
    MdmsDataService(RestTemplate restTemplate, MdmsTenantService mdmsTenantService, MdmsLocationService mdmsLocationService) {

        this.restTemplate = restTemplate;

        this.mdmsTenantService = mdmsTenantService;

        this.mdmsLocationService = mdmsLocationService;
    }
    public void setTenantDetails(RegisterCertificateData registerCert, Object  mdmsData) {
        String lbCode = mdmsTenantService.getTenantLbType(mdmsData, registerCert.getTenantId());
        registerCert.setTenantLbType(mdmsTenantService.getLbTypeNameEn(mdmsData,lbCode));
        registerCert.setTenantLbTypeMl(mdmsTenantService.getLbTypeNameMl(mdmsData,lbCode));
        String distCode = mdmsTenantService.getTenantLbType(mdmsData, registerCert.getTenantId());
        registerCert.setTenantDistrict(mdmsTenantService.getDistrictNameEn(mdmsData, distCode));
        registerCert.setTenantDistrictMl(mdmsTenantService.getDistrictNameMl(mdmsData, distCode));
        String talukCode = mdmsTenantService.getTenantTaluk(mdmsData, registerCert.getTenantId());
        registerCert.setTenantTaluk(mdmsTenantService.getTalukNameEn(mdmsData, talukCode));
        registerCert.setTenantTalukMl(mdmsTenantService.getTalukNameMl(mdmsData, talukCode));
        String stateCode = mdmsTenantService.getTenantState(mdmsData, registerCert.getTenantId());
        registerCert.setTenantState(mdmsTenantService.getStateNameEn(mdmsData, stateCode));
        registerCert.setTenantStateMl(mdmsTenantService.getStateNameMl(mdmsData, stateCode));
    }

    public void setBirthPlaceDetails(RegisterCertificateData register, Object  mdmsData) {
        if (register.getBirthPlaceId().contains(BIRTH_PLACE_HOSPITAL)) {
            String placeEn = mdmsLocationService.getHospitalNameEn(mdmsData, register.getBirthPlaceHospitalId());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getBirthPlaceHospitalId());
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);

        } else if (register.getBirthPlaceId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, register.getBirthPlaceInstitutionId()) + ", "
                    + mdmsLocationService.getInstitutionAddressEn(mdmsData, register.getBirthPlaceInstitutionId());
            String placeMl = mdmsLocationService.getInstitutionNameMl(mdmsData, register.getPlaceDetails()) + " , "
                    + mdmsLocationService.getInstitutionAddressMl(mdmsData, register.getPlaceDetails());
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else {
        }
    }

    public void setPresentAddressDetailsEn(RegisterCertificateData register, Object  mdmsData) {
//        if(register.getBirthPlaceId().contains(BIRTH_PLACE_HOSPITAL)){
//            String placeEn = mdmsLocationService.getHospitalNameEn(mdmsData, register.getPlaceDetails());
//            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getPlaceDetails());
//            register.setPlaceDetails(placeEn);
//            register.setPlaceDetailsMl(placeMl);
//
//        }
    }

    public void setPremananttAddressDetailsEn(RegisterCertificateData register, Object  mdmsData) {
        if (register.getPlaceDetails().contains(BIRTH_PLACE_HOSPITAL)) {
            String placeEn = mdmsLocationService.getHospitalNameEn(mdmsData, register.getPlaceDetails());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getPlaceDetails());
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);

        } else if (register.getPlaceDetails().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, register.getPlaceDetails()) + ", "
                    + mdmsLocationService.getInstitutionNameMl(mdmsData, register.getPlaceDetails());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getPlaceDetails()) + " , "
                    + mdmsLocationService.getHospitalAddressMl(mdmsData, register.getPlaceDetails());
            register.setPlaceDetails(placeEn);
            register.setPlaceDetailsMl(placeMl);
        } else {
        }
    }


    public void setKsmartLocationDetails(KsmartBirthAppliactionDetail register,  Object mdmsData) {
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

    public void setKsmartTenantDetails(KsmartBirthAppliactionDetail register,  Object mdmsData) {
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
