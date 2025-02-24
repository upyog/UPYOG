package org.upyog.sv.repository.impl;

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
import org.upyog.sv.web.models.PersisterWrapper;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingDraftDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

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

	@Override
	public void renew(StreetVendingRequest vendingRequest) {
		log.info("Renewing street vending booking application for new and old booking number: "
				+ vendingRequest.getStreetVendingDetail().getApplicationNo() + "::"
				+ vendingRequest.getStreetVendingDetail().getOldApplicationNumber());
		producer.push(vendingConfiguration.getStreetVendingApplicationRenewTopic(), vendingRequest);
	}

}
