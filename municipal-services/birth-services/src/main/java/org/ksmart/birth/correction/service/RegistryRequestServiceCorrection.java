package org.ksmart.birth.correction.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.correction.repository.CorrectionBirthRepository;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestServiceCorrection {
    private final CorrectionBirthRepository repository;
    @Autowired
    RegistryRequestServiceCorrection(CorrectionBirthRepository repository) {
        this.repository = repository;
    }


    public RegisterBirthDetailsRequest createRegistryRequestNew(CorrectionRequest request) {///Work to get req to Register
        return repository.searchBirthDetailsForRegister(request);
    }
}


