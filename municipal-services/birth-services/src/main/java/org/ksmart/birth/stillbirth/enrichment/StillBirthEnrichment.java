package org.ksmart.birth.stillbirth.enrichment;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.common.enrichment.BaseEnrichment;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.repository.IdGenRepository;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.stillbirth.service.MdmsForStillBirthService;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.CommonUtils;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthApplication;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import static org.ksmart.birth.utils.BirthConstants.APPLICATION_NO;
import static org.ksmart.birth.utils.BirthConstants.FILE_NO;

@Component
public class StillBirthEnrichment implements BaseEnrichment {
    @Autowired
    BirthConfiguration config;
    @Autowired
    MdmsUtil mdmsUtil;
    @Autowired
    MdmsForStillBirthService mdmsBirthService;
    @Autowired
    MdmsTenantService mdmsTenantService;
    @Autowired
    IdGenRepository idGenRepository;


    public void enrichCreate(StillBirthDetailRequest request, Object mdmsData) {
        String tenantId = null;
        RequestInfo requestInfo = request.getRequestInfo();
        String uuid = requestInfo.getUserInfo().getUuid();
        AuditDetails auditDetails = CommonUtils.buildAuditDetails(uuid, true);
        for (StillBirthApplication birth : request.getBirthDetails()) {
            tenantId = birth.getTenantId();
            birth.setDateOfReport(CommonUtils.currentDateTime());

            birth.setId(UUID.randomUUID().toString());
            birth.setAuditDetails(auditDetails);
        }
        setPlaceOfBirth(request, tenantId, mdmsData,auditDetails, true);
        setApplicationNumbers(request);
        setFileNumbers(request);
        setPresentAddress(request, mdmsData, true);
        setPermanentAddress(request, mdmsData, true);
        setStatisticalInfo(request, true);
    }

