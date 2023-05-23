package org.ksmart.birth.abandoned.service;

import org.ksmart.birth.abandoned.repository.AbandonedRepository;
import org.ksmart.birth.abandoned.validator.AbandonedApplicationValidator;
import org.ksmart.birth.birthcommon.model.demand.Demand;
import org.ksmart.birth.birthcommon.services.DemandService;
import org.ksmart.birth.newbirth.repository.NewBirthRepository;
import org.ksmart.birth.newbirth.validator.NewBirthApplicationValidator;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.abandoned.AbandonedApplication;
import org.ksmart.birth.web.model.abandoned.AbandonedRequest;
import org.ksmart.birth.web.model.abandoned.AbandonedResponse;
import org.ksmart.birth.web.model.abandoned.AbandonedSearchResponse;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.workflow.WorkflowIntegratorAbandoned;
import org.ksmart.birth.workflow.WorkflowIntegratorNewBirth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.STATUS_FOR_PAYMENT;

@Service
public class AbandonedService {
    private final AbandonedRepository repository;
    private final WorkflowIntegratorAbandoned workflowIntegrator;
    private final MdmsUtil mdmsUtil;
    private final AbandonedApplicationValidator validator;
    private final DemandService demandService;

    @Autowired
    AbandonedService(AbandonedRepository repository, MdmsUtil mdmsUtil, WorkflowIntegratorAbandoned workflowIntegrator,
                     AbandonedApplicationValidator validator, DemandService demandService) {
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
        this.demandService = demandService;
    }

    public List<AbandonedApplication> saveKsmartBirthDetails(AbandonedRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());

        // validate request
        validator.validateCreate(request, mdmsData);

        //call save
        List<AbandonedApplication> birthApplicationDetails =  repository.saveBirthDetails(request);
//        WorkFlow Integration
        workflowIntegrator.callWorkFlow(request);
        return birthApplicationDetails;
    }

    public List<AbandonedApplication> updateKsmartBirthDetails(AbandonedRequest request) {
        workflowIntegrator.callWorkFlow(request);
        return repository.updateBirthDetails(request);
    }

    public AbandonedSearchResponse searchKsmartBirthDetails(AbandonedRequest request, SearchCriteria criteria) {
        return repository.searchBirthDetails(request,criteria);
    }
}
