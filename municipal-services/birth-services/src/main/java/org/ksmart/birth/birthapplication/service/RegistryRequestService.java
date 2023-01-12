package org.ksmart.birth.birthapplication.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.birthcorrection.model.BirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestService {
    private List<RegisterBirthDetailsRequest> createRegistryRequest(BirthDetailsRequest requestBirthReq){
        List<RegisterBirthDetailsRequest> request = new LinkedList<>();
        List<RegisterBirthDetail> registerBirthDetail =  new LinkedList<>();
        registerBirthDetail.get(0).setDateOfReport(requestBirthReq.getBirthDetails().get(0).getDateOfReport());
        registerBirthDetail.get(0).setDateOfBirth(requestBirthReq.getBirthDetails().get(0).getDateOfBirth());




        registerBirthDetail.get(0).setFirstNameEn(requestBirthReq.getBirthDetails().get(0).getFirstNameEn());







        request.get(0).setRequestInfo(requestBirthReq.getRequestInfo());
        request.get(0).setRegisterBirthDetails(registerBirthDetail);
        return  request;
    }
}

