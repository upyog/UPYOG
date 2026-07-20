package org.egov.garbageservice.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import digit.models.coremodels.IdGenerationRequest;
import digit.models.coremodels.IdGenerationResponse;
import digit.models.coremodels.IdRequest;

/**
 * Calls egov-idgen service to generate unique, platform-standard IDs.
 * Replaces hardcoded application number generation in GarbageAccountService.
 */
@Repository
public class IdGenRepository {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    /**
     * Generates {@code count} unique IDs for the given tenant using the idgen service.
     *
     * @param requestInfo  caller's RequestInfo (passed through to idgen)
     * @param tenantId     tenant for which the ID is generated
     * @param idName       format name registered in MDMS (e.g. "garbage.application.num")
     * @param idFormat     format pattern  (e.g. "GB/[CITY.CODE]/[SEQ_GRBG_APPLICATION_NUM]")
     * @param count        number of IDs to generate in one call
     * @return list of generated ID strings, size == count
     */
    public List<String> getIdList(RequestInfo requestInfo, String tenantId,
                                  String idName, String idFormat, int count) {

        List<IdRequest> idRequests = new ArrayList<>();
        IdRequest idRequest = IdRequest.builder()
                .idName(idName)
                .format(idFormat)
                .tenantId(tenantId)
                .build();
        idRequests.add(idRequest);

        IdGenerationRequest idGenerationRequest = IdGenerationRequest.builder()
                .idRequests(idRequests)
                .requestInfo(requestInfo)
                .build();

        IdGenerationResponse response = restTemplate.postForObject(
                idGenHost + idGenPath, idGenerationRequest, IdGenerationResponse.class);

        if (response == null || response.getIdResponses() == null || response.getIdResponses().isEmpty()) {
            throw new CustomException("IDGEN_ERROR",
                    "No ID returned from idgen service for idName: " + idName);
        }

        // collect the generated ID strings from each IdResponse
        List<String> ids = new ArrayList<>();
        response.getIdResponses().forEach(idResponse -> ids.add(idResponse.getId()));
        return ids;
    }
}
