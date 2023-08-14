package org.egov.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.IdGenerationRequest;
import org.egov.web.models.IdGenerationResponse;
import org.egov.web.models.IdRequest;
import org.egov.web.models.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class IdgenUtil {
	
	 	@Value("${egov.idgen.host}")
	    private String idGenHost;

	    @Value("${egov.idgen.path}")
	    private String idGenPath;

	    @Autowired
	    private ObjectMapper mapper;

	    @Autowired
	    private org.egov.repository.ServiceRequestRepository restRepo;

	    public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, Integer count) {
	        List<IdRequest> reqList = new ArrayList<>();
	        for (int i = 0; i < count; i++) {
	        	reqList.add(IdRequest.builder().idName(idName).format(idformat).tenantId(tenantId).build());
	        }

	        IdGenerationRequest request = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
	        StringBuilder uri = new StringBuilder(idGenHost).append(idGenPath);
	        IdGenerationResponse response = mapper.convertValue(restRepo.fetchResult(uri, request), IdGenerationResponse.class);

	        List<IdResponse> idResponses = response.getIdResponses();

	        if (CollectionUtils.isEmpty(idResponses))
	            throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

	        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	    }

}
