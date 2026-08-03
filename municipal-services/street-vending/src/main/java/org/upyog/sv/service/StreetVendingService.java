package org.upyog.sv.service;

import java.util.List;

import jakarta.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

import jakarta.validation.constraints.NotNull;

public interface StreetVendingService {

	public StreetVendingDetail createStreetVendingApplication(StreetVendingRequest vendingRequest);

	public List<StreetVendingDetail> getStreetVendingDetails(RequestInfo requestInfo,
			StreetVendingSearchCriteria streetVendingSearchCriteria);

	public StreetVendingDetail updateStreetVendingApplication(StreetVendingRequest vendingRequest);

	public Integer getApplicationsCount(StreetVendingSearchCriteria streetVendingSearchCriteria,
			RequestInfo requestInfo);

	public StreetVendingDetail createStreetVendingDraftApplication(StreetVendingRequest vendingRequest);

	public List<StreetVendingDetail> getStreetVendingDraftApplicationDetails(@NotNull RequestInfo requestInfo,
			@Valid StreetVendingSearchCriteria streetVendingSearchCriteria);

	public String deleteStreetVendingDraft(String draftId);

}
