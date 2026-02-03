package org.egov.pt.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.DashboardDataSearch;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.repository.builder.DashboardReportQueryBuilder;
import org.egov.pt.service.PropertyService;
import org.egov.pt.util.PropertyRedisCache;
import org.egov.pt.web.contracts.DashboardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Repository
public class DashboardReportRepository {
	
	@Autowired
	DashboardReportQueryBuilder reportQueryBuilder;
	
	@Autowired
	PropertyService propertyService;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	PropertyRedisCache propertyRedisCache;
	
	public List<Property> getTotalPropertyRegisteredCount(DashboardRequest dashboardRequest)
	{
		String query = reportQueryBuilder.getTotalPropertyRegisteredQuery(dashboardRequest.getDashboardDataSearch());

		List<String> propertyIdList = jdbcTemplate.query(
		    query,
		    (rs, rowNum) -> rs.getString("propertyid")  
		);

		Set<String> propertyIds = new HashSet<>(propertyIdList);
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		List<Property> properties=new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		
		return properties;
		
	}
	
	public List<Property>getPropertiesPendingWithCount(DashboardRequest dashboardRequest,String pendingString) {
	    String query = reportQueryBuilder.getTotalPropertyPendingWithQuery(dashboardRequest.getDashboardDataSearch());

	    Map<String, String> queryResult =  jdbcTemplate.query(query, rs -> {
	        Map<String, String> resultMap = new HashMap<>();
	        while (rs.next()) {
	            String action = rs.getString("action_st");
	            String propertyids = rs.getString("property_ids");
	            resultMap.put(action, propertyids);
	        }
	        return resultMap;
	    });
	    
	    Set<String> propertyIds = new HashSet<>();
		String propertyIdstring =null;
		propertyIds = new HashSet<>();
		if(pendingString.equals("PENDINGWITHDOCVERIFIER")) {
			propertyIdstring = queryResult.getOrDefault("PENDINGWITHDOCVERIFIER", null);
		}
		if(pendingString.equals("PENDINGWITHFILEDVERIFIER")) {
			propertyIdstring = queryResult.getOrDefault("PENDINGWITHFILEDVERIFIER", null);
		}
		if(pendingString.equals("PENDINGWITHAPPROVER")) {
			propertyIdstring = queryResult.getOrDefault("PENDINGWITHAPPROVER", null);
		}
		if(pendingString.equals("APPROVED")) {
			propertyIdstring = queryResult.getOrDefault("APPROVED", null);
		}
		if(pendingString.equals("REJECTED")) {
			propertyIdstring = queryResult.getOrDefault("REJECTED", null);
		}
		if(!StringUtils.isEmpty(propertyIdstring))
			propertyIds.addAll(
				    Arrays.stream(propertyIdstring.split(","))
				          .map(String::trim)
				          .collect(Collectors.toSet()));
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		if(!CollectionUtils.isEmpty(propertyIds))
			return getPropertiesList (getPropertiesWithCache(dashboardRequest.getDashboardDataSearch().getTenantid(),propertyIds,dashboardRequest.getRequestInfo()));
		
		return null;
	}
	
	public BigInteger getTotalPropertyApprovedCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=reportQueryBuilder.getTotalPropertyApprovedQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public List<Property> getTotalPropertySelfassessedCount(DashboardRequest dashboardRequest)
	{
		String query=reportQueryBuilder.getTotalPropertySelfassessedQuery(dashboardRequest.getDashboardDataSearch());
		List<String> propertyIdList = jdbcTemplate.query(
			    query,
			    (rs, rowNum) -> rs.getString("propertyid")  
			);
		Set<String> propertyIds = new HashSet<>(propertyIdList);
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		List<Property> properties=null;
		
		
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =  getPropertiesList (getPropertiesWithCache(dashboardRequest.getDashboardDataSearch().getTenantid(),propertyIds,dashboardRequest.getRequestInfo()));
		
		return properties;
	}
	
	public List<Property> getTotalPropertyPendingselfAssessedCount(DashboardRequest dashboardRequest)
	{
		String query=reportQueryBuilder.getTotalPropertyPendingselfAssessmentQuery(dashboardRequest.getDashboardDataSearch());
		List<String> propertyIdList = jdbcTemplate.query(
			    query,
			    (rs, rowNum) -> rs.getString("propertyid")  
			);
		Set<String> propertyIds = new HashSet<>(propertyIdList);
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		List<Property> properties=null;
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =  getPropertiesList (getPropertiesWithCache(dashboardRequest.getDashboardDataSearch().getTenantid(),propertyIds,dashboardRequest.getRequestInfo()));
		
		return properties;
	}
	
	public List<Property> getTotalPropertyPaidCount(DashboardRequest dashboardRequest)
	{
		String query=reportQueryBuilder.getTotalPropertyPaidQuery(dashboardRequest.getDashboardDataSearch());
		List<String> propertyIdList = jdbcTemplate.query(
			    query,
			    (rs, rowNum) -> rs.getString("propertyid")  
			);
		Set<String> propertyIds = new HashSet<>(propertyIdList);
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		List<Property> properties=null;
		//if(!CollectionUtils.isEmpty(propertyIds))
			//properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =  getPropertiesList (getPropertiesWithCache(dashboardRequest.getDashboardDataSearch().getTenantid(),propertyIds,dashboardRequest.getRequestInfo()));
		return properties;
		
	
	}
	
