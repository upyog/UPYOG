package org.ksmart.birth.birthnac.enrichment;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthnac.service.MdmsForNacService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.newbirth.enrichment.NewBirthResponseEnrichment;
 
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.birthnac.NacApplication;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NacResponseEnrichment {
	
	 private final MdmsUtil mdmsUtil;
	 private final MdmsForNacService mdmsBirthService;
	 private final MdmsTenantService mdmsTenantService;
	 
	 @Autowired
	 NacResponseEnrichment(MdmsTenantService mdmsTenantService, MdmsForNacService mdmsBirthService, MdmsUtil mdmsUtil) {
	        this.mdmsTenantService = mdmsTenantService;
	        this.mdmsBirthService = mdmsBirthService;
	        this.mdmsUtil = mdmsUtil;
	    }

	 
	 public void setNacRequestData(RequestInfo requestInfo, List<NacApplication> result) {
	        Object mdmsData = mdmsUtil.mdmsCall(requestInfo);
	        if(result.size() == 0){
	            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
	        } else if(result.size() >= 1) {
	            result.forEach(nac -> {
	            	nac.setIsWorkflow(true);
	               
	                Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(requestInfo, nac.getTenantId());
	                if (nac.getPlaceofBirthId() != null) {
	                	nac.setPlaceofBirthIdEn(mdmsTenantService.getBirthPlaceEn(mdmsData, nac.getPlaceofBirthId()));
	                	nac.setPlaceofBirthIdMl(mdmsTenantService.getBirthPlaceMl(mdmsData, nac.getPlaceofBirthId()));
	                    mdmsBirthService.setLocationDetails(nac, mdmsDataLoc, mdmsData);
	                    mdmsBirthService.setParentsDetails(nac.getParentsDetails(), mdmsData);
	                }
	                if (nac.getParentAddress().getCountryIdPermanent() != null && nac.getParentAddress().getStateIdPermanent() != null) {
	                    if (nac.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)) {
	                        if (nac.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
	                            mdmsBirthService.setTenantDetails(nac, mdmsData);
	                            Object mdmsDataLocPermanent = mdmsUtil.mdmsCallForLocation(requestInfo, nac.getParentAddress().getPermntInKeralaAdrLBName());
	                            //Country
	                            nac.getParentAddress().setPermtaddressCountry(nac.getParentAddress().getCountryIdPermanent());
	                            nac.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, nac.getParentAddress().getCountryIdPermanent()));
	                            nac.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, nac.getParentAddress().getCountryIdPermanent()));

	                            //State
	                            nac.getParentAddress().setPermtaddressStateName(nac.getParentAddress().getStateIdPermanent());
	                            nac.getParentAddress().setStateIdPermanentEn(mdmsTenantService.getStateNameEn(mdmsData, nac.getParentAddress().getStateIdPermanent()));
	                            nac.getParentAddress().setStateIdPermanentMl(mdmsTenantService.getStateNameMl(mdmsData, nac.getParentAddress().getStateIdPermanent()));

	                            //District
	                            nac.getParentAddress().setPermntInKeralaAdrDistrict(nac.getParentAddress().getDistrictIdPermanent());
	                            nac.getParentAddress().setDistrictIdPermanentEn(mdmsTenantService.getDistrictNameEn(mdmsData, nac.getParentAddress().getDistrictIdPermanent()));
	                            nac.getParentAddress().setDistrictIdPermanentMl(mdmsTenantService.getDistrictNameMl(mdmsData, nac.getParentAddress().getDistrictIdPermanent()));

	                            //Taluk
	                            nac.getParentAddress().setPermntInKeralaAdrTaluk(nac.getParentAddress().getPermntInKeralaAdrTaluk());
	                            nac.getParentAddress().setPermntInKeralaAdrTalukEn(mdmsTenantService.getTalukNameEn(mdmsData, nac.getParentAddress().getPermntInKeralaAdrTaluk()));
	                            nac.getParentAddress().setPermntInKeralaAdrTalukMl(mdmsTenantService.getTalukNameMl(mdmsData, nac.getParentAddress().getPermntInKeralaAdrTaluk()));

	                            //village
	                            nac.getParentAddress().setPermntInKeralaAdrVillage(nac.getParentAddress().getPermntInKeralaAdrVillage());
	                            nac.getParentAddress().setPermntInKeralaAdrVillageEn(mdmsTenantService.getVillageNameEn(mdmsData, nac.getParentAddress().getPermntInKeralaAdrVillage()));
	                            nac.getParentAddress().setPermntInKeralaAdrVillageMl(mdmsTenantService.getVillageNameMl(mdmsData, nac.getParentAddress().getPermntInKeralaAdrVillage()));

	                            //Local Body
	                            nac.getParentAddress().setPermntInKeralaAdrLBNameEn(mdmsTenantService.getTenantNameEn(mdmsData, nac.getParentAddress().getPermntInKeralaAdrLBName()));
	                            nac.getParentAddress().setPermntInKeralaAdrLBNameMl(mdmsTenantService.getTenantNameMl(mdmsData, nac.getParentAddress().getPermntInKeralaAdrLBName()));
	                            
	                            //Post Office
	                            nac.getParentAddress().setPermntInKeralaAdrPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, nac.getParentAddress().getPermntInKeralaAdrPostOffice()));
	                            nac.getParentAddress().setPermntInKeralaAdrPostOfficeMl(mdmsTenantService.getPostOfficeNameMl(mdmsData, nac.getParentAddress().getPermntInKeralaAdrPostOffice()));


	                            nac.getParentAddress().setPermntInKeralaAdrLocalityNameEn(nac.getParentAddress().getLocalityEnPermanent());
	                            nac.getParentAddress().setPermntInKeralaAdrLocalityNameMl(nac.getParentAddress().getLocalityMlPermanent());

	                            nac.getParentAddress().setPermntInKeralaAdrStreetNameEn(nac.getParentAddress().getStreetNameEnPermanent());
	                            nac.getParentAddress().setPermntInKeralaAdrStreetNameMl(nac.getParentAddress().getStreetNameMlPermanent());

	                            nac.getParentAddress().setPermntInKeralaAdrHouseNameEn(nac.getParentAddress().getHouseNameNoEnPermanent());
	                            nac.getParentAddress().setPermntInKeralaAdrHouseNameMl(nac.getParentAddress().getHouseNameNoMlPermanent());

	                            nac.getParentAddress().setPermntInKeralaAdrPostOffice(nac.getParentAddress().getPermntInKeralaAdrPostOffice());
	                            nac.getParentAddress().setPermntInKeralaAdrPincode(nac.getParentAddress().getPinNoPermanent());
	                            
	                          //Ward Name
	                            mdmsBirthService.setLocationForAddressPermanent(nac.getParentAddress(), mdmsDataLocPermanent);


	                        } else {
	                            //Country
	                        	nac.getParentAddress().setPermtaddressCountry(nac.getParentAddress().getCountryIdPermanent());
	                        	nac.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, nac.getParentAddress().getCountryIdPermanent()));
	                        	nac.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, nac.getParentAddress().getCountryIdPermanent()));

	                            //State
	                        	nac.getParentAddress().setPermtaddressStateName(nac.getParentAddress().getStateIdPermanent());
	                        	nac.getParentAddress().setStateIdPermanentEn(mdmsTenantService.getStateNameEn(mdmsData, nac.getParentAddress().getStateIdPermanent()));
	                        	nac.getParentAddress().setStateIdPermanentMl(mdmsTenantService.getStateNameMl(mdmsData, nac.getParentAddress().getStateIdPermanent()));

	                            //District
	                        	nac.getParentAddress().setPermntOutsideKeralaDistrict(nac.getParentAddress().getDistrictIdPermanent());
	                        	nac.getParentAddress().setDistrictIdPermanentEn(mdmsTenantService.getDistrictNameEn(mdmsData, nac.getParentAddress().getDistrictIdPermanent()));
	                        	nac.getParentAddress().setDistrictIdPermanentMl(mdmsTenantService.getDistrictNameMl(mdmsData, nac.getParentAddress().getDistrictIdPermanent()));


	                        	nac.getParentAddress().setPermntOutsideKeralaVillage(nac.getParentAddress().getPermntOutsideKeralaVillage());

	                        	nac.getParentAddress().setPermntOutsideKeralaLocalityNameEn(nac.getParentAddress().getLocalityEnPermanent());
	                        	nac.getParentAddress().setPermntOutsideKeralaLocalityNameMl(nac.getParentAddress().getLocalityMlPermanent());

	                        	nac.getParentAddress().setPermntOutsideKeralaStreetNameEn(nac.getParentAddress().getStreetNameEnPermanent());
	                        	nac.getParentAddress().setPermntOutsideKeralaStreetNameMl(nac.getParentAddress().getStreetNameMlPermanent());

	                        	nac.getParentAddress().setPermntOutsideKeralaHouseNameEn(nac.getParentAddress().getHouseNameNoEnPermanent());
	                        	nac.getParentAddress().setPermntOutsideKeralaHouseNameMl(nac.getParentAddress().getHouseNameNoMlPermanent());
	                        }
	                    } else {
	                        //Country
	                    	nac.getParentAddress().setPermntOutsideIndiaCountry(nac.getParentAddress().getCountryIdPermanent());
	                    	nac.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, nac.getParentAddress().getCountryIdPermanent()));
	                    	nac.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, nac.getParentAddress().getCountryIdPermanent()));

	                    	nac.getParentAddress().setPermntOutsideKeralaVillage(nac.getParentAddress().getPermntOutsideKeralaVillage());
	                    }
	                }
	                if (nac.getParentAddress().getCountryIdPresent() != null && nac.getParentAddress().getStateIdPresent() != null) {
	                    if (nac.getParentAddress().getCountryIdPresent().contains(BirthConstants.COUNTRY_CODE)) {
	                        if (nac.getParentAddress().getStateIdPresent().contains(BirthConstants.STATE_CODE_SMALL)) {
	                        	  Object mdmsDataLocPresent = mdmsUtil.mdmsCallForLocation(requestInfo, nac.getParentAddress().getPresentInsideKeralaLBName());
	                            //Country
	                        	nac.getParentAddress().setPresentaddressCountry(nac.getParentAddress().getCountryIdPresent());
	                        	nac.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, nac.getParentAddress().getCountryIdPresent()));
	                        	nac.getParentAddress().setCountryIdPresentMl(mdmsTenantService.getCountryNameMl(mdmsData, nac.getParentAddress().getCountryIdPresent()));

	                            //State
	                        	nac.getParentAddress().setPresentaddressStateName(nac.getParentAddress().getStateIdPresent());
	                        	nac.getParentAddress().setStateIdPresentEn(mdmsTenantService.getStateNameEn(mdmsData, nac.getParentAddress().getStateIdPresent()));
	                        	nac.getParentAddress().setStateIdPresentMl(mdmsTenantService.getStateNameMl(mdmsData, nac.getParentAddress().getStateIdPresent()));

	                            //District
	                        	nac.getParentAddress().setPresentInsideKeralaDistrict(nac.getParentAddress().getDistrictIdPresent());
	                        	nac.getParentAddress().setDistrictIdPresentEn(mdmsTenantService.getDistrictNameEn(mdmsData, nac.getParentAddress().getDistrictIdPresent()));
	                            nac.getParentAddress().setDistrictIdPresentMl(mdmsTenantService.getDistrictNameMl(mdmsData, nac.getParentAddress().getDistrictIdPresent()));

	                            //Taluk
	                           // birth.getParentAddress().setPresentInsideKeralaTaluk(birth.getParentAddress().setPresentInsideKeralaTaluk());
	                           
	                            nac.getParentAddress().setPresentInsideKeralaTalukEn(mdmsTenantService.getTalukNameEn(mdmsData, nac.getParentAddress().getPresentInsideKeralaTaluk()));
	                            nac.getParentAddress().setPresentInsideKeralaTalukMl(mdmsTenantService.getTalukNameMl(mdmsData, nac.getParentAddress().getPresentInsideKeralaTaluk()));
	                           
	                            //village
	                            nac.getParentAddress().setPresentInsideKeralaVillageEn(mdmsTenantService.getVillageNameEn(mdmsData, nac.getParentAddress().getPresentInsideKeralaVillage()));
	                            nac.getParentAddress().setPresentInsideKeralaVillageMl(mdmsTenantService.getVillageNameMl(mdmsData, nac.getParentAddress().getPresentInsideKeralaVillage()));

	                            //Local Body
	                            nac.getParentAddress().setPresentInsideKeralaLBNameEn(mdmsTenantService.getTenantNameEn(mdmsData, nac.getParentAddress().getPresentInsideKeralaLBName()));
	                            nac.getParentAddress().setPresentInsideKeralaLBNameMl(mdmsTenantService.getTenantNameMl(mdmsData, nac.getParentAddress().getPresentInsideKeralaLBName()));

	                            //Post Office
	                            nac.getParentAddress().setPresentInsideKeralaPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, nac.getParentAddress().getPresentInsideKeralaPostOffice()));
	                            nac.getParentAddress().setPresentInsideKeralaPostOfficeMl(mdmsTenantService.getPostOfficeNameMl(mdmsData, nac.getParentAddress().getPresentInsideKeralaPostOffice()));

	                            nac.getParentAddress().setPresentInsideKeralaLocalityNameEn(nac.getParentAddress().getLocalityEnPresent());
	                            nac.getParentAddress().setPresentInsideKeralaLocalityNameMl(nac.getParentAddress().getLocalityMlPresent());

	                            nac.getParentAddress().setPresentInsideKeralaStreetNameEn(nac.getParentAddress().getStreetNameEnPresent());
	                            nac.getParentAddress().setPresentInsideKeralaStreetNameMl(nac.getParentAddress().getStreetNameMlPresent());

	                            nac.getParentAddress().setPresentInsideKeralaHouseNameEn(nac.getParentAddress().getHouseNameNoEnPresent());
	                            nac.getParentAddress().setPresentInsideKeralaHouseNameMl(nac.getParentAddress().getHouseNameNoMlPresent());

	                            nac.getParentAddress().setPresentInsideKeralaPincode(nac.getParentAddress().getPinNoPresent());
	                            nac.getParentAddress().setPresentInsideKeralaPostOffice(nac.getParentAddress().getPresentInsideKeralaPostOffice());
	                            
	                          //Ward Name
	                            mdmsBirthService.setLocationForAddressPresent(nac.getParentAddress(), mdmsDataLocPresent);

	                        } else {
	                            //Country
	                        	nac.getParentAddress().setPresentaddressCountry(nac.getParentAddress().getCountryIdPresent());
	                        	nac.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, nac.getParentAddress().getCountryIdPresent()));
	                        	nac.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, nac.getParentAddress().getCountryIdPresent()));

	                            //State
	                        	nac.getParentAddress().setPresentaddressStateName(nac.getParentAddress().getStateIdPresent());
	                        	nac.getParentAddress().setStateIdPresentEn(mdmsTenantService.getStateNameEn(mdmsData, nac.getParentAddress().getStateIdPresent()));
	                        	nac.getParentAddress().setStateIdPresentMl(mdmsTenantService.getStateNameMl(mdmsData, nac.getParentAddress().getStateIdPresent()));

	                            //District
	                        	nac.getParentAddress().setPresentOutsideKeralaDistrict(nac.getParentAddress().getDistrictIdPresent());
	                        	nac.getParentAddress().setDistrictIdPresentEn(mdmsTenantService.getDistrictNameEn(mdmsData, nac.getParentAddress().getDistrictIdPresent()));
	                        	nac.getParentAddress().setDistrictIdPresentMl(mdmsTenantService.getDistrictNameMl(mdmsData, nac.getParentAddress().getDistrictIdPresent()));

	                        	nac.getParentAddress().setPresentOutsideKeralaVillageName(nac.getParentAddress().getPresentOutsideKeralaVillageName());

	                        	nac.getParentAddress().setPresentOutsideKeralaPincode(nac.getParentAddress().getPinNoPresent());

	                        	nac.getParentAddress().setPresentOutsideKeralaLocalityNameEn(nac.getParentAddress().getLocalityEnPresent());
	                        	nac.getParentAddress().setPresentOutsideKeralaLocalityNameMl(nac.getParentAddress().getLocalityMlPresent());

	                        	nac.getParentAddress().setPresentOutsideKeralaStreetNameEn(nac.getParentAddress().getStreetNameEnPresent());
	                        	nac.getParentAddress().setPresentOutsideKeralaStreetNameMl(nac.getParentAddress().getStreetNameMlPresent());

	                        	nac.getParentAddress().setPresentOutsideKeralaHouseNameEn(nac.getParentAddress().getHouseNameNoEnPresent());
	                        	nac.getParentAddress().setPresentOutsideKeralaHouseNameMl(nac.getParentAddress().getHouseNameNoMlPresent());

	                        	nac.getParentAddress().setPresentOutsideKeralaCityVilgeEn(nac.getParentAddress().getTownOrVillagePresent());
	                        }
	                    } else {
	                        //Country
	                    	nac.getParentAddress().setPresentOutSideCountry(nac.getParentAddress().getCountryIdPresent());
	                    	nac.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, nac.getParentAddress().getCountryIdPresent()));
	                    	nac.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, nac.getParentAddress().getCountryIdPresent()));

	                    	nac.getParentAddress().setPresentOutSideIndiaadrsVillage(nac.getParentAddress().getPresentOutSideIndiaadrsVillage());
	                    	nac.getParentAddress().setPresentOutSideIndiaadrsCityTown(nac.getParentAddress().getPresentOutSideIndiaadrsCityTown());
	                    }
	                }
	            });
	        }

	    }
	 
}
