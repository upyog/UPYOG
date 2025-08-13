package org.upyog.sv.repository;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.sv.web.models.PaymentScheduleStatus;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;
import org.upyog.sv.web.models.VendorPaymentSchedule;
import org.upyog.sv.web.models.VendorPaymentScheduleRequest;

import lombok.NonNull;

public interface StreetVendingRepository {
	void save(StreetVendingRequest streetVendingRequest);

	List<StreetVendingDetail> getStreetVendingApplications(StreetVendingSearchCriteria streetVendingSearchCriteria);

	void update(StreetVendingRequest vendingRequest);

	Integer getApplicationsCount(StreetVendingSearchCriteria criteria);

	void saveDraftApplication(StreetVendingRequest vendingRequest);

	List<StreetVendingDetail> getStreetVendingDraftApplications(@NonNull RequestInfo requestInfo, 
			@Valid StreetVendingSearchCriteria streetVendingSearchCriteria);

	void updateDraftApplication(StreetVendingRequest vendingRequest);

	void deleteDraftApplication(String draftId);
	
	void savePaymentSchedule(VendorPaymentScheduleRequest paymentSchedule);
	
    List<VendorPaymentSchedule> getVendorPayScheduleForDueDateAndStatus(LocalDate dueDate, PaymentScheduleStatus status);

	void updatePaymentSchedule(VendorPaymentScheduleRequest updateSchedule);

	List<VendorPaymentSchedule> getVendorPaymentScheduleApplication(String applicationNo, PaymentScheduleStatus status);
	
	boolean isSchedulePaymentPending(String applicationNo, PaymentScheduleStatus status);



}
