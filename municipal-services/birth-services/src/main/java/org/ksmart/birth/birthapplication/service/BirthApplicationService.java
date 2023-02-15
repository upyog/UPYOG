package org.ksmart.birth.birthapplication.service;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthapplication.model.birth.BirthApplicationSearchCriteria;
import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.birthapplication.repository.BirthApplicationRepository;
import org.ksmart.birth.birthapplication.validator.BirthApplicationValidator;
import org.ksmart.birth.birthapplication.validator.MdmsValidator;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BirthApplicationService {
    private final BirthApplicationRepository repository;
    private final WorkflowIntegrator workflowIntegrator;
    private final MdmsUtil mdmsUtil;
    private final MdmsValidator mdmsValidator;
    private final BirthApplicationValidator applicationValidator;

    @Autowired
    BirthApplicationService(BirthApplicationRepository repository, WorkflowIntegrator workflowIntegrator, MdmsUtil mdmsUtil,
                            MdmsValidator mdmsValidator, BirthApplicationValidator applicationValidator) {
        this.repository = repository;
        this.workflowIntegrator = workflowIntegrator;
        this.mdmsUtil = mdmsUtil;
        this.mdmsValidator = mdmsValidator;
        this.applicationValidator = applicationValidator;

    }


    public List<BirthApplicationDetail> saveBirthDetails(BirthDetailsRequest request) {

         //validate mdms data
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());

        // validate request
        applicationValidator.validateCreate(request, mdmsData);

        //call save
        List<BirthApplicationDetail> birthApplicationDetails =  repository.saveBirthDetails(request);

        //WorkFlow Integration
        workflowIntegrator.callWorkFlow(request);

        return  birthApplicationDetails;
    }

    public List<BirthApplicationDetail> updateBirthDetails(BirthDetailsRequest request) {
        
     //   workflowIntegrator.callWorkFlow(request);

        return repository.updateBirthDetails(request);

    }

    public List<BirthApplicationDetail> searchBirthDetails(BirthApplicationSearchCriteria criteria) {
        return repository.searchBirthDetails(criteria);
    }
}
