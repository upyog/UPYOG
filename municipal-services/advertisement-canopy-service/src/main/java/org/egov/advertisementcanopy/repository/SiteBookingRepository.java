package org.egov.advertisementcanopy.repository;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.egov.advertisementcanopy.model.SiteBooking;
import org.egov.advertisementcanopy.model.SiteBookingSearchCriteria;
import org.egov.advertisementcanopy.repository.rowmapper.SiteBookingRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class SiteBookingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private SiteBookingRowMapper egSiteBookingRowMapper;
    
    private static final String baseSearchQuery = "SELECT * FROM eg_site_booking";

    public List<SiteBooking> search(SiteBookingSearchCriteria siteBookingSearchCriteria) {
    	List<String> preparedStatementValues = new ArrayList<>();
        StringBuilder searchQuery = new StringBuilder(baseSearchQuery);
		searchQuery = addWhereClause(searchQuery, preparedStatementValues, siteBookingSearchCriteria);
        return jdbcTemplate.query(searchQuery.toString(), preparedStatementValues.toArray(), egSiteBookingRowMapper);
    }

    private StringBuilder addWhereClause(StringBuilder searchQuery, List preparedStatementValues,
			SiteBookingSearchCriteria siteBookingSearchCriteria) {
		

        if (CollectionUtils.isEmpty(siteBookingSearchCriteria.getUuids()) 
        		&& CollectionUtils.isEmpty(siteBookingSearchCriteria.getApplicationNumbers())
        		&& CollectionUtils.isEmpty(siteBookingSearchCriteria.getCreatedBy())) {
        	return searchQuery;
        }
        

        searchQuery.append(" WHERE");
        boolean isAppendAndClause = false;

        if (!CollectionUtils.isEmpty(siteBookingSearchCriteria.getUuids())) {
            isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
            searchQuery.append(" uuid IN ( ").append(getQueryForCollection(siteBookingSearchCriteria.getUuids(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(siteBookingSearchCriteria.getApplicationNumbers())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" application_no IN ( ").append(getQueryForCollection(siteBookingSearchCriteria.getApplicationNumbers(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(siteBookingSearchCriteria.getCreatedBy())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" created_by IN ( ").append(getQueryForCollection(siteBookingSearchCriteria.getCreatedBy(),
                    preparedStatementValues)).append(" )");
        }

        
        
        return searchQuery;
	}
    
    private String getQueryForCollection(List<?> ids, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = ids.iterator();
        while (iterator.hasNext()) {
            builder.append(" ?");
            preparedStmtList.add(iterator.next());

            if (iterator.hasNext())
                builder.append(",");
        }
        return builder.toString();
    }
    
    private boolean addAndClauseIfRequired(final boolean appendAndClauseFlag, final StringBuilder queryString) {
        if (appendAndClauseFlag)
            queryString.append(" AND");

        return true;
    }

}