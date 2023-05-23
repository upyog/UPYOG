package org.ksmart.birth.birthnac.service;


import org.ksmart.birth.birthcommon.model.WorkFlowCheck;
import org.ksmart.birth.birthcommon.model.demand.Demand;
import org.ksmart.birth.birthcommon.services.DemandService;
import org.ksmart.birth.birthnac.repository.NacRepository;
import org.ksmart.birth.birthnac.validator.NacApplicationValidator;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.birthnac.*;
 
import org.ksmart.birth.workflow.WorkflowIntegratorNac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.ksmart.birth.utils.BirthConstants.STATUS_FOR_PAYMENT;

import java.util.ArrayList;
import java.util.List;

@Service
public class NacService {
 
    private final NacRepository repository;
    private final WorkflowIntegratorNac workflowIntegrator;
    private final DemandService demandService;

 
    private final MdmsUtil mdmsUtil;
    private final NacApplicationValidator validator;

    @Autowired
    NacService(NacRepository repository, MdmsUtil mdmsUtil, 
    		WorkflowIntegratorNac workflowIntegrator,
    		DemandService demandService,
               NacApplicationValidator validator) {
 
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
        this.demandService= demandService;
        
    }

    public List<NacApplication> saveNacDetails(NacDetailRequest request) {
    	WorkFlowCheck wfc = new WorkFlowCheck();
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
        Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), request.getNacDetails().get(0).getTenantId());
        // validate request
        validator.validateCreate(request, mdmsData,mdmsDataLoc);

        //call save
        List<NacApplication> nacDetails =  repository.saveNacDetails(request);

        //WorkFlow Integration
        workflowIntegrator.callWorkFlow(request);
        
        //Demand Creation Maya commented
        nacDetails.forEach(birth->{
//            if(wfc.getPayment()!=null) {
//                if (wfc.getPayment()) {
        	 
                    if (birth.getApplicationStatus().equals(STATUS_FOR_PAYMENT)) {
                        List<Demand> demands = new ArrayList<>();
                        Demand demand = new Demand();
                        demand.setTenantId(birth.getTenantId());
                        demand.setConsumerCode(birth.getApplicationNo());
                        demands.add(demand);
                        wfc.setAmount(2);
                        birth.setDemands(demandService.saveDemandDetails(demands, request.getRequestInfo(), wfc));
                    }
//                }
//            }
       });
        
        
        return nacDetails;
    }

    public List<NacApplication> updateNacDetails(NacDetailRequest request) {
//        workflowIntegrator.callWorkFlow(request);
        return repository.updateKsmartBirthDetails(request);
    }

    public List<NacApplication> searchNacDetails(NacDetailRequest request, SearchCriteria criteria) {
        return repository.searchNacDetails(request,criteria);
    }
    
//     Nac certificate download
//    public List<CertificateDetails> downloadCertificate(final RequestInfo requestInfo,
//                                                        final NacSearchCriteria searchCriteria) {
//        final CertificateRequest request = certService.createCertificateRequest(requestInfo,searchCriteria );       
//
//        // producer.push(fmConfig.getSaveApplicantCertificateTopic(), request);
//
//        return request.getCertificateDetails();
//    }

    
//    public List<NacApplication> searchRegisterForCert(NacDetailRequest request, NacSearchCriteria criteria) { 
//        List<NacApplication> registerDetails = repository.searchNacCertDetails(request,criteria);
//        List<RegisterCertificateData> registerCertDetails = new ArrayList<>();
//        registerDetails
//                .forEach(register -> {
//                    RegisterCertificateData registerForCertificate = birthCertService.setCertificateDetails(register, requestInfo);
//                    registerCertDetails.add(registerForCertificate);
//                });
//        return registerCertDetails;
//        
//    }
//    public NacCertificate download(NacSearchCriteria criteria, NacDetailRequest requestInfo) {
//        try {
//        	NacCertificate nacCertificate = new NacCertificate();
//        	NacCertRequest nacCertRequest = NacCertRequest.builder().nacCertificate(nacCertificate).requestInfo(requestInfo).build();
//            List<RegisterCertificateData> regDetail = searchRegisterForCert(requestInfo ,criteria );
//            if(regDetail.size() > 0) {
//            	nacCertificate.setBirthPlace(regDetail.get(0).getPlaceDetails());
//            	nacCertificate.setRegistrationId(regDetail.get(0).getId());
//            	nacCertificate.setApplicationId(regDetail.get(0).getApplicationId());
//            	nacCertificate.setApplicationNumber(regDetail.get(0).getAckNo());
//            	nacCertificate.setGender(regDetail.get(0).getGenderEn().toString());
//            	nacCertificate.setWard(regDetail.get(0).getWardCode());
//            	nacCertificate.setState(regDetail.get(0).getTenantState());
//            	nacCertificate.setDistrict(regDetail.get(0).getTenantDistrict());
//            	nacCertificate.setBirthCertificateNo(regDetail.get(0).getRegistrationNo().toString());
//            	nacCertificate.setDateofbirth(new Timestamp(regDetail.get(0).getDateOfBirth()) );
//            	nacCertificate.setDateofreport(new Timestamp(regDetail.get(0).getDateOfReport()));
//            	nacCertificate.setTenantId(regDetail.get(0).getTenantId());          
//                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//                String date = format.format(regDetail.get(0).getDateOfReport());
//                String datestr = date.split("-")[2];
//                nacCertificate.setYear(datestr);
//                if (regDetail.size() > 1)
//                    throw new CustomException("Invalid_Input", "Error in processing data");
//                enrichmentService.enrichCreateRequest(birthCertRequest);
//                regDetail.get(0).setCertId(birthCertRequest.getBirthCertificate().getBirthCertificateNo());
//                BirthPdfRegisterRequest pdfRegisterRequest = BirthPdfRegisterRequest.builder().requestInfo(requestInfo).birthCertificate(regDetail).build();
//                EgovPdfResp pdfResp = repository.saveBirthCertPdf(pdfRegisterRequest);
//                nacCertificate.setEmbeddedUrl(pdfRegisterRequest.getBirthCertificate().get(0).getEmbeddedUrl());
//                nacCertificate.setDateofissue(pdfRegisterRequest.getBirthCertificate().get(0).getRegistrationDate());
//                nacCertificate.setFilestoreid(pdfResp.getFilestoreIds().get(0));
//                nacCertificate.setApplicationStatus(NacCertificate.StatusEnum.FREE_DOWNLOAD);
//                 
//            }
//            return nacCertificate;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new CustomException("DOWNLOAD_ERROR", "Error in Downloading Certificate");
//        }
//    }
}
