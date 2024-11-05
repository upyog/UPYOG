package org.upyog.sv.repository;

import java.time.LocalDate;
import java.util.List;

import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

public interface StreetVendingRepository {
	void save(StreetVendingRequest streetVendingRequest);
	List<StreetVendingDetail> getStreetVendingApplications(StreetVendingSearchCriteria streetVendingSearchCriteria);
	StreetVendingDetail getApplications(StreetVendingSearchCriteria criteria);
	void update(StreetVendingRequest vendingRequest);
	Integer getApplicationsCount(StreetVendingSearchCriteria criteria);
}
