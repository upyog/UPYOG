package org.upyog.cdwm.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.kafka.Producer;
import org.upyog.cdwm.repository.CNDServiceRepository;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.CNDServiceSearchCriteria;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class CNDServiceRepositoryImpl implements CNDServiceRepository {

	@Autowired
	CNDConfiguration config;
	
	@Autowired
	Producer producer;
	
    @Override
    public void saveCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest) {
    	log.info("Saving CND application request data for appliaction no : "
				+ cndApplicationRequest.getCndApplication().getApplicationNumber());
		producer.push(config.getCndApplicationSaveTopic(), cndApplicationRequest);

    }

    @Override
    public List<CNDApplicationDetail> getCNDApplicationDetail(CNDServiceSearchCriteria cndServiceSearchCriteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getCNDApplicationsCount(CNDServiceSearchCriteria criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest) {
        // TODO Auto-generated method stub

    }
}
