package org.egov.pt.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.DashboardDataSearch;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.repository.builder.DashboardDataQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardDataRepository {
	
	@Autowired
	DashboardDataQueryBuilder dashboardDataQueryBuilder;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public BigInteger getTotalPropertyRegisteredCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getTotalPropertyRegisteredQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public Map<String, BigInteger> getPropertiesPendingWithCount(DashboardDataSearch dashboardDataSearch) {
	    String query = dashboardDataQueryBuilder.getTotalPropertyPendingWithQuery(dashboardDataSearch);

	    return jdbcTemplate.query(query, rs -> {
	        Map<String, BigInteger> resultMap = new HashMap<>();
	        while (rs.next()) {
	            String action = rs.getString("action_st");
	            BigInteger count = rs.getBigDecimal("count").toBigInteger();
	            resultMap.put(action, count);
	        }
	        return resultMap;
	    });
	}
	
	public BigInteger getTotalPropertyApprovedCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getTotalPropertyApprovedQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public BigInteger getTotalPropertySelfassessedCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getTotalPropertySelfassessedQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public BigInteger getTotalPropertyPendingselfAssessedCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getTotalPropertyPendingselfAssessmentQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public BigInteger getTotalPropertyPaidCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getTotalPropertyPaidQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public BigInteger getTotalPropertyAppealSubmitedCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getTotalPropertyAppealSubmitedQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public BigInteger getTotalPropertyAppealPendingCount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getTotalPropertyAppealPendingQuery(dashboardDataSearch);
		BigInteger result = jdbcTemplate.queryForObject(query, BigInteger.class);
		return result != null ? result : BigInteger.ZERO;
	}
	
	public BigDecimal getTotalTaxCollectedAmount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getTotalTaxCollectedQuery(dashboardDataSearch);
		BigDecimal result = jdbcTemplate.queryForObject(query, BigDecimal.class);
		return result != null ? result : BigDecimal.ZERO;
	}
	
	public BigDecimal getPropertyTaxShareAmount(DashboardDataSearch dashboardDataSearch) {
	    String query = dashboardDataQueryBuilder.getPropertyTaxShareQuery(dashboardDataSearch);
	    BigDecimal result = jdbcTemplate.queryForObject(query, BigDecimal.class);
	    return result != null ? result.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
	}


	public BigDecimal getPenaltyShareAmount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getPenaltyShareQuery(dashboardDataSearch);
		BigDecimal result = jdbcTemplate.queryForObject(query, BigDecimal.class);
		return result != null ? result : BigDecimal.ZERO;
	}
	
	public BigDecimal getInterestShareAmount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getInterestShareQuery(dashboardDataSearch);
		BigDecimal result = jdbcTemplate.queryForObject(query, BigDecimal.class);
		return result != null ? result : BigDecimal.ZERO;
	}
	
	public BigDecimal getAdvanceShareAmount(DashboardDataSearch dashboardDataSearch)
	{
		String query=dashboardDataQueryBuilder.getAdvanceShareQuery(dashboardDataSearch);
		BigDecimal result = jdbcTemplate.queryForObject(query, BigDecimal.class);
		return result != null ? result : BigDecimal.ZERO;
	}
	
	public PropertyCriteria getApplicationData(DashboardDataSearch dashboardDataSearch,RequestInfo requestInfo)
	{
		PropertyCriteria propertyCriteria=new PropertyCriteria();
		Map<String, String> resultMap = new HashMap<>();
		String query = dashboardDataQueryBuilder.getApplicationData(dashboardDataSearch, requestInfo);
		jdbcTemplate.query(query, rs -> {
	        while (rs.next()) {
	            String propertyid = rs.getString("propertyid");
	            String tenantId = rs.getString("tenantid");
	            resultMap.put(propertyid, tenantId);
	        }
	        return resultMap;
	    });
		propertyCriteria.setPropertyIds(resultMap.keySet());
		propertyCriteria.setTenantIds(new HashSet<String>(resultMap.values()));
		
		return propertyCriteria;
	}
}
