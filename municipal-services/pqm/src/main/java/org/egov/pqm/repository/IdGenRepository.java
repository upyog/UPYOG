package org.egov.pqm.repository;

import static org.egov.pqm.util.ErrorConstants.IDGEN_ERROR;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.web.model.idgen.IdGenerationRequest;
import org.egov.pqm.web.model.idgen.IdGenerationResponse;
import org.egov.pqm.web.model.idgen.IdRequest;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class IdGenRepository {

  private RestTemplate restTemplate;

  private ServiceConfiguration config;

  @Autowired
  public IdGenRepository(RestTemplate restTemplate, ServiceConfiguration config) {
    this.restTemplate = restTemplate;
    this.config = config;
  }

  public IdGenerationResponse getId(RequestInfo requestInfo, String tenantId, String name,
      String format) {

    List<IdRequest> reqList = new ArrayList<>();
    reqList.add(IdRequest.builder().idName(name).format(format).tenantId(tenantId).build());
    IdGenerationRequest req = IdGenerationRequest.builder().idRequests(reqList)
        .requestInfo(requestInfo).build();
    IdGenerationResponse response = null;
    try {
      response = restTemplate.postForObject(config.getIdGenHost() + config.getIdGenPath(), req,
          IdGenerationResponse.class);
    } catch (HttpClientErrorException e) {
      throw new ServiceCallException(e.getResponseBodyAsString());
    } catch (Exception e) {
      throw new CustomException(IDGEN_ERROR, e.getMessage());
    }
    return response;
  }
}