package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birth.certmodel.BirthCertRequest;
import org.ksmart.birth.birth.certmodel.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.repository.RegisterBirthRepository;
import org.ksmart.birth.common.contract.BirthPdfApplicationRequest;
import org.ksmart.birth.common.contract.EgovPdfResp;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
@Slf4j
@Service
public class RegisterBirthService {
    @Autowired
    RegisterBirthRepository repository;


    public List<RegisterBirthDetail> saveRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        return repository.saveRegisterBirthDetails(request);
    }

    public List<RegisterBirthDetail> updateRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        return repository.updateRegisterBirthDetails(request);
    }

    public List<RegisterBirthDetail> searchRegisterBirthDetails(RegisterBirthSearchCriteria criteria) {
        return repository.searchRegisterBirthDetails(criteria);
    }
    public void download(RegisterBirthSearchCriteria criteria, RequestInfo requestInfo) {
//        try {
//            BirthCertificate birthCertificate = new BirthCertificate();
//            //birthCertificate.setSource(criteria.getSource().toString());
//            birthCertificate.setBirthDtlId(criteria.getId());
//            birthCertificate.setTenantId(criteria.getTenantId());
//            BirthCertRequest birthCertRequest = BirthCertRequest.builder().birthCertificate(birthCertificate).requestInfo(requestInfo).build();
//            List<RegisterBirthDetail> birtDtls = repository.searchRegisterBirthDetails(criteria);
//            birthCertificate.setBirthPlace(birtDtls.get(0).getPlaceofbirth());
//            birthCertificate.setGender(birtDtls.get(0).getGenderStr());
//            birthCertificate.setWard(birtDtls.get(0).getBirthPermaddr().getTehsil());
//            birthCertificate.setState(birtDtls.get(0).getBirthPermaddr().getState());
//            birthCertificate.setDistrict(birtDtls.get(0).getBirthPermaddr().getDistrict());
//            birthCertificate.setDateofbirth(birtDtls.get(0).getDateOfBirth());
//            birthCertificate.setDateofreport(birtDtls.get(0).getDateOfReport());
//            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//            String date = format.format(birtDtls.get(0).getDateOfReport());
//            String datestr = date.split("-")[2];
//            birthCertificate.setYear(datestr);
//            if (birtDtls.size() > 1)
//                throw new CustomException("Invalid_Input", "Error in processing data");
//            enrichmentService.enrichCreateRequest(birthCertRequest);
//            enrichmentService.setIdgenIds(birthCertRequest);
//            birtDtls.get(0).setBirthcertificateno(birthCertRequest.getBirthCertificate().getBirthCertificateNo());
//            BirthPdfApplicationRequest applicationRequest = BirthPdfApplicationRequest.builder().requestInfo(requestInfo).birthCertificate(birtDtls).build();
//            EgovPdfResp pdfResp = repository.saveBirthCertPdf(applicationRequest);
//            birthCertificate.setEmbeddedUrl(applicationRequest.getBirthCertificate().get(0).getEmbeddedUrl());
//            birthCertificate.setDateofissue(applicationRequest.getBirthCertificate().get(0).getDateofissue());
//            birthCertificate.setFilestoreid(pdfResp.getFilestoreIds().get(0));
//            birthCertificate.setApplicationStatus(BirthCertificate.StatusEnum.FREE_DOWNLOAD);
//            repository.saveRegisterBirthCert(birthCertRequest);
//            return birthCertificate;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new CustomException("DOWNLOAD_ERROR", "Error in Downloading Certificate");
//        }
    }


}
