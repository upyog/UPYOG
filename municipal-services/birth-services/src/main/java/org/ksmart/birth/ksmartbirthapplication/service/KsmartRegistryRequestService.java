package org.ksmart.birth.ksmartbirthapplication.service;

import lombok.extern.slf4j.Slf4j;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.*;

import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthDetailsRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class KsmartRegistryRequestService {
    public RegisterBirthDetailsRequest createRegistryRequest(KsmartBirthDetailsRequest requestKsmartBirthReq) {
        RegisterBirthDetailsRequest request = new RegisterBirthDetailsRequest();
        List<RegisterBirthDetail> registerBirthDetails = new LinkedList<>();
        RegisterBirthDetail registerBirthDetail = new RegisterBirthDetail();
        registerBirthDetail.setDateOfReport(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.setDateOfBirth(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getDateOfBirth());
        registerBirthDetail.setDateOfReport(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.setDateOfBirth(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getDateOfBirth());
        registerBirthDetail.setTimeOfBirth(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getTimeOfBirth());
        registerBirthDetail.setAmpm(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getAmpm());
        registerBirthDetail.setFirstNameEn(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getFirstNameEn());
        registerBirthDetail.setFirstNameMl(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getFirstNameMl());
        registerBirthDetail.setMiddleNameEn(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getMiddleNameEn());
        registerBirthDetail.setMiddleNameMl(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getMiddleNameMl());
        registerBirthDetail.setLastNameEn(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getLastNameEn());
        registerBirthDetail.setLastNameMl(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getLastNameMl());
        registerBirthDetail.setTenantId(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getTenantId());
        registerBirthDetail.setGender(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getGender());
        registerBirthDetail.setRemarksEn(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getRemarksEn());
        registerBirthDetail.setRemarksMl(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getRemarksMl());
        registerBirthDetail.setAadharNo(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getAadharNo());
        registerBirthDetail.setEsignUserCode(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getEsignUserCode());
        registerBirthDetail.setEsignUserDesigCode(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getEsignUserDesigCode());
        registerBirthDetail.setIsFatherInfoMissing(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getIsFatherInfoMissing());
        registerBirthDetail.setIsMotherInfoMissing(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getIsMotherInfoMissing());
//        registerBirthDetail.setNoOfAliveBirth(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getNoOfAliveBirth());
//        registerBirthDetail.setMultipleBirthDetId(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getMultipleBirthDetailsIid());
//        registerBirthDetail.setIsBornOutside(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getIsBornOutside());
//        registerBirthDetail.setOtPassportNo(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getPassportNo());
        registerBirthDetail.setRegistrationNo(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getRegistrationNo());
        registerBirthDetail.setRegistrationStatus("ACTIVE");
        registerBirthDetail.setRegistrationDate(requestKsmartBirthReq.getKsmartBirthDetails().get(0).getRegistrationDate());
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

    private RegisterBirthPlace createBirthPlace(KsmartBirthDetailsRequest requestBirthReq) {
        RegisterBirthPlace registerBirthPlace = new RegisterBirthPlace();
        registerBirthPlace.setPlaceOfBirthId(requestBirthReq.getKsmartBirthDetails().get(0).getPlaceofBirthId());
        registerBirthPlace.setHospitalId(requestBirthReq.getKsmartBirthDetails().get(0).getHospitalId());
        registerBirthPlace.setVehicleTypeId(requestBirthReq.getKsmartBirthDetails().get(0).getVehicleTypeid());
        registerBirthPlace.setVehicleRegistrationNo(requestBirthReq.getKsmartBirthDetails().get(0).getVehicleRegistrationNo());
        registerBirthPlace.setVehicleFromEn(requestBirthReq.getKsmartBirthDetails().get(0).getVehicleFromEn());
        registerBirthPlace.setVehicleToEn(requestBirthReq.getKsmartBirthDetails().get(0).getVehicleToEn());
        registerBirthPlace.setVehicleFromMl(requestBirthReq.getKsmartBirthDetails().get(0).getVehicleFromMl());
        registerBirthPlace.setVehicleToMl(requestBirthReq.getKsmartBirthDetails().get(0).getVehicleToMl());
//        registerBirthPlace.setVehicleOtherEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleOtherEn());
//        registerBirthPlace.setVehicleOtherMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleOtherMl());
        registerBirthPlace.setVehicleAdmitHospitalEn(requestBirthReq.getKsmartBirthDetails().get(0).getSetadmittedHospitalEn());
//        registerBirthPlace.setVehicleAdmitHospitalMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleAdmitHospitalMl());
        registerBirthPlace.setPublicPlaceId(requestBirthReq.getKsmartBirthDetails().get(0).getPublicPlaceType());
//        registerBirthPlace.setHoHouseHolderEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoHouseHolderEn());
//        registerBirthPlace.setHoHouseHolderMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoHouseHolderMl());
//        registerBirthPlace.setHoBuildingNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoBuildingNo());
//        registerBirthPlace.setHoResAssoNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoResAssoNo());
//        registerBirthPlace.setHoHousenNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoHousenNo());
        registerBirthPlace.setHouseNameEn(requestBirthReq.getKsmartBirthDetails().get(0).getAdrsHouseNameEn());
        registerBirthPlace.setHouseNameMl(requestBirthReq.getKsmartBirthDetails().get(0).getAdrsHouseNameMl());

//        registerBirthPlace.setHoVillageId(requestBirthReq.getKsmartBirthDetails().get(0).getPresentInsideKeralaVillage());
//        registerBirthPlace.setHoTalukId(requestBirthReq.getKsmartBirthgetKsmartBirthDetailsDetails().get(0).getParentAddress().getPresentInsideKeralaTaluk());
//        registerBirthPlace.setHoDistrictId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoDistrictId());
//        registerBirthPlace.setHoStateId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoStateId());
        registerBirthPlace.setHoPoId(requestBirthReq.getKsmartBirthDetails().get(0).getAdrsPostOffice());
        registerBirthPlace.setHoPinNo(requestBirthReq.getKsmartBirthDetails().get(0).getAdrsPincode());
//        registerBirthPlace.setHoCountryId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoCountryId());
        registerBirthPlace.setWardId(requestBirthReq.getKsmartBirthDetails().get(0).getWardId());
//        registerBirthPlace.setOthDetailsEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getOthDetailsEn());
//        registerBirthPlace.setOthDetailsMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getOthDetailsMl());
        registerBirthPlace.setInstitutionTypeId(requestBirthReq.getKsmartBirthDetails().get(0).getInstitutionTypeId());
        registerBirthPlace.setInstitutionId(requestBirthReq.getKsmartBirthDetails().get(0).getInstitutionId());

//        registerBirthPlace.setAuthOfficerId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getAuthOfficerId());
//        registerBirthPlace.setAuthOfficerDesigId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getAuthOfficerDesigId());
//        registerBirthPlace.setOthAuthOfficerName(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getOthAuthOfficerName());
//        registerBirthPlace.setOthAuthOfficerDesig(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getOthAuthOfficerDesig());
//        registerBirthPlace.setInformantsNameEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsNameEn());
//        registerBirthPlace.setInformantsNameMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsNameMl());
//        registerBirthPlace.setInformantsAddressEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsAddressEn());
//        registerBirthPlace.setInformantsAddressMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsAddressMl());


//        registerBirthPlace.setInformantsMobileNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsMobileNo());
//        registerBirthPlace.setInformantsAadhaarNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsAadhaarNo());
        return registerBirthPlace;
    }

    //  /*
    private RegisterBirthMotherInfo createMotherInfor(KsmartBirthDetailsRequest requestBirthReq) {
        RegisterBirthMotherInfo registerBirthMotherInfo = new RegisterBirthMotherInfo();
        registerBirthMotherInfo.setFirstNameEn(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getFirstNameEn());

        registerBirthMotherInfo.setFirstNameMl(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getFirstNameMl());

//        registerBirthMotherInfo.setMiddleNameEn(requestBirthReq.getKsmartBirthDetails().get(0).getBirthMotherInfo().getMiddleNameEn());
//        registerBirthMotherInfo.setMiddleNameMl(requestBirthReq.getKsmartBirthDetails().get(0).getBirthMotherInfo().getMiddleNameMl());
//        registerBirthMotherInfo.setLastNameEn(requestBirthReq.getKsmartBirthDetails().get(0).getBirthMotherInfo().getLastNameEn());
//        registerBirthMotherInfo.setLastNameMl(requestBirthReq.getKsmartBirthDetails().get(0).getBirthMotherInfo().getLastNameMl());
        registerBirthMotherInfo.setAadharNo(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getMotherAadhar());
        registerBirthMotherInfo.setOtPassportNo(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getMotherPassport());
//        registerBirthMotherInfo.setEmailId(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getEmailId());
//        registerBirthMotherInfo.setMobileNo(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getMobileNo());
        return registerBirthMotherInfo;
    }

    private RegisterBirthFatherInfo createFatherInfor(KsmartBirthDetailsRequest requestBirthReq) {
        RegisterBirthFatherInfo registerBirthFatherInfo = new RegisterBirthFatherInfo();
        registerBirthFatherInfo.setFirstNameEn(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getFatherFirstNameEn());
        registerBirthFatherInfo.setFirstNameMl(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getFirstNameMl());
//        registerBirthFatherInfo.setMiddleNameEn(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getMiddleNameEn());
//        registerBirthFatherInfo.setMiddleNameMl(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getMiddleNameMl());
//        registerBirthFatherInfo.setLastNameEn(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getLastNameEn());
//        registerBirthFatherInfo.setLastNameMl(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getLastNameMl());
        registerBirthFatherInfo.setAadharNo(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getFatherAadharno());
//        registerBirthFatherInfo.setOtPassportNo(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getPassportNo());
        registerBirthFatherInfo.setEmailId(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getFamilyEmailid());
        registerBirthFatherInfo.setMobileNo(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getFamilyMobileNo());
        return registerBirthFatherInfo;
    }

    private RegisterBirthPermanentAddress createRegisterBirthPermanentAddress(KsmartBirthDetailsRequest requestBirthReq) {
        RegisterBirthPermanentAddress registerBirthPermanentAddress = new RegisterBirthPermanentAddress();
//        registerBirthPermanentAddress.setResAssoNo(requestBirthReq.getKsmartBirthDetails().get(0).getBirthPermanentAddress().getResAssoNo());
        registerBirthPermanentAddress.setHouseNameEn(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getHouseNameNoEnPermanent());

        registerBirthPermanentAddress.setHouseNameMl(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getHouseNameNoMlPermanent());

//        registerBirthPermanentAddress.setOtAddress1En(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getOtAddress1En());
//        registerBirthPermanentAddress.setOtAddress1Ml(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getOtAddress1Ml());
//        registerBirthPermanentAddress.setOtAddress2En(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getOtAddress2En());
//        registerBirthPermanentAddress.setOtAddress2Ml(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getOtAddress2Ml());
        registerBirthPermanentAddress.setVillageId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPermntInKeralaAdrVillage());

        registerBirthPermanentAddress.setTenantId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPermntInKeralaAdrLBName());

        registerBirthPermanentAddress.setTalukId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPermntInKeralaAdrTaluk());

        registerBirthPermanentAddress.setDistrictId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getDistrictIdPermanent());

        registerBirthPermanentAddress.setStateId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getStateIdPermanent());
        registerBirthPermanentAddress.setPoId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPoNoPermanent());

        registerBirthPermanentAddress.setPinNo(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPinNoPermanent());

        registerBirthPermanentAddress.setOtStateRegionProvinceEn(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPermntOutsideIndiaprovinceEn());

