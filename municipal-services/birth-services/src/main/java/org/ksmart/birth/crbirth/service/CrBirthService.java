package org.ksmart.birth.crbirth.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.crbirth.model.BirthApplicationSearchCriteria;
import org.ksmart.birth.crbirth.model.BirthDetail;
import org.ksmart.birth.crbirth.model.BirthDetailsRequest;
import org.ksmart.birth.crbirth.repository.CrBirthRepository;
import org.ksmart.birth.crbirth.validator.BirthApplicationValidator;
import org.ksmart.birth.crbirth.validator.MdmsValidator;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CrBirthService {
    private final CrBirthRepository repository;
    private final WorkflowIntegrator workflowIntegrator;
    private final MdmsUtil mdmsUtil;
    private final MdmsValidator mdmsValidator;

    private final BirthApplicationValidator applicationValidator;

    @Autowired
    CrBirthService(CrBirthRepository repository,  WorkflowIntegrator workflowIntegrator, MdmsUtil mdmsUtil,
                   MdmsValidator mdmsValidator, BirthApplicationValidator applicationValidator) {
        this.repository = repository;
        this.workflowIntegrator = workflowIntegrator;
        this.mdmsUtil = mdmsUtil;
        this.mdmsValidator = mdmsValidator;
        this.applicationValidator = applicationValidator;
    }


    public List<BirthDetail> saveBirthDetails(BirthDetailsRequest request) {
        String tenantId = request.getBirthDetails()
                .get(0)
                .getTenantId();

        // validate mdms data
//        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo(), tenantId);

        // validate request
//        applicationValidator.validateCreate(request, mdmsData);

//        workflowIntegrator.callWorkFlow(request);
        return repository.saveBirthDetails(request);
    }

    public List<BirthDetail> updateBirthDetails(BirthDetailsRequest request) {
        return repository.updateBirthDetails(request);
    }

    public List<BirthDetail> searchBirthDetails(BirthApplicationSearchCriteria criteria) {
        return repository.searchBirthDetails(criteria);
    }
}
