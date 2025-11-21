package org.egov.ewst.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ewst.web.contracts.IdGenerationRequest;
import org.egov.ewst.web.contracts.IdGenerationResponse;
import org.egov.ewst.web.contracts.IdRequest;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Repository
public class IdGenRepository {

	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Generates a list of unique IDs using the ID generation service.
	 *
	 * @param requestInfo The request information containing details about the request.
	 * @param tenantId   The tenant ID for which the IDs are to be generated.
	 * @param name       The name of the ID to be generated.
	 * @param format     The format of the ID to be generated.
	 * @param count      The number of IDs to be generated.
	 * @return An IdGenerationResponse object containing the generated IDs.
	 */
	public IdGenerationResponse getId(RequestInfo requestInfo, String tenantId, String name, String format, int count) {

		List<IdRequest> reqList = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			reqList.add(IdRequest.builder().idName(name).format(format).tenantId(tenantId).build());
		}

		IdGenerationRequest req = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
		IdGenerationResponse response = null;

		try {

			response = restTemplate.postForObject(idGenHost + idGenPath, req, IdGenerationResponse.class);
		} catch (HttpClientErrorException e) {

			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {

			Map<String, String> map = new HashMap<>();
			map.put(e.getCause().getClass().getName(), e.getMessage());
			throw new CustomException(map);
		}

		return response;
	}

}
