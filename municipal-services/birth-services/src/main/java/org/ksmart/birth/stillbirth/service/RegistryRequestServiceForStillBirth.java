package org.ksmart.birth.stillbirth.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.newbirth.repository.NewBirthRepository;
import org.ksmart.birth.stillbirth.repository.StillBirthRepository;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestServiceForStillBirth {

    private final StillBirthRepository repository;
    @Autowired
    RegistryRequestServiceForStillBirth(StillBirthRepository repository) {
        this.repository = repository;
    }
    public RegisterBirthDetailsRequest createRegistryRequestNew(StillBirthDetailRequest request) {///Work to get req to Register
        return repository.searchStillBirthDetailsForRegister(request);
    }
}


