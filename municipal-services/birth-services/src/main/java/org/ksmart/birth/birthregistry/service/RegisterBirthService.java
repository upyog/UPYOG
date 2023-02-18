package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.birthregistry.repository.RegisterBirthRepository;

import org.ksmart.birth.common.contract.EgovPdfResp;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.utils.MdmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
@Slf4j
@Service
public class RegisterBirthService {
    private final RegisterBirthRepository repository;
    private final EnrichmentService enrichmentService;

    private final MdmsUtil mdmsUtil;

    private final MdmsDataService mdmsDataService;

    @Autowired
    RegisterBirthService(RegisterBirthRepository repository, EnrichmentService enrichmentService, MdmsUtil mdmsUtil, MdmsDataService mdmsDataService) {
        this.repository = repository;
        this.enrichmentService = enrichmentService;
        this.mdmsUtil = mdmsUtil;
        this.mdmsDataService = mdmsDataService;
    }

    public List<RegisterBirthDetail> saveRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        return repository.saveRegisterBirthDetails(request);
    }

    public List<RegisterBirthDetail> updateRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        return repository.updateRegisterBirthDetails(request);
    }

    public List<RegisterBirthDetail> searchRegisterBirthDetails(RegisterBirthSearchCriteria criteria, RequestInfo requestInfo)  {

        List<RegisterBirthDetail>  registerBirthDetails = repository.searchRegisterBirthDetails(criteria);
//        mdmsDataService.setTenantDetails(registerBirthDetails, requestInfo);
//        mdmsDataService.setLocationDetails(registerBirthDetails, requestInfo);
        return registerBirthDetails;
    }

    public List<RegisterCertificateData> searchRegisterForCert(RegisterBirthSearchCriteria criteria, RequestInfo requestInfo)  {

        List<RegisterCertificateData>  registerBirthDetails = repository.searchRegisterCert(criteria);
        mdmsDataService.setTenantDetails(registerBirthDetails, requestInfo);
        mdmsDataService.setLocationDetails(registerBirthDetails, requestInfo);
        return registerBirthDetails;
    }
    public BirthCertificate download(RegisterBirthSearchCriteria criteria, RequestInfo requestInfo) {
        try {
            BirthCertificate birthCertificate = new BirthCertificate();
//            birthCertificate.setSource(criteria.getSource().toString());
            birthCertificate.setBirthDtlId(criteria.getId());
            birthCertificate.setTenantId(criteria.getTenantId());
            BirthCertRequest birthCertRequest = BirthCertRequest.builder().birthCertificate(birthCertificate).requestInfo(requestInfo).build();
            List<RegisterCertificateData> regDetail = searchRegisterForCert(criteria, requestInfo);
            birthCertificate.setBirthPlace(regDetail.get(0).getPlaceDetails());
            birthCertificate.setGender(regDetail.get(0).getGender().toString());
            birthCertificate.setWard(regDetail.get(0).getWardId());
            birthCertificate.setState(regDetail.get(0).getTenantState());
            birthCertificate.setDistrict(regDetail.get(0).getTenantDistrict());
            birthCertificate.setBirthCertificateNo(regDetail.get(0).getRegistrationNo().toString());
            birthCertificate.setDateofbirth(new Timestamp(regDetail.get(0).getDateOfBirth()) );
            birthCertificate.setDateofreport(new Timestamp(regDetail.get(0).getDateOfReport()));
            birthCertificate.setTenantId(regDetail.get(0).getTenantId());
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String date = format.format(regDetail.get(0).getDateOfReport());
            String datestr = date.split("-")[2];
            birthCertificate.setYear(datestr);
            if (regDetail.size() > 1)
                throw new CustomException("Invalid_Input", "Error in processing data");
            enrichmentService.enrichCreateRequest(birthCertRequest);
            regDetail.get(0).setCertId(birthCertRequest.getBirthCertificate().getBirthCertificateNo());
            BirthPdfRegisterRequest pdfRegisterRequest = BirthPdfRegisterRequest.builder().requestInfo(requestInfo).birthCertificate(regDetail).build();
            EgovPdfResp pdfResp = repository.saveBirthCertPdf(pdfRegisterRequest);
            birthCertificate.setEmbeddedUrl(pdfRegisterRequest.getBirthCertificate().get(0).getEmbeddedUrl());
            birthCertificate.setDateofissue(pdfRegisterRequest.getBirthCertificate().get(0).getRegistrationDate());
            birthCertificate.setFilestoreid(pdfResp.getFilestoreIds().get(0));
            birthCertificate.setApplicationStatus(BirthCertificate.StatusEnum.FREE_DOWNLOAD);
            repository.saveRegisterBirthCert(birthCertRequest);
            return birthCertificate;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("DOWNLOAD_ERROR", "Error in Downloading Certificate");
        }
    }


}
