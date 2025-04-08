package org.egov.garbageservice.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.egov.garbageservice.model.GrbgBillTrackerSearchCriteria;
import org.egov.garbageservice.repository.rowmapper.GrbgBillTrackerRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

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

	private static final String GRBG_BILL_TRACKER_SEARCH_QUERY = "SELECT * FROM eg_grbg_bill_tracker egbt";

	private static final String INSERT_BILL_TRACKER = "INSERT INTO eg_grbg_bill_tracker (uuid, grbg_application_id, tenant_id, month, year, from_date, "
			+ "to_date, grbg_bill_amount, created_by, created_time, last_modified_by, last_modified_time,ward,bill_id) VALUES "
			+ "(:uuid, :grbgApplicationId, :tenantId, :month, :year, :fromDate, :toDate, :grbgBillAmount, :createdBy, :createdDate, :lastModifiedBy, :lastModifiedDate,:ward,:billId)";

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

		namedParameterJdbcTemplate.update(INSERT_BILL_TRACKER, billTrackerInputs);

		return grbgBillTracker;
	}

	public List<GrbgBillTracker> getBillTracker(GrbgBillTrackerSearchCriteria grbgBillTrackerSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = getBillTrackerSearchQuery(grbgBillTrackerSearchCriteria, preparedStmtList);
		List<GrbgBillTracker> grbgBillTrackers = jdbcTemplate.query(query, preparedStmtList.toArray(),
				grbgBillTrackerRowMapper);
		return grbgBillTrackers;
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

		return builder.toString();
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
}
