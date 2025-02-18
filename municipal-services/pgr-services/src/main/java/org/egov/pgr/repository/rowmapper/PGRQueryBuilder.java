package org.egov.pgr.repository.rowmapper;

import org.egov.pgr.config.PGRConfiguration;
import org.egov.pgr.web.models.PgrNotificationSearchCriteria;
import org.egov.pgr.web.models.RequestSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public class PGRQueryBuilder {

	private PGRConfiguration config;

	@Autowired
	public PGRQueryBuilder(PGRConfiguration config) {
		this.config = config;
	}
	
	private static final String NOTIFICATION_SEARCH_QUERY = "SELECT * FROM eg_pgr_notification epn";

	private static final String QUERY_ALIAS = "ser.id as ser_id,ads.id as ads_id,"
			+ "ser.tenantId as ser_tenantId,ads.tenantId as ads_tenantId,"
			+ "ser.additionaldetails as ser_additionaldetails,ads.additionaldetails as ads_additionaldetails,"
			+ "ser.createdby as ser_createdby,ser.createdtime as ser_createdtime,"
			+ "ser.lastmodifiedby as ser_lastmodifiedby,ser.lastmodifiedtime as ser_lastmodifiedtime,"
			+ "ads.createdby as ads_createdby,ads.createdtime as ads_createdtime,"
			+ "ads.lastmodifiedby as ads_lastmodifiedby,ads.lastmodifiedtime as ads_lastmodifiedtime ";

	private static final String QUERY = "select ser.*,ads.*," + QUERY_ALIAS
			+ " from eg_pgr_service_v2 ser INNER JOIN eg_pgr_address_v2 ads" + " ON ads.parentId = ser.id ";

	private static final String COUNT_WRAPPER = "select count(*) from ({INTERNAL_QUERY}) as count";

	private static final String RESOLVED_COMPLAINTS_QUERY = "select count(*) from eg_pgr_service_v2 where applicationstatus='CLOSEDAFTERRESOLUTION' and tenantid=? and lastmodifiedtime>? ";

	// private static final String AVERAGE_RESOLUTION_TIME_QUERY = "select
	// round(avg(lastmodifiedtime-createdtime)/86400000) from eg_pgr_service_v2
	// where applicationstatus='CLOSEDAFTERRESOLUTION' and tenantid=? ";

	private static final String AVERAGE_RESOLUTION_TIME_QUERY = "select case when round(avg(lastmodifiedtime-createdtime)/ 86400000) is null then  0 else round(avg(lastmodifiedtime-createdtime)/ 86400000) end from eg_pgr_service_v2 where applicationstatus = 'CLOSEDAFTERRESOLUTION' and tenantid = ?";

	public static final String COUNT_QUERY = "SELECT " 
	+       "SUM(CASE WHEN pt.applicationstatus = 'PENDINGFORASSIGNMENT' THEN 1 ELSE 0 END) AS pendingForAssignment, " +
            "SUM(CASE WHEN pt.applicationstatus = 'PENDINGFORREASSIGNMENT' THEN 1 ELSE 0 END) AS pendingForReAssignment, " +
            "SUM(CASE WHEN pt.applicationstatus = 'PENDINGATLME' THEN 1 ELSE 0 END) AS pendingAtLME, " +
            "SUM(CASE WHEN pt.applicationstatus = 'REJECTED' THEN 1 ELSE 0 END) AS rejected, " +
            "SUM(CASE WHEN pt.applicationstatus = 'RESOLVED' THEN 1 ELSE 0 END) AS resolved, " +
            "SUM(CASE WHEN pt.applicationstatus = 'CLOSEDAFTERREJECTION' THEN 1 ELSE 0 END) AS closedAfterRejection, " +
            "SUM(CASE WHEN pt.applicationstatus = 'CLOSEDAFTERRESOLUTION' THEN 1 ELSE 0 END) AS closedAfterResolution " +
            "FROM eg_pgr_service_v2 as pt " ;
	
	public static final String COUNT_APPLCATIONSTATUS_SUMMARY  = "SELECT " + 
		    " period, " +
		    " SUM(pendingForAssignment) AS pendingForAssignment, " + 
		    " SUM(pendingForReAssignment) AS pendingForReAssignment, "+ 
		    " SUM(pendingAtLME) AS pendingAtLME, "+ 
		    " SUM(rejected) AS rejected, " + 
		    " SUM(resolved) AS resolved, "+ 
		    " SUM(closedAfterRejection) AS closedAfterRejection, " +
		    " SUM(closedAfterResolution) AS closedAfterResolution " + 
		    " FROM application_status_summary " +
		    " GROUP BY period " +
		    " ORDER BY CASE period " + 
		    " WHEN 'Last_10_days' THEN 1 " + 
		    " WHEN '11_to_30_days' THEN 2 " + 
		    " WHEN '31_to_60_days' THEN 3 " + 
		    " ELSE 4 END";

           /* "WHERE created_date BETWEEN CURRENT_DATE - INTERVAL '60 days' AND CURRENT_DATE " +
            "GROUP BY date_range " +
            "ORDER BY date_range";*/
	public String getPGRSearchQuery(RequestSearchCriteria criteria, List<Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(QUERY);

		if (criteria.getIsPlainSearch() != null && criteria.getIsPlainSearch()) {
			Set<String> tenantIds = criteria.getTenantIds();
			if (!CollectionUtils.isEmpty(tenantIds)) {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" ser.tenantId IN (").append(createQuery(tenantIds)).append(")");
				addToPreparedStatement(preparedStmtList, tenantIds);
			}
		} else {
			if (criteria.getTenantId() != null) {
				String tenantId = criteria.getTenantId();

				String[] tenantIdChunks = tenantId.split("\\.");

				if (tenantIdChunks.length == 1) {
					addClauseIfRequired(preparedStmtList, builder);
					builder.append(" ser.tenantid LIKE ? ");
					preparedStmtList.add(criteria.getTenantId() + '%');
				} else {
					addClauseIfRequired(preparedStmtList, builder);
					builder.append(" ser.tenantid=? ");
					preparedStmtList.add(criteria.getTenantId());
				}
			}
		}
		Set<String> serviceCodes = criteria.getServiceCode();
		if (!CollectionUtils.isEmpty(serviceCodes)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ser.serviceCode IN (").append(createQuery(serviceCodes)).append(")");
			addToPreparedStatement(preparedStmtList, serviceCodes);
		}

		Set<String> applicationStatuses = criteria.getApplicationStatus();
		if (!CollectionUtils.isEmpty(applicationStatuses)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ser.applicationStatus IN (").append(createQuery(applicationStatuses)).append(")");
			addToPreparedStatement(preparedStmtList, applicationStatuses);
		}

		if (criteria.getServiceRequestId() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ser.serviceRequestId=? ");
			preparedStmtList.add(criteria.getServiceRequestId());
		}

		Set<String> ids = criteria.getIds();
		if (!CollectionUtils.isEmpty(ids)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ser.id IN (").append(createQuery(ids)).append(")");
			addToPreparedStatement(preparedStmtList, ids);
		}

		// When UI tries to fetch "escalated" complaints count.
		if (criteria.getSlaDeltaMaxLimit() != null && criteria.getSlaDeltaMinLimit() == null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ((extract(epoch FROM NOW())*1000) - ser.createdtime) > ? ");
			preparedStmtList.add(criteria.getSlaDeltaMaxLimit());
		}
		// When UI tries to fetch "other" complaints count.
		if (criteria.getSlaDeltaMaxLimit() != null && criteria.getSlaDeltaMinLimit() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ((extract(epoch FROM NOW())*1000) - ser.createdtime) > ? ");
			preparedStmtList.add(criteria.getSlaDeltaMinLimit());
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ((extract(epoch FROM NOW())*1000) - ser.createdtime) < ? ");
			preparedStmtList.add(criteria.getSlaDeltaMaxLimit());
		}

		Set<String> userIds = criteria.getUserIds();
		if (!CollectionUtils.isEmpty(userIds)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ser.accountId IN (").append(createQuery(userIds)).append(")");
			addToPreparedStatement(preparedStmtList, userIds);
		}

		Set<String> localities = criteria.getLocality();
		if (!CollectionUtils.isEmpty(localities)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" ads.locality IN (").append(createQuery(localities)).append(")");
			addToPreparedStatement(preparedStmtList, localities);
		}

		if (criteria.getFromDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);

			// If user does not specify toDate, take today's date as toDate by default.
			if (criteria.getToDate() == null) {
				criteria.setToDate(Instant.now().toEpochMilli());
			}

			builder.append(" ser.createdtime BETWEEN ? AND ?");
			preparedStmtList.add(criteria.getFromDate());
			preparedStmtList.add(criteria.getToDate());

		} else {
			// if only toDate is provided as parameter without fromDate parameter, throw an
			// exception.
			if (criteria.getToDate() != null) {
				throw new CustomException("INVALID_SEARCH", "Cannot specify to-Date without a from-Date");
			}
		}

		addOrderByClause(builder, criteria);

		addLimitAndOffset(builder, criteria, preparedStmtList);

		return builder.toString();
	}

	public String getCountQuery(RequestSearchCriteria criteria, List<Object> preparedStmtList) {
		String query = getPGRSearchQuery(criteria, preparedStmtList);
		String countQuery = COUNT_WRAPPER.replace("{INTERNAL_QUERY}", query);
		return countQuery;
	}

	private void addOrderByClause(StringBuilder builder, RequestSearchCriteria criteria) {

		if (StringUtils.isEmpty(criteria.getSortBy()))
			builder.append(" ORDER BY ser_createdtime ");

		else if (criteria.getSortBy() == RequestSearchCriteria.SortBy.locality)
			builder.append(" ORDER BY ads.locality ");

		else if (criteria.getSortBy() == RequestSearchCriteria.SortBy.applicationStatus)
			builder.append(" ORDER BY ser.applicationStatus ");

		else if (criteria.getSortBy() == RequestSearchCriteria.SortBy.serviceRequestId)
			builder.append(" ORDER BY ser.serviceRequestId ");

		if (criteria.getSortOrder() == RequestSearchCriteria.SortOrder.ASC)
			builder.append(" ASC ");
		else
			builder.append(" DESC ");

	}

	private void addLimitAndOffset(StringBuilder builder, RequestSearchCriteria criteria,
			List<Object> preparedStmtList) {

		builder.append(" OFFSET ? ");
		preparedStmtList.add(criteria.getOffset());

		builder.append(" LIMIT ? ");
		preparedStmtList.add(criteria.getLimit());

	}

	private static void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND");
		}
	}

	private String createQuery(Collection<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ? ");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, Collection<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});
	}

	public String getResolvedComplaints(String tenantId, List<Object> preparedStmtListComplaintsResolved) {

		StringBuilder query = new StringBuilder("");
		query.append(RESOLVED_COMPLAINTS_QUERY);

		preparedStmtListComplaintsResolved.add(tenantId);

		// In order to get data of last 12 months, the months variables is
		// pre-configured in application properties
		int days = Integer.valueOf(config.getNumberOfDays());

		Calendar calendar = Calendar.getInstance();

		// To subtract 12 months from current time, we are adding -12 to the calendar
		// instance, as subtract function is not in-built
		calendar.add(Calendar.DATE, -1 * days);

		// Converting the timestamp to milliseconds and adding it to prepared statement
		// list
		preparedStmtListComplaintsResolved.add(calendar.getTimeInMillis());

		return query.toString();
	}

	public String getAverageResolutionTime(String tenantId, List<Object> preparedStmtListAverageResolutionTime) {
		StringBuilder query = new StringBuilder("");
		query.append(AVERAGE_RESOLUTION_TIME_QUERY);

		preparedStmtListAverageResolutionTime.add(tenantId);

		return query.toString();
	}

	public String getPGRNotificationSearchQuery(PgrNotificationSearchCriteria criteria,
			List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(NOTIFICATION_SEARCH_QUERY);

		if (!StringUtils.isEmpty(criteria.getTenantId())) {
			String tenantId = criteria.getTenantId();

			String[] tenantIdChunks = tenantId.split("\\.");

			if (tenantIdChunks.length == 1) {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" epn.tenantid LIKE ? ");
				preparedStmtList.add(criteria.getTenantId() + '%');
			} else {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" epn.tenantid=? ");
				preparedStmtList.add(criteria.getTenantId());
			}
		}

		if (!CollectionUtils.isEmpty(criteria.getTenantIds())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" epn.tenantId IN (").append(createQuery(criteria.getTenantIds())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getTenantIds());
		}
		if (!CollectionUtils.isEmpty(criteria.getUuids())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" epn.uuid IN (").append(createQuery(criteria.getUuids())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getUuids());
		}
		if (!CollectionUtils.isEmpty(criteria.getServiceRequestIds())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" epn.servicerequestid IN (").append(createQuery(criteria.getServiceRequestIds()))
					.append(")");
			addToPreparedStatement(preparedStmtList, criteria.getServiceRequestIds());
		}
		if (!CollectionUtils.isEmpty(criteria.getApplicationStatus())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" epn.applicationstatus IN (").append(createQuery(criteria.getApplicationStatus()))
					.append(")");
			addToPreparedStatement(preparedStmtList, criteria.getApplicationStatus());
		}
		if (!CollectionUtils.isEmpty(criteria.getRecipientNames())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" epn.recipientname IN (").append(createQuery(criteria.getRecipientNames())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getRecipientNames());
		}
		if (!CollectionUtils.isEmpty(criteria.getEmailIds())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" epn.emailid IN (").append(createQuery(criteria.getEmailIds())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getEmailIds());
		}
		if (!CollectionUtils.isEmpty(criteria.getMobileNumbers())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" epn.mobilenumber IN (").append(createQuery(criteria.getMobileNumbers())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getMobileNumbers());
		}
		if (!StringUtils.isEmpty(criteria.getIsEmailSent())) {
			addClauseIfRequired(preparedStmtList, builder);
			if (criteria.getIsEmailSent()) {
				builder.append(" epn.isemailsent = true");
			} else {
				builder.append(" epn.isemailsent = false");
			}
		}
		if (!StringUtils.isEmpty(criteria.getIsSmsSent())) {
			addClauseIfRequired(preparedStmtList, builder);
			if (criteria.getIsSmsSent()) {
				builder.append(" epn.issmssent = true");
			} else {
				builder.append(" epn.issmssent = false");
			}
		}

		return builder.toString();
	}

}
