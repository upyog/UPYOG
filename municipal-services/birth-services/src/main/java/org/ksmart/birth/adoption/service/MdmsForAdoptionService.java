package org.ksmart.birth.adoption.service;

import lombok.extern.slf4j.Slf4j;
 
import org.ksmart.birth.birthregistry.service.KsmartAddressService;
import org.ksmart.birth.common.services.MdmsLocationService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.web.model.ParentAddress;
import org.ksmart.birth.web.model.ParentsDetail;
import org.ksmart.birth.web.model.adoption.AdoptionApplication;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.ksmart.birth.utils.BirthConstants.*;
@Slf4j
@Service

public class MdmsForAdoptionService {

	  private RestTemplate restTemplate;
	    private final MdmsTenantService mdmsTenantService;

	    private final MdmsLocationService mdmsLocationService;

	    private final KsmartAddressService ksmartAddressService;
	    @Autowired
	    MdmsForAdoptionService(RestTemplate restTemplate, MdmsTenantService mdmsTenantService, MdmsLocationService mdmsLocationService, KsmartAddressService ksmartAddressService) {

	        this.restTemplate = restTemplate;
	        this.mdmsTenantService = mdmsTenantService;
	        this.mdmsLocationService = mdmsLocationService;
	        this.ksmartAddressService = ksmartAddressService;
	    }

