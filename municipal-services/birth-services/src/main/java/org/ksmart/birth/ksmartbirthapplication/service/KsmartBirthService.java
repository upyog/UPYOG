package org.ksmart.birth.ksmartbirthapplication.service;

import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthAppliactionDetail;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthApplicationSearchCriteria;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthDetailsRequest;
import org.ksmart.birth.ksmartbirthapplication.repository.KsmartBirthRepository;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
public class KsmartBirthService {
    private final KsmartBirthRepository repository;
    private final WorkflowIntegrator workflowIntegrator;
    private final MdmsUtil mdmsUtil;

    @Autowired
    KsmartBirthService(KsmartBirthRepository repository, MdmsUtil mdmsUtil, WorkflowIntegrator workflowIntegrator) {
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
        this.workflowIntegrator  = workflowIntegrator;
    }

    public List<KsmartBirthAppliactionDetail> saveKsmartBirthDetails(KsmartBirthDetailsRequest request) {
        Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());

        // validate request
        //applicationValidator.validateCreate(request, mdmsData);

        //call save
        List<KsmartBirthAppliactionDetail> birthApplicationDetails =  repository.saveKsmartBirthDetails(request);

        //WorkFlow Integration
        workflowIntegrator.callWorkFlow(request);

        return birthApplicationDetails;
    }

    public List<KsmartBirthAppliactionDetail> updateKsmartBirthDetails(KsmartBirthDetailsRequest request) {
        workflowIntegrator.callWorkFlow(request);
        return repository.updateKsmartBirthDetails(request);
    }

    public List<KsmartBirthAppliactionDetail> searchKsmartBirthDetails(KsmartBirthDetailsRequest request,KsmartBirthApplicationSearchCriteria criteria) {
        return repository.searchKsmartBirthDetails(request,criteria);
    }
}