//        registerBirthPermanentAddress.setOtStateRegionProvinceMl(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getOtStateRegionProvinceMl());
        registerBirthPermanentAddress.setCountryId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getCountryIdPermanent());

//        registerBirthPermanentAddress.setResAssoNoMl(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getResAssoNoMl());
        return registerBirthPermanentAddress;
    }

    private RegisterBirthPresentAddress createRegisterBirthPresentAddress(KsmartBirthDetailsRequest requestBirthReq) {
        RegisterBirthPresentAddress registerBirthPresentAddress = new RegisterBirthPresentAddress();
//        registerBirthPresentAddress.setResAssoNo(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getResAssoNo());
        registerBirthPresentAddress.setHouseNameEn(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getHouseNameNoEnPresent());

        registerBirthPresentAddress.setHouseNameMl(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getHouseNameNoMlPresent());

//        registerBirthPresentAddress.setOtAddress1En(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getOtAddress1En());
//        registerBirthPresentAddress.setOtAddress1Ml(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getOtAddress1Ml());
//        registerBirthPresentAddress.setOtAddress2En(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getOtAddress2En());
//        registerBirthPresentAddress.setOtAddress2Ml(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getOtAddress2Ml());
        registerBirthPresentAddress.setVillageId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getVillageNamePresent());

        registerBirthPresentAddress.setTenantId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPresentInsideKeralaLBName());

        registerBirthPresentAddress.setTalukId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPresentInsideKeralaTaluk());

        registerBirthPresentAddress.setDistrictId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getDistrictIdPresent());

        registerBirthPresentAddress.setStateId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getStateIdPresent());

        registerBirthPresentAddress.setPoId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPoNoPresent());

        registerBirthPresentAddress.setPinNo(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPinNoPresent());

        registerBirthPresentAddress.setOtStateRegionProvinceEn(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPresentOutSideIndiaProvinceEn());

        registerBirthPresentAddress.setOtStateRegionProvinceMl(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPresentOutSideIndiaProvinceMl());

        registerBirthPresentAddress.setCountryId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getCountryIdPresent());

