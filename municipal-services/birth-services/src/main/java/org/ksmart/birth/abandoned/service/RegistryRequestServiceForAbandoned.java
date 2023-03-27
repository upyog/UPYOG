package org.ksmart.birth.abandoned.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.newbirth.repository.NewBirthRepository;
import org.ksmart.birth.web.model.abandoned.AbandonedRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestServiceForAbandoned {
    private final NewBirthRepository repository;
    @Autowired
    RegistryRequestServiceForAbandoned(NewBirthRepository repository) {
        this.repository = repository;
    }


    public RegisterBirthDetailsRequest createRegistryRequestNew(NewBirthDetailRequest request) {///Work to get req to Register
        return repository.searchBirthDetailsForRegister(request);
    }
//        List<Object> preparedStmtValues = new ArrayList<>();
//        RegisterBirthDetailsRequest request = new RegisterBirthDetailsRequest();
//        SearchCriteria criteria = new SearchCriteria();
//        if(requestApplication.getBirthDetails().size() > 0) {
//            criteria.setApplicationNumber(requestApplication.getBirthDetails().get(0).getApplicationNo());
//            criteria.setTenantId(requestApplication.getBirthDetails().get(0).getTenantId());
//            String query = birthQueryBuilder.getApplicationSearchQueryForRegistry(criteria, preparedStmtValues);
//        }
//        List<NewBirthApplication> result = new ArrayList<>();
//        List<NewBirthApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), ksmartBirthApplicationRowMapper);
//        while (rs.next()) {
//            result.add(NewBirthApplication.builder()
//
//        return request;
//
//    }






    public RegisterBirthDetailsRequest createRegistryRequest(AbandonedRequest requestKsmartBirthReq) {
        RegisterBirthDetailsRequest request = new RegisterBirthDetailsRequest();
        List<RegisterBirthDetail> registerBirthDetails = new LinkedList<>();
        RegisterBirthDetail registerBirthDetail = new RegisterBirthDetail();
        registerBirthDetail.setDateOfReport(requestKsmartBirthReq.getBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.setApplicationId(requestKsmartBirthReq.getBirthDetails().get(0).getId());
        registerBirthDetail.setDateOfBirth(requestKsmartBirthReq.getBirthDetails().get(0).getDateOfBirth());
        registerBirthDetail.setDateOfReport(requestKsmartBirthReq.getBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.setDateOfBirth(requestKsmartBirthReq.getBirthDetails().get(0).getDateOfBirth());
        registerBirthDetail.setTimeOfBirth(requestKsmartBirthReq.getBirthDetails().get(0).getTimeOfBirth());
        registerBirthDetail.setAmpm(requestKsmartBirthReq.getBirthDetails().get(0).getAmpm());
        registerBirthDetail.setTenantId(requestKsmartBirthReq.getBirthDetails().get(0).getTenantId());
        registerBirthDetail.setGender(requestKsmartBirthReq.getBirthDetails().get(0).getGender());
        registerBirthDetail.setRemarksEn(requestKsmartBirthReq.getBirthDetails().get(0).getRemarksEn());
        registerBirthDetail.setRemarksMl(requestKsmartBirthReq.getBirthDetails().get(0).getRemarksMl());
        registerBirthDetail.setEsignUserCode(requestKsmartBirthReq.getBirthDetails().get(0).getEsignUserCode());
        registerBirthDetail.setEsignUserDesigCode(requestKsmartBirthReq.getBirthDetails().get(0).getEsignUserDesigCode());
        registerBirthDetail.setIsFatherInfoMissing(requestKsmartBirthReq.getBirthDetails().get(0).getParentsDetails().getIsFatherInfoMissing());
        registerBirthDetail.setIsMotherInfoMissing(requestKsmartBirthReq.getBirthDetails().get(0).getParentsDetails().getIsMotherInfoMissing());
//        registerBirthDetail.setNoOfAliveBirth(requestKsmartBirthReq.getBirthDetails().get(0).getNoOfAliveBirth());
//        registerBirthDetail.setMultipleBirthDetId(requestKsmartBirthReq.getBirthDetails().get(0).getMultipleBirthDetailsIid());
//        registerBirthDetail.setIsBornOutside(requestKsmartBirthReq.getBirthDetails().get(0).getIsBornOutside());
//        registerBirthDetail.setOtPassportNo(requestKsmartBirthReq.getBirthDetails().get(0).getPassportNo());
        registerBirthDetail.setRegistrationNo(requestKsmartBirthReq.getBirthDetails().get(0).getRegistrationNo());
        registerBirthDetail.setApplicationType(requestKsmartBirthReq.getBirthDetails().get(0).getApplicationType());
        registerBirthDetail.setRegistrationStatus("ACTIVE");
        registerBirthDetail.setRegistrationDate(requestKsmartBirthReq.getBirthDetails().get(0).getRegistrationDate());
        registerBirthDetail.setRegisterBirthPlace(createBirthPlace(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthFather(createFatherInfor(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthMother(createMotherInfor(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthStatitical(createRegisterBirthStatiticalInformation(requestKsmartBirthReq, registerBirthDetail));
        registerBirthDetails.add(registerBirthDetail);
        request.setRequestInfo(requestKsmartBirthReq.getRequestInfo());
        request.setRegisterBirthDetails(registerBirthDetails);
        return request;
    }

    private RegisterBirthPlace createBirthPlace(AbandonedRequest requestBirthReq) {
        RegisterBirthPlace registerBirthPlace = new RegisterBirthPlace();
        registerBirthPlace.setPlaceOfBirthId(requestBirthReq.getBirthDetails().get(0).getPlaceofBirthId());
        registerBirthPlace.setHospitalId(requestBirthReq.getBirthDetails().get(0).getHospitalId());
        registerBirthPlace.setVehicleTypeId(requestBirthReq.getBirthDetails().get(0).getVehicleTypeid());
        registerBirthPlace.setVehicleRegistrationNo(requestBirthReq.getBirthDetails().get(0).getVehicleRegistrationNo());
        registerBirthPlace.setVehicleFromEn(requestBirthReq.getBirthDetails().get(0).getVehicleFromEn());
        registerBirthPlace.setVehicleToEn(requestBirthReq.getBirthDetails().get(0).getVehicleToEn());
        registerBirthPlace.setVehicleFromMl(requestBirthReq.getBirthDetails().get(0).getVehicleFromMl());
        registerBirthPlace.setVehicleToMl(requestBirthReq.getBirthDetails().get(0).getVehicleToMl());
//        registerBirthPlace.setVehicleOtherEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleOtherEn());
//        registerBirthPlace.setVehicleOtherMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleOtherMl());
        registerBirthPlace.setVehicleAdmitHospitalEn(requestBirthReq.getBirthDetails().get(0).getSetadmittedHospitalEn());
//        registerBirthPlace.setVehicleAdmitHospitalMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getVehicleAdmitHospitalMl());
        registerBirthPlace.setPublicPlaceId(requestBirthReq.getBirthDetails().get(0).getPublicPlaceType());
//        registerBirthPlace.setHoHouseHolderEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoHouseHolderEn());
//        registerBirthPlace.setHoHouseHolderMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoHouseHolderMl());
//        registerBirthPlace.setHoBuildingNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoBuildingNo());
//        registerBirthPlace.setHoResAssoNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoResAssoNo());
//        registerBirthPlace.setHoHousenNo(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoHousenNo());
        registerBirthPlace.setHouseNameEn(requestBirthReq.getBirthDetails().get(0).getAdrsHouseNameEn());
        registerBirthPlace.setHouseNameMl(requestBirthReq.getBirthDetails().get(0).getAdrsHouseNameMl());

//        registerBirthPlace.setHoVillageId(requestBirthReq.getBirthDetails().get(0).getPresentInsideKeralaVillage());
//        registerBirthPlace.setHoTalukId(requestBirthReq.getBirthDetails().get(0).getParentAddress().getPresentInsideKeralaTaluk());
//        registerBirthPlace.setHoDistrictId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoDistrictId());
//        registerBirthPlace.setHoStateId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoStateId());
        registerBirthPlace.setHoPoId(requestBirthReq.getBirthDetails().get(0).getAdrsPostOffice());
        registerBirthPlace.setHoPinNo(requestBirthReq.getBirthDetails().get(0).getAdrsPincode());
//        registerBirthPlace.setHoCountryId(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getHoCountryId());
        registerBirthPlace.setWardId(requestBirthReq.getBirthDetails().get(0).getWardId());
//        registerBirthPlace.setOthDetailsEn(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getOthDetailsEn());
//        registerBirthPlace.setOthDetailsMl(requestBirthReq.getBirthDetails().get(0).getBirthPlace().getOthDetailsMl());
        registerBirthPlace.setInstitutionTypeId(requestBirthReq.getBirthDetails().get(0).getInstitutionTypeId());
        registerBirthPlace.setInstitutionId(requestBirthReq.getBirthDetails().get(0).getInstitutionId());

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
    private RegisterBirthMotherInfo createMotherInfor(AbandonedRequest requestBirthReq) {
        RegisterBirthMotherInfo registerBirthMotherInfo = new RegisterBirthMotherInfo();
        registerBirthMotherInfo.setFirstNameEn(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getFirstNameEn());
        registerBirthMotherInfo.setFirstNameMl(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getFirstNameMl());

//        registerBirthMotherInfo.setMiddleNameEn(requestBirthReq.getBirthDetails().get(0).getBirthMotherInfo().getMiddleNameEn());
//        registerBirthMotherInfo.setMiddleNameMl(requestBirthReq.getBirthDetails().get(0).getBirthMotherInfo().getMiddleNameMl());
//        registerBirthMotherInfo.setLastNameEn(requestBirthReq.getBirthDetails().get(0).getBirthMotherInfo().getLastNameEn());
//        registerBirthMotherInfo.setLastNameMl(requestBirthReq.getBirthDetails().get(0).getBirthMotherInfo().getLastNameMl());
        registerBirthMotherInfo.setAadharNo(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getMotherAadhar());
        registerBirthMotherInfo.setOtPassportNo(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getMotherPassport());
//        registerBirthMotherInfo.setEmailId(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getEmailId());
//        registerBirthMotherInfo.setMobileNo(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getMobileNo());
        return registerBirthMotherInfo;
    }

    private RegisterBirthFatherInfo createFatherInfor(AbandonedRequest requestBirthReq) {
        RegisterBirthFatherInfo registerBirthFatherInfo = new RegisterBirthFatherInfo();
        registerBirthFatherInfo.setFirstNameEn(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getFatherFirstNameEn());
        registerBirthFatherInfo.setFirstNameMl(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getFatherFirstNameMl());
//        registerBirthFatherInfo.setMiddleNameEn(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getMiddleNameEn());
//        registerBirthFatherInfo.setMiddleNameMl(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getMiddleNameMl());
//        registerBirthFatherInfo.setLastNameEn(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getLastNameEn());
//        registerBirthFatherInfo.setLastNameMl(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getLastNameMl());
        registerBirthFatherInfo.setAadharNo(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getFatherAadharno());
//        registerBirthFatherInfo.setOtPassportNo(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getPassportNo());
        registerBirthFatherInfo.setEmailId(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getFamilyEmailid());
        registerBirthFatherInfo.setMobileNo(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getFamilyMobileNo());
        return registerBirthFatherInfo;
    }
    private RegisterBirthStatiticalInformation createRegisterBirthStatiticalInformation(AbandonedRequest requestBirthReq, RegisterBirthDetail registerBirthDetail) {
        RegisterBirthStatiticalInformation registerBirthStatiticalInformation = new RegisterBirthStatiticalInformation();
        registerBirthStatiticalInformation.setDeliveryMethod(requestBirthReq.getBirthDetails().get(0).getDeliveryMethods());
        registerBirthStatiticalInformation.setWeightOfChild((long) requestBirthReq.getBirthDetails().get(0).getBirthWeight());
        registerBirthStatiticalInformation.setDurationOfPregnancyInWeek(requestBirthReq.getBirthDetails().get(0).getPregnancyDuration());
        registerBirthStatiticalInformation.setNatureOfMedicalAttention(requestBirthReq.getBirthDetails().get(0).getMedicalAttensionSub());
        registerBirthStatiticalInformation.setDeliveryMethod(requestBirthReq.getBirthDetails().get(0).getDeliveryMethods());
        registerBirthStatiticalInformation.setReligionId(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getReligionId());
        registerBirthStatiticalInformation.setFatherEducationId(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getFatherEucationid());
        registerBirthStatiticalInformation.setFatherProffessionId(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getFatherProffessionid());

        registerBirthStatiticalInformation.setMotherEducationId(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getMotherEducationid());
        registerBirthStatiticalInformation.setMotherProffessionId(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getMotherProffessionid());
        registerBirthStatiticalInformation.setMotherNationalityId(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getMotherNationalityid());
        registerBirthStatiticalInformation.setMotherAgeMarriage(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getMotherAgeMarriage());
        registerBirthStatiticalInformation.setMotherAgeDelivery(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getMotherAgeDelivery());

        // registerBirthStatiticalInformation.setMotherMaritalStatusId(requestBirthReq.getBirthDetails().get(0).getParentsDetails().getM;
//        registerBirthStatiticalInformation.setMotherUnmarried(requestBirthReq.getBirthDetails().get(0).getMotherUnmarried());


//        registerBirthStatiticalInformation.setMotherResLbId(registerBirthDetail.getTenantId());
//        registerBirthStatiticalInformation.setMotherResLbCode(registerBirthDetail.getTenantId());
//        registerBirthStatiticalInformation.setMotherResPlaceTypeId(requestBirthReq.getBirthDetails().get(0).getMotherResPlaceTypeId());
//        registerBirthStatiticalInformation.setMotherResLbTypeId(requestBirthReq.getBirthDetails().get(0).getMotherResLbTypeId());
//        registerBirthStatiticalInformation.setMotherResDistrictId(requestBirthReq.getBirthDetails().get(0).getMotherResDistrictId());
//        registerBirthStatiticalInformation.setMotherResStateId(requestBirthReq.getBirthDetails().get(0).getMotherResStateId());
//        registerBirthStatiticalInformation.setMotherResCountryId(requestBirthReq.getBirthDetails().get(0).getMotherResCountryId());
//        registerBirthStatiticalInformation.setMotherResdnceAddrType(registerBirthDetail.getTenantLbType());
//        registerBirthStatiticalInformation.setMotherResdnceTenentId(registerBirthDetail.getTenantId());
//        registerBirthStatiticalInformation.setMotherResdncePlaceType(requestBirthReq.getBirthDetails().get(0).getParentAddress().getPresentOutSideIndiaadrsCityTown());
////        registerBirthStatiticalInformation.setMotherResdncePlaceEn(requestBirthReq.getBirthDetails().get(0).getParentAddress().getPresentInside());
////        registerBirthStatiticalInformation.setMotherResdncePlaceMl(requestBirthReq.getBirthDetails().get(0).getMotherResdncePlaceMl());
////        registerBirthStatiticalInformation.setMotherResdnceLbType(requestBirthReq.getBirthDetails().get(0).getMotherResdnceLbType());
//        registerBirthStatiticalInformation.setMotherResdnceDistrictId(requestBirthReq.getBirthDetails().get(0).getParentAddress().getDistrictIdPresent());
//        registerBirthStatiticalInformation.setMotherResdnceStateId(requestBirthReq.getBirthDetails().get(0).getParentAddress().getStateIdPresent());
//        registerBirthStatiticalInformation.setMotherResdnceCountryId(requestBirthReq.getBirthDetails().get(0).getParentAddress().getCountryIdPresent());
        return registerBirthStatiticalInformation;

    }
}


