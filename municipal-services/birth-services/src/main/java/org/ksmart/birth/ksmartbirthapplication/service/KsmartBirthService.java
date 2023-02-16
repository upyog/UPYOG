package org.ksmart.birth.ksmartbirthapplication.service;

import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthAppliactionDetail;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthApplicationSearchCriteria;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthDetailsRequest;
import org.ksmart.birth.ksmartbirthapplication.repository.KsmartBirthRepository;
import org.ksmart.birth.utils.MdmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
public class KsmartBirthService {
    private final KsmartBirthRepository repository;

    private final MdmsUtil mdmsUtil;

    @Autowired
    KsmartBirthService(KsmartBirthRepository repository, MdmsUtil mdmsUtil) {
        this.repository = repository;
        this.mdmsUtil = mdmsUtil;
    }

    public List<KsmartBirthAppliactionDetail> saveKsmartBirthDetails(KsmartBirthDetailsRequest request) {
        return repository.saveKsmartBirthDetails(request);
    }

    public List<KsmartBirthAppliactionDetail> updateKsmartBirthDetails(KsmartBirthDetailsRequest request) {
        return repository.updateKsmartBirthDetails(request);    }

    public List<KsmartBirthAppliactionDetail> searchKsmartBirthDetails(@Valid KsmartBirthApplicationSearchCriteria criteria) {
        return repository.searchKsmartBirthDetails(criteria);
    }
}
