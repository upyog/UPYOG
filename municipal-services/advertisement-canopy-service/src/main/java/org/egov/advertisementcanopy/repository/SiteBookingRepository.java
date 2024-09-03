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


    public int save(SiteBooking egSiteBooking) {
        String sql = "INSERT INTO eg_site_booking (uuid, application_no, site_uuid, applicant_name, applicant_father_name, gender, mobile_number, email_id, advertisement_type, from_date, period_in_days, hoarding_type, structure, is_active, additional_detail :: JSONB, created_by, created_date, last_modified_by, last_modified_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, egSiteBooking.getUuid(), egSiteBooking.getApplicationNo(), egSiteBooking.getSiteUuid(),
                egSiteBooking.getApplicantName(), egSiteBooking.getApplicantFatherName(), egSiteBooking.getGender(),
                egSiteBooking.getMobileNumber(), egSiteBooking.getEmailId(), egSiteBooking.getAdvertisementType(),
                egSiteBooking.getFromDate(), egSiteBooking.getPeriodInDays(), egSiteBooking.getHoardingType(), egSiteBooking.getStructure()
                , egSiteBooking.getIsActive(), egSiteBooking.getAdditionalDetail(), egSiteBooking.getAuditDetails().getCreatedBy(), egSiteBooking.getAuditDetails().getCreatedDate(), egSiteBooking.getAuditDetails().getLastModifiedBy(), egSiteBooking.getAuditDetails().getLastModifiedDate());
    }

    public int update(SiteBooking egSiteBooking) {
        String sql = "UPDATE eg_site_booking SET application_no = ?, site_uuid = ?, applicant_name = ?, applicant_father_name = ?, gender = ?, mobile_number = ?, email_id = ?, advertisement_type = ?, from_date = ?, period_in_days = ?, hoarding_type = ?, structure = ? WHERE uuid = ?";
        return jdbcTemplate.update(sql, egSiteBooking.getApplicationNo(), egSiteBooking.getSiteUuid(),
                egSiteBooking.getApplicantName(), egSiteBooking.getApplicantFatherName(), egSiteBooking.getGender(),
                egSiteBooking.getMobileNumber(), egSiteBooking.getEmailId(), egSiteBooking.getAdvertisementType(),
                egSiteBooking.getFromDate(), egSiteBooking.getPeriodInDays(), egSiteBooking.getHoardingType(), egSiteBooking.getStructure(),
                egSiteBooking.getUuid());
    }

}