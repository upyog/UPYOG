package org.ksmart.birth.adoption.service;

import org.ksmart.birth.adoption.repository.AdoptionRepository;
import org.ksmart.birth.adoption.validator.AdoptionApplicationValidator;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
 
 
 
import org.ksmart.birth.web.model.adoption.AdoptionApplication;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
//import org.ksmart.birth.workflow.WorkflowIntegrator;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptionService {
 
    private final AdoptionRepository repository;
//    private final WorkflowIntegrator workflowIntegrator;
 
    private final MdmsUtil mdmsUtil;
    private final AdoptionApplicationValidator validator;

    @Autowired 
    AdoptionService(AdoptionRepository repository, MdmsUtil mdmsUtil,
//    		WorkflowIntegrator workflowIntegrator,
    		AdoptionApplicationValidator validator) {
 
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
//        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
    }

    public List<AdoptionApplication> saveAdoptionDetails(AdoptionDetailRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());

        // validate request
        //validator.validateCreate(request, mdmsData);

        //call save
        List<AdoptionApplication> adoptionDetails =  repository.saveAdoptionDetails(request);

        //WorkFlow Integration
      //  workflowIntegrator.callWorkFlow(request);
        return adoptionDetails;
    }

    public List<AdoptionApplication> updateAdoptionBirthDetails(AdoptionDetailRequest request) {
    //    workflowIntegrator.callWorkFlow(request);
        return repository.updateKsmartBirthDetails(request);
    }

    public List<AdoptionApplication> searchKsmartBirthDetails(AdoptionDetailRequest request, SearchCriteria criteria) {
        return repository.searchKsmartBirthDetails(request,criteria);
    }
}
