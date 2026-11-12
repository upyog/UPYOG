package org.upyog.wms.service;

import static org.upyog.wms.util.WmsUtil.nn;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.wms.config.WmsConfiguration;
import org.upyog.wms.constants.ErrorCodes;
import org.upyog.wms.repository.ServiceRequestRepository;
import org.upyog.wms.web.models.idgen.IdGenerationRequest;
import org.upyog.wms.web.models.idgen.IdGenerationResponse;
import org.upyog.wms.web.models.idgen.IdRequest;
import org.upyog.wms.web.models.idgen.IdResponse;

/** egov-idgen client. Generates several ids in ONE call (count) instead of one call per id. */
@Service
@RequiredArgsConstructor
public class IdGenService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final WmsConfiguration config;

    public List<String> generate(RequestInfo requestInfo, String tenantId, String idName, int count) {
        if (count <= 0) {
            return List.of();
        }
        IdGenerationRequest request = IdGenerationRequest.builder()
                .requestInfo(requestInfo)
                .idRequests(List.of(IdRequest.builder().idName(idName).tenantId(tenantId).count(count).build()))
                .build();
        IdGenerationResponse response = serviceRequestRepository.post(config.getIdGenHost() + config.getIdGenPath(),
                request, IdGenerationResponse.class);
        List<String> ids = response == null ? List.of()
                : nn(response.getIdResponses()).stream().map(IdResponse::getId).toList();
        if (ids.size() != count) {
            throw new CustomException(ErrorCodes.EXTERNAL_SERVICE, "IdGen returned " + ids.size() + " ids for "
                    + idName + ", expected " + count);
        }
        return ids;
    }

    public String generateOne(RequestInfo requestInfo, String tenantId, String idName) {
        return generate(requestInfo, tenantId, idName, 1).get(0);
    }
}
