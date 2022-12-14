package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.BirthCertRequest;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.service.EnrichmentService;
import org.ksmart.birth.birthregistry.model.BirthPdfRegisterRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.repository.RegisterBirthRepository;

import org.ksmart.birth.common.contract.EgovPdfResp;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class RegisterBirthService {
    @Autowired
    RegisterBirthRepository repository;

    @Autowired
    EnrichmentService enrichmentService;


    public List<RegisterBirthDetail> saveRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        return repository.saveRegisterBirthDetails(request);
    }

    public List<RegisterBirthDetail> updateRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        return repository.updateRegisterBirthDetails(request);
    }

    public List<RegisterBirthDetail> searchRegisterBirthDetails(RegisterBirthSearchCriteria criteria) {
        return repository.searchRegisterBirthDetails(criteria);
    }
    public BirthCertificate download(RegisterBirthSearchCriteria criteria, RequestInfo requestInfo) {
       // try {
            BirthCertificate birthCertificate = new BirthCertificate();
            //birthCertificate.setSource(criteria.getSource().toString());
            birthCertificate.setBirthDtlId(criteria.getId());
            birthCertificate.setTenantId(criteria.getTenantId());
            BirthCertRequest birthCertRequest = BirthCertRequest.builder().birthCertificate(birthCertificate).requestInfo(requestInfo).build();
            List<RegisterBirthDetail> regDetail = repository.searchRegisterBirthDetails(criteria);
            birthCertificate.setBirthPlace(regDetail.get(0).getRegisterBirthPlace().getHospitalId());
            birthCertificate.setGender(regDetail.get(0).getGender().toString());
            birthCertificate.setWard(regDetail.get(0).getRegisterBirthPlace().getWardId());
            birthCertificate.setState(regDetail.get(0).getRegisterBirthPermanent().getStateId());
            birthCertificate.setDistrict(regDetail.get(0).getRegisterBirthPermanent().getDistrictId());
            birthCertificate.setDateofbirth(new Timestamp(regDetail.get(0).getDateOfBirth()) );
            birthCertificate.setDateofreport(new Timestamp(regDetail.get(0).getRegistrationDate()));


        Date datetest=new Date(1346524199000l);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        String dateText = df2.format(datetest);
        System.out.println("dateText");
        System.out.println(dateText);

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String date = format.format(regDetail.get(0).getDateOfReport());
            String datestr = date.split("-")[2];
            birthCertificate.setYear(datestr);
            if (regDetail.size() > 1)
                throw new CustomException("Invalid_Input", "Error in processing data");
            enrichmentService.enrichCreateRequest(birthCertRequest);
            enrichmentService.setIdgenIds(birthCertRequest);
            //regDetail.get(0).setR(birthCertRequest.getBirthCertificate().getBirthCertificateNo());
        System.out.println("getDateOfReport()");
        System.out.println(date);
        System.out.println(datestr);
            BirthPdfRegisterRequest applicationRequest = BirthPdfRegisterRequest.builder().requestInfo(requestInfo).birthCertificate(regDetail).build();
            EgovPdfResp pdfResp = repository.saveBirthCertPdf(applicationRequest);
            birthCertificate.setEmbeddedUrl(applicationRequest.getBirthCertificate().get(0).getEmbeddedUrl());
            birthCertificate.setDateofissue(applicationRequest.getBirthCertificate().get(0).getDateofissue());
            birthCertificate.setFilestoreid(pdfResp.getFilestoreIds().get(0));
            birthCertificate.setApplicationStatus(BirthCertificate.StatusEnum.FREE_DOWNLOAD);
            repository.saveRegisterBirthCert(birthCertRequest);
            return birthCertificate;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new CustomException("DOWNLOAD_ERROR", "Error in Downloading Certificate");
//        }
    }


}
