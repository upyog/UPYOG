package org.ksmart.birth.outsidecountry.service;

import org.ksmart.birth.newbirth.repository.NewBirthRepository;
import org.ksmart.birth.newbirth.validator.NewBirthApplicationValidator;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptionService {
    private final NewBirthRepository repository;
    private final WorkflowIntegrator workflowIntegrator;
    private final MdmsUtil mdmsUtil;
    private final NewBirthApplicationValidator validator;

    @Autowired
    AdoptionService(NewBirthRepository repository, MdmsUtil mdmsUtil, WorkflowIntegrator workflowIntegrator,
                    NewBirthApplicationValidator validator) {
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
    }

    public List<NewBirthApplication> saveKsmartBirthDetails(NewBirthDetailRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());

        // validate request
        //validator.validateCreate(request, mdmsData);

        //call save
        List<NewBirthApplication> birthApplicationDetails =  repository.saveKsmartBirthDetails(request);

        //WorkFlow Integration
      //  workflowIntegrator.callWorkFlow(request);
        return birthApplicationDetails;
    }

    public List<NewBirthApplication> updateKsmartBirthDetails(NewBirthDetailRequest request) {
    //    workflowIntegrator.callWorkFlow(request);
        return repository.updateKsmartBirthDetails(request);
    }

    public List<NewBirthApplication> searchKsmartBirthDetails(NewBirthDetailRequest request, SearchCriteria criteria) {
        return repository.searchKsmartBirthDetails(request,criteria);
    }
}
