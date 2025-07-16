package org.egov.waterquality.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.waterquality.config.WSConfiguration;
import org.egov.waterquality.constants.WCConstants;
import org.egov.waterquality.repository.IdGenRepository;
import org.egov.waterquality.web.models.Idgen.IdResponse;
import org.egov.waterquality.web.models.collection.ApplicationType;
import org.egov.waterquality.web.models.collection.WaterQuality;
import org.egov.waterquality.web.models.collection.WaterQualityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class EnrichmentService {
	
	@Autowired
	private WSConfiguration config;
	
	@Autowired
	private IdGenRepository idGenRepository;
	
	public void enrichCreateRequest(WaterQualityRequest waterQualityRequest) {
		WaterQuality application = new WaterQuality();
		if(waterQualityRequest != null) {
			application = waterQualityRequest.getWaterQualityApplication();
		}
		Long time = System.currentTimeMillis();
		String by = waterQualityRequest.getRequestInfo().getUserInfo().getUuid();
		
		application.setId(UUID.randomUUID().toString());
		application.setApplicationNo(null);
		application.setCreatedBy(by);
		application.setCreatedTime(time);
		setApplicationIdGenIds(waterQualityRequest);
	}
	
	private void setApplicationIdGenIds(WaterQualityRequest request) {
		WaterQuality waterQuality = request.getWaterQualityApplication();
		List<String> applicationNumbers = new ArrayList<>();
		if(request.getWaterQualityApplication().getType()==ApplicationType.WATER_SAMPLE) {
			applicationNumbers = getIdList(request.getRequestInfo(),
					request.getWaterQualityApplication().getTenantId(), config.getWaterQualitySampleIdGenName(),
					config.getWaterQualitySampleIdGenFormat());
		} else {
			applicationNumbers = getIdList(request.getRequestInfo(),
					request.getWaterQualityApplication().getTenantId(), config.getWaterQualityTestIdGenName(),
					config.getWaterQualityTestIdGenFormat());
		}
		waterQuality.setApplicationNo(applicationNumbers.get(0));
	}

	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idFormat) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idFormat, 1)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException(WCConstants.IDGEN_ERROR_CONST, "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

}
