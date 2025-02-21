package org.upyog.cdwm.repository;

import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.CNDServiceSearchCriteria;


import java.util.List;

public interface CNDServiceRepository {

	void saveCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest);

	List<CNDApplicationDetail> getCNDApplicationDetail(CNDServiceSearchCriteria cndServiceSearchCriteria);

	Integer getCNDApplicationsCount(CNDServiceSearchCriteria criteria);

	void updateCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest);

}
