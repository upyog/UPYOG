package org.egov.advertisementcanopy.repository;

import java.util.List;

import org.egov.advertisementcanopy.model.SearchCriteriaSite;
import org.egov.advertisementcanopy.model.SiteCreationData;
import org.egov.advertisementcanopy.repository.builder.SiteApplicationQueryBuilder;
import org.egov.advertisementcanopy.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SiteRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	SiteService siteService;

	@Autowired
	private SiteApplicationQueryBuilder queryBuilder;
	public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_id_eg_site_application')";
	public static final String SELECT_NEXT_SITE_SEQUENCE = "select nextval('seq_id_eg_site_type')";
	private Long getNextSequence() {
		return jdbcTemplate.queryForObject(SELECT_NEXT_SEQUENCE, Long.class);
	}
	private Long getNextSiteSequence() {
		return jdbcTemplate.queryForObject(SELECT_NEXT_SITE_SEQUENCE, Long.class);
	}

	public void create(SiteCreationData siteCreation) {
		siteCreation.setId(getNextSequence());
		if(siteCreation.getSiteType().equals(siteService.ADVERTISEMENT_HOARDING)) {
			siteCreation.setSiteID("AHS"+"/"+siteCreation.getUlbName()+"/"+getNextSiteSequence());
		}
		if(siteCreation.getSiteType().equals(siteService.CANOPY)) {
			siteCreation.setSiteID("ACS"+"/"+siteCreation.getUlbName()+"/"+getNextSiteSequence());
		}
	        jdbcTemplate.update(queryBuilder.CREATE_QUERY,
	        		siteCreation.getId(),
	        		siteCreation.getUuid(),
	        		siteCreation.getSiteID(),
	        		siteCreation.getSiteName(),
	        		siteCreation.getSiteDescription(),
	        		siteCreation.getGpsLocation(),
	        		siteCreation.getSiteAddress(),
	        		siteCreation.getSiteCost(),
	        		siteCreation.getSitePhotograph(),
	        		siteCreation.getStructure(),
	        		siteCreation.getSizeLength(),
	        		siteCreation.getSizeWidth(),
	        		siteCreation.getLedSelection(),
	        		siteCreation.getSecurityAmount(),
	        		siteCreation.getPowered(),
	        		siteCreation.getOthers(),
	        		siteCreation.getDistrictName(),
	        		siteCreation.getUlbName(),
	        		siteCreation.getUlbType(),
	        		siteCreation.getWardNumber(),
	        		siteCreation.getPinCode(),
	        		siteCreation.getAdditionalDetail(),
	        		siteCreation.getAuditDetails().getCreatedBy(),
	        		siteCreation.getAuditDetails().getCreatedDate(),
	        		siteCreation.getAuditDetails().getLastModifiedBy(),
	        		siteCreation.getAuditDetails().getLastModifiedDate(),
	        		siteCreation.getSiteType(),
	        		siteCreation.getAccountId());
	    }

	/*
	 * public String searchSiteIds(SearchCriteriaSite searchCriteriaSite) {
	 * StringBuilder searchQuery = null; final List preparedStatementValues = new
	 * ArrayList<>();
	 * 
	 * //generate search query searchQuery = getSearchQueryByCriteria(searchQuery,
	 * searchCriteriaSite, preparedStatementValues);
	 * 
	 * log.debug("### search siteIds for Sites: "+searchQuery.toString());
	 * 
	 * String siteIds = jdbcTemplate.query(searchQuery.toString(),
	 * preparedStatementValues.toArray(), garbageAccountRowMapper);
	 * 
	 * return siteIds; }
	 */

	private StringBuilder getSearchQueryByCriteria(StringBuilder searchQuery, SearchCriteriaSite searchCriteriaSite,
			List preparedStatementValues) {
		// TODO Auto-generated method stub
		return null;
	}

}
