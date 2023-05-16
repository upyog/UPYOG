package org.ksmart.birth.stillbirth.enrichment;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.newbirth.service.MdmsForNewBirthService;
import org.ksmart.birth.stillbirth.service.MdmsForStillBirthService;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.InitiatorDetail;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.stillbirth.StillBirthApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.BIRTH_PLACE_HOSPITAL;

@Slf4j
@Component
public class StillBirthResponseEnrichment {

    private final MdmsUtil mdmsUtil;
    private final MdmsForStillBirthService mdmsBirthService;
    private final MdmsTenantService mdmsTenantService;
    @Autowired
    StillBirthResponseEnrichment(MdmsTenantService mdmsTenantService, MdmsForStillBirthService mdmsBirthService, MdmsUtil mdmsUtil) {
        this.mdmsTenantService = mdmsTenantService;
        this.mdmsBirthService = mdmsBirthService;
        this.mdmsUtil = mdmsUtil;
    }

    public void setNewBirthRequestData(RequestInfo requestInfo, List<StillBirthApplication> result) {
        Object mdmsData = mdmsUtil.mdmsCall(requestInfo);
        if(result.size() == 0){
            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
        } else if(result.size() >= 1) {
            result.forEach(birth -> {
                birth.setIsWorkflow(true);
                if(birth.getDeliveryMethods() != null) {
                    birth.setDeliveryMethodsEn(mdmsTenantService.getDeliveryMethodEn(mdmsData, birth.getDeliveryMethods()));
                    birth.setDeliveryMethodsMl(mdmsTenantService.getDeliveryMethodMl(mdmsData, birth.getDeliveryMethods()));
                }
                if(birth.getMedicalAttensionSub() != null) {
                    birth.setMedicalAttensionSubEn(mdmsTenantService.getMedicalAttentionEn(mdmsData, birth.getMedicalAttensionSub()));
                    birth.setMedicalAttensionSubMl(mdmsTenantService.getMedicalAttentionMl(mdmsData, birth.getMedicalAttensionSub()));
                }
                if(birth.getCauseFoetalDeath() != null) {
                    birth.setCauseFoetalDeathEn(mdmsTenantService.getCauseFoetalDeathdEn(mdmsData, birth.getCauseFoetalDeath()));
                    birth.setCauseFoetalDeathMl(mdmsTenantService.getCauseFoetalDeathdMl(mdmsData, birth.getCauseFoetalDeath()));
                }
                Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(requestInfo, birth.getTenantId());
                if (birth.getPlaceofBirthId() != null) {
                    birth.setCountryidEn(mdmsTenantService.getBirthPlaceEn(mdmsData, birth.getCountryid()));
                    birth.setCountryidMl(mdmsTenantService.getBirthPlaceMl(mdmsData, birth.getCountryid()));
                    mdmsBirthService.setLocationDetails(birth, mdmsDataLoc, mdmsData);
                }
                if(birth.getParentsDetails()!= null) {
                    mdmsBirthService.setParentsDetails(birth.getParentsDetails(), mdmsData);
                }
                if(birth.getInitiatorDetails() != null) {
                    InitiatorDetail initiatorDetail = birth.getInitiatorDetails();
                    initiatorDetail.setInitiatorEn(mdmsTenantService.getInitiatorEn(mdmsData, initiatorDetail.getInitiator()));
                    initiatorDetail.setInitiatorMl(mdmsTenantService.getInitiatorEn(mdmsData, initiatorDetail.getInitiator()));

                    initiatorDetail.setRelationEn(mdmsTenantService.getRelationEn(mdmsData, initiatorDetail.getRelation()));
                    initiatorDetail.setRelationMl(mdmsTenantService.getRelationMl(mdmsData, initiatorDetail.getRelation()));

                    initiatorDetail.setInitiatorDesiEn(mdmsTenantService.getCareTakerEn(mdmsData, initiatorDetail.getInitiatorDesi()));
                    initiatorDetail.setInitiatorDesiMl(mdmsTenantService.getCareTakerMl(mdmsData, initiatorDetail.getInitiatorDesi()));

                    if (birth.getPlaceofBirthId() != null) {
                        if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
                            initiatorDetail.setIpopListEn(mdmsTenantService.getOpIpEn(mdmsData, initiatorDetail.getIpopList()));
                            initiatorDetail.setIpopListMl(mdmsTenantService.getOpIpMl(mdmsData, initiatorDetail.getIpopList()));
                        }
                    }
                }
                if (birth.getParentAddress().getCountryIdPermanent() != null && birth.getParentAddress().getStateIdPermanent() != null) {
                    if (birth.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)) {
                        if (birth.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
                            mdmsBirthService.setTenantDetails(birth, mdmsData);
                            Object mdmsDataLocPermanent = mdmsUtil.mdmsCallForLocation(requestInfo, birth.getParentAddress().getPermntInKeralaAdrLBName());
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

                            //Village
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
                            //Ward Name
                            mdmsBirthService.setLocationForAddressPermanent(birth.getParentAddress(), mdmsDataLocPermanent);

                        } else {
                            //Country
                            birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());
                            birth.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
                            birth.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPermanent()));

                            //State
                            birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
                            birth.getParentAddress().setStateIdPermanentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPermanent()));
                            birth.getParentAddress().setStateIdPermanentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPermanent()));

                            //District
                            birth.getParentAddress().setPermntOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPermanent());
                            birth.getParentAddress().setDistrictIdPermanentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));
                            birth.getParentAddress().setDistrictIdPermanentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));


                            birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());

                            birth.getParentAddress().setPermntOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
                            birth.getParentAddress().setPermntOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());

                            birth.getParentAddress().setPermntOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                            birth.getParentAddress().setPermntOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                            birth.getParentAddress().setPermntOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
                            birth.getParentAddress().setPermntOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());
                        }
                    } else {
                        //Country
                        birth.getParentAddress().setPermntOutsideIndiaCountry(birth.getParentAddress().getCountryIdPermanent());
                        birth.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
                        birth.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPermanent()));

                        birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());
                    }
                }
                if (birth.getParentAddress().getCountryIdPresent() != null && birth.getParentAddress().getStateIdPresent() != null) {
                    if (birth.getParentAddress().getCountryIdPresent().contains(BirthConstants.COUNTRY_CODE)) {
                        if (birth.getParentAddress().getStateIdPresent().contains(BirthConstants.STATE_CODE_SMALL)) {
                            Object mdmsDataLocPresent = mdmsUtil.mdmsCallForLocation(requestInfo, birth.getParentAddress().getPresentInsideKeralaLBName());
                            //Country
                            birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());
                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
                            birth.getParentAddress().setCountryIdPresentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));

                            //State
                            birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());
                            birth.getParentAddress().setStateIdPresentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPresent()));
                            birth.getParentAddress().setStateIdPresentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPresent()));

                            //District
                            birth.getParentAddress().setPresentInsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());
                            birth.getParentAddress().setDistrictIdPresentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPresent()));
                            birth.getParentAddress().setDistrictIdPresentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPresent()));

                            //Taluk
                            // birth.getParentAddress().setPresentInsideKeralaTaluk(birth.getParentAddress().setPresentInsideKeralaTaluk());
                            birth.getParentAddress().setPresentInsideKeralaTalukEn(mdmsTenantService.getTalukNameEn(mdmsData, birth.getParentAddress().getPresentInsideKeralaTaluk()));
                            birth.getParentAddress().setPresentInsideKeralaTalukMl(mdmsTenantService.getTalukNameMl(mdmsData, birth.getParentAddress().getPresentInsideKeralaTaluk()));

                            //village
                            birth.getParentAddress().setPresentInsideKeralaVillageEn(mdmsTenantService.getVillageNameEn(mdmsData, birth.getParentAddress().getPresentInsideKeralaVillage()));
                            birth.getParentAddress().setPresentInsideKeralaVillageMl(mdmsTenantService.getVillageNameMl(mdmsData, birth.getParentAddress().getPresentInsideKeralaVillage()));

                            //Local Body
                            birth.getParentAddress().setPresentInsideKeralaLBNameEn(mdmsTenantService.getTenantNameEn(mdmsData, birth.getParentAddress().getPresentInsideKeralaLBName()));
                            birth.getParentAddress().setPresentInsideKeralaLBNameMl(mdmsTenantService.getTenantNameMl(mdmsData, birth.getParentAddress().getPresentInsideKeralaLBName()));

                            //Post Office
                            birth.getParentAddress().setPresentInsideKeralaPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getParentAddress().getPresentInsideKeralaPostOffice()));
                            birth.getParentAddress().setPresentInsideKeralaPostOfficeMl(mdmsTenantService.getPostOfficeNameMl(mdmsData, birth.getParentAddress().getPresentInsideKeralaPostOffice()));

                            birth.getParentAddress().setPresentInsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
                            birth.getParentAddress().setPresentInsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());

                            birth.getParentAddress().setPresentInsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                            birth.getParentAddress().setPresentInsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                            birth.getParentAddress().setPresentInsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
                            birth.getParentAddress().setPresentInsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());

                            birth.getParentAddress().setPresentInsideKeralaPincode(birth.getParentAddress().getPinNoPresent());
                            birth.getParentAddress().setPresentInsideKeralaPostOffice(birth.getParentAddress().getPresentInsideKeralaPostOffice());
                            //Ward Name
                            mdmsBirthService.setLocationForAddressPresent(birth.getParentAddress(), mdmsDataLocPresent);

                        } else {
                            //Country
                            birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());
                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));

                            //State
                            birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());
                            birth.getParentAddress().setStateIdPresentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPresent()));
                            birth.getParentAddress().setStateIdPresentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPresent()));

                            //District
                            birth.getParentAddress().setPresentOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());
                            birth.getParentAddress().setDistrictIdPresentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPresent()));
                            birth.getParentAddress().setDistrictIdPresentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPresent()));

                            birth.getParentAddress().setPresentOutsideKeralaVillageName(birth.getParentAddress().getVillageNamePresent());

                            birth.getParentAddress().setPresentOutsideKeralaPincode(birth.getParentAddress().getPinNoPresent());

                            birth.getParentAddress().setPresentOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
                            birth.getParentAddress().setPresentOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());

                            birth.getParentAddress().setPresentOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPresent());
                            birth.getParentAddress().setPresentOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPresent());

                            birth.getParentAddress().setPresentOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
                            birth.getParentAddress().setPresentOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());

                            birth.getParentAddress().setPresentOutsideKeralaCityVilgeEn(birth.getParentAddress().getTownOrVillagePresent());
                        }
                    } else {
                        //Country
                        birth.getParentAddress().setPresentOutSideCountry(birth.getParentAddress().getCountryIdPresent());
                        birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
                        birth.getParentAddress().setCountryIdPresentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));

                        birth.getParentAddress().setPresentOutSideIndiaadrsVillage(birth.getParentAddress().getVillageNamePresent());
                        birth.getParentAddress().setPresentOutSideIndiaadrsCityTown(birth.getParentAddress().getTownOrVillagePresent());
                    }
                }
            });
        }

    }
}
