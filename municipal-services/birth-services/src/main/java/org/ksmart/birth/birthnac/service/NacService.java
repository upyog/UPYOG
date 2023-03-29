package org.ksmart.birth.birthnac.service;

import org.ksmart.birth.birthnac.repository.NacRepository;
import org.ksmart.birth.birthnac.validator.NacApplicationValidator;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.adoption.AdoptionApplication;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.ksmart.birth.workflow.WorkflowIntegratorAdoption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NacService {
 
    private final NacRepository repository;
    private final WorkflowIntegratorAdoption workflowIntegrator;
 
    private final MdmsUtil mdmsUtil;
    private final NacApplicationValidator validator;

    @Autowired
    NacService(NacRepository repository, MdmsUtil mdmsUtil,
               WorkflowIntegratorAdoption workflowIntegrator,
               NacApplicationValidator validator) {
 
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
    }

    public List<AdoptionApplication> saveAdoptionDetails(AdoptionDetailRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
        Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), request.getAdoptionDetails().get(0).getTenantId());
        // validate request
        //validator.validateCreate(request, mdmsData,mdmsDataLoc);

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
