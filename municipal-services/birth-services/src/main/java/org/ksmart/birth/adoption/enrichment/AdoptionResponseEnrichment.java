package org.ksmart.birth.adoption.enrichment;


import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.adoption.service.MdmsForAdoptionService;
 
import org.ksmart.birth.common.services.MdmsTenantService;
 
 
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.adoption.AdoptionApplication;
 
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AdoptionResponseEnrichment {
	
	 private final MdmsUtil mdmsUtil;
	 private final MdmsForAdoptionService mdmsBirthService;
	 private final MdmsTenantService mdmsTenantService;
	 
	 @Autowired
	 AdoptionResponseEnrichment(MdmsTenantService mdmsTenantService, MdmsForAdoptionService mdmsBirthService, MdmsUtil mdmsUtil) {
	        this.mdmsTenantService = mdmsTenantService;
	        this.mdmsBirthService = mdmsBirthService;
	        this.mdmsUtil = mdmsUtil;
	    }

	 
	 public void setAdoptionRequestData(RequestInfo requestInfo, List<AdoptionApplication> result) {
	        Object mdmsData = mdmsUtil.mdmsCall(requestInfo);
	        if(result.size() == 0){
	            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
	        } else if(result.size() >= 1) {
	            result.forEach(adoption -> {
	            	adoption.setIsWorkflow(true);
	               
	                Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(requestInfo, adoption.getTenantId());
	                if (adoption.getPlaceofBirthId() != null) {
	                	adoption.setPlaceofBirthIdEn(mdmsTenantService.getBirthPlaceEn(mdmsData, adoption.getPlaceofBirthId()));
	                	adoption.setPlaceofBirthIdMl(mdmsTenantService.getBirthPlaceMl(mdmsData, adoption.getPlaceofBirthId()));
	                    mdmsBirthService.setLocationDetails(adoption, mdmsDataLoc, mdmsData);
	                    mdmsBirthService.setParentsDetails(adoption.getParentsDetails(), mdmsData);
	                }
	                if (adoption.getParentAddress().getCountryIdPermanent() != null ) {
	                    if (adoption.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)) {
	                        if (adoption.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
	                            mdmsBirthService.setTenantDetails(adoption, mdmsData);
	                            Object mdmsDataLocPermanent = mdmsUtil.mdmsCallForLocation(requestInfo, adoption.getParentAddress().getPermntInKeralaAdrLBName());
	                            //Country
	                            adoption.getParentAddress().setPermtaddressCountry(adoption.getParentAddress().getCountryIdPermanent());
	                            adoption.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, adoption.getParentAddress().getCountryIdPermanent()));
	                            adoption.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, adoption.getParentAddress().getCountryIdPermanent()));

	                            //State
	                            adoption.getParentAddress().setPermtaddressStateName(adoption.getParentAddress().getStateIdPermanent());
	                            adoption.getParentAddress().setStateIdPermanentEn(mdmsTenantService.getStateNameEn(mdmsData, adoption.getParentAddress().getStateIdPermanent()));
	                            adoption.getParentAddress().setStateIdPermanentMl(mdmsTenantService.getStateNameMl(mdmsData, adoption.getParentAddress().getStateIdPermanent()));

	                            //District
	                            adoption.getParentAddress().setPermntInKeralaAdrDistrict(adoption.getParentAddress().getDistrictIdPermanent());
	                            adoption.getParentAddress().setDistrictIdPermanentEn(mdmsTenantService.getDistrictNameEn(mdmsData, adoption.getParentAddress().getDistrictIdPermanent()));
	                            adoption.getParentAddress().setDistrictIdPermanentMl(mdmsTenantService.getDistrictNameMl(mdmsData, adoption.getParentAddress().getDistrictIdPermanent()));

	                            //Taluk
	                            adoption.getParentAddress().setPermntInKeralaAdrTaluk(adoption.getParentAddress().getPermntInKeralaAdrTaluk());
	                            adoption.getParentAddress().setPermntInKeralaAdrTalukEn(mdmsTenantService.getTalukNameEn(mdmsData, adoption.getParentAddress().getPermntInKeralaAdrTaluk()));
	                            adoption.getParentAddress().setPermntInKeralaAdrTalukMl(mdmsTenantService.getTalukNameMl(mdmsData, adoption.getParentAddress().getPermntInKeralaAdrTaluk()));

	                            //village
	                            adoption.getParentAddress().setPermntInKeralaAdrVillage(adoption.getParentAddress().getPermntInKeralaAdrVillage());
	                            adoption.getParentAddress().setPermntInKeralaAdrVillageEn(mdmsTenantService.getVillageNameEn(mdmsData, adoption.getParentAddress().getPermntInKeralaAdrVillage()));
	                            adoption.getParentAddress().setPermntInKeralaAdrVillageMl(mdmsTenantService.getVillageNameMl(mdmsData, adoption.getParentAddress().getPermntInKeralaAdrVillage()));

	                            //Local Body
	                            adoption.getParentAddress().setPermntInKeralaAdrLBNameEn(mdmsTenantService.getTenantNameEn(mdmsData, adoption.getParentAddress().getPermntInKeralaAdrLBName()));
	                            adoption.getParentAddress().setPermntInKeralaAdrLBNameMl(mdmsTenantService.getTenantNameMl(mdmsData, adoption.getParentAddress().getPermntInKeralaAdrLBName()));

	                            //Post Office
	                            adoption.getParentAddress().setPermntInKeralaAdrPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, adoption.getParentAddress().getPermntInKeralaAdrPostOffice()));
	                            adoption.getParentAddress().setPermntInKeralaAdrPostOfficeMl(mdmsTenantService.getPostOfficeNameMl(mdmsData, adoption.getParentAddress().getPermntInKeralaAdrPostOffice()));


	                            adoption.getParentAddress().setPermntInKeralaAdrLocalityNameEn(adoption.getParentAddress().getLocalityEnPermanent());
	                            adoption.getParentAddress().setPermntInKeralaAdrLocalityNameMl(adoption.getParentAddress().getLocalityMlPermanent());

	                            adoption.getParentAddress().setPermntInKeralaAdrStreetNameEn(adoption.getParentAddress().getStreetNameEnPermanent());
	                            adoption.getParentAddress().setPermntInKeralaAdrStreetNameMl(adoption.getParentAddress().getStreetNameMlPermanent());

	                            adoption.getParentAddress().setPermntInKeralaAdrHouseNameEn(adoption.getParentAddress().getHouseNameNoEnPermanent());
	                            adoption.getParentAddress().setPermntInKeralaAdrHouseNameMl(adoption.getParentAddress().getHouseNameNoMlPermanent());

	                            adoption.getParentAddress().setPermntInKeralaAdrPostOffice(adoption.getParentAddress().getPermntInKeralaAdrPostOffice());
	                            adoption.getParentAddress().setPermntInKeralaAdrPincode(adoption.getParentAddress().getPinNoPermanent());
	                            
	                          //Ward Name
	                            mdmsBirthService.setLocationForAddressPermanent(adoption.getParentAddress(), mdmsDataLocPermanent);


	                        } else {
	                            //Country
	                        	adoption.getParentAddress().setPermtaddressCountry(adoption.getParentAddress().getCountryIdPermanent());
	                        	adoption.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, adoption.getParentAddress().getCountryIdPermanent()));
	                        	adoption.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, adoption.getParentAddress().getCountryIdPermanent()));

	                            //State
	                        	adoption.getParentAddress().setPermtaddressStateName(adoption.getParentAddress().getStateIdPermanent());
	                        	adoption.getParentAddress().setStateIdPermanentEn(mdmsTenantService.getStateNameEn(mdmsData, adoption.getParentAddress().getStateIdPermanent()));
	                        	adoption.getParentAddress().setStateIdPermanentMl(mdmsTenantService.getStateNameMl(mdmsData, adoption.getParentAddress().getStateIdPermanent()));

	                            //District
	                        	adoption.getParentAddress().setPermntOutsideKeralaDistrict(adoption.getParentAddress().getDistrictIdPermanent());
	                        	adoption.getParentAddress().setDistrictIdPermanentEn(mdmsTenantService.getDistrictNameEn(mdmsData, adoption.getParentAddress().getDistrictIdPermanent()));
	                        	adoption.getParentAddress().setDistrictIdPermanentMl(mdmsTenantService.getDistrictNameMl(mdmsData, adoption.getParentAddress().getDistrictIdPermanent()));


	                        	adoption.getParentAddress().setPermntOutsideKeralaVillage(adoption.getParentAddress().getPermntOutsideKeralaVillage());

	                        	adoption.getParentAddress().setPermntOutsideKeralaLocalityNameEn(adoption.getParentAddress().getLocalityEnPermanent());
	                        	adoption.getParentAddress().setPermntOutsideKeralaLocalityNameMl(adoption.getParentAddress().getLocalityMlPermanent());

	                        	adoption.getParentAddress().setPermntOutsideKeralaStreetNameEn(adoption.getParentAddress().getStreetNameEnPermanent());
	                        	adoption.getParentAddress().setPermntOutsideKeralaStreetNameMl(adoption.getParentAddress().getStreetNameMlPermanent());

	                        	adoption.getParentAddress().setPermntOutsideKeralaHouseNameEn(adoption.getParentAddress().getHouseNameNoEnPermanent());
	                        	adoption.getParentAddress().setPermntOutsideKeralaHouseNameMl(adoption.getParentAddress().getHouseNameNoMlPermanent());
	                        }
	                    } else {
	                        //Country
	                    	adoption.getParentAddress().setPermntOutsideIndiaCountry(adoption.getParentAddress().getCountryIdPermanent());
	                    	adoption.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, adoption.getParentAddress().getCountryIdPermanent()));
	                    	adoption.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, adoption.getParentAddress().getCountryIdPermanent()));

	                    	adoption.getParentAddress().setPermntOutsideKeralaVillage(adoption.getParentAddress().getPermntOutsideKeralaVillage());
	                    }
	                }
	                if (adoption.getParentAddress().getCountryIdPresent() != null  ) {
	                    if (adoption.getParentAddress().getCountryIdPresent().contains(BirthConstants.COUNTRY_CODE)) {
	                        if (adoption.getParentAddress().getStateIdPresent().contains(BirthConstants.STATE_CODE_SMALL)) {
	                        	  Object mdmsDataLocPresent = mdmsUtil.mdmsCallForLocation(requestInfo, adoption.getParentAddress().getPresentInsideKeralaLBName());
	                            //Country
	                        	 
	                        	adoption.getParentAddress().setPresentaddressCountry(adoption.getParentAddress().getCountryIdPresent());
	                        	adoption.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, adoption.getParentAddress().getCountryIdPresent()));
	                        	adoption.getParentAddress().setCountryIdPresentMl(mdmsTenantService.getCountryNameMl(mdmsData, adoption.getParentAddress().getCountryIdPresent()));

	                            //State
	                        	adoption.getParentAddress().setPresentaddressStateName(adoption.getParentAddress().getStateIdPresent());
	                        	adoption.getParentAddress().setStateIdPresentEn(mdmsTenantService.getStateNameEn(mdmsData, adoption.getParentAddress().getStateIdPresent()));
	                        	adoption.getParentAddress().setStateIdPresentMl(mdmsTenantService.getStateNameMl(mdmsData, adoption.getParentAddress().getStateIdPresent()));

	                            //District
	                        	adoption.getParentAddress().setPresentInsideKeralaDistrict(adoption.getParentAddress().getDistrictIdPresent());
	                        	adoption.getParentAddress().setDistrictIdPresentEn(mdmsTenantService.getDistrictNameEn(mdmsData, adoption.getParentAddress().getDistrictIdPresent()));
	                        	adoption.getParentAddress().setDistrictIdPresentMl(mdmsTenantService.getDistrictNameMl(mdmsData, adoption.getParentAddress().getDistrictIdPresent()));

	                            //Taluk
	                           // birth.getParentAddress().setPresentInsideKeralaTaluk(birth.getParentAddress().setPresentInsideKeralaTaluk());
	                        	adoption.getParentAddress().setPresentInsideKeralaTalukEn(mdmsTenantService.getTalukNameEn(mdmsData, adoption.getParentAddress().getPresentInsideKeralaTaluk()));
	                        	adoption.getParentAddress().setPresentInsideKeralaTalukMl(mdmsTenantService.getTalukNameMl(mdmsData, adoption.getParentAddress().getPresentInsideKeralaTaluk()));

	                            //village
	                        	adoption.getParentAddress().setPresentInsideKeralaVillageEn(mdmsTenantService.getVillageNameEn(mdmsData, adoption.getParentAddress().getPresentInsideKeralaVillage()));
	                        	adoption.getParentAddress().setPresentInsideKeralaVillageMl(mdmsTenantService.getVillageNameMl(mdmsData, adoption.getParentAddress().getPresentInsideKeralaVillage()));

	                            //Local Body
	                        	adoption.getParentAddress().setPresentInsideKeralaLBNameEn(mdmsTenantService.getTenantNameEn(mdmsData, adoption.getParentAddress().getPresentInsideKeralaLBName()));
	                        	adoption.getParentAddress().setPresentInsideKeralaLBNameMl(mdmsTenantService.getTenantNameMl(mdmsData, adoption.getParentAddress().getPresentInsideKeralaLBName()));

	                            //Post Office
	                        	adoption.getParentAddress().setPresentInsideKeralaPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, adoption.getParentAddress().getPresentInsideKeralaPostOffice()));
	                        	adoption.getParentAddress().setPresentInsideKeralaPostOfficeMl(mdmsTenantService.getPostOfficeNameMl(mdmsData, adoption.getParentAddress().getPresentInsideKeralaPostOffice()));

	                        	adoption.getParentAddress().setPresentInsideKeralaLocalityNameEn(adoption.getParentAddress().getLocalityEnPresent());
	                        	adoption.getParentAddress().setPresentInsideKeralaLocalityNameMl(adoption.getParentAddress().getLocalityMlPresent());

	                        	adoption.getParentAddress().setPresentInsideKeralaStreetNameEn(adoption.getParentAddress().getStreetNameEnPresent());
	                        	adoption.getParentAddress().setPresentInsideKeralaStreetNameMl(adoption.getParentAddress().getStreetNameMlPresent());

	                        	adoption.getParentAddress().setPresentInsideKeralaHouseNameEn(adoption.getParentAddress().getHouseNameNoEnPresent());
	                        	adoption.getParentAddress().setPresentInsideKeralaHouseNameMl(adoption.getParentAddress().getHouseNameNoMlPresent());

	                        	adoption.getParentAddress().setPresentInsideKeralaPincode(adoption.getParentAddress().getPinNoPresent());
	                        	adoption.getParentAddress().setPresentInsideKeralaPostOffice(adoption.getParentAddress().getPresentInsideKeralaPostOffice());
	                        	
	                        	 //Ward Name
	                            mdmsBirthService.setLocationForAddressPresent(adoption.getParentAddress(), mdmsDataLocPresent);

	                        } else {
	                            //Country
	                        	adoption.getParentAddress().setPresentaddressCountry(adoption.getParentAddress().getCountryIdPresent());
	                        	adoption.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, adoption.getParentAddress().getCountryIdPresent()));
	                        	adoption.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, adoption.getParentAddress().getCountryIdPresent()));

	                            //State
	                        	adoption.getParentAddress().setPresentaddressStateName(adoption.getParentAddress().getStateIdPresent());
	                        	adoption.getParentAddress().setStateIdPresentEn(mdmsTenantService.getStateNameEn(mdmsData, adoption.getParentAddress().getStateIdPresent()));
	                        	adoption.getParentAddress().setStateIdPresentMl(mdmsTenantService.getStateNameMl(mdmsData, adoption.getParentAddress().getStateIdPresent()));

	                            //District
	                        	adoption.getParentAddress().setPresentOutsideKeralaDistrict(adoption.getParentAddress().getDistrictIdPresent());
	                        	adoption.getParentAddress().setDistrictIdPresentEn(mdmsTenantService.getDistrictNameEn(mdmsData, adoption.getParentAddress().getDistrictIdPresent()));
	                        	adoption.getParentAddress().setDistrictIdPresentMl(mdmsTenantService.getDistrictNameMl(mdmsData, adoption.getParentAddress().getDistrictIdPresent()));

	                        	adoption.getParentAddress().setPresentOutsideKeralaVillageName(adoption.getParentAddress().getPresentOutsideKeralaVillageName());

	                        	adoption.getParentAddress().setPresentOutsideKeralaPincode(adoption.getParentAddress().getPinNoPresent());

	                        	adoption.getParentAddress().setPresentOutsideKeralaLocalityNameEn(adoption.getParentAddress().getLocalityEnPresent());
	                        	adoption.getParentAddress().setPresentOutsideKeralaLocalityNameMl(adoption.getParentAddress().getLocalityMlPresent());

	                        	adoption.getParentAddress().setPresentOutsideKeralaStreetNameEn(adoption.getParentAddress().getStreetNameEnPresent());
	                        	adoption.getParentAddress().setPresentOutsideKeralaStreetNameMl(adoption.getParentAddress().getStreetNameMlPresent());

	                        	adoption.getParentAddress().setPresentOutsideKeralaHouseNameEn(adoption.getParentAddress().getHouseNameNoEnPresent());
	                        	adoption.getParentAddress().setPresentOutsideKeralaHouseNameMl(adoption.getParentAddress().getHouseNameNoMlPresent());

	                        	adoption.getParentAddress().setPresentOutsideKeralaCityVilgeEn(adoption.getParentAddress().getTownOrVillagePresent());
	                        }
	                    } else {
	                        //Country	                    	 
	                    	adoption.getParentAddress().setPresentOutSideCountry(adoption.getParentAddress().getCountryIdPresent());
	                    	adoption.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, adoption.getParentAddress().getCountryIdPresent()));
	                    	adoption.getParentAddress().setCountryIdPresentMl(mdmsTenantService.getCountryNameMl(mdmsData, adoption.getParentAddress().getCountryIdPresent()));

	                    	adoption.getParentAddress().setPresentOutSideIndiaadrsVillage(adoption.getParentAddress().getVillageNamePresent());
	                    	adoption.getParentAddress().setPresentOutSideIndiaadrsCityTown(adoption.getParentAddress().getTownOrVillagePresent());
	                    }
	                }
	            });
	        }

	    }
	 
	 
}
