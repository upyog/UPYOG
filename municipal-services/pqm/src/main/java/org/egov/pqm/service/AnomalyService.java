package org.egov.pqm.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.web.model.anomaly.PqmAnomaly;
import org.egov.pqm.web.model.anomaly.PqmAnomalyResponse;
import org.egov.pqm.web.model.anomaly.PqmAnomalySearchCriteria;
import org.egov.pqm.web.model.anomaly.PqmAnomalySearchRequest;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.egov.pqm.util.ErrorConstants.IDGEN_ERROR;
import static org.egov.pqm.util.ErrorConstants.PQM_ANOMALY_SEARCH_ERROR;

@Service
@Slf4j
public class AnomalyService {

    @Autowired
    ServiceConfiguration config;

    @Autowired
    private RestTemplate restTemplate;

    public List<PqmAnomaly> search(RequestInfo requestInfo, PqmAnomalySearchRequest pqmAnomalySearchRequest) {
      String uri = config.getPqmAnomalyHost() + config.getPqmAnomalySearchEndpoint();

        PqmAnomalyResponse response;
        try {
            response = restTemplate.postForObject(uri, pqmAnomalySearchRequest,
                    PqmAnomalyResponse.class);
        } catch (HttpClientErrorException e) {
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new CustomException(PQM_ANOMALY_SEARCH_ERROR, e.getMessage());
        }

        return response.getPqmAnomalys();
    }


}
