package org.ksmart.birth.birthapplication.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.birthapplication.model.birth.BirthPlace;
import org.ksmart.birth.birthregistry.model.*;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestService {
    public RegisterBirthDetailsRequest createRegistryRequest(BirthDetailsRequest requestBirthReq){
        RegisterBirthDetailsRequest request = new RegisterBirthDetailsRequest();
        List<RegisterBirthDetail> registerBirthDetail =  new LinkedList<>();
        registerBirthDetail.get(0).setDateOfReport(requestBirthReq.getBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.get(0).setDateOfBirth(requestBirthReq.getBirthDetails().get(0).getDateOfBirth());
        registerBirthDetail.get(0).setDateOfReport(requestBirthReq.getBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.get(0).setDateOfBirth(requestBirthReq.getBirthDetails().get(0).getDateOfBirth());
        registerBirthDetail.get(0).setTimeOfBirth(requestBirthReq.getBirthDetails().get(0).getTimeOfBirth());
        registerBirthDetail.get(0).setAmpm(requestBirthReq.getBirthDetails().get(0).getAmpm());
        registerBirthDetail.get(0).setFirstNameEn(requestBirthReq.getBirthDetails().get(0).getFirstNameEn());
        registerBirthDetail.get(0).setFirstNameMl(requestBirthReq.getBirthDetails().get(0).getFirstNameMl());
        registerBirthDetail.get(0).setMiddleNameEn(requestBirthReq.getBirthDetails().get(0).getMiddleNameEn());
        registerBirthDetail.get(0).setMiddleNameMl(requestBirthReq.getBirthDetails().get(0).getMiddleNameMl());
        registerBirthDetail.get(0).setLastNameEn(requestBirthReq.getBirthDetails().get(0).getLastNameEn());
        registerBirthDetail.get(0).setLastNameMl(requestBirthReq.getBirthDetails().get(0).getLastNameMl());
        registerBirthDetail.get(0).setTenantId(requestBirthReq.getBirthDetails().get(0).getTenantId());
        registerBirthDetail.get(0).setGender(requestBirthReq.getBirthDetails().get(0).getGender());
        registerBirthDetail.get(0).setRemarksEn(requestBirthReq.getBirthDetails().get(0).getRemarksEn());
        registerBirthDetail.get(0).setRemarksMl(requestBirthReq.getBirthDetails().get(0).getRemarksMl());
        registerBirthDetail.get(0).setAadharNo(requestBirthReq.getBirthDetails().get(0).getAadharNo());
        registerBirthDetail.get(0).setEsignUserCode(requestBirthReq.getBirthDetails().get(0).getEsignUserCode());
        registerBirthDetail.get(0).setEsignUserDesigCode(requestBirthReq.getBirthDetails().get(0).getEsignUserDesigCode());
        registerBirthDetail.get(0).setIsAdopted(requestBirthReq.getBirthDetails().get(0).getIsAdopted());
        registerBirthDetail.get(0).setIsAbandoned(requestBirthReq.getBirthDetails().get(0).getIsAbandoned());
        registerBirthDetail.get(0).setIsMultipleBirth(requestBirthReq.getBirthDetails().get(0).getIsMultipleBirth());
        registerBirthDetail.get(0).setIsFatherInfoMissing(requestBirthReq.getBirthDetails().get(0).getIsFatherInfoMissing());
        registerBirthDetail.get(0).setIsMotherInfoMissing(requestBirthReq.getBirthDetails().get(0).getIsMotherInfoMissing());
        registerBirthDetail.get(0).setNoOfAliveBirth(requestBirthReq.getBirthDetails().get(0).getNoOfAliveBirth());
        registerBirthDetail.get(0).setMultipleBirthDetId(requestBirthReq.getBirthDetails().get(0).getMultipleBirthDetailsIid());
        registerBirthDetail.get(0).setIsBornOutside(requestBirthReq.getBirthDetails().get(0).getIsBornOutside());
        registerBirthDetail.get(0).setOtPassportNo(requestBirthReq.getBirthDetails().get(0).getPassportNo());
        registerBirthDetail.get(0).setRegistrationNo(requestBirthReq.getBirthDetails().get(0).getRegistrationNo());
        registerBirthDetail.get(0).setRegistrationStatus("ACTIVE");
        registerBirthDetail.get(0).setRegistrationDate(requestBirthReq.getBirthDetails().get(0).getRegistrationDate());
        registerBirthDetail.get(0).setRegisterBirthPlace(createBirthPlace(requestBirthReq));
        request.setRequestInfo(requestBirthReq.getRequestInfo());
        request.setRegisterBirthDetails(registerBirthDetail);
        return  request;
    }
    private RegisterBirthPlace createBirthPlace(BirthDetailsRequest requestBirthReq) {
        RegisterBirthPlace registerBirthPlace = new RegisterBirthPlace();
        registerBirthPlace.setPlaceOfBirthId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getPlaceOfBirthId());
        registerBirthPlace.setHospitalId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHospitalId());
        registerBirthPlace.setVehicleTypeId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleTypeId());
        registerBirthPlace.setVehicleRegistrationNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleRegistrationNo());
        registerBirthPlace.setVehicleFromEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleFromEn());
        registerBirthPlace.setVehicleToEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleToEn());
        registerBirthPlace.setVehicleFromMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleFromMl());
        registerBirthPlace.setVehicleToMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleToMl());
        registerBirthPlace.setVehicleOtherEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleOtherEn());
        registerBirthPlace.setVehicleOtherMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleOtherMl());
        registerBirthPlace.setVehicleAdmitHospitalEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleAdmitHospitalEn());
        registerBirthPlace.setVehicleAdmitHospitalMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleAdmitHospitalMl());
        registerBirthPlace.setPublicPlaceId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getPublicPlaceId());
        registerBirthPlace.setHoHouseHolderEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoHouseHolderEn());
        registerBirthPlace.setHoHouseHolderMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoHouseHolderMl());
        registerBirthPlace.setHoBuildingNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoBuildingNo());
        registerBirthPlace.setHoResAssoNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoResAssoNo());
        registerBirthPlace.setHoHousenNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoHousenNo());
        registerBirthPlace.setHouseNameEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHouseNameEn());
        registerBirthPlace.setHouseNameMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHouseNameMl());
        registerBirthPlace.setHoVillageId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoVillageId());
        registerBirthPlace.setHoTalukId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoTalukId());
        registerBirthPlace.setHoDistrictId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoDistrictId());
        registerBirthPlace.setHoStateId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoStateId());
        registerBirthPlace.setHoPoId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoPoId());
        registerBirthPlace.setHoPinNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoPinNo());
        registerBirthPlace.setHoCountryId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoCountryId());
        registerBirthPlace.setWardId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getWardId());
        registerBirthPlace.setOthDetailsEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getOthDetailsEn());
        registerBirthPlace.setOthDetailsMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getOthDetailsMl());
        registerBirthPlace.setInstitutionTypeId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInstitutionTypeId());
        registerBirthPlace.setInstitutionId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInstitutionId());
        registerBirthPlace.setAuthOfficerId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getAuthOfficerId());
        registerBirthPlace.setAuthOfficerDesigId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getAuthOfficerDesigId());
        registerBirthPlace.setOthAuthOfficerName(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getOthAuthOfficerName());
        registerBirthPlace.setOthAuthOfficerDesig(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getOthAuthOfficerDesig());
        registerBirthPlace.setInformantsNameEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsNameEn());
        registerBirthPlace.setInformantsNameMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsNameMl());
        registerBirthPlace.setInformantsAddressEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsAddressEn());
        registerBirthPlace.setInformantsAddressMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsAddressMl());
        registerBirthPlace.setInformantsMobileNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsMobileNo());
        registerBirthPlace.setInformantsAadhaarNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getInformantsAadhaarNo());
        return registerBirthPlace;
    }

    private RegisterBirthMotherInfo createMotherInfor(BirthDetailsRequest requestBirthReq) {
        RegisterBirthMotherInfo registerBirthMotherInfo = new RegisterBirthMotherInfo();
        registerBirthMotherInfo.setFirstNameEn(requestBirthReq.getBirthDetails().get(0).getBirthMotherInfo().getFirstNameEn());

        return registerBirthMotherInfo;
    }

    private RegisterBirthFatherInfo createFatherInfor(BirthDetailsRequest requestBirthReq) {
        RegisterBirthFatherInfo registerBirthFatherInfo = new RegisterBirthFatherInfo();
        registerBirthFatherInfo.setFirstNameEn(requestBirthReq.getBirthDetails().get(0).getBirthFatherInfo().getFirstNameEn());

        return registerBirthFatherInfo;
    }

    private RegisterBirthPermanentAddress createRegisterBirthPermanentAddress(BirthDetailsRequest requestBirthReq) {
        RegisterBirthPermanentAddress registerBirthPermanentAddress = new RegisterBirthPermanentAddress();

        return registerBirthPermanentAddress;
    }

    private RegisterBirthPresentAddress createRegisterBirthPresentAddress(BirthDetailsRequest requestBirthReq) {
        RegisterBirthPresentAddress registerBirthPresentAddress = new RegisterBirthPresentAddress();
        registerBirthPresentAddress.setPresentAddress(requestBirthReq.getBirthDetails().get(0).getBirthPresentAddress().getHouseNameEn());

        return registerBirthPresentAddress;
    }

    private RegisterBirthStatiticalInformation createRegisterBirthStatiticalInformation(BirthDetailsRequest requestBirthReq) {
        RegisterBirthStatiticalInformation registerBirthStatiticalInformation = new RegisterBirthStatiticalInformation();
        registerBirthStatiticalInformation.setDeliveryMethod(requestBirthReq.getBirthDetails().get(0).getBirthStatisticalInformation().getDeliveryMethod());

        return registerBirthStatiticalInformation;
    }
}

