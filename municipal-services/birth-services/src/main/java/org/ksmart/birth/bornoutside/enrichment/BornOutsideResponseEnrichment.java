package org.ksmart.birth.bornoutside.enrichment;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.bornoutside.service.MdmsForBornOutsideService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
public class BornOutsideResponseEnrichment {
    private final MdmsUtil mdmsUtil;
    private final MdmsForBornOutsideService mdmsDataService;
    private final MdmsTenantService mdmsTenantService;
    @Autowired
    BornOutsideResponseEnrichment(MdmsTenantService mdmsTenantService, MdmsForBornOutsideService mdmsDataService, MdmsUtil mdmsUtil) {
        this.mdmsTenantService = mdmsTenantService;
        this.mdmsDataService = mdmsDataService;
        this.mdmsUtil = mdmsUtil;
    }
    public void setNewBirthRequestData(RequestInfo requestInfo, List<BornOutsideApplication> result) {
        Object mdmsData = mdmsUtil.mdmsCall(requestInfo);
        if (result.size() == 0) {
            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
        } else if (result.size() >= 1) {
            result.forEach(birth -> {
                birth.setIsWorkflow(true);
                if(birth.getBornOutsideStaticInformant() != null){
                    if (birth.getBornOutsideStaticInformant().getDeliveryMethods() != null) {
                        birth.getBornOutsideStaticInformant().setDeliveryMethodsEn(mdmsTenantService.getDeliveryMethodEn(mdmsData, birth.getBornOutsideStaticInformant().getDeliveryMethods()));
                        birth.getBornOutsideStaticInformant().setDeliveryMethodsMl(mdmsTenantService.getDeliveryMethodMl(mdmsData, birth.getBornOutsideStaticInformant().getDeliveryMethods()));
                    }
                    if (birth.getBornOutsideStaticInformant().getMedicalAttensionSub() != null) {
                        birth.getBornOutsideStaticInformant().setMedicalAttensionSubEn(mdmsTenantService.getMedicalAttentionEn(mdmsData, birth.getBornOutsideStaticInformant().getMedicalAttensionSub()));
                        birth.getBornOutsideStaticInformant().setMedicalAttensionSubMl(mdmsTenantService.getMedicalAttentionMl(mdmsData, birth.getBornOutsideStaticInformant().getMedicalAttensionSub()));
                    }
                }
                if (birth.getPlaceofBirthId()  != null) {
                    if(birth.getCountry() != null) {
                        birth.setCountryEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getCountry()));
                        birth.setCountryMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getCountry()));
                    }
                }
                if (birth.getParentsDetails() != null) {
                    mdmsDataService.setParentsDetails(birth.getParentsDetails(), mdmsData);
                }
                if (birth.getParentAddress().getCountryIdPermanent() != null && birth.getParentAddress().getStateIdPermanent() != null) {
                    if (birth.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)) {
                        if (birth.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
                            mdmsDataService.setTenantDetails(birth, mdmsData);
                            //Country
                            birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());
                            birth.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
                            birth.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPermanent()));

                            //State
                            birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
                            birth.getParentAddress().setStateIdPermanentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPermanent()));
                            birth.getParentAddress().setStateIdPermanentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPermanent()));

                            //District
                            birth.getParentAddress().setPermntInKeralaAdrDistrict(birth.getParentAddress().getDistrictIdPermanent());
                            birth.getParentAddress().setDistrictIdPermanentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));
                            birth.getParentAddress().setDistrictIdPermanentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));

                            //Taluk
                            birth.getParentAddress().setPermntInKeralaAdrTaluk(birth.getParentAddress().getPermntInKeralaAdrTaluk());
                            birth.getParentAddress().setPermntInKeralaAdrTalukEn(mdmsTenantService.getTalukNameEn(mdmsData, birth.getParentAddress().getPermntInKeralaAdrTaluk()));
                            birth.getParentAddress().setPermntInKeralaAdrTalukMl(mdmsTenantService.getTalukNameMl(mdmsData, birth.getParentAddress().getPermntInKeralaAdrTaluk()));

                            //village
                            birth.getParentAddress().setPermntInKeralaAdrVillage(birth.getParentAddress().getPermntInKeralaAdrVillage());
                            birth.getParentAddress().setPermntInKeralaAdrVillageEn(mdmsTenantService.getVillageNameEn(mdmsData, birth.getParentAddress().getPermntInKeralaAdrVillage()));
                            birth.getParentAddress().setPermntInKeralaAdrVillageMl(mdmsTenantService.getVillageNameMl(mdmsData, birth.getParentAddress().getPermntInKeralaAdrVillage()));

                            //Local Body
                            birth.getParentAddress().setPermntInKeralaAdrLBNameEn(mdmsTenantService.getTenantNameEn(mdmsData, birth.getParentAddress().getPermntInKeralaAdrLBName()));
                            birth.getParentAddress().setPermntInKeralaAdrLBNameMl(mdmsTenantService.getTenantNameMl(mdmsData, birth.getParentAddress().getPermntInKeralaAdrLBName()));

                            //Post Office
                            birth.getParentAddress().setPermntInKeralaAdrPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getParentAddress().getPermntInKeralaAdrPostOffice()));
                            birth.getParentAddress().setPermntInKeralaAdrPostOfficeMl(mdmsTenantService.getPostOfficeNameMl(mdmsData, birth.getParentAddress().getPermntInKeralaAdrPostOffice()));


                            birth.getParentAddress().setPermntInKeralaAdrLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
                            birth.getParentAddress().setPermntInKeralaAdrLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());

                            birth.getParentAddress().setPermntInKeralaAdrStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                            birth.getParentAddress().setPermntInKeralaAdrStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                            birth.getParentAddress().setPermntInKeralaAdrHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
                            birth.getParentAddress().setPermntInKeralaAdrHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());

                            birth.getParentAddress().setPermntInKeralaAdrPostOffice(birth.getParentAddress().getPoNoPermanent());

                        }
                    }
                }