//        registerBirthPresentAddress.setResAssoNoMl(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getResAssoNoMl());
        return registerBirthPresentAddress;
    }

    private RegisterBirthStatiticalInformation createRegisterBirthStatiticalInformation(KsmartBirthDetailsRequest requestBirthReq, RegisterBirthDetail registerBirthDetail) {
        RegisterBirthStatiticalInformation registerBirthStatiticalInformation = new RegisterBirthStatiticalInformation();
        registerBirthStatiticalInformation.setDeliveryMethod(requestBirthReq.getKsmartBirthDetails().get(0).getDeliveryMethods());
        registerBirthStatiticalInformation.setWeightOfChild((long) requestBirthReq.getKsmartBirthDetails().get(0).getBirthWeight());
        registerBirthStatiticalInformation.setDurationOfPregnancyInWeek(requestBirthReq.getKsmartBirthDetails().get(0).getPregnancyDuration());
        registerBirthStatiticalInformation.setNatureOfMedicalAttention(requestBirthReq.getKsmartBirthDetails().get(0).getMedicalAttensionSub());
        registerBirthStatiticalInformation.setDeliveryMethod(requestBirthReq.getKsmartBirthDetails().get(0).getDeliveryMethods());
        registerBirthStatiticalInformation.setReligionId(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getReligionId());
        registerBirthStatiticalInformation.setFatherEducationId(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getFatherEucationid());
        registerBirthStatiticalInformation.setFatherProffessionId(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getFatherProffessionid());

        registerBirthStatiticalInformation.setMotherEducationId(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getMotherEducationid());
        registerBirthStatiticalInformation.setMotherProffessionId(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getMotherProffessionid());
        registerBirthStatiticalInformation.setMotherNationalityId(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getMotherNationalityid());
        registerBirthStatiticalInformation.setMotherAgeMarriage(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getMotherAgeMarriage());
        registerBirthStatiticalInformation.setMotherAgeDelivery(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getMotherAgeDelivery());

        // registerBirthStatiticalInformation.setMotherMaritalStatusId(requestBirthReq.getKsmartBirthDetails().get(0).getParentsDetails().getM;
//        registerBirthStatiticalInformation.setMotherUnmarried(requestBirthReq.getKsmartBirthDetails().get(0).getMotherUnmarried());


//        registerBirthStatiticalInformation.setMotherResLbId(registerBirthDetail.getTenantId());
//        registerBirthStatiticalInformation.setMotherResLbCode(registerBirthDetail.getTenantId());
//        registerBirthStatiticalInformation.setMotherResPlaceTypeId(requestBirthReq.getKsmartBirthDetails().get(0).getMotherResPlaceTypeId());
//        registerBirthStatiticalInformation.setMotherResLbTypeId(requestBirthReq.getKsmartBirthDetails().get(0).getMotherResLbTypeId());
//        registerBirthStatiticalInformation.setMotherResDistrictId(requestBirthReq.getKsmartBirthDetails().get(0).getMotherResDistrictId());
//        registerBirthStatiticalInformation.setMotherResStateId(requestBirthReq.getKsmartBirthDetails().get(0).getMotherResStateId());
//        registerBirthStatiticalInformation.setMotherResCountryId(requestBirthReq.getKsmartBirthDetails().get(0).getMotherResCountryId());
        registerBirthStatiticalInformation.setMotherResdnceAddrType(registerBirthDetail.getTenantLbType());
        registerBirthStatiticalInformation.setMotherResdnceTenentId(registerBirthDetail.getTenantId());
        registerBirthStatiticalInformation.setMotherResdncePlaceType(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPresentOutSideIndiaadrsCityTown());
//        registerBirthStatiticalInformation.setMotherResdncePlaceEn(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getPresentInside());
//        registerBirthStatiticalInformation.setMotherResdncePlaceMl(requestBirthReq.getKsmartBirthDetails().get(0).getMotherResdncePlaceMl());
//        registerBirthStatiticalInformation.setMotherResdnceLbType(requestBirthReq.getKsmartBirthDetails().get(0).getMotherResdnceLbType());
        registerBirthStatiticalInformation.setMotherResdnceDistrictId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getDistrictIdPresent());
        registerBirthStatiticalInformation.setMotherResdnceStateId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getStateIdPresent());
        registerBirthStatiticalInformation.setMotherResdnceCountryId(requestBirthReq.getKsmartBirthDetails().get(0).getParentAddress().getCountryIdPresent());
        return registerBirthStatiticalInformation;

    }
}


