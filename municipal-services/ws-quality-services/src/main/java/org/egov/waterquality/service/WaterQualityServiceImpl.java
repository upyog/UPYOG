package org.egov.waterquality.service;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.waterquality.repository.WaterDao;
import org.egov.waterquality.web.models.collection.SearchCriteria;
import org.egov.waterquality.web.models.collection.WaterQuality;
import org.egov.waterquality.web.models.collection.WaterQualityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.validation.Valid;

@Slf4j
@Component
public class WaterQualityServiceImpl implements WaterQualityService {

	@Autowired
	private WaterDao waterDao;

	@Autowired
	private EnrichmentService enrichmentService;

	@Override
	public List<WaterQuality> createWaterQualityApplication(@Valid WaterQualityRequest waterQualityApplicationRequest) {
		enrichmentService.enrichCreateRequest(waterQualityApplicationRequest);
		waterDao.saveWaterQualityApplication(waterQualityApplicationRequest);
		return Arrays.asList(waterQualityApplicationRequest.getWaterQualityApplication());
	}


	@Override
	public List<WaterQuality> search(SearchCriteria criteria, RequestInfo requestInfo) {
		// TODO Auto-generated method stub
		return null;
	}

}