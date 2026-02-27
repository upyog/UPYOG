package org.egov.pt.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.Appeal;
import org.egov.pt.models.Assessment;
import org.egov.pt.models.AuditDetails;
import org.egov.pt.models.DashboardDataSearch;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.PropertyData;
import org.egov.pt.models.collection.Payment;
import org.egov.pt.models.collection.RevenuDataBucket;
import org.egov.pt.repository.builder.DashboardReportQueryBuilder;
import org.egov.pt.service.PropertyService;
import org.egov.pt.util.PropertyRedisCache;
import org.egov.pt.web.contracts.DashboardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.netty.util.internal.ObjectUtil;

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
	
	
	public List<PropertyData> getTotalPropertyRegisteredCount(DashboardRequest dashboardRequest)
	{
		String query = reportQueryBuilder.getTotalPropertyRegisteredQuery(dashboardRequest.getDashboardDataSearch());
		Map<String, String> propertyTenantMap = new LinkedHashMap<String, String>();
		 List<String> propertyIdList = null;
		if(!ObjectUtils.isEmpty(dashboardRequest.getDashboardDataSearch().getTenantid())) {
			 propertyTenantMap = jdbcTemplate.query(query, rs -> {
			        Map<String, String> map = new LinkedHashMap<>();
			        while (rs.next()) {
			            map.put(
			                rs.getString("propertyid"),
			                dashboardRequest.getDashboardDataSearch().getTenantid()
			            );
			        }
			        return map;
			    });
		 
		}
		else {
			propertyTenantMap = jdbcTemplate.query(
				    query,
				    rs -> {
				        Map<String, String> map = new LinkedHashMap<>();
				        while (rs.next()) {
				            map.put(
				                rs.getString("propertyid"),
				                rs.getString("tenantid")
				            );
				        }
				        return map;
				    }
				);
			//propertyIdList =
			      //  propertyTenantMap.keySet().stream().collect(Collectors.toList());
		}
			
		//PropertyCriteria criteria = new PropertyCriteria();
		//criteria.setPropertyIds(propertyIdList.stream().collect(Collectors.toSet()));
		List<PropertyData> properties=null;
		if(!CollectionUtils.isEmpty(propertyTenantMap))
			properties=  getPropertiesList (getPropertiesWithCache(propertyTenantMap,dashboardRequest));
		
		
		return properties;
		
	}
	
	
	public List<PropertyData> getPropertiesPendingWithCount(DashboardRequest dashboardRequest,String pendingString) {
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
		Map<String, String> propertyTenantMap = new HashMap<>();
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
			 propertyTenantMap =
	        Arrays.stream(propertyIdstring.split(","))
	              .map(String::trim)
	              .map(s -> s.split(":"))
	              .filter(arr -> arr.length == 2)
	              .collect(Collectors.toMap(
	                  arr -> arr[0], // propertyId
	                  arr -> arr[1]  // tenantId
	              ));
		if(!CollectionUtils.isEmpty(propertyTenantMap))
			return getPropertiesList (getPropertiesWithCache(propertyTenantMap,dashboardRequest));
		
		return null;
	}
	
	public List<PropertyData> getTotalPropertyApprovedCount(DashboardRequest dashboardRequest)
	{
		String query = reportQueryBuilder.getTotalPropertyApprovedQuery(dashboardRequest.getDashboardDataSearch());
		Map<String, String> propertyTenantMap = new HashMap<>();
		 List<String> propertyIdList = null;
			propertyTenantMap = jdbcTemplate.query(
				    query,
				    rs -> {
				        Map<String, String> map = new HashMap<>();
				        while (rs.next()) {
				            map.put(
				                rs.getString("propertyid"),
				                rs.getString("tenantid")
				            );
				        }
				        return map;
				    }
				);
		List<PropertyData> properties=null;
		if(!CollectionUtils.isEmpty(propertyTenantMap))
			properties=  getPropertiesList (getPropertiesWithCache(propertyTenantMap,dashboardRequest));
			
		return properties;

	}
	
	public List<PropertyData> getTotalPropertyRejectedCount(DashboardRequest dashboardRequest)
	{
		String query = reportQueryBuilder.getTotalPropertyRejectedQuery(dashboardRequest.getDashboardDataSearch());
		Map<String, String> propertyTenantMap = new HashMap<>();
		 List<String> propertyIdList = null;
			propertyTenantMap = jdbcTemplate.query(
				    query,
				    rs -> {
				        Map<String, String> map = new HashMap<>();
				        while (rs.next()) {
				            map.put(
				                rs.getString("propertyid"),
				                rs.getString("tenantid")
				            );
				        }
				        return map;
				    }
				);
		List<PropertyData> properties=null;
		if(!CollectionUtils.isEmpty(propertyTenantMap))
			properties=  getPropertiesList (getPropertiesWithCache(propertyTenantMap,dashboardRequest));
			
		return properties;

	}
	
	public List<PropertyData> getTotalPropertySelfassessedCount(DashboardRequest dashboardRequest)
	{
		Map<String, String> propertyTenantMap = new HashMap<>();
		String query=reportQueryBuilder.getTotalPropertySelfassessedQuery(dashboardRequest.getDashboardDataSearch());
		
		propertyTenantMap = jdbcTemplate.query(
			    query,
			    rs -> {
			        Map<String, String> map = new HashMap<>();
			        while (rs.next()) {
			            map.put(
			                rs.getString("propertyid"),
			                rs.getString("tenantid")
			            );
			        }
			        return map;
			    }
			);
		
		
		List<PropertyData> properties = null;
		if(!CollectionUtils.isEmpty(propertyTenantMap))
			properties =  getPropertiesList (getPropertiesWithCache(propertyTenantMap,dashboardRequest));
		
		return properties;
	}
	
	public List<PropertyData> getTotalPropertyPendingselfAssessedCount(DashboardRequest dashboardRequest)
	{
		Map<String, String> propertyTenantMap = new HashMap<>();
		String query=reportQueryBuilder.getTotalPropertyPendingselfAssessmentQuery(dashboardRequest.getDashboardDataSearch());
		propertyTenantMap = jdbcTemplate.query(
			    query,
			    rs -> {
			        Map<String, String> map = new HashMap<>();
			        while (rs.next()) {
			            map.put(
			                rs.getString("propertyid"),
			                rs.getString("tenantid")
			            );
			        }
			        return map;
			    }
			);
		
		
		List<PropertyData> properties = null;
		if(!CollectionUtils.isEmpty(propertyTenantMap))
			properties =  getPropertiesList (getPropertiesWithCache(propertyTenantMap,dashboardRequest));
		
		return properties;
	}
	
	public List<PropertyData> getTotalPropertyPaidCount(
	        DashboardRequest dashboardRequest) {

	    String query =
	            reportQueryBuilder.getTotalPropertyPaidQuery(
	                    dashboardRequest.getDashboardDataSearch());

	    Map<String, String> propertyTenantMap = new HashMap<>();

	    // Redis payload
	    Map<String, List<Payment>> redisPayload = new HashMap<>();

	    // Track unique transactions per property
	    Map<String, Set<String>> txnTracker = new HashMap<>();

	    jdbcTemplate.query(query, (RowCallbackHandler) rs -> {
	            String propertyId = rs.getString("propertyid");
	            String tenantId   = rs.getString("tenantid");
	            String txnNo      = rs.getString("txn_id");

	            propertyTenantMap.put(propertyId, tenantId);

	            String redisKey =
	                    propertyRedisCache.PREFIX_PAYMENT
	                            + tenantId + ":" + propertyId;

	            // init trackers
	            txnTracker.computeIfAbsent(redisKey, k -> new HashSet<>());

	            // skip duplicate transaction
	            if (!txnTracker.get(redisKey).add(txnNo)) {
	                return; // duplicate → skip
	            }
	            AuditDetails auditDetails= AuditDetails.builder().createdBy(rs.getString("createdby"))
	            		.createdTime(rs.getLong("createdtime")).lastModifiedBy(rs.getString("lastmodifiedby")).lastModifiedTime(rs.getLong("lastmodifiedtime")).build();
	            Payment payment = Payment.builder()
	                    .transactionNumber(txnNo)
	                    .totalAmountPaid(rs.getBigDecimal("txn_amount"))
	                    .auditDetails(auditDetails)
	                    .build();

	            redisPayload
	                .computeIfAbsent(redisKey, k -> new ArrayList<>())
	                .add(payment);
	        
	    });
	    redisPayload.forEach((key, payments) ->
	        propertyRedisCache.put(key,payments)
	        
	    );

	    return getPropertiesList(
	            getPropertiesWithCache(
	                    propertyTenantMap, dashboardRequest));
	}

	public List<PropertyData> getTotalPropertyAppealSubmitedCount(DashboardRequest dashboardRequest)
	{
		Map<String, String> propertyTenantMap = new HashMap<>();
		String query=reportQueryBuilder.getTotalPropertyAppealSubmitedQuery(dashboardRequest.getDashboardDataSearch());
		propertyTenantMap = jdbcTemplate.query(
			    query,
			    rs -> {
			        Map<String, String> map = new HashMap<>();
			        while (rs.next()) {
			            map.put(
			                rs.getString("propertyid"),
			                rs.getString("tenantid")
			            );
			        }
			        return map;
			    }
			);
		
		List<PropertyData> properties=null;
		
		
		if(!CollectionUtils.isEmpty(propertyTenantMap))
			properties =  getPropertiesList (getPropertiesWithCache(propertyTenantMap,dashboardRequest));
		return properties;
	}
	
	public List<PropertyData> getTotalPropertyAppealPendingCount(DashboardRequest dashboardRequest)
	{
		Map<String, String> propertyTenantMap = new HashMap<>();
		String query=reportQueryBuilder.getTotalPropertyAppealPendingQuery(dashboardRequest.getDashboardDataSearch());
		propertyTenantMap = jdbcTemplate.query(
			    query,
			    rs -> {
			        Map<String, String> map = new HashMap<>();
			        while (rs.next()) {
			            map.put(
			                rs.getString("propertyid"),
			                rs.getString("tenantid")
			            );
			        }
			        return map;
			    }
			);
		
		List<PropertyData> properties=null;
		
		if(!CollectionUtils.isEmpty(propertyTenantMap))
			properties  =  getPropertiesList (getPropertiesWithCache(propertyTenantMap,dashboardRequest));
		
		return properties;
	}
	
	public List<PropertyData> getTotalTaxCollectedAmount(DashboardRequest dashboardRequest)
	{
	    Map<String, String> propertyTenantMap = new HashMap<>();
	    Map<String, List<RevenuDataBucket>> redisPayload = new HashMap<>();

	    String query = reportQueryBuilder.getTotalTaxCollectedQuery(dashboardRequest.getDashboardDataSearch());
	    jdbcTemplate.query(query, rs -> {
	        while (rs.next()) {
	            String propertyId = rs.getString("propertyid");
	            String tenantId = rs.getString("tenantid");
	            BigDecimal totalAmount = rs.getBigDecimal("totalamount");

	            propertyTenantMap.put(propertyId, tenantId);

	            String redisKey = "PROPERTY_TOTAL:" + tenantId + ":" + propertyId;
	            
	            	RevenuDataBucket bucket = RevenuDataBucket.builder()
		                   // .amount(totalAmount)
		                    .propertyId(propertyId)
		                    .tenantId(tenantId)
		                    .build();

		            redisPayload.computeIfAbsent(redisKey, k -> new ArrayList<>()).add(bucket);
	        }
	        return null;
	    });
		
	    if (!redisPayload.isEmpty()) {
	        propertyRedisCache.putAll(redisPayload);
	    }
	    
		List<PropertyData> properties=null;
		
		if(!CollectionUtils.isEmpty(propertyTenantMap))
			properties  =  getPropertiesList (getPropertiesWithCache(propertyTenantMap,dashboardRequest));
		
		
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


	public List<PropertyData> getPenaltyShareAmount(DashboardRequest dashboardRequest)
	{
		Map<String, String> propertyTenantMap = new HashMap<>();
	    Map<String, List<RevenuDataBucket>> redisPayload = new HashMap<>();


		String query=reportQueryBuilder.getPenaltyShareQuery(dashboardRequest.getDashboardDataSearch());
		jdbcTemplate.query(query, rs -> {
            String propertyId = rs.getString("propertyid");
            String tenantId   = rs.getString("tenantid");
            BigDecimal penaltyamount      = rs.getBigDecimal("penalty");
            BigDecimal billAmount      = rs.getBigDecimal("actualbill");
            Long createdTime      = rs.getLong("paymentdate");
            String txn_num = rs.getString("txn_num");
            String redisKey = propertyRedisCache.PREFIX_PENALTY
                    + tenantId + ":" + propertyId;
            
            propertyTenantMap.put(propertyId, tenantId);
            RevenuDataBucket bucket = RevenuDataBucket.builder()
                    .totalBillAmount(billAmount)
                    .penaltyamount(penaltyamount)
                    .creationTime(createdTime)
                    .transactionNumber(txn_num)
                    .propertyId(propertyId)
                    .tenantId(tenantId)
                    .build();

            redisPayload
                .computeIfAbsent(redisKey, k -> new ArrayList<>())
                .add(bucket);
        
    });
    redisPayload.forEach((key, payments) ->
        propertyRedisCache.put(key,payments)
        
    );
		
		List<PropertyData> properties=null;
		
		if(!CollectionUtils.isEmpty(propertyTenantMap))
			properties  =  getPropertiesList (getPropertiesWithCache(propertyTenantMap,dashboardRequest));
		
			
			return properties;
	}
	
	public List<PropertyData> getInterestShareAmount(DashboardRequest dashboardRequest)
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
		List<PropertyData> propertyDatas=new ArrayList<PropertyData>();
		if(!CollectionUtils.isEmpty(propertyIds))
		{
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
			for (Property property : properties) {
	        	PropertyData propertyData= PropertyData.builder().propertyId(property.getPropertyId()).tenantId(property.getTenantId())
	        			.owners(property.getOwners()).address(property.getAddress()).auditDetails(property.getAuditDetails()).build();
	        	propertyDatas.add(propertyData);
			}
		}
			
		
		return propertyDatas;
	}
	
	public List<PropertyData> getAdvanceShareAmount(DashboardRequest dashboardRequest)
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
		List<PropertyData> propertyDatas=new ArrayList<PropertyData>();
		if(!CollectionUtils.isEmpty(propertyIds))
		{
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
			for (Property property : properties) {
	        	PropertyData propertyData= PropertyData.builder().propertyId(property.getPropertyId()).tenantId(property.getTenantId())
	        			.owners(property.getOwners()).address(property.getAddress()).auditDetails(property.getAuditDetails()).build();
	        	propertyDatas.add(propertyData);
			}
		}
		return propertyDatas;
	}
	

	 public Map<String, PropertyData> getPropertiesWithCache(
			 Map<String, String> propertyTenantMap,DashboardRequest dashboardRequest ){
	        List<String> allPropertyIds =
	                new ArrayList<>(propertyTenantMap.keySet().stream().collect(Collectors.toList()));

	        // 1️⃣ Fetch from Redis (batch)
	        Map<String, PropertyData> cachedMap =
	                propertyRedisCache.multiGet(propertyTenantMap);
	        
	       

	        // 2️⃣ Find misses
	        Set<String> missedPropertyIds = allPropertyIds.stream()
	                .filter(id -> !cachedMap.containsKey(id))
	                .collect(Collectors.toSet());

	        // 3️⃣ Call search API ONLY if misses exist
	        if (!missedPropertyIds.isEmpty()) {

	        	PropertyCriteria criteria = PropertyCriteria.builder()
	        			.tenantIds(propertyTenantMap.values().stream().collect(Collectors.toSet()))
	        			.propertyIds(missedPropertyIds)
	        			.build();
	          
	        	
	        	List<Property> searchResponse =propertyService.searchProperty(criteria,dashboardRequest.getRequestInfo());
	            if (!CollectionUtils.isEmpty(searchResponse)) {

	                for (Property property : searchResponse) {
	                	PropertyData propertyData= PropertyData.builder().propertyId(property.getPropertyId()).tenantId(property.getTenantId())
	                			.owners(property.getOwners()).address(property.getAddress()).auditDetails(property.getAuditDetails()).build();
	                    String propertyId = property.getPropertyId();
	                    propertyRedisCache.put(property.getTenantId(), propertyId, propertyData);
	                    cachedMap.put(propertyId, propertyData);
	                }
	            }
	        }

	        LinkedHashMap<String, PropertyData> sortedMap=cachedMap.entrySet().stream().
	        		sorted((p1,p2)->p2.getValue().getAuditDetails().getLastModifiedTime().compareTo(p1.getValue().getAuditDetails().getLastModifiedTime()))
	        		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(p1,p2)->p1,LinkedHashMap::new));
	        return sortedMap;
	    }
	 
	 private List<PropertyData> getPropertiesList(Map<String, PropertyData> propertiesPendingWithCount) {
		    if (propertiesPendingWithCount == null || propertiesPendingWithCount.isEmpty()) {
		        return Collections.emptyList();
		    }
		    return new ArrayList<>(propertiesPendingWithCount.values());
		}
	 
	 
	 public Map<String,List<Payment>>getCacheDataForPaymentReport(Map<String, String> propertyTenantMap){
		 return propertyRedisCache.multiGetPayment(propertyTenantMap);
	 } 
	 
	 public Map<String, List<Assessment>>getCacheDataForAssesmentReport(Set<String> propertyIds,RequestInfo requestInfo)
	 {
		 return propertyRedisCache.getAssessmentsForProperties(propertyIds, requestInfo);
	 }
	 
	 public Map<String, List<Appeal>>getCacheDataForAppealReport(Set<String> propertyIds)
	 {
		 return propertyRedisCache.getAppealsForProperties(propertyIds);
	 }
	 
	 public Map<String, List<RevenuDataBucket>>getCacheDataForPenaltyReport(Map<String, String> propertyTenantMap)
	 {
		 return propertyRedisCache.multiGetPenalty(propertyTenantMap);
	 }
}
