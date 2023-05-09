package org.ksmart.birth.newbirth.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.birthcommon.model.WorkFlowCheck;
import org.ksmart.birth.birthcommon.model.demand.Demand;
import org.ksmart.birth.birthcommon.services.DemandService;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.newbirth.repository.NewBirthRepository;
import org.ksmart.birth.newbirth.validator.NewBirthApplicationValidator;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.workflow.WorkflowIntegratorNewBirth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.STATUS_FOR_PAYMENT;

@Service
public class NewBirthService {
    private final NewBirthRepository repository;
    private final WorkflowIntegratorNewBirth workflowIntegrator;
    private final MdmsUtil mdmsUtil;
    private final NewBirthApplicationValidator validator;
    private final DemandService demandService;


    @Autowired
    NewBirthService(NewBirthRepository repository, MdmsUtil mdmsUtil, WorkflowIntegratorNewBirth workflowIntegrator,
                    NewBirthApplicationValidator validator,  DemandService demandService) {
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
        this.demandService = demandService;
    }

    public List<NewBirthApplication> saveKsmartBirthDetails(NewBirthDetailRequest request) {
        WorkFlowCheck wfc = new WorkFlowCheck();
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
        // validate request

        validator.validateCreate(request, wfc, mdmsData);
        //call save
        List<NewBirthApplication> birthApplicationDetails =  repository.saveKsmartBirthDetails(request, mdmsData);
        //WorkFlow Integration
        workflowIntegrator.callWorkFlow(request);

        //Demand Creation Maya commented
        birthApplicationDetails.forEach(birth->{
            if(wfc.getPayment()!=null) {
                if (wfc.getPayment()) {
                    if (birth.getApplicationStatus().equals(STATUS_FOR_PAYMENT)) {
                        List<Demand> demands = new ArrayList<>();
                        Demand demand = new Demand();
                        demand.setTenantId(birth.getTenantId());
                        demand.setConsumerCode(birth.getApplicationNo());
                        demands.add(demand);
                        birth.setDemands(demandService.saveDemandDetails(demands, request.getRequestInfo(), wfc));
                    }
                }
            }
       });
        
        return birthApplicationDetails;
    }
      
    public List<NewBirthApplication> updateBirthDetails(NewBirthDetailRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
        // validate request

        final List<NewBirthApplication> newApplication = request.getNewBirthDetails();
        Assert.notNull(newApplication, "New birth application details must not be null.");

        NewBirthApplication newBirthApplicationExisting = findBirthRequestById(request);

        //search application exist
        validator.validateUpdate(request,  newBirthApplicationExisting, mdmsData, false);
        if(request.getNewBirthDetails().get(0).getIsWorkflow()) {
            workflowIntegrator.callWorkFlow(request);
        }
        return repository.updateKsmartBirthDetails(request, mdmsData);
    }
    public RegisterBirthDetailsRequest createRegistryRequest(NewBirthDetailRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
        return repository.searchBirthDetailsForRegister(request, mdmsData);
    }

    private NewBirthApplication findBirthRequestById(final NewBirthDetailRequest request) {
        if(request.getNewBirthDetails().size() >0){
            final SearchCriteria searchCriteria = buildSearchCriteria(request.getNewBirthDetails().get(0));
            final List<NewBirthApplication> application = repository.searchBirthDetails(request,searchCriteria);
            return CollectionUtils.isNotEmpty(application)
                    ? application.get(0)
                    : null;
        } else{
            return null;
        }
    }

    public List<NewBirthApplication> searchBirthDetails(NewBirthDetailRequest request, SearchCriteria criteria) {
        return repository.searchBirthDetails(request,criteria);
    }

    public List<NewBirthApplication> searchBirthDetailsCommon(NewBirthDetailRequest request, SearchCriteria criteria) {
        return repository.searchBirthDetailsCommon(request,criteria);
    }
    public List<NewBirthApplication> searchBirth(RequestInfo request, SearchCriteria criteria) {
        return repository.searchBirth(request,criteria);
    }

    protected SearchCriteria buildSearchCriteria(final NewBirthApplication application) {
        SearchCriteria searchCriteria = null;

        if (StringUtils.isNotBlank(application.getId())) {
            searchCriteria = new SearchCriteria();

            if (StringUtils.isNotBlank(application.getId())) {
                searchCriteria.setId(application.getId());
            }
        }
        return searchCriteria;
    }
}
