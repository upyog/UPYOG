package org.egov.ptr.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.web.contracts.IdGenerationRequest;
import org.egov.ptr.web.contracts.IdGenerationResponse;
import org.egov.ptr.web.contracts.IdRequest;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Repository class for interacting with the ID generation service.
 * This class is responsible for generating unique IDs based on given parameters.
 */
@Repository
public class IdGenRepository {

	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Autowired
	private RestTemplate restTemplate;


	/**
	 * Generates unique IDs by calling the external ID generation service.
	 *
	 * @param requestInfo The request information containing metadata.
	 * @param tenantId    The tenant ID for which the ID is generated.
	 * @param name        The name of the ID series.
	 * @param format      The format of the ID.
	 * @param count       The number of IDs to generate.
	 * @return IdGenerationResponse containing the generated IDs.
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
