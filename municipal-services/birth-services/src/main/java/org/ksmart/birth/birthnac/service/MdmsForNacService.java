package org.ksmart.birth.birthnac.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.service.KsmartAddressService;
import org.ksmart.birth.common.services.MdmsLocationService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.web.model.ParentAddress;
import org.ksmart.birth.web.model.ParentsDetail;
import org.ksmart.birth.web.model.birthnac.NacApplication; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.ksmart.birth.utils.BirthConstants.*;
@Slf4j
@Service
public class MdmsForNacService {
	
	  private RestTemplate restTemplate;
	    private final MdmsTenantService mdmsTenantService;

	    private final MdmsLocationService mdmsLocationService;

	    private final KsmartAddressService ksmartAddressService;
	    @Autowired
	    MdmsForNacService(RestTemplate restTemplate, MdmsTenantService mdmsTenantService, MdmsLocationService mdmsLocationService, KsmartAddressService ksmartAddressService) {

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
	        }
	    }
	    public void setLocationDetails(NacApplication nac, Object mdmsDataLoc, Object mdmsData) {
	        if (nac.getWardId() != null) {
	            String wardEn = mdmsLocationService.getWardNameEn(mdmsDataLoc, nac.getWardId());
	            String wardMl = mdmsLocationService.getWardNameMl(mdmsDataLoc, nac.getWardId());
	            String wardNo = mdmsLocationService.getWardNo(mdmsDataLoc, nac.getWardId());
	            nac.setWardNameEn(wardEn);
	            nac.setWardNameMl(wardMl);
	            nac.setWardNumber(wardNo);
	        }
	        if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
	            String placeEn = mdmsLocationService.getHospitalNameEn(mdmsDataLoc, nac.getHospitalId()) +" "+ mdmsLocationService.getHospitalAddressEn(mdmsDataLoc, nac.getHospitalId());
	            String placeMl = mdmsLocationService.getHospitalNameMl(mdmsDataLoc, nac.getHospitalId()) +" "+ mdmsLocationService.getHospitalAddressMl(mdmsDataLoc, nac.getHospitalId());
	            nac.setHospitalName(placeEn);
	            nac.setHospitalNameMl(placeMl);
	        } else if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
	            String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsDataLoc, nac.getInstitutionNameCode());
	            String placeMl = mdmsLocationService.getInstitutionNameMl(mdmsDataLoc, nac.getInstitutionNameCode());
	            nac.setInstitutionId(placeEn);
	            nac.setInstitutionIdMl(placeMl);
	            setInstitutionDetailsEn(nac, mdmsData);
	            setInstitutionDetailsMl(nac, mdmsData);
	        }else if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_HOME)) {
	            //Post Office
	        	nac.setAdrsPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, nac.getAdrsPostOffice()));
	        	nac.setAdrsPostOfficeMl(mdmsTenantService.getPostOfficeNameEn(mdmsData, nac.getAdrsPostOffice()));

	        }else if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_VEHICLE)) {
	            //Vehicle Type
	            setVehicleTypeEn(nac, mdmsData,mdmsDataLoc);
	            setVehicleTypeMl(nac, mdmsData,mdmsDataLoc);
	        } else if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_PUBLIC)) {
	            //Public Place Type
	            setPublicPlaceTypeEn(nac, mdmsData);
	            setPublicPlaceTypeMl(nac, mdmsData);
	        }
	        else {

	        }
	    }

	    public void setInstitutionDetailsEn(NacApplication nac, Object  mdmsData) {
	        if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
	            String placeInstType = mdmsTenantService.getInstitutionTypeNameEn(mdmsData, nac.getInstitutionTypeId());
	            nac.setInstitution(placeInstType);
	            nac.setInstitutionTypeEn(placeInstType);
	        }
	    }
	    public void setInstitutionDetailsMl(NacApplication nac, Object  mdmsData) {
	        if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
	            String placeInstType = mdmsTenantService.getInstitutionTypeNameMl(mdmsData, nac.getInstitutionTypeId());
	            nac.setInstitutionIdMl(placeInstType);
	        }
	    }

	    public void setVehicleTypeEn(NacApplication nac, Object  mdmsData,Object mdmsDataLoc) {
	        if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_VEHICLE)) {
	            String vehicleType = mdmsTenantService.getVehicleTypeEn(mdmsData, nac.getVehicleTypeid());
	            nac.setVehicleTypeidEn(vehicleType);
	            
	            String admittedHsopEn = mdmsLocationService.getHospitalNameEn(mdmsDataLoc, nac.getSetadmittedHospitalEn()) +" "+ mdmsLocationService.getHospitalAddressEn(mdmsDataLoc, nac.getSetadmittedHospitalEn());
	            nac.setAdmittedHospitalEn(admittedHsopEn);
	        }
	    }
	    public void setVehicleTypeMl(NacApplication nac, Object  mdmsData,Object mdmsDataLoc) {
	        if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_VEHICLE)) {
	            String vehicleType = mdmsTenantService.getVehicleTypeMl(mdmsData, nac.getVehicleTypeid());
	            nac.setVehicleTypeidMl(vehicleType);
	            
	            String admittedHsopMl = mdmsLocationService.getHospitalNameMl(mdmsDataLoc, nac.getSetadmittedHospitalEn()) +" "+ mdmsLocationService.getHospitalAddressMl(mdmsDataLoc, nac.getSetadmittedHospitalEn());
	            nac.setAdmittedHospitalMl(admittedHsopMl);
	        }
	    }
	    public void setPublicPlaceTypeEn(NacApplication nac, Object  mdmsData) {
	        if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_PUBLIC)) {
	            String publicPlaceType = mdmsTenantService.getPublicPlaceTypeEn(mdmsData, nac.getPublicPlaceType());
	            nac.setVehicleTypeidEn(publicPlaceType);
	        }
	    }
	    public void setPublicPlaceTypeMl(NacApplication nac, Object  mdmsData) {
	        if (nac.getPlaceofBirthId().contains(BIRTH_PLACE_PUBLIC)) {
	            String publicPlaceType = mdmsTenantService.getPublicPlaceTypeMl(mdmsData, nac.getPublicPlaceType());
	            nac.setPublicPlaceTypeMl(publicPlaceType);
	        }
	    }

	    public void setTenantDetails(NacApplication nac, Object  mdmsData) {
	        String lbType = mdmsTenantService.getTenantLbType(mdmsData, nac.getTenantId());
	        if (lbType.contains(LB_TYPE_CORPORATION) || lbType.contains(LB_TYPE_MUNICIPALITY) ) {
	        	nac.getParentAddress().setTownOrVillagePresent("TOWN");
	        } else if(lbType.contains(LB_TYPE_GP)) {
	        	nac.getParentAddress().setTownOrVillagePresent("VILLAGE");
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
