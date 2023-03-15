package org.ksmart.birth.newbirth.service;

import lombok.extern.slf4j.Slf4j;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestService {
    public RegisterBirthDetailsRequest createRegistryRequest(NewBirthDetailRequest requestKsmartBirthReq) {
        RegisterBirthDetailsRequest request = new RegisterBirthDetailsRequest();
        List<RegisterBirthDetail> registerBirthDetails = new LinkedList<>();
        RegisterBirthDetail registerBirthDetail = new RegisterBirthDetail();
        registerBirthDetail.setDateOfReport(requestKsmartBirthReq.getNewBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.setApplicationId(requestKsmartBirthReq.getNewBirthDetails().get(0).getId());
        registerBirthDetail.setDateOfBirth(requestKsmartBirthReq.getNewBirthDetails().get(0).getDateOfBirth());
        registerBirthDetail.setDateOfReport(requestKsmartBirthReq.getNewBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.setDateOfBirth(requestKsmartBirthReq.getNewBirthDetails().get(0).getDateOfBirth());
        registerBirthDetail.setTimeOfBirth(requestKsmartBirthReq.getNewBirthDetails().get(0).getTimeOfBirth());
        registerBirthDetail.setAmpm(requestKsmartBirthReq.getNewBirthDetails().get(0).getAmpm());
        registerBirthDetail.setFirstNameEn(requestKsmartBirthReq.getNewBirthDetails().get(0).getFirstNameEn());
        registerBirthDetail.setFirstNameMl(requestKsmartBirthReq.getNewBirthDetails().get(0).getFirstNameMl());
        registerBirthDetail.setMiddleNameEn(requestKsmartBirthReq.getNewBirthDetails().get(0).getMiddleNameEn());
        registerBirthDetail.setMiddleNameMl(requestKsmartBirthReq.getNewBirthDetails().get(0).getMiddleNameMl());
        registerBirthDetail.setLastNameEn(requestKsmartBirthReq.getNewBirthDetails().get(0).getLastNameEn());
        registerBirthDetail.setLastNameMl(requestKsmartBirthReq.getNewBirthDetails().get(0).getLastNameMl());
        registerBirthDetail.setTenantId(requestKsmartBirthReq.getNewBirthDetails().get(0).getTenantId());
        registerBirthDetail.setGender(requestKsmartBirthReq.getNewBirthDetails().get(0).getGender());
        registerBirthDetail.setRemarksEn(requestKsmartBirthReq.getNewBirthDetails().get(0).getRemarksEn());
        registerBirthDetail.setRemarksMl(requestKsmartBirthReq.getNewBirthDetails().get(0).getRemarksMl());
        registerBirthDetail.setAadharNo(requestKsmartBirthReq.getNewBirthDetails().get(0).getAadharNo());
        registerBirthDetail.setEsignUserCode(requestKsmartBirthReq.getNewBirthDetails().get(0).getEsignUserCode());
        registerBirthDetail.setEsignUserDesigCode(requestKsmartBirthReq.getNewBirthDetails().get(0).getEsignUserDesigCode());
        registerBirthDetail.setIsFatherInfoMissing(requestKsmartBirthReq.getNewBirthDetails().get(0).getParentsDetails().getIsFatherInfoMissing());
        registerBirthDetail.setIsMotherInfoMissing(requestKsmartBirthReq.getNewBirthDetails().get(0).getParentsDetails().getIsMotherInfoMissing());
//        registerBirthDetail.setNoOfAliveBirth(requestKsmartBirthReq.getNewBirthDetails().get(0).getNoOfAliveBirth());
//        registerBirthDetail.setMultipleBirthDetId(requestKsmartBirthReq.getNewBirthDetails().get(0).getMultipleBirthDetailsIid());
//        registerBirthDetail.setIsBornOutside(requestKsmartBirthReq.getNewBirthDetails().get(0).getIsBornOutside());
//        registerBirthDetail.setOtPassportNo(requestKsmartBirthReq.getNewBirthDetails().get(0).getPassportNo());
        registerBirthDetail.setRegistrationNo(requestKsmartBirthReq.getNewBirthDetails().get(0).getRegistrationNo());
        registerBirthDetail.setApplicationType(requestKsmartBirthReq.getNewBirthDetails().get(0).getApplicationType());
        registerBirthDetail.setRegistrationStatus("ACTIVE");
        registerBirthDetail.setRegistrationDate(requestKsmartBirthReq.getNewBirthDetails().get(0).getRegistrationDate());
        registerBirthDetail.setRegisterBirthPlace(createBirthPlace(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthFather(createFatherInfor(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthMother(createMotherInfor(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthPermanent(createRegisterBirthPermanentAddress(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthPresent(createRegisterBirthPresentAddress(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthStatitical(createRegisterBirthStatiticalInformation(requestKsmartBirthReq, registerBirthDetail));
        registerBirthDetails.add(registerBirthDetail);
        request.setRequestInfo(requestKsmartBirthReq.getRequestInfo());
        request.setRegisterBirthDetails(registerBirthDetails);
        return request;
    }

    private RegisterBirthPlace createBirthPlace(NewBirthDetailRequest requestBirthReq) {
        RegisterBirthPlace registerBirthPlace = new RegisterBirthPlace();
        registerBirthPlace.setPlaceOfBirthId(requestBirthReq.getNewBirthDetails().get(0).getPlaceofBirthId());
        registerBirthPlace.setHospitalId(requestBirthReq.getNewBirthDetails().get(0).getHospitalId());
        registerBirthPlace.setVehicleTypeId(requestBirthReq.getNewBirthDetails().get(0).getVehicleTypeid());
        registerBirthPlace.setVehicleRegistrationNo(requestBirthReq.getNewBirthDetails().get(0).getVehicleRegistrationNo());
        registerBirthPlace.setVehicleFromEn(requestBirthReq.getNewBirthDetails().get(0).getVehicleFromEn());
        registerBirthPlace.setVehicleToEn(requestBirthReq.getNewBirthDetails().get(0).getVehicleToEn());
        registerBirthPlace.setVehicleFromMl(requestBirthReq.getNewBirthDetails().get(0).getVehicleFromMl());
        registerBirthPlace.setVehicleToMl(requestBirthReq.getNewBirthDetails().get(0).getVehicleToMl());
//        registerBirthPlace.setVehicleOtherEn(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getVehicleOtherEn());
//        registerBirthPlace.setVehicleOtherMl(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getVehicleOtherMl());
        registerBirthPlace.setVehicleAdmitHospitalEn(requestBirthReq.getNewBirthDetails().get(0).getSetadmittedHospitalEn());
//        registerBirthPlace.setVehicleAdmitHospitalMl(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getVehicleAdmitHospitalMl());
        registerBirthPlace.setPublicPlaceId(requestBirthReq.getNewBirthDetails().get(0).getPublicPlaceType());
//        registerBirthPlace.setHoHouseHolderEn(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getHoHouseHolderEn());
//        registerBirthPlace.setHoHouseHolderMl(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getHoHouseHolderMl());
//        registerBirthPlace.setHoBuildingNo(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getHoBuildingNo());
//        registerBirthPlace.setHoResAssoNo(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getHoResAssoNo());
//        registerBirthPlace.setHoHousenNo(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getHoHousenNo());
        registerBirthPlace.setHouseNameEn(requestBirthReq.getNewBirthDetails().get(0).getAdrsHouseNameEn());
        registerBirthPlace.setHouseNameMl(requestBirthReq.getNewBirthDetails().get(0).getAdrsHouseNameMl());

//        registerBirthPlace.setHoVillageId(requestBirthReq.getNewBirthDetails().get(0).getPresentInsideKeralaVillage());
//        registerBirthPlace.setHoTalukId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPresentInsideKeralaTaluk());
//        registerBirthPlace.setHoDistrictId(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getHoDistrictId());
//        registerBirthPlace.setHoStateId(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getHoStateId());
        registerBirthPlace.setHoPoId(requestBirthReq.getNewBirthDetails().get(0).getAdrsPostOffice());
        registerBirthPlace.setHoPinNo(requestBirthReq.getNewBirthDetails().get(0).getAdrsPincode());
//        registerBirthPlace.setHoCountryId(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getHoCountryId());
        registerBirthPlace.setWardId(requestBirthReq.getNewBirthDetails().get(0).getWardId());
//        registerBirthPlace.setOthDetailsEn(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getOthDetailsEn());
//        registerBirthPlace.setOthDetailsMl(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getOthDetailsMl());
        registerBirthPlace.setInstitutionTypeId(requestBirthReq.getNewBirthDetails().get(0).getInstitutionTypeId());
        registerBirthPlace.setInstitutionId(requestBirthReq.getNewBirthDetails().get(0).getInstitutionId());

//        registerBirthPlace.setAuthOfficerId(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getAuthOfficerId());
//        registerBirthPlace.setAuthOfficerDesigId(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getAuthOfficerDesigId());
//        registerBirthPlace.setOthAuthOfficerName(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getOthAuthOfficerName());
//        registerBirthPlace.setOthAuthOfficerDesig(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getOthAuthOfficerDesig());
//        registerBirthPlace.setInformantsNameEn(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getInformantsNameEn());
//        registerBirthPlace.setInformantsNameMl(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getInformantsNameMl());
//        registerBirthPlace.setInformantsAddressEn(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getInformantsAddressEn());
//        registerBirthPlace.setInformantsAddressMl(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getInformantsAddressMl());


//        registerBirthPlace.setInformantsMobileNo(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getInformantsMobileNo());
//        registerBirthPlace.setInformantsAadhaarNo(requestBirthReq.getNewBirthDetails().get(0).getBirthPlace().getInformantsAadhaarNo());
        return registerBirthPlace;
    }

    //  /*
    private RegisterBirthMotherInfo createMotherInfor(NewBirthDetailRequest requestBirthReq) {
        RegisterBirthMotherInfo registerBirthMotherInfo = new RegisterBirthMotherInfo();
        registerBirthMotherInfo.setFirstNameEn(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getFirstNameEn());
        registerBirthMotherInfo.setFirstNameMl(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getFirstNameMl());

//        registerBirthMotherInfo.setMiddleNameEn(requestBirthReq.getNewBirthDetails().get(0).getBirthMotherInfo().getMiddleNameEn());
//        registerBirthMotherInfo.setMiddleNameMl(requestBirthReq.getNewBirthDetails().get(0).getBirthMotherInfo().getMiddleNameMl());
//        registerBirthMotherInfo.setLastNameEn(requestBirthReq.getNewBirthDetails().get(0).getBirthMotherInfo().getLastNameEn());
//        registerBirthMotherInfo.setLastNameMl(requestBirthReq.getNewBirthDetails().get(0).getBirthMotherInfo().getLastNameMl());
        registerBirthMotherInfo.setAadharNo(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getMotherAadhar());
        registerBirthMotherInfo.setOtPassportNo(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getMotherPassport());
//        registerBirthMotherInfo.setEmailId(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getEmailId());
//        registerBirthMotherInfo.setMobileNo(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getMobileNo());
        return registerBirthMotherInfo;
    }

    private RegisterBirthFatherInfo createFatherInfor(NewBirthDetailRequest requestBirthReq) {
        RegisterBirthFatherInfo registerBirthFatherInfo = new RegisterBirthFatherInfo();
        registerBirthFatherInfo.setFirstNameEn(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getFatherFirstNameEn());
        registerBirthFatherInfo.setFirstNameMl(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getFatherFirstNameMl());
//        registerBirthFatherInfo.setMiddleNameEn(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getMiddleNameEn());
//        registerBirthFatherInfo.setMiddleNameMl(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getMiddleNameMl());
//        registerBirthFatherInfo.setLastNameEn(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getLastNameEn());
//        registerBirthFatherInfo.setLastNameMl(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getLastNameMl());
        registerBirthFatherInfo.setAadharNo(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getFatherAadharno());
//        registerBirthFatherInfo.setOtPassportNo(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getPassportNo());
        registerBirthFatherInfo.setEmailId(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getFamilyEmailid());
        registerBirthFatherInfo.setMobileNo(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getFamilyMobileNo());
        return registerBirthFatherInfo;
    }

    private RegisterBirthPermanentAddress createRegisterBirthPermanentAddress(NewBirthDetailRequest requestBirthReq) {
        RegisterBirthPermanentAddress registerBirthPermanentAddress = new RegisterBirthPermanentAddress();
//        registerBirthPermanentAddress.setResAssoNo(requestBirthReq.getNewBirthDetails().get(0).getBirthPermanentAddress().getResAssoNo());
        registerBirthPermanentAddress.setHouseNameEn(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getHouseNameNoEnPermanent());

        registerBirthPermanentAddress.setHouseNameMl(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getHouseNameNoMlPermanent());

//        registerBirthPermanentAddress.setOtAddress1En(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getOtAddress1En());
//        registerBirthPermanentAddress.setOtAddress1Ml(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getOtAddress1Ml());
//        registerBirthPermanentAddress.setOtAddress2En(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getOtAddress2En());
//        registerBirthPermanentAddress.setOtAddress2Ml(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getOtAddress2Ml());
        registerBirthPermanentAddress.setVillageId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPermntInKeralaAdrVillage());

        registerBirthPermanentAddress.setTenantId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPermntInKeralaAdrLBName());

        registerBirthPermanentAddress.setTalukId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPermntInKeralaAdrTaluk());

        registerBirthPermanentAddress.setDistrictId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getDistrictIdPermanent());

        registerBirthPermanentAddress.setStateId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getStateIdPermanent());
        registerBirthPermanentAddress.setPoId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPoNoPermanent());

        registerBirthPermanentAddress.setPinNo(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPinNoPermanent());

        registerBirthPermanentAddress.setOtStateRegionProvinceEn(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPermntOutsideIndiaprovinceEn());

//        registerBirthPermanentAddress.setOtStateRegionProvinceMl(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getOtStateRegionProvinceMl());
        registerBirthPermanentAddress.setCountryId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getCountryIdPermanent());

//        registerBirthPermanentAddress.setResAssoNoMl(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getResAssoNoMl());
        return registerBirthPermanentAddress;
    }

    private RegisterBirthPresentAddress createRegisterBirthPresentAddress(NewBirthDetailRequest requestBirthReq) {
        RegisterBirthPresentAddress registerBirthPresentAddress = new RegisterBirthPresentAddress();
//        registerBirthPresentAddress.setResAssoNo(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getResAssoNo());
        registerBirthPresentAddress.setHouseNameEn(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getHouseNameNoEnPresent());

        registerBirthPresentAddress.setHouseNameMl(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getHouseNameNoMlPresent());

//        registerBirthPresentAddress.setOtAddress1En(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getOtAddress1En());
//        registerBirthPresentAddress.setOtAddress1Ml(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getOtAddress1Ml());
//        registerBirthPresentAddress.setOtAddress2En(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getOtAddress2En());
//        registerBirthPresentAddress.setOtAddress2Ml(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getOtAddress2Ml());
        registerBirthPresentAddress.setVillageId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getVillageNamePresent());

        registerBirthPresentAddress.setTenantId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPresentInsideKeralaLBName());

        registerBirthPresentAddress.setTalukId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPresentInsideKeralaTaluk());

        registerBirthPresentAddress.setDistrictId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getDistrictIdPresent());

        registerBirthPresentAddress.setStateId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getStateIdPresent());

        registerBirthPresentAddress.setPoId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPoNoPresent());

        registerBirthPresentAddress.setPinNo(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPinNoPresent());

        registerBirthPresentAddress.setOtStateRegionProvinceEn(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPresentOutSideIndiaProvinceEn());

        registerBirthPresentAddress.setOtStateRegionProvinceMl(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPresentOutSideIndiaProvinceMl());

        registerBirthPresentAddress.setCountryId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getCountryIdPresent());

