package org.egov.advertisementcanopy.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.advertisementcanopy.model.SiteCreationActionRequest;
import org.egov.advertisementcanopy.model.SiteCreationData;
import org.egov.advertisementcanopy.model.SiteSearchCriteria;
import org.egov.advertisementcanopy.model.SiteSearchRequest;
import org.egov.advertisementcanopy.model.SiteUpdateRequest;
import org.egov.advertisementcanopy.repository.builder.SiteApplicationQueryBuilder;
import org.egov.advertisementcanopy.repository.rowmapper.SiteApplicationRowMapper;
import org.egov.advertisementcanopy.service.SiteService;
import org.egov.advertisementcanopy.util.AdvtConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SiteRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	SiteApplicationRowMapper siteApplicationRowMapper;

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private SiteApplicationQueryBuilder queryBuilder;
	public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_id_eg_site_application')";
	public static final String SELECT_NEXT_SITE_SEQUENCE = "select nextval('seq_id_eg_site_type')";

	public Long getNextSequence() {
		return jdbcTemplate.queryForObject(SELECT_NEXT_SEQUENCE, Long.class);
	}

	public Long getNextSiteSequence() {
		return jdbcTemplate.queryForObject(SELECT_NEXT_SITE_SEQUENCE, Long.class);
	}

	public void create(SiteCreationData siteCreation) {
		siteCreation.setId(getNextSequence());

		jdbcTemplate.update(queryBuilder.CREATE_QUERY, siteCreation.getId(), siteCreation.getUuid(),
				siteCreation.getSiteID(), siteCreation.getSiteName(), siteCreation.getSiteDescription(),
				siteCreation.getGpsLocation(), siteCreation.getSiteAddress(), siteCreation.getSiteCost(),
				siteCreation.getSitePhotograph(), siteCreation.getStructure(), siteCreation.getSizeLength(),
				siteCreation.getSizeWidth(), siteCreation.getLedSelection(), siteCreation.getSecurityAmount(),
				siteCreation.getPowered(), siteCreation.getOthers(), siteCreation.getDistrictName(),
				siteCreation.getUlbName(), siteCreation.getUlbType(), siteCreation.getWardNumber(),
				siteCreation.getPinCode(),
				(null == siteCreation.getAdditionalDetail() || siteCreation.getAdditionalDetail().isEmpty()) ? null
						: objectMapper.convertValue(siteCreation.getAdditionalDetail(), ObjectNode.class).toString(),
				siteCreation.getAuditDetails().getCreatedBy(), siteCreation.getAuditDetails().getCreatedDate(),
				siteCreation.getAuditDetails().getLastModifiedBy(),
				siteCreation.getAuditDetails().getLastModifiedDate(), siteCreation.getSiteType(),
				siteCreation.getAccountId(), siteCreation.getStatus(), siteCreation.isActive(),
				siteCreation.getTenantId(), siteCreation.getBookingPeriodStartDate(),
				siteCreation.getBookingPeriodEndDate(), siteCreation.getApplicationStartDate(),
				siteCreation.getApplicationEndDate());
	}

	public List<SiteCreationData> searchSiteIds(String siteName, String districtName, String ulbName,
			String wardNumber) {
		List<SiteCreationData> list = new ArrayList<>();
		try {
			Object object = jdbcTemplate.query(queryBuilder.SEARCH_EXISTING_SITE,
					Arrays.asList(siteName, districtName, ulbName, wardNumber).toArray(), siteApplicationRowMapper);

			list = objectMapper.convertValue(object, ArrayList.class);

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;

	}

	public List<SiteCreationData> searchSites(SiteCreationData siteCreationData) {
		List<SiteCreationData> list = new ArrayList<>();
		try {
			Object object = jdbcTemplate.query(queryBuilder.SEARCH_QUERY_FOR_SITE_UPDATE,
					Arrays.asList(siteCreationData.getId(), siteCreationData.getUuid(), siteCreationData.getSiteID())
							.toArray(),
					siteApplicationRowMapper);

			list = objectMapper.convertValue(object, ArrayList.class);

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	public List<SiteCreationData> fetchSites(SiteSearchCriteria criteria) {
		List<String> preparedStatementValues = new ArrayList<>();
		StringBuilder searchQuery = new StringBuilder(queryBuilder.SELECT_SITE_FOR_CITIZEN);
		searchQuery = addWhereClauseforSites(searchQuery, preparedStatementValues, criteria);
		return jdbcTemplate.query(searchQuery.toString(), preparedStatementValues.toArray(), siteApplicationRowMapper);
	}

	private StringBuilder addWhereClauseforSites(StringBuilder searchQuery, List preparedStatementValues,
			SiteSearchCriteria criteria) {
		if (CollectionUtils.isEmpty(criteria.getUuids()) && CollectionUtils.isEmpty(criteria.getSiteId())
				&& StringUtils.isEmpty(criteria.getTenantId()) && CollectionUtils.isEmpty(criteria.getCreatedBy())) {
			return searchQuery;
		}
		searchQuery.append(" WHERE");
        boolean isAppendAndClause = false;
        
        if (!CollectionUtils.isEmpty(criteria.getUuids())) {
            isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
            searchQuery.append(" eg_site_application.uuid IN ( ").append(getQueryForCollection(criteria.getUuids(),
                    preparedStatementValues)).append(" )");
        }
        if(!CollectionUtils.isEmpty(criteria.getSiteId())) {
        	isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
        	 searchQuery.append(" eg_site_application.site_id IN ( ").append(getQueryForCollection(criteria.getSiteId(),
                     preparedStatementValues)).append(" )");
        }
        if(!CollectionUtils.isEmpty(criteria.getCreatedBy())) {
        	isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
        	 searchQuery.append(" eg_site_application.created_by IN ( ").append(getQueryForCollection(criteria.getCreatedBy(),
                     preparedStatementValues)).append(" )");
        }
        if(!ObjectUtils.isEmpty(criteria.getTenantId())) {
        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
			searchQuery.append(" eg_site_application.tenant_id = ? ");
			preparedStatementValues.add(criteria.getTenantId());
        }

			return searchQuery;
	}
	
	 

	public void updateSiteData(SiteUpdateRequest updateSiteRequest) {
		jdbcTemplate.update(queryBuilder.UPDATE_QUERY, updateSiteRequest.getSiteUpdationData().getSiteDescription(),
				updateSiteRequest.getSiteUpdationData().getGpsLocation(),
				updateSiteRequest.getSiteUpdationData().getSiteAddress(),
				updateSiteRequest.getSiteUpdationData().getSiteCost(),
				updateSiteRequest.getSiteUpdationData().getSitePhotograph(),
				updateSiteRequest.getSiteUpdationData().getStructure(),
				updateSiteRequest.getSiteUpdationData().getSizeLength(),
				updateSiteRequest.getSiteUpdationData().getSizeWidth(),
				updateSiteRequest.getSiteUpdationData().getLedSelection(),
				updateSiteRequest.getSiteUpdationData().getSecurityAmount(),
				updateSiteRequest.getSiteUpdationData().getPowered(),
				updateSiteRequest.getSiteUpdationData().getOthers(),
				updateSiteRequest.getSiteUpdationData().getDistrictName(),
				updateSiteRequest.getSiteUpdationData().getUlbName(),
				updateSiteRequest.getSiteUpdationData().getUlbType(),
				updateSiteRequest.getSiteUpdationData().getWardNumber(),
				updateSiteRequest.getSiteUpdationData().getPinCode(),
				updateSiteRequest.getSiteUpdationData().getAdditionalDetail(),
				updateSiteRequest.getSiteUpdationData().getAuditDetails().getLastModifiedBy(),
				updateSiteRequest.getSiteUpdationData().getAuditDetails().getLastModifiedDate(),
				updateSiteRequest.getSiteUpdationData().getSiteType(),
				updateSiteRequest.getSiteUpdationData().getAccountId(),
				updateSiteRequest.getSiteUpdationData().getStatus(), updateSiteRequest.getSiteUpdationData().isActive(),
				updateSiteRequest.getSiteUpdationData().getUuid(), updateSiteRequest.getSiteUpdationData().getId());

	}

	public List<SiteCreationData> searchSites(SiteSearchRequest searchSiteRequest) {
		StringBuilder siteSearchQuery = null;
		final List preparedStatementValues = new ArrayList<>();

		List<SiteCreationData> resultList = new ArrayList<>();
		try {
			// Generate site search query
			siteSearchQuery = getSiteSearchQueryByCriteria(searchSiteRequest, siteSearchQuery, preparedStatementValues);
			resultList = jdbcTemplate.query(siteSearchQuery.toString(), preparedStatementValues.toArray(),
					siteApplicationRowMapper);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return resultList;
	}

	/*
	 * public List<SiteCreationData> `(SiteSearchRequest searchSiteRequest) {
	 * StringBuilder siteSearchQuery = null; final List preparedStatementValues =
	 * new ArrayList<>(); List<SiteCreationData> resultList = new ArrayList<>(); try
	 * { siteSearchQuery = getSiteSearchQueryByCriteriaForCitzens(searchSiteRequest,
	 * siteSearchQuery, preparedStatementValues); } catch (Exception e) {
	 * 
	 * throw new RuntimeException(e.getMessage()); } return null; }
	 */

	/*
	 * private StringBuilder
	 * getSiteSearchQueryByCriteriaForCitzens(SiteSearchRequest searchSiteRequest,
	 * StringBuilder siteSearchQuery, List preparedStatementValues) { String query =
	 * queryBuilder.SELECT_SITE_FOR_CITIZEN; boolean isActive = true; final
	 * List<Object> parametersMap = new ArrayList<Object>();
	 * preparedStatementValues.add(searchSiteRequest);
	 * preparedStatementValues.add(true); // StringBuilder siteSearchQuery, List
	 * preparedStatementValues) { siteSearchQuery = new
	 * StringBuilder(queryBuilder.SELECT_SITE_FOR_CITIZEN); siteSearchQuery =
	 * addWhereClauseForCitizens(siteSearchQuery, preparedStatementValues,
	 * siteApplicationRowMapper); return siteSearchQuery; }
	 */

	/*
	 * private StringBuilder addWhereClauseForCitizens(StringBuilder
	 * siteSearchQuery, List preparedStatementValues, SiteApplicationRowMapper
	 * siteApplicationRowMapper) { boolean isAppendAndClause = false; try {
	 * siteSearchQuery.append(" WHERE");
	 * 
	 * } catch (Exception e) { // TODO: handle exception } return null; }
	 */

	private StringBuilder getSiteSearchQueryByCriteria(SiteSearchRequest searchSiteRequest,
			StringBuilder siteSearchQuery, List preparedStatementValues) {
		siteSearchQuery = new StringBuilder(queryBuilder.SELECT_SITE_QUERY);
		siteSearchQuery = addWhereClause(siteSearchQuery, preparedStatementValues, searchSiteRequest);
		return siteSearchQuery;
	}

	private StringBuilder addWhereClause(StringBuilder siteSearchQuery, List preparedStatementValues,
			SiteSearchRequest searchSiteRequest) {
		boolean isAppendAndClause = false;
		try {
			if (null == searchSiteRequest.getSiteSearchData()
					|| (StringUtils.isEmpty(searchSiteRequest.getSiteSearchData().getAdvertisementType())
							&& StringUtils.isEmpty(searchSiteRequest.getSiteSearchData().getDistrictName())
							&& StringUtils.isEmpty(searchSiteRequest.getSiteSearchData().getStatus())
							&& StringUtils.isEmpty(searchSiteRequest.getSiteSearchData().getUlbName())
							&& StringUtils.isEmpty(searchSiteRequest.getSiteSearchData().getWardNumber())
							&& CollectionUtils.isEmpty(searchSiteRequest.getSiteSearchData().getUuids())
							&& CollectionUtils.isEmpty(searchSiteRequest.getRequestInfo().getUserInfo().getRoles())
							&& BooleanUtils.isFalse(searchSiteRequest.getSiteSearchData().isActive()))) {
				return siteSearchQuery;
			}
			siteSearchQuery.append(" WHERE");
			if (null != searchSiteRequest.getSiteSearchData().getAdvertisementType()
					&& !searchSiteRequest.getSiteSearchData().getAdvertisementType().isEmpty()) {
				isAppendAndClause = true;
				isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, siteSearchQuery);
				siteSearchQuery.append(" eg_site_application.site_type = ").append("'")
						.append(searchSiteRequest.getSiteSearchData().getAdvertisementType()).append("'");
			}
			if (null != searchSiteRequest.getSiteSearchData().getDistrictName()
					&& !searchSiteRequest.getSiteSearchData().getDistrictName().isEmpty()) {
				isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, siteSearchQuery);
				siteSearchQuery.append(" eg_site_application.district_name = ").append("'")
						.append(searchSiteRequest.getSiteSearchData().getAdvertisementType()).append("'");
			}
			if (null != searchSiteRequest.getSiteSearchData().getStatus()
					&& !searchSiteRequest.getSiteSearchData().getStatus().isEmpty()) {
				isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, siteSearchQuery);
				siteSearchQuery.append(" eg_site_application.status = ").append("'")
						.append(searchSiteRequest.getSiteSearchData().getStatus()).append("'");
			}
			if (null != searchSiteRequest.getSiteSearchData().getWardNumber()
					&& !searchSiteRequest.getSiteSearchData().getWardNumber().isEmpty()) {
				isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, siteSearchQuery);
				siteSearchQuery.append(" eg_site_application.ward_number = ").append("'")
						.append(searchSiteRequest.getSiteSearchData().getWardNumber()).append("'");
			}
			if (true == searchSiteRequest.getSiteSearchData().isActive()) {
				isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, siteSearchQuery);
				siteSearchQuery.append(" eg_site_application.is_active = ")
						.append(searchSiteRequest.getSiteSearchData().isActive());
			}

			if (null != searchSiteRequest.getSiteSearchData().getSiteID()
					&& !searchSiteRequest.getSiteSearchData().getSiteID().isEmpty()) {
				isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, siteSearchQuery);
				siteSearchQuery.append(" eg_site_application.site_id =  ").append("'")
						.append(searchSiteRequest.getSiteSearchData().getSiteID()).append("'");
			}

			if (!CollectionUtils.isEmpty(searchSiteRequest.getSiteSearchData().getUuids())) {
				isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, siteSearchQuery);
				siteSearchQuery.append(" eg_site_application.uuid IN ( ")
						.append(getQueryForCollection(searchSiteRequest.getSiteSearchData().getUuids(),
								preparedStatementValues))
						.append(" )");
			}

			if (!CollectionUtils.isEmpty(searchSiteRequest.getSiteSearchData().getWorkflowStatus())) {
				isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, siteSearchQuery);
				siteSearchQuery.append(" eg_site_application.workflow_status IN ( ")
						.append(getQueryForCollection(searchSiteRequest.getSiteSearchData().getWorkflowStatus(),
								preparedStatementValues))
						.append(" )");
			}
			/*
			 * if (!searchSiteRequest.getSiteSearchData().getWorkflowStatus().isEmpty() &&
			 * null != searchSiteRequest.getSiteSearchData().getWorkflowStatus()) {
			 * isAppendAndClause = addAndClauseIfRequired(isAppendAndClause,
			 * siteSearchQuery);
			 * siteSearchQuery.append(" eg_site_application.workflow_status IN ( ")
			 * .append(getQueryForCollection(searchSiteRequest.getSiteSearchData().getUuids(
			 * ), preparedStatementValues)) .append(" )"); }
			 */
