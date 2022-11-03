package org.egov.filemgmnt.service;

import java.util.List;

import javax.validation.Valid;

import org.egov.filemgmnt.web.models.ApplicantService;
import org.egov.filemgmnt.web.models.ApplicantServiceRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.egov.filemgmnt.config.FilemgmntConfiguration;
import org.egov.filemgmnt.enrichment.ApplicantServiceEnrichment;
import org.egov.filemgmnt.kafka.Producer;
import org.egov.filemgmnt.repository.ApplicantServiceRepository;
import org.egov.filemgmnt.validators.ApplicantPersonalValidator;
import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class ApplicantServiceService {

    public List<ApplicantService> create(@Valid ApplicantServiceRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

}
