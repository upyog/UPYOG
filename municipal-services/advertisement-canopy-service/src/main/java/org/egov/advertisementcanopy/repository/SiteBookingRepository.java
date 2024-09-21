package org.egov.advertisementcanopy.repository;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.advertisementcanopy.model.SiteBooking;
import org.egov.advertisementcanopy.model.SiteBookingSearchCriteria;
import org.egov.advertisementcanopy.repository.rowmapper.SiteBookingRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Repository
public class SiteBookingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private SiteBookingRowMapper egSiteBookingRowMapper;
    
    private static final String baseSearchQuery = "SELECT booking.*, site.id as site_id, site.uuid as site_uuid, site.site_id as site_site_id, site.site_name as site_site_name, site.site_description as site_site_description, site.site_cost as site_site_cost, site.site_address as site_site_address, site.site_photograph as site_site_photograph, site.structure as site_structure, site.size_length as site_size_length, site.size_width as site_size_width, site.led_selection as site_led_selection, site.security_amount as site_security_amount, site.powered as site_powered, site.others as site_others, site.pincode as site_pincode, site.ulb_name as site_ulb_name, site.ulb_type as site_ulb_type, site.ward_number as site_ward_number, site.site_type as site_site_type, site.account_id as site_account_id, site.status as site_status, site.is_active as site_is_active, site.tenant_id as site_tenant_id, site.gps_location as site_gps_location, site.district_name as site_district_name, site.pincode as site_pincode"
    		+ ", site.others as site_others "
    		+ " FROM eg_site_booking as booking "
    		+ " LEFT OUTER JOIN eg_site_application as site ON booking.site_uuid = site.uuid";


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
        		&& CollectionUtils.isEmpty(siteBookingSearchCriteria.getCreatedBy())
        		&& StringUtils.isEmpty(siteBookingSearchCriteria.getTenantId())) {
        	return searchQuery;
        }
        

        searchQuery.append(" WHERE");
        boolean isAppendAndClause = false;

        if (!CollectionUtils.isEmpty(siteBookingSearchCriteria.getUuids())) {
            isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
            searchQuery.append(" booking.uuid IN ( ").append(getQueryForCollection(siteBookingSearchCriteria.getUuids(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(siteBookingSearchCriteria.getApplicationNumbers())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" booking.application_no IN ( ").append(getQueryForCollection(siteBookingSearchCriteria.getApplicationNumbers(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(siteBookingSearchCriteria.getCreatedBy())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" booking.created_by IN ( ").append(getQueryForCollection(siteBookingSearchCriteria.getCreatedBy(),
                    preparedStatementValues)).append(" )");
        }

		if (!ObjectUtils.isEmpty(siteBookingSearchCriteria.getTenantId())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
			searchQuery.append(" booking.tenant_id = ? ");
			preparedStatementValues.add(siteBookingSearchCriteria.getTenantId());
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