package digit.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import digit.bmc.model.Schemes;
import digit.repository.SchemeSearchCriteria;
import digit.repository.SchemesRepository;
import digit.web.models.SchemeApplicationRequest;
import digit.web.models.scheme.EventDetails;

@Service
public class SchemeService {
    @Autowired
    private SchemesRepository schemesRepository;

    private static final Logger logger = LoggerFactory.getLogger(SchemeService.class);



    public List<EventDetails> getSchemes(RequestInfo requestInfo,SchemeSearchCriteria searchcriteria){
        // Fetch applications from database according to the given search criteria
        List<EventDetails> schemes = schemesRepository.getSchemeDetails(searchcriteria);
        // If no applications are found matching the given criteria, return an empty list
        if (CollectionUtils.isEmpty(schemes))
            return new ArrayList<>();
        return schemes;
    }

}
