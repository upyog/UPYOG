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
	
	public BigInteger getTotalPropertyPendingselfAssessedCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=reportQueryBuilder.getTotalPropertyPendingselfAssessmentQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public BigInteger getTotalPropertyPaidCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=reportQueryBuilder.getTotalPropertyPaidQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public BigInteger getTotalPropertyAppealSubmitedCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=reportQueryBuilder.getTotalPropertyAppealSubmitedQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public BigInteger getTotalPropertyAppealPendingCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=reportQueryBuilder.getTotalPropertyAppealPendingQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public BigDecimal getTotalTaxCollectedAmount(DashboardDataSearch dashboardDataSearch)
	{
		String query=reportQueryBuilder.getTotalTaxCollectedQuery(dashboardDataSearch);
		BigDecimal result = jdbcTemplate.queryForObject(query, BigDecimal.class);
		return result != null ? result : BigDecimal.ZERO;
	}
	
	public BigDecimal getPropertyTaxShareAmount(DashboardDataSearch dashboardDataSearch) {
	    String query = reportQueryBuilder.getPropertyTaxShareQuery(dashboardDataSearch);
	    BigDecimal result = jdbcTemplate.queryForObject(query, BigDecimal.class);
	    return result != null ? result.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
	}


	public BigDecimal getPenaltyShareAmount(DashboardDataSearch dashboardDataSearch)
	{
		String query=reportQueryBuilder.getPenaltyShareQuery(dashboardDataSearch);
		BigDecimal result = jdbcTemplate.queryForObject(query, BigDecimal.class);
		return result != null ? result : BigDecimal.ZERO;
	}
	
	public BigDecimal getInterestShareAmount(DashboardDataSearch dashboardDataSearch)
	{
		String query=reportQueryBuilder.getInterestShareQuery(dashboardDataSearch);
		BigDecimal result = jdbcTemplate.queryForObject(query, BigDecimal.class);
		return result != null ? result : BigDecimal.ZERO;
	}
	
	public BigDecimal getAdvanceShareAmount(DashboardDataSearch dashboardDataSearch)
	{
		String query=reportQueryBuilder.getAdvanceShareQuery(dashboardDataSearch);
		BigDecimal result = jdbcTemplate.queryForObject(query, BigDecimal.class);
		return result != null ? result : BigDecimal.ZERO;
	}
}
