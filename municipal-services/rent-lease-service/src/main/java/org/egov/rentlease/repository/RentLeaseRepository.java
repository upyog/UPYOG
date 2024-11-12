package org.egov.rentlease.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.rentlease.model.Asset;
import org.egov.rentlease.model.RentLease;
import org.egov.rentlease.model.SearchCriteria;
import org.egov.rentlease.repo.rowmapper.RentLeaseRowMapper;
import org.egov.rentlease.repo.rowmapper.RentLeaseSearchRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Repository
public class RentLeaseRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private RentLeaseRowMapper rentLeaseRowMapper;
	
	@Autowired
	private RentLeaseSearchRowMapper rentLeaseSearchRowMapper;

	private static final String baseAssetSearchQuery = "SELECT * from eg_asset_assetdetails";
	private static final String SEQ_FOR_RENT_APPLICATION = "select nextval('seq_eg_rent_lease_application')";
	private static final String RENT_SEARCH_QUERY= "Select * from eg_rent_lease_application";

	private static final String BASE_SEARCH_QUERY = "SELECT booking.*, asset.id as asset_id,asset.bookingrefno as bookrefno, asset.name as name,asset.description as description, asset.classification as classification, asset.parentcategory as parentcategory, asset.category as category, asset.subcategory as subcategory, asset.department as department, asset.applicationno as applicationno, asset.approvalno as approvalno, asset.tenantid as tenantid, asset.status as status, asset.action as action, asset.businessservice as businessservice, asset.additionaldetails as  additionaldetails, asset.approvaldate as approvaldate, asset.applicationdate as applicationdate, asset.acccountid as accountid, asset.accountid as accountid, asset.remarks as remarks, asset.asset_id as assetid, asset.financialyear as financialyear, asset.sourceoffinance as sourceoffinance"
			+ "FROM eg_rent_lease_application as booking"
			+ "LEFT OUTER JOIN eg_asset_assetdetails as asset ON booking.assetid = asset.id";

	public List<Asset> searchAssetFromDB(List<String> ids) {
		StringBuilder assetSearchQuery = null;
		final List preparedStatementValues = new ArrayList<>();
		List<Asset> resultList = new ArrayList<>();
		try {
			assetSearchQuery = getAssetSearchQueryByCriteria(ids, assetSearchQuery, preparedStatementValues);
			resultList = jdbcTemplate.query(assetSearchQuery.toString(), preparedStatementValues.toArray(),
					rentLeaseRowMapper);

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return resultList;
	}

	public Long getNextRentSequence() {
		return jdbcTemplate.queryForObject(SEQ_FOR_RENT_APPLICATION, Long.class);
	}

	private StringBuilder getAssetSearchQueryByCriteria(List<String> ids, StringBuilder assetSearchQuery,
			List preparedStatementValues) {
		assetSearchQuery = new StringBuilder(baseAssetSearchQuery);
		assetSearchQuery = addwhereClauseforSiteSearch(assetSearchQuery, preparedStatementValues, ids);
		return assetSearchQuery;
	}

	private StringBuilder addwhereClauseforSiteSearch(StringBuilder assetSearchQuery, List preparedStatementValues,
			List<String> ids) {
		assetSearchQuery.append(" WHERE");
		if (!CollectionUtils.isEmpty(ids)) {
			assetSearchQuery.append(" id IN ( ").append(getQueryForCollection(ids, preparedStatementValues))
					.append(" )");
		}
		return assetSearchQuery;
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

	public List<RentLease> search(SearchCriteria searchCriteria) {
		List<String> preparedStatementValues = new ArrayList<>();
		StringBuilder searchQuery = new StringBuilder(BASE_SEARCH_QUERY);
		searchQuery = addWhereClause(searchQuery, preparedStatementValues, searchCriteria);
		return jdbcTemplate.query(searchQuery.toString(), preparedStatementValues.toArray(), rentLeaseSearchRowMapper);
		
	}

	private StringBuilder addWhereClause(StringBuilder searchQuery, List preparedStatementValues,
			SearchCriteria searchCriteria) {
		if (CollectionUtils.isEmpty(searchCriteria.getUuids())
				&& CollectionUtils.isEmpty(searchCriteria.getApplicationNo())
				&& CollectionUtils.isEmpty(searchCriteria.getCreatedBy())
				&& StringUtils.isEmpty(searchCriteria.getTenantId())) {
			return searchQuery;
		}
		searchQuery.append(" WHERE");
		boolean isAppendAndClause = false;
		if (!CollectionUtils.isEmpty(searchCriteria.getUuids())) {
			isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
			searchQuery.append(" booking.uuid IN ( ")
					.append(getQueryForCollection(searchCriteria.getUuids(), preparedStatementValues)).append(" )");
		}
		 if (!CollectionUtils.isEmpty(searchCriteria.getApplicationNo())) {
	            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
	            searchQuery.append(" booking.applicationno IN ( ").append(getQueryForCollection(searchCriteria.getApplicationNo(),
	                    preparedStatementValues)).append(" )");
	        }

	        if (!CollectionUtils.isEmpty(searchCriteria.getStatus())) {
	            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
	            searchQuery.append(" booking.status IN ( ").append(getQueryForCollection(searchCriteria.getStatus(),
	                    preparedStatementValues)).append(" )");
	        }

	        if (!CollectionUtils.isEmpty(searchCriteria.getCreatedBy())) {
	            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
	            searchQuery.append(" booking.createdby IN ( ").append(getQueryForCollection(searchCriteria.getCreatedBy(),
	                    preparedStatementValues)).append(" )");
	        }

			if (!ObjectUtils.isEmpty(searchCriteria.getTenantId())) {
				isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
				searchQuery.append(" booking.tenantid = ? ");
				preparedStatementValues.add(searchCriteria.getTenantId());
			}
		return searchQuery;
	}

	

	private boolean addAndClauseIfRequired(boolean appendAndClauseFlag, StringBuilder queryString) {
		if (appendAndClauseFlag)
			queryString.append(" AND");

		return true;
	}

	public List<RentLease> searchForRentAndLeaseFromDb(List<String> ids) {
		List<String> preparedStatementValues = new ArrayList<>();
		StringBuilder searchQuery = new StringBuilder(RENT_SEARCH_QUERY);
		searchQuery = addWhereClauseforRentAndLease(searchQuery, preparedStatementValues, ids);
		return jdbcTemplate.query(searchQuery.toString(), preparedStatementValues.toArray(), rentLeaseSearchRowMapper);
		
	}

	private StringBuilder addWhereClauseforRentAndLease(StringBuilder searchQuery, List preparedStatementValues,
			List<String> ids) {
		searchQuery.append(" WHERE");
		boolean isAppendAndClause = false;
		if(!CollectionUtils.isEmpty(ids)) {
			isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
			 searchQuery.append(" assetid IN ( ").append(getQueryForCollection(ids,
	                    preparedStatementValues)).append(" )");
		}
		return searchQuery;
	}

}
