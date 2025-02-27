package org.upyog.cdwm.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.CNDServiceSearchCriteria;

public interface CNDService {

    public CNDApplicationDetail createConstructionAndDemolitionRequest(CNDApplicationRequest cndApplicationRequest);

	List<CNDApplicationDetail> getCNDApplicationDetails(RequestInfo requestInfo,
			CNDServiceSearchCriteria cndServiceSearchCriteria);

	Integer getApplicationsCount(CNDServiceSearchCriteria cndServiceSearchCriteria, RequestInfo requestInfo);
}
