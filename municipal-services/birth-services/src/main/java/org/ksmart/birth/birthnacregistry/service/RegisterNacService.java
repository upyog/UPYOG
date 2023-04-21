package org.ksmart.birth.birthnacregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthnacregistry.enrichment.RegisterNacEnrichment;
 
 
import org.ksmart.birth.birthnacregistry.repository.RegisterNacRepository;
import org.ksmart.birth.birthregistry.model.BirthCertRequest;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.BirthPdfRegisterRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthnacregistry.model.NacCertRequest;
import org.ksmart.birth.birthnacregistry.model.NacCertificate;
import org.ksmart.birth.birthnacregistry.model.NacPdfRegisterRequest;
import org.ksmart.birth.birthnacregistry.model.RegisterCertificateData;
import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.ksmart.birth.birthnacregistry.model.RegisterNacRequest;
import org.ksmart.birth.birthnacregistry.model.RegisterNacSearchCriteria;
import org.ksmart.birth.birthnacregistry.service.NacCertService;
import org.ksmart.birth.birthnacregistry.service.EnrichmentServiceNac;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.birthnacregistry.validation.RegistryRequestValidatorNac;
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
public class RegisterNacService {
	
	 private final RegisterNacRepository repository;
	    private final RegisterNacEnrichment enrichment;

	    private final EnrichmentServiceNac certEnrichment;
	    private final NacCertService nacCertService;
	    private final MdmsUtil mdmsUtil;

	    private final RegistryRequestValidatorNac validator;
	    private final MdmsDataService mdmsDataService;
	    @Autowired
	    RegisterNacService(RegisterNacRepository repository, RegisterNacEnrichment enrichment,RegistryRequestValidatorNac validator,
	                         MdmsUtil mdmsUtil, MdmsDataService mdmsDataService, NacCertService nacCertService, EnrichmentServiceNac certEnrichment) {
	        this.repository = repository;
	        this.enrichment = enrichment;
	        this.validator = validator;
	        this.mdmsUtil = mdmsUtil;
	        this.mdmsDataService = mdmsDataService;
	        this.nacCertService = nacCertService;
	        this.certEnrichment = certEnrichment;
	    }

	    public List<RegisterNac> saveRegisterBirthDetails(RegisterNacRequest request) {
	    	RegisterNacSearchCriteria criteria = new RegisterNacSearchCriteria();	
	    	
	    	 if(request.getRegisternacDetails().get(0).getAckNumber() == null) {
	             throw new CustomException(ErrorCodes.REQUIRED.getCode(),
	                     "Application number is required.");

	         }
	    	 
	        criteria.setApplicationNumber(request.getRegisternacDetails().get(0).getAckNumber());
	        criteria.setTenantId(request.getRegisternacDetails().get(0).getTenantid());
	        List<RegisterNac> registerBirthDetails = searchRegisterNacDetails(criteria);

	        validator.validateCreate(request, registerBirthDetails);
	        return repository.saveRegisterBirthDetails(request);
	    }
	    public List<RegisterNac> searchRegisterNacDetails(RegisterNacSearchCriteria criteria)  {
	        List<RegisterNac>  registerBirthDetails = repository.searchRegisterNacDetails(criteria);
	        return registerBirthDetails;
	    }

	    public List<RegisterNac> updateRegisterBirthDetails(RegisterNacRequest request) {
	        return repository.updateRegisterNacDetails(request);
	    }
	    public List<RegisterCertificateData> searchRegisterForCert(RegisterNacSearchCriteria criteria, RequestInfo requestInfo) {
	        List<RegisterNac> registerDetails = repository.searchRegisterNacDetails(criteria);
	        List<RegisterCertificateData> registerCertDetails = new ArrayList<>();
	        registerDetails
	                .forEach(register -> {
	                    RegisterCertificateData registerForCertificate = nacCertService.setCertificateDetails(register, requestInfo);
	                    registerCertDetails.add(registerForCertificate);
	                });	         
	        return registerCertDetails;
	    }
	    public NacCertificate download(RegisterNacSearchCriteria criteria, RequestInfo requestInfo) {
	        try {
	            NacCertificate nacCertificate = new NacCertificate();
	            NacCertRequest nacCertRequest = NacCertRequest.builder().nacCertificate(nacCertificate).requestInfo(requestInfo).build();
	            List<RegisterCertificateData> regDetail = searchRegisterForCert(criteria, requestInfo);
	             
	            if(regDetail.size() == 1) {
	            	nacCertificate.setBirthPlace(regDetail.get(0).getBirthPlaceId());
	            	nacCertificate.setRegistrationId(regDetail.get(0).getId());
	            	nacCertificate.setApplicationId(regDetail.get(0).getApplicationId());
	            	nacCertificate.setApplicationNumber(regDetail.get(0).getAckNo()); 
	            	nacCertificate.setWard(regDetail.get(0).getWardCode());
	            	nacCertificate.setDistrict(regDetail.get(0).getTenantDistrict());
	            	nacCertificate.setDateofbirth(new Timestamp(regDetail.get(0).getDateOfBirth()) );
	            	nacCertificate.setDateofreport(new Timestamp(regDetail.get(0).getDateOfReport()));
	            	nacCertificate.setTenantId(regDetail.get(0).getTenantId());
	            	nacCertificate.setRegistrtionNo(regDetail.get(0).getRegistrationNo());
	                enrichment.setCertificateNumber(nacCertificate,requestInfo);
	                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	                String date = format.format(regDetail.get(0).getDateOfReport());
	                String datestr = date.split("-")[2];
	                nacCertificate.setYear(datestr);
	                if (regDetail.size() > 1)
	                    throw new CustomException("Invalid_Input", "Error in processing data");
	                certEnrichment.enrichCreateRequest(nacCertRequest);
	                regDetail.get(0).setCertId(nacCertRequest.getNacCertificate().getNacCertificateNo());
	                NacPdfRegisterRequest pdfRegisterRequest = NacPdfRegisterRequest.builder().requestInfo(requestInfo).nacCertificate(regDetail).build();
	                EgovPdfResp pdfResp = repository.saveBirthCertPdf(pdfRegisterRequest);
	                nacCertificate.setEmbeddedUrl(pdfRegisterRequest.getNacCertificate().get(0).getEmbeddedUrl());
	                nacCertificate.setDateofissue(pdfRegisterRequest.getNacCertificate().get(0).getRegistrationDate());
	                nacCertificate.setFilestoreid(pdfResp.getFilestoreIds().get(0));
	                nacCertificate.setApplicationStatus(NacCertificate.StatusEnum.FREE_DOWNLOAD);
	                repository.saveRegisterNacCert(nacCertRequest);
	            }
	            return nacCertificate;
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new CustomException("DOWNLOAD_ERROR", "Error in Downloading Certificate");
	        }
	    }
}
