package org.ksmart.birth.birthadoption.service;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthadoption.model.AdoptionDetail;
import org.ksmart.birth.birthadoption.model.adoption.AdoptionRequest;
import org.ksmart.birth.birthadoption.repository.AdoptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdoptionService {
    private final AdoptionRepository adoptionRepository;
    @Autowired
    AdoptionService(AdoptionRepository adoptionRepository) {
        this.adoptionRepository = adoptionRepository;
    }

//    public List<AdoptionDetail> saveAdoptionDetails(AdoptionRequest request) {
//        return adoptionRepository.saveRegisterBirthDetails(request);
//    }

}
