package org.egov.garbageservice.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.model.GrbgBillFailure;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.egov.garbageservice.model.GrbgBillTrackerSearchCriteria;
import org.egov.garbageservice.repository.rowmapper.GrbgBillTrackerRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GarbageBillTrackerRepository {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private GrbgBillTrackerRowMapper grbgBillTrackerRowMapper;
	
	@Autowired
	private ObjectMapper objectMapper;

	private static final String GRBG_BILL_TRACKER_SEARCH_QUERY = "SELECT * FROM eg_grbg_bill_tracker egbt";

	private static final String INSERT_BILL_TRACKER = "INSERT INTO eg_grbg_bill_tracker (uuid, grbg_application_id, tenant_id, month, year, from_date, "
			+ "to_date, grbg_bill_amount, created_by, created_time, last_modified_by, last_modified_time,ward,bill_id,type,additionaldetail) VALUES "
			+ "(:uuid, :grbgApplicationId, :tenantId, :month, :year, :fromDate, :toDate, :grbgBillAmount, :createdBy, :createdDate, :lastModifiedBy, :lastModifiedDate,:ward,:billId,:type,:additionaldetail::JSONB)";

//	private static final String INSERT_BILL_FAILURE = "INSERT INTO eg_bill_failure (id, consumer_code, module_name, tenant_id, failure_reason,month, year, from_date, "
//			+ "to_date, request_payload, response_payload, status_code) VALUES "
//			+ "(:id, :consumer_code,:module_name, :tenant_id, failure_reason,:month, :year, :from_date, :to_date, :request_payload, :response_payload, :status_code)";
//	
	private static final String INSERT_BILL_FAILURE = "INSERT INTO eg_bill_failure (id, consumer_code,module_name,tenant_id, failure_reason,month,year,from_date,to_date,request_payload,response_payload,error_json,status_code,created_time,last_modified_time)"
			+ "VALUES ("
			+ "    :id,"
			+ "    :consumer_code,"
			+ "    :module_name,"
			+ "    :tenant_id,"
			+ "    :failure_reason,"
			+ "    :month,"
			+ "    :year,"
			+ "    :from_date,"
			+ "    :to_date,"
			+ "    :request_payload :: JSONB,"
			+ "    :response_payload :: JSONB,"
			+ "	   :error_json 	 	 :: JSONB,"
			+ "    :status_code,"
			+ "    :created_time,"
			+ "    :last_modified_time"
			+ ")"
			+ "ON CONFLICT (consumer_code, from_date, to_date)"
			+ "DO UPDATE SET"
			+ "    module_name = EXCLUDED.module_name,"
			+ "    tenant_id = EXCLUDED.tenant_id,"
			+ "    failure_reason = EXCLUDED.failure_reason,month = EXCLUDED.month,year = EXCLUDED.year,"
			+ "    request_payload = EXCLUDED.request_payload :: JSONB,"
			+ "    response_payload = EXCLUDED.response_payload :: JSONB,"
			+ "    status_code = EXCLUDED.status_code,"
			+ "    last_modified_time = EXCLUDED.last_modified_time;";
	
	private static final String DELETE_BILL_FAILURE = "DELETE FROM eg_bill_failure"
			+ " WHERE consumer_code = :consumer_code"
			+ "  AND from_date = :from_date"
			+ "  AND to_date = :to_date;";
	
	public GrbgBillTracker createTracker(GrbgBillTracker grbgBillTracker) {

		Map<String, Object> billTrackerInputs = new HashMap<>();
		billTrackerInputs.put("uuid", grbgBillTracker.getUuid());
		billTrackerInputs.put("grbgApplicationId", grbgBillTracker.getGrbgApplicationId());
		billTrackerInputs.put("tenantId", grbgBillTracker.getTenantId());
		billTrackerInputs.put("month", grbgBillTracker.getMonth());
		billTrackerInputs.put("year", grbgBillTracker.getYear());
		billTrackerInputs.put("fromDate", grbgBillTracker.getFromDate());
		billTrackerInputs.put("toDate", grbgBillTracker.getToDate());
		billTrackerInputs.put("grbgBillAmount", grbgBillTracker.getGrbgBillAmount());
		billTrackerInputs.put("billId", grbgBillTracker.getBillId());
		billTrackerInputs.put("ward", grbgBillTracker.getWard());
		billTrackerInputs.put("createdBy", grbgBillTracker.getAuditDetails().getCreatedBy());
		billTrackerInputs.put("createdDate", grbgBillTracker.getAuditDetails().getCreatedDate());
		billTrackerInputs.put("lastModifiedBy", grbgBillTracker.getAuditDetails().getLastModifiedBy());
		billTrackerInputs.put("lastModifiedDate", grbgBillTracker.getAuditDetails().getLastModifiedDate());
		billTrackerInputs.put("type", grbgBillTracker.getType());
		billTrackerInputs.put("additionaldetail", grbgBillTracker.getAdditionaldetail().isNull() ? null : grbgBillTracker.getAdditionaldetail().toString());


		namedParameterJdbcTemplate.update(INSERT_BILL_TRACKER, billTrackerInputs);

		return grbgBillTracker;
	}
	
	public GrbgBillFailure createBillFailure(GrbgBillFailure GrbgBillFailureReq) {
		Map<String, Object> billFailureInputs = new HashMap<>();
		billFailureInputs.put("id", GrbgBillFailureReq.getId());
		billFailureInputs.put("consumer_code", GrbgBillFailureReq.getConsumer_code());
		billFailureInputs.put("module_name", GrbgBillFailureReq.getModule_name());
		billFailureInputs.put("failure_reason", GrbgBillFailureReq.getFailure_reason());
		billFailureInputs.put("tenant_id", GrbgBillFailureReq.getTenant_id());
		billFailureInputs.put("month", GrbgBillFailureReq.getMonth());
		billFailureInputs.put("year", GrbgBillFailureReq.getYear());
		billFailureInputs.put("from_date", GrbgBillFailureReq.getFrom_date());
		billFailureInputs.put("to_date", GrbgBillFailureReq.getTo_date());
//		billTrackerInputs.put("ward", GrbgBillFailureReq.getWard());
		billFailureInputs.put("error_json", GrbgBillFailureReq.getError_json().isNull() ? null : objectMapper.convertValue(GrbgBillFailureReq.getError_json(), ObjectNode.class).toString());
		billFailureInputs.put("response_payload", GrbgBillFailureReq.getResponse_payload().isNull() ? null : objectMapper.convertValue(GrbgBillFailureReq.getResponse_payload(), ObjectNode.class).toString());
		billFailureInputs.put("request_payload", GrbgBillFailureReq.getRequest_payload().isNull() ? null : objectMapper.convertValue(GrbgBillFailureReq.getRequest_payload(), ObjectNode.class).toString());
		billFailureInputs.put("status_code", GrbgBillFailureReq.getStatus_code());
		billFailureInputs.put("module_name", GrbgBillFailureReq.getModule_name());
		billFailureInputs.put("created_time",GrbgBillFailureReq.getCreated_time());
		billFailureInputs.put("last_modified_time",GrbgBillFailureReq.getLast_modified_time());

		namedParameterJdbcTemplate.update(INSERT_BILL_FAILURE, billFailureInputs);
		return GrbgBillFailureReq;
	}

	public List<GrbgBillTracker> getBillTracker(GrbgBillTrackerSearchCriteria grbgBillTrackerSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = getBillTrackerSearchQuery(grbgBillTrackerSearchCriteria, preparedStmtList);
		List<GrbgBillTracker> grbgBillTrackers = jdbcTemplate.query(query, preparedStmtList.toArray(),
				grbgBillTrackerRowMapper);
		return grbgBillTrackers;
	}
	
	public String getLimitAndOrderByUpdatedTimeDesc(GrbgBillTrackerSearchCriteria criteria, String query,
			List<Object> preparedStmtList) {
		StringBuilder queryBuilder = new StringBuilder(query);
		if (null != criteria.getLimit()) {
			queryBuilder.append(" order by egbt.last_modified_time desc ");
			queryBuilder.append(" limit ? ");
			preparedStmtList.add(criteria.getLimit());
		}

		return queryBuilder.toString();
	}

	private String getBillTrackerSearchQuery(GrbgBillTrackerSearchCriteria criteria, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(GRBG_BILL_TRACKER_SEARCH_QUERY);

		builder.append(" WHERE 1 = 1 ");

		if (!StringUtils.isEmpty(criteria.getTenantId())) {
			String tenantId = criteria.getTenantId();

			String[] tenantIdChunks = tenantId.split("\\.");

			if (tenantIdChunks.length == 1) {
				andClauseIfRequired(preparedStmtList, builder);
				builder.append(" egbt.tenant_id LIKE ? ");
				preparedStmtList.add(criteria.getTenantId() + '%');
			} else {
				andClauseIfRequired(preparedStmtList, builder);
				builder.append(" egbt.tenant_id=? ");
				preparedStmtList.add(criteria.getTenantId());
			}
		}

		if (!CollectionUtils.isEmpty(criteria.getTenantIds())) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" egbt.tenant_id IN (").append(createQuery(criteria.getTenantIds())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getTenantIds());
		}
		if (!CollectionUtils.isEmpty(criteria.getUuids())) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" egbt.uuid IN (").append(createQuery(criteria.getUuids())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getUuids());
		}
		if (!CollectionUtils.isEmpty(criteria.getGrbgApplicationIds())) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" egbt.grbg_application_id IN (").append(createQuery(criteria.getGrbgApplicationIds()))
					.append(")");
			addToPreparedStatement(preparedStmtList, criteria.getGrbgApplicationIds());
		}
		if (!CollectionUtils.isEmpty(criteria.getGrbgApplicationIds())) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" egbt.grbg_application_id IN (").append(createQuery(criteria.getGrbgApplicationIds()))
					.append(")");
			addToPreparedStatement(preparedStmtList, criteria.getGrbgApplicationIds());
		}
		if (!StringUtils.isEmpty(criteria.getType())) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" egbt.type =?");
			preparedStmtList.add(criteria.getType());
		}
		
		String Query = getLimitAndOrderByUpdatedTimeDesc(criteria,builder.toString(),preparedStmtList);
		
		return Query;
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, Set<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});
	}

	private String createQuery(Set<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	private static void andClauseIfRequired(List<Object> values, StringBuilder queryString) {
		queryString.append(" AND");
	}
	
	public void removeBillFailure(GrbgBillFailure grbgBillFailureRequest) {
		Map<String, Object> params = new HashMap<>();
		params.put("consumer_code", grbgBillFailureRequest.getConsumer_code());
		params.put("from_date", grbgBillFailureRequest.getFrom_date());
		params.put("to_date", grbgBillFailureRequest.getTo_date());
		namedParameterJdbcTemplate.update(DELETE_BILL_FAILURE, params);
	}
}