    public void enrichUpdate(StillBirthDetailRequest request, Object mdmsData) {
        String tenantId = null;
        for (StillBirthApplication birth : request.getBirthDetails()) {
            tenantId = birth.getTenantId();
            birth.setDateOfReport(CommonUtils.currentDateTime());
        }
        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);
        setPlaceOfBirth(request, tenantId, mdmsData,auditDetails, false);
        setPresentAddress(request, mdmsData, false);
        setPermanentAddress(request, mdmsData, false);
    }
    private void setApplicationNumbers(StillBirthDetailRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<StillBirthApplication> birthDetails = request.getBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();
        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthApplNumberIdName(),
                request.getBirthDetails().get(0).getApplicationType(),
                APPLICATION_NO,
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());

        ListIterator<String> itr = filecodes.listIterator();
        request.getBirthDetails()
                .forEach(birth -> {
                    birth.setApplicationNo(itr.next());
                });
    }

    private void setFileNumbers(StillBirthDetailRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<StillBirthApplication> birthDetails = request.getBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthFileNumberName(),
                request.getBirthDetails().get(0).getApplicationType(),
                FILE_NO,
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        ListIterator<String> itr = filecodes.listIterator();
        request.getBirthDetails()
                .forEach(birth -> {
                    birth.setFileNumber(itr.next());
                    birth.setFileDate(currentTime);
                });
    }

    private void setPresentAddress(StillBirthDetailRequest request, Object mdmsData,  boolean isCreate) {
        request.getBirthDetails()
                .forEach(birth -> {
                    if (birth.getParentAddress() != null) {
                        if(isCreate) {
                            birth.getParentAddress().setPermanentUuid(UUID.randomUUID().toString());
                            birth.getParentAddress().setPresentUuid(UUID.randomUUID().toString());
                            birth.getParentAddress().setBioAdoptPermanent("BIOLOGICAL");
                            birth.getParentAddress().setBioAdoptPresent("BIOLOGICAL");
                        }

                        if (birth.getParentAddress().getPresentaddressCountry() != null && birth.getParentAddress().getPresentaddressStateName() != null) {
                            if (birth.getParentAddress().getPresentaddressCountry().contains(BirthConstants.COUNTRY_CODE)) {
                                if (birth.getParentAddress().getPresentaddressStateName().contains(BirthConstants.STATE_CODE_SMALL)) {
                                    birth.getParentAddress().setCountryIdPresent(birth.getParentAddress().getPresentaddressCountry());
                                    birth.getParentAddress().setStateIdPresent(birth.getParentAddress().getPresentaddressStateName());
                                    //Pincode
                                    birth.getParentAddress().setPinNoPresent(birth.getParentAddress().getPresentInsideKeralaPincode());

                                    //District
                                    birth.getParentAddress().setDistrictIdPresent(birth.getParentAddress().getPresentInsideKeralaDistrict());
                                    //Local Body
                                    birth.getParentAddress().setPresentInsideKeralaLBNameEn(mdmsTenantService.getTenantNameEn(mdmsData, birth.getParentAddress().getPresentInsideKeralaLBName()));
                                    birth.getParentAddress().setPresentInsideKeralaLBNameMl(mdmsTenantService.getTenantNameMl(mdmsData, birth.getParentAddress().getPresentInsideKeralaLBName()));

                                    //Post Office
                                    birth.getParentAddress().setPresentInsideKeralaPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getParentAddress().getPresentInsideKeralaPostOffice()));
                                    birth.getParentAddress().setPresentInsideKeralaPostOfficeMl(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getParentAddress().getPresentInsideKeralaPostOffice()));

                                    birth.getParentAddress().setLocalityEnPresent(birth.getParentAddress().getPresentInsideKeralaLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPresent(birth.getParentAddress().getPresentInsideKeralaLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPresent(birth.getParentAddress().getPresentInsideKeralaStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPresent(birth.getParentAddress().getPresentInsideKeralaStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPresent(birth.getParentAddress().getPresentInsideKeralaHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPresent(birth.getParentAddress().getPresentInsideKeralaHouseNameMl());

                                    birth.getParentAddress().setPinNoPresent(birth.getParentAddress().getPresentInsideKeralaPincode());
                                    birth.getParentAddress().setTownOrVillagePresent(birth.getParentAddress().getPresentOutsideKeralaCityVilgeEn());

                                } else {
                                    birth.getParentAddress().setCountryIdPresent(birth.getParentAddress().getPresentaddressCountry());

                                    birth.getParentAddress().setStateIdPresent(birth.getParentAddress().getPresentaddressStateName());

                                    birth.getParentAddress().setDistrictIdPresent(birth.getParentAddress().getPresentOutsideKeralaDistrict());
                                    //Pincode
                                    birth.getParentAddress().setPinNoPresent(birth.getParentAddress().getPresentOutsideKeralaPincode());

                                    birth.getParentAddress().setLocalityEnPresent(birth.getParentAddress().getPresentOutsideKeralaLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPresent(birth.getParentAddress().getPresentOutsideKeralaLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPresent(birth.getParentAddress().getPresentOutsideKeralaStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPresent(birth.getParentAddress().getPresentOutsideKeralaStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPresent(birth.getParentAddress().getPresentOutsideKeralaHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPresent(birth.getParentAddress().getPresentOutsideKeralaHouseNameMl());

                                    birth.getParentAddress().setVillageNamePresent(birth.getParentAddress().getPresentOutSideIndiaadrsVillage());

                                    birth.getParentAddress().setTownOrVillagePresent(birth.getParentAddress().getPresentOutsideKeralaCityVilgeEn());

                                }
                                //Country
                                birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
                                birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));

                                //State
                                birth.getParentAddress().setStateIdPresentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPresent()));
                                birth.getParentAddress().setStateIdPresentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPresent()));

                                //District
                                birth.getParentAddress().setDistrictIdPresentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPresent()));
                                birth.getParentAddress().setDistrictIdPresentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPresent()));

                            } else {
                                if (birth.getParentAddress().getPresentOutSideCountry() != null) {
                                    birth.getParentAddress().setCountryIdPresent(birth.getParentAddress().getPresentOutSideCountry());

                                    //Country
                                    birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
                                    birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));

                                    birth.getParentAddress().setVillageNamePresent(birth.getParentAddress().getPresentOutSideIndiaadrsVillage());
                                    birth.getParentAddress().setTownOrVillagePresent(birth.getParentAddress().getPresentOutSideIndiaadrsCityTown());
                                }
                            }


                        }
                    }
                });
    }
    private void setPermanentAddress(StillBirthDetailRequest request, Object mdmsData,  boolean isCreate ) {
        request.getBirthDetails()
                .forEach(birth -> {
                    if (birth.getParentAddress() != null && birth.getParentAddress().getIsPrsentAddress() != null)  {
                        birth.getParentAddress().setIsPrsentAddressInt(birth.getParentAddress().getIsPrsentAddress() == true ? 1 : 0);
                        if(birth.getParentAddress().getIsPrsentAddress()){
                            birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getCountryIdPresent());
                            birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getStateIdPresent());
                            if(birth.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)){
                                birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPresent());
                                if (birth.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
                                    birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
                                }
                            }
                        }
                        if (birth.getParentAddress().getPermtaddressCountry() != null && birth.getParentAddress().getPermtaddressStateName() != null) {
                            if (birth.getParentAddress().getPermtaddressCountry().contains(BirthConstants.COUNTRY_CODE)) {
                                if (birth.getParentAddress().getPermtaddressStateName().contains(BirthConstants.STATE_CODE_SMALL)) {
                                    if(!birth.getParentAddress().getIsPrsentAddress()){
                                        birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermtaddressCountry());
                                        birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getPermtaddressStateName());
                                    }
                                    birth.getParentAddress().setDistrictIdPermanent(birth.getParentAddress().getPermntInKeralaAdrDistrict());
                                    //Pincode
                                    birth.getParentAddress().setPinNoPermanent(birth.getParentAddress().getPermntInKeralaAdrPincode());
                                    //Local Body
                                    birth.getParentAddress().setPermntInKeralaAdrLBNameEn(mdmsTenantService.getTenantNameEn(mdmsData, birth.getParentAddress().getPermntInKeralaAdrLBName()));
                                    birth.getParentAddress().setPermntInKeralaAdrLBNameMl(mdmsTenantService.getTenantNameMl(mdmsData, birth.getParentAddress().getPermntInKeralaAdrLBName()));

                                    //Post Office
                                    birth.getParentAddress().setPermntInKeralaAdrPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getParentAddress().getPermntInKeralaAdrPostOffice()));
                                    birth.getParentAddress().setPermntInKeralaAdrPostOfficeMl(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getParentAddress().getPermntInKeralaAdrPostOffice()));

                                    birth.getParentAddress().setLocalityEnPermanent(birth.getParentAddress().getPermntInKeralaAdrLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPermanent(birth.getParentAddress().getPermntInKeralaAdrLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPermanent(birth.getParentAddress().getPermntInKeralaAdrStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPermanent(birth.getParentAddress().getPermntInKeralaAdrStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPermanent(birth.getParentAddress().getPermntInKeralaAdrHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPermanent(birth.getParentAddress().getPermntInKeralaAdrHouseNameMl());

                                    birth.getParentAddress().setPinNoPermanent(birth.getParentAddress().getPermntInKeralaAdrPincode());
                                    birth.getParentAddress().setPoNoPermanent(birth.getParentAddress().getPermntInKeralaAdrPostOffice());
                                    birth.getParentAddress().setTownOrVillagePermanent(birth.getParentAddress().getPermntOutsideKeralaCityVilgeEn());
                                } else {
                                    if(!birth.getParentAddress().getIsPrsentAddress()){
                                        birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermtaddressCountry());
                                        birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getPermtaddressStateName());
                                    }
                                    birth.getParentAddress().setDistrictIdPermanent(birth.getParentAddress().getPermntOutsideKeralaDistrict());

                                    birth.getParentAddress().setLocalityEnPermanent(birth.getParentAddress().getPermntOutsideKeralaLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPermanent(birth.getParentAddress().getPermntOutsideKeralaLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPermanent(birth.getParentAddress().getPermntOutsideKeralaStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPermanent(birth.getParentAddress().getPermntOutsideKeralaStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPermanent(birth.getParentAddress().getPermntOutsideKeralaHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPermanent(birth.getParentAddress().getPermntOutsideKeralaHouseNameMl());

                                    birth.getParentAddress().setPinNoPermanent(birth.getParentAddress().getPermntOutsideKeralaPincode());

                                    birth.getParentAddress().setVillageNamePermanent(birth.getParentAddress().getPermntOutsideKeralaVillage());
                                    birth.getParentAddress().setTownOrVillagePermanent(birth.getParentAddress().getPermntOutsideKeralaCityVilgeEn());

                                }
                            } else {
                                if (birth.getParentAddress().getPermntOutsideIndiaCountry() != null) {
                                    birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermntOutsideIndiaCountry());
                                    birth.getParentAddress().setVillageNamePermanent(birth.getParentAddress().getPermntOutsideIndiaVillage());
                                    birth.getParentAddress().setTownOrVillagePermanent(birth.getParentAddress().getPermntOutsideIndiaCityTown());
                                }
                            }
                            //Country
                            birth.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
                            birth.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPermanent()));

                            //State
                            birth.getParentAddress().setStateIdPermanentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPermanent()));
                            birth.getParentAddress().setStateIdPermanentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPermanent()));

                            //District
                            birth.getParentAddress().setDistrictIdPermanentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));
                            birth.getParentAddress().setDistrictIdPermanentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));
                            ;
                        }
                    }
                });
    }
    private void setPlaceOfBirth(StillBirthDetailRequest request, String trnantId, Object mdmsData, AuditDetails auditDetails,  boolean isCreate) {
        Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), trnantId);
