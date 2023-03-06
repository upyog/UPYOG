package org.ksmart.birth.stillbirth.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthResponse;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestServiceForStillBirth {
    public RegisterBirthDetailsRequest createRegistryRequest(StillBirthDetailRequest requestStill) {
        RegisterBirthDetailsRequest request = new RegisterBirthDetailsRequest();
        List<RegisterBirthDetail> registerBirthDetails = new LinkedList<>();
        RegisterBirthDetail registerBirthDetail = new RegisterBirthDetail();
        registerBirthDetail.setDateOfReport(requestStill.getBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.setDateOfBirth(requestStill.getBirthDetails().get(0).getDateOfBirth());
        registerBirthDetail.setDateOfReport(requestStill.getBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.setDateOfBirth(requestStill.getBirthDetails().get(0).getDateOfBirth());
        registerBirthDetail.setTimeOfBirth(requestStill.getBirthDetails().get(0).getTimeOfBirth());
        registerBirthDetail.setAmpm(requestStill.getBirthDetails().get(0).getAmpm());
        registerBirthDetail.setFirstNameEn(requestStill.getBirthDetails().get(0).getFirstNameEn());
        registerBirthDetail.setFirstNameMl(requestStill.getBirthDetails().get(0).getFirstNameMl());
        registerBirthDetail.setMiddleNameEn(requestStill.getBirthDetails().get(0).getMiddleNameEn());
        registerBirthDetail.setMiddleNameMl(requestStill.getBirthDetails().get(0).getMiddleNameMl());
        registerBirthDetail.setLastNameEn(requestStill.getBirthDetails().get(0).getLastNameEn());
        registerBirthDetail.setLastNameMl(requestStill.getBirthDetails().get(0).getLastNameMl());
        registerBirthDetail.setTenantId(requestStill.getBirthDetails().get(0).getTenantId());
        registerBirthDetail.setGender(requestStill.getBirthDetails().get(0).getGender());
        registerBirthDetail.setRemarksEn(requestStill.getBirthDetails().get(0).getRemarksEn());
        registerBirthDetail.setRemarksMl(requestStill.getBirthDetails().get(0).getRemarksMl());
        registerBirthDetail.setAadharNo(requestStill.getBirthDetails().get(0).getAadharNo());
        registerBirthDetail.setEsignUserCode(requestStill.getBirthDetails().get(0).getEsignUserCode());
        registerBirthDetail.setEsignUserDesigCode(requestStill.getBirthDetails().get(0).getEsignUserDesigCode());
        registerBirthDetail.setIsFatherInfoMissing(requestStill.getBirthDetails().get(0).getParentsDetails().getIsFatherInfoMissing());
        registerBirthDetail.setIsMotherInfoMissing(requestStill.getBirthDetails().get(0).getParentsDetails().getIsMotherInfoMissing());
//        registerBirthDetail.setNoOfAliveBirth(requestStill.getBirthDetails().get(0).getNoOfAliveBirth());
//        registerBirthDetail.setMultipleBirthDetId(requestStill.getBirthDetails().get(0).getMultipleBirthDetailsIid());
//        registerBirthDetail.setIsBornOutside(requestStill.getBirthDetails().get(0).getIsBornOutside());
//        registerBirthDetail.setOtPassportNo(requestStill.getBirthDetails().get(0).getPassportNo());
        registerBirthDetail.setRegistrationNo(requestStill.getBirthDetails().get(0).getRegistrationNo());
        registerBirthDetail.setRegistrationStatus("ACTIVE");
        registerBirthDetail.setRegistrationDate(requestStill.getBirthDetails().get(0).getRegistrationDate());
        registerBirthDetail.setRegisterBirthPlace(createBirthPlace(requestStill));
        registerBirthDetail.setRegisterBirthFather(createFatherInfor(requestStill));
        registerBirthDetail.setRegisterBirthMother(createMotherInfor(requestStill));
        registerBirthDetail.setRegisterBirthPermanent(createRegisterBirthPermanentAddress(requestStill));
        registerBirthDetail.setRegisterBirthPresent(createRegisterBirthPresentAddress(requestStill));
        registerBirthDetail.setRegisterBirthStatitical(createRegisterBirthStatiticalInformation(requestStill, registerBirthDetail));
        registerBirthDetails.add(registerBirthDetail);
        request.setRequestInfo(requestStill.getRequestInfo());
        request.setRegisterBirthDetails(registerBirthDetails);
        return request;
    }

    private RegisterBirthPlace createBirthPlace(StillBirthDetailRequest requestStill) {
        RegisterBirthPlace registerBirthPlace = new RegisterBirthPlace();
        registerBirthPlace.setPlaceOfBirthId(requestStill.getBirthDetails().get(0).getPlaceofBirthId());
        registerBirthPlace.setHospitalId(requestStill.getBirthDetails().get(0).getHospitalId());
        registerBirthPlace.setVehicleTypeId(requestStill.getBirthDetails().get(0).getVehicleTypeid());
        registerBirthPlace.setVehicleRegistrationNo(requestStill.getBirthDetails().get(0).getVehicleRegistrationNo());
        registerBirthPlace.setVehicleFromEn(requestStill.getBirthDetails().get(0).getVehicleFromEn());
        registerBirthPlace.setVehicleToEn(requestStill.getBirthDetails().get(0).getVehicleToEn());
        registerBirthPlace.setVehicleFromMl(requestStill.getBirthDetails().get(0).getVehicleFromMl());
        registerBirthPlace.setVehicleToMl(requestStill.getBirthDetails().get(0).getVehicleToMl());
//        registerBirthPlace.setVehicleOtherEn(requestStill.getBirthDetails().get(0).getBirthPlace().getVehicleOtherEn());
//        registerBirthPlace.setVehicleOtherMl(requestStill.getBirthDetails().get(0).getBirthPlace().getVehicleOtherMl());
        registerBirthPlace.setVehicleAdmitHospitalEn(requestStill.getBirthDetails().get(0).getSetadmittedHospitalEn());
//        registerBirthPlace.setVehicleAdmitHospitalMl(requestStill.getBirthDetails().get(0).getBirthPlace().getVehicleAdmitHospitalMl());
        registerBirthPlace.setPublicPlaceId(requestStill.getBirthDetails().get(0).getPublicPlaceType());
//        registerBirthPlace.setHoHouseHolderEn(requestStill.getBirthDetails().get(0).getBirthPlace().getHoHouseHolderEn());
//        registerBirthPlace.setHoHouseHolderMl(requestStill.getBirthDetails().get(0).getBirthPlace().getHoHouseHolderMl());
//        registerBirthPlace.setHoBuildingNo(requestStill.getBirthDetails().get(0).getBirthPlace().getHoBuildingNo());
//        registerBirthPlace.setHoResAssoNo(requestStill.getBirthDetails().get(0).getBirthPlace().getHoResAssoNo());
//        registerBirthPlace.setHoHousenNo(requestStill.getBirthDetails().get(0).getBirthPlace().getHoHousenNo());
        registerBirthPlace.setHouseNameEn(requestStill.getBirthDetails().get(0).getAdrsHouseNameEn());
        registerBirthPlace.setHouseNameMl(requestStill.getBirthDetails().get(0).getAdrsHouseNameMl());

//        registerBirthPlace.setHoVillageId(requestStill.getBirthDetails().get(0).getPresentInsideKeralaVillage());
//        registerBirthPlace.setHoTalukId(requestStill.getBirthDetails().get(0).getParentAddress().getPresentInsideKeralaTaluk());
//        registerBirthPlace.setHoDistrictId(requestStill.getBirthDetails().get(0).getBirthPlace().getHoDistrictId());
//        registerBirthPlace.setHoStateId(requestStill.getBirthDetails().get(0).getBirthPlace().getHoStateId());
        registerBirthPlace.setHoPoId(requestStill.getBirthDetails().get(0).getAdrsPostOffice());
        registerBirthPlace.setHoPinNo(requestStill.getBirthDetails().get(0).getAdrsPincode());
//        registerBirthPlace.setHoCountryId(requestStill.getBirthDetails().get(0).getBirthPlace().getHoCountryId());
        registerBirthPlace.setWardId(requestStill.getBirthDetails().get(0).getWardId());
//        registerBirthPlace.setOthDetailsEn(requestStill.getBirthDetails().get(0).getBirthPlace().getOthDetailsEn());
//        registerBirthPlace.setOthDetailsMl(requestStill.getBirthDetails().get(0).getBirthPlace().getOthDetailsMl());
        registerBirthPlace.setInstitutionTypeId(requestStill.getBirthDetails().get(0).getInstitutionTypeId());
        registerBirthPlace.setInstitutionId(requestStill.getBirthDetails().get(0).getInstitutionId());

//        registerBirthPlace.setAuthOfficerId(requestStill.getBirthDetails().get(0).getBirthPlace().getAuthOfficerId());
//        registerBirthPlace.setAuthOfficerDesigId(requestStill.getBirthDetails().get(0).getBirthPlace().getAuthOfficerDesigId());
//        registerBirthPlace.setOthAuthOfficerName(requestStill.getBirthDetails().get(0).getBirthPlace().getOthAuthOfficerName());
//        registerBirthPlace.setOthAuthOfficerDesig(requestStill.getBirthDetails().get(0).getBirthPlace().getOthAuthOfficerDesig());
//        registerBirthPlace.setInformantsNameEn(requestStill.getBirthDetails().get(0).getBirthPlace().getInformantsNameEn());
//        registerBirthPlace.setInformantsNameMl(requestStill.getBirthDetails().get(0).getBirthPlace().getInformantsNameMl());
//        registerBirthPlace.setInformantsAddressEn(requestStill.getBirthDetails().get(0).getBirthPlace().getInformantsAddressEn());
//        registerBirthPlace.setInformantsAddressMl(requestStill.getBirthDetails().get(0).getBirthPlace().getInformantsAddressMl());


//        registerBirthPlace.setInformantsMobileNo(requestStill.getBirthDetails().get(0).getBirthPlace().getInformantsMobileNo());
//        registerBirthPlace.setInformantsAadhaarNo(requestStill.getBirthDetails().get(0).getBirthPlace().getInformantsAadhaarNo());
        return registerBirthPlace;
    }

    //  /*
    private RegisterBirthMotherInfo createMotherInfor(StillBirthDetailRequest requestStill) {
        RegisterBirthMotherInfo registerBirthMotherInfo = new RegisterBirthMotherInfo();
        registerBirthMotherInfo.setFirstNameEn(requestStill.getBirthDetails().get(0).getParentsDetails().getFirstNameEn());

        registerBirthMotherInfo.setFirstNameMl(requestStill.getBirthDetails().get(0).getParentsDetails().getFirstNameMl());

//        registerBirthMotherInfo.setMiddleNameEn(requestStill.getBirthDetails().get(0).getBirthMotherInfo().getMiddleNameEn());
//        registerBirthMotherInfo.setMiddleNameMl(requestStill.getBirthDetails().get(0).getBirthMotherInfo().getMiddleNameMl());
//        registerBirthMotherInfo.setLastNameEn(requestStill.getBirthDetails().get(0).getBirthMotherInfo().getLastNameEn());
//        registerBirthMotherInfo.setLastNameMl(requestStill.getBirthDetails().get(0).getBirthMotherInfo().getLastNameMl());
        registerBirthMotherInfo.setAadharNo(requestStill.getBirthDetails().get(0).getParentsDetails().getMotherAadhar());
        registerBirthMotherInfo.setOtPassportNo(requestStill.getBirthDetails().get(0).getParentsDetails().getMotherPassport());
//        registerBirthMotherInfo.setEmailId(requestStill.getBirthDetails().get(0).getParentsDetails().getEmailId());
//        registerBirthMotherInfo.setMobileNo(requestStill.getBirthDetails().get(0).getParentsDetails().getMobileNo());
        return registerBirthMotherInfo;
    }

    private RegisterBirthFatherInfo createFatherInfor(StillBirthDetailRequest requestStill) {
        RegisterBirthFatherInfo registerBirthFatherInfo = new RegisterBirthFatherInfo();
        registerBirthFatherInfo.setFirstNameEn(requestStill.getBirthDetails().get(0).getParentsDetails().getFatherFirstNameEn());
        registerBirthFatherInfo.setFirstNameMl(requestStill.getBirthDetails().get(0).getParentsDetails().getFirstNameMl());
//        registerBirthFatherInfo.setMiddleNameEn(requestStill.getBirthDetails().get(0).getParentsDetails().getMiddleNameEn());
//        registerBirthFatherInfo.setMiddleNameMl(requestStill.getBirthDetails().get(0).getParentsDetails().getMiddleNameMl());
//        registerBirthFatherInfo.setLastNameEn(requestStill.getBirthDetails().get(0).getParentsDetails().getLastNameEn());
//        registerBirthFatherInfo.setLastNameMl(requestStill.getBirthDetails().get(0).getParentsDetails().getLastNameMl());
        registerBirthFatherInfo.setAadharNo(requestStill.getBirthDetails().get(0).getParentsDetails().getFatherAadharno());
//        registerBirthFatherInfo.setOtPassportNo(requestStill.getBirthDetails().get(0).getParentsDetails().getPassportNo());
        registerBirthFatherInfo.setEmailId(requestStill.getBirthDetails().get(0).getParentsDetails().getFamilyEmailid());
        registerBirthFatherInfo.setMobileNo(requestStill.getBirthDetails().get(0).getParentsDetails().getFamilyMobileNo());
        return registerBirthFatherInfo;
    }

    private RegisterBirthPermanentAddress createRegisterBirthPermanentAddress(StillBirthDetailRequest requestStill) {
        RegisterBirthPermanentAddress registerBirthPermanentAddress = new RegisterBirthPermanentAddress();
//        registerBirthPermanentAddress.setResAssoNo(requestStill.getBirthDetails().get(0).getBirthPermanentAddress().getResAssoNo());
        registerBirthPermanentAddress.setHouseNameEn(requestStill.getBirthDetails().get(0).getParentAddress().getHouseNameNoEnPermanent());

        registerBirthPermanentAddress.setHouseNameMl(requestStill.getBirthDetails().get(0).getParentAddress().getHouseNameNoMlPermanent());

//        registerBirthPermanentAddress.setOtAddress1En(requestStill.getBirthDetails().get(0).getParentAddress().getOtAddress1En());
//        registerBirthPermanentAddress.setOtAddress1Ml(requestStill.getBirthDetails().get(0).getParentAddress().getOtAddress1Ml());
//        registerBirthPermanentAddress.setOtAddress2En(requestStill.getBirthDetails().get(0).getParentAddress().getOtAddress2En());
//        registerBirthPermanentAddress.setOtAddress2Ml(requestStill.getBirthDetails().get(0).getParentAddress().getOtAddress2Ml());
        registerBirthPermanentAddress.setVillageId(requestStill.getBirthDetails().get(0).getParentAddress().getPermntInKeralaAdrVillage());

        registerBirthPermanentAddress.setTenantId(requestStill.getBirthDetails().get(0).getParentAddress().getPermntInKeralaAdrLBName());

        registerBirthPermanentAddress.setTalukId(requestStill.getBirthDetails().get(0).getParentAddress().getPermntInKeralaAdrTaluk());

        registerBirthPermanentAddress.setDistrictId(requestStill.getBirthDetails().get(0).getParentAddress().getDistrictIdPermanent());

        registerBirthPermanentAddress.setStateId(requestStill.getBirthDetails().get(0).getParentAddress().getStateIdPermanent());
        registerBirthPermanentAddress.setPoId(requestStill.getBirthDetails().get(0).getParentAddress().getPoNoPermanent());

        registerBirthPermanentAddress.setPinNo(requestStill.getBirthDetails().get(0).getParentAddress().getPinNoPermanent());

        registerBirthPermanentAddress.setOtStateRegionProvinceEn(requestStill.getBirthDetails().get(0).getParentAddress().getPermntOutsideIndiaprovinceEn());

//        registerBirthPermanentAddress.setOtStateRegionProvinceMl(requestStill.getBirthDetails().get(0).getParentAddress().getOtStateRegionProvinceMl());
        registerBirthPermanentAddress.setCountryId(requestStill.getBirthDetails().get(0).getParentAddress().getCountryIdPermanent());

//        registerBirthPermanentAddress.setResAssoNoMl(requestStill.getBirthDetails().get(0).getParentAddress().getResAssoNoMl());
        return registerBirthPermanentAddress;
    }

    private RegisterBirthPresentAddress createRegisterBirthPresentAddress(StillBirthDetailRequest requestStill) {
        RegisterBirthPresentAddress registerBirthPresentAddress = new RegisterBirthPresentAddress();
//        registerBirthPresentAddress.setResAssoNo(requestStill.getBirthDetails().get(0).getParentAddress().getResAssoNo());
        registerBirthPresentAddress.setHouseNameEn(requestStill.getBirthDetails().get(0).getParentAddress().getHouseNameNoEnPresent());

        registerBirthPresentAddress.setHouseNameMl(requestStill.getBirthDetails().get(0).getParentAddress().getHouseNameNoMlPresent());

//        registerBirthPresentAddress.setOtAddress1En(requestStill.getBirthDetails().get(0).getParentAddress().getOtAddress1En());
//        registerBirthPresentAddress.setOtAddress1Ml(requestStill.getBirthDetails().get(0).getParentAddress().getOtAddress1Ml());
//        registerBirthPresentAddress.setOtAddress2En(requestStill.getBirthDetails().get(0).getParentAddress().getOtAddress2En());
//        registerBirthPresentAddress.setOtAddress2Ml(requestStill.getBirthDetails().get(0).getParentAddress().getOtAddress2Ml());
        registerBirthPresentAddress.setVillageId(requestStill.getBirthDetails().get(0).getParentAddress().getVillageNamePresent());

        registerBirthPresentAddress.setTenantId(requestStill.getBirthDetails().get(0).getParentAddress().getPresentInsideKeralaLBName());

        registerBirthPresentAddress.setTalukId(requestStill.getBirthDetails().get(0).getParentAddress().getPresentInsideKeralaTaluk());

        registerBirthPresentAddress.setDistrictId(requestStill.getBirthDetails().get(0).getParentAddress().getDistrictIdPresent());

        registerBirthPresentAddress.setStateId(requestStill.getBirthDetails().get(0).getParentAddress().getStateIdPresent());

        registerBirthPresentAddress.setPoId(requestStill.getBirthDetails().get(0).getParentAddress().getPoNoPresent());

        registerBirthPresentAddress.setPinNo(requestStill.getBirthDetails().get(0).getParentAddress().getPinNoPresent());

        registerBirthPresentAddress.setOtStateRegionProvinceEn(requestStill.getBirthDetails().get(0).getParentAddress().getPresentOutSideIndiaProvinceEn());

        registerBirthPresentAddress.setOtStateRegionProvinceMl(requestStill.getBirthDetails().get(0).getParentAddress().getPresentOutSideIndiaProvinceMl());

        registerBirthPresentAddress.setCountryId(requestStill.getBirthDetails().get(0).getParentAddress().getCountryIdPresent());

//        registerBirthPresentAddress.setResAssoNoMl(requestStill.getBirthDetails().get(0).getParentAddress().getResAssoNoMl());
        return registerBirthPresentAddress;
    }

    private RegisterBirthStatiticalInformation createRegisterBirthStatiticalInformation(StillBirthDetailRequest requestStill, RegisterBirthDetail registerBirthDetail) {
        RegisterBirthStatiticalInformation registerBirthStatiticalInformation = new RegisterBirthStatiticalInformation();
        registerBirthStatiticalInformation.setDeliveryMethod(requestStill.getBirthDetails().get(0).getDeliveryMethods());
        registerBirthStatiticalInformation.setWeightOfChild((long) requestStill.getBirthDetails().get(0).getBirthWeight());
        registerBirthStatiticalInformation.setDurationOfPregnancyInWeek(requestStill.getBirthDetails().get(0).getPregnancyDuration());
        registerBirthStatiticalInformation.setNatureOfMedicalAttention(requestStill.getBirthDetails().get(0).getMedicalAttensionSub());
        registerBirthStatiticalInformation.setDeliveryMethod(requestStill.getBirthDetails().get(0).getDeliveryMethods());
        registerBirthStatiticalInformation.setReligionId(requestStill.getBirthDetails().get(0).getParentsDetails().getReligionId());
        registerBirthStatiticalInformation.setFatherEducationId(requestStill.getBirthDetails().get(0).getParentsDetails().getFatherEucationid());
        registerBirthStatiticalInformation.setFatherProffessionId(requestStill.getBirthDetails().get(0).getParentsDetails().getFatherProffessionid());

        registerBirthStatiticalInformation.setMotherEducationId(requestStill.getBirthDetails().get(0).getParentsDetails().getMotherEducationid());
        registerBirthStatiticalInformation.setMotherProffessionId(requestStill.getBirthDetails().get(0).getParentsDetails().getMotherProffessionid());
        registerBirthStatiticalInformation.setMotherNationalityId(requestStill.getBirthDetails().get(0).getParentsDetails().getMotherNationalityid());
        registerBirthStatiticalInformation.setMotherAgeMarriage(requestStill.getBirthDetails().get(0).getParentsDetails().getMotherAgeMarriage());
        registerBirthStatiticalInformation.setMotherAgeDelivery(requestStill.getBirthDetails().get(0).getParentsDetails().getMotherAgeDelivery());

        // registerBirthStatiticalInformation.setMotherMaritalStatusId(requestStill.getBirthDetails().get(0).getParentsDetails().getM;
//        registerBirthStatiticalInformation.setMotherUnmarried(requestStill.getBirthDetails().get(0).getMotherUnmarried());


//        registerBirthStatiticalInformation.setMotherResLbId(registerBirthDetail.getTenantId());
//        registerBirthStatiticalInformation.setMotherResLbCode(registerBirthDetail.getTenantId());
//        registerBirthStatiticalInformation.setMotherResPlaceTypeId(requestStill.getBirthDetails().get(0).getMotherResPlaceTypeId());
//        registerBirthStatiticalInformation.setMotherResLbTypeId(requestStill.getBirthDetails().get(0).getMotherResLbTypeId());
//        registerBirthStatiticalInformation.setMotherResDistrictId(requestStill.getBirthDetails().get(0).getMotherResDistrictId());
//        registerBirthStatiticalInformation.setMotherResStateId(requestStill.getBirthDetails().get(0).getMotherResStateId());
//        registerBirthStatiticalInformation.setMotherResCountryId(requestStill.getBirthDetails().get(0).getMotherResCountryId());
        registerBirthStatiticalInformation.setMotherResdnceAddrType(registerBirthDetail.getTenantLbType());
        registerBirthStatiticalInformation.setMotherResdnceTenentId(registerBirthDetail.getTenantId());
        registerBirthStatiticalInformation.setMotherResdncePlaceType(requestStill.getBirthDetails().get(0).getParentAddress().getPresentOutSideIndiaadrsCityTown());
//        registerBirthStatiticalInformation.setMotherResdncePlaceEn(requestStill.getBirthDetails().get(0).getParentAddress().getPresentInside());
//        registerBirthStatiticalInformation.setMotherResdncePlaceMl(requestStill.getBirthDetails().get(0).getMotherResdncePlaceMl());
//        registerBirthStatiticalInformation.setMotherResdnceLbType(requestStill.getBirthDetails().get(0).getMotherResdnceLbType());
        registerBirthStatiticalInformation.setMotherResdnceDistrictId(requestStill.getBirthDetails().get(0).getParentAddress().getDistrictIdPresent());
        registerBirthStatiticalInformation.setMotherResdnceStateId(requestStill.getBirthDetails().get(0).getParentAddress().getStateIdPresent());
        registerBirthStatiticalInformation.setMotherResdnceCountryId(requestStill.getBirthDetails().get(0).getParentAddress().getCountryIdPresent());
        return registerBirthStatiticalInformation;

    }
}


