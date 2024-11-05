package org.egov.nationaldashboardingest.service;


import org.egov.common.contract.request.RequestInfo;
import org.egov.nationaldashboardingest.validators.IngestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;




@Slf4j
@Service
public class StateFetchService {

    @Autowired
    IngestValidator ingestValidator;


    public Map<String, Map< String, String>> stateList(RequestInfo requestInfo) {
        return ingestValidator.stateListMDMS(requestInfo);

    };
};