//        Object mdmsDataComm = mdmsUtil.mdmsCall(request.getRequestInfo());
        request.getBirthDetails().forEach(birth -> {
            if(birth.getPlaceofBirthId() != null || !birth.getPlaceofBirthId().isEmpty()){
                mdmsBirthService.setLocationDetails(birth, mdmsDataLoc, mdmsData);
            }
            if(isCreate) {
                birth.setBirthPlaceUuid(UUID.randomUUID().toString());
                birth.getParentsDetails().setFatherUuid(UUID.randomUUID().toString());
                birth.getParentsDetails().setMotherUuid(UUID.randomUUID().toString());
            }
            if(birth.getParentsDetails() != null) {
                if(!birth.getParentsDetails().getIsFatherInfoMissing()){
                    birth.getParentsDetails().setFatherBioAdopt("BIOLOGICAL");
                }
                if(!birth.getParentsDetails().getIsMotherInfoMissing()){
                    birth.getParentsDetails().setMotherBioAdopt("BIOLOGICAL");
                }
            }
        });
    }
    private void setStatisticalInfo(StillBirthDetailRequest request,boolean  isCreate) {
        request.getBirthDetails()
                .forEach(birth -> {
                    if(isCreate) {
                        birth.setBirthStatisticsUuid(UUID.randomUUID().toString());
                        birth.setBirthInitiatorUuid(UUID.randomUUID().toString());
                    }

                    // mdmsBirthService.setTenantDetails(birth, mdmsData);//Check/////////

                    //  TOWN VILLAGe INSIDE Kerala
                });

    }
    private List<String> getIds(RequestInfo requestInfo, String tenantId, String idName, String moduleCode, String  fnType, int count) {
        return idGenRepository.getIdList(requestInfo, tenantId, idName, moduleCode, fnType, count);
    }
    private void validateFileCodes(List<String> fileCodes, int count) {
        if (CollectionUtils.isEmpty(fileCodes)) {
            throw new CustomException(ErrorCodes.IDGEN_ERROR.getCode(), "No file code(s) returned from idgen service");
        }

        if (fileCodes.size() != count) {
            throw new CustomException(ErrorCodes.IDGEN_ERROR.getCode(),
                    "The number of file code(s) returned by idgen service is not equal to the request count");
        }
    }
}
