package org.ksmart.birth.birthapplication.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
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




        registerBirthDetail.get(0).setFirstNameEn(requestBirthReq.getBirthDetails().get(0).getFirstNameEn());







        request.setRequestInfo(requestBirthReq.getRequestInfo());
        request.setRegisterBirthDetails(registerBirthDetail);
        return  request;
    }
}

