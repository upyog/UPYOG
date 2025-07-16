package org.egov.waterquality.repository;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.waterquality.web.models.collection.SearchCriteria;
import org.egov.waterquality.web.models.collection.WaterQuality;
import org.egov.waterquality.web.models.collection.WaterQualityRequest;

public interface WaterDao {
	void saveWaterQualityApplication(WaterQualityRequest waterConnectionRequest);

	List<WaterQuality> getWaterQualityApplicationList(SearchCriteria criteria,RequestInfo requestInfo);
	
}
