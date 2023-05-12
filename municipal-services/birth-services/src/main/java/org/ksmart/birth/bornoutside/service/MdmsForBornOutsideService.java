package org.ksmart.birth.bornoutside.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.service.KsmartAddressService;
import org.ksmart.birth.common.services.MdmsLocationService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.web.model.ParentsDetail;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.ksmart.birth.utils.BirthConstants.*;

@Slf4j
@Service
public class MdmsForBornOutsideService {

    private RestTemplate restTemplate;
    private final MdmsTenantService mdmsTenantService;

    private final MdmsLocationService mdmsLocationService;

    private final KsmartAddressService ksmartAddressService;
    @Autowired
    MdmsForBornOutsideService(RestTemplate restTemplate, MdmsTenantService mdmsTenantService, MdmsLocationService mdmsLocationService, KsmartAddressService ksmartAddressService) {

        this.restTemplate = restTemplate;
        this.mdmsTenantService = mdmsTenantService;
        this.mdmsLocationService = mdmsLocationService;
        this.ksmartAddressService = ksmartAddressService;
    }

    public void setParentsDetails(ParentsDetail parentsDetail, Object mdmsData) {
        if (parentsDetail != null) {
            //Mothers Details
            parentsDetail.setMotherEducationidEn(mdmsTenantService.getQualificatioinEn(mdmsData, parentsDetail.getMotherEducationid()));
            parentsDetail.setMotherEducationidMl(mdmsTenantService.getQualificatioinMl(mdmsData, parentsDetail.getMotherEducationid()));

            parentsDetail.setMotherProffessionidEn(mdmsTenantService.getProfessionEn(mdmsData, parentsDetail.getMotherProffessionid()));
            parentsDetail.setMotherProffessionidMl(mdmsTenantService.getProfessionMl(mdmsData, parentsDetail.getMotherProffessionid()));

            parentsDetail.setMotherNationalityidEn(mdmsTenantService.getNationalityEn(mdmsData, parentsDetail.getMotherNationalityid()));
            parentsDetail.setMotherNationalityidMl(mdmsTenantService.getNationalityMl(mdmsData, parentsDetail.getMotherNationalityid()));
            //Father Details
            parentsDetail.setFatherEucationidEn(mdmsTenantService.getQualificatioinEn(mdmsData, parentsDetail.getFatherEucationid()));
            parentsDetail.setFatherEucationidMl(mdmsTenantService.getQualificatioinMl(mdmsData, parentsDetail.getFatherEucationid()));

            parentsDetail.setFatherProffessionidEn(mdmsTenantService.getProfessionEn(mdmsData, parentsDetail.getFatherProffessionid()));
            parentsDetail.setFatherProffessionidMl(mdmsTenantService.getProfessionMl(mdmsData, parentsDetail.getFatherProffessionid()));

            parentsDetail.setFatherNationalityidEn(mdmsTenantService.getNationalityEn(mdmsData, parentsDetail.getFatherNationalityid()));
            parentsDetail.setFatherNationalityidMl(mdmsTenantService.getNationalityMl(mdmsData, parentsDetail.getFatherNationalityid()));

            parentsDetail.setReligionIdEn(mdmsTenantService.getReligionEn(mdmsData, parentsDetail.getReligionId()));
            parentsDetail.setReligionIdMl(mdmsTenantService.getReligionMl(mdmsData, parentsDetail.getReligionId()));

            parentsDetail.setMotherMaritalStatusEn(mdmsTenantService.getMaritalStatusEn(mdmsData, parentsDetail.getMotherMaritalStatus()));
            parentsDetail.setMotherMaritalStatusMl(mdmsTenantService.getMaritalStatusMl(mdmsData, parentsDetail.getMotherMaritalStatus()));
        }
    }
    public void setTenantDetails(BornOutsideApplication birth, Object  mdmsData) {
        String lbType = mdmsTenantService.getTenantLbType(mdmsData, birth.getTenantId());
        if (lbType.contains(LB_TYPE_CORPORATION) || lbType.contains(LB_TYPE_MUNICIPALITY) ) {
            birth.getParentAddress().setTownOrVillagePresent("TOWN");
        } else if(lbType.contains(LB_TYPE_GP)) {
            birth.getParentAddress().setTownOrVillagePresent("VILLAGE");
        } else{}
    }
}
