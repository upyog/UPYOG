package org.egov.bpa.calculator.repository.querybuilder;

import java.util.List;

import org.egov.bpa.calculator.web.models.PreapprovedPlanSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class PreapprovedPlanQueryBuilder {

	private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";

	private static final String QUERY = "SELECT ebpap.id as id,ebpap.drawing_number as drawingNo,ebpap.tenantid as tenantId,ebpap.plot_length as plotLength,ebpap.plot_width as plotWidth,ebpap.road_width as roadWidth,ebpap.drawing_detail as drawingDetail,ebpap.active as active,ebpap.additional_details as additionalDetails,ebpap.createdby as createdBy,ebpap.lastmodifiedby as lastModifiedBy,ebpap.createdtime as createdTime,ebpap.lastmodifiedtime as lastModifiedTime"
			+ ", ebpapd.id as ebpapd_id, ebpapd.documenttype as ebpapd_documenttype, ebpapd.filestoreid as ebpapd_filestoreid, ebpapd.preapprovedplanid as ebpapd_preapprovedplanid, ebpapd.documentuid as ebpapd_documentuid, ebpapd.additionalDetails as ebpapd_additionalDetails, ebpapd.createdby as ebpapd_createdby, ebpapd.lastmodifiedby as ebpapd_lastmodifiedby, ebpapd.createdtime as ebpapd_createdtime, ebpapd.lastmodifiedtime as ebpapd_lastmodifiedtime "
			+ " FROM eg_bpa_preapprovedplan ebpap "
			+  LEFT_OUTER_JOIN_STRING
			+ " eg_bpa_preapprovedplan_documents ebpapd on ebpap.id=ebpapd.preapprovedplanid";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY lastModifiedTime DESC) offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";

	private final String countWrapper = "SELECT COUNT(DISTINCT(bpa_id)) FROM ({INTERNAL_QUERY}) as bpa_count";

	/**
	 *
	 */
	public String getPreapprovedPlanSearchQuery(PreapprovedPlanSearchCriteria criteria, List<Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(QUERY);

		if (criteria.getTenantId() != null) {
			if (criteria.getTenantId().split("\\.").length == 1) {

				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" ebpap.tenantid like ?");
				preparedStmtList.add('%' + criteria.getTenantId() + '%');
			} else {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" ebpap.tenantid=? ");
				preparedStmtList.add(criteria.getTenantId());
			}
		}

		if (criteria.getDrawingNo() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebpap.drawing_number=? ");
			preparedStmtList.add(criteria.getDrawingNo());
		}
		if (criteria.getPlotLength() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebpap.plot_length=? ");
			preparedStmtList.add(criteria.getPlotLength());
		}
		if (criteria.getPlotWidth() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebpap.plot_width=? ");
			preparedStmtList.add(criteria.getPlotWidth());
		}
		if (criteria.getRoadWidth() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebpap.road_width=? ");
			preparedStmtList.add(criteria.getRoadWidth());
		}
		if (criteria.getActive() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebpap.active=? ");
			preparedStmtList.add(criteria.getActive());
		}
		if (criteria.getCreatedBy() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebpap.createdby=? ");
			preparedStmtList.add(criteria.getCreatedBy());
		}

		List<String> ids = criteria.getIds();
		if (!CollectionUtils.isEmpty(ids)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebpap.id IN (").append(createQuery(ids)).append(")");
			addToPreparedStatement(preparedStmtList, ids);
		}

		if (criteria.getFromDate() != null && criteria.getToDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebpap.createdtime BETWEEN ").append(criteria.getFromDate()).append(" AND ")
					.append(criteria.getToDate());
		} else if (criteria.getFromDate() != null && criteria.getToDate() == null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ebpap.createdtime >= ").append(criteria.getFromDate());
		}

		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);

	}

	/**
	 * 
	 * @param query            prepared Query
	 * @param preparedStmtList values to be replased on the query
	 * @param criteria         bpa search criteria
	 * @return the query by replacing the placeholders with preparedStmtList
	 */
	private String addPaginationWrapper(String query, List<Object> preparedStmtList,
			PreapprovedPlanSearchCriteria criteria) {

		int limit = 10;
		int offset = 0;
		int maxLimit = 1000;
		String finalQuery = paginationWrapper.replace("{}", query);

		if (criteria.getLimit() == null && criteria.getOffset() == null) {
			limit = maxLimit;
		}

		if (criteria.getLimit() != null && criteria.getLimit() <= maxLimit)
			limit = criteria.getLimit();

		if (criteria.getLimit() != null && criteria.getLimit() > maxLimit) {
			limit = maxLimit;
		}

		if (criteria.getOffset() != null)
			offset = criteria.getOffset();

		if (limit == -1) {
			finalQuery = finalQuery.replace("WHERE offset_ > ? AND offset_ <= ?", "");
		} else {
			preparedStmtList.add(offset);
			preparedStmtList.add(limit + offset);
		}

		return finalQuery;

	}

	/**
	 * add if clause to the Statement if required or elese AND
	 * 
	 * @param values
	 * @param queryString
	 */
	private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND");
		}
	}

	/**
	 * add values to the preparedStatment List
	 * 
	 * @param preparedStmtList
	 * @param ids
	 */
	private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});

	}

	/**
	 * produce a query input for the multiple values
	 * 
	 * @param ids
	 * @return
	 */
	private Object createQuery(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	private String addCountWrapper(String query) {
		return countWrapper.replace("{INTERNAL_QUERY}", query);
	}

}
