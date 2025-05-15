package org.upyog.sv.repository.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.kafka.producer.Producer;
import org.upyog.sv.repository.StreetVendingRepository;
import org.upyog.sv.repository.querybuilder.StreetVendingQueryBuilder;
import org.upyog.sv.repository.rowmapper.StreetVendingApplicationRowMapper;
import org.upyog.sv.repository.rowmapper.StreetVendingDraftApplicationRowMapper;
import org.upyog.sv.repository.rowmapper.VendorPaymentScheduleRowMapper;
import org.upyog.sv.web.models.PaymentScheduleStatus;
import org.upyog.sv.web.models.PersisterWrapper;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingDraftDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;
import org.upyog.sv.web.models.VendorPaymentSchedule;
import org.upyog.sv.web.models.VendorPaymentScheduleRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class StreetVendingRepositoryImpl implements StreetVendingRepository {

	@Autowired
	private Producer producer;
	@Autowired
	private StreetVendingConfiguration vendingConfiguration;

	@Autowired
	private StreetVendingQueryBuilder queryBuilder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private StreetVendingApplicationRowMapper rowMapper;

	@Autowired
	private StreetVendingDraftApplicationRowMapper draftApplicationRowMapper;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void save(StreetVendingRequest streetVendingRequest) {
		log.info("Saving street vending booking request data for booking no : "
				+ streetVendingRequest.getStreetVendingDetail().getApplicationNo());
		producer.push(vendingConfiguration.getStreetVendingApplicationSaveTopic(), streetVendingRequest);
	}

	@Override
	public List<StreetVendingDetail> getStreetVendingApplications(
			StreetVendingSearchCriteria streetVendingSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getStreetVendingSearchQuery(streetVendingSearchCriteria, preparedStmtList);
		log.info("Final query for getStreetVendingApplications {} and paramsList {} : ", preparedStmtList);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);

	}

	@Override
	public Integer getApplicationsCount(StreetVendingSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getStreetVendingSearchQuery(criteria, preparedStatement);

		if (query == null)
			return 0;

		log.info("Final query for getStreetVendingApplications {} and paramsList {} : ", preparedStatement);

		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
		return count;
	}

	@Override
	public void update(StreetVendingRequest vendingRequest) {
		log.info("Updating street vending request data for booking no : "
				+ vendingRequest.getStreetVendingDetail().getApplicationNo());
		producer.push(vendingConfiguration.getStreetVendingApplicationUpdateTopic(), vendingRequest);

	}

	@Override
	public void saveDraftApplication(StreetVendingRequest vendingRequest) {
		/*
		 * String sql = "INSERT INTO eg_sv_street_vending_draft_detail(\n" +
		 * "	draft_id, tenant_id, user_uuid, draft_application_data, createdby, lastmodifiedby, createdtime, lastmodifiedtime)\n"
		 * + "	VALUES (?, ?, ?, cast(? as jsonb), ?, ?, ?, ?)"; try {
		 * 
		 * int result = jdbcTemplate.update(sql,
		 * vendingRequest.getStreetVendingDetail().getDraftId(),
		 * vendingRequest.getStreetVendingDetail().getTenantId(),
		 * vendingRequest.getRequestInfo().getUserInfo().getUuid() ,
		 * draftApplicationData,
		 * vendingRequest.getStreetVendingDetail().getAuditDetails().getCreatedBy(),
		 * vendingRequest.getStreetVendingDetail().getAuditDetails().getLastModifiedBy()
		 * , vendingRequest.getStreetVendingDetail().getAuditDetails().getCreatedTime(),
		 * vendingRequest.getStreetVendingDetail().getAuditDetails().getLastModifiedTime
		 * ()); log.info("result : " + result); } catch (JsonProcessingException e) {
		 * throw new RuntimeException("Failed to convert application details to JSON",
		 * e); }
		 */
		StreetVendingDraftDetail streetVendingDraftDetail = convertToDraftDetailsObject(vendingRequest);
		PersisterWrapper<StreetVendingDraftDetail> persisterWrapper = new PersisterWrapper<StreetVendingDraftDetail>(
				streetVendingDraftDetail);
		producer.push(vendingConfiguration.getStreetVendingDraftApplicationSaveTopic(), persisterWrapper);
	}

	@Override
	public List<StreetVendingDetail> getStreetVendingDraftApplications(@NonNull RequestInfo requestInfo,
			@Valid StreetVendingSearchCriteria streetVendingSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = "SELECT draft_id, draft_application_data FROM eg_sv_street_vending_draft_detail where user_uuid = ? and tenant_id = ?";
		preparedStmtList.add(requestInfo.getUserInfo().getUuid());
		preparedStmtList.add(streetVendingSearchCriteria.getTenantId());

		log.info("Final query for getStreetVendingApplications {} and paramsList {} : ", preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), draftApplicationRowMapper);
	}

	@Override
	public void updateDraftApplication(StreetVendingRequest vendingRequest) {
		StreetVendingDraftDetail streetVendingDraftDetail = convertToDraftDetailsObject(vendingRequest);
		PersisterWrapper<StreetVendingDraftDetail> persisterWrapper = new PersisterWrapper<StreetVendingDraftDetail>(
				streetVendingDraftDetail);
		producer.push(vendingConfiguration.getStreetVendingDraftApplicationUpdateTopic(), persisterWrapper);

	}

	public void deleteDraftApplication(String draftId) {
		StreetVendingDraftDetail streetVendingDraftDetail = StreetVendingDraftDetail.builder().draftId(draftId).build();

		PersisterWrapper<StreetVendingDraftDetail> persisterWrapper = new PersisterWrapper<StreetVendingDraftDetail>(
				streetVendingDraftDetail);
		producer.push(vendingConfiguration.getStreetVendingDraftApplicationDeleteTopic(), persisterWrapper);

	}

	private StreetVendingDraftDetail convertToDraftDetailsObject(StreetVendingRequest vendingRequest) {
		StreetVendingDetail streetVendingDetail = vendingRequest.getStreetVendingDetail();
		String draftApplicationData = null;
		try {
			draftApplicationData = objectMapper.writeValueAsString(vendingRequest.getStreetVendingDetail());
		} catch (JsonProcessingException e) {
			log.error("Serialization error for StreetVendingDraftDetail with ID: {} and Tenant: {}",
					vendingRequest.getStreetVendingDetail().getDraftId(),
					vendingRequest.getStreetVendingDetail().getTenantId(), e);

		}
		StreetVendingDraftDetail streetVendingDraftDetail = StreetVendingDraftDetail.builder()
				.draftId(streetVendingDetail.getDraftId()).tenantId(streetVendingDetail.getTenantId())
				.userUuid(vendingRequest.getRequestInfo().getUserInfo().getUuid())
				.draftApplicationData(draftApplicationData).auditDetails(streetVendingDetail.getAuditDetails()).build();
		return streetVendingDraftDetail;
	}
	
	/**
	 * Saves the vendor payment schedule by publishing the schedule request
	 * to the configured message queue topic.
	 *
	 * @param scheduleRequest the request object containing vendor payment schedule details
	 */
	
	@Override
	public void savePaymentSchedule(VendorPaymentScheduleRequest scheduleRequest) {
		
		producer.push(vendingConfiguration.getStreetVendingPaymentScheduleSaveTopic(), scheduleRequest);
	}

	/**
	 * Retrieves a list of vendor payment schedules that match the given due date and status.
	 *
	 * @param dueDate the due date of the payment schedules to retrieve
	 * @param status the status of the payment schedules to retrieve
	 * @return a list of {@link VendorPaymentSchedule} objects matching the specified criteria
	 */
	
	@Override
	public List<VendorPaymentSchedule> getVendorPayScheduleForDueDateAndStatus(LocalDate dueDate, PaymentScheduleStatus status) {
	    String query = StreetVendingQueryBuilder.PAYMENT_SCHEDULE;

	    return jdbcTemplate.query(query, new Object[] { dueDate, status.toString() }, new VendorPaymentScheduleRowMapper());
	}
	
	/**
	 * Updates an existing vendor payment schedule by publishing the update request
	 * to the configured message queue topic.
	 *
	 * @param schedule the request object containing the updated vendor payment schedule details
	 */

	@Override
	public void updatePaymentSchedule(VendorPaymentScheduleRequest schedule) {
		
		producer.push(vendingConfiguration.getStreetVendingPaymentScheduleUpdateTopic(), schedule);
		
	}
	
	/**
	 * Retrieves a list of vendor payment schedules based on the provided application number
	 * and payment schedule status.
	 *
	 * @param applicationNo the unique identifier of the application
	 * @param status the status of the payment schedules to filter by
	 * @return a list of {@link VendorPaymentSchedule} objects matching the application number and status
	 */

	@Override
	public List<VendorPaymentSchedule> getVendorPaymentScheduleApplication(String applicationNo, PaymentScheduleStatus status) {
		   String query = StreetVendingQueryBuilder.VENDOR_PAYMENT_SCHEDULE;

		   return jdbcTemplate.query(query, new Object[] { applicationNo, status.toString() }, new VendorPaymentScheduleRowMapper());
	}
	
	/**
	 * Checks if a scheduled payment is pending for the given application number and status.
	 *
	 * @param applicationNo the application number of the vendor
	 * @param status the payment schedule status to check against
	 * @return {@code true} if there is at least one pending scheduled payment for the given application,
	 *         {@code false} otherwise
	 */
	
	@Override
	public boolean isSchedulePaymentPending(String applicationNo, PaymentScheduleStatus status) {
	    String query = StreetVendingQueryBuilder.VENDOR_PAYMENT_SCHEDULE;

	    List<Object> result = jdbcTemplate.query(query, new Object[]{applicationNo, status.toString()},
	            (rs, rowNum) -> new Object());

	    return !result.isEmpty();
	}

	
}
