package org.ksmart.birth.birthregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.enrichment.RegisterBirthEnrichment;
import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.birthregistry.repository.RegisterBirthRepository;

import org.ksmart.birth.birthregistry.validation.RegistryRequestValidator;
import org.ksmart.birth.common.contract.EgovPdfResp;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RegisterBirthService {
    private final RegisterBirthRepository repository;
    private final RegisterBirthEnrichment enrichment;

    private final EnrichmentService certEnrichment;
    private final BirthCertService birthCertService;
    private final MdmsUtil mdmsUtil;

    private final RegistryRequestValidator validator;
    private final MdmsDataService mdmsDataService;

    @Autowired
    RegisterBirthService(RegisterBirthRepository repository, RegisterBirthEnrichment enrichment,RegistryRequestValidator validator,
                         MdmsUtil mdmsUtil, MdmsDataService mdmsDataService, BirthCertService birthCertService, EnrichmentService certEnrichment) {
        this.repository = repository;
        this.enrichment = enrichment;
        this.validator = validator;
        this.mdmsUtil = mdmsUtil;
        this.mdmsDataService = mdmsDataService;
        this.birthCertService = birthCertService;
        this.certEnrichment = certEnrichment;
    }

    public List<RegisterBirthDetail> saveRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        RegisterBirthSearchCriteria criteria = new RegisterBirthSearchCriteria();
        criteria.setApplicationNumber(request.getRegisterBirthDetails().get(0).getAckNumber());
        criteria.setTenantId(request.getRegisterBirthDetails().get(0).getTenantId());
        List<RegisterBirthDetail> registerBirthDetails = searchRegisterBirthDetails(criteria);

        validator.validateCreate(request, registerBirthDetails);
        return repository.saveRegisterBirthDetails(request);
    }

    public List<RegisterBirthDetail> updateRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        return repository.updateRegisterBirthDetails(request);
    }

    public List<RegisterBirthDetail> searchRegisterBirthDetails(RegisterBirthSearchCriteria criteria)  {
        List<RegisterBirthDetail>  registerBirthDetails = repository.searchRegisterBirthDetails(criteria);
        return registerBirthDetails;
    }

    public List<RegisterCertificateData> searchRegisterForCert(RegisterBirthSearchCriteria criteria, RequestInfo requestInfo) {
        List<RegisterBirthDetail> registerDetails = repository.searchRegisterBirthDetails(criteria);
        List<RegisterCertificateData> registerCertDetails = new ArrayList<>();
        registerDetails
                .forEach(register -> {
                    RegisterCertificateData registerForCertificate = birthCertService.setCertificateDetails(register, requestInfo);
                    registerCertDetails.add(registerForCertificate);
                });
        return registerCertDetails;
    }
    public BirthCertificate download(RegisterBirthSearchCriteria criteria, RequestInfo requestInfo) {
//        try {
            BirthCertificate birthCertificate = new BirthCertificate();
            BirthCertRequest birthCertRequest = BirthCertRequest.builder().birthCertificate(birthCertificate).requestInfo(requestInfo).build();
            List<RegisterCertificateData> regDetail = searchRegisterForCert(criteria, requestInfo);
            if(regDetail.size() == 1) {
                birthCertificate.setBirthPlace(regDetail.get(0).getPlaceDetails());
                birthCertificate.setRegistrationId(regDetail.get(0).getId());
                birthCertificate.setApplicationId(regDetail.get(0).getApplicationId());
                birthCertificate.setApplicationNumber(regDetail.get(0).getAckNo());
                birthCertificate.setGender(regDetail.get(0).getGenderEn().toString());
                birthCertificate.setWard(regDetail.get(0).getWardCode());
                birthCertificate.setState(regDetail.get(0).getTenantState());
                birthCertificate.setDistrict(regDetail.get(0).getTenantDistrict());
                birthCertificate.setDateofbirth(new Timestamp(regDetail.get(0).getDateOfBirth()) );
                birthCertificate.setDateofreport(new Timestamp(regDetail.get(0).getDateOfReport()));
                birthCertificate.setTenantId(regDetail.get(0).getTenantId());
                birthCertificate.setApplicationType(regDetail.get(0).getApplicationType());
                birthCertificate.setRegistrtionNo(regDetail.get(0).getRegistrationNo());
                enrichment.setCertificateNumber(birthCertificate,requestInfo);
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                String date = format.format(regDetail.get(0).getDateOfReport());
                String datestr = date.split("-")[2];
                birthCertificate.setYear(datestr);
                if (regDetail.size() > 1)
                    throw new CustomException("Invalid_Input", "Error in processing data");
                certEnrichment.enrichCreateRequest(birthCertRequest);
                regDetail.get(0).setCertId(birthCertRequest.getBirthCertificate().getBirthCertificateNo());
                BirthPdfRegisterRequest pdfRegisterRequest = BirthPdfRegisterRequest.builder().requestInfo(requestInfo).birthCertificate(regDetail).build();
                EgovPdfResp pdfResp = repository.saveBirthCertPdf(pdfRegisterRequest);
                birthCertificate.setEmbeddedUrl(pdfRegisterRequest.getBirthCertificate().get(0).getEmbeddedUrl());
                birthCertificate.setDateofissue(pdfRegisterRequest.getBirthCertificate().get(0).getRegistrationDate());
                birthCertificate.setFilestoreid(pdfResp.getFilestoreIds().get(0));
                birthCertificate.setApplicationStatus(BirthCertificate.StatusEnum.FREE_DOWNLOAD);
                repository.saveRegisterBirthCert(birthCertRequest);
            } else{
                throw new CustomException(ErrorCodes.INVALID_INPUT.getCode(), "Error in processing data");
            }
            return birthCertificate;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new CustomException("DOWNLOAD_ERROR", "Error in Downloading Certificate");
//        }
    }


}
