package org.ksmart.birth.stillbirth.service;

import org.ksmart.birth.newbirth.validator.NewBirthApplicationValidator;
import org.ksmart.birth.stillbirth.repository.StillBirthRepository;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.stillbirth.StillBirthApplication;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.ksmart.birth.workflow.WorkflowIntegratorNewBirth;
import org.ksmart.birth.workflow.WorkflowIntegratorStillBirth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StillBirthService {
    private final StillBirthRepository repository;
    private final WorkflowIntegratorStillBirth workflowIntegrator;
    private final MdmsUtil mdmsUtil;
    private final NewBirthApplicationValidator validator;

    @Autowired
    StillBirthService(StillBirthRepository repository, MdmsUtil mdmsUtil, WorkflowIntegratorStillBirth workflowIntegrator,
                      NewBirthApplicationValidator validator) {
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
    }

    public List<StillBirthApplication> saveBirthDetails(StillBirthDetailRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());

        // validate request
        //validator.validateCreate(request, mdmsData);

        //call save
        List<StillBirthApplication> birthApplicationDetails =  repository.saveBirthDetails(request);

        //WorkFlow Integration
        //workflowIntegrator.callWorkFlow(request);
        return birthApplicationDetails;
    }

    public List<StillBirthApplication> updateBirthDetails(StillBirthDetailRequest request) {
        workflowIntegrator.callWorkFlow(request);
        return repository.updateBirthDetails(request);
    }

    public List<StillBirthApplication> searchKsmartBirthDetails(StillBirthDetailRequest request, SearchCriteria criteria) {
        return repository.searchStillBirthDetails(request,criteria);
    }
}
