package org.upyog.sv.repository;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

import lombok.NonNull;

public interface StreetVendingRepository {
	void save(StreetVendingRequest streetVendingRequest);

	List<StreetVendingDetail> getStreetVendingApplications(StreetVendingSearchCriteria streetVendingSearchCriteria);

	void update(StreetVendingRequest vendingRequest);

	Integer getApplicationsCount(StreetVendingSearchCriteria criteria);

	void saveDraftApplication(StreetVendingRequest vendingRequest);

	List<StreetVendingDetail> getStreetVendingDraftApplications(@NonNull RequestInfo requestInfo, 
			@Valid StreetVendingSearchCriteria streetVendingSearchCriteria);
}
