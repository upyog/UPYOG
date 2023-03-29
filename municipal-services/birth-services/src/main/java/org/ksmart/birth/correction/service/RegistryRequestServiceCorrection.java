package org.ksmart.birth.correction.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.correction.repository.CorrectionBirthRepository;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestServiceCorrection {
    private final CorrectionBirthRepository repository;
    @Autowired
    RegistryRequestServiceCorrection(CorrectionBirthRepository repository) {
        this.repository = repository;
    }


    public RegisterBirthDetailsRequest createRegistryRequestNew(CorrectionRequest request) {///Work to get req to Register
        return repository.searchBirthDetailsForRegister(request);
    }
//        List<Object> preparedStmtValues = new ArrayList<>();
//        RegisterBirthDetailsRequest request = new RegisterBirthDetailsRequest();
//        SearchCriteria criteria = new SearchCriteria();
//        if(requestApplication.getNewBirthDetails().size() > 0) {
//            criteria.setApplicationNumber(requestApplication.getNewBirthDetails().get(0).getApplicationNo());
//            criteria.setTenantId(requestApplication.getNewBirthDetails().get(0).getTenantId());
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






    public RegisterBirthDetailsRequest createRegistryRequest(CorrectionRequest requestKsmartBirthReq) {
        RegisterBirthDetailsRequest request = new RegisterBirthDetailsRequest();
        List<RegisterBirthDetail> registerBirthDetails = new LinkedList<>();
        RegisterBirthDetail registerBirthDetail = new RegisterBirthDetail();
        registerBirthDetail.setDateOfReport(requestKsmartBirthReq.getCorrectionDetails().get(0).getDateOfReport());
        registerBirthDetail.setApplicationId(requestKsmartBirthReq.getCorrectionDetails().get(0).getId());
        registerBirthDetail.setDateOfBirth(requestKsmartBirthReq.getCorrectionDetails().get(0).getDateOfBirth());
        registerBirthDetail.setDateOfReport(requestKsmartBirthReq.getCorrectionDetails().get(0).getDateOfReport());
        registerBirthDetail.setDateOfBirth(requestKsmartBirthReq.getCorrectionDetails().get(0).getDateOfBirth());
        registerBirthDetail.setFirstNameEn(requestKsmartBirthReq.getCorrectionDetails().get(0).getFirstNameEn());
        registerBirthDetail.setFirstNameMl(requestKsmartBirthReq.getCorrectionDetails().get(0).getFirstNameMl());
        registerBirthDetail.setMiddleNameEn(requestKsmartBirthReq.getCorrectionDetails().get(0).getMiddleNameEn());
        registerBirthDetail.setMiddleNameMl(requestKsmartBirthReq.getCorrectionDetails().get(0).getMiddleNameMl());
        registerBirthDetail.setLastNameEn(requestKsmartBirthReq.getCorrectionDetails().get(0).getLastNameEn());
        registerBirthDetail.setLastNameMl(requestKsmartBirthReq.getCorrectionDetails().get(0).getLastNameMl());
        registerBirthDetail.setTenantId(requestKsmartBirthReq.getCorrectionDetails().get(0).getTenantId());
        registerBirthDetail.setGender(requestKsmartBirthReq.getCorrectionDetails().get(0).getGender());
        registerBirthDetail.setAadharNo(requestKsmartBirthReq.getCorrectionDetails().get(0).getAadharNo());
        registerBirthDetail.setEsignUserCode(requestKsmartBirthReq.getCorrectionDetails().get(0).getEsignUserCode());
        registerBirthDetail.setEsignUserDesigCode(requestKsmartBirthReq.getCorrectionDetails().get(0).getEsignUserDesigCode());;
//        registerBirthDetail.setNoOfAliveBirth(requestKsmartBirthReq.getCorrectionDetails().get(0).getNoOfAliveBirth());
//        registerBirthDetail.setMultipleBirthDetId(requestKsmartBirthReq.getCorrectionDetails().get(0).getMultipleBirthDetailsIid());
//        registerBirthDetail.setIsBornOutside(requestKsmartBirthReq.getCorrectionDetails().get(0).getIsBornOutside());
//        registerBirthDetail.setOtPassportNo(requestKsmartBirthReq.getCorrectionDetails().get(0).getPassportNo());
        registerBirthDetail.setRegistrationNo(requestKsmartBirthReq.getCorrectionDetails().get(0).getRegistrationNo());
        registerBirthDetail.setApplicationType(requestKsmartBirthReq.getCorrectionDetails().get(0).getApplicationType());
        registerBirthDetail.setRegistrationStatus("ACTIVE");
        registerBirthDetail.setRegistrationDate(requestKsmartBirthReq.getCorrectionDetails().get(0).getRegistrationDate());
        registerBirthDetail.setRegisterBirthPlace(createBirthPlace(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthFather(createFatherInfor(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthMother(createMotherInfor(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthPermanent(createRegisterBirthPermanentAddress(requestKsmartBirthReq));
        registerBirthDetail.setRegisterBirthPresent(createRegisterBirthPresentAddress(requestKsmartBirthReq));
        registerBirthDetails.add(registerBirthDetail);
        request.setRequestInfo(requestKsmartBirthReq.getRequestInfo());
        request.setRegisterBirthDetails(registerBirthDetails);
        return request;
    }

    private RegisterBirthPlace createBirthPlace(CorrectionRequest requestBirthReq) {
        RegisterBirthPlace registerBirthPlace = new RegisterBirthPlace();
        registerBirthPlace.setPlaceOfBirthId(requestBirthReq.getCorrectionDetails().get(0).getPlaceofBirthId());
        registerBirthPlace.setHospitalId(requestBirthReq.getCorrectionDetails().get(0).getHospitalId());
        registerBirthPlace.setVehicleTypeId(requestBirthReq.getCorrectionDetails().get(0).getVehicleTypeid());
        registerBirthPlace.setVehicleRegistrationNo(requestBirthReq.getCorrectionDetails().get(0).getVehicleRegistrationNo());
        registerBirthPlace.setVehicleFromEn(requestBirthReq.getCorrectionDetails().get(0).getVehicleFromEn());
        registerBirthPlace.setVehicleToEn(requestBirthReq.getCorrectionDetails().get(0).getVehicleToEn());
        registerBirthPlace.setVehicleFromMl(requestBirthReq.getCorrectionDetails().get(0).getVehicleFromMl());
        registerBirthPlace.setVehicleToMl(requestBirthReq.getCorrectionDetails().get(0).getVehicleToMl());
//        registerBirthPlace.setVehicleOtherEn(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getVehicleOtherEn());
//        registerBirthPlace.setVehicleOtherMl(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getVehicleOtherMl());
        registerBirthPlace.setVehicleAdmitHospitalEn(requestBirthReq.getCorrectionDetails().get(0).getSetadmittedHospitalEn());
//        registerBirthPlace.setVehicleAdmitHospitalMl(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getVehicleAdmitHospitalMl());
        registerBirthPlace.setPublicPlaceId(requestBirthReq.getCorrectionDetails().get(0).getPublicPlaceType());
//        registerBirthPlace.setHoHouseHolderEn(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getHoHouseHolderEn());
//        registerBirthPlace.setHoHouseHolderMl(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getHoHouseHolderMl());
//        registerBirthPlace.setHoBuildingNo(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getHoBuildingNo());
//        registerBirthPlace.setHoResAssoNo(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getHoResAssoNo());
//        registerBirthPlace.setHoHousenNo(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getHoHousenNo());
        registerBirthPlace.setHouseNameEn(requestBirthReq.getCorrectionDetails().get(0).getAdrsHouseNameEn());
        registerBirthPlace.setHouseNameMl(requestBirthReq.getCorrectionDetails().get(0).getAdrsHouseNameMl());

//        registerBirthPlace.setHoVillageId(requestBirthReq.getCorrectionDetails().get(0).getPresentInsideKeralaVillage());
//        registerBirthPlace.setHoTalukId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPresentInsideKeralaTaluk());
//        registerBirthPlace.setHoDistrictId(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getHoDistrictId());
//        registerBirthPlace.setHoStateId(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getHoStateId());
        registerBirthPlace.setHoPoId(requestBirthReq.getCorrectionDetails().get(0).getAdrsPostOffice());
        registerBirthPlace.setHoPinNo(requestBirthReq.getCorrectionDetails().get(0).getAdrsPincode());
//        registerBirthPlace.setHoCountryId(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getHoCountryId());
        registerBirthPlace.setWardId(requestBirthReq.getCorrectionDetails().get(0).getWardId());
//        registerBirthPlace.setOthDetailsEn(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getOthDetailsEn());
//        registerBirthPlace.setOthDetailsMl(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getOthDetailsMl());
        registerBirthPlace.setInstitutionTypeId(requestBirthReq.getCorrectionDetails().get(0).getInstitutionTypeId());
        registerBirthPlace.setInstitutionId(requestBirthReq.getCorrectionDetails().get(0).getInstitutionId());

//        registerBirthPlace.setAuthOfficerId(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getAuthOfficerId());
//        registerBirthPlace.setAuthOfficerDesigId(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getAuthOfficerDesigId());
//        registerBirthPlace.setOthAuthOfficerName(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getOthAuthOfficerName());
//        registerBirthPlace.setOthAuthOfficerDesig(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getOthAuthOfficerDesig());
//        registerBirthPlace.setInformantsNameEn(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getInformantsNameEn());
//        registerBirthPlace.setInformantsNameMl(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getInformantsNameMl());
//        registerBirthPlace.setInformantsAddressEn(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getInformantsAddressEn());
//        registerBirthPlace.setInformantsAddressMl(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getInformantsAddressMl());


//        registerBirthPlace.setInformantsMobileNo(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getInformantsMobileNo());
//        registerBirthPlace.setInformantsAadhaarNo(requestBirthReq.getCorrectionDetails().get(0).getBirthPlace().getInformantsAadhaarNo());
        return registerBirthPlace;
    }

    //  /*
    private RegisterBirthMotherInfo createMotherInfor(CorrectionRequest requestBirthReq) {
        RegisterBirthMotherInfo registerBirthMotherInfo = new RegisterBirthMotherInfo();

//        registerBirthMotherInfo.setMiddleNameEn(requestBirthReq.getCorrectionDetails().get(0).getBirthMotherInfo().getMiddleNameEn());
//        registerBirthMotherInfo.setMiddleNameMl(requestBirthReq.getCorrectionDetails().get(0).getBirthMotherInfo().getMiddleNameMl());
//        registerBirthMotherInfo.setLastNameEn(requestBirthReq.getCorrectionDetails().get(0).getBirthMotherInfo().getLastNameEn());
//        registerBirthMotherInfo.setLastNameMl(requestBirthReq.getCorrectionDetails().get(0).getBirthMotherInfo().getLastNameMl());
        registerBirthMotherInfo.setAadharNo(requestBirthReq.getCorrectionDetails().get(0).getMotherAadhar());
//        registerBirthMotherInfo.setEmailId(requestBirthReq.getCorrectionDetails().get(0).getParentsDetails().getEmailId());
//        registerBirthMotherInfo.setMobileNo(requestBirthReq.getCorrectionDetails().get(0).getParentsDetails().getMobileNo());
        return registerBirthMotherInfo;
    }

    private RegisterBirthFatherInfo createFatherInfor(CorrectionRequest requestBirthReq) {
        RegisterBirthFatherInfo registerBirthFatherInfo = new RegisterBirthFatherInfo();
        registerBirthFatherInfo.setFirstNameEn(requestBirthReq.getCorrectionDetails().get(0).getFatherFirstNameEn());
        registerBirthFatherInfo.setFirstNameMl(requestBirthReq.getCorrectionDetails().get(0).getFatherFirstNameMl());
//        registerBirthFatherInfo.setMiddleNameEn(requestBirthReq.getCorrectionDetails().get(0).getParentsDetails().getMiddleNameEn());
//        registerBirthFatherInfo.setMiddleNameMl(requestBirthReq.getCorrectionDetails().get(0).getParentsDetails().getMiddleNameMl());
//        registerBirthFatherInfo.setLastNameEn(requestBirthReq.getCorrectionDetails().get(0).getParentsDetails().getLastNameEn());
//        registerBirthFatherInfo.setLastNameMl(requestBirthReq.getCorrectionDetails().get(0).getParentsDetails().getLastNameMl());
        registerBirthFatherInfo.setAadharNo(requestBirthReq.getCorrectionDetails().get(0).getFatherAadharno());
//        registerBirthFatherInfo.setOtPassportNo(requestBirthReq.getCorrectionDetails().get(0).getParentsDetails().getPassportNo());
        return registerBirthFatherInfo;
    }

    private RegisterBirthPermanentAddress createRegisterBirthPermanentAddress(CorrectionRequest requestBirthReq) {
        RegisterBirthPermanentAddress registerBirthPermanentAddress = new RegisterBirthPermanentAddress();
//        registerBirthPermanentAddress.setResAssoNo(requestBirthReq.getCorrectionDetails().get(0).getBirthPermanentAddress().getResAssoNo());
        registerBirthPermanentAddress.setHouseNameEn(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getHouseNameNoEnPermanent());

        registerBirthPermanentAddress.setHouseNameMl(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getHouseNameNoMlPermanent());

//        registerBirthPermanentAddress.setOtAddress1En(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getOtAddress1En());
//        registerBirthPermanentAddress.setOtAddress1Ml(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getOtAddress1Ml());
//        registerBirthPermanentAddress.setOtAddress2En(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getOtAddress2En());
//        registerBirthPermanentAddress.setOtAddress2Ml(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getOtAddress2Ml());
        registerBirthPermanentAddress.setVillageId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPermntInKeralaAdrVillage());

        registerBirthPermanentAddress.setTenantId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPermntInKeralaAdrLBName());

        registerBirthPermanentAddress.setTalukId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPermntInKeralaAdrTaluk());

        registerBirthPermanentAddress.setDistrictId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getDistrictIdPermanent());

        registerBirthPermanentAddress.setStateId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getStateIdPermanent());
        registerBirthPermanentAddress.setPoId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPoNoPermanent());

        registerBirthPermanentAddress.setPinNo(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPinNoPermanent());

        registerBirthPermanentAddress.setOtStateRegionProvinceEn(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPermntOutsideIndiaprovinceEn());

//        registerBirthPermanentAddress.setOtStateRegionProvinceMl(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getOtStateRegionProvinceMl());
        registerBirthPermanentAddress.setCountryId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getCountryIdPermanent());

//        registerBirthPermanentAddress.setResAssoNoMl(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getResAssoNoMl());
        return registerBirthPermanentAddress;
    }

    private RegisterBirthPresentAddress createRegisterBirthPresentAddress(CorrectionRequest requestBirthReq) {
        RegisterBirthPresentAddress registerBirthPresentAddress = new RegisterBirthPresentAddress();
//        registerBirthPresentAddress.setResAssoNo(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getResAssoNo());
        registerBirthPresentAddress.setHouseNameEn(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getHouseNameNoEnPresent());

        registerBirthPresentAddress.setHouseNameMl(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getHouseNameNoMlPresent());

//        registerBirthPresentAddress.setOtAddress1En(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getOtAddress1En());
//        registerBirthPresentAddress.setOtAddress1Ml(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getOtAddress1Ml());
//        registerBirthPresentAddress.setOtAddress2En(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getOtAddress2En());
//        registerBirthPresentAddress.setOtAddress2Ml(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getOtAddress2Ml());
        registerBirthPresentAddress.setVillageId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getVillageNamePresent());

        registerBirthPresentAddress.setTenantId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPresentInsideKeralaLBName());

        registerBirthPresentAddress.setTalukId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPresentInsideKeralaTaluk());

        registerBirthPresentAddress.setDistrictId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getDistrictIdPresent());

        registerBirthPresentAddress.setStateId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getStateIdPresent());

        registerBirthPresentAddress.setPoId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPoNoPresent());

        registerBirthPresentAddress.setPinNo(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPinNoPresent());

        registerBirthPresentAddress.setOtStateRegionProvinceEn(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPresentOutSideIndiaProvinceEn());

        registerBirthPresentAddress.setOtStateRegionProvinceMl(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getPresentOutSideIndiaProvinceMl());

        registerBirthPresentAddress.setCountryId(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getCountryIdPresent());

//        registerBirthPresentAddress.setResAssoNoMl(requestBirthReq.getCorrectionDetails().get(0).getParentAddress().getResAssoNoMl());
        return registerBirthPresentAddress;
    }
}


