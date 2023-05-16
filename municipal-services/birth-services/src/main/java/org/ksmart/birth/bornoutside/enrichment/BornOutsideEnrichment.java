package org.ksmart.birth.bornoutside.enrichment;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.bornoutside.service.MdmsForBornOutsideService;
import org.ksmart.birth.common.enrichment.BaseEnrichment;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.repository.IdGenRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.ksmart.birth.web.model.bornoutside.BornOutsideDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

@Component
public class BornOutsideEnrichment implements BaseEnrichment {
    @Autowired
    BirthConfiguration config;
    @Autowired
   MdmsUtil mdmsUtil;
    @Autowired
    MdmsForBornOutsideService mdmsDataService;
    @Autowired
    IdGenRepository idGenRepository;
    public void enrichCreate(BornOutsideDetailRequest request) {
        Date date = new Date();
        String tenantId = null;
        long doreport = date.getTime();
        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);
        for (BornOutsideApplication birth : request.getNewBirthDetails()) {
            tenantId = birth.getTenantId();
            birth.setDateOfReport(doreport);
        }
        setPlaceOfBirth(request, tenantId, auditDetails);
        setApplicationNumbers(request);
        setFileNumbers(request);
        setPresentAddress(request);
        setPermanentAddress(request);
        setStatisticalInfo(request);
    }

    public void enrichUpdate(BornOutsideDetailRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);
        request.getNewBirthDetails()
                .forEach(birth -> birth.setAuditDetails(auditDetails));
        setPresentAddress(request);
        setPermanentAddress(request);
    }

    private void setApplicationNumbers(BornOutsideDetailRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<BornOutsideApplication> birthDetails = request.getNewBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();
        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthApplNumberIdName(),
                request.getNewBirthDetails().get(0).getApplicationType(),
                "APPL",
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());

        ListIterator<String> itr = filecodes.listIterator();
        request.getNewBirthDetails()
                .forEach(birth -> {
                    birth.setApplicationNo(itr.next());
                });
    }

    private void setFileNumbers(BornOutsideDetailRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<BornOutsideApplication> birthDetails = request.getNewBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthFileNumberName(),
                request.getNewBirthDetails().get(0).getApplicationType(),
                "FILE",
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        ListIterator<String> itr = filecodes.listIterator();
        request.getNewBirthDetails()
                .forEach(birth -> {
                    birth.setFileNumber(itr.next());
                    birth.setFileDate(currentTime);
                });
    }

    private void setPresentAddress(BornOutsideDetailRequest request) {
        request.getNewBirthDetails()
                .forEach(birth -> {
                    birth.getParentAddress().setPresentUuid(UUID.randomUUID().toString());
                    birth.getParentAddress().setBioAdoptPresent("BIOLOGICAL");
                    if (birth.getParentAddress() != null) {
                        if (birth.getParentAddress().getPresentOutSideCountry() != null) {
                            birth.getParentAddress().setTownOrVillagePresent(birth.getParentAddress().getPresentOutSideIndiaadrsCityTown());
                            birth.getParentAddress().setCountryIdPresent(birth.getParentAddress().getPresentOutSideCountry());
                            birth.getParentAddress().setVillageNamePresent(birth.getParentAddress().getPresentOutSideIndiaadrsVillage());
//
                        }
                    }
                });
    }
    private void setPermanentAddress(BornOutsideDetailRequest request) {
        request.getNewBirthDetails()
                .forEach(birth -> {
                    birth.getParentAddress().setPermanentUuid(UUID.randomUUID().toString());
                    birth.getParentAddress().setBioAdoptPermanent("BIOLOGICAL");
                    if (birth.getParentAddress() != null)  {
                        if (birth.getParentAddress().getPermtaddressCountry() != null && birth.getParentAddress().getPermtaddressStateName() != null) {
                            if (birth.getParentAddress().getPermtaddressCountry().contains(BirthConstants.COUNTRY_CODE)) {
                                if (birth.getParentAddress().getPermtaddressStateName().contains(BirthConstants.STATE_CODE_SMALL)) {
//                                    if(birth.getParentAddress().getIsPrsentAddress()){
//                                        birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getCountryIdPresent());
//                                        birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getStateIdPresent());
//
//                                    } else{
                                        birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermtaddressCountry());

                                        birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getPermtaddressStateName());
//                                    }

                                    birth.getParentAddress().setDistrictIdPermanent(birth.getParentAddress().getPermntInKeralaAdrDistrict());

                                    birth.getParentAddress().setLocalityEnPermanent(birth.getParentAddress().getPermntInKeralaAdrLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPermanent(birth.getParentAddress().getPermntInKeralaAdrLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPermanent(birth.getParentAddress().getPermntInKeralaAdrStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPermanent(birth.getParentAddress().getPermntInKeralaAdrStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPermanent(birth.getParentAddress().getPermntInKeralaAdrHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPermanent(birth.getParentAddress().getPermntInKeralaAdrHouseNameMl());

                                    birth.getParentAddress().setPinNoPermanent(birth.getParentAddress().getPermntInKeralaAdrPincode());
                                    birth.getParentAddress().setVillageNamePermanent(null);
                                    birth.setWardId(birth.getParentAddress().getPermntInKeralaWardNo());
                                }
//                                else {
//                                    birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermtaddressCountry());
//
//                                    birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getPermtaddressStateName());
//
//                                    birth.getParentAddress().setDistrictIdPermanent(birth.getParentAddress().getPermntOutsideKeralaDistrict());
//
//                                    birth.getParentAddress().setLocalityEnPermanent(birth.getParentAddress().getPermntOutsideKeralaLocalityNameEn());
//                                    birth.getParentAddress().setLocalityMlPermanent(birth.getParentAddress().getPermntOutsideKeralaLocalityNameMl());
//
//                                    birth.getParentAddress().setStreetNameEnPermanent(birth.getParentAddress().getPermntOutsideKeralaStreetNameEn());
//                                    birth.getParentAddress().setStreetNameMlPermanent(birth.getParentAddress().getPermntOutsideKeralaStreetNameMl());
//
//                                    birth.getParentAddress().setHouseNameNoEnPermanent(birth.getParentAddress().getPermntOutsideKeralaHouseNameEn());
//                                    birth.getParentAddress().setHouseNameNoMlPermanent(birth.getParentAddress().getPermntOutsideKeralaHouseNameMl());
//
//                                    birth.getParentAddress().setPinNoPermanent(birth.getParentAddress().getPermntOutsideKeralaPincode());
//                                    birth.getParentAddress().setVillageNamePermanent(birth.getParentAddress().getPermntOutsideKeralaVillage());
//
//                                }
//                            } else {
//                                if (birth.getParentAddress().getPermntOutsideIndiaCountry() != null) {
//                                    birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermntOutsideIndiaCountry());
//                                    birth.getParentAddress().setVillageNamePermanent(birth.getParentAddress().getPermntOutsideIndiaVillage());
//
//                                }
                            }
                        }
                    }
                });
    }

    private void setPlaceOfBirth(BornOutsideDetailRequest request, String tenantId, AuditDetails auditDetails) {
    //    Object mdmsData = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), tenantId);
        request.getNewBirthDetails().forEach(birth -> {
            birth.setId(UUID.randomUUID().toString());
            birth.setAuditDetails(auditDetails);
            birth.setIsBornOutside(true);
            birth.setBirthPlaceUuid(UUID.randomUUID().toString());
            birth.getParentsDetails().setFatherUuid(UUID.randomUUID().toString());
            birth.getParentsDetails().setMotherUuid(UUID.randomUUID().toString());
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

    private void setStatisticalInfo(BornOutsideDetailRequest request) {
        request.getNewBirthDetails()
                .forEach(birth -> {
                    birth.setBirthStatisticsUuid(UUID.randomUUID().toString());
            birth.setBirthInitiatorUuid(UUID.randomUUID().toString());
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