	    public void setLocationDetails(AdoptionApplication adoption, Object mdmsDataLoc, Object mdmsData) {
	        if (adoption.getWardId() != null) {
	            String wardEn = mdmsLocationService.getWardNameEn(mdmsDataLoc, adoption.getWardId());
	            String wardMl = mdmsLocationService.getWardNameMl(mdmsDataLoc, adoption.getWardId());
	            String wardNo = mdmsLocationService.getWardNo(mdmsDataLoc, adoption.getWardId());
	            adoption.setWardNameEn(wardEn);
	            adoption.setWardNameMl(wardMl);
	            adoption.setWardNumber(wardNo);
	        }
	        if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
	            String placeEn = mdmsLocationService.getHospitalNameEn(mdmsDataLoc, adoption.getHospitalId()) +" "+ mdmsLocationService.getHospitalAddressEn(mdmsDataLoc, adoption.getHospitalId());
	            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsDataLoc, adoption.getHospitalId()) +" "+ mdmsLocationService.getHospitalAddressMl(mdmsDataLoc, adoption.getHospitalId());
	            adoption.setHospitalName(placeEn);
	            adoption.setHospitalNameMl(placeMl);
	        } else if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
	            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsDataLoc, adoption.getInstitutionNameCode());
	            String placeMl = mdmsLocationService.getInstitutionNameMl(mdmsDataLoc, adoption.getInstitutionNameCode());
	            adoption.setInstitutionId(placeEn);
	            adoption.setInstitutionIdMl(placeMl);
	            setInstitutionDetailsEn(adoption, mdmsData);
	            setInstitutionDetailsMl(adoption, mdmsData);
	        }else if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_HOME)) {
	            //Post Office
	        	adoption.setAdrsPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, adoption.getAdrsPostOffice()));
	        	adoption.setAdrsPostOfficeMl(mdmsTenantService.getPostOfficeNameEn(mdmsData, adoption.getAdrsPostOffice()));

	        }else if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_VEHICLE)) {
	            //Vehicle Type
	            setVehicleTypeEn(adoption, mdmsData,mdmsDataLoc);
	            setVehicleTypeMl(adoption, mdmsData,mdmsDataLoc);
	        } else if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_PUBLIC)) {
	            //Public Place Type
	            setPublicPlaceTypeEn(adoption, mdmsData);
	            setPublicPlaceTypeMl(adoption, mdmsData);
	        }
	        else {

	        }
	    }
	    public void setPublicPlaceTypeEn(AdoptionApplication adoption, Object  mdmsData) {
	        if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_PUBLIC)) {
	            String publicPlaceType = mdmsTenantService.getPublicPlaceTypeEn(mdmsData, adoption.getPublicPlaceType());
	            adoption.setPublicPlaceTypeEn(publicPlaceType);
	        }
	    }
	    public void setPublicPlaceTypeMl(AdoptionApplication adoption, Object  mdmsData) {
	        if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_PUBLIC)) {
	            String publicPlaceType = mdmsTenantService.getPublicPlaceTypeMl(mdmsData, adoption.getPublicPlaceType());
	            adoption.setPublicPlaceTypeMl(publicPlaceType);
	        }
	    }

	    
	    public void setVehicleTypeEn(AdoptionApplication adoption, Object  mdmsData,Object mdmsDataLoc) {
	        if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_VEHICLE)) {
	            String vehicleType = mdmsTenantService.getVehicleTypeEn(mdmsData, adoption.getVehicleTypeid());
	            adoption.setVehicleTypeidEn(vehicleType);
	            
	            
	            String admittedHsopEn = mdmsLocationService.getHospitalNameEn(mdmsDataLoc, adoption.getSetadmittedHospitalEn()) +" "+ mdmsLocationService.getHospitalAddressEn(mdmsDataLoc, adoption.getSetadmittedHospitalEn());
	            adoption.setAdmittedHospitalEn(admittedHsopEn);
	           
	        }
	    }
	    public void setVehicleTypeMl(AdoptionApplication adoption, Object  mdmsData,Object mdmsDataLoc) {
	        if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_VEHICLE)) {
	            String vehicleType = mdmsTenantService.getVehicleTypeMl(mdmsData, adoption.getVehicleTypeid());
	            adoption.setVehicleTypeidMl(vehicleType);
	            
	            String admittedHsopMl = mdmsLocationService.getHospitalNameMl(mdmsDataLoc, adoption.getSetadmittedHospitalEn()) +" "+ mdmsLocationService.getHospitalAddressMl(mdmsDataLoc, adoption.getSetadmittedHospitalEn());
	            adoption.setAdmittedHospitalMl(admittedHsopMl);
	        }
	    }

	    public void setInstitutionDetailsEn(AdoptionApplication adoption, Object  mdmsData) {
	        if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
	            String placeInstType = mdmsTenantService.getInstitutionTypeNameEn(mdmsData, adoption.getInstitutionTypeId());
	            adoption.setInstitution(placeInstType);
	            adoption.setInstitutionTypeEn(placeInstType);
	        }
	    }
	    public void setInstitutionDetailsMl(AdoptionApplication adoption, Object  mdmsData) {
	        if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
	            String placeInstType = mdmsTenantService.getInstitutionTypeNameMl(mdmsData, adoption.getInstitutionTypeId());
	            adoption.setInstitutionIdMl(placeInstType);
	        }
	    }

	    public void setInstitutionDetails(AdoptionApplication adoption, Object  mdmsData) {
	        if (adoption.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
	            String placeInstType = mdmsTenantService.getInstitutionTypeNameEn(mdmsData, adoption.getInstitutionTypeId());
	            adoption.setInstitution(placeInstType);
	        }
	    }

	    public void setTenantDetails(AdoptionApplication adoption, Object  mdmsData) {
	        String lbType = mdmsTenantService.getTenantLbType(mdmsData, adoption.getTenantId());
	        if (lbType.contains(LB_TYPE_CORPORATION) || lbType.contains(LB_TYPE_MUNICIPALITY) ) {
	        	adoption.getParentAddress().setTownOrVillagePresent("TOWN");
	        } else if(lbType.contains(LB_TYPE_GP)) {
	        	adoption.getParentAddress().setTownOrVillagePresent("VILLAGE");
	        } else{}
	    }

	    public void setParentsDetails(ParentsDetail parentsDetail, Object mdmsData) {
	        if (parentsDetail != null) {
	        //Mothers Details
	         if (parentsDetail.getIsMotherInfoMissing().equals(FALSE)) {
	            parentsDetail.setMotherEducationidEn(mdmsTenantService.getQualificatioinEn(mdmsData, parentsDetail.getMotherEducationid()));
	            parentsDetail.setMotherEducationidMl(mdmsTenantService.getQualificatioinMl(mdmsData, parentsDetail.getMotherEducationid()));

	            parentsDetail.setMotherProffessionidEn(mdmsTenantService.getProfessionEn(mdmsData, parentsDetail.getMotherProffessionid()));
	            parentsDetail.setMotherProffessionidMl(mdmsTenantService.getProfessionMl(mdmsData, parentsDetail.getMotherProffessionid()));

	            parentsDetail.setMotherNationalityidEn(mdmsTenantService.getNationalityEn(mdmsData, parentsDetail.getMotherNationalityid()));
	            parentsDetail.setMotherNationalityidMl(mdmsTenantService.getNationalityMl(mdmsData, parentsDetail.getMotherNationalityid()));
	     	 }
	//Father Details
	            
	         if (parentsDetail.getIsFatherInfoMissing().equals(FALSE)) {
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
