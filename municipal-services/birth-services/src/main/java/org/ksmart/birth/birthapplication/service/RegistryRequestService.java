package org.ksmart.birth.birthapplication.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.birthapplication.model.birth.BirthPlace;
import org.ksmart.birth.birthregistry.model.BirthPdfRegisterRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthPlace;
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

        return registerBirthPlace;
    }
}

