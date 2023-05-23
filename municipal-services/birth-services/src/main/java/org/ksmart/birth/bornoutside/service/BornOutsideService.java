package org.ksmart.birth.bornoutside.service;

import org.ksmart.birth.birthcommon.model.WorkFlowCheck;
import org.ksmart.birth.birthcommon.model.demand.Demand;
import org.ksmart.birth.birthcommon.services.DemandService;
import org.ksmart.birth.bornoutside.validator.BirthOutsideApplicationValidator;
import org.ksmart.birth.bornoutside.repository.BornOutsideRepository;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.ksmart.birth.web.model.bornoutside.BornOutsideDetailRequest;
import org.ksmart.birth.web.model.bornoutside.BornOutsideResponse;
import org.ksmart.birth.workflow.WorkflowIntegratorBornOutside;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.STATUS_FOR_PAYMENT;

@Service
public class BornOutsideService {
    private final BornOutsideRepository repository;
    private final WorkflowIntegratorBornOutside workflowIntegrator;
    private final MdmsUtil mdmsUtil;
    private final BirthOutsideApplicationValidator validator;
    private final DemandService demandService;

    @Autowired
    BornOutsideService(BornOutsideRepository repository, MdmsUtil mdmsUtil, WorkflowIntegratorBornOutside workflowIntegrator,
                       BirthOutsideApplicationValidator validator, DemandService demandService) {
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
        this.demandService = demandService;
    }

    public List<BornOutsideApplication> saveBirthApplication(BornOutsideDetailRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
        WorkFlowCheck wfc = new WorkFlowCheck();
        // validate request
        validator.validateCreate(request, mdmsData);

        //call save
        List<BornOutsideApplication> application =  repository.saveBirthApplication(request);

        //WorkFlow Integration
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

    public List<BornOutsideApplication> updateBirthApplication(BornOutsideDetailRequest request) {
        workflowIntegrator.callWorkFlow(request);
        return repository.updateBirthApplication(request);
    }

    public BornOutsideResponse searchBirthDetails(BornOutsideDetailRequest request, SearchCriteria criteria) {
        return repository.searchBirthDetails(request,criteria);
    }
}
