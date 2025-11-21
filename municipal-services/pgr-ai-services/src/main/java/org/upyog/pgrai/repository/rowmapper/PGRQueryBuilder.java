package org.upyog.pgrai.repository.rowmapper;

import org.upyog.pgrai.config.PGRConfiguration;
import org.upyog.pgrai.web.models.RequestSearchCriteria;
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

/**
 * Query builder class for constructing SQL queries for PGR (Public Grievance Redressal) services.
 * Provides methods to build search, count, and other specific queries based on search criteria.
 */
@Repository
public class PGRQueryBuilder {

	private PGRConfiguration config;

    /**
     * Constructor for `PGRQueryBuilder`.
     *
     * @param config The configuration object for PGR.
     */
	@Autowired
    public PGRQueryBuilder(PGRConfiguration config) {
        this.config = config;
	}

    private static final String QUERY_ALIAS =   "ser.id as ser_id,ads.id as ads_id," +
                                                "ser.tenantId as ser_tenantId,ads.tenantId as ads_tenantId," +
                                                "ser.additionaldetails as ser_additionaldetails,ads.additionaldetails as ads_additionaldetails," +
                                                "ser.createdby as ser_createdby,ser.createdtime as ser_createdtime," +
                                                "ser.lastmodifiedby as ser_lastmodifiedby,ser.lastmodifiedtime as ser_lastmodifiedtime," +
                                                "ads.createdby as ads_createdby,ads.createdtime as ads_createdtime," +
                                                "ads.lastmodifiedby as ads_lastmodifiedby,ads.lastmodifiedtime as ads_lastmodifiedtime " ;


    private static final String QUERY = "select ser.*,ads.*," + QUERY_ALIAS+
                                        " from {schema}.ug_pgr_service ser INNER JOIN {schema}.ug_pgr_address ads" +
                                        " ON ads.parentId = ser.id ";

    private static final String COUNT_WRAPPER = "select count(*) from ({INTERNAL_QUERY}) as count";   

    private static final String RESOLVED_COMPLAINTS_QUERY = "select count(*) from {schema}.ug_pgr_service where applicationstatus='CLOSEDAFTERRESOLUTION' and tenantid=? and lastmodifiedtime>? ";

    private static final String AVERAGE_RESOLUTION_TIME_QUERY = "select round(avg(lastmodifiedtime-createdtime)/86400000) from {schema}.ug_pgr_service where applicationstatus='CLOSEDAFTERRESOLUTION' and tenantid=? ";


