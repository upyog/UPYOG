package org.upyog.sv.service;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

import lombok.NonNull;

public interface StreetVendingService {

	public StreetVendingDetail createStreetVendingApplication(StreetVendingRequest vendingRequest);

	public List<StreetVendingDetail> getStreetVendingDetails(RequestInfo info,
			StreetVendingSearchCriteria streetVendingSearchCriteria);

	public StreetVendingDetail updateStreetVendingApplication(StreetVendingRequest vendingRequest);

	public Integer getApplicationsCount(StreetVendingSearchCriteria streetVendingSearchCriteria, RequestInfo requestInfo);

}
