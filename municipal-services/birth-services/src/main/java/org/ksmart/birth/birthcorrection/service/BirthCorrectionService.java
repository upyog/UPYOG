package org.ksmart.birth.birthcorrection.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthcorrection.repository.BirthCorrectionRepository;
import org.ksmart.birth.birthapplication.model.birth.BirthApplicationSearchCriteria;
import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BirthCorrectionService {

    @Autowired
    BirthCorrectionRepository repository;

    @Autowired
    WorkflowIntegrator WorkflowIntegrator;


    public List<BirthApplicationDetail> saveBirthDetails(BirthDetailsRequest request) {

       // WorkflowIntegrator.callWorkFlow(request);
        return repository.saveBirthDetails(request);
    }

    public List<BirthApplicationDetail> updateBirthDetails(BirthDetailsRequest request) {
        return repository.updateBirthDetails(request);
    }

    public List<BirthApplicationDetail> searchBirthDetails(BirthApplicationSearchCriteria criteria) {
        return repository.searchBirthDetails(criteria);
    }
}