    /**
     * Constructs the SQL query for searching PGR services based on the provided criteria.
     *
     * @param criteria         The search criteria for filtering PGR services.
     * @param preparedStmtList The list of prepared statement parameters.
     * @return The constructed SQL query as a string.
     */
    public String getPGRSearchQuery(RequestSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder builder = new StringBuilder(QUERY);

        if(criteria.getIsPlainSearch() != null && criteria.getIsPlainSearch()){
            Set<String> tenantIds = criteria.getTenantIds();
            if(!CollectionUtils.isEmpty(tenantIds)){
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" ser.tenantId IN (").append(createQuery(tenantIds)).append(")");
                addToPreparedStatement(preparedStmtList, tenantIds);
            }
        }
        else {
            if (criteria.getTenantId() != null) {
                String tenantId = criteria.getTenantId();

                String[] tenantIdChunks = tenantId.split("\\.");

                if (tenantIdChunks.length == config.getStateLevelTenantIdLength()) {
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

        //When UI tries to fetch "escalated" complaints count.
        if(criteria.getSlaDeltaMaxLimit() != null && criteria.getSlaDeltaMinLimit() == null){
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" ((extract(epoch FROM NOW())*1000) - ser.createdtime) > ? ");
            preparedStmtList.add(criteria.getSlaDeltaMaxLimit());
        }
        //When UI tries to fetch "other" complaints count.
        if(criteria.getSlaDeltaMaxLimit() != null && criteria.getSlaDeltaMinLimit() != null){
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
        if(!CollectionUtils.isEmpty(localities)){
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" ads.locality IN (").append(createQuery(localities)).append(")");
            addToPreparedStatement(preparedStmtList, localities);
        }

        if (criteria.getFromDate() != null) {
            addClauseIfRequired(preparedStmtList, builder);

            //If user does not specify toDate, take today's date as toDate by default.
            if (criteria.getToDate() == null) {
                criteria.setToDate(Instant.now().toEpochMilli());
            }

            builder.append(" ser.createdtime BETWEEN ? AND ?");
            preparedStmtList.add(criteria.getFromDate());
            preparedStmtList.add(criteria.getToDate());

        } else {
            //if only toDate is provided as parameter without fromDate parameter, throw an exception.
            if (criteria.getToDate() != null) {
                throw new CustomException("INVALID_SEARCH", "Cannot specify to-Date without a from-Date");
            }
        }


        addOrderByClause(builder, criteria);

        addLimitAndOffset(builder, criteria, preparedStmtList);

        return builder.toString();
    }

    /**
     * Constructs the SQL query for counting PGR services based on the provided criteria.
     *
     * @param criteria         The search criteria for filtering PGR services.
     * @param preparedStmtList The list of prepared statement parameters.
     * @return The constructed count query as a string.
     */
    public String getCountQuery(RequestSearchCriteria criteria, List<Object> preparedStmtList){
        String query = getPGRSearchQuery(criteria, preparedStmtList);
        String countQuery = COUNT_WRAPPER.replace("{INTERNAL_QUERY}", query);
        return countQuery;
    }

    /**
     * Adds an ORDER BY clause to the SQL query based on the provided criteria.
     *
     * @param builder  The SQL query builder.
     * @param criteria The search criteria containing sort options.
     */
    private void addOrderByClause(StringBuilder builder, RequestSearchCriteria criteria){

        if(StringUtils.isEmpty(criteria.getSortBy()))
            builder.append( " ORDER BY ser_createdtime ");

        else if(criteria.getSortBy()== RequestSearchCriteria.SortBy.locality)
            builder.append(" ORDER BY ads.locality ");

        else if(criteria.getSortBy()== RequestSearchCriteria.SortBy.applicationStatus)
            builder.append(" ORDER BY ser.applicationStatus ");

        else if(criteria.getSortBy()== RequestSearchCriteria.SortBy.serviceRequestId)
            builder.append(" ORDER BY ser.serviceRequestId ");

        if(criteria.getSortOrder()== RequestSearchCriteria.SortOrder.ASC)
            builder.append(" ASC ");
        else builder.append(" DESC ");

    }

    /**
     * Adds LIMIT and OFFSET clauses to the SQL query based on the provided criteria.
     *
     * @param builder          The SQL query builder.
     * @param criteria         The search criteria containing pagination options.
     * @param preparedStmtList The list of prepared statement parameters.
     */
    private void addLimitAndOffset(StringBuilder builder, RequestSearchCriteria criteria, List<Object> preparedStmtList){

        builder.append(" OFFSET ? ");
        preparedStmtList.add(criteria.getOffset());

        builder.append(" LIMIT ? ");
        preparedStmtList.add(criteria.getLimit());

    }

    /**
     * Adds a WHERE or AND clause to the SQL query if required.
     *
     * @param values      The list of prepared statement parameters.
     * @param queryString The SQL query builder.
     */
    private static void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
        if (values.isEmpty())
            queryString.append(" WHERE ");
        else {
            queryString.append(" AND");
        }
    }

    /**
     * Creates a query string for a collection of IDs to be used in an IN clause.
     *
     * @param ids The collection of IDs.
     * @return The constructed query string for the IN clause.
     */
    private String createQuery(Collection<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for( int i = 0; i< length; i++){
            builder.append(" ? ");
            if(i != length -1) builder.append(",");
        }
        return builder.toString();
    }

    /**
     * Adds a collection of IDs to the prepared statement parameters.
     *
     * @param preparedStmtList The list of prepared statement parameters.
     * @param ids              The collection of IDs to be added.
     */
    private void addToPreparedStatement(List<Object> preparedStmtList, Collection<String> ids)
    {
        ids.forEach(id ->{ preparedStmtList.add(id);});
    }

    /**
     * Constructs the SQL query for fetching the count of resolved complaints.
     *
     * @param tenantId                     The tenant ID for filtering complaints.
     * @param preparedStmtListComplaintsResolved The list of prepared statement parameters.
     * @return The constructed query for resolved complaints count.
     */
	public String getResolvedComplaints(String tenantId, List<Object> preparedStmtListComplaintsResolved) {
		StringBuilder query = new StringBuilder("");
		query.append(RESOLVED_COMPLAINTS_QUERY);

		preparedStmtListComplaintsResolved.add(tenantId);

		// In order to get data of last 12 months, the months variables is pre-configured in application properties
    	int days = Integer.valueOf(config.getNumberOfDays()) ;

    	Calendar calendar = Calendar.getInstance();

    	// To subtract 12 months from current time, we are adding -12 to the calendar instance, as subtract function is not in-built
    	calendar.add(Calendar.DATE, -1*days);

    	// Converting the timestamp to milliseconds and adding it to prepared statement list
    	preparedStmtListComplaintsResolved.add(calendar.getTimeInMillis());

		return query.toString();
	}

    /**
     * Constructs the SQL query for calculating the average resolution time of complaints.
     *
     * @param tenantId                     The tenant ID for filtering complaints.
     * @param preparedStmtListAverageResolutionTime The list of prepared statement parameters.
     * @return The constructed query for average resolution time.
     */
	public String getAverageResolutionTime(String tenantId, List<Object> preparedStmtListAverageResolutionTime) {
		StringBuilder query = new StringBuilder("");
		query.append(AVERAGE_RESOLUTION_TIME_QUERY);

		preparedStmtListAverageResolutionTime.add(tenantId);

		return query.toString();
	}

}