	public List<Property> getTotalPropertyAppealSubmitedCount(DashboardRequest dashboardRequest)
	{
		String query=reportQueryBuilder.getTotalPropertyAppealSubmitedQuery(dashboardRequest.getDashboardDataSearch());
		List<String> propertyIdList = jdbcTemplate.query(
			    query,
			    (rs, rowNum) -> rs.getString("propertyid")  
			);
		Set<String> propertyIds = new HashSet<>(propertyIdList);
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		List<Property> properties=null;
		/*
		 * if(!CollectionUtils.isEmpty(propertyIds)) properties
		 * =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		 */
		
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =  getPropertiesList (getPropertiesWithCache(dashboardRequest.getDashboardDataSearch().getTenantid(),propertyIds,dashboardRequest.getRequestInfo()));
		return properties;
	}
	
	public List<Property> getTotalPropertyAppealPendingCount(DashboardRequest dashboardRequest)
	{
		String query=reportQueryBuilder.getTotalPropertyAppealPendingQuery(dashboardRequest.getDashboardDataSearch());
		List<String> propertyIdList = jdbcTemplate.query(
			    query,
			    (rs, rowNum) -> rs.getString("propertyid")  
			);
		Set<String> propertyIds = new HashSet<>(propertyIdList);
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		List<Property> properties=new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		
		return properties;
	}
	
	public List<Property> getTotalTaxCollectedAmount(DashboardRequest dashboardRequest)
	{
		String query=reportQueryBuilder.getTotalTaxCollectedQuery(dashboardRequest.getDashboardDataSearch());
		List<String> propertyIdList = jdbcTemplate.query(
			    query,
			    (rs, rowNum) -> rs.getString("propertyid")  
			);
		Set<String> propertyIds = new HashSet<>(propertyIdList);
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		List<Property> properties=new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		
		return properties;
	}
	
	public List<Property> getPropertyTaxShareAmount(DashboardRequest dashboardRequest) {
	    String query = reportQueryBuilder.getPropertyTaxShareQuery(dashboardRequest.getDashboardDataSearch());
	    List<String> propertyIdList = jdbcTemplate.query(
			    query,
			    (rs, rowNum) -> rs.getString("propertyid")  
			);
		Set<String> propertyIds = new HashSet<>(propertyIdList);
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		List<Property> properties=new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		
		return properties;
	}


	public List<Property> getPenaltyShareAmount(DashboardRequest dashboardRequest)
	{
		String query=reportQueryBuilder.getPenaltyShareQuery(dashboardRequest.getDashboardDataSearch());
		 List<String> propertyIdList = jdbcTemplate.query(
				    query,
				    (rs, rowNum) -> rs.getString("consumercode")  
				);
			Set<String> propertyIds = new HashSet<>(propertyIdList);
			PropertyCriteria criteria = new PropertyCriteria();
			criteria.setPropertyIds(propertyIds);
			List<Property> properties=new ArrayList<>();
			if(!CollectionUtils.isEmpty(propertyIds))
				properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
			
			return properties;
	}
	
	public List<Property> getInterestShareAmount(DashboardRequest dashboardRequest)
	{
		String query=reportQueryBuilder.getInterestShareQuery(dashboardRequest.getDashboardDataSearch());
		List<String> propertyIdList = jdbcTemplate.query(
			    query,
			    (rs, rowNum) -> rs.getString("consumercode")  
			);
		Set<String> propertyIds = new HashSet<>(propertyIdList);
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		List<Property> properties=new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		
		return properties;
	}
	
	public List<Property> getAdvanceShareAmount(DashboardRequest dashboardRequest)
	{
		String query=reportQueryBuilder.getAdvanceShareQuery(dashboardRequest.getDashboardDataSearch());
		List<String> propertyIdList = jdbcTemplate.query(
			    query,
			    (rs, rowNum) -> rs.getString("propertyid")  
			);
		Set<String> propertyIds = new HashSet<>(propertyIdList);
		PropertyCriteria criteria = new PropertyCriteria();
		criteria.setPropertyIds(propertyIds);
		List<Property> properties=new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		
		return properties;
	}
	

	 public Map<String, Property> getPropertiesWithCache(
	            String tenantId,
	           Set<String> propertiesPendingWithDocVerifierMap,RequestInfo requestInfo ){
	        List<String> allPropertyIds =
	                new ArrayList<>(propertiesPendingWithDocVerifierMap);

	        // 1️⃣ Fetch from Redis (batch)
	        Map<String, Property> cachedMap =
	                propertyRedisCache.multiGet(tenantId, allPropertyIds);

	        // 2️⃣ Find misses
	        Set<String> missedPropertyIds = allPropertyIds.stream()
	                .filter(id -> !cachedMap.containsKey(id))
	                .collect(Collectors.toSet());

	        // 3️⃣ Call search API ONLY if misses exist
	        if (!missedPropertyIds.isEmpty()) {

	        	PropertyCriteria criteria = PropertyCriteria.builder()
	        			.tenantId(tenantId)
	        			.propertyIds(missedPropertyIds)
	        			.build();
	                    

	        	List<Property> searchResponse =propertyService.searchProperty(criteria,requestInfo);

	            if (!CollectionUtils.isEmpty(searchResponse)) {

	                for (Property property : searchResponse) {

	                    String propertyId = property.getPropertyId();
	                    propertyRedisCache.put(tenantId, propertyId, property);
	                    cachedMap.put(propertyId, property);
	                }
	            }
	        }

	        return cachedMap;
	    }
	 
	 private List<Property> getPropertiesList(Map<String, Property> propertiesPendingWithCount) {
		    if (propertiesPendingWithCount == null || propertiesPendingWithCount.isEmpty()) {
		        return Collections.emptyList();
		    }
		    return new ArrayList<>(propertiesPendingWithCount.values());
		}
}
