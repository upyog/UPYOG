package org.bel.birthdeath.crdeath.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bel.birthdeath.common.repository.ServiceRequestRepository;
import org.bel.birthdeath.crdeath.config.CrDeathConfiguration;
import org.bel.birthdeath.crdeath.web.models.idgen.IdGenerationRequest;
import org.bel.birthdeath.crdeath.web.models.idgen.IdGenerationResponse;
import org.bel.birthdeath.crdeath.web.models.idgen.IdRequest;
import org.bel.birthdeath.crdeath.web.models.idgen.IdResponse;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

// import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class IdgenUtil {

    // private ServiceRequestRepository serviceRequestRepository;
    // private CrDeathConfiguration config;

    // @Autowired
    // public IdgenUtil(CrDeathConfiguration config, ServiceRequestRepository serviceRequestRepository) {
    //     this.config = config;
    //     this.serviceRequestRepository = serviceRequestRepository;
    // }

    // @Autowired
    // private ObjectMapper mapper;

    // @Autowired
    // private ServiceRequestRepository restRepo;

    // public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat,
    //                               Integer count) {
    //     List<IdRequest> reqList = new ArrayList<>();
    //     for (int i = 0; i < count; i++) {
    //         reqList.add(IdRequest.builder()
    //                              .idName(idName)
    //                              .format(idformat)
    //                              .tenantId(tenantId)
    //                              .build());
    //     }

    //     IdGenerationRequest request = IdGenerationRequest.builder()
    //                                                      .idRequests(reqList)
    //                                                      .requestInfo(requestInfo)
    //                                                      .build();
    //     // StringBuilder uri = new StringBuilder(idGenHost).append(idGenPath);
    //     // IdGenerationResponse response = mapper.convertValue(restRepo.fetchResult(uri, request),
    //     //                                                     IdGenerationResponse.class);

    //     Object result = serviceRequestRepository.fetchResult(getIDGenSearchUrl(), request); 
    //     //return result;
    //     // List<IdResponse> idResponses = response.getIdResponses();

    //     // if (CollectionUtils.isEmpty(idResponses)) {
    //     //     throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");
    //     // }

    //     // return idResponses.stream()
    //     //                   .map(IdResponse::getId)
    //     //                   .collect(Collectors.toList());
    // }   

    // public StringBuilder getIDGenSearchUrl() {
    //     return new StringBuilder().append(config.getIdGenHost()).append(config.getMdmsEndPoint());
    // }
}