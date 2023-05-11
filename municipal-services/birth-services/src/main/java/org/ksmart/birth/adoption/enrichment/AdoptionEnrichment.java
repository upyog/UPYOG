package org.ksmart.birth.adoption.enrichment;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.common.enrichment.BaseEnrichment;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.adoption.AdoptionApplication;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
 
 
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.common.repository.IdGenRepository;

import java.util.*;

@Component
public class AdoptionEnrichment implements BaseEnrichment {

	@Autowired
	BirthConfiguration config;
    @Autowired
    MdmsUtil mdmsUtil;
    @Autowired 
    MdmsDataService mdmsDataService;     
    @Autowired
    IdGenRepository idGenRepository;
 

    public void enrichCreate(AdoptionDetailRequest request) {
        Date date = new Date();
        long doreport = date.getTime();
        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);
        request.getAdoptionDetails().forEach(birth -> {

            birth.setId(UUID.randomUUID().toString());
            birth.setDateOfReport(doreport);
            birth.setAuditDetails(auditDetails);
            birth.setIsAdopted(true);
            if(birth.getPlaceofBirthId() != null || !birth.getPlaceofBirthId().isEmpty()){
                Object mdmsData = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), birth.getTenantId());
               // mdmsDataService.setKsmartLocationDetails(birth, mdmsData);
            }
            birth.setBirthPlaceUuid(UUID.randomUUID().toString());
            birth.getParentsDetails().setFatherUuid(UUID.randomUUID().toString());
            birth.getParentsDetails().setMotherUuid(UUID.randomUUID().toString());
            
            birth.getDocumentDetails().forEach(
            		document -> {
            			document.setId(UUID.randomUUID().toString());
            			document.setActive(true);
            			document.setAuditDetails(auditDetails);
            			document.setBirthdtlid(birth.getId());
            			document.setParentBrthDtlId(birth.getId());
            		});
            
            if(birth.getParentsDetails() != null) {
                if(!birth.getParentsDetails().getIsFatherInfoMissing()){
                    birth.getParentsDetails().setFatherBioAdopt("ADOPT");
                }
                if(!birth.getParentsDetails().getIsMotherInfoMissing()){
                    birth.getParentsDetails().setMotherBioAdopt("ADOPT");
                }
            }
            if(birth.getParentAddress() != null) {
                birth.getParentAddress().setPermanentUuid(UUID.randomUUID().toString());
                birth.getParentAddress().setPresentUuid(UUID.randomUUID().toString());
                birth.getParentAddress().setBioAdoptPermanent("ADOPT");
                birth.getParentAddress().setBioAdoptPresent("ADOPT");
            }
            birth.setBirthStatisticsUuid(UUID.randomUUID().toString());
            birth.setBirthInitiatorUuid(UUID.randomUUID().toString());

        });
        setApplicationNumbers(request);
