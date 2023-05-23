package org.ksmart.birth.correction.service;

import org.ksmart.birth.birthcommon.model.WorkFlowCheck;
import org.ksmart.birth.birthcommon.model.demand.Demand;
import org.ksmart.birth.birthcommon.services.DemandService;
import org.ksmart.birth.correction.repository.CorrectionBirthRepository;
import org.ksmart.birth.correction.validator.CorrectionBirthApplicationValidator;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.correction.CorrectionApplication;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.ksmart.birth.web.model.correction.CorrectionResponse;
import org.ksmart.birth.workflow.WorkflowIntegratorCorrection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.STATUS_FOR_PAYMENT;

@Service
public class CorrectionBirthService {
    private final CorrectionBirthRepository repository;
    private final WorkflowIntegratorCorrection workflowIntegrator;
    private final MdmsUtil mdmsUtil;
    private final CorrectionBirthApplicationValidator validator;
    private final DemandService demandService;

    @Autowired
    CorrectionBirthService(CorrectionBirthRepository repository, MdmsUtil mdmsUtil, WorkflowIntegratorCorrection workflowIntegrator,
                           CorrectionBirthApplicationValidator validator, DemandService demandService) {
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
        this.demandService = demandService;
    }

    public List<CorrectionApplication> saveCorrectionBirthDetails(CorrectionRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
        WorkFlowCheck wfc = new WorkFlowCheck();
        // validate request
        validator.validateCreate(request, mdmsData);

        //call save
        List<CorrectionApplication> application =  repository.saveCorrectionBirthDetails(request);

//        WorkFlow Integration
        workflowIntegrator.callWorkFlow(request);
        application.forEach(birth->{
            if (birth.getApplicationStatus().equals(STATUS_FOR_PAYMENT)) {
                List<Demand> demands = new ArrayList<>();
                Demand demand = new Demand();
                demand.setTenantId(birth.getTenantId());
                demand.setConsumerCode(birth.getApplicationNo());
                demands.add(demand);
                wfc.setAmount(5);
                birth.setDemands(demandService.saveDemandDetails(demands, request.getRequestInfo(), wfc));
            }
        });

        return application;
    }

    public List<CorrectionApplication> updateKsmartBirthDetails(CorrectionRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
        // validate request
        validator.validateUpdate(request, mdmsData);

        //search application exist
        validator.validateUpdate(request, mdmsData);
        workflowIntegrator.callWorkFlow(request);
        return repository.updateKsmartBirthDetails(request);
    }

    public CorrectionResponse searchCorrectionDetails(CorrectionRequest request, SearchCriteria criteria) {
        return repository.searchCorrectionDetails(request,criteria);
    }
}
