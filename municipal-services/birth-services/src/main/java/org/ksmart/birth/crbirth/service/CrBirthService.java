package org.ksmart.birth.crbirth.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.crbirth.model.BirthApplicationSearchCriteria;
import org.ksmart.birth.crbirth.model.BirthDetail;
import org.ksmart.birth.crbirth.model.BirthDetailsRequest;
import org.ksmart.birth.crbirth.repository.CrBirthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ksmart.birth.workflow.WorkflowIntegrator;

import java.util.List;

@Slf4j
@Service
public class CrBirthService {

    @Autowired
    CrBirthRepository repository;

    @Autowired
    WorkflowIntegrator WorkflowIntegrator;


    public List<BirthDetail> saveBirthDetails(BirthDetailsRequest request) {

        WorkflowIntegrator.callWorkFlow(request);
        return repository.saveBirthDetails(request);
    }

    public List<BirthDetail> updateBirthDetails(BirthDetailsRequest request) {
        return repository.updateBirthDetails(request);
    }

    public List<BirthDetail> searchBirthDetails(BirthApplicationSearchCriteria criteria) {
        return repository.searchBirthDetails(criteria);
    }
}
