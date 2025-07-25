package org.egov.pt.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.pt.models.DashboardDataSearch;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.repository.builder.DashboardReportQueryBuilder;
import org.egov.pt.service.PropertyService;
import org.egov.pt.web.contracts.DashboardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class DashboardReportRepository {
	
	@Autowired
	DashboardReportQueryBuilder reportQueryBuilder;
	
	@Autowired
	PropertyService propertyService;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
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
	
	public Map<String, String> getPropertiesPendingWithCount(DashboardRequest dashboardRequest) {
	    String query = reportQueryBuilder.getTotalPropertyPendingWithQuery(dashboardRequest.getDashboardDataSearch());

	    return jdbcTemplate.query(query, rs -> {
	        Map<String, String> resultMap = new HashMap<>();
	        while (rs.next()) {
	            String action = rs.getString("action_st");
	            String propertyids = rs.getString("property_ids");
	            resultMap.put(action, propertyids);
	        }
	        return resultMap;
	    });
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
		List<Property> properties=new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		
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
		List<Property> properties=new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		
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
		List<Property> properties=new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		
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
		List<Property> properties=new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyIds))
			properties =propertyService.searchProperty(criteria, dashboardRequest.getRequestInfo());
		
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
}