//                        else {
//                            //Country
//                            birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());
//                            birth.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
//                            birth.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
//
//                            //State
//                            birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
//                            birth.getParentAddress().setStateIdPermanentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPermanent()));
//                            birth.getParentAddress().setStateIdPermanentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPermanent()));
//
//                            //District
//                            birth.getParentAddress().setPermntOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPermanent());
//                            birth.getParentAddress().setDistrictIdPermanentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));
//                            birth.getParentAddress().setDistrictIdPermanentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));
//
//
//                            birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());
//
//                            birth.getParentAddress().setPermntOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
//                            birth.getParentAddress().setPermntOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());
//
//                            birth.getParentAddress().setPermntOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
//                            birth.getParentAddress().setPermntOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());
//
//                            birth.getParentAddress().setPermntOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
//                            birth.getParentAddress().setPermntOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());
//                        }
//                    } else {
//                        //Country
//                        birth.getParentAddress().setPermntOutsideIndiaCountry(birth.getParentAddress().getCountryIdPermanent());
//                        birth.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
//                        birth.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
//
//                        birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());
//                    }
//                }
                if (birth.getParentAddress().getCountryIdPresent() != null ) {
////                    if (birth.getParentAddress().getCountryIdPresent().contains(BirthConstants.COUNTRY_CODE)) {
////                        if (birth.getParentAddress().getStateIdPresent().contains(BirthConstants.STATE_CODE_SMALL)) {
//
//                            birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());
//                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
//                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));
//
//                            birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());
//
//                            birth.getParentAddress().setPresentInsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());
//
//                            birth.getParentAddress().setPresentInsideKeralaLBName(birth.getParentAddress().getPresentInsideKeralaLBName());
//                            birth.getParentAddress().setPresentInsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
//                            birth.getParentAddress().setPresentInsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());
//
//                            birth.getParentAddress().setPresentInsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
//                            birth.getParentAddress().setPresentInsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());
//
//                            birth.getParentAddress().setPresentInsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
//                            birth.getParentAddress().setPresentInsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());
//
//                            birth.getParentAddress().setPresentInsideKeralaPincode(birth.getParentAddress().getPinNoPresent());
//
//
//                            birth.getParentAddress().setPresentInsideKeralaPostOffice(birth.getParentAddress().getPresentInsideKeralaPostOffice());
//
//                        } else {
//                            birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());
//                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
//                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));
//
//                            birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaVillageName(birth.getParentAddress().getVillageNamePresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaPincode(birth.getParentAddress().getPinNoPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
//                            birth.getParentAddress().setPresentOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPresent());
//                            birth.getParentAddress().setPresentOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
//                            birth.getParentAddress().setPresentOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaCityVilgeEn(birth.getParentAddress().getTownOrVillagePresent());
//
//                        }
//                    } else {
                        birth.getParentAddress().setPresentOutSideCountry(birth.getParentAddress().getCountryIdPresent());
                        birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
                        birth.getParentAddress().setCountryIdPresentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));

                        birth.getParentAddress().setPresentOutSideIndiaadrsVillage(birth.getParentAddress().getVillageNamePresent());
                        birth.getParentAddress().setPresentOutSideIndiaadrsCityTown(birth.getParentAddress().getTownOrVillagePresent());
                    }
//                }
            });
        }

    }
}
