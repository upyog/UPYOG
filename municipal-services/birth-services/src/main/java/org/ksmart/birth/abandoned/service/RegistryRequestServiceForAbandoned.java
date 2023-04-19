package org.ksmart.birth.abandoned.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.abandoned.repository.AbandonedRepository;
import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.newbirth.repository.NewBirthRepository;
import org.ksmart.birth.web.model.abandoned.AbandonedRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestServiceForAbandoned {
    private final AbandonedRepository repository;
    @Autowired
    RegistryRequestServiceForAbandoned(AbandonedRepository repository) {
        this.repository = repository;
    }


    public RegisterBirthDetailsRequest createRegistryRequestNew(AbandonedRequest request) {///Work to get req to Register
        return repository.searchBirthDetailsForRegister(request);
    }

}


