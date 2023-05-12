package org.ksmart.birth.newbirth.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.service.KsmartAddressService;
import org.ksmart.birth.common.services.MdmsLocationService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.web.model.ParentAddress;
import org.ksmart.birth.web.model.ParentsDetail;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.ksmart.birth.utils.BirthConstants.*;
@Slf4j
@Service
public class MdmsForNewBirthService {

    private RestTemplate restTemplate;
    private final MdmsTenantService mdmsTenantService;

    private final MdmsLocationService mdmsLocationService;

    private final KsmartAddressService ksmartAddressService;
    @Autowired
    MdmsForNewBirthService(RestTemplate restTemplate, MdmsTenantService mdmsTenantService, MdmsLocationService mdmsLocationService, KsmartAddressService ksmartAddressService) {

        this.restTemplate = restTemplate;
        this.mdmsTenantService = mdmsTenantService;
        this.mdmsLocationService = mdmsLocationService;
        this.ksmartAddressService = ksmartAddressService;
    }

    public void setParentsDetails(ParentsDetail parentsDetail, Object mdmsData) {
        if (parentsDetail != null) {
            if(!parentsDetail.getIsMotherInfoMissing()){
                //Mothers Details
                parentsDetail.setMotherEducationidEn(mdmsTenantService.getQualificatioinEn(mdmsData, parentsDetail.getMotherEducationid()));
                parentsDetail.setMotherEducationidMl(mdmsTenantService.getQualificatioinMl(mdmsData, parentsDetail.getMotherEducationid()));

                parentsDetail.setMotherProffessionidEn(mdmsTenantService.getProfessionEn(mdmsData, parentsDetail.getMotherProffessionid()));
                parentsDetail.setMotherProffessionidMl(mdmsTenantService.getProfessionMl(mdmsData, parentsDetail.getMotherProffessionid()));

                parentsDetail.setMotherNationalityidEn(mdmsTenantService.getNationalityEn(mdmsData, parentsDetail.getMotherNationalityid()));
                parentsDetail.setMotherNationalityidMl(mdmsTenantService.getNationalityMl(mdmsData, parentsDetail.getMotherNationalityid()));

                parentsDetail.setMotherMaritalStatusEn(mdmsTenantService.getMaritalStatusEn(mdmsData, parentsDetail.getMotherMaritalStatus()));
                parentsDetail.setMotherMaritalStatusMl(mdmsTenantService.getMaritalStatusMl(mdmsData, parentsDetail.getMotherMaritalStatus()));
            }
            if(!parentsDetail.getIsFatherInfoMissing()) {

                //Father Details
                parentsDetail.setFatherEucationidEn(mdmsTenantService.getQualificatioinEn(mdmsData, parentsDetail.getFatherEucationid()));
                parentsDetail.setFatherEucationidMl(mdmsTenantService.getQualificatioinMl(mdmsData, parentsDetail.getFatherEucationid()));

                parentsDetail.setFatherProffessionidEn(mdmsTenantService.getProfessionEn(mdmsData, parentsDetail.getFatherProffessionid()));
                parentsDetail.setFatherProffessionidMl(mdmsTenantService.getProfessionMl(mdmsData, parentsDetail.getFatherProffessionid()));

                parentsDetail.setFatherNationalityidEn(mdmsTenantService.getNationalityEn(mdmsData, parentsDetail.getFatherNationalityid()));
                parentsDetail.setFatherNationalityidMl(mdmsTenantService.getNationalityMl(mdmsData, parentsDetail.getFatherNationalityid()));
            }

            parentsDetail.setReligionIdEn(mdmsTenantService.getReligionEn(mdmsData, parentsDetail.getReligionId()));
            parentsDetail.setReligionIdMl(mdmsTenantService.getReligionMl(mdmsData, parentsDetail.getReligionId()));
        }
    }
    public void setLocationDetails(NewBirthApplication birth, Object mdmsDataLoc, Object mdmsData) {
        if (birth.getWardId() != null) {
            String wardEn = mdmsLocationService.getWardNameEn(mdmsDataLoc, birth.getWardId());
            String wardMl = mdmsLocationService.getWardNameMl(mdmsDataLoc, birth.getWardId());
            String wardNo = mdmsLocationService.getWardNo(mdmsDataLoc, birth.getWardId());
            birth.setWardNameEn(wardEn);
            birth.setWardNameMl(wardMl);
            birth.setWardNumber(wardNo);
        }
        if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
            String placeEn = mdmsLocationService.getHospitalNameEn(mdmsDataLoc, birth.getHospitalId()) +" "+ mdmsLocationService.getHospitalAddressEn(mdmsDataLoc, birth.getHospitalId());
            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsDataLoc, birth.getHospitalId()) +" "+ mdmsLocationService.getHospitalAddressMl(mdmsDataLoc, birth.getHospitalId());
            birth.setHospitalName(placeEn);
            birth.setHospitalNameMl(placeMl);
        } else if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsDataLoc, birth.getInstitutionNameCode());
            String placeMl = mdmsLocationService.getInstitutionNameMl(mdmsDataLoc, birth.getInstitutionNameCode());
            birth.setInstitutionId(placeEn);
            birth.setInstitutionIdMl(placeMl);
            setInstitutionDetailsEn(birth, mdmsData);
            setInstitutionDetailsMl(birth, mdmsData);
        }else if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_HOME)) {
            //Post Office
            birth.setAdrsPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getAdrsPostOffice()));
            birth.setAdrsPostOfficeMl(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getAdrsPostOffice()));

        }else if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_VEHICLE)) {
            //Vehicle Type
            setVehicleTypeEn(birth, mdmsData);
            setVehicleTypeMl(birth, mdmsData);
        } else if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_PUBLIC)) {
            //Public Place Type
            setPublicPlaceTypeEn(birth, mdmsData);
            setPublicPlaceTypeMl(birth, mdmsData);
        }
        else {

        }
    }

    public void setInstitutionDetailsEn(NewBirthApplication birth, Object  mdmsData) {
        if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeInstType = mdmsTenantService.getInstitutionTypeNameEn(mdmsData, birth.getInstitutionTypeId());
            birth.setInstitution(placeInstType);
            birth.setInstitutionTypeEn(placeInstType);
        }
    }
    public void setInstitutionDetailsMl(NewBirthApplication birth, Object  mdmsData) {
        if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
            String placeInstType = mdmsTenantService.getInstitutionTypeNameMl(mdmsData, birth.getInstitutionTypeId());
            birth.setInstitutionIdMl(placeInstType);
        }
    }

    public void setVehicleTypeEn(NewBirthApplication birth, Object  mdmsData) {
        if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_VEHICLE)) {
            String vehicleType = mdmsTenantService.getVehicleTypeEn(mdmsData, birth.getVehicleTypeid());
            birth.setVehicleTypeidEn(vehicleType);
        }
    }
    public void setVehicleTypeMl(NewBirthApplication birth, Object  mdmsData) {
        if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_VEHICLE)) {
            String vehicleType = mdmsTenantService.getVehicleTypeMl(mdmsData, birth.getVehicleTypeid());
            birth.setVehicleTypeidMl(vehicleType);
        }
    }
    public void setPublicPlaceTypeEn(NewBirthApplication birth, Object  mdmsData) {
        if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_PUBLIC)) {
            String vehicleType = mdmsTenantService.getPublicPlaceTypeEn(mdmsData, birth.getVehicleTypeid());
            birth.setVehicleTypeidEn(vehicleType);
        }
    }
    public void setPublicPlaceTypeMl(NewBirthApplication birth, Object  mdmsData) {
        if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_PUBLIC)) {
            String vehicleType = mdmsTenantService.getPublicPlaceTypeMl(mdmsData, birth.getVehicleTypeid());
            birth.setVehicleTypeidMl(vehicleType);
        }
    }

    public void setTenantDetails(NewBirthApplication birth, Object  mdmsData) {
        String lbType = mdmsTenantService.getTenantLbType(mdmsData, birth.getTenantId());
        if (lbType.contains(LB_TYPE_CORPORATION) || lbType.contains(LB_TYPE_MUNICIPALITY) ) {
            birth.getParentAddress().setTownOrVillagePresent("TOWN");
        } else if(lbType.contains(LB_TYPE_GP)) {
            birth.getParentAddress().setTownOrVillagePresent("VILLAGE");
        } else{}
    }

    public void setLocationForAddressPermanent(ParentAddress parentAddress, Object mdmsDataLoc) {
        parentAddress.setPermntInKeralaWardNoText(mdmsLocationService.getWardNo(mdmsDataLoc, parentAddress.getPermntInKeralaWardNo()));
        parentAddress.setPermntInKeralaWardNoEn(mdmsLocationService.getWardNameEn(mdmsDataLoc, parentAddress.getPermntInKeralaWardNo()));
        parentAddress.setPermntInKeralaWardNoMl(mdmsLocationService.getWardNameMl(mdmsDataLoc, parentAddress.getPermntInKeralaWardNo()));
    }
    public void setLocationForAddressPresent(ParentAddress parentAddress, Object mdmsDataLoc) {
        parentAddress.setPresentWardText(mdmsLocationService.getWardNo(mdmsDataLoc, parentAddress.getPresentWardNo()));
        parentAddress.setPresentWardNoEn(mdmsLocationService.getWardNameEn(mdmsDataLoc, parentAddress.getPresentWardNo()));
        parentAddress.setPresentWardNoMl(mdmsLocationService.getWardNameMl(mdmsDataLoc, parentAddress.getPresentWardNo()));
    }

}
