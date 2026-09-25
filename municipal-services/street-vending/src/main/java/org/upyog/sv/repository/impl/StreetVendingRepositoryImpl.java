package org.upyog.sv.repository.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

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
@SuppressWarnings("java:S1874")
public class StreetVendingRepositoryImpl implements StreetVendingRepository {

	private static final String FINAL_QUERY_LOG_MESSAGE = "Final query for getStreetVendingApplications {} and paramsList {} : ";

	private final Producer producer;
	private final StreetVendingConfiguration vendingConfiguration;
	private final StreetVendingQueryBuilder queryBuilder;
	private final JdbcTemplate jdbcTemplate;
	private final StreetVendingApplicationRowMapper rowMapper;
	private final StreetVendingDraftApplicationRowMapper draftApplicationRowMapper;
	private final ObjectMapper objectMapper;

	@Autowired
	public StreetVendingRepositoryImpl(Producer producer, StreetVendingConfiguration vendingConfiguration,
			StreetVendingQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate,
			StreetVendingApplicationRowMapper rowMapper,
			StreetVendingDraftApplicationRowMapper draftApplicationRowMapper, ObjectMapper objectMapper) {
		this.producer = producer;
		this.vendingConfiguration = vendingConfiguration;
		this.queryBuilder = queryBuilder;
		this.jdbcTemplate = jdbcTemplate;
		this.rowMapper = rowMapper;
		this.draftApplicationRowMapper = draftApplicationRowMapper;
		this.objectMapper = objectMapper;
	}

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
		log.info(FINAL_QUERY_LOG_MESSAGE, preparedStmtList);
		return jdbcTemplate.query(query, rowMapper, preparedStmtList.toArray());

	}

	@Override
	public Integer getApplicationsCount(StreetVendingSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getStreetVendingSearchQuery(criteria, preparedStatement);

		if (query == null)
			return 0;

		log.info(FINAL_QUERY_LOG_MESSAGE, preparedStatement);

		return jdbcTemplate.queryForObject(query, Integer.class, preparedStatement.toArray());
	}

	@Override
	public void update(StreetVendingRequest vendingRequest) {
		log.info("Updating street vending request data for booking no : "
				+ vendingRequest.getStreetVendingDetail().getApplicationNo());
		producer.push(vendingConfiguration.getStreetVendingApplicationUpdateTopic(), vendingRequest);

	}

	@Override
	public void saveDraftApplication(StreetVendingRequest vendingRequest) {
		StreetVendingDraftDetail streetVendingDraftDetail = convertToDraftDetailsObject(vendingRequest);
		PersisterWrapper<StreetVendingDraftDetail> persisterWrapper = new PersisterWrapper<>(streetVendingDraftDetail);
		producer.push(vendingConfiguration.getStreetVendingDraftApplicationSaveTopic(), persisterWrapper);
	}

	@Override
	public List<StreetVendingDetail> getStreetVendingDraftApplications(@NonNull RequestInfo requestInfo,
			@Valid StreetVendingSearchCriteria streetVendingSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = "SELECT draft_id, draft_application_data FROM eg_sv_street_vending_draft_detail where user_uuid = ? and tenant_id = ?";
		preparedStmtList.add(requestInfo.getUserInfo().getUuid());
		preparedStmtList.add(streetVendingSearchCriteria.getTenantId());

		log.info(FINAL_QUERY_LOG_MESSAGE, preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, draftApplicationRowMapper, preparedStmtList.toArray());
	}

	@Override
	public void updateDraftApplication(StreetVendingRequest vendingRequest) {
		StreetVendingDraftDetail streetVendingDraftDetail = convertToDraftDetailsObject(vendingRequest);
		PersisterWrapper<StreetVendingDraftDetail> persisterWrapper = new PersisterWrapper<>(streetVendingDraftDetail);
		producer.push(vendingConfiguration.getStreetVendingDraftApplicationUpdateTopic(), persisterWrapper);

	}

	public void deleteDraftApplication(String draftId) {
		StreetVendingDraftDetail streetVendingDraftDetail = StreetVendingDraftDetail.builder().draftId(draftId).build();

		PersisterWrapper<StreetVendingDraftDetail> persisterWrapper = new PersisterWrapper<>(streetVendingDraftDetail);
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
		return StreetVendingDraftDetail.builder()
				.draftId(streetVendingDetail.getDraftId()).tenantId(streetVendingDetail.getTenantId())
				.userUuid(vendingRequest.getRequestInfo().getUserInfo().getUuid())
				.draftApplicationData(draftApplicationData).auditDetails(streetVendingDetail.getAuditDetails()).build();
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

	    return jdbcTemplate.query(query, new VendorPaymentScheduleRowMapper(), dueDate, status.toString());
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

		   return jdbcTemplate.query(query, new VendorPaymentScheduleRowMapper(), applicationNo, status.toString());
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

	    List<Object> result = jdbcTemplate.query(query, (rs, rowNum) -> new Object(), applicationNo, status.toString());

	    return !result.isEmpty();
	}

	
}
