package upyog.util;

import lombok.RequiredArgsConstructor;
import org.egov.common.contract.idgen.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.listener.AbstractAuditListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import upyog.client.IdGenClient;
import upyog.config.EstateConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static upyog.config.ServiceConstants.IDGEN_ERROR;
import static upyog.config.ServiceConstants.NO_IDS_FOUND_ERROR;

@Component
public class IdGenUtil {

    @Autowired
    private IdGenClient idGenClient;

    public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, Integer count) {
        List<IdRequest> reqList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reqList.add(IdRequest.builder()
                    .idName(idName)
                    .format(idformat)
                    .tenantId(tenantId)
                    .build());
        }

        IdGenerationRequest request = IdGenerationRequest.builder()
                .idRequests(reqList)
                .requestInfo(requestInfo)
                .build();

        IdGenerationResponse idGenerationResponse = idGenClient.getId(request);

        if (idGenerationResponse == null || idGenerationResponse.getIdResponses() == null)
            throw new CustomException("IDGEN_ERROR", "No ids returned from idgen Service");
        List<IdResponse> idResponses = idGenerationResponse.getIdResponses();
        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(IDGEN_ERROR, NO_IDS_FOUND_ERROR);
        }

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }
}
