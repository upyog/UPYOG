package org.egov.asset.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.kafka.Producer;
import org.egov.asset.repository.querybuilder.AssetQueryBuilder;
import org.egov.asset.repository.rowmapper.AssetAuditRowMapper;
import org.egov.asset.repository.rowmapper.AssetLimitedDateRowMapper;
import org.egov.asset.repository.rowmapper.AssetRowMapper;
import org.egov.asset.repository.rowmapper.AssetUpdateLimitedDateRowMapper;
import org.egov.asset.repository.rowmapper.AssetUpdateRowMapper;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetAuditDetails;
import org.egov.asset.web.models.AssetSearchCriteria;
import org.egov.asset.web.models.AssetUpdate;
import org.egov.asset.web.models.AssetUpdateRequest;
import org.egov.asset.web.models.AssetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class AssetRepository {
	
	@Autowired
	private AssetQueryBuilder queryBuilder;
	
	@Autowired
	private AssetConfiguration config;
	
	@Autowired
	private Producer producer;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	AssetRowMapper rowMapper;
	
	@Autowired
	AssetLimitedDateRowMapper assetLimitedDateRowMapper;
	
	@Autowired
	AssetAuditRowMapper assetAuditRowMapper; 
	
	@Autowired
	AssetUpdateRowMapper assetUpdateRowMapper;
	
	@Autowired
	AssetUpdateLimitedDateRowMapper assetUpdateLimitedDateRowMapper;
	
	
	/**
	 * Pushes the request on save topic through kafka
	 *
	 * @param bpaRequest
	 *            The asset create request
	 */
	public void save(AssetRequest assetRequest) {
		producer.push(config.getSaveTopic(), assetRequest);
	}
	
	private static final String SEQ_FOR_ASSET_APPLICATION_NUMBER = "select nextval('seq_eg_asset_application_no')";
	
	public Long getNextassetApplicationSequence() {
		return jdbcTemplate.queryForObject(SEQ_FOR_ASSET_APPLICATION_NUMBER, Long.class);
	}

	/**
	 * Pushes the request on save assignment topic through kafka
	 *
	 * @param bpaRequest
	 *            The asset create request
	 */
	public void saveAssignment(AssetRequest assetRequest) {
		producer.push(config.getSaveAssignmentTopic(), assetRequest);
	}
	
	
	/**
	 * Pushes the request on update topic through kafka
	 *
	 * @param bpaRequest
	 *            The asset update request
	 */
	public void update(AssetRequest assetRequest) {
		producer.push(config.getUpdateTopic(), assetRequest);
	}
	
	/**
	 * Pushes the request on update assignment topic through kafka
	 *
	 * @param bpaRequest
	 *            The asset update request
	 */
	public void updateAssignment(AssetUpdateRequest assetRequest) {
		producer.push(config.getUpdateAssignmentTopic(), assetRequest);
	}

	public List<Asset> getAssetData(AssetSearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = null;
		if(searchCriteria.getApplicationNo() != null) {
			 query = queryBuilder.getAssetSearchQuery(searchCriteria, preparedStmtList);
			 log.info("Final query: " + query);
			return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
		}
		else {
			 query = queryBuilder.getAssetSearchQueryForLimitedData(searchCriteria, preparedStmtList);
			 log.info("Final query: " + query);
				return jdbcTemplate.query(query, preparedStmtList.toArray(), assetLimitedDateRowMapper);
		}
	}

	public List<AssetUpdate> getAssetDataFromDB(AssetSearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = null;
		if(searchCriteria.getApplicationNo() != null) {
			 query = queryBuilder.getAssetSearchQuery(searchCriteria, preparedStmtList);
			 log.info("Final query: " + query);
			return jdbcTemplate.query(query, preparedStmtList.toArray(), assetUpdateRowMapper);
		}
		else {
			 query = queryBuilder.getAssetSearchQueryForLimitedData(searchCriteria, preparedStmtList);
			 log.info("Final query: " + query);
				return jdbcTemplate.query(query, preparedStmtList.toArray(), assetUpdateLimitedDateRowMapper);
		}
	}

	
	public List<Map<String, Object>> getAllCounts() {
		List<Map<String, Object>> statusList = null;
		String query = "SELECT SUM(COUNT(*)) OVER () AS total_applications,EXTRACT(MONTH FROM TO_TIMESTAMP(createdtime / 1000)) AS month,COUNT(*) AS application_count FROM eg_wf_processinstance_v2 WHERE modulename = 'SITE' AND action = 'APPROVE' GROUP BY month ORDER BY month";
		statusList =jdbcTemplate.queryForList(query);
        return statusList;
	}


	public List<String> getTypesOfAllApplications(Boolean isHistoryCall, String tenantId) {
		List<String> statusList = null;
		List<AssetAuditDetails> listFromDb=null;
		String query = null;
    	List<Object> preparedStmtList = new ArrayList<>();
    	try {
    		if (BooleanUtils.isTrue(isHistoryCall)) {
    			query = "select classification,parentcategory,category,subcategory from eg_asset_auditdetails where \"action\" = 'APPROVE' and  status = 'APPROVED'";
    		}
    		else {
    			if(StringUtils.isEmpty(tenantId)) {
    				query = "select classification,parentcategory,category,subcategory from eg_asset_auditdetails";
    			}
    			else {
    				query = "select classification,parentcategory,category,subcategory from eg_asset_auditdetails e where e.tenantid = ?";
    				preparedStmtList.add(tenantId);
    			}
    		}
    		listFromDb =jdbcTemplate.query(query, preparedStmtList.toArray(), assetAuditRowMapper);
    		if(!CollectionUtils.isEmpty(listFromDb)) {
    			for(AssetAuditDetails i:listFromDb) {
    				statusList.add(i.getCategory());
    				statusList.add(i.getClassification());
    				statusList.add(i.getParentCategory());
    				statusList.add(i.getSubCategory());
    			}
    		}
    		
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return statusList;
	}

	public void updateAsset(@Valid AssetUpdateRequest assetRequest) {
		producer.push(config.getUpdateTopic(), assetRequest);
		
	}

}
