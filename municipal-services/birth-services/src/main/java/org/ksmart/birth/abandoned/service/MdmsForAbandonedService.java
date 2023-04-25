package org.ksmart.birth.abandoned.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.service.KsmartAddressService;
import org.ksmart.birth.common.services.MdmsLocationService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.web.model.abandoned.AbandonedApplication;
import org.ksmart.birth.web.model.abandoned.AbandonedRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.ksmart.birth.utils.BirthConstants.*;
@Slf4j
@Service
public class MdmsForAbandonedService {

    private RestTemplate restTemplate;
    private final MdmsTenantService mdmsTenantService;

    private final MdmsLocationService mdmsLocationService;

    private final KsmartAddressService ksmartAddressService;
    @Autowired
    MdmsForAbandonedService(RestTemplate restTemplate, MdmsTenantService mdmsTenantService, MdmsLocationService mdmsLocationService, KsmartAddressService ksmartAddressService) {

        this.restTemplate = restTemplate;
        this.mdmsTenantService = mdmsTenantService;
        this.mdmsLocationService = mdmsLocationService;
        this.ksmartAddressService = ksmartAddressService;
    }

    public void setLocationDetails(AbandonedApplication birth, Object mdmsData) {
        if (birth.getWardId() != null) {
            String wardEn = mdmsLocationService.getWardNameEn(mdmsData, birth.getWardId());
            String wardMl = mdmsLocationService.getWardNameMl(mdmsData, birth.getWardId());
            String wardNo = mdmsLocationService.getWardNo(mdmsData, birth.getWardId());
            birth.setWardNameEn(wardEn);
            birth.setWardNameMl(wardMl);
            birth.setWardNumber(wardNo);
        }
        if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
            String placeEn = mdmsLocationService.getHospitalAddressEn(mdmsData, birth.getHospitalId());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, birth.getHospitalId());
            birth.setHospitalName(placeEn);
            birth.setHospitalNameMl(placeMl);
        } else if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, birth.getInstitutionNameCode());
            String placeMl = mdmsLocationService.getInstitutionNameMl(mdmsData, birth.getInstitutionNameCode());
            birth.setInstitutionId(placeEn);
            birth.setInstitutionIdMl(placeMl);
        }else { }
    }

    public void setInstitutionDetails(AbandonedApplication birth, Object  mdmsData) {
        if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeInstType = mdmsTenantService.getInstitutionTypeNameEn(mdmsData, birth.getInstitutionTypeId());
            birth.setInstitution(placeInstType);
        }
    }

    public void setTenantDetails(AbandonedApplication birth, Object  mdmsData) {
        String lbType = mdmsTenantService.getTenantLbType(mdmsData, birth.getTenantId());
        if (lbType.contains(LB_TYPE_CORPORATION) || lbType.contains(LB_TYPE_MUNICIPALITY) ) {
           // birth.getParentsDetails().setTownOrVillagePresent("TOWN");
        } else if(lbType.contains(LB_TYPE_GP)) {
            //birth.getParentAddress().setTownOrVillagePresent("VILLAGE");
        } else{}
    }

}