//			if (!CollectionUtils.isEmpty(searchSiteRequest.getSiteSearchData().getWorkflowStatus())) {
//				isAppendAndClause=addAndClauseIfRequired(isAppendAndClause, siteSearchQuery);
//				siteSearchQuery.append(" eg_site_application.workflow_status IN ( ")
//				.append(getQueryForCollection(searchSiteRequest.getSiteSearchData().getWorkflowStatus())).append(" ) ");
//				addToPreparedStatement(preparedStatementValues, searchSiteRequest.getSiteSearchData().getWorkflowStatus());
//			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return siteSearchQuery;
	}

	private boolean addAndClauseIfRequired(boolean appendAndClauseFlag, StringBuilder siteSearchQuery) {
		if (appendAndClauseFlag)
			siteSearchQuery.append(" AND");

		return true;
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

	public int siteCount(String ulbName) {
		String query = queryBuilder.SELECT_SITE_COUNT;
		final Map<String, Object> parametersMap = new HashMap<String, Object>();
		parametersMap.put("ulbName", ulbName);
		int siteCount1 = namedParameterJdbcTemplate.queryForObject(query, parametersMap, Integer.class);
		return siteCount1;

	}

	public int siteAvailableCount(String ulbName) {
		String query = queryBuilder.SELECT_AVAILABLE_COUNT;
		String available = "Available";
		final Map<String, Object> parametersMap = new HashMap<String, Object>();
		parametersMap.put("ulbName", ulbName);
		parametersMap.put("status", available);
		int siteAvailableCount1 = namedParameterJdbcTemplate.queryForObject(query, parametersMap, Integer.class);
		return siteAvailableCount1;
	}

	public int siteBookedCount(String ulbName) {
		String query = queryBuilder.SELECT_BOOKED_COUNT;
		String booked = "Booked";
		final Map<String, Object> parametersMap = new HashMap<String, Object>();
		parametersMap.put("ulbName", ulbName);
		parametersMap.put("status", booked);
		int siteBookedCount1 = namedParameterJdbcTemplate.queryForObject(query, parametersMap, Integer.class);
		return siteBookedCount1;
	}

}