//        registerBirthPresentAddress.setResAssoNoMl(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getResAssoNoMl());
        return registerBirthPresentAddress;
    }

    private RegisterBirthStatiticalInformation createRegisterBirthStatiticalInformation(NewBirthDetailRequest requestBirthReq, RegisterBirthDetail registerBirthDetail) {
        RegisterBirthStatiticalInformation registerBirthStatiticalInformation = new RegisterBirthStatiticalInformation();
        registerBirthStatiticalInformation.setDeliveryMethod(requestBirthReq.getNewBirthDetails().get(0).getDeliveryMethods());
        registerBirthStatiticalInformation.setWeightOfChild((long) requestBirthReq.getNewBirthDetails().get(0).getBirthWeight());
        registerBirthStatiticalInformation.setDurationOfPregnancyInWeek(requestBirthReq.getNewBirthDetails().get(0).getPregnancyDuration());
        registerBirthStatiticalInformation.setNatureOfMedicalAttention(requestBirthReq.getNewBirthDetails().get(0).getMedicalAttensionSub());
        registerBirthStatiticalInformation.setDeliveryMethod(requestBirthReq.getNewBirthDetails().get(0).getDeliveryMethods());
        registerBirthStatiticalInformation.setReligionId(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getReligionId());
        registerBirthStatiticalInformation.setFatherEducationId(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getFatherEucationid());
        registerBirthStatiticalInformation.setFatherProffessionId(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getFatherProffessionid());

        registerBirthStatiticalInformation.setMotherEducationId(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getMotherEducationid());
        registerBirthStatiticalInformation.setMotherProffessionId(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getMotherProffessionid());
        registerBirthStatiticalInformation.setMotherNationalityId(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getMotherNationalityid());
        registerBirthStatiticalInformation.setMotherAgeMarriage(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getMotherAgeMarriage());
        registerBirthStatiticalInformation.setMotherAgeDelivery(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getMotherAgeDelivery());

        // registerBirthStatiticalInformation.setMotherMaritalStatusId(requestBirthReq.getNewBirthDetails().get(0).getParentsDetails().getM;
//        registerBirthStatiticalInformation.setMotherUnmarried(requestBirthReq.getNewBirthDetails().get(0).getMotherUnmarried());


//        registerBirthStatiticalInformation.setMotherResLbId(registerBirthDetail.getTenantId());
//        registerBirthStatiticalInformation.setMotherResLbCode(registerBirthDetail.getTenantId());
//        registerBirthStatiticalInformation.setMotherResPlaceTypeId(requestBirthReq.getNewBirthDetails().get(0).getMotherResPlaceTypeId());
//        registerBirthStatiticalInformation.setMotherResLbTypeId(requestBirthReq.getNewBirthDetails().get(0).getMotherResLbTypeId());
//        registerBirthStatiticalInformation.setMotherResDistrictId(requestBirthReq.getNewBirthDetails().get(0).getMotherResDistrictId());
//        registerBirthStatiticalInformation.setMotherResStateId(requestBirthReq.getNewBirthDetails().get(0).getMotherResStateId());
//        registerBirthStatiticalInformation.setMotherResCountryId(requestBirthReq.getNewBirthDetails().get(0).getMotherResCountryId());
        registerBirthStatiticalInformation.setMotherResdnceAddrType(registerBirthDetail.getTenantLbType());
        registerBirthStatiticalInformation.setMotherResdnceTenentId(registerBirthDetail.getTenantId());
        registerBirthStatiticalInformation.setMotherResdncePlaceType(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPresentOutSideIndiaadrsCityTown());
//        registerBirthStatiticalInformation.setMotherResdncePlaceEn(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getPresentInside());
//        registerBirthStatiticalInformation.setMotherResdncePlaceMl(requestBirthReq.getNewBirthDetails().get(0).getMotherResdncePlaceMl());
//        registerBirthStatiticalInformation.setMotherResdnceLbType(requestBirthReq.getNewBirthDetails().get(0).getMotherResdnceLbType());
        registerBirthStatiticalInformation.setMotherResdnceDistrictId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getDistrictIdPresent());
        registerBirthStatiticalInformation.setMotherResdnceStateId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getStateIdPresent());
        registerBirthStatiticalInformation.setMotherResdnceCountryId(requestBirthReq.getNewBirthDetails().get(0).getParentAddress().getCountryIdPresent());
        return registerBirthStatiticalInformation;

    }
}


