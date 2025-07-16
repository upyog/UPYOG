package org.egov.waterquality.service;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.waterquality.web.models.collection.SearchCriteria;
import org.egov.waterquality.web.models.collection.WaterQuality;
import org.egov.waterquality.web.models.collection.WaterQualityRequest;

public interface WaterQualityService {

	List<WaterQuality> search(SearchCriteria criteria, RequestInfo requestInfo);

	List<WaterQuality> createWaterQualityApplication(@Valid WaterQualityRequest waterQualityApplicationRequest);

}