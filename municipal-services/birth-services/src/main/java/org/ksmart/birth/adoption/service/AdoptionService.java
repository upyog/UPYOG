package org.ksmart.birth.adoption.service;

import org.ksmart.birth.adoption.enrichment.AdoptionEnrichment;
import org.ksmart.birth.adoption.repository.AdoptionRepository;
import org.ksmart.birth.adoption.validator.AdoptionApplicationValidator;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
 
 
 
import org.ksmart.birth.web.model.adoption.AdoptionApplication;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.ksmart.birth.workflow.WorkflowIntegratorAdoption;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptionService {
 
    private final AdoptionRepository repository;
    private final AdoptionEnrichment adoptionEnrichment;
    private final WorkflowIntegratorAdoption workflowIntegrator;
 
    private final MdmsUtil mdmsUtil;
    private final AdoptionApplicationValidator validator;

    @Autowired 
    AdoptionService(AdoptionRepository repository, MdmsUtil mdmsUtil, AdoptionEnrichment adoptionEnrichment,		
    		WorkflowIntegratorAdoption workflowIntegrator,
    		AdoptionApplicationValidator validator) {
 
    	this.adoptionEnrichment = adoptionEnrichment;
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
    }

    public List<AdoptionApplication> saveAdoptionDetails(AdoptionDetailRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
        Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), request.getAdoptionDetails().get(0).getTenantId());
        
        // validate request
        validator.validateCreate(request, mdmsData,mdmsDataLoc);
        
        // Enrich request
        adoptionEnrichment.enrichCreate(request);

        //WorkFlow Integration
        workflowIntegrator.callWorkFlow(request);
        
        //call save
        List<AdoptionApplication> adoptionDetails =  repository.saveAdoptionDetails(request);
        return adoptionDetails;
    }

    public List<AdoptionApplication> updateAdoptionBirthDetails(AdoptionDetailRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
        Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), request.getAdoptionDetails().get(0).getTenantId());
        
        // validate request
        validator.validateUpdate(request, mdmsData,mdmsDataLoc);

       // Enrich request
    	adoptionEnrichment.enrichUpdate(request);
    	
       //WorkFlow Integration
    	 if(request.getAdoptionDetails().get(0).getIsWorkflow()) {
    		 
    		 workflowIntegrator.callWorkFlow(request);
    	 }
    	 
    	//call update
        return repository.updateKsmartBirthDetails(request);
    }

    public List<AdoptionApplication> searchKsmartBirthDetails(AdoptionDetailRequest request, SearchCriteria criteria) {
        return repository.searchKsmartBirthDetails(request,criteria);
    }
}
