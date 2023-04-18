package org.ksmart.birth.bornoutside.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.*;
import org.ksmart.birth.bornoutside.repository.BornOutsideRepository;
import org.ksmart.birth.web.model.bornoutside.BornOutsideDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class RegistryRequestServiceForBirthOutside {
    @Autowired
    BornOutsideRepository repository;
    public RegisterBirthDetailsRequest createRegistryRequestNew(BornOutsideDetailRequest request) {
        return repository.searchBirthDetailsForRegister(request);
    }
}


