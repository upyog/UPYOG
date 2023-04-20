package org.ksmart.birth.bornoutside.service;

import org.ksmart.birth.bornoutside.validator.BirthOutsideApplicationValidator;
import org.ksmart.birth.newbirth.validator.NewBirthApplicationValidator;
import org.ksmart.birth.bornoutside.repository.BornOutsideRepository;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.ksmart.birth.web.model.bornoutside.BornOutsideDetailRequest;
import org.ksmart.birth.workflow.WorkflowIntegratorBornOutside;
import org.ksmart.birth.workflow.WorkflowIntegratorNewBirth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BornOutsideService {
    private final BornOutsideRepository repository;
    private final WorkflowIntegratorBornOutside workflowIntegrator;
    private final MdmsUtil mdmsUtil;
    private final BirthOutsideApplicationValidator validator;

    @Autowired
    BornOutsideService(BornOutsideRepository repository, MdmsUtil mdmsUtil, WorkflowIntegratorBornOutside workflowIntegrator,
                       BirthOutsideApplicationValidator validator) {
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
        this.validator = validator;
    }

    public List<BornOutsideApplication> saveBirthApplication(BornOutsideDetailRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());

        // validate request
        validator.validateCreate(request, mdmsData);

        //call save
        List<BornOutsideApplication> birthApplicationDetails =  repository.saveBirthApplication(request);

        //WorkFlow Integration
        workflowIntegrator.callWorkFlow(request);
        return birthApplicationDetails;
    }

    public List<BornOutsideApplication> updateBirthApplication(BornOutsideDetailRequest request) {
      //  workflowIntegrator.callWorkFlow(request);
        return repository.updateBirthApplication(request);
    }

    public List<BornOutsideApplication> searchBirthDetails(BornOutsideDetailRequest request, SearchCriteria criteria) {
        return repository.searchBirthDetails(request,criteria);
    }
}