//        setFileNumbers(request);
        setPresentAddress(request);
        setPermanentAddress(request);
    }

    public void enrichUpdate(AdoptionDetailRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);
        request.getAdoptionDetails()
        .forEach(birth -> {
            birth.setAuditDetails(auditDetails);
            if ((birth.getApplicationStatus() == "APPROVED" && birth.getAction() == "APPROVE")) {
                setRegistrationNumber(request);
            }
            if(birth.getParentsDetails() != null) {
                if(!birth.getParentsDetails().getIsFatherInfoMissing()){
                    birth.getParentsDetails().setFatherBioAdopt("ADOPT");
                }
                if(!birth.getParentsDetails().getIsMotherInfoMissing()){
                    birth.getParentsDetails().setMotherBioAdopt("ADOPT");
                }
            }
        });
      //  setRegistrationNumber(request); 
        setPresentAddress(request);
        setPermanentAddress(request);
    }
 

    private void setRegistrationNumber(AdoptionDetailRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<AdoptionApplication> birthDetails = request.getAdoptionDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthRegisNumberName(),
                request.getAdoptionDetails().get(0).getApplicationType(),
                "REG",
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        ListIterator<String> itr = filecodes.listIterator();
        request.getAdoptionDetails()
                .forEach(birth -> {
                    if((birth.getApplicationStatus() == "APPROVED" && birth.getAction() == "APPROVE")) {
                        birth.setRegistrationNo(itr.next());
                        birth.setRegistrationDate(currentTime);
                    }
                });
    }
    private void setApplicationNumbers(AdoptionDetailRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<AdoptionApplication> adoptionDetails = request.getAdoptionDetails();
        String tenantId = adoptionDetails.get(0)
                .getTenantId();
        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getAdoptionAckIdName(),
                request.getAdoptionDetails().get(0).getApplicationType(),
                "APPL",
                adoptionDetails.size());
        validateFileCodes(filecodes, adoptionDetails.size());

        ListIterator<String> itr = filecodes.listIterator();
        request.getAdoptionDetails()
                .forEach(adoption -> {
                	adoption.setApplicationNo(itr.next());
                });
    }
 

    private void setPresentAddress(AdoptionDetailRequest request) {
        request.getAdoptionDetails()
                .forEach(birth -> {
                	
                    if (birth.getParentAddress() != null) {
                    	if(birth.getParentAddress() != null) {
                            birth.getParentAddress().setPermanentUuid(UUID.randomUUID().toString());
                            birth.getParentAddress().setPresentUuid(UUID.randomUUID().toString());
                            birth.getParentAddress().setBioAdoptPermanent("ADOPT");
                            birth.getParentAddress().setBioAdoptPresent("ADOPT");
                        }
                    	
                        if (birth.getParentAddress().getPresentaddressCountry() != null && birth.getParentAddress().getPresentaddressStateName() != null) {
                            if (birth.getParentAddress().getPresentaddressCountry().contains(BirthConstants.COUNTRY_CODE)) {
                                if (birth.getParentAddress().getPresentaddressStateName().contains(BirthConstants.STATE_CODE_SMALL)) {

                                    birth.getParentAddress().setCountryIdPresent(birth.getParentAddress().getPresentaddressCountry());

                                    birth.getParentAddress().setStateIdPresent(birth.getParentAddress().getPresentaddressStateName());

                                    birth.getParentAddress().setDistrictIdPresent(birth.getParentAddress().getPresentInsideKeralaDistrict());

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

                                    birth.getParentAddress().setLocalityEnPresent(birth.getParentAddress().getPresentOutsideKeralaLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPresent(birth.getParentAddress().getPresentOutsideKeralaLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPresent(birth.getParentAddress().getPresentOutsideKeralaStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPresent(birth.getParentAddress().getPresentOutsideKeralaStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPresent(birth.getParentAddress().getPresentOutsideKeralaHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPresent(birth.getParentAddress().getPresentOutsideKeralaHouseNameMl());

                                    birth.getParentAddress().setVillageNamePresent(birth.getParentAddress().getPresentOutsideKeralaVillageName());

                                    birth.getParentAddress().setPinNoPresent(birth.getParentAddress().getPresentOutsideKeralaPincode());

                                }
                            } else {
                            	 
                                if (birth.getParentAddress().getPresentaddressCountry() != null) {
                                    birth.getParentAddress().setCountryIdPresent(birth.getParentAddress().getPresentaddressCountry());
                                    birth.getParentAddress().setVillageNamePresent(birth.getParentAddress().getPresentOutSideIndiaadrsVillage());
                                    birth.getParentsDetails().setTownOrVillagePresent(birth.getParentAddress().getPresentOutSideIndiaadrsCityTown());
                                }
                                 
                            }
                        }
                    }
                });
    }
    private void setPermanentAddress(AdoptionDetailRequest request) {
        request.getAdoptionDetails()
                .forEach(birth -> {
                    if (birth.getParentAddress() != null && birth.getParentAddress().getIsPrsentAddress() != null)  {
                        birth.getParentAddress().setIsPrsentAddressInt(birth.getParentAddress().getIsPrsentAddress() == true ? 1 : 0);
                        if (birth.getParentAddress().getPermtaddressCountry() != null && birth.getParentAddress().getPermtaddressStateName() != null) {
                            if (birth.getParentAddress().getPermtaddressCountry().contains(BirthConstants.COUNTRY_CODE)) {
                                if (birth.getParentAddress().getPermtaddressStateName().contains(BirthConstants.STATE_CODE_SMALL)) {
                                    if(birth.getParentAddress().getIsPrsentAddress()){
                                        birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getCountryIdPresent());
                                        birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getStateIdPresent());

                                    } else{
                                        birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermtaddressCountry());

                                        birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getPermtaddressStateName());
                                    }

                                    birth.getParentAddress().setDistrictIdPermanent(birth.getParentAddress().getPermntInKeralaAdrDistrict());

                                    birth.getParentAddress().setLocalityEnPermanent(birth.getParentAddress().getPermntInKeralaAdrLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPermanent(birth.getParentAddress().getPermntInKeralaAdrLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPermanent(birth.getParentAddress().getPermntInKeralaAdrStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPermanent(birth.getParentAddress().getPermntInKeralaAdrStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPermanent(birth.getParentAddress().getPermntInKeralaAdrHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPermanent(birth.getParentAddress().getPermntInKeralaAdrHouseNameMl());

                                    birth.getParentAddress().setPinNoPermanent(birth.getParentAddress().getPermntInKeralaAdrPincode());
                                    birth.getParentAddress().setVillageNamePermanent(null);
                                } else {
                                    birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermtaddressCountry());

                                    birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getPermtaddressStateName());

                                    birth.getParentAddress().setDistrictIdPermanent(birth.getParentAddress().getPermntOutsideKeralaDistrict());

                                    birth.getParentAddress().setLocalityEnPermanent(birth.getParentAddress().getPermntOutsideKeralaLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPermanent(birth.getParentAddress().getPermntOutsideKeralaLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPermanent(birth.getParentAddress().getPermntOutsideKeralaStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPermanent(birth.getParentAddress().getPermntOutsideKeralaStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPermanent(birth.getParentAddress().getPermntOutsideKeralaHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPermanent(birth.getParentAddress().getPermntOutsideKeralaHouseNameMl());

                                    birth.getParentAddress().setPinNoPermanent(birth.getParentAddress().getPermntOutsideKeralaPincode());
                                    birth.getParentAddress().setVillageNamePermanent(birth.getParentAddress().getPermntOutsideKeralaVillage());

                                }
                            } else {
                                if (birth.getParentAddress().getPermtaddressCountry() != null) {
                                    birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermtaddressCountry());
                                    birth.getParentAddress().setVillageNamePermanent(birth.getParentAddress().getPermntOutsideIndiaVillage());

                                }
                            }
                        }
                    }
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
